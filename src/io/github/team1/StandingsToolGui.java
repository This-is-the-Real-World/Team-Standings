package io.github.team1;

/**
 * Created with IntelliJ IDEA.
 * User: André and Qosmiof2
 * <p/>
 * Date: 14/06/2014
 * Time: 15:47
 */

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StandingsToolGui {

    /**
     * Frame instance and the exit, about and howTo JMenuItem instances.
     */
    private JFrame frame;
    private JMenuItem exitItem;
    private JMenuItem aboutItem;
    private JMenuItem wikiItem;

    /**
     * Table related fields, the table instance and the search field.
     */
    private StandingsTable table;
    private JTextField searchField;

    /**
     * Initializes the Standings tool gui.
     */
    public void init() {
        WebLookAndFeel.install();
        frame = new JFrame("Team Standings");

        table = new StandingsTable();
        JScrollPane tableScrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchField = new JTextField() {{
            getDocument().addDocumentListener(new SearchListener());
        }};

        JMenuBar bar = new JMenuBar();
        final ItemMenuListener itemMenuListener = new ItemMenuListener();

        JMenu helpMenu = new JMenu("Help");
        wikiItem = new JMenuItem("Wiki");
        wikiItem.addActionListener(itemMenuListener);
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(itemMenuListener);

        JMenu fileMenu = new JMenu("File");
        JMenu standingsType = new JMenu("Standings type");
        for (StandingsTable.StandingType t : StandingsTable.StandingType.values()) {
            standingsType.add(new JMenuItem(t.toString()) {{
                addActionListener(itemMenuListener);
            }});
        }
        exitItem = new JMenuItem("Exit") {{
            addActionListener(itemMenuListener);
        }};

        frame.getContentPane().add(searchField, BorderLayout.SOUTH);
        frame.getContentPane().add(tableScrollPane, BorderLayout.CENTER);

        fileMenu.add(standingsType);
        fileMenu.add(exitItem);

        helpMenu.add(wikiItem);
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(helpMenu);
        frame.setJMenuBar(bar);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        searchField.requestFocus();
    }

    /**
     * Opens the default browser and goes to the given url.
     * @param url that the browser will open
     */
    private void browseURL(String url){
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a frame when the About JItemMenu is clicked.
     * Contains information about the team, the tool and when it was released.
     */
    private void createAboutFrame(){
        String about = "<html>"
                + "<b><u>ProjectGitHubStandings v0.01</u></b><br><br>"
                + "<font size=2><b>Creators:</b> Team *INSERT NAME HERE*<br>"
                + "<b>Released on</b>: 15/6/2014<br>"
                + "<b>Team *INSERT NAME HERE*:</b> Qosmiof2, Patriq, Term</b></font>"
                + "</html>";
        JOptionPane.showMessageDialog(frame, about, "About", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * This class will keep track of the text that is inserted in the searchField and will,
     * real time filter the table's rows.
     */
    private class SearchListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText());
        }
    }

    /**
     * Will handle the menuItem events, will close the frame when exitItem os selected,
     * and will change the current table's StandingType if selected.
     */
    private class ItemMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent a) {
            JMenuItem item = (JMenuItem) a.getSource();
            if (item.equals(exitItem)) {
                frame.dispose();
                return;
            }
            if(item.equals(wikiItem)){
                browseURL("https://github.com/This-is-the-Real-World/Team-Standings/wiki");
                return;
            }
            if(item.equals(aboutItem)){
                createAboutFrame();
                return;
            }
            for (StandingsTable.StandingType s : StandingsTable.StandingType.values()) {
                if (item.getText().equals(s.toString())) {
                    if (!table.getType().equals(s)) {
                        table.setStandingsType(s);
                        frame.setTitle(s.toString() + " Standings");
                    }
                    break;
                }
            }
        }
    }
}