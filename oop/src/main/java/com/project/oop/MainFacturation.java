package com.project.oop;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Lanceur AUTONOME pour tester MenuFacturation seul (sans Spring, sans MenuPrincipal).
 * A supprimer (ou ne plus utiliser) quand le MenuPrincipal sera relie aux sous-menus.
 */
public class MainFacturation {

    public static void main(String[] args) {
        Ecole ecole = new Ecole();
        Facturation facturation = new Facturation(ecole);
        chargerDonneesDemo(ecole, facturation);
        new MenuFacturation(new Scanner(System.in), facturation).lancer();
        System.out.println("Au revoir.");
    }

    /** Quelques etudiants, achats et paiements pour pouvoir tester tout de suite. */
    static void chargerDonneesDemo(Ecole ecole, Facturation facturation) {
        Etudiant emma = new Etudiant("Leroy", "Emma", "emma@mail.fr", "0655667788", Niveau.DEBUTANT);
        Etudiant karim = new Etudiant("Benali", "Karim", "karim@mail.fr", "0644332211", Niveau.INTERMEDIAIRE);
        Etudiant lina = new Etudiant("Nguyen", "Lina", "lina@mail.fr", "0699887766", Niveau.AVANCE);
        ecole.ajouterEtudiant(emma);
        ecole.ajouterEtudiant(karim);
        ecole.ajouterEtudiant(lina);

        LocalDate aujourdhui = LocalDate.now();
        Achat forfaitEmma = facturation.creerAchat(emma, TypeAchat.FORFAIT, "Forfait 10 heures", 350, aujourdhui.minusDays(10));
        Achat forfaitKarim = facturation.creerAchat(karim, TypeAchat.FORFAIT, "Forfait illimite 3 mois", 600, aujourdhui.minusDays(40));
        Achat locationLina = facturation.creerAchat(lina, TypeAchat.LOCATION_INSTRUMENT, "Location violon (30 j)", 75, aujourdhui.minusDays(5));

        facturation.enregistrerPaiement(emma, forfaitEmma, 150, ModePaiement.VIREMENT, aujourdhui.minusDays(10));
        facturation.enregistrerPaiement(karim, forfaitKarim, 300, ModePaiement.CARTE, aujourdhui.minusDays(40));
        facturation.enregistrerPaiement(karim, forfaitKarim, 300, ModePaiement.CHEQUE, aujourdhui.minusDays(3));
        facturation.enregistrerPaiement(lina, locationLina, 25, ModePaiement.ESPECES, aujourdhui.minusDays(5));
    }
}
