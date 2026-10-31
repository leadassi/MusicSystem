package com.project.oop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Activite {
    private static final AtomicInteger PROCHAIN_ID = new AtomicInteger(1);

    private final int id = PROCHAIN_ID.getAndIncrement();
    private final TypeActivite type;
    private final String libelle;
    private final LocalDateTime debut;
    private final int dureeMinutes;
    private final int capaciteMax;
    private final Professeur professeur;
    private final Salle salle;
    private final Instrument instrument;
    private final List<Participation> participations = new ArrayList<>();
    private Statut statut = Statut.PLANIFIEE;

    public Activite(TypeActivite type, String libelle, LocalDateTime debut, int dureeMinutes,
                    int capaciteMax, Professeur professeur, Salle salle, Instrument instrument) {
        this.type = Objects.requireNonNull(type, "Le type d'activité est obligatoire.");
        this.libelle = Objects.requireNonNull(libelle, "Le libellé est obligatoire.").trim();
        if (this.libelle.isEmpty()) {
            throw new IllegalArgumentException("Le libellé ne peut pas être vide.");
        }
        this.debut = Objects.requireNonNull(debut, "La date de début est obligatoire.");
        if (dureeMinutes < 1) {
            throw new IllegalArgumentException("La durée doit être supérieure à zéro.");
        }
        if (capaciteMax < 1) {
            throw new IllegalArgumentException("La capacité doit être supérieure à zéro.");
        }
        this.dureeMinutes = dureeMinutes;
        this.capaciteMax = capaciteMax;
        this.professeur = professeur;
        this.salle = Objects.requireNonNull(salle, "La salle est obligatoire.");
        // AJOUT : une location de salle n'a pas d'instrument
        this.instrument = (type == TypeActivite.LOCATION_SALLE)
                ? instrument
                : Objects.requireNonNull(instrument, "L'instrument est obligatoire.");
    }

    public int getId() {
        return id;
    }

    public TypeActivite getType() {
        return type;
    }

    public String getLibelle() {
        return libelle;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    // AJOUT
    public LocalDateTime getFin() {
        return debut.plusMinutes(dureeMinutes);
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public Professeur getProfesseur() {
        return professeur;
    }

    public Salle getSalle() {
        return salle;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public List<Participation> getParticipations() {
        return Collections.unmodifiableList(participations);
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = Objects.requireNonNull(statut, "Le statut est obligatoire.");
    }

    public boolean estInscrit(Etudiant etudiant) {
        return participations.stream()
                .anyMatch(p -> p.getEtudiant().getId() == etudiant.getId());
    }

    public boolean inscriptionOuverte() {
        return statut == Statut.PLANIFIEE && participations.size() < capaciteMax;
    }

    public void ajouterParticipation(Participation participation) {
        Objects.requireNonNull(participation, "La participation est obligatoire.");
        if (participation.getActivite() != this) {
            throw new IllegalArgumentException("La participation ne concerne pas cette activité.");
        }
        if (estInscrit(participation.getEtudiant())) {
            throw new IllegalStateException("Cet étudiant est déjà inscrit à l'activité.");
        }
        if (!inscriptionOuverte()) {
            throw new IllegalStateException("Les inscriptions sont fermées.");
        }
        participations.add(participation);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s, %s, %d min, salle %s (%d/%d inscrits, %s)",
                id, type, libelle, debut, dureeMinutes, salle.getNom(),
                participations.size(), capaciteMax, statut);
    }
}
