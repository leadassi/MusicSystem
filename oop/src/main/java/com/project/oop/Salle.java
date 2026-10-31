package com.project.oop;

import java.util.Objects;

public final class Salle {
    private final String nom;
    private final int capacite;
    private final double tarifHoraire;   // AJOUT : prix de location par heure

    public Salle(String nom, int capacite) {
        this(nom, capacite, 0.0);
    }

    // AJOUT : constructeur avec tarif horaire
    public Salle(String nom, int capacite, double tarifHoraire) {
        this.nom = Objects.requireNonNull(nom, "Le nom de la salle est obligatoire.").trim();
        if (this.nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide.");
        }
        if (capacite < 1) {
            throw new IllegalArgumentException("La capacité de la salle doit être supérieure à zéro.");
        }
        if (!Double.isFinite(tarifHoraire) || tarifHoraire < 0) {
            throw new IllegalArgumentException("Le tarif horaire doit être positif ou nul.");
        }
        this.capacite = capacite;
        this.tarifHoraire = tarifHoraire;
    }

    public String getNom() {
        return nom;
    }

    public int getCapacite() {
        return capacite;
    }

    public double getTarifHoraire() {
        return tarifHoraire;
    }

    @Override
    public String toString() {
        return nom + " (capacité : " + capacite + ")";
    }
}
