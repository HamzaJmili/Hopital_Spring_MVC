package master.sdia.hopital_spring_mvc;

import ma.fs.hospital.entities.Patient;
import ma.fs.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class HospitalApplication implements CommandLineRunner {

    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Patient patient = new Patient();
//        patient.setId(null);
//        patient.setName("Ahmed");
//        patient.setDateNaissance(new Date());
//        patient.setMaladie(false);
//        patient.setScore(23);

//        Patient patient2 = new Patient(null, "Yassine", new Date(), false, 123);
//
//        Patient patient3 = Patient.builder().name("Imane").dateNaissance(new Date()).maladie(false).score(12).build();

        patientRepository.save(new Patient(null, "Ahmed", LocalDate.now(), Boolean.FALSE, 23));
        patientRepository.save(new Patient(null, "Yassine", LocalDate.now(), Boolean.TRUE, 123));
        patientRepository.save(Patient.builder().name("Imane").dateNaissance(LocalDate.now()).maladie(true).score(12).build());
    }
}

