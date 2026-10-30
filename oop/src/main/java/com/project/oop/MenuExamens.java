package com.project.oop;

// MenuExamens.java
import java.time.LocalDateTime;
import java.util.Scanner;

public class MenuExamens extends Menu {
    private final GestionExamens gestion;

    public MenuExamens(Scanner scanner, GestionExamens gestion) {
        super(scanner);
        this.gestion = gestion;
    }

    @Override
    public String getTitre() { return "e. Examens et resultats"; }

    @Override
    public void afficher() {
        System.out.println("1. Creer un examen");
        System.out.println("2. Inscrire un etudiant a un examen");
        System.out.println("3. Saisir un resultat");
        System.out.println("4. Lister les examens et les inscrits");
    }

    @Override
    public void executerChoix(int choix) {
        switch (choix) {
            case 1: creerExamen(); break;
            case 2: inscrire(); break;
            case 3: saisirResultat(); break;
            case 4: lister(); break;
            default: System.out.println("Choix inconnu.");
        }
    }

    private void creerExamen() {
        String libelle = lireTexte("Libelle : ");
        LocalDateTime debut = lireDateHeure("Date et heure (yyyy-MM-dd HH:mm) : ");
        int duree = lireEntier("Duree (minutes) : ");
        int capacite = lireEntier("Capacite max : ");
        String salle = lireTexte("Salle : ");
        String instrument = lireTexte("Instrument : ");
        Activite ex = gestion.creerExamen(libelle, debut, duree, capacite, salle, instrument);
        System.out.println("Examen cree : " + ex);
    }

    private void inscrire() {
        int idEtudiant = lireEntier("Id etudiant : ");
        int idExamen = lireEntier("Id examen : ");
        Etudiant e = gestion.trouverEtudiant(idEtudiant);
        Activite ex = gestion.trouverExamen(idExamen);
        if (e == null || ex == null) {
            System.out.println("Etudiant ou examen introuvable.");
            return;
        }
        Participation p = gestion.inscrire(e, ex);   // regle 3 -> exception attrapee dans Menu.lancer()
        System.out.println("Inscription enregistree : " + p);
    }

    private void saisirResultat() {
        int idExamen = lireEntier("Id examen : ");
        int idEtudiant = lireEntier("Id etudiant : ");
        Activite ex = gestion.trouverExamen(idExamen);
        Etudiant e = gestion.trouverEtudiant(idEtudiant);
        if (e == null || ex == null) {
            System.out.println("Etudiant ou examen introuvable.");
            return;
        }
        Participation p = gestion.trouverParticipation(ex, e);
        if (p == null) {
            System.out.println(e.getNomComplet() + " n'est pas inscrit(e) a cet examen.");
            return;
        }
        Resultat r = lireEnum("Resultat", Resultat.class);
        double note = lireDecimal("Note /20 : ");
        gestion.saisirResultat(p, r, note);
        System.out.println("Resultat enregistre : " + p);
    }

    private void lister() {
        for (Activite ex : gestion.listerExamens()) {
            System.out.println(ex);
            for (Participation p : ex.getParticipations()) System.out.println("   - " + p);
        }
    }
}
