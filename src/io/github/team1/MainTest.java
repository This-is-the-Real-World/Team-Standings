package io.github.team1;

/**
 * Created with IntelliJ IDEA.
 * User: André
 * <p/>
 * Date: 14/06/2014
 * Time: 11:33
 */
import com.alee.laf.WebLookAndFeel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: André
 * <p/>
 * Date: 13/06/2014
 * Time: 07:34
 */

/**
 * To be replaced with qosmio gui, this is just a test and source to show how to implement the table.
 */
public class MainTest {
    public static void main(String[] args) {
        WebLookAndFeel.install();
        JFrame frame = new JFrame("Team Standings");
        final StandingsTable table = new StandingsTable();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(table.getTableHeader(), BorderLayout.PAGE_START);
        final JTextField field = new JTextField();
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                table.filterRows(field.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                table.filterRows(field.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                table.filterRows(field.getText().trim());
            }
        });
        frame.getContentPane().add(field, BorderLayout.SOUTH);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(300, 120);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

