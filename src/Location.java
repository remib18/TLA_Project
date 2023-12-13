import java.util.List;

/*
 * Lieu dans l'aventure.
 * 
 * Composée d'une description et d'aucune, une ou plusieurs propositions.
 */
public class Location {
    String description;
    List<Proposition> propositions;

    public Location(String description, List<Proposition> propositions) {
        this.description = description;
        this.propositions = propositions;
    }
}
