/*Print a list of patients who have their 2nd, 3rd or 4th doses of the COVID-19 vaccine scheduled by a
certain date (given that the date is entered by the user).*/

ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS';

select patientID,age, appointmentTime, covid_desc
from(	select *
	from(	select patients.patientID,age,appointmentTime,serviceID
		from wilsonliam.patients
		INNER JOIN wilsonliam.appointments
		ON wilsonliam.patients.patientID=wilsonliam.appointments.patientID) t1
	INNER JOIN rtguinanao.service
	ON t1.serviceID=rtguinanao.service.service_id) t2
WHERE TRUNC(appointmentTime) = TO_DATE('2021-06-07', 'YYYY-MM-DD')
AND t2.covid_desc = 'Dose 2'
OR t2.covid_desc = 'Dose 3'
OR t2.covid_desc = 'Dose 4';



/*

select patientID,age, appointmentTime, covid_desc
from(	select *
	from(	select patients.patientID,age,appointmentTime,serviceID
		from wilsonliam.patients
		INNER JOIN wilsonliam.appointments
		ON wilsonliam.patients.patientID=wilsonliam.appointments.patientID) t1
	INNER JOIN rtguinanao.service
	ON t1.serviceID=rtguinanao.service.service_id) t2
WHERE t2.covid_desc = 'Dose 2'
OR t2.covid_desc = 'Dose 3'
OR t2.covid_desc = 'Dose 4';


select *
from(	select *
	from wilsonliam.patients
	INNER JOIN wilsonliam.appointments
	ON wilsonliam.patients.patientID=wilsonliam.appointments.patientID) t1
INNER JOIN rtguinanao.service
ON t1.serviceID=rtguinanao.service.service_id;
*/