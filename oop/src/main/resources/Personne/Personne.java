// Personne.java — classe abstraite n°1
public abstract class Personne {
    private static int compteur = 1;

    private final int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    protected Personne(String nom, String prenom, String email, String telephone) {
        this.id = compteur++;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    /** Constructeur de copie */
    protected Personne(Personne autre) {
        this.id = autre.id;
        this.nom = autre.nom;
        this.prenom = autre.prenom;
        this.email = autre.email;
        this.telephone = autre.telephone;
    }

    public abstract String getRole();

    public String getNomComplet() { return prenom + " " + nom; }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s, %s)", id, getRole(), getNomComplet(), email, telephone);
    }
}
