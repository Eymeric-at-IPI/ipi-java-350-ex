package com.ipiecoles.java.java350.repository;


import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmployeRepositoryIntegrationTest {

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @ParameterizedTest(name = "La performance moyenne des employés type {0} est {1}")
    @CsvSource({
            //type  expectedAvgPerf
            "'C',   102.6",
            "'T',   21",
            "'M',   98.6"
    })
    void testAvgPerformanceWhereMatriculeStartsWith(String _type,Double _expectedAvgPerf){
        //Given
        // insert some commercial
        populateDBWithEmployeWithMatriculeAndPerformance("C12345", 15);
        populateDBWithEmployeWithMatriculeAndPerformance("C12346", 76);
        populateDBWithEmployeWithMatriculeAndPerformance("C12347", 400);
        populateDBWithEmployeWithMatriculeAndPerformance("C12348", 13);
        populateDBWithEmployeWithMatriculeAndPerformance("C12349", 9);
        // insert some technicien
        populateDBWithEmployeWithMatriculeAndPerformance("T12345", 32);
        populateDBWithEmployeWithMatriculeAndPerformance("T12346", 60);
        populateDBWithEmployeWithMatriculeAndPerformance("T12347", 1);
        populateDBWithEmployeWithMatriculeAndPerformance("T12348", 3);
        populateDBWithEmployeWithMatriculeAndPerformance("T12349", 9);
        // insert some manager
        populateDBWithEmployeWithMatriculeAndPerformance("M12345", 5);
        populateDBWithEmployeWithMatriculeAndPerformance("M12346", 8);
        populateDBWithEmployeWithMatriculeAndPerformance("M12347", 4);
        populateDBWithEmployeWithMatriculeAndPerformance("M12348", 378);
        populateDBWithEmployeWithMatriculeAndPerformance("M12349", 98);

        //When
        Double avgActual = this.employeRepository.avgPerformanceWhereMatriculeStartsWith(_type);

        //Then
        Assertions.assertThat(avgActual).isEqualTo(_expectedAvgPerf);
    }

    void populateDBWithEmployeWithMatriculeAndPerformance(String _matricule, int _performance) {
        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("Jhon");
        employe.setMatricule(_matricule);
        employe.setPerformance(_performance);
        employe.setDateEmbauche(LocalDate.now());

        employeRepository.save(employe);
    }
}