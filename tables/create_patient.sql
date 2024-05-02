CREATE TABLE patient
    (patientID int not null,
    fullName varchar2(50) not null,
    isStudent int not null,
    isEmployee int not null,
    hasInsurance int not null,
    age int not null,

    CONSTRAINT pat_pk primary key (patientID)
);

GRANT SELECT ON patient TO PUBLIC;
SET autocommit OFF;
