package com.project.oop;

// GestionExamens.java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestionExamens {
    private final Ecole ecole;

    public GestionExamens(Ecole ecole) { this.ecole = ecole; }

    public Activite creerExamen(String libelle, LocalDateTime debut, int dureeMinutes, int capaciteMax,
                                String nomSalle, String nomInstrument) {
        Salle salle = ecole.trouverSalle(nomSalle);
        Instrument instrument = ecole.trouverInstrument(nomInstrument);
        if (salle == null) throw new IllegalArgumentException("Salle inconnue : " + nomSalle);
        if (instrument == null) throw new IllegalArgumentException("Instrument inconnu : " + nomInstrument);
        if (capaciteMax < 1) throw new IllegalArgumentException("La capacite doit etre >= 1.");
        if (capaciteMax > salle.getCapacite())
            throw new IllegalArgumentException("La capacité de l'examen dépasse celle de la salle.");

        Activite ex = new Activite(TypeActivite.EXAMEN, libelle, debut, dureeMinutes,
                                   capaciteMax, null, salle, instrument);
        ecole.ajouterActivite(ex);
        return ex;
    }

    /** Règle 3 : inscription refusée si la capacité max est atteinte */
    public Participation inscrire(Etudiant e, Activite ex) {
        if (e == null) throw new IllegalArgumentException("L'étudiant est obligatoire.");
        if (ex == null) throw new IllegalArgumentException("L'examen est obligatoire.");
        if (ex.getType() != TypeActivite.EXAMEN)
            throw new IllegalArgumentException("L'activite " + ex.getId() + " n'est pas un examen.");
        if (ex.estInscrit(e))
            throw new IllegalStateException(e.getNomComplet() + " est deja inscrit(e) a cet examen.");
        if (!ex.inscriptionOuverte())
            throw new IllegalStateException("Inscriptions fermees : capacite max atteinte ("
                    + ex.getCapaciteMax() + " places).");

        Participation p = new Participation(e, ex);
        ex.ajouterParticipation(p);
        ecole.ajouterParticipation(p);
        return p;
    }

    public void saisirResultat(Participation p, Resultat r, double note) {
        if (note < 0 || note > 20) throw new IllegalArgumentException("La note doit etre entre 0 et 20.");
        p.enregistrerResultat(r, note);

        // quand tous les inscrits ont un resultat, l'examen est termine
        boolean tousNotes = true;
        for (Participation x : p.getActivite().getParticipations())
            if (x.getResultat() == null) tousNotes = false;
        if (tousNotes) p.getActivite().setStatut(Statut.TERMINEE);
    }

    public List<Activite> listerExamens() {
        List<Activite> examens = new ArrayList<>();
        for (Activite a : ecole.getActivites())
            if (a.getType() == TypeActivite.EXAMEN) examens.add(a);
        return examens;
    }

    public Etudiant trouverEtudiant(int id) { return ecole.trouverEtudiant(id); }

    public Activite trouverExamen(int id) {
        Activite a = ecole.trouverActivite(id);
        return (a != null && a.getType() == TypeActivite.EXAMEN) ? a : null;
    }

    public Participation trouverParticipation(Activite ex, Etudiant e) {
        for (Participation p : ex.getParticipations())
            if (p.getEtudiant() == e) return p;
        return null;
    }
}
