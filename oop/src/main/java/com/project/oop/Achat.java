package com.project.oop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tout ce qui est vendu et facturable a un etudiant : forfait, cours a l'unite,
 * location d'instrument ou de salle. Un Paiement est toujours rattache a un Achat.
 */
public class Achat implements Facturable {
    private static final AtomicInteger PROCHAIN_ID = new AtomicInteger(1);
    private static final double EPS = 1e-6;

    private final int id = PROCHAIN_ID.getAndIncrement();
    private final Etudiant etudiant;
    private final TypeAchat type;
    private final String libelle;
    private final double montant;
    private final LocalDate dateAchat;

    // Uniquement pour les forfaits / credits d'heures (utilise par GestionOffres)
    private double heuresAchetees;
    private double heuresRestantes;
    private LocalDate dateDebut;   // null = pas de limite
    private LocalDate dateFin;     // null = pas d'expiration
    private boolean illimite;

    // AJOUT (planning) : annulation et historique des heures
    private boolean annule;
    private final List<String> historique = new ArrayList<>();

    public Achat(Etudiant etudiant, TypeAchat type, String libelle, double montant, LocalDate dateAchat) {
        this.etudiant = Objects.requireNonNull(etudiant, "L'etudiant est obligatoire.");
        this.type = Objects.requireNonNull(type, "Le type d'achat est obligatoire.");
        this.libelle = Objects.requireNonNull(libelle, "Le libelle est obligatoire.").trim();
        if (this.libelle.isEmpty()) {
            throw new IllegalArgumentException("Le libelle ne peut pas etre vide.");
        }
        if (!Double.isFinite(montant) || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif.");
        }
        this.montant = Math.round(montant * 100.0) / 100.0;
        this.dateAchat = Objects.requireNonNull(dateAchat, "La date d'achat est obligatoire.");
    }

    /** Definit un forfait : N heures (ou illimite) valables de debut a fin. */
    public void definirForfait(double heures, boolean illimite, LocalDate debut, LocalDate fin) {
        if (!illimite && (!Double.isFinite(heures) || heures <= 0)) {
            throw new IllegalArgumentException("Le nombre d'heures doit etre strictement positif.");
        }
        if (debut != null && fin != null && fin.isBefore(debut)) {
            throw new IllegalArgumentException("La date de fin precede la date de debut.");
        }
        this.illimite = illimite;
        this.heuresAchetees = illimite ? 0 : heures;
        this.heuresRestantes = illimite ? 0 : heures;
        this.dateDebut = debut;
        this.dateFin = fin;
    }

    /** Regle 1 : les heures expirent a la date de fin du forfait. */
    public boolean estValideLe(LocalDate date) {
        if (dateDebut != null && date.isBefore(dateDebut)) return false;
        if (dateFin != null && date.isAfter(dateFin)) return false;
        return illimite || heuresRestantes > EPS;
    }

    public void consommer(double heures) {
        if (illimite) return;
        if (heures > heuresRestantes + EPS) {
            throw new IllegalStateException("Heures insuffisantes sur l'achat " + id + ".");
        }
        heuresRestantes = Math.max(0, heuresRestantes - heures);
    }

    // ---------- AJOUT (planning) : gestion des heures d'un forfait ----------
    public boolean estForfait() { return type == TypeAchat.FORFAIT; }

    public boolean aAssezDHeures(double heures) {
        return illimite || heuresRestantes + EPS >= heures;
    }

    /** Retire des heures du forfait pour un cours (et garde une trace dans l'historique). */
    public void decompter(double heures, String note) {
        consommer(heures);
        historique.add(note);
    }

    /** Rend des heures (annulation a temps). On ne depasse jamais le nombre d'heures achetees. */
    public void rembourser(double heures, String note) {
        if (!illimite) heuresRestantes = Math.min(heuresAchetees, heuresRestantes + heures);
        historique.add(note);
    }

    public void ajouterHistorique(String note) { historique.add(note); }

    /** Annule l'achat : il ne coute plus rien (cours annule a temps, reservation annulee). */
    public void annuler() { this.annule = true; }

    /** Heures perdues : il en restait mais le forfait a expire. */
    public double heuresPerdues(LocalDate aujourdhui) {
        if (estForfait() && !illimite && dateFin != null && aujourdhui.isAfter(dateFin)) return heuresRestantes;
        return 0;
    }

    public String decrire(LocalDate aujourdhui) {
        boolean expire = dateFin != null && aujourdhui.isAfter(dateFin);
        String etat = annule ? "annule" : (expire ? "EXPIRE" : "valide");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.ROOT, "Achat [%d] %s - %s, achete le %s", id, type, libelle, dateAchat));
        if (estForfait() && dateFin != null) sb.append(", jusqu'au ").append(dateFin);
        if (estForfait() && illimite) {
            sb.append(" | heures illimitees");
        } else if (estForfait()) {
            sb.append(String.format(Locale.ROOT, " | %.1f h restantes sur %.1f h", heuresRestantes, heuresAchetees));
        }
        sb.append(String.format(Locale.ROOT, " | %.2f EUR (%s)", getMontant(), etat));
        return sb.toString();
    }

    public boolean estAnnule() { return annule; }

    public List<String> getHistorique() { return Collections.unmodifiableList(historique); }

    public int getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public TypeAchat getType() { return type; }
    public String getLibelle() { return libelle; }
    @Override
    public double getMontant() { return annule ? 0 : montant; }   // AJOUT : un achat annule ne coute rien
    public LocalDate getDateAchat() { return dateAchat; }
    public double getHeuresAchetees() { return heuresAchetees; }
    public double getHeuresRestantes() { return heuresRestantes; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public boolean isIllimite() { return illimite; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%d] %s - %s (%.2f EUR)", id, type, libelle, montant);
    }
}
