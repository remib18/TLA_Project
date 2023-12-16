package App.adventure;

/*
 * Proposition faite au joueur pour poursuivre dans l'aventure.
 * 
 * Une proposition mène à un nouveau lieu, identifié par un numéro.
 */
public record Proposition(String text, Integer locationNumber) {

    /**
     * Get the text of the proposition
     * @return The text of the proposition
     */
    @Override
    public String text() {
        return text;
    }

    /**
     * Get the location number of the proposition
     * @return The location number of the proposition
     */
    @Override
    public Integer locationNumber() {
        return locationNumber;
    }
}
