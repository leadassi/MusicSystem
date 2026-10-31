package com.project.oop;

import java.util.Objects;

public class Participation {
    private final Etudiant etudiant;
    private final Activite activite;
    private Resultat resultat;
    private Double note;
    private Achat achat;   // AJOUT : achat (forfait, cours unitaire, location) qui couvre cette participation

    public Participation(Etudiant etudiant, Activite activite) {
        this.etudiant = Objects.requireNonNull(etudiant, "L'étudiant est obligatoire.");
        this.activite = Objects.requireNonNull(activite, "L'activité est obligatoire.");
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public Activite getActivite() {
        return activite;
    }

    public Resultat getResultat() {
        return resultat;
    }

    public Double getNote() {
        return note;
    }

    // AJOUT
    public Achat getAchat() {
        return achat;
    }

    // AJOUT
    public void setAchat(Achat achat) {
        this.achat = achat;
    }

    public void enregistrerResultat(Resultat resultat, double note) {
        Objects.requireNonNull(resultat, "Le résultat est obligatoire.");
        if (!Double.isFinite(note) || note < 0 || note > 20) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 20.");
        }
        this.resultat = resultat;
        this.note = note;
    }

    @Override
    public String toString() {
        return etudiant.getNomComplet() + (resultat == null
                ? " (inscrit, résultat à saisir)"
                : " — " + resultat + ", note : " + note + "/20");
    }
}
