package com.project.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FacturationTests {
    private Ecole ecole;
    private Facturation facturation;
    private Etudiant emma;
    private Etudiant karim;
    private Achat forfait;

    @BeforeEach
    void init() {
        ecole = new Ecole();
        facturation = new Facturation(ecole);
        emma = new Etudiant("Leroy", "Emma", "emma@example.test", "0102030405", Niveau.DEBUTANT);
        karim = new Etudiant("Benali", "Karim", "karim@example.test", "0102030406", Niveau.AVANCE);
        ecole.ajouterEtudiant(emma);
        ecole.ajouterEtudiant(karim);
        forfait = facturation.creerAchat(emma, TypeAchat.FORFAIT, "Forfait 10h", 300, LocalDate.of(2026, 10, 5));
    }

    @Test
    void paiementPartielPuisSolde() {
        facturation.enregistrerPaiement(emma, forfait, 100, ModePaiement.CARTE, LocalDate.of(2026, 10, 6));
        assertEquals(200, facturation.soldeAchat(forfait), 0.001);
        assertEquals(1, facturation.achatsImpayes(emma).size());

        facturation.enregistrerPaiement(emma, forfait, 200, ModePaiement.CARTE, LocalDate.of(2026, 10, 7));
        assertEquals(0, facturation.soldeAchat(forfait), 0.001);
        assertEquals(300, facturation.totalDepense(emma), 0.001);
        assertTrue(facturation.achatsImpayes(emma).isEmpty());
    }

    @Test
    void surpaiementRefuse() {
        assertThrows(IllegalArgumentException.class,
                () -> facturation.enregistrerPaiement(emma, forfait, 301, ModePaiement.ESPECES, LocalDate.now()));
        assertEquals(0, facturation.totalDepense(emma), 0.001);
    }

    @Test
    void montantNulOuNegatifRefuse() {
        assertThrows(IllegalArgumentException.class,
                () -> facturation.enregistrerPaiement(emma, forfait, 0, ModePaiement.ESPECES, LocalDate.now()));
        assertThrows(IllegalArgumentException.class,
                () -> facturation.enregistrerPaiement(emma, forfait, -5, ModePaiement.ESPECES, LocalDate.now()));
    }

    @Test
    void achatPayeDejaEntierementRefuse() {
        facturation.enregistrerPaiement(emma, forfait, 300, ModePaiement.VIREMENT, LocalDate.now());
        assertThrows(IllegalStateException.class,
                () -> facturation.enregistrerPaiement(emma, forfait, 10, ModePaiement.VIREMENT, LocalDate.now()));
    }

    @Test
    void achatDUnAutreEtudiantRefuse() {
        assertThrows(IllegalArgumentException.class,
                () -> facturation.enregistrerPaiement(karim, forfait, 50, ModePaiement.ESPECES, LocalDate.now()));
    }

    @Test
    void etudiantInconnuDeLEcoleRefuse() {
        Etudiant inconnu = new Etudiant("X", "Y", "x@example.test", "0", Niveau.DEBUTANT);
        assertThrows(IllegalArgumentException.class,
                () -> facturation.creerAchat(inconnu, TypeAchat.COURS_UNITAIRE, "Cours", 40, LocalDate.now()));
    }

    @Test
    void soldesImpayesEtTotalDepense() {
        facturation.enregistrerPaiement(emma, forfait, 120, ModePaiement.VIREMENT, LocalDate.now());
        assertEquals(1, facturation.soldesImpayes().size());
        assertEquals(180, facturation.soldesImpayes().get(emma), 0.001);
        assertEquals(0, facturation.totalDepense(karim), 0.001);
        assertEquals(180, facturation.soldeEtudiant(emma), 0.001);
    }

    @Test
    void resumeMensuel() {
        Achat cours = facturation.creerAchat(karim, TypeAchat.COURS_UNITAIRE, "Cours piano", 50, LocalDate.of(2026, 10, 20));
        facturation.enregistrerPaiement(emma, forfait, 100, ModePaiement.CARTE, LocalDate.of(2026, 10, 6));
        facturation.enregistrerPaiement(karim, cours, 50, ModePaiement.ESPECES, LocalDate.of(2026, 10, 21));
        facturation.enregistrerPaiement(emma, forfait, 50, ModePaiement.CARTE, LocalDate.of(2026, 11, 2));

        Facturation.ResumeMensuel r = facturation.resumeMensuel(2026, 10);
        assertEquals(2, r.nbPaiements());
        assertEquals(150, r.totalEncaisse(), 0.001);
        assertEquals(350, r.totalFacture(), 0.001);
        assertEquals(100, r.parMode().get(ModePaiement.CARTE), 0.001);
        assertThrows(IllegalArgumentException.class, () -> facturation.resumeMensuel(2026, 13));
    }

    @Test
    void forfaitExpireALaDateDeFin() {
        forfait.definirForfait(10, false, LocalDate.of(2026, 10, 5), LocalDate.of(2026, 11, 5));
        assertTrue(forfait.estValideLe(LocalDate.of(2026, 11, 5)));
        assertFalse(forfait.estValideLe(LocalDate.of(2026, 11, 6)));
    }

    @Test
    void menuEnregistrerPaiementDeBoutEnBout() {
        // choix 1, etudiant, achat n 1, montant, mode, puis 0 pour quitter
        String saisie = "1\n" + emma.getId() + "\n1\n100\nCARTE\n0\n";
        new MenuFacturation(new Scanner(saisie), facturation).lancer();
        assertEquals(100, facturation.totalDepense(emma), 0.001);
        assertEquals(200, facturation.soldeAchat(forfait), 0.001);
    }

    @Test
    void menuAffichageErreurSansPlanter() {
        // montant trop eleve : l'erreur est attrapee par Menu.lancer(), rien n'est enregistre
        String saisie = "1\n" + emma.getId() + "\n1\n999\nCARTE\n0\n";
        new MenuFacturation(new Scanner(saisie), facturation).lancer();
        assertEquals(0, facturation.totalDepense(emma), 0.001);
    }
}
