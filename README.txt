====== STEPS ======
1) Ensure that you've entered (export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH})
2) Compile Prog4.java
3) Simply run Prog4 providing your username and password to oracle as arguments
    ex. java Prog4 username password
Note) If you run a query, you must re-run the program.

Workload Distribution:
Gary: Analyzed FDs and Normalization for MedicalStaff/BursarTransactions tables and created them. Worked on queries 1, 2, and 5.
Liam: Analyzed FDs and Normalization for Patient/Availability tables. Created Patient/Availability/Appointment tables. Worked on queries 3, 4. Created script which created table inserts into each table to populate the tables with random information.
Fontaine: Analyzed FDs and Normalization for Insurance table and created it. Handled viewing/ deleting records from patients, employees, and appointments. Modified handleQueries method. 
Raphaelle: Analyzed FDs and Normalization for Appointment and Service tables. Created the Service table. Handled prompting user functionality by allowing users to add/ update patients, employees, and appointments.