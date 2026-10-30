package com.project.oop;

import java.util.Objects;

public final class Instrument {
    private final String nom;

    public Instrument(String nom) {
        this.nom = Objects.requireNonNull(nom, "Le nom de l'instrument est obligatoire.").trim();
        if (this.nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'instrument ne peut pas être vide.");
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) return true;
        if (!(autre instanceof Instrument instrument)) return false;
        return nom.equalsIgnoreCase(instrument.nom);
    }

    @Override
    public int hashCode() {
        return nom.toLowerCase(java.util.Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return nom;
    }
}
