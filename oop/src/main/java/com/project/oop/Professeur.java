package com.project.oop;

// Professeur.java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Professeur extends Personne {
    private double tauxHoraire;
    private final List<Instrument> specialites = new ArrayList<>();
    private final List<Disponibilite> disponibilites = new ArrayList<>();   // AJOUT

    public Professeur(String nom, String prenom, String email, String telephone, double tauxHoraire) {
        super(nom, prenom, email, telephone);
        setTauxHoraire(tauxHoraire);
    }

    /** Constructeur de copie */
    public Professeur(Professeur autre) {
        super(autre);
        this.tauxHoraire = autre.tauxHoraire;
        this.specialites.addAll(autre.specialites);
        this.disponibilites.addAll(autre.disponibilites);   // AJOUT (Disponibilite est immuable)
    }

    @Override
    public String getRole() { return "Professeur"; }

    public void ajouterSpecialite(Instrument i) {
        if (!specialites.contains(i)) specialites.add(i);
    }

    public boolean peutEnseigner(Instrument i) { return specialites.contains(i); }

    // AJOUT
    public void ajouterDisponibilite(Disponibilite d) {
        disponibilites.add(d);
    }

    // AJOUT : si aucune disponibilite n'a ete saisie, le professeur est considere comme toujours disponible
    public boolean estDisponible(LocalDateTime debut, int dureeMinutes) {
        if (disponibilites.isEmpty()) return true;
        for (Disponibilite d : disponibilites) {
            if (d.couvre(debut, dureeMinutes)) return true;
        }
        return false;
    }

    // AJOUT
    public List<Disponibilite> getDisponibilites() {
        return Collections.unmodifiableList(disponibilites);
    }

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
