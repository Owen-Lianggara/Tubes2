import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAccess {
    private String db_url = "jdbc:sqlserver://LAPTOP-H2N2C3LD;databaseName=sRusunDB;user=Administrator;password=owen1234;encrypt=true;trustServerCertificate=true";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static DBAccess instance;

    public static DBAccess getInstance() {
        if (instance == null) {
            instance = new DBAccess();
        }
        return instance;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(db_url);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Connection connection = DriverManager.getConnection(db_url);
                Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public static void printTable(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Initialize column widths with column name lengths
        int[] columnWidths = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = metaData.getColumnLabel(i + 1).length();
        }

        // Store all rows temporarily
        List<String[]> rowData = new ArrayList<>();

        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                String value = rs.getString(i + 1);
                value = (value == null) ? "NULL" : value;
                row[i] = value;

                // Update column width if this value is longer
                columnWidths[i] = Math.max(columnWidths[i], value.length());
            }
            rowData.add(row);
        }

        // Print column headers
        for (int i = 0; i < columnCount; i++) {
            System.out.printf("%-" + (columnWidths[i] + 2) + "s", metaData.getColumnLabel(i + 1));
        }
        System.out.println();

        // Print separator
        for (int i = 0; i < columnCount; i++) {
            System.out.print("-".repeat(columnWidths[i] + 2));
        }
        System.out.println();

        // Print data rows
        for (String[] row : rowData) {
            for (int i = 0; i < columnCount; i++) {
                System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            System.out.println();
        }
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Gagal clear console: " + ex.getMessage());
        }
    }
    
    public static void Bold(String message){
        System.out.println("\033[1m" + message + "\033[0m");
    }
}
