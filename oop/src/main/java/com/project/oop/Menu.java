package com.project.oop;

// Menu.java — classe abstraite n°2
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public abstract class Menu {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    protected final Scanner scanner;

    protected Menu(Scanner scanner) { this.scanner = scanner; }

    public abstract String getTitre();
    public abstract void afficher();
    public abstract void executerChoix(int choix);

    /** Boucle commune a tous les sous-menus (0 = retour au menu principal) */
    public void lancer() {
        int choix;
        do {
            System.out.println("\n=== " + getTitre() + " ===");
            afficher();
            choix = lireEntier("Votre choix (0 = retour) : ");
            if (choix != 0) {
                try {
                    executerChoix(choix);
                } catch (RuntimeException ex) {
                    System.out.println("Erreur : " + ex.getMessage());
                }
            }
        } while (choix != 0);
    }

    protected int lireEntier(String invite) {
        while (true) {
            try {
                return Integer.parseInt(lireTexte(invite));
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre entier.");
            }
        }
    }

    protected double lireDecimal(String invite) {
        while (true) {
            try {
                return Double.parseDouble(lireTexte(invite).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre.");
            }
        }
    }

    protected String lireTexte(String invite) {
        System.out.print(invite);
        return scanner.nextLine().trim();
    }

    protected LocalDateTime lireDateHeure(String invite) {
        while (true) {
            try {
                return LocalDateTime.parse(lireTexte(invite), FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Format attendu : yyyy-MM-dd HH:mm (ex. 2026-12-15 14:00).");
            }
        }
    }

    protected <E extends Enum<E>> E lireEnum(String invite, Class<E> type) {
        E[] valeurs = type.getEnumConstants();
        while (true) {
            String s = lireTexte(invite + " " + Arrays.toString(valeurs) + " : ").toUpperCase();
            for (E v : valeurs) if (v.name().equals(s)) return v;
            System.out.println("Valeur invalide.");
        }
    }
}
