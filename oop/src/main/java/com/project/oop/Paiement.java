package com.project.oop;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Paiement {
    private static final AtomicInteger PROCHAIN_ID = new AtomicInteger(1);

    private final int id = PROCHAIN_ID.getAndIncrement();
    private final Etudiant etudiant;
    private final Achat achat;
    private final double montant;
    private final LocalDate date;
    private final ModePaiement mode;

    public Paiement(Etudiant etudiant, Achat achat, double montant, LocalDate date, ModePaiement mode) {
        this.etudiant = Objects.requireNonNull(etudiant, "L'etudiant est obligatoire.");
        this.achat = Objects.requireNonNull(achat, "L'achat est obligatoire.");
        if (!Double.isFinite(montant) || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif.");
        }
        this.montant = Math.round(montant * 100.0) / 100.0;
        this.date = Objects.requireNonNull(date, "La date est obligatoire.");
        this.mode = Objects.requireNonNull(mode, "Le mode de paiement est obligatoire.");
    }

    public int getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public Achat getAchat() { return achat; }
    public double getMontant() { return montant; }
    public LocalDate getDate() { return date; }
    public ModePaiement getMode() { return mode; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%d] %s - %.2f EUR (%s) - %s", id, date, montant, mode, achat.getLibelle());
    }
}
