package com.project.oop;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/** Creneau hebdomadaire pendant lequel un professeur travaille (ex. lundi 09:00-18:00). */
public final class Disponibilite {
    private final DayOfWeek jour;
    private final LocalTime heureDebut;
    private final LocalTime heureFin;

    public Disponibilite(DayOfWeek jour, LocalTime heureDebut, LocalTime heureFin) {
        this.jour = Objects.requireNonNull(jour, "Le jour est obligatoire.");
        this.heureDebut = Objects.requireNonNull(heureDebut, "L'heure de debut est obligatoire.");
        this.heureFin = Objects.requireNonNull(heureFin, "L'heure de fin est obligatoire.");
        if (!heureFin.isAfter(heureDebut)) {
            throw new IllegalArgumentException("L'heure de fin doit etre apres l'heure de debut.");
        }
    }

    public DayOfWeek getJour() { return jour; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }

    /** Vrai si l'activite (debut + duree) tient entierement dans ce creneau. */
    public boolean couvre(LocalDateTime debut, int dureeMinutes) {
        LocalDateTime fin = debut.plusMinutes(dureeMinutes);
        if (debut.getDayOfWeek() != jour) return false;
        if (!fin.toLocalDate().equals(debut.toLocalDate())) return false;
        return !debut.toLocalTime().isBefore(heureDebut) && !fin.toLocalTime().isAfter(heureFin);
    }

    @Override
    public String toString() {
        return jour + " " + heureDebut + "-" + heureFin;
    }
}
