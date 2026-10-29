// MenuPersonnes.java
import java.util.Scanner;

public class MenuPersonnes extends Menu {
    private final GestionPersonnes gestion;

    public MenuPersonnes(Scanner scanner, GestionPersonnes gestion) {
        super(scanner);
        this.gestion = gestion;
    }

    @Override
    public String getTitre() { return "a. Etudiants et professeurs"; }

    @Override
    public void afficher() {
        System.out.println("1. Ajouter un etudiant");
        System.out.println("2. Ajouter un professeur");
        System.out.println("3. Rechercher une personne par id");
        System.out.println("4. Lister toutes les personnes");
    }

    @Override
    public void executerChoix(int choix) {
        switch (choix) {
            case 1: ajouterEtudiant(); break;
            case 2: ajouterProfesseur(); break;
            case 3: rechercher(); break;
            case 4: lister(); break;
            default: System.out.println("Choix inconnu.");
        }
    }

    private void ajouterEtudiant() {
        String nom = lireTexte("Nom : ");
        String prenom = lireTexte("Prenom : ");
        String email = lireTexte("Email : ");
        String tel = lireTexte("Telephone : ");
        Niveau niveau = lireEnum("Niveau", Niveau.class);
        Etudiant e = gestion.ajouterEtudiant(nom, prenom, email, tel, niveau);
        System.out.println("Etudiant cree : " + e);
    }

    private void ajouterProfesseur() {
        String nom = lireTexte("Nom : ");
        String prenom = lireTexte("Prenom : ");
        String email = lireTexte("Email : ");
        String tel = lireTexte("Telephone : ");
        double taux = lireDecimal("Taux horaire (EUR) : ");
        Professeur p = gestion.ajouterProfesseur(nom, prenom, email, tel, taux);
        System.out.println("Professeur cree : " + p);
    }

    private void rechercher() {
        int id = lireEntier("Id de la personne : ");
        Personne p = gestion.rechercher(id);
        // p.toString() : dispatch dynamique vers Etudiant ou Professeur
        System.out.println(p == null ? "Aucune personne avec l'id " + id : p.toString());
    }

    private void lister() {
        for (Personne p : gestion.listerTous()) System.out.println(p);
    }
}
