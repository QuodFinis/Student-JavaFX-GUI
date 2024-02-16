package com.example.studentdatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

interface TableInterface {

    // Static methods
    // Drop Schema
    static void dropSchema(Connection connection, String nameSchema) throws SQLException {
        PreparedStatement psDropTable = connection.prepareStatement("DROP SCHEMA IF EXISTS " + nameSchema);
        try {
            psDropTable.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Create Schema
    static void createSchema(Connection connection, String nameSchema) throws SQLException {
        PreparedStatement psCreateSchema = connection.prepareStatement("CREATE SCHEMA " + nameSchema);
        try {
            psCreateSchema.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Drop Table
    static void dropTable(Connection connection, String nameTable) throws SQLException {
        PreparedStatement psDropTable = connection.prepareStatement("DROP TABLE IF EXISTS " + nameTable);
        try {
            psDropTable.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Create a Table
    static void createTable(Connection connection, String ddlCreateTable) throws SQLException {
        PreparedStatement psCreateTable = connection.prepareStatement(ddlCreateTable);
        try {
            psCreateTable.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Set local in-file parameter for local data loading: MySQL server
    static void setLocalInFileLoading(Connection connection) throws SQLException {
        // Set variable for local file loading
        PreparedStatement psSetLocalInFileLoading = connection.prepareStatement("SET GLOBAL local_infile = 1");
        try {
            psSetLocalInFileLoading.executeUpdate();
            System.out.println("\nGlobal local-infile set successfully");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Populate Table using local data file: MySQL server
    static String loadDataInFileTable(String nameFile, String nameTable) {
        return "LOAD DATA LOCAL INFILE '" + nameFile + "' INTO TABLE " + nameTable +
                " COLUMNS TERMINATED BY '\t'" +
                " LINES TERMINATED BY '\n'" +
                " IGNORE 1 LINES";
    }

    // Populate Table
    static void populateTable(Connection connection, String ddlPopulateTable) throws SQLException {
        PreparedStatement psPopulateTable = connection.prepareStatement(ddlPopulateTable);
        try {
            psPopulateTable.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Insert into Table from SELECT.
    static void insertFromSelect(Connection connection, String nameToTable, String nameFromTable) throws SQLException {
        PreparedStatement psInsertFromSelect = connection.prepareStatement("INSERT INTO " + nameToTable + " SELECT * FROM " + nameFromTable);
        try {
            psInsertFromSelect.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Insert into Table from SELECT given specific fields and conditions
    static void insertFromSelect(Connection connection, String ddlInsertFromSelect) throws SQLException {
        PreparedStatement psInsertFromSelect = connection.prepareStatement(ddlInsertFromSelect);
        try {
            psInsertFromSelect.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Insert Record
    static void insertRecord(Connection connection, String ddlInsertRecord) throws SQLException {
        PreparedStatement psInsertRecord = connection.prepareStatement(ddlInsertRecord);
        try {
            psInsertRecord.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Update a field in records
    static void updateField(Connection connection, String ddlUpdateField) throws SQLException {
        PreparedStatement psUpdateField = connection.prepareStatement(ddlUpdateField);
        try {
            psUpdateField.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Delete a record from Table
    static void deleteRecord(Connection connection, String ddlDeleteRecord) throws SQLException {
        PreparedStatement psDeleteRecord = connection.prepareStatement(ddlDeleteRecord);
        try {
            psDeleteRecord.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Get a Table
    static ResultSet getTable(Connection connection, String nameTable) throws SQLException {
        ResultSet rs = null;
        PreparedStatement psGetTable = connection.prepareStatement("SELECT * FROM " + nameTable);
        try {
            rs = psGetTable.executeQuery();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    Connection getConnection(String url, String username, String password);
}
