package com.project.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class PlanificateurTests {
    // lundi 2 novembre 2026, 09:00 : l'horloge de l'ecole est fixee pour que les tests soient stables
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 2, 9, 0);

    private Ecole ecole;
    private Facturation facturation;
    private GestionOffres offres;
    private Planificateur planificateur;
    private Instrument piano;
    private Instrument guitare;
    private Salle salle;
    private Professeur marie;
    private Etudiant alice;
    private Etudiant bob;

    private void preparer() {
        ecole = new Ecole();
        ecole.fixerMaintenant(MAINTENANT);
        facturation = new Facturation(ecole);
        offres = new GestionOffres(ecole, facturation);
        planificateur = new Planificateur(ecole, offres);
        piano = new Instrument("Piano");
        guitare = new Instrument("Guitare");
        ecole.ajouterInstrument(piano);
        ecole.ajouterInstrument(guitare);
        salle = new Salle("Salle A", 3, 15.0);
        ecole.ajouterSalle(salle);
        marie = new Professeur("Dupont", "Marie", "marie@example.test", "0102030407", 40.0);
        marie.ajouterSpecialite(piano);
        marie.ajouterDisponibilite(new Disponibilite(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)));
        ecole.ajouterProfesseur(marie);
        alice = new Etudiant("Martin", "Alice", "alice@example.test", "0102030405", Niveau.DEBUTANT);
        bob = new Etudiant("Durand", "Bob", "bob@example.test", "0102030406", Niveau.INTERMEDIAIRE);
        ecole.ajouterEtudiant(alice);
        ecole.ajouterEtudiant(bob);
    }

    @Test
    void unCoursConsommeLesHeuresDuForfait() {
        preparer();
        Achat forfait = offres.vendreForfait(alice, "Pack 10h", 10, false, 90, 200.0);

        Activite cours = planificateur.planifierCours(marie, List.of(alice), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        assertEquals(9.0, forfait.getHeuresRestantes(), 0.001);
        assertSame(forfait, cours.getParticipations().get(0).getAchat());
    }

    @Test
    void unCoursSurLaMemeSalleAuMemeMomentEstRefuse() {
        preparer();
        planificateur.planifierCours(marie, List.of(alice), salle, piano, LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        assertThrows(IllegalStateException.class, () -> planificateur.planifierCours(marie, List.of(bob), salle,
                piano, LocalDateTime.of(2026, 11, 9, 10, 30), 60));
    }

    @Test
    void uneAnnulationATardiveNeRendPasLesHeures() {
        preparer();
        Achat forfait = offres.vendreForfait(alice, "Pack 10h", 10, false, 90, 200.0);
        // le cours a lieu aujourd'hui a 14:00, il est 09:00 : moins de 24 h avant
        Activite cours = planificateur.planifierCours(marie, List.of(alice), salle, piano,
                LocalDateTime.of(2026, 11, 2, 14, 0), 60);

        boolean tardive = planificateur.annulerCours(cours);

        assertTrue(tardive);
        assertEquals(9.0, forfait.getHeuresRestantes(), 0.001);
        assertEquals(Statut.ANNULEE, cours.getStatut());
    }

    @Test
    void uneAnnulationATempsRendLesHeures() {
        preparer();
        Achat forfait = offres.vendreForfait(alice, "Pack 10h", 10, false, 90, 200.0);
        Activite cours = planificateur.planifierCours(marie, List.of(alice), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        boolean tardive = planificateur.annulerCours(cours);

        assertTrue(!tardive);
        assertEquals(10.0, forfait.getHeuresRestantes(), 0.001);
    }

    @Test
    void unForfaitExpireNEstPasUtiliseEtLeCoursEstFacture() {
        preparer();
        // forfait achete en aout pour 60 jours : expire fin septembre
        Achat forfait = offres.vendreForfait(alice, "Pack 5h", 5, false, 60, 110.0,
                LocalDate.of(2026, 8, 1));

        Activite cours = planificateur.planifierCours(marie, List.of(alice), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        Achat achat = cours.getParticipations().get(0).getAchat();
        assertEquals(TypeAchat.COURS_UNITAIRE, achat.getType());
        assertEquals(40.0, achat.getMontant(), 0.001);
        assertEquals(5.0, forfait.getHeuresRestantes(), 0.001);
        assertEquals(5.0, offres.heuresPerdues(alice, MAINTENANT.toLocalDate()), 0.001);
    }

    @Test
    void unProfesseurQuiNEnseignePasLInstrumentEstRefuse() {
        preparer();

        assertThrows(IllegalStateException.class, () -> planificateur.planifierCours(marie, List.of(alice), salle,
                guitare, LocalDateTime.of(2026, 11, 9, 10, 0), 60));
    }

    @Test
    void uneReservationDeSalleEstFactureeAuTarifHoraire() {
        preparer();

        Activite reservation = planificateur.reserverSalle(alice, salle, LocalDateTime.of(2026, 11, 10, 10, 0), 90);

        assertEquals(TypeActivite.LOCATION_SALLE, reservation.getType());
        assertEquals(22.5, reservation.getParticipations().get(0).getAchat().getMontant(), 0.001);

        planificateur.annulerReservation(reservation);

        assertEquals(0.0, reservation.getParticipations().get(0).getAchat().getMontant(), 0.001);
    }

    @Test
    void unForfaitVenduParLesOffresSePayeAvecLaFacturation() {
        preparer();
        Achat forfait = offres.vendreForfait(alice, "Pack 10h", 10, false, 90, 200.0);

        facturation.enregistrerPaiement(alice, forfait, 80, ModePaiement.CARTE, MAINTENANT.toLocalDate());

        assertEquals(TypeAchat.FORFAIT, forfait.getType());
        assertEquals(120.0, facturation.soldeAchat(forfait), 0.001);
        assertEquals(1, facturation.achatsImpayes(alice).size());
    }

    @Test
    void unCoursUnitaireApparaitDansLesImpayesDeLEtudiant() {
        preparer();
        Activite cours = planificateur.planifierCours(marie, List.of(bob), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        Achat achat = cours.getParticipations().get(0).getAchat();

        assertTrue(facturation.achatsImpayes(bob).contains(achat));
        assertEquals(40.0, facturation.soldeEtudiant(bob), 0.001);
    }

    @Test
    void unCoursAnnuleATempsNeCouteRienALEtudiantSansForfait() {
        preparer();
        Activite cours = planificateur.planifierCours(marie, List.of(bob), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 60);

        planificateur.annulerCours(cours);

        assertEquals(0.0, facturation.soldeEtudiant(bob), 0.001);
    }

    @Test
    void unForfaitIllimiteNeDecomptePasLesHeures() {
        preparer();
        Achat forfait = offres.vendreForfait(alice, "Illimite 3 mois", 0, true, 90, 600.0);

        Activite cours = planificateur.planifierCours(marie, List.of(alice), salle, piano,
                LocalDateTime.of(2026, 11, 9, 10, 0), 120);

        assertSame(forfait, cours.getParticipations().get(0).getAchat());
        assertEquals(0.0, forfait.getHeuresRestantes(), 0.001);
    }

    @Test
    void unEtudiantInconnuDeLEcoleNePeutPasReserverUneSalle() {
        preparer();
        Etudiant inconnu = new Etudiant("X", "Y", "x@example.test", "0", Niveau.DEBUTANT);

        assertThrows(IllegalArgumentException.class, () -> planificateur.reserverSalle(inconnu, salle,
                LocalDateTime.of(2026, 11, 10, 10, 0), 60));
        assertTrue(ecole.getActivites().isEmpty());
        assertTrue(ecole.getAchats().isEmpty());
    }

    @Test
    void uneSalleSansTarifNePeutPasEtreReservee() {
        preparer();
        Salle gratuite = new Salle("Salle B", 2);
        ecole.ajouterSalle(gratuite);

        assertThrows(IllegalStateException.class, () -> planificateur.reserverSalle(alice, gratuite,
                LocalDateTime.of(2026, 11, 10, 10, 0), 60));
        assertTrue(ecole.getActivites().isEmpty());
    }

    @Test
    void lesMenusOffresEtPlanningDeBoutEnBout() {
        preparer();
        // menu b : vendre un forfait (etudiant, nom, illimite ?, heures, validite, prix) puis quitter
        String saisieOffres = "1\n" + alice.getId() + "\nPack 4h\nnon\n4\n30\n100\n0\n";
        new MenuOffres(new Scanner(saisieOffres), offres).lancer();
        assertEquals(1, facturation.achatsDe(alice).size());
        assertEquals(4.0, offres.heuresRestantes(alice, MAINTENANT.toLocalDate()), 0.001);

        // menu c : planifier un cours (prof, instrument, salle, etudiants, debut, duree) puis quitter
        String saisiePlanning = "2\n" + marie.getId() + "\nPiano\nSalle A\n" + alice.getId()
                + "\n2026-11-09 10:00\n90\n0\n";
        new MenuPlanning(new Scanner(saisiePlanning), planificateur).lancer();
        assertEquals(1, ecole.getActivites().size());
        assertEquals(2.5, offres.heuresRestantes(alice, MAINTENANT.toLocalDate()), 0.001);
    }
}
