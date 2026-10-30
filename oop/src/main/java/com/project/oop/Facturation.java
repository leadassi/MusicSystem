package com.project.oop;

// Facturation.java
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Paiements, soldes impayes, total depense et resumes. Un paiement est toujours rattache a un Achat. */
public class Facturation {
    private static final double EPS = 0.005;

    private final Ecole ecole;

    public Facturation(Ecole ecole) {
        this.ecole = Objects.requireNonNull(ecole, "L'ecole est obligatoire.");
    }

    public record ResumeMensuel(YearMonth periode, int nbPaiements, double totalEncaisse, double totalFacture,
                                Map<Etudiant, Double> encaisseParEtudiant, Map<ModePaiement, Double> parMode) { }

    public record Releve(Etudiant etudiant, double totalFacture, double totalPaye, double solde, List<Achat> achats) { }

    public static double arrondir(double v) { return Math.round(v * 100.0) / 100.0; }

    // ---------- recherche (meme principe que GestionExamens) ----------
    public Etudiant trouverEtudiant(int id) { return ecole.trouverEtudiant(id); }

    public List<Etudiant> listerEtudiants() { return ecole.getEtudiants(); }

    public Achat trouverAchat(int id) { return ecole.trouverAchat(id); }

    // ---------- achats ----------
    /**
     * Cree un achat facturable. Methode provisoire tant que GestionOffres n'alimente pas
     * les achats : elle permet de tester la facturation de bout en bout.
     */
    public Achat creerAchat(Etudiant e, TypeAchat type, String libelle, double montant, LocalDate date) {
        verifierEtudiant(e);
        Achat a = new Achat(e, type, libelle, montant, date);
        ecole.ajouterAchat(a);
        return a;
    }

    public List<Achat> achatsDe(Etudiant e) {
        return ecole.getAchats().stream().filter(a -> a.getEtudiant().getId() == e.getId()).toList();
    }

    // ---------- paiements ----------
    /** Enregistre un paiement (partiel ou total) sur un achat de l'etudiant. */
    public Paiement enregistrerPaiement(Etudiant e, Achat achat, double montant, ModePaiement mode, LocalDate date) {
        verifierEtudiant(e);
        if (achat == null) throw new IllegalArgumentException("L'achat est obligatoire.");
        if (ecole.trouverAchat(achat.getId()) != achat) {
            throw new IllegalArgumentException("Achat inconnu de l'ecole : " + achat.getId());
        }
        if (mode == null) throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
        if (date == null) throw new IllegalArgumentException("La date est obligatoire.");
        if (achat.getEtudiant().getId() != e.getId()) {
            throw new IllegalArgumentException("Cet achat n'appartient pas a l'etudiant " + e.getId() + ".");
        }
        if (!Double.isFinite(montant) || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif.");
        }
        double solde = soldeAchat(achat);
        if (solde <= EPS) {
            throw new IllegalStateException("Cet achat est deja entierement paye.");
        }
        if (montant > solde + EPS) {
            throw new IllegalArgumentException(
                    String.format(java.util.Locale.ROOT, "Montant superieur au solde restant (%.2f EUR).", solde));
        }
        Paiement p = new Paiement(e, achat, montant, date, mode);
        ecole.ajouterPaiement(p);
        return p;
    }

    public double montantPaye(Achat a) {
        return arrondir(ecole.getPaiements().stream()
                .filter(p -> p.getAchat() == a)
                .mapToDouble(Paiement::getMontant).sum());
    }

    public double soldeAchat(Achat a) { return arrondir(a.getMontant() - montantPaye(a)); }

    public double totalFacture(Etudiant e) {
        return arrondir(achatsDe(e).stream().mapToDouble(Achat::getMontant).sum());
    }

    /** Montant total depense par l'etudiant = somme de ses paiements. */
    public double totalDepense(Etudiant e) {
        return arrondir(ecole.getPaiements().stream()
                .filter(p -> p.getEtudiant().getId() == e.getId())
                .mapToDouble(Paiement::getMontant).sum());
    }

    public double soldeEtudiant(Etudiant e) { return arrondir(totalFacture(e) - totalDepense(e)); }

    public List<Achat> achatsImpayes(Etudiant e) {
        return achatsDe(e).stream().filter(a -> soldeAchat(a) > EPS).toList();
    }

    /** Etudiants ayant un solde du, avec le montant. */
    public Map<Etudiant, Double> soldesImpayes() {
        Map<Etudiant, Double> res = new LinkedHashMap<>();
        for (Etudiant e : ecole.getEtudiants()) {
            double s = soldeEtudiant(e);
            if (s > EPS) res.put(e, s);
        }
        return res;
    }

    public List<Paiement> historique(Etudiant e) {
        return ecole.getPaiements().stream()
                .filter(p -> p.getEtudiant().getId() == e.getId())
                .sorted(Comparator.comparing(Paiement::getDate))
                .toList();
    }

    // ---------- syntheses ----------
    public ResumeMensuel resumeMensuel(int annee, int mois) {
        if (mois < 1 || mois > 12) throw new IllegalArgumentException("Le mois doit etre entre 1 et 12.");
        YearMonth ym = YearMonth.of(annee, mois);
        List<Paiement> ps = ecole.getPaiements().stream()
                .filter(p -> YearMonth.from(p.getDate()).equals(ym)).toList();
        double encaisse = ps.stream().mapToDouble(Paiement::getMontant).sum();
        double facture = ecole.getAchats().stream()
                .filter(a -> YearMonth.from(a.getDateAchat()).equals(ym))
                .mapToDouble(Achat::getMontant).sum();
        Map<Etudiant, Double> parEtudiant = ps.stream().collect(Collectors.groupingBy(Paiement::getEtudiant,
                LinkedHashMap::new, Collectors.summingDouble(Paiement::getMontant)));
        Map<ModePaiement, Double> parMode = ps.stream().collect(Collectors.groupingBy(Paiement::getMode,
                () -> new EnumMap<>(ModePaiement.class), Collectors.summingDouble(Paiement::getMontant)));
        return new ResumeMensuel(ym, ps.size(), arrondir(encaisse), arrondir(facture), parEtudiant, parMode);
    }

    public Releve releve(Etudiant e) {
        return new Releve(e, totalFacture(e), totalDepense(e), soldeEtudiant(e), achatsDe(e));
    }

    private void verifierEtudiant(Etudiant e) {
        if (e == null) throw new IllegalArgumentException("L'etudiant est obligatoire.");
        if (ecole.trouverEtudiant(e.getId()) != e) {
            throw new IllegalArgumentException("Etudiant inconnu de l'ecole : " + e.getId());
        }
    }
}
