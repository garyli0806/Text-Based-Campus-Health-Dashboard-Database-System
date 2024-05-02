/*Given a transaction date, output what transactions occured in the Immunizations department on that particular day.*/

ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS';

select transaction_id, tr_date, amount, service_dept
from(	select *
	from(	select *
		from garyli.transactions
		INNER JOIN wilsonliam.appointments
		ON garyli.transactions.appt_id=wilsonliam.appointments.appointmentID) t1
	INNER JOIN rtguinanao.service
	ON t1.serviceID=rtguinanao.service.service_id)
WHERE TRUNC(tr_date) = TO_DATE('2020-11-01', 'YYYY-MM-DD');