package App.adventure;

/*
 * Proposition faite au joueur pour poursuivre dans l'aventure.
 * 
 * Une proposition mène à un nouveau lieu, identifié par un numéro.
 */
public record Proposition(String text, Integer locationNumber) {

    @Override
    public String text() {
        return text;
    }

    @Override
    public Integer locationNumber() {
        return locationNumber;
    }
}
