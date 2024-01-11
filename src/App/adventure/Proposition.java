package App.adventure;

import java.util.List;

/*
 * Proposition faite au joueur pour poursuivre dans l'aventure.
 * 
 * Une proposition mène à un nouveau lieu, identifié par un numéro.
 */
public record Proposition(List<Condition> conditions, String text, Integer locationNumber, List<Event> events) {

    /**
     * Get the list of conditions of the proposition
     * @return The list of conditions of the proposition
     */
    @Override
    public List<Condition> conditions() {
        return conditions;
    }

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

    /**
     * Get the events of the proposition
     * @return The events of the proposition
     */
    @Override
    public List<Event> events() {
        return events;
    }
}
