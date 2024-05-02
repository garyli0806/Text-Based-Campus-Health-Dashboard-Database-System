CREATE TABLE availability 
    (medicalStaffId int not null,
    checkIn date not null,
    checkOut date not null,

    CONSTRAINT avail_pk PRIMARY KEY(medicalStaffId,checkIn,checkOut)
);

GRANT SELECT ON availability TO PUBLIC;
SET autocommit OFF;
