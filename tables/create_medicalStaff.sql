--netId: garyli

create table medical_staff(
	medical_staff_id integer not null,
    	staff_name varchar2(50),
	service_id integer not null,
	constraint med_staff_pk primary key (medical_staff_id)
);

GRANT ALL ON medical_staff TO PUBLIC;

set autocommit off;
