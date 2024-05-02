CREATE TABLE appointments 
    (appointmentID int not null,
    patientID int not null,
    serviceID int not null,
    appointmentTime date,
    severity int not null,
    isWalkIn int not null,
    isCancelled int not null,

    CONSTRAINT appt_pk PRIMARY KEY(appointmentID)
    );

GRANT SELECT ON appointment TO PUBLIC;
SET autocommit OFF;
