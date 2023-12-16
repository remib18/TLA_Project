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

import App.adventure.Adventure;
import App.adventure.AdventureContent;
import App.adventure.Location;
import App.adventure.Proposition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

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

    Map<Integer, Location> locations;
    Location currentLocation;

    JFrame frame;
    JPanel mainPanel;

    /**
     * Labels composant la zone de texte
     */
    JLabel[] labels;

    /**
     * Boutons de proposition
     */
    ArrayList<JButton> btns;

    public static void main(String[] args) {
        App app = new App();
        SwingUtilities.invokeLater(app::init);
    }

    private void init() {

        // Load adventure
        // Todo: load from file (using AdventureContent for testing)
        Adventure adventure = AdventureContent.getAdventure();

        // Charge le contenu de l'aventure
        locations = adventure.locations();

        // Prépare l'IHM
        labels = new JLabel[linesNumber];
        btns = new ArrayList<>();

        frame = new JFrame(adventure.title());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        frame.add(mainPanel);

        for(int i = 0; i< linesNumber; i++) {
            labels[i] = new JLabel(" ");
            mainPanel.add(labels[i], new GridBagConstraints() {{
                this.gridwidth = GridBagConstraints.REMAINDER;
                this.anchor = GridBagConstraints.WEST;
                this.insets = new Insets(0,20,0,20);
            }});
            labels[i].setMinimumSize(new Dimension(750, 20));
            labels[i].setPreferredSize(new Dimension(750, 20));
        }

        // Démarre l'aventure au lieu n° 1
        currentLocation = locations.get(1);
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
        display(currentLocation.description().split("\n"));
        frame.pack();
        for(int i = 0; i< currentLocation.propositions().size(); i++) {
            JButton btn = new JButton(STR."<html><p>\{currentLocation.propositions().get(i).text()}</p></html>");
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
        Proposition proposition = currentLocation.propositions().get(index);

        // Recherche le lieu désigné par la proposition
        Location location = locations.get(proposition.locationNumber());
        if (location != null) {

            // Affiche la proposition qui vient d'être choisie par le joueur
            display(new String[]{STR."> \{proposition.text()}"});

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
    private void display(String[] contenu) {
        int n = contenu.length;
        for (int i = 0; i < linesNumber -(n+1); i++) {
            labels[i].setText(labels[i + n + 1].getText());
        }
        labels[linesNumber -(n+1)].setText(" ");
        for(int i = 0; i<n; i++) {
            labels[linesNumber -n+i].setText(contenu[i]);
        }
    }

}
