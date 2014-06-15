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

class StandingsToolGui {

    /**
     * Frame instance and the exit exit menu item in the File JMenu.
     */
    private JFrame frame;
    private JMenuItem exitItem;

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
        JMenu fileMenu = new JMenu("File");
        JMenu standingsType = new JMenu("Standings type");
        final ItemMenuListener itemMenuListener = new ItemMenuListener();
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
        bar.add(fileMenu);
        frame.setJMenuBar(bar);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        searchField.requestFocus();
    }

    /**
     * This class will keep track of the text that is inserted in the searchField and will,
     * real time filter the table's rows.
     */
    private class SearchListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText().trim());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText().trim());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            table.filterRows(searchField.getText().trim());
        }
    }

    /**
     * Will handle the menuItem events, will close the frame when exitItem os selected,
     * and will change the current table's StandingType if selected.
     */
    private class ItemMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            if (item.equals(exitItem)) {
                frame.dispose();
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