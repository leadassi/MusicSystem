package com.project.oop;

// MenuPlanning.java
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPlanning extends Menu {
    private final Planificateur planificateur;

    public MenuPlanning(Scanner scanner, Planificateur planificateur) {
        super(scanner);
        this.planificateur = planificateur;
    }

    @Override
    public String getTitre() { return "c. Planning et reservations"; }

    @Override
    public void afficher() {
        System.out.println("1. Afficher l'agenda (cours, reservations, examens)");
        System.out.println("2. Planifier un cours");
        System.out.println("3. Annuler un cours");
        System.out.println("4. Terminer un cours");
        System.out.println("5. Reserver une salle");
        System.out.println("6. Annuler une reservation de salle");
        System.out.println("7. Voir / changer la date et l'heure du programme");
    }

    @Override
    public void executerChoix(int choix) {
        switch (choix) {
            case 1: agenda(); break;
            case 2: planifierCours(); break;
            case 3: annulerCours(); break;
            case 4: terminerCours(); break;
            case 5: reserverSalle(); break;
            case 6: annulerReservation(); break;
            case 7: changerHorloge(); break;
            default: System.out.println("Choix inconnu.");
        }
    }

    private void agenda() {
        for (Activite a : planificateur.agenda()) System.out.println(a);
    }

    private void planifierCours() {
        for (Professeur p : planificateur.listerProfesseurs()) System.out.println("  " + p);
        Professeur prof = planificateur.trouverProfesseur(lireEntier("Id professeur : "));
        if (prof == null) throw new IllegalArgumentException("Professeur introuvable.");
        for (Instrument i : planificateur.listerInstruments()) System.out.println("  - " + i);
        Instrument instrument = planificateur.trouverInstrument(lireTexte("Instrument : "));
        if (instrument == null) throw new IllegalArgumentException("Instrument inconnu.");
        for (Salle s : planificateur.listerSalles()) System.out.println("  - " + s);
        Salle salle = planificateur.trouverSalle(lireTexte("Salle : "));
        if (salle == null) throw new IllegalArgumentException("Salle inconnue.");
        List<Etudiant> etudiants = lireEtudiants();
        LocalDateTime debut = lireDateHeure("Debut (yyyy-MM-dd HH:mm) : ");
        int duree = lireEntier("Duree (minutes) : ");

        Activite cours = planificateur.planifierCours(prof, etudiants, salle, instrument, debut, duree);
        System.out.println("Cours planifie : " + cours);
        for (Participation p : cours.getParticipations())
            System.out.println("   - " + p.getEtudiant().getNomComplet() + " -> "
                    + p.getAchat().decrire(planificateur.maintenant().toLocalDate()));
    }

    private List<Etudiant> lireEtudiants() {
        String texte = lireTexte("Ids etudiants separes par des virgules (ex. 1,2) : ");
        List<Etudiant> liste = new ArrayList<>();
        for (String morceau : texte.split(",")) {
            if (morceau.isBlank()) continue;
            int id;
            try {
                id = Integer.parseInt(morceau.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Id invalide : " + morceau.trim());
            }
            Etudiant e = planificateur.trouverEtudiant(id);
            if (e == null) throw new IllegalArgumentException("Etudiant introuvable : " + id);
            liste.add(e);
        }
        return liste;
    }

    private void annulerCours() {
        Activite cours = planificateur.trouverActivite(lireEntier("Id du cours : "));
        if (cours == null) throw new IllegalArgumentException("Cours introuvable.");
        boolean tardive = planificateur.annulerCours(cours);
        if (tardive) System.out.println("Annulation a moins de 24 h : les heures sont consommees.");
        else System.out.println("Annulation a temps : les heures sont rendues.");
    }

    private void terminerCours() {
        Activite cours = planificateur.trouverActivite(lireEntier("Id du cours : "));
        if (cours == null) throw new IllegalArgumentException("Cours introuvable.");
        planificateur.terminerCours(cours);
        System.out.println("Cours termine.");
    }

    private void reserverSalle() {
        Etudiant e = planificateur.trouverEtudiant(lireEntier("Id etudiant : "));
        if (e == null) throw new IllegalArgumentException("Etudiant introuvable.");
        for (Salle s : planificateur.listerSalles())
            System.out.printf("  - %s, %.2f EUR/h%n", s, s.getTarifHoraire());
        Salle salle = planificateur.trouverSalle(lireTexte("Salle : "));
        if (salle == null) throw new IllegalArgumentException("Salle inconnue.");
        LocalDateTime debut = lireDateHeure("Debut (yyyy-MM-dd HH:mm) : ");
        int duree = lireEntier("Duree (minutes) : ");
        Activite r = planificateur.reserverSalle(e, salle, debut, duree);
        System.out.println("Salle reservee : " + r);
        System.out.println("   " + r.getParticipations().get(0).getAchat());
    }

    private void annulerReservation() {
        Activite r = planificateur.trouverActivite(lireEntier("Id de la reservation : "));
        if (r == null) throw new IllegalArgumentException("Reservation introuvable.");
        planificateur.annulerReservation(r);
        System.out.println("Reservation annulee.");
    }

    private void changerHorloge() {
        System.out.println("Date et heure du programme : " + planificateur.maintenant()
                + (planificateur.horlogeFictive() ? " (fixee)" : " (horloge reelle)"));
        String texte = lireTexte("Nouvelle date (yyyy-MM-dd HH:mm), 'reel' pour l'horloge reelle, vide pour garder : ");
        if (texte.isEmpty()) return;
        if (texte.equalsIgnoreCase("reel")) {
            planificateur.utiliserHorlogeReelle();
        } else {
            try {
                planificateur.fixerMaintenant(LocalDateTime.parse(texte.replace(' ', 'T')));
            } catch (DateTimeParseException e) {
                System.out.println("Format attendu : yyyy-MM-dd HH:mm (ex. 2026-11-02 18:00).");
                return;
            }
        }
        System.out.println("Il est maintenant : " + planificateur.maintenant());
    }
}
