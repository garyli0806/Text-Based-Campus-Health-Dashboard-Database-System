
import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/*=============================================================================
 |   Assignment:  Program 4: Database Design and Implementation
 |       Author:  Gary Li, Liam Wilson, Fontaine Coutino, Raphaelle Guinanao
 |       Grader:  CSC 460 TA
 |
 |       Course:  CSC 460
 |   Instructor:  L. McCann
 |     Due Date:  May 2, 2022 | 12:30pm
 |
 |  Description: The following program is an implementation of a text-based
 |               campus health dashboard, allowing users to add/edit/delete
 |               patients, employees, appointments, and add availability for
 |               employees. If invalid information is entered, you will be
 |               prompted to either re-enter that field or re-enter the record
 |               entirely. The program also includes several methods allowing
 |               you to query the database for information.
 |
 | Requirements: Requires that you have a valid account established with oracle
 |               and that executing oracle exists in your classpath
 |
 |     Language:  Java 16
 | Ex. Packages: java.io, java.sql, java.util, java.text.DateFOrmat, java.text
 |               SimpleDateFormat, java.text.ParseException
 |
 | Deficiencies: N/A
 *===========================================================================*/

public class Prog4
{
    /**---------------------------------------------------------------------
    |  Method getTable
    |
    |  Purpose: Given a table name, this retreives the appropriate table name
    |           and prefix
    |
    |  Pre-condition: The returned tablename must be correct
    |
    |  Post-condition: None
    |
    |  Parameters:
    |	String table -- Name of table
    |
    |  Returns: Name of table accessibly by oracle (String)
    *-------------------------------------------------------------------*/
    public static String getTable(String table) {
        if (table.equals("patient")) return "wilsonliam.patients";
        if (table.equals("availability")) return "wilsonliam.availability";
        if (table.equals("appointment")) return "wilsonliam.appointments";
        if (table.equals("service")) return "rtguinanao.service";
        if (table.equals("transactions")) return "garyli.transactions";
        if (table.equals("employee")) return "garyli.medical_staff";
        if (table.equals("insurance")) return "fcoutino.insurance";
        return "INVALID";
    }

    /**---------------------------------------------------------------------
    |  Method connectSQL
    |
    |  Purpose: Given user credentials, connect to SQL
    |
    |  Pre-condition: The classpath must be created to ensure access to JDBC
    |                 connection
    |
    |  Post-condition: DBConnection is opened. Must be closed in the end.
    |
    |  Parameters:
    |	String[] args -- User creds
    |
    |  Returns: Connection to oracle database (Connection)
    *-------------------------------------------------------------------*/
    public static Connection connectSQL(String[] args) {
        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        String username = null,    // Oracle DBMS username
               password = null;    // Oracle DBMS password

        if (args.length == 2) {    // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n"
                             + "    where <username> is your Oracle DBMS"
                             + " username,\n    and <password> is your Oracle"
                             + " password (not your system password).\n");
            System.exit(-1);
        }

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                + "Error loading Oracle JDBC driver.  \n"
                + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);

        }

        // make and return a database connection to the user's
        // Oracle database
        Connection dbconn = null;
        try {
            dbconn = DriverManager.getConnection
                (oracleURL,username,password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        return dbconn;
    }

    /**---------------------------------------------------------------------
    |  Method runQuery
    |
    |  Purpose: Given query and connection, runs that particular query
    |
    |  Pre-condition: The returned tablename must be correct
    |
    |  Post-condition: None
    |
    |  Parameters:
    |	Connection dbconn -- Oracle db connection
    |   String query -- Particular query we are running
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void runQuery(Connection dbconn, String query) {
        // Send the query to the DBMS, and get and display the results
        Statement stmt = null;
        ResultSet answer = null;

        try {
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);

            if (answer != null) {

                System.out.println("\nThe results of the query [" + query + "] are:\n");

                // Get the data about the query result to learn
                // the attribute names and use them as column headers
                ResultSetMetaData answermetadata = answer.getMetaData();

                for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                    System.out.print(answermetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                // Use next() to advance cursor through the result
                // tuples and print their attribute values
                while (answer.next()) {
                    System.out.println(answer.getString("sno") + "\t"
                        + answer.getInt("status"));
                }
            }
            System.out.println();

            stmt.close();

        } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

        }
    }

    /**---------------------------------------------------------------------
    |  Method promptUser
    |
    |  Purpose: Prompts user with the options
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |	Connection dbconn -- To create a connection with the database
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void promptUser(Connection dbconn) {
        System.out.println("===== VIEW =====");
        System.out.println("1 - Patient");
        System.out.println("2 - Employee");
        System.out.println("3 - Appointment");
        System.out.println("4 - Queries");
        System.out.println("0 - QUIT");
        System.out.println("----------");

        while (true) {
            Scanner kb = new Scanner(System.in);
            System.out.print("Choose an option: ");

            int userResponse = -1;
            try {
                userResponse = kb.nextInt();
            } catch (InputMismatchException e) {};

            switch(userResponse) {
                case 0:
                    // QUIT
                    System.exit(0);
                case 1:
                    handleView("patient", dbconn);
                    break;
                case 2:
                    handleView("employee", dbconn);
                    break;
                case 3:
                    handleView("appointment", dbconn);
                    break;
                case 4:
                    handleQueries(dbconn);
                    break;
                default:
                    System.out.println("> Invalid option.");
                    break;
            }

            System.out.println();
        }
    }

    /**---------------------------------------------------------------------
    |  Method handleView
    |
    |  Purpose: Prompts users with options to do under a particular view
    |
    |  Pre-condition: Must be given one of the following views: patient,
    |                 employee, or appointment
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String view -- Table we should view
    |	Connection dbconn -- To create a connection with the database
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void handleView(String view, Connection dbconn) {
        boolean isValid = false;
        do {
            Scanner kb = new Scanner(System.in);
            System.out.println("=====" + view.toUpperCase() + "=====");
            viewRecords(dbconn, view);
            System.out.println("----------");
            System.out.println("1 - Add record");
            System.out.println("2 - Update record");
            System.out.println("3 - Delete record");
            System.out.println("4 - View records");
            System.out.println("0 - BACK");
            System.out.print("Choose an option: ");

            int userResponse = -1;
            try {
                userResponse = kb.nextInt();
            } catch (InputMismatchException e) {};

            switch(userResponse) {
                case 0:
                    isValid = true;
                    promptUser(dbconn);
                    break;
                case 1:
                    addRecord(dbconn, view);
                    break;
                case 2:
                    updateRecord(dbconn, view);
                    break;
                case 3:
                    deleteRecord(dbconn, view);
                    break;
                case 4:
                    viewRecords(dbconn, view);
                    break;
                default:
                    System.out.println("> Invalid option.");
                    break;
            }

            System.out.println();
        } while (!isValid);
    }

    /**---------------------------------------------------------------------
    |  Method viewRecords
    |
    |  Purpose: View records for a particular table
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String view -- Table we should view
    |	Connection dbconn -- To create a connection with the database
    |
    |  Returns: Name of table accessibly by oracle (String)
    *-------------------------------------------------------------------*/
    private static void viewRecords(Connection dbconn, String view) {
        String query = "";
        switch (view) {
            case "patient":
                query = "SELECT * FROM " + getTable("patient");
                break;

            case "employee":
                query = "select medical_staff_id, staff_name, service_id from " + getTable("employee");
                break;

            case "appointment":
                query = "select appointmentID,patientID,appointmentTime from " + getTable("appointment");
                break;
            case "service":
                query = "select service_id,service_dept,service_desc, covid_desc from " + getTable("service");
                break;
        }

        // perform query
        Statement stmt = null;
        ResultSet answer = null;
        try {
            Statement alter = dbconn.createStatement();
			      alter.executeQuery("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'");
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);
            if (answer != null) {

                // Get the data about the query result to learn
                // the attribute names and use them as column headers
                ResultSetMetaData answermetadata = answer.getMetaData();


                // Use next() to advance cursor through the result
                // tuples and print their attribute values
                switch (view) {
                    case "patient":
                        System.out.println(String.format("%-8s%-30s%-12s%-12s%-12s%-4s", "ID", "fullName", "isStudent ", "isEmployee ", "hasInsurance ", "age"));
                        while (answer.next()) {
                            System.out.println(String.format("%-8s%-30s%-12s%-12s%-12s%-4s", answer.getInt("patientID"), answer.getString("fullName"), answer.getInt("isStudent"), answer.getInt("isEmployee"), answer.getInt("hasInsurance"), answer.getInt("age")));
                        }
                        break;
                    case "employee":
                        System.out.println(String.format("%-8s%-30s%-4s", "ID", "Name", "ServiceID"));
                        while (answer.next()) {
                            System.out.println(String.format("%-8s%-30s%-4s", answer.getInt("medical_staff_id"), answer.getString("staff_name"), answer.getInt("service_id")));
                        }
                        break;
                    case "appointment":
                        System.out.println(String.format("%-8s%-8s%-10s", "AppID", "PatID", "App Time"));
                        while (answer.next()) {
                            System.out.println(String.format("%-8d%-8d%-10s", (answer.getInt("appointmentID")),
                            answer.getInt("patientID"), answer.getDate("appointmentTime")));
                        }
                        break;
                    case "service":
                        System.out.println(String.format("%-8s%-30s%-10s%-4s", "ID", "Dept", "Desc", "Covid_Desc"));
                        while (answer.next()) {
                            System.out.println(String.format("%-8s%-30s%-10s%-4s", answer.getInt("service_id"), answer.getString("service_dept"), answer.getString("service_desc"), answer.getString("covid_desc")));
                        }
                        break;
                }

            }
            System.out.println();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  ");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.err.println("\tQuery: " + query);
            System.exit(-1);
        }
    }

    /**---------------------------------------------------------------------
    |  Method findNextID
    |
    |  Purpose: Given a table and its primary key, this determines what the
    |           next appropriate primary key should be
    |
    |  Pre-condition: The provided credentials must fall in-line with our schema
    |
    |  Post-condition: None
    |
    |  Parameters:
    |	String view -- Name of table
    |   String pk -- Name of primary key
    |   Connection dbconn -- Connection with database
    |
    |  Returns: Next id to insert(int)
    *-------------------------------------------------------------------*/
    public static int findNextID(String view, String pk, Connection dbconn) {
        String query = String.format("SELECT MAX(%s) + 1 AS id FROM %s", pk, getTable(view));
        Statement stmt = null;
        ResultSet answer = null;
        int id = -1;
        try {
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);
            while (answer.next()) {
                id = answer.getInt("id");
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not insert into table.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        return id;
    }

    /**---------------------------------------------------------------------
    |  Method validateInput
    |
    |  Purpose: Validates the given input based on type and re-prompts the user
    |           if invalid
    |
    |  Pre-condition: type must be datetime, str, bool, or int
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String question -- Question to ask user
    |	String type -- Type of data being entered
    |
    |  Returns: Valid response (String)
    *-------------------------------------------------------------------*/
    public static String validateInput(String question, String type) {
        boolean isValid = false;
        String response = "";
        Scanner kb = new Scanner(System.in);

        do {
            System.out.print(question);
            switch (type) {
                case "bool":
                    char responseChar = '\0';
                    try {
                        responseChar = Character.toLowerCase(kb.next().charAt(0));
                    } catch (InputMismatchException e) {};
                    if (responseChar == 'y' || responseChar == 'n') {
                        isValid = true;
                        response = (responseChar == 'y') ? "1"  : "0";
                    }
                    break;
                case "int":
                    int responseInt = -1;
                    try {
                        responseInt = kb.nextInt();
                    } catch (InputMismatchException e) {};
                    if (responseInt > 0) {
                        isValid = true;
                        response = Integer.toString(responseInt);
                    }
                    break;
                case "datetime":
                    String responseDate = "";
                    try {
                        responseDate = kb.nextLine().strip();
                        java.util.Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(responseDate);
                        isValid = true;
                        response = "TO_DATE(\'" + responseDate + "\', \'YYYY-MM-DD HH24:MI:SS\')";
                    } catch (IllegalArgumentException e) {}
                    catch (ParseException e) {};
                    break;
                case "str":
                    response = "\'" + kb.nextLine().strip() + "\'";
                    isValid = true;
                    break;
            }
            if (!isValid) System.out.println("> Invalid response");
        } while (!isValid);

        return response;
    }

    /**---------------------------------------------------------------------
    |  Method validateFK
    |
    |  Purpose: Validates the foreign key based on the table. Ensuring that
    |           it's a valid FK
    |
    |  Pre-condition: View and fk must be appropriate based off of schema
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String question -- Question to ask user
    |	String view -- Table we are checking
    |   String fk -- Column name of foreign key
    |   Connection dbconn -- How we connect to our database
    |
    |  Returns: Valid response (String)
    *-------------------------------------------------------------------*/
    public static String validateFK(String question, String view, String fk, Connection dbconn) {
        boolean isValid = false;
        String response = "";

        do {
            System.out.print(question);
            Statement stmt = null;
            ResultSet answer = null;
            String query = "";
            try {
                Scanner kb = new Scanner(System.in);
                query = String.format("SELECT * FROM %s WHERE %s = %s", getTable(view), fk, kb.nextInt());

                stmt = dbconn.createStatement();
                answer = stmt.executeQuery(query);

                while (answer.next()) {
                    response = Integer.toString(answer.getInt(fk));
                    isValid = true;
                }

                stmt.close();
            } catch (SQLException e) {
                System.err.println("*** SQLException:  "
                    + "Could not attain information for " + fk);
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.err.println("\tQuery: " + query);
                System.exit(-1);
            } catch (InputMismatchException e) {};

            if (!isValid) System.out.println("> Invalid response");
        } while (!isValid);

        return response;
    }

    /**---------------------------------------------------------------------
    |  Method isQueryEmpty
    |
    |  Purpose: Determines whether or not the provided query produces results
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String query -- Query to check
    |	Connection dbconn -- Database connection
    |
    |  Returns: whether or not the query produces empty results (boolean)
    *-------------------------------------------------------------------*/
    public static boolean isQueryEmpty(String query, Connection dbconn) {
        Statement stmt = null;
        ResultSet answer = null;
        try {
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);

            while (answer.next()) {
                return false;
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  ");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.err.println("\tQuery: " + query);
            System.exit(-1);
        } catch (InputMismatchException e) {};

        return true;
    }

    /**---------------------------------------------------------------------
    |  Method validateAppointment
    |
    |  Purpose: Validates the appointment, checking that 1) there are no
    |           overlapping appointments 2) there is an available staff member
    |           3) if it's a walk-in, then it must not be severe 4) if 3rd dose
    |           is being added for those 50 >=, a 4th dose must be
    |           administered. If the appointment is invalid, a reason why is
    |           printed and false is returned.
    |
    |  Pre-condition: type must be datetime, str, bool, or int
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   String question -- Question to ask user
    |	String type -- Type of data being entered
    |
    |  Returns: Name of table accessibly by oracle (String)
    *-------------------------------------------------------------------*/
    public static boolean validateAppointment(String patientID, String serviceID, String time, String severity, String isWalkIn, String isCancelled, Connection dbconn) {
        // First, ensure no overlapping appointments within the hour
        String overlapQuery = String.format("SELECT * FROM %s " +
            "WHERE appointmentTime BETWEEN %s - (1 / 24) AND %s + (1 / 24)", getTable("appointment"), time, time);
        if (!isQueryEmpty(overlapQuery, dbconn)) {
            System.out.println("> Overlapping appointment exists");
            return false;
        }

        // Then, ensure there's an available staff member for that service department
        String availableQuery = String.format("SELECT * FROM %s A " +
            "JOIN %s B ON A.medicalStaffId = B.medical_staff_id " +
            "JOIN %s C on B.service_id = C.service_id " +
            "WHERE (%s BETWEEN A.checkin AND A.checkout) AND " +
            "((SELECT service_dept FROM %s WHERE service_id = %s) = c.service_dept)",
            getTable("availability"), getTable("employee"), getTable("service"), time, getTable("service"), serviceID);
        if (isQueryEmpty(availableQuery, dbconn)) {
            System.out.println("> No available staff during this time");
            return false;
        }

        // If walkIn and not severe, then must ask to reschedule
        if (isWalkIn.equals("1") && severity.equals("0")) {
            System.out.println("> You can not request for a walk-in if your case is not severe. Please reschedule.");
            return false;
        }

        // If for immunization, double-check if patient over 50
        // First check if patient is over 50 and if they're scheduling a 3rd dose
        String patientExistsQuery = String.format("SELECT * FROM %s " +
            "WHERE age >= 50 AND patientID = %s", getTable("patient"), patientID);
        String scheduledFourth = String.format("SELECT * FROM %s " +
            "WHERE patientID = %s AND serviceID = 4",
            getTable("appointment"), patientID);
        String checkThirdBeforeFourth = String.format("SELECT * FROM %s " +
            "WHERE patientID = %s AND serviceID = 4 AND appointmentTime > %s",
            getTable("appointment"), patientID, time);

        if (serviceID.equals("3") && !isQueryEmpty(patientExistsQuery, dbconn)) {
            // Patient does not have a 4th dose scheduled
            if (isQueryEmpty(scheduledFourth, dbconn)) {
                System.out.println("> You must schedule a 4th dose before scheduling a 3rd because you are >= 50 years old.");
                return false;
            // Patient has a 4th dose, but scheduled their 3rd after the 4th
            } else if (isQueryEmpty(checkThirdBeforeFourth, dbconn)) {
                System.out.println("> You must schedule a 3rd dose before your 4th");
                return false;
            };
        }

        return true;
    }

    /**---------------------------------------------------------------------
    |  Method addRecord
    |
    |  Purpose: Adds a given record to a particular table, prompting the user
    |           until valid values are provided
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   Connection dbconn -- Connection do db
    |	String view -- Table we are adding to
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void addRecord(Connection dbconn, String view) {
        String query = "INSERT INTO " + getTable(view) + " VALUES (";
        Statement stmt = null;
        ResultSet answer = null;

        switch (view) {
            case "patient":
                query += findNextID(view, "patientID", dbconn) + ", ";
                query += validateInput("What is this patient's name?", "str") + ", ";
                query += validateInput("Is this patient a student? (Y/N)", "bool") + ", ";
                query += validateInput("Is this patient an employee? (Y/N)", "bool") + ", ";
                query += validateInput("Does this patient have insurance? (Y/N)", "bool") + ", ";
                query += validateInput("What is this patient's age? (Y/N)", "int");
                break;
            case "employee":
                query += findNextID(view, "medical_staff_id", dbconn) + ", ";
                // Listing all services
                viewRecords(dbconn, "service");
                query += validateInput("What is this employee's name?", "str") + ", ";
                query += validateFK("For what service does this employee work for?", "service", "service_id", dbconn);
                break;
            case "appointment":
                query += findNextID(view, "appointmentID", dbconn) + ", ";

                // Listing all patients
                viewRecords(dbconn, "patient");
                String patientID = validateFK("Which patient are you? (Provide patientID)", "patient", "PatientID", dbconn);
                query += patientID + ", ";

                // Listing all services
                viewRecords(dbconn, "service");
                String serviceID = validateFK("For what service are you coming in for? (Provide serviceID)", "service", "service_id", dbconn);
                query += serviceID + ", ";

                String time = "TO_DATE(\'" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date(System.currentTimeMillis())) + "\', \'YYYY-MM-DD HH24:MI:SS\')";
                String isWalkIn = validateInput("Is this appointment a walk-in? (Y/N)", "bool");
                if (isWalkIn.equals("0")) {
                    // Scheduled appointment, must prompt user
                    time = validateInput("What time is your appointment? (yyyy-MM-dd HH:mm:ss)", "datetime");
                }
                String severity = validateInput("Is your case severe? (Y/N)", "bool");
                query += time + ", ";
                query += severity + ", ";
                query += isWalkIn + ", ";
                if (!validateAppointment(patientID, serviceID, time, severity, isWalkIn, "0", dbconn)) {
                    System.out.println("> Information entered for this appointment is invalid. Please attempt adding another record.");
                    return;
                }

                // Assume isCancelled is false when first entering data
                query += "0";
                break;
            case "availability":
                // Assume last entered employee
                int staffID = findNextID("employee", "medical_staff_id", dbconn) - 1;
                query += Integer.toString(staffID) + ", ";
                String startTime = validateInput("What time is your check-in? (yyyy-mm-dd hh:mm:ss)", "datetime");
                query += startTime + ", ";

                // Add 10 hours for check-out date
                try {
                    String parseTime = startTime.split(",")[0];
                    parseTime = parseTime.substring(9, parseTime.length() - 1);
                    java.util.Date start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parseTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(start);
                    calendar.add(Calendar.HOUR_OF_DAY, 10);
                    java.util.Date end = calendar.getTime();
                    String endTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(end);

                    query += "TO_DATE(\'" + endTime + "\', \'YYYY-MM-DD HH24:MI:SS\')";
                } catch (ParseException e) {
                    System.out.println("Error with calculating end time for: " + startTime);
                };
                break;
        }
        query += ")";

        try {
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);
            System.out.println("*** RECORD HAS BEEN INSERTED ***");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not insert into table.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.err.println("\tQuery: " + query);
            System.exit(-1);
        }

        // If we just entered an employee, enter an availability record for the employee
        if (view.equals("employee")) {
            char isPromptAvail = 'y';
            Scanner kb = new Scanner(System.in);
            while (isPromptAvail == 'y') {
                System.out.print("Do you wish to enter an(other) availability record for this employee? (Y/N)");
                try {
                    isPromptAvail = Character.toLowerCase(kb.next().charAt(0));
                } catch (InputMismatchException e) {};
                System.out.println();

                if (isPromptAvail != 'y') break;
                addRecord(dbconn, "availability");
            }
        }
    }

    /**---------------------------------------------------------------------
    |  Method updateRecord
    |
    |  Purpose: UpdatesAdds a given record to a particular table, prompting the
    |           user until valid values are provided
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   Connection dbconn -- Connection do db
    |	String view -- Table we are adding to
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void updateRecord(Connection dbconn, String view) {
        String query = "UPDATE " + getTable(view) + " SET ";
        Statement stmt = null;
        ResultSet answer = null;

        switch (view) {
            case "patient":
                // PatientID
                String patient_id = validateFK("Which patient's information would you like to edit? (Provide PatientID)", "patient", "patientID", dbconn);
                // Full name
                query += String.format("fullName = %s, ", validateInput("What is this patient's name?", "str"));
                // isStudent
                query += String.format("isStudent = %s, ", validateInput("Is this patient a student? (Y/N)", "bool"));
                // isEmployee
                query += String.format("isEmployee = %s, ", validateInput("Is this patient an employee? (Y/N)", "bool"));
                // hasInsurance
                query += String.format("hasInsurance = %s, ", validateInput("Does this patient have insurance? (Y/N)", "bool"));
                // age
                query += String.format("age = %s", validateInput("What is this patient's age?", "int"));
                query += " WHERE patientID = " + patient_id;
                break;
            case "employee":
                // Employee id
                String staff_id = validateFK("Which employee's information would you like to edit? (Provide employee id)", "employee", "medical_staff_id", dbconn);
                // Employee name
                query += String.format("staff_name = %s, ",
                    validateInput("What is this employee's name?", "str"));
                // ServiceId
                viewRecords(dbconn, "service");
                query += String.format("service_id = %s ",
                    validateFK("For what service are does this employee work for?", "service", "service_id", dbconn));
                query += " WHERE medical_staff_id = " + staff_id;
                break;
            case "appointment":
                // AppointmentID
                String appointment_id = validateFK("Which appointment's information would you like to edit? (Provide appointment id)", "appointment", "appointmentID", dbconn);
                // PatientID
                viewRecords(dbconn, "patient");
                String patientID = validateFK("Which patient are you? (Provide patientID)", "patient", "PatientID", dbconn);
                // ServiceID
                viewRecords(dbconn, "service");
                String serviceID = validateFK("For what service are you coming in for?", "service", "service_id", dbconn);
                // Time
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date(System.currentTimeMillis()));
                // Walk-in
                String isWalkIn = validateInput("Is this appointment a walk-in? (Y/N)", "bool");
                if (isWalkIn.equals("0")) {
                    // Scheduled appointment, must prompt user
                    time = validateInput("What time is your appointment? (yyyy-MM-dd HH:mm:ss)", "datetime");
                }
                // Severity
                String severity = validateInput("Is your case severe? (Y/N)", "bool");
                // isCancelled
                String isCancelled = validateInput("Is this appointment cancelled? (Y/N)", "bool");

                if (!validateAppointment(patientID, serviceID, time, severity, isWalkIn, isCancelled, dbconn)) {
                    System.out.println("> Information entered for this appointment is invalid. Please attempt updating another record.");
                    return;
                }
                query += String.format("patientID = %s, ", patientID);
                query += String.format("serviceID = %s, ", serviceID);
                query += String.format("appointmentTime = %s, ", time);
                query += String.format("severity = %s, ", severity);
                query += String.format("isWalkIn = %s, ", isWalkIn);
                query += String.format("isCancelled = %s ", isCancelled);
                query += " WHERE appointmentID = " + appointment_id;
                break;
        }

        try {
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);
            System.out.println("*** RECORD HAS BEEN UPDATED ***");

            // Shut down the connection to the DBMS.
            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not update record.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.err.println("\tQuery: " + query);
            System.exit(-1);
        }
    }

    /**---------------------------------------------------------------------
    |  Method deleteRecord
    |
    |  Purpose: delete a  record given the id
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   Connection dbconn -- Connection do db
    |	  String view -- Table we are deleting from
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void deleteRecord(Connection dbconn, String view) {
        // what is being deleted
        System.out.println("Enter ID to delete: ");
        Scanner sc = new Scanner(System.in);
        String userResponse = "";
        try { userResponse = sc.next();
        } catch (InputMismatchException e) {};

        // delete queries for patient table and all tables that may mention PK
        ArrayList<String> queries = new ArrayList<String>();
        switch (view) {
            case "patient":
                queries.add( "DELETE FROM " + getTable(view) + " WHERE patientID=" + userResponse + "");
                queries.add( "DELETE FROM " + getTable("transactions") + " WHERE patient_id=" + userResponse + "");
                queries.add( "DELETE FROM " + getTable("appointment")  + " WHERE patientID=" + userResponse);
                break;

            case "employee":
                queries.add( "DELETE FROM " + getTable(view) + " WHERE medical_staff_id=" + userResponse);
                queries.add( "DELETE FROM " + getTable("availability") + " WHERE medicalStaffId=" + userResponse);
                break;

            case "appointment":
                queries.add( "DELETE FROM " + getTable(view) + " WHERE appointmentID=" + userResponse);
                queries.add( "DELETE FROM " + getTable("transactions") + " WHERE appt_id=" + userResponse);
                break;
        }

        // execute delete queries
        Statement stmt = null;
        ResultSet answer = null;
        for (String query : queries){
            try {
                stmt = dbconn.createStatement();
                answer = stmt.executeQuery(query);

                stmt.close();
            } catch (SQLException e) {
                System.err.println("*** SQLException:  "
                    + "Could not update record.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
            }
        }
        System.out.println("*** RECORD HAS BEEN DELETED ***");
    }

    /**---------------------------------------------------------------------
    |  Method addRecord
    |
    |  Purpose: Prompts the user for which query
    |
    |  Pre-condition: None
    |
    |  Post-condition: None
    |
    |  Parameters:
    |   Connection dbconn -- Connection to db
    |
    |  Returns: None
    *-------------------------------------------------------------------*/
    public static void handleQueries(Connection dbconn) {

        // prompt user
        System.out.println("==========");
        System.out.println("Select query:");
        System.out.println("1 - Print a list of patients who have their 2nd, 3rd or 4th doses of the COVID-19 vaccine scheduled by a certain date.");
        System.out.println("2 - Given a certain date, output which patients had a non-walk-in appointment. Sort in order by appoint- ment time and group by type of service.");
        System.out.println("3 - Print the schedule of staff given a certain date (input by the user). A schedule contains the list of staff members working that day (including those who were working that day as usual and those who were working to handle an appointment) and a staff member’s working hours (start and stop times).");
        System.out.println("4 - Print the vaccine statistics of the two categories of patients (student, employees). The statistics include the count of patients that have completed all 4 doses of a vaccine series, the count of patients that have received three doses, but not the 4th, the count of patients that have received two doses but not the 3rd, and the count of patients who have only received the first dose.");
        System.out.println("5 - Given a date, output what transactions occured in the Immunizations department on that particular day.");
        System.out.println("0 - QUIT");
        System.out.println("Choose an option: ");

        Scanner sc = new Scanner(System.in);
        int userResponse = Integer.parseInt(sc.nextLine());

        // get query
        switch(userResponse) {
            case 0:
                System.exit(0);
                return;
            case 1:
            	query1(dbconn);
                break;
            case 2:
                query2(dbconn);
                break;
            case 3:
                query3(dbconn);
                break;
            case 4:
                query4(dbconn);
                break;
            case 5:
            	query5(dbconn);
                break;
            default:
                return;
        }
    }

  /**
	 * Method Name: query1
	 * Purpose: Displays the query results for query1
	 *
	 * @param dbconn, a database connection to my Oracle database
	 */
	public static void query1(Connection dbconn) {
		try {
			System.out.println("Enter date in the format (YYYY-MM-DD) with no parentheses: ");
			Scanner scanner = new Scanner(System.in);
      String date1 = scanner.nextLine().strip();

      Statement stmt = null;
			ResultSet answer = null;
      String query = "select patientID,age,appointmentTime,covid_desc"
          + " from(select *"
          + "	from(select patients.patientID,age,appointmentTime,serviceID"
          + "		from wilsonliam.patients"
          + "		INNER JOIN wilsonliam.appointments"
          + "		ON wilsonliam.patients.patientID=wilsonliam.appointments.patientID) t1"
          + "	INNER JOIN rtguinanao.service"
          + "	ON t1.serviceID=rtguinanao.service.service_id) t2"
          + " WHERE TRUNC(appointmentTime) <= TO_DATE('"+ date1 +"', 'YYYY-MM-DD')"
          + " AND (t2.covid_desc = 'Dose 2'"
          + " OR t2.covid_desc = 'Dose 3'"
          + " OR t2.covid_desc = 'Dose 4')";
      stmt = dbconn.createStatement();
			answer = stmt.executeQuery(query);
			if (answer != null) {
            while (answer.next()) {
              System.out.println("PatientID:" + answer.getInt("patientID")
              + ", Age:" + answer.getInt("age")
              + ", AppointmentTime:" + answer.getTimestamp("appointmentTime")
              + ", Covid dose:" + answer.getString("covid_desc"));
            }
			}
		    stmt.close();
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
			    + "Could not execute option4.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
	}

	/**
	 * Method Name: query2
	 * Purpose: Displays the query results for query2
	 *
	 * @param dbconn, a database connection to my Oracle database
	 */
	public static void query2(Connection dbconn) {
		//TODO: Test Query
		try {
			System.out.println("Enter date in the format (YYYY-MM-DD) with no parentheses: ");
			Scanner scanner = new Scanner(System.in);
      String date1 = scanner.nextLine();
      Statement stmt = null;
      stmt = dbconn.createStatement();
			ResultSet answer = null;
      String query = " select *"
          + " from(select *"
          + "	from wilsonliam.appointments"
          + "	INNER JOIN rtguinanao.service"
          + "	ON wilsonliam.appointments.serviceID=rtguinanao.service.service_id"
          + "	WHERE TRUNC(appointmentTime) = TO_DATE('"+ date1+ "', 'YYYY-MM-DD')"
          + "	AND isWalkIn = 0"
          + "	AND isCancelled = 0"
          + "	ORDER BY service_dept, appointmentTime ASC)";

      answer = stmt.executeQuery(query);
			if (answer != null) {
          while (answer.next()) {
            System.out.println("PatientID:" + answer.getInt("patientID")
            + ", AppointmentTime:" + answer.getTimestamp("appointmentTime")
            + ", Service department:" + answer.getString("service_dept"));
          }
			}
		    stmt.close();
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
			    + "Could execute option4.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
	}

    public static void query3(Connection dbconn) {
        try {
            System.out.println("Enter query date in YYYY-MM-DD format:");
            Scanner scanner = new Scanner(System.in);
            String q3in = scanner.nextLine();
            Statement stmt = null;
            stmt = dbconn.createStatement();
            ResultSet answer = null;
            String query = "SELECT medicalStaffID,checkIn,checkOut from wilsonliam.availability WHERE (TRUNC(CHECKIN) = "
                    + String.format("TO_DATE('"+ q3in +"', 'YYYY-MM-DD'))");

            answer = stmt.executeQuery(query);
            if (answer != null) {
                while (answer.next()) {
                    System.out.println("medicalStaffID:" + answer.getInt("medicalStaffID")
                    + ", checkIn:" + answer.getTimestamp("checkIn")
                    + ", checkOut:" + answer.getTimestamp("checkOut"));
                }
            }
        stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could execute option4.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

    }

    public static void query4(Connection dbconn) {
        try {
            Statement stmt = null;
            stmt = dbconn.createStatement();
            ResultSet answer = null;
            String query = "SELECT p.isStudent, p.isEmployee, a.ServiceID, count(a.ServiceID) as NUM"
                    + " FROM wilsonliam.appointments a INNER JOIN  wilsonliam.patients p"
                    + " ON a.PatientID = p.PatientID"
                    + " WHERE (a.SERVICEID < 5)"
                    + " GROUP BY p.isEmployee, p.isStudent,a.ServiceID";

            answer = stmt.executeQuery(query);
            if (answer != null) {
                while (answer.next()) {
                    System.out.println("isStudent:" + answer.getInt("isStudent")
                    + ", isEmployee:" + answer.getInt("isEmployee")
                    + ", ServiceID:" + answer.getInt("ServiceID")
                    + ", NumOfDoses:" + answer.getInt("NUM"));
		    //TODO: Add count of patients that have received two doses but not the 3rd.
		    //TODO: Add the count of patients who have only received the first dose.
                }
            }
        stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could execute option4.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

    }

	/**
	 * Method Name: query5
	 * Purpose: Displays the query results for query5
	 *
	 * @param dbconn, a database connection to my Oracle database
	 */
	public static void query5(Connection dbconn) {
		try {
			System.out.println("Enter date in the format (YYYY-MM-DD) with no parentheses: ");
			Scanner scanner = new Scanner(System.in);
            String date1 = scanner.nextLine();
            Statement stmt = null;
            stmt = dbconn.createStatement();
            ResultSet answer = null;
                String query = "select transaction_id, tr_date, amount, service_dept"
                + " from(select *"
                + "	from(select *"
                + "		from garyli.transactions"
                + "		INNER JOIN wilsonliam.appointments"
                + "		ON garyli.transactions.appt_id=wilsonliam.appointments.appointmentID) t1"
                + "	INNER JOIN rtguinanao.service"
                + "	ON t1.serviceID=rtguinanao.service.service_id)"
                + " WHERE TRUNC(tr_date) = TO_DATE('"+ date1 +"', 'YYYY-MM-DD')"
                + " AND service_dept = 'Immunization'";

            answer = stmt.executeQuery(query);
            if (answer != null) {
                while (answer.next()) {
                    System.out.println("Transaction Id:" + answer.getInt("transaction_id")
                    + ", Transaction Date:" + answer.getTimestamp("tr_date")
                    + ", Transaction Amount:" + answer.getBigDecimal("amount")
                    + ", Service department:" + answer.getString("service_dept"));
                }
			}
		    stmt.close();
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
			    + "Could execute option4.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
	}

    public static void main (String [] args)
    {
        Connection dbconn = connectSQL(args);
        // Use to ensure code is working: runQuery(dbconn, "SELECT sno, status FROM mccann.s");

        promptUser(dbconn);
        try {
            dbconn.close();
        } catch (SQLException e) {};
    }
}
