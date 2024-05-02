import datetime
import random
from faker import Faker

FINAL_START = datetime.datetime(2020,1,1,8,0,0)
DAY_LENGTH = datetime.timedelta(hours=10)
FINAL_END = datetime.datetime(2022,5,1,17,0,0)

fake = Faker()

class PatientRecord:
    counter = 0

    pid_set = set()

    def __init__(self, table_name):
        PatientRecord.counter += 1

        self.id = PatientRecord.counter
        self.name = fake.name()
        
        PatientRecord.pid_set.add(self.id)

        self.student = random.randint(0,1)
        self.employee = random.randint(0,1)
        self.insurance = self.employee if self.employee else random.randint(0,1)
        self.age = random.randint(2,100)
        self.table_name = table_name 

    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.id},'{self.name}',{self.student},"
                f"{self.employee},{self.insurance},{self.age}"
                ");"
                )

class MedicalStaffRecord:
    counter = 0

    med_id_set = set()

    def __init__(self,table_name):
        self.table_name = table_name
        MedicalStaffRecord.counter += 1
        self.id = MedicalStaffRecord.counter
        MedicalStaffRecord.med_id_set.add(self.id)

        self.name = fake.name()
        self.service = random.choice(tuple(ServiceRecord.service_id_set))

    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.id},'{self.name}',{self.service},"
                ");"
                )

class AvailabilityRecord:
    def __init__(self,table_name):
        self.table_name = table_name
        self.med_staff_id = random.choice(tuple(MedicalStaffRecord.med_id_set))
        self.checkIn = fake.date_time_between(start_date=FINAL_START, end_date=FINAL_END)
        self.checkOut = fake.date_time_between(start_date=self.checkIn, end_date=self.checkIn+DAY_LENGTH)

    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.med_staff_id},"
                f"TO_DATE('{str(self.checkIn)}','YYYY-MM-DD HH24:MI:SS'),"
                f"TO_DATE('{str(self.checkOut)}','YYYY-MM-DD HH24:MI:SS')"
                ");"
                )

class AppointmentRecord:
    counter = 0

    appt_id_set = set()

    def __init__(self,table_name):
        AppointmentRecord.counter += 1
        self.id = AppointmentRecord.counter
        AppointmentRecord.appt_id_set.add(self.id)
        self.table_name = table_name
        self.patient_id = random.choice(tuple(PatientRecord.pid_set))
        #self.transaction_id = random.choice(tuple(TransactionRecord.transaction_id_set))
        self.service_id = random.choice(tuple(ServiceRecord.service_id_set))
        self.appointment_time = fake.date_time_between(start_date=FINAL_START, end_date=FINAL_END)
        self.severity = random.randint(0,1)
        self.isWalkIn = random.randint(0,1)
        # less likely to be cancelled
        self.isCancelled = (1 if random.randint(0,20) > 15 and (not self.isWalkIn) else 0)



    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.id},{self.patient_id},{self.service_id},"
                f"TO_DATE('{str(self.appointment_time)}','YYYY-MM-DD HH24:MI:SS'),"
                f"{self.severity},{self.isWalkIn},{self.isCancelled}"
                ");"
                )

class TransactionRecord:
    counter = 0

    transaction_id_set = set()

    def __init__(self,table_name):
        TransactionRecord.counter += 1
        self.id = TransactionRecord.counter
        self.table_name = table_name
        TransactionRecord.transaction_id_set.add(self.id)
        self.patient_id = random.choice(tuple(PatientRecord.pid_set))
        self.appointment_id = random.choice(tuple(AppointmentRecord.appt_id_set)) 
        self.date = fake.date_time_between(start_date=FINAL_START, end_date=FINAL_END)
        self.amount = random.randint(0,500) + random.randint(0,100)/100 

    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.id},{self.patient_id},{self.appointment_id},"
                f"{self.amount},TO_DATE('{self.date}','YYYY-MM-DD HH24:MI:SS')"
                ");"
                )

class ServiceRecord:
    counter = 0
    service_id_set = set()
    covid_iter = 0
    services = ['COVID-19 immunization',
                'Broken bone',
                'Traumatic brain injury',
                'Influenza immunization',
                'Laceration',
                'Bacterial or viral infection',
                'STD testing',
                'ADHD',
                'Depression/anxiety',
                ]
    depts = ('General Medicine', 'CAPS','Laboratory and Testing', 'Immunizations')
    covid_descs = ('Dose 1', 'Dose 2', 'Dose 3', 'Dose 4')

    def __init__(self,table_name):
        if ServiceRecord.covid_iter >= 4:
            ServiceRecord.services.pop(0)
        if len(ServiceRecord.services) == 0:
            print("TOO MANY")
 
        ServiceRecord.counter += 1
        self.table_name = table_name
        self.id = ServiceRecord.counter
        ServiceRecord.service_id_set.add(self.id)
        self.service_type = ServiceRecord.services[0]
        if self.service_type != 'COVID-19 immunization' and ServiceRecord.covid_iter > 4:
            ServiceRecord.services.pop(0)
        self.covid_desc = 'NULL' 
        self.service_dept = 'NULL'

        # we need one of each type
        if self.service_type == 'COVID-19 immunization':
            self.covid_desc = ServiceRecord.covid_descs[ServiceRecord.covid_iter]
            ServiceRecord.covid_iter += 1

        if self.service_type in ('Influenza immunization','COVID-19 immunization'):
            self.service_dept = 'Immunizations'
        elif self.service_type in ('ADHD','Depression/anxiety'):
            self.service_dept = 'CAPS'
        elif self.service_type in ('Laceration','Traumatic brain injury','Broken bone'):
            self.service_dept = 'General Medicine'
        elif self.service_type in ('Bacterial or viral infection','STD testing'):
            self.service_dept = 'Laboratory and Testing'
       

    def record_to_insert(self):
        return (f"INSERT INTO {self.table_name} VALUES ("
                f"{self.id},'{self.service_dept}','{self.service_type}',"
                f"'{self.covid_desc}'"
                ");"
                )


def dump_inserts(fname, inserts):
    with open(fname,'w') as sf:
        for i in inserts:
            sf.write(i+'\n')


def main():
    services = [ServiceRecord('rtguinanao.service').record_to_insert() for i in range(12)]
    patients = [PatientRecord('wilsonliam.patients').record_to_insert() for i in range(20)]
    medical_staff = [MedicalStaffRecord('garyli.medical_staff').record_to_insert() for i in range(20)]
    appointments = [AppointmentRecord('wilsonliam.appointments').record_to_insert() for i in range(25)]
    transactions = [TransactionRecord('garyli.transactions').record_to_insert() for i in range(25)]
    availability = [AvailabilityRecord('wilsonliam.availability').record_to_insert() for i in range(100)]

    # Insurance all done via Fontaine's file...?

    dump_inserts('insert_services.sql', services)
    dump_inserts('insert_patients.sql',patients)
    dump_inserts('insert_medical_staff.sql', medical_staff)
    dump_inserts('insert_appointments.sql',appointments)
    dump_inserts('insert_transactions.sql',transactions)
    dump_inserts('insert_availability.sql', availability)

main()
