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

    Adventure adventure;

    Location currentLocation;

    JFrame frame;
    JPanel mainPanel;

    JTextPane textPane;

    JLabel life;

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

        initAdventure();

        // Prépare l'IHM
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("monospaced", Font.PLAIN, 18));
        textPane.setPreferredSize(new Dimension(1000, 400));
        textPane.setMinimumSize(new Dimension(600, 400));
        life = new JLabel();

        btns = new ArrayList<>();

        frame = new JFrame(adventure.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        frame.add(mainPanel);

        scrollPane = new JScrollPane(textPane);

        mainPanel.add(life);

        mainPanel.add(scrollPane, new GridBagConstraints() {{
            this.gridwidth = GridBagConstraints.REMAINDER;
            this.anchor = GridBagConstraints.WEST;
            this.insets = new Insets(0,20,0,20);
        }});

        // Démarre l'aventure au lieu n° 1
        initLocations();

        adventure.getState().subscribe(state -> {
            if (state.isGameOver()) {
                JOptionPane.showMessageDialog(null, STR."Oh no, you died !");
                System.exit(0);
            }
            if (state.isGameWon()) {
                JOptionPane.showMessageDialog(null, STR."Congratulations, you won !");
                System.exit(0);
            }
            life.setText(STR."PV: \{state.getCurrentHealth()}");
        });

        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(1100, 400));
    }

    /*
     * Affichage du lieu lieuActuel et créations des boutons de propositions correspondantes
     * à ce lieu
     */
    void initLocations() {
        Location location = adventure.getCurrentLocation();
        for(JButton btn: btns) {
            mainPanel.remove(btn);
        }
        btns.clear();
        display(location.description());
        frame.pack();
        List<Proposition> propositionList = adventure.getAvailablePropositions(location.id());
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

        Proposition proposition = adventure.performProposition(index);

        // Mise à jour de l'emplacement du joueur
        Location location = adventure.getCurrentLocation();
        if (!Objects.isNull(location) && !Objects.isNull(proposition)) {

            // Affiche la proposition qui vient d'être choisie par le joueur
            display(STR."> \{proposition.text()}");

            // Affichage du nouveau lieu et création des boutons des nouvelles propositions
            initLocations();
        }
    }

    /*
     * Gère l'affichage dans la zone de texte, avec un effet de défilement
     * (comme dans un terminal)
     */
    private void display(String contenu) {
        textPane.setText(contenu);
    }

    private void initAdventure() {
        String file = "adventures/"+AdventureAnalyzer.getFile();
        System.out.println(file);
        try {
            adventure = Interpreter.interpret(file);
        } catch (LexicalErrorException | UnexpectedTokenException | IncompleteParsingException | IllegalCaracterException e) {
            throw new RuntimeException(e);
        }
    }

}
