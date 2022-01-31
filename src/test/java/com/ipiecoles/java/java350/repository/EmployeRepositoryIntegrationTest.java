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

    @ParameterizedTest(name = "La performance moyenne des {0} est {4}")
    @CsvSource({
            "'C', 300, 100, 50, 150",
            "'T', 300, 100, 50, 150",
            "'M', 300, 100, 50, 150"
    })
    void testAvgPerformanceWhereMatriculeStartsWith(String type, int perf1, int perf2, int perf3, Double expectedAvgPerf){
        //Given
        createEmployee(type + "00000", perf1);
        createEmployee(type + "00001", perf2);
        createEmployee(type + "00002", perf3);

        //When
        Double avgActual = this.employeRepository.avgPerformanceWhereMatriculeStartsWith(type);

        //Then
        Assertions.assertThat(avgActual).isEqualTo(expectedAvgPerf);
    }

    void createEmployee(String matricule, int performance) {
        Employe employe = new Employe();
        employe.setNom("NOM");
        employe.setPrenom("Prénom");
        employe.setPerformance(performance);
        employe.setMatricule(matricule);
        employe.setDateEmbauche(LocalDate.now());

        employeRepository.save(employe);
    }
}