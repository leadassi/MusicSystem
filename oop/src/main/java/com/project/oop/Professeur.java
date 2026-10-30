package com.project.oop;

// Professeur.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Professeur extends Personne {
    private double tauxHoraire;
    private final List<Instrument> specialites = new ArrayList<>();

    public Professeur(String nom, String prenom, String email, String telephone, double tauxHoraire) {
        super(nom, prenom, email, telephone);
        setTauxHoraire(tauxHoraire);
    }

    /** Constructeur de copie */
    public Professeur(Professeur autre) {
        super(autre);
        this.tauxHoraire = autre.tauxHoraire;
        this.specialites.addAll(autre.specialites);
    }

    @Override
    public String getRole() { return "Professeur"; }

    public void ajouterSpecialite(Instrument i) {
        if (!specialites.contains(i)) specialites.add(i);
    }

    public boolean peutEnseigner(Instrument i) { return specialites.contains(i); }

    public double getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(double tauxHoraire) {
        if (!Double.isFinite(tauxHoraire) || tauxHoraire < 0) {
            throw new IllegalArgumentException("Le taux horaire doit être positif ou nul.");
        }
        this.tauxHoraire = tauxHoraire;
    }

    public List<Instrument> getSpecialites() {
        return Collections.unmodifiableList(specialites);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - %.2f EUR/h - %s", tauxHoraire, specialites);
    }
}
