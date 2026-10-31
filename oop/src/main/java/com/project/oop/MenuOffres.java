package com.project.oop;

// MenuOffres.java
import java.time.LocalDate;
import java.util.Scanner;

public class MenuOffres extends Menu {
    private final GestionOffres gestion;

    public MenuOffres(Scanner scanner, GestionOffres gestion) {
        super(scanner);
        this.gestion = gestion;
    }

    @Override
    public String getTitre() { return "b. Forfaits et cours"; }

    @Override
    public void afficher() {
        System.out.println("1. Vendre un forfait a un etudiant");
        System.out.println("2. Heures d'un etudiant (achetees, restantes, historique)");
        System.out.println("3. Lister les cours");
        System.out.println("4. Louer un instrument");
        System.out.println("5. Lister tous les achats");
    }

    @Override
    public void executerChoix(int choix) {
        switch (choix) {
            case 1: vendreForfait(); break;
            case 2: heuresEtudiant(); break;
            case 3: listerCours(); break;
            case 4: louerInstrument(); break;
            case 5: listerAchats(); break;
            default: System.out.println("Choix inconnu.");
        }
    }

    private Etudiant demanderEtudiant() {
        int id = lireEntier("Id etudiant : ");
        Etudiant e = gestion.trouverEtudiant(id);
        if (e == null) throw new IllegalArgumentException("Etudiant introuvable : " + id);
        return e;
    }

    private void vendreForfait() {
        Etudiant e = demanderEtudiant();
        String libelle = lireTexte("Nom du forfait : ");
        String reponse = lireTexte("Forfait illimite ? (oui/non) : ");
        boolean illimite = reponse.equalsIgnoreCase("oui") || reponse.equalsIgnoreCase("o");
        int heures = 0;
        if (!illimite) heures = lireEntier("Nombre d'heures : ");
        int validite = lireEntier("Validite (jours) : ");
        double prix = lireDecimal("Prix (EUR) : ");
        Achat a = gestion.vendreForfait(e, libelle, heures, illimite, validite, prix);
        System.out.println("Forfait vendu : " + a.decrire(gestion.aujourdhui()));
    }

    private void heuresEtudiant() {
        Etudiant e = demanderEtudiant();
        LocalDate aujourdhui = gestion.aujourdhui();
        double achetees = 0;
        for (Achat a : gestion.listerAchats(e)) {
            System.out.println(a.decrire(aujourdhui));
            for (String ligne : a.getHistorique()) System.out.println("      - " + ligne);
            if (a.estForfait() && !a.isIllimite()) achetees += a.getHeuresAchetees();
        }
        System.out.printf("Total : %.1f h achetees, %.1f h restantes (valides), %.1f h perdues (forfaits expires)%n",
                achetees, gestion.heuresRestantes(e, aujourdhui), gestion.heuresPerdues(e, aujourdhui));
    }

    private void listerCours() {
        for (Activite c : gestion.listerCours()) System.out.println(c);
    }

    private void louerInstrument() {
        Etudiant e = demanderEtudiant();
        for (Instrument i : gestion.listerInstruments()) System.out.println("  - " + i);
        String nom = lireTexte("Instrument : ");
        Instrument instrument = gestion.trouverInstrument(nom);
        if (instrument == null) throw new IllegalArgumentException("Instrument inconnu : " + nom);
        int jours = lireEntier("Nombre de jours : ");
        double prixParJour = lireDecimal("Prix par jour (EUR) : ");
        Achat a = gestion.louerInstrument(e, instrument, jours, prixParJour);
        System.out.println("Location enregistree : " + a.decrire(gestion.aujourdhui()));
    }

    private void listerAchats() {
        LocalDate aujourdhui = gestion.aujourdhui();
        for (Achat a : gestion.listerTousLesAchats())
            System.out.println(a.getEtudiant().getNomComplet() + " : " + a.decrire(aujourdhui));
    }
}
