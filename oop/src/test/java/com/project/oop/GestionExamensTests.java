package com.project.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class GestionExamensTests {
    @Test
    void inscriptionRefuseeQuandLaCapaciteEstAtteinte() {
        Ecole ecole = creerEcole(1);
        GestionExamens gestion = new GestionExamens(ecole);
        Etudiant premier = new Etudiant("Martin", "Alice", "alice@example.test", "0102030405", Niveau.DEBUTANT);
        Etudiant second = new Etudiant("Durand", "Bob", "bob@example.test", "0102030406", Niveau.INTERMEDIAIRE);
        ecole.ajouterEtudiant(premier);
        ecole.ajouterEtudiant(second);
        Activite examen = gestion.creerExamen(
                "Examen de piano", LocalDateTime.now().plusDays(1), 60, 1, "Salle A", "Piano");

        gestion.inscrire(premier, examen);

        assertThrows(IllegalStateException.class, () -> gestion.inscrire(second, examen));
    }

    @Test
    void resultatInvalideNeModifiePasLaParticipation() {
        Etudiant etudiant = new Etudiant("Martin", "Alice", "alice@example.test", "0102030405", Niveau.DEBUTANT);
        Salle salle = new Salle("Salle A", 1);
        Instrument instrument = new Instrument("Piano");
        Activite examen = new Activite(TypeActivite.EXAMEN, "Examen", LocalDateTime.now().plusDays(1),
                60, 1, null, salle, instrument);
        Participation participation = new Participation(etudiant, examen);

        assertThrows(IllegalArgumentException.class,
                () -> participation.enregistrerResultat(Resultat.ADMIS, 21));

        assertNull(participation.getResultat());
        assertNull(participation.getNote());
    }

    @Test
    void uneCopieDePersonneObtientUnIdentifiantDistinct() {
        Etudiant original = new Etudiant("Martin", "Alice", "alice@example.test", "0102030405", Niveau.DEBUTANT);
        Etudiant copie = new Etudiant(original);

        assertNotEquals(original.getId(), copie.getId());
        assertEquals(original.getNomComplet(), copie.getNomComplet());
    }

    private Ecole creerEcole(int capaciteSalle) {
        Ecole ecole = new Ecole();
        ecole.ajouterSalle(new Salle("Salle A", capaciteSalle));
        ecole.ajouterInstrument(new Instrument("Piano"));
        return ecole;
    }
}
