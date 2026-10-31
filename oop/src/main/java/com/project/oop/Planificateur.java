package com.project.oop;

// Planificateur.java : planning des cours et reservations de salle
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Planificateur {
    private final Ecole ecole;
    private final GestionOffres offres;

    public Planificateur(Ecole ecole, GestionOffres offres) {
        this.ecole = ecole;
        this.offres = offres;
    }

    /**
     * Planifie un cours (individuel ou collectif). Toutes les verifications sont faites AVANT
     * de modifier quoi que ce soit : specialite, disponibilite, capacite de la salle, conflits.
     * Chaque etudiant consomme la duree du cours dans son forfait, ou paie le cours seul
     * s'il n'a aucun forfait valide.
     */
    public Activite planifierCours(Professeur prof, List<Etudiant> etudiants, Salle salle,
                                   Instrument instrument, LocalDateTime debut, int dureeMinutes) {
        if (prof == null) throw new IllegalArgumentException("Le professeur est obligatoire.");
        if (salle == null) throw new IllegalArgumentException("La salle est obligatoire.");
        if (instrument == null) throw new IllegalArgumentException("L'instrument est obligatoire.");
        if (etudiants == null || etudiants.isEmpty())
            throw new IllegalArgumentException("Un cours a besoin d'au moins un etudiant.");
        if (dureeMinutes < 1) throw new IllegalArgumentException("La duree doit etre >= 1 minute.");
        verifierPasDansLePasse(debut);

        Set<Integer> ids = new HashSet<>();
        for (Etudiant e : etudiants) {
            if (ecole.trouverEtudiant(e.getId()) != e)
                throw new IllegalArgumentException(e.getNomComplet() + " n'est pas inscrit a l'ecole.");
            if (!ids.add(e.getId()))
                throw new IllegalArgumentException(e.getNomComplet() + " est present deux fois dans la liste.");
        }
        if (!prof.peutEnseigner(instrument))
            throw new IllegalStateException(prof.getNomComplet() + " n'enseigne pas " + instrument + ".");
        if (!prof.estDisponible(debut, dureeMinutes))
            throw new IllegalStateException(prof.getNomComplet() + " n'est pas disponible a ce moment ("
                    + prof.getDisponibilites() + ").");
        if (etudiants.size() > salle.getCapacite())
            throw new IllegalStateException(salle.getNom() + " est trop petite (capacite "
                    + salle.getCapacite() + ").");
        String conflit = trouverConflit(debut, dureeMinutes, salle, prof, etudiants);
        if (conflit != null) throw new IllegalStateException("Conflit d'horaire : " + conflit);

        // 1) chercher le forfait de chaque etudiant (rien n'est modifie)
        double heures = dureeMinutes / 60.0;
        List<Achat> credits = new ArrayList<>();
        for (Etudiant e : etudiants) {
            Achat credit = offres.trouverCredit(e, debut.toLocalDate(), heures);
            if (credit == null && prof.getTauxHoraire() <= 0)
                throw new IllegalStateException("Pas de forfait pour " + e.getNomComplet()
                        + " et " + prof.getNomComplet() + " n'a pas de taux horaire : cours impossible a facturer.");
            credits.add(credit);
        }

        // 2) creer le cours puis une participation par etudiant
        Activite cours = new Activite(TypeActivite.COURS, "Cours de " + instrument.getNom(), debut,
                dureeMinutes, salle.getCapacite(), prof, salle, instrument);
        ecole.ajouterActivite(cours);
        for (int i = 0; i < etudiants.size(); i++) {
            Etudiant e = etudiants.get(i);
            Achat achat = credits.get(i);
            if (achat != null) {
                achat.decompter(heures, String.format("Cours [%d] le %s (-%.1f h)",
                        cours.getId(), debut.toLocalDate(), heures));
            } else {
                achat = offres.creerAchatCoursUnitaire(e, cours);
            }
            Participation p = new Participation(e, cours);
            p.setAchat(achat);
            cours.ajouterParticipation(p);
            ecole.ajouterParticipation(p);
        }
        return cours;
    }

    /**
     * Annule un cours. Retourne true si l'annulation est tardive (moins de 24 h avant le debut) :
     * les heures sont alors consommees. Sinon elles sont rendues.
     */
    public boolean annulerCours(Activite cours) {
        verifierType(cours, TypeActivite.COURS);
        if (cours.getStatut() != Statut.PLANIFIEE)
            throw new IllegalStateException("Seul un cours planifie peut etre annule (statut : "
                    + cours.getStatut() + ").");

        boolean tardive = ecole.maintenant().plusHours(24).isAfter(cours.getDebut());
        double heures = cours.getDureeMinutes() / 60.0;
        cours.setStatut(Statut.ANNULEE);
        for (Participation p : cours.getParticipations()) {
            Achat achat = p.getAchat();
            if (achat == null) continue;
            if (tardive) {
                achat.ajouterHistorique("Cours [" + cours.getId()
                        + "] annule moins de 24 h avant : heures consommees");
            } else if (achat.estForfait()) {
                achat.rembourser(heures, String.format("Cours [%d] annule a temps (+%.1f h)",
                        cours.getId(), heures));
            } else {
                achat.annuler();
            }
        }
        return tardive;
    }

    public void terminerCours(Activite cours) {
        verifierType(cours, TypeActivite.COURS);
        if (cours.getStatut() != Statut.PLANIFIEE)
            throw new IllegalStateException("Seul un cours planifie peut etre termine.");
        if (cours.getFin().isAfter(ecole.maintenant()))
            throw new IllegalStateException("Ce cours n'est pas encore fini (fin : " + cours.getFin() + ").");
        cours.setStatut(Statut.TERMINEE);
    }

    /** Un etudiant reserve une salle. La reservation est facturee au tarif horaire de la salle. */
    public Activite reserverSalle(Etudiant etudiant, Salle salle, LocalDateTime debut, int dureeMinutes) {
        if (etudiant == null) throw new IllegalArgumentException("L'etudiant est obligatoire.");
        if (salle == null) throw new IllegalArgumentException("La salle est obligatoire.");
        if (dureeMinutes < 1) throw new IllegalArgumentException("La duree doit etre >= 1 minute.");
        verifierPasDansLePasse(debut);
        if (ecole.trouverEtudiant(etudiant.getId()) != etudiant)
            throw new IllegalArgumentException(etudiant.getNomComplet() + " n'est pas inscrit a l'ecole.");
        if (salle.getTarifHoraire() <= 0)
            throw new IllegalStateException(salle.getNom() + " n'a pas de tarif horaire : reservation impossible.");

        List<Etudiant> participants = new ArrayList<>();
        participants.add(etudiant);
        String conflit = trouverConflit(debut, dureeMinutes, salle, null, participants);
        if (conflit != null) throw new IllegalStateException("Conflit d'horaire : " + conflit);

        Activite reservation = new Activite(TypeActivite.LOCATION_SALLE, "Location de salle", debut,
                dureeMinutes, 1, null, salle, null);
        ecole.ajouterActivite(reservation);
        Participation p = new Participation(etudiant, reservation);
        p.setAchat(offres.creerAchatLocationSalle(etudiant, reservation));
        reservation.ajouterParticipation(p);
        ecole.ajouterParticipation(p);
        return reservation;
    }

    public void annulerReservation(Activite reservation) {
        verifierType(reservation, TypeActivite.LOCATION_SALLE);
        if (reservation.getStatut() != Statut.PLANIFIEE)
            throw new IllegalStateException("Seule une reservation planifiee peut etre annulee.");
        reservation.setStatut(Statut.ANNULEE);
        for (Participation p : reservation.getParticipations()) {
            if (p.getAchat() != null) p.getAchat().annuler();
        }
    }

    /** Agenda : cours, reservations et examens melanges, tries par date de debut. */
    public List<Activite> agenda() {
        List<Activite> liste = new ArrayList<>(ecole.getActivites());
        liste.sort((a, b) -> a.getDebut().compareTo(b.getDebut()));
        return liste;
    }

    /**
     * Cherche une activite planifiee qui chevauche le creneau et qui utilise la meme salle,
     * le meme professeur ou un des memes etudiants. Retourne un message, ou null s'il n'y a pas de conflit.
     */
    public String trouverConflit(LocalDateTime debut, int dureeMinutes, Salle salle,
                                 Professeur prof, List<Etudiant> etudiants) {
        LocalDateTime fin = debut.plusMinutes(dureeMinutes);
        for (Activite a : ecole.getActivites()) {
            if (a.getStatut() != Statut.PLANIFIEE) continue;
            boolean chevauche = debut.isBefore(a.getFin()) && a.getDebut().isBefore(fin);
            if (!chevauche) continue;
            if (a.getSalle() == salle)
                return "la salle " + salle.getNom() + " est occupee par " + a;
            if (prof != null && a.getProfesseur() == prof)
                return prof.getNomComplet() + " a deja : " + a;
            for (Etudiant e : etudiants) {
                if (a.estInscrit(e)) return e.getNomComplet() + " a deja : " + a;
            }
        }
        return null;
    }

    private void verifierPasDansLePasse(LocalDateTime debut) {
        if (debut == null) throw new IllegalArgumentException("La date de debut est obligatoire.");
        if (debut.isBefore(ecole.maintenant()))
            throw new IllegalArgumentException("Impossible de planifier dans le passe (maintenant : "
                    + ecole.maintenant() + ").");
    }

    private void verifierType(Activite a, TypeActivite attendu) {
        if (a == null) throw new IllegalArgumentException("L'activite est introuvable.");
        if (a.getType() != attendu)
            throw new IllegalArgumentException("L'activite " + a.getId() + " n'est pas de type " + attendu + ".");
    }

    // ---------- recherches utilisees par le menu ----------
    public Activite trouverActivite(int id) { return ecole.trouverActivite(id); }
    public Etudiant trouverEtudiant(int id) { return ecole.trouverEtudiant(id); }
    public Professeur trouverProfesseur(int id) { return ecole.trouverProfesseur(id); }
    public Salle trouverSalle(String nom) { return ecole.trouverSalle(nom); }
    public Instrument trouverInstrument(String nom) { return ecole.trouverInstrument(nom); }
    public List<Professeur> listerProfesseurs() { return ecole.getProfesseurs(); }
    public List<Salle> listerSalles() { return ecole.getSalles(); }
    public List<Instrument> listerInstruments() { return ecole.getInstruments(); }

    // ---------- horloge de l'ecole ----------
    public LocalDateTime maintenant() { return ecole.maintenant(); }
    public boolean horlogeFictive() { return ecole.horlogeFictive(); }
    public void fixerMaintenant(LocalDateTime date) { ecole.fixerMaintenant(date); }
    public void utiliserHorlogeReelle() { ecole.utiliserHorlogeReelle(); }
}
