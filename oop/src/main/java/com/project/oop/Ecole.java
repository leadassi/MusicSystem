package com.project.oop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Ecole {
    private final List<Etudiant> etudiants = new ArrayList<>();
    private final List<Professeur> professeurs = new ArrayList<>();
    private final List<Activite> activites = new ArrayList<>();
    private final List<Participation> participations = new ArrayList<>();
    private final List<Salle> salles = new ArrayList<>();
    private final List<Instrument> instruments = new ArrayList<>();
    private final List<Achat> achats = new ArrayList<>();           // facturation
    private final List<Paiement> paiements = new ArrayList<>();     // facturation

    public List<Etudiant> getEtudiants() {
        return Collections.unmodifiableList(etudiants);
    }

    public List<Professeur> getProfesseurs() {
        return Collections.unmodifiableList(professeurs);
    }

    public List<Activite> getActivites() {
        return Collections.unmodifiableList(activites);
    }

    public List<Participation> getParticipations() {
        return Collections.unmodifiableList(participations);
    }

    public List<Salle> getSalles() {
        return Collections.unmodifiableList(salles);
    }

    public List<Instrument> getInstruments() {
        return Collections.unmodifiableList(instruments);
    }

    public List<Achat> getAchats() {
        return Collections.unmodifiableList(achats);
    }

    public List<Paiement> getPaiements() {
        return Collections.unmodifiableList(paiements);
    }

    public void ajouterEtudiant(Etudiant etudiant) {
        etudiants.add(Objects.requireNonNull(etudiant, "L'étudiant est obligatoire."));
    }

    public void ajouterProfesseur(Professeur professeur) {
        professeurs.add(Objects.requireNonNull(professeur, "Le professeur est obligatoire."));
    }

    public void ajouterActivite(Activite activite) {
        activites.add(Objects.requireNonNull(activite, "L'activité est obligatoire."));
    }

    public void ajouterParticipation(Participation participation) {
        participations.add(Objects.requireNonNull(participation, "La participation est obligatoire."));
    }

    public void ajouterSalle(Salle salle) {
        salles.add(Objects.requireNonNull(salle, "La salle est obligatoire."));
    }

    public void ajouterInstrument(Instrument instrument) {
        instruments.add(Objects.requireNonNull(instrument, "L'instrument est obligatoire."));
    }

    public void ajouterAchat(Achat achat) {
        achats.add(Objects.requireNonNull(achat, "L'achat est obligatoire."));
    }

    public void ajouterPaiement(Paiement paiement) {
        paiements.add(Objects.requireNonNull(paiement, "Le paiement est obligatoire."));
    }

    public Etudiant trouverEtudiant(int id) {
        return etudiants.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public Activite trouverActivite(int id) {
        return activites.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public Achat trouverAchat(int id) {
        return achats.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public Salle trouverSalle(String nom) {
        return salles.stream()
                .filter(s -> s.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(null);
    }

    public Instrument trouverInstrument(String nom) {
        return instruments.stream()
                .filter(i -> i.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(null);
    }
}
