-- rtguinanao
CREATE TABLE service (
    service_id INT NOT NULL,
    service_dept VARCHAR2(50) NOT NULL,
    service_desc VARCHAR2(200),
    covid_desc VARCHAR2(200),

    CONSTRAINT servicePK PRIMARY KEY(service_id)
);

GRANT SELECT ON service TO PUBLIC;

ALTER TABLE service
ADD CONSTRAINT check_service_dept
CHECK (service_dept IN ('General Medicine', 'CAPS', 'Laboratory and Testing', 'Immunization'));

SET autocommit OFF;
