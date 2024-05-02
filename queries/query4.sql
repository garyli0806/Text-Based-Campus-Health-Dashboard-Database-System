SELECT p.isStudent, p.isEmployee, a.ServiceID, count(a.ServiceID) as NUM 
FROM wilsonliam.appointments a INNER JOIN  wilsonliam.patients p 
ON a.PatientID = p.PatientID
WHERE (a.SERVICEID < 5)
GROUP BY p.isEmployee, p.isStudent,a.ServiceID;


