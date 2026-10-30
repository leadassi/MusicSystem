package com.project.oop;

// GestionPersonnes.java
import java.util.ArrayList;
import java.util.List;

public class GestionPersonnes {
    private final Ecole ecole;

    public GestionPersonnes(Ecole ecole) { this.ecole = ecole; }

    public Etudiant ajouterEtudiant(String nom, String prenom, String email, String tel, Niveau niveau) {
        Etudiant e = new Etudiant(nom, prenom, email, tel, niveau);
        ecole.ajouterEtudiant(e);
        return e;
    }

    public Professeur ajouterProfesseur(String nom, String prenom, String email, String tel, double taux) {
        Professeur p = new Professeur(nom, prenom, email, tel, taux);
        ecole.ajouterProfesseur(p);
        return p;
    }

    /** Retourne une Personne : l'appelant ne sait pas si c'est un Etudiant ou un Professeur */
    public Personne rechercher(int id) {
        for (Personne p : listerTous()) if (p.getId() == id) return p;
        return null;
    }

    /** Collection polymorphe */
    public List<Personne> listerTous() {
        List<Personne> tous = new ArrayList<>(ecole.getEtudiants());
        tous.addAll(ecole.getProfesseurs());
        return tous;
    }
}
