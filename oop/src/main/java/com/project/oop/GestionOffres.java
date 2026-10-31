package com.project.oop;

// GestionOffres.java : forfaits, cours unitaires, locations d'instruments
// Les achats sont crees par Facturation (meme principe que GestionExamens avec Ecole).
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestionOffres {
    private final Ecole ecole;
    private final Facturation facturation;

    public GestionOffres(Ecole ecole, Facturation facturation) {
        this.ecole = ecole;
        this.facturation = facturation;
    }

    /** Vend un forfait a un etudiant, a la date du jour de l'ecole. */
    public Achat vendreForfait(Etudiant etudiant, String libelle, int heures, boolean illimite,
                               int validiteJours, double prix) {
        return vendreForfait(etudiant, libelle, heures, illimite, validiteJours, prix, aujourdhui());
    }

    /** Vend un forfait a une date donnee (utile pour les donnees de test). */
    public Achat vendreForfait(Etudiant etudiant, String libelle, int heures, boolean illimite,
                               int validiteJours, double prix, LocalDate dateAchat) {
        if (etudiant == null) throw new IllegalArgumentException("L'etudiant est obligatoire.");
        if (libelle == null || libelle.isBlank())
            throw new IllegalArgumentException("Le libelle du forfait est obligatoire.");
        if (validiteJours < 1) throw new IllegalArgumentException("La validite doit etre d'au moins 1 jour.");
        if (!Double.isFinite(prix) || prix <= 0) throw new IllegalArgumentException("Le prix doit etre > 0.");
        if (!illimite && heures < 1)
            throw new IllegalArgumentException("Un forfait d'heures doit contenir au moins 1 heure.");

        Achat achat = facturation.creerAchat(etudiant, TypeAchat.FORFAIT, libelle.trim(), prix, dateAchat);
        achat.definirForfait(heures, illimite, dateAchat, dateAchat.plusDays(validiteJours));
        return achat;
    }

    public Achat louerInstrument(Etudiant etudiant, Instrument instrument, int jours, double prixParJour) {
        if (etudiant == null) throw new IllegalArgumentException("L'etudiant est obligatoire.");
        if (instrument == null) throw new IllegalArgumentException("L'instrument est obligatoire.");
        if (jours < 1) throw new IllegalArgumentException("La location doit durer au moins 1 jour.");
        if (!Double.isFinite(prixParJour) || prixParJour <= 0)
            throw new IllegalArgumentException("Le prix par jour doit etre > 0.");

        return facturation.creerAchat(etudiant, TypeAchat.LOCATION_INSTRUMENT,
                "Location " + instrument.getNom() + " (" + jours + " jours)",
                prixParJour * jours, aujourdhui());
    }

    /**
     * Cherche un forfait valide a cette date et qui a assez d'heures.
     * S'il y en a plusieurs, on prend celui qui expire en premier. Retourne null si aucun.
     */
    public Achat trouverCredit(Etudiant etudiant, LocalDate date, double heures) {
        Achat meilleur = null;
        for (Achat a : facturation.achatsDe(etudiant)) {
            if (a.estForfait() && !a.estAnnule() && a.estValideLe(date) && a.aAssezDHeures(heures)) {
                if (meilleur == null || expireAvant(a, meilleur)) meilleur = a;
            }
        }
        return meilleur;
    }

    private boolean expireAvant(Achat a, Achat b) {
        if (a.getDateFin() == null) return false;   // pas d'expiration : passe en dernier
        if (b.getDateFin() == null) return true;
        return a.getDateFin().isBefore(b.getDateFin());
    }

    /** Pas de forfait valide : le cours est facture seul (taux horaire du professeur). */
    public Achat creerAchatCoursUnitaire(Etudiant etudiant, Activite cours) {
        double heures = cours.getDureeMinutes() / 60.0;
        double prix = cours.getProfesseur().getTauxHoraire() * heures;
        return facturation.creerAchat(etudiant, TypeAchat.COURS_UNITAIRE,
                "Cours unitaire #" + cours.getId(), prix, aujourdhui());
    }

    public Achat creerAchatLocationSalle(Etudiant etudiant, Activite reservation) {
        double heures = reservation.getDureeMinutes() / 60.0;
        double prix = reservation.getSalle().getTarifHoraire() * heures;
        return facturation.creerAchat(etudiant, TypeAchat.LOCATION_SALLE,
                "Reservation #" + reservation.getId() + " (" + reservation.getSalle().getNom() + ")",
                prix, aujourdhui());
    }

    /** Somme des heures restantes des forfaits d'heures encore valides ce jour-la. */
    public double heuresRestantes(Etudiant etudiant, LocalDate date) {
        double total = 0;
        for (Achat a : facturation.achatsDe(etudiant)) {
            if (a.estForfait() && !a.isIllimite() && a.estValideLe(date)) total += a.getHeuresRestantes();
        }
        return total;
    }

    /** Heures perdues parce que le forfait a expire. */
    public double heuresPerdues(Etudiant etudiant, LocalDate date) {
        double total = 0;
        for (Achat a : facturation.achatsDe(etudiant)) total += a.heuresPerdues(date);
        return total;
    }

    public List<Activite> listerCours() {
        List<Activite> cours = new ArrayList<>();
        for (Activite a : ecole.getActivites())
            if (a.getType() == TypeActivite.COURS) cours.add(a);
        return cours;
    }

    public List<Achat> listerAchats(Etudiant etudiant) { return facturation.achatsDe(etudiant); }

    public List<Achat> listerTousLesAchats() { return ecole.getAchats(); }

    public Etudiant trouverEtudiant(int id) { return ecole.trouverEtudiant(id); }

    public Instrument trouverInstrument(String nom) { return ecole.trouverInstrument(nom); }

    public List<Instrument> listerInstruments() { return ecole.getInstruments(); }

    public LocalDate aujourdhui() { return ecole.maintenant().toLocalDate(); }
}
