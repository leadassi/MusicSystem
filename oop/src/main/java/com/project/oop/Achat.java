package com.project.oop;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tout ce qui est vendu et facturable a un etudiant : forfait, cours a l'unite,
 * location d'instrument ou de salle. Un Paiement est toujours rattache a un Achat.
 */
public class Achat {
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

    public int getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public TypeAchat getType() { return type; }
    public String getLibelle() { return libelle; }
    public double getMontant() { return montant; }
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
