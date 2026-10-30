package com.project.oop;

import java.util.Objects;

public final class Salle {
    private final String nom;
    private final int capacite;

    public Salle(String nom, int capacite) {
        this.nom = Objects.requireNonNull(nom, "Le nom de la salle est obligatoire.").trim();
        if (this.nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide.");
        }
        if (capacite < 1) {
            throw new IllegalArgumentException("La capacité de la salle doit être supérieure à zéro.");
        }
        this.capacite = capacite;
    }

    public String getNom() {
        return nom;
    }

    public int getCapacite() {
        return capacite;
    }

    @Override
    public String toString() {
        return nom + " (capacité : " + capacite + ")";
    }
}
