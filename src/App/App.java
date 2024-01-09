package App;

/*
 * Projet TLA 2023-24
 *
 * Réalisé par :
 * - BERNARD Rémi
 * - CHERAMY Benoît
 * - GALLI Gabriel
 * - QUILLON Alexis
 *
 */

import App.adventure.*;
import App.exceptions.IllegalCaracterException;
import App.exceptions.IncompleteParsingException;
import App.exceptions.LexicalErrorException;
import App.exceptions.UnexpectedTokenException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/*
 * Classe principale.
 * 
 * Gère l'IHM.
 * Affiche les lieux et propositions suivant les décisions du joueur.
 */

public class App implements ActionListener {

    /**
     * Nombre de lignes dans la zone de texte
     */
    final int linesNumber = 20;

    Adventure adventure;

    Location currentLocation;

    JFrame frame;
    JPanel mainPanel;

    JTextPane textPane;

    JScrollPane scrollPane;

    /**
     * Boutons de proposition
     */
    ArrayList<JButton> btns;

    public static void main(String[] args) {
        App app = new App();
        SwingUtilities.invokeLater(app::init);
    }

    private void init() {


        String file = "adventures/"+AdventureAnalyzer.getFile();
        System.out.println(file);
        try {
            adventure = Interpreter.interpret(file);
        } catch (LexicalErrorException | UnexpectedTokenException | IncompleteParsingException | IllegalCaracterException e) {
            throw new RuntimeException(e);
        }

        // Prépare l'IHM
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("monospaced", Font.PLAIN, 18));
        textPane.setPreferredSize(new Dimension(1000, 400));
        textPane.setMinimumSize(new Dimension(600, 400));

        btns = new ArrayList<>();

        frame = new JFrame(adventure.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        frame.add(mainPanel);

        scrollPane = new JScrollPane(textPane);

        mainPanel.add(scrollPane, new GridBagConstraints() {{
            this.gridwidth = GridBagConstraints.REMAINDER;
            this.anchor = GridBagConstraints.WEST;
            this.insets = new Insets(0,20,0,20);
        }});

        // Démarre l'aventure au lieu n° 1
        currentLocation = adventure.getLocation(1);
        initLocations();

        frame.pack();
        frame.setVisible(true);
    }

    /*
     * Affichage du lieu lieuActuel et créations des boutons de propositions correspondantes
     * à ce lieu
     */
    void initLocations() {
        for(JButton btn: btns) {
            mainPanel.remove(btn);
        }
        btns.clear();
        display(currentLocation.description());
        frame.pack();
        List<Proposition> propositionList = adventure.getAvailablePropositions(currentLocation.id());
        for(int i = 0; i< propositionList.size(); i++) {
            JButton btn = new JButton(STR."<html><p>\{propositionList.get(i).text()}</p></html>");
            btn.setActionCommand(String.valueOf(i));
            btn.addActionListener(this);
            mainPanel.add(btn, new GridBagConstraints() {{
                this.gridwidth = GridBagConstraints.REMAINDER;
                this.fill = GridBagConstraints.HORIZONTAL;
                this.insets = new Insets(3,20,3,20);
            }});
            btns.add(btn);
        }
        frame.pack();
    }

    /*
     * Gère les clics sur les boutons de propostion
     */
    public void actionPerformed(ActionEvent event) {

        // Retrouve l'index de la proposition
        int index = Integer.parseInt(event.getActionCommand());

        // Retrouve la proposition
        Proposition proposition = adventure.getAvailablePropositions(currentLocation.id()).get(index);

        // Recherche le lieu désigné par la proposition
        Location location = adventure.getLocation(proposition.locationNumber());
        if (location != null) {

            // Affiche la proposition qui vient d'être choisie par le joueur
            display(STR."> \{proposition.text()}");

            // Affichage du nouveau lieu et création des boutons des nouvelles propositions
            currentLocation = location;
            initLocations();
        } else {
            // Cas particulier : le lieu est déclarée dans une proposition mais pas encore décrit
            // (lors de l'élaboration de l'aventure par exemple)
            JOptionPane.showMessageDialog(null, STR."Lieu n° \{proposition.locationNumber()} à implémenter");
        }
    }

    /*
     * Gère l'affichage dans la zone de texte, avec un effet de défilement
     * (comme dans un terminal)
     */
    private void display(String contenu) {
        textPane.setText(contenu);
    }

}
