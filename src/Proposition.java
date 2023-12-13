/*
 * Proposition faite au joueur pour poursuivre dans l'aventure.
 * 
 * Une proposition mène à un nouveau lieu, identifié par un numéro.
 */
public class Proposition {
    String text;
    int locationNumber;

    public Proposition(String text, int locationNumber) {
        this.text = text;
        this.locationNumber = locationNumber;
    }
}
