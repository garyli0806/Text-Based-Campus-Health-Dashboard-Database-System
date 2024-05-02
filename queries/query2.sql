/*Given a certain date, output which patients had a non–walk–in appointment. Sort in order by appointment time and group by type of service.*/

ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS';

select * 
from(	select *
	from wilsonliam.appointments
	INNER JOIN rtguinanao.service
	ON wilsonliam.appointments.serviceID=rtguinanao.service.service_id
	WHERE TRUNC(appointmentTime) = TO_DATE('2021-06-07', 'YYYY-MM-DD')
	AND isWalkIn = 0
	AND isCancelled = 0
	ORDER BY service_dept, appointmentTime ASC);

/*
Brainstorming
*/

/*
select patientID
from wilsonliam.appointments
WHERE TRUNC(appointmentTime) = TO_DATE('2020-09-05', 'YYYY-MM-DD')
AND isWalkIn = 0
AND isCancelled = 0
ORDER BY appointmentTime ASC;
*/

/*
select *
from wilsonliam.appointments
INNER JOIN rtguinanao.service
ON wilsonliam.appointments.serviceID=rtguinanao.service.service_id;
*/

/*
select patientID
from wilsonliam.appointments
INNER JOIN rtguinanao.service
ON wilsonliam.appointments.serviceID=rtguinanao.service.service_id
WHERE TRUNC(wilsonliam.appointmentTime) = TO_DATE('2020-09-05', 'YYYY-MM-DD')
AND wilsonliam.isWalkIn = 0;
AND wilsonliam.isCancelled = 0
ORDER BY wilsonliam.appointmentTime ASC
GROUP BY service_id;
*/