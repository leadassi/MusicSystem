package com.project.oop;

import java.util.Objects;

// Etudiant.java
public class Etudiant extends Personne {
    private Niveau niveau;

    public Etudiant(String nom, String prenom, String email, String telephone, Niveau niveau) {
        super(nom, prenom, email, telephone);
        this.niveau = Objects.requireNonNull(niveau, "Le niveau est obligatoire.");
    }

    /** Constructeur de copie */
    public Etudiant(Etudiant autre) {
        super(autre);
        this.niveau = autre.niveau;
    }

    @Override
    public String getRole() { return "Etudiant"; }

    public Niveau getNiveau() { return niveau; }
    public void setNiveau(Niveau niveau) {
        this.niveau = Objects.requireNonNull(niveau, "Le niveau est obligatoire.");
    }

    @Override
    public String toString() { return super.toString() + " - niveau " + niveau; }
}
