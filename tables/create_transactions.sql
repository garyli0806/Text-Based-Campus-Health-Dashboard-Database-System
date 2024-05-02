--netId: garyli

create table transactions(
	transaction_id integer not null,
    	patient_id integer not null,
    	appt_id integer not null,
	amount decimal(38,2) not null,
	tr_date date not null,
	CONSTRAINT transPK primary key (transaction_id)
);

GRANT ALL ON transactions TO PUBLIC;

set autocommit off;
