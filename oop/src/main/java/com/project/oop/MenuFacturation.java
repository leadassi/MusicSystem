package com.project.oop;

// MenuFacturation.java
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MenuFacturation extends Menu {
    private final Facturation facturation;

    public MenuFacturation(Scanner scanner, Facturation facturation) {
        super(scanner);
        this.facturation = facturation;
    }

    @Override
    public String getTitre() { return "d. Paiements et facturation"; }

    @Override
    public void afficher() {
        System.out.println("1. Enregistrer un paiement");
        System.out.println("2. Total depense par un etudiant");
        System.out.println("3. Soldes impayes");
        System.out.println("4. Resume mensuel");
        System.out.println("5. Releve d'un etudiant (resume par etudiant)");
        System.out.println("6. Historique des paiements d'un etudiant");
        System.out.println("7. Facturer un achat (forfait, cours, location)");
    }

    @Override
    public void executerChoix(int choix) {
        switch (choix) {
            case 1: enregistrerPaiement(); break;
            case 2: totalDepense(); break;
            case 3: soldesImpayes(); break;
            case 4: resumeMensuel(); break;
            case 5: releve(); break;
            case 6: historique(); break;
            case 7: facturerAchat(); break;
            default: System.out.println("Choix inconnu.");
        }
    }

    // ---------- 1 ----------
    private void enregistrerPaiement() {
        Etudiant e = choisirEtudiant();
        List<Achat> impayes = facturation.achatsImpayes(e);
        if (impayes.isEmpty()) {
            System.out.println(e.getNomComplet() + " n'a aucun achat impaye.");
            return;
        }
        System.out.println("Achats impayes de " + e.getNomComplet() + " :");
        for (int i = 0; i < impayes.size(); i++) {
            Achat a = impayes.get(i);
            System.out.printf("  %d. [%d] %s | montant %s | paye %s | reste %s%n", i + 1, a.getId(), a.getLibelle(),
                    euros(a.getMontant()), euros(facturation.montantPaye(a)), euros(facturation.soldeAchat(a)));
        }
        int n = lireEntier("Numero de l'achat a payer : ");
        if (n < 1 || n > impayes.size()) throw new IllegalArgumentException("Numero d'achat invalide.");
        Achat achat = impayes.get(n - 1);

        double montant = lireDecimal("Montant paye (reste " + euros(facturation.soldeAchat(achat)) + ") : ");
        ModePaiement mode = lireEnum("Mode de paiement", ModePaiement.class);

        Paiement p = facturation.enregistrerPaiement(e, achat, montant, mode, LocalDate.now());   // exceptions attrapees dans Menu.lancer()
        System.out.println("Paiement enregistre : " + p);
        System.out.println("Reste a payer sur l'achat " + achat.getId() + " : " + euros(facturation.soldeAchat(achat)));
    }

    // ---------- 2 ----------
    private void totalDepense() {
        Etudiant e = choisirEtudiant();
        System.out.println("Total depense par " + e.getNomComplet() + " : " + euros(facturation.totalDepense(e)));
    }

    // ---------- 3 ----------
    private void soldesImpayes() {
        Map<Etudiant, Double> soldes = facturation.soldesImpayes();
        if (soldes.isEmpty()) {
            System.out.println("Aucun solde impaye.");
            return;
        }
        double total = 0;
        System.out.println("Soldes impayes :");
        for (Map.Entry<Etudiant, Double> s : soldes.entrySet()) {
            System.out.printf("  [%d] %-25s %s%n", s.getKey().getId(), s.getKey().getNomComplet(), euros(s.getValue()));
            total += s.getValue();
        }
        System.out.println("Total a recouvrer : " + euros(total));
    }

    // ---------- 4 ----------
    private void resumeMensuel() {
        int annee = lireEntier("Annee (ex. " + LocalDate.now().getYear() + ") : ");
        int mois = lireEntier("Mois (1-12) : ");
        Facturation.ResumeMensuel r = facturation.resumeMensuel(annee, mois);
        System.out.println("Resume de " + r.periode() + " :");
        System.out.println("  Facture (achats du mois) : " + euros(r.totalFacture()));
        System.out.println("  Encaisse                 : " + euros(r.totalEncaisse()) + " (" + r.nbPaiements() + " paiement(s))");
        if (!r.parMode().isEmpty()) {
            System.out.println("  Par mode de paiement :");
            r.parMode().forEach((mode, v) -> System.out.printf("    %-10s %s%n", mode, euros(v)));
        }
        if (!r.encaisseParEtudiant().isEmpty()) {
            System.out.println("  Par etudiant :");
            r.encaisseParEtudiant().forEach((et, v) ->
                    System.out.printf("    [%d] %-25s %s%n", et.getId(), et.getNomComplet(), euros(v)));
        }
    }

    // ---------- 5 ----------
    private void releve() {
        Facturation.Releve r = facturation.releve(choisirEtudiant());
        System.out.println("Releve de " + r.etudiant().getNomComplet() + " :");
        if (r.achats().isEmpty()) System.out.println("  Aucun achat.");
        for (Achat a : r.achats()) {
            System.out.printf("  [%d] %-38s %10s | paye %10s | reste %10s%n", a.getId(), a.getLibelle(),
                    euros(a.getMontant()), euros(facturation.montantPaye(a)), euros(facturation.soldeAchat(a)));
        }
        System.out.println("  Total facture : " + euros(r.totalFacture()));
        System.out.println("  Total paye    : " + euros(r.totalPaye()));
        System.out.println("  Solde du      : " + euros(r.solde()));
    }

    // ---------- 6 ----------
    private void historique() {
        Etudiant e = choisirEtudiant();
        List<Paiement> ps = facturation.historique(e);
        if (ps.isEmpty()) {
            System.out.println("Aucun paiement pour " + e.getNomComplet() + ".");
            return;
        }
        for (Paiement p : ps) System.out.println("  " + p);
    }

    // ---------- 7 ----------
    private void facturerAchat() {
        Etudiant e = choisirEtudiant();
        TypeAchat type = lireEnum("Type d'achat", TypeAchat.class);
        String libelle = lireTexte("Libelle (ex. Forfait 10 heures) : ");
        double montant = lireDecimal("Montant (EUR) : ");
        Achat a = facturation.creerAchat(e, type, libelle, montant, LocalDate.now());
        System.out.println("Achat facture : " + a);
    }

    // ---------- utilitaires ----------
    private Etudiant choisirEtudiant() {
        List<Etudiant> liste = facturation.listerEtudiants();
        if (liste.isEmpty()) throw new IllegalStateException("Aucun etudiant : ajoutez-en via le menu a.");
        System.out.println("Etudiants :");
        for (Etudiant e : liste) System.out.println("  [" + e.getId() + "] " + e.getNomComplet());
        int id = lireEntier("Id etudiant : ");
        Etudiant e = facturation.trouverEtudiant(id);
        if (e == null) throw new IllegalArgumentException("Etudiant introuvable : " + id);
        return e;
    }

    private static String euros(double v) { return String.format(Locale.ROOT, "%.2f EUR", v); }
}
