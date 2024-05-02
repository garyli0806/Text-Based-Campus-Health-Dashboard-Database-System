-- netId: fcoutino

-- these are the rates inputted into the sql table
-- INSERT INTO fcoutino.insurance (insurance_type, insurance_rate)
--                         VALUES ('studentTEST',  10);
-- INSERT INTO fcoutino.insurance (insurance_type, insurance_rate)
--                         VALUES ('employeeTEST', 15);

CREATE TABLE insurance(
	insurance_type VARCHAR2(50) NOT NULL,
	insurance_rate INT          NOT NULL,
    CONSTRAINT ins_pk PRIMARY KEY(insurance_type)
);

GRANT SELECT ON insurance TO PUBLIC;
SET autocommit OFF;
