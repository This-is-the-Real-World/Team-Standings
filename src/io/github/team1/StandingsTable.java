package io.github.team1;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: André
 * <p/>
 * Date: 13/06/2014
 * Time: 18:37
 */

/**
 * A JTable that will be able to retrieve the current Standings values, from GitHub
 * and be able to live update them whenever they are edited.
 */
public class StandingsTable extends JTable {

    /**
     * oauth key used to log in into GitHub
     */
    private static final String OAUTH = "4ebe91f1a81037996949da2d30ecef6e9bbd8ac4";

    /**
     * GitHub and readMe instance
     */
    private GitHub gitHub;
    private GHContent readMe;

    /**
     * Current Table model and Standings Type
     */
    private StandingType type;
    private StandingsTableModel model;

    /**
     * Will create a StandingsTable with TEAM as a default StandingType.
     */
    public StandingsTable() {
        this(StandingType.TEAM);
    }

    /**
     * Will create a new table
     *
     * @param type Current Standings type, use INDIVIDUAL for individual standings,
     *             or TEAM for team standings.
     */
    public StandingsTable(StandingType type) {
        try {
            this.gitHub = GitHub.connectUsingOAuth(OAUTH);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.type = type;
        model = new StandingsTableModel(this);
        if (fetchReadMe()) parseReadMe();

        setModel(model);
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Filters rows using a given name.
     * It will filter the first column, that means its gonna filter either the team name, or person name.
     * The regex that is used will allow to filter when the name has the characters typed in, is case insensitive, example:
     * patr would only show up the PatriqDesigns row
     *
     * @param name the name to filter
     */
    public void filterRows(String name) {
        RowFilter<StandingsTableModel, Object> rowFilter;
        try {
            rowFilter = RowFilter.regexFilter("(?i)" + name + "[A-Za-z0-9_ -]*", 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        TableRowSorter<StandingsTableModel> rowSorter = ((TableRowSorter) getRowSorter());
        rowSorter.setRowFilter(rowFilter);
    }

    /**
     * Edits the current readMe and gets the new one full committed and pushed.
     *
     * @param name   team or person's name
     * @param points The points that we want to update to
     * @return if it was successful committing it and pushing it
     */
    private boolean editReadMe(Object name, Object points) {
        try {
            String commit = "Changed " + name + "'s score to " + points + ".";
            readMe = readMe.update(readMe.getContent().replaceAll(name + ":([A-Za-z0-9_ -]+)",
                    name + ": " + points.toString()), commit).getContent();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Will parse the read me and fill the table with the team or person names.
     *
     * @return if it was successful getting the readMe content and parsing it
     */
    private boolean parseReadMe() {
        try {
            Pattern pattern = Pattern.compile("([A-Za-z0-9_ -]+):([A-Za-z0-9_ -]+)");
            Matcher matcher = pattern.matcher(readMe.getContent());

            int rowCount = 0;
            while (matcher.find()) rowCount++;
            model.tableValues = new Object[rowCount][matcher.groupCount()];
            matcher.reset();

            for (int row = 0; row < rowCount; row++) {
                if (matcher.find()) {
                    for (int col = 0; col < model.tableValues[0].length; col++) {
                        String matchGroup = matcher.group(col + 1).trim();
                        model.tableValues[row][col] = matchGroup.matches("\\d+") ? Integer.parseInt(matchGroup) : matchGroup;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Will fetch the readMe file of the current type repository.
     *
     * @return if it was successful fetching the readMe file
     */
    private boolean fetchReadMe() {
        try {
            GHRepository repository = gitHub.getRepository(type.repository());
            readMe = repository.getFileContent("README.md");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * The standings type, is the type that we want the table to have.
     * You can use either the TEAM one to show and edit team points,
     * or you can use the INDIVIDUALS one to only be able to see their current points.
     */
    enum StandingType {

        TEAM(new String[]{"Team Names", "Points"}),
        INDIVIDUAL(new String[]{"Names", "Points"});

        private String[] tableNames;

        StandingType(String[] tableNames) {
            this.tableNames = tableNames;
        }

        public String[] tableNames() {
            return tableNames;
        }

        public String repository() {
            return "This-is-the-Real-World/" + toString() + "-Standings";
        }

        @Override
        public String toString() {
            return name().substring(0, 1).concat(name().substring(1).toLowerCase());
        }
    }

    /**
     * The TableModel that is gonna contain all the tables data.
     */
    private static class StandingsTableModel extends AbstractTableModel {

        /**
         * A StandingsTable instance
         */
        private StandingsTable table;

        /**
         * Table names and values
         */
        private String[] tableNames;
        private Object[][] tableValues;

        /**
         * Will create a new TableModel to be used in a JTable.
         * Will contain all the tables data and how it should or not handle data editions.
         *
         * @param table instance of a StandingsTable wheres this model is gonna be used
         */
        private StandingsTableModel(StandingsTable table) {
            this.table = table;
            tableNames = table.type.tableNames();
        }

        /**
         * Will get the name of the given column index.
         *
         * @param columnIndex the column index
         * @return the column name
         */
        @Override
        public String getColumnName(int columnIndex) {
            return tableNames[columnIndex];
        }

        /**
         * Will get the Claa of the given column index
         *
         * @param columnIndex the column index
         * @return class of the column
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        /**
         * @return row count
         */
        @Override
        public int getRowCount() {
            return tableValues.length;
        }

        /**
         * @return column count
         */
        @Override
        public int getColumnCount() {
            return tableNames.length;
        }

        /**
         * @param rowIndex    the row index of the cell
         * @param columnIndex the column index of the cell
         * @return if the cell is editable or not
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !getColumnClass(columnIndex).equals(String.class);
        }

        /**
         * Will submit the values to the GitHub upon change, and if its done successfully
         * then will change in the table values
         *
         * @param aValue      the new value
         * @param rowIndex    the row index of the cell that is edited
         * @param columnIndex the column index of the cell that is edited
         */
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (table.editReadMe(tableValues[rowIndex][0], aValue)) {
                tableValues[rowIndex][columnIndex] = aValue;
            }
        }

        /**
         * Will retrieve the current cell value
         *
         * @param rowIndex    the row index of the cell
         * @param columnIndex the column index of the cell
         * @return the value in the cell
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return tableValues[rowIndex][columnIndex];
        }
    }

}
