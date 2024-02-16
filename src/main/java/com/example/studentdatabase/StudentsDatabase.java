package com.example.studentdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StudentsDatabase implements TableInterface, StudentsDatabaseInterface {
    String url, username, password;
    Connection connection;

    // Constructor
    public StudentsDatabase(String url, String username, String password) {
        // Constructor: connect to the database
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(String url, String username, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("\nConnection to the database server successful!");
        } catch (SQLException e) {
            System.out.println(e);
        }

        return connection;
    }

    // Nested class Schedule
    class Schedule {
        String filename, nameTable;
        String ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableSchedule;
        String ddlPopulateTable = TableInterface.loadDataInFileTable(filename, nameTable);
        String ddlUpDateCourseInstructor;
        String ddlUpDateInstructor;

        // Constructor
        Schedule(String filename, String nameTable) throws SQLException {
            this.filename = filename;
            this.nameTable = nameTable;

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Schedule created successfully");

            // Populate Table
            String ddlPopulateTable = TableInterface.loadDataInFileTable(filename, nameTable);
            TableInterface.setLocalInFileLoading(connection);
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Schedule populated successfully");

            ResultSet RS = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Schedule executed successfully");
        }

        // Update course instructor
        public void updateCourseInstructor(String courseID, String sectionNumber, String nameInstructor) throws SQLException {
            this.ddlUpDateCourseInstructor = StudentsDatabaseInterface.ddlUpDateCourseInstructor(courseID, sectionNumber, nameInstructor);
            TableInterface.updateField(connection, ddlUpDateCourseInstructor);
        }

        // Update instructor
        public void updateInstructor(String nameInstructor, String nameNewInstructor) throws SQLException {
            this.ddlUpDateInstructor = StudentsDatabaseInterface.ddlUpDateInstructor(nameInstructor, nameNewInstructor);
            TableInterface.updateField(connection, ddlUpDateInstructor);
        }
    }

    // Nested class Courses
    class Courses {
        String ddlCreateTable, ddlPopulateTable;
        String nameToTable, nameFromTable;

        // Constructor: Build and populate Table Courses in DBMS
        Courses(String nameToTable, String nameFromTable) throws SQLException {
            this.nameToTable = nameToTable;
            this.nameFromTable = nameFromTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableCourses;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableCourses(nameToTable, nameFromTable);

            // Create Table
            TableInterface.dropTable(connection, nameToTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Courses created successfully");

            // Populate Table
            TableInterface.insertFromSelect(connection, ddlPopulateTable);
            System.out.println("\nTable Courses populated successfully");

            ResultSet rs = TableInterface.getTable(connection, nameToTable);
            System.out.println("\nQuery on Courses executed successfully");
        }
    }

    public class Students {
        String nameTable;
        String ddlCreateTable, ddlPopulateTable;

        // Constructor
        public Students(String nameTable) throws SQLException {
            this.nameTable = nameTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableStudents;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableStudents;
            System.out.println(ddlPopulateTable); // I assume you wanted to print the ddlPopulateTable here.

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Students created successfully");

            // Populate Table
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Students populated successfully");

            ResultSet rs = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Students executed successfully");
        }
    }

    public class Classes {
        String nameTable;
        String ddlCreateTable, ddlPopulateTable;

        // Constructor
        public Classes(String nameTable) throws SQLException {
            this.nameTable = nameTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableClasses;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableClasses;
            System.out.println(ddlPopulateTable); // I assume you wanted to print the ddlPopulateTable here.

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Classes created successfully");

            // Populate Table
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Classes populated successfully");

            ResultSet rs = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Classes executed successfully");
        }
    }

    public class AggregateGrades {
        String nameToTable, nameFromTable;
        String ddlCreateTable, ddlPopulateTable;

        // Constructor
        public AggregateGrades(String nameToTable, String nameFromTable) throws SQLException {
            this.nameToTable = nameToTable;
            this.nameFromTable = nameFromTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableAggregateGrades;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableAggregateGrades(nameToTable, nameFromTable);

            // Create Table
            TableInterface.dropTable(connection, nameToTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable AggregateGrades created successfully");

            // Populate Table
            TableInterface.insertFromSelect(connection, ddlPopulateTable);
            System.out.println("\nTable AggregateGrades populated successfully");

            ResultSet rs = TableInterface.getTable(connection, nameToTable);
            System.out.println("\nQuery on AggregateGrades executed successfully");
        }

        // Get map of aggregate grades
        public Map<Character, Integer> getAggregateGrades(String nameTable) {
            Map<Character, Integer> mapAggregateGrades = new HashMap<>();
            try {
                ResultSet rs = TableInterface.getTable(connection, nameTable);
                while (rs.next()) {
                    mapAggregateGrades.put(rs.getString("grade").charAt(0), rs.getInt("numberStudents"));
                    // Alternatively, you can print the values here if needed.
                    // System.out.println(rs.getString("grade").charAt(0) + "\t" + rs.getInt("numberStudents"));
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return mapAggregateGrades;
        }
    }
}
