package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @Test
    public void testIntegrationEmbaucheEmploye() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "Jane";
        String lastMatricule = "T12345";
        String expectedMatricule = "T12346";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        employeRepository.save(new Employe(nom, prenom, lastMatricule, LocalDate.now(), Entreprise.SALAIRE_BASE, 1, tempsPartiel));

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule(expectedMatricule);
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo(expectedMatricule);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);

        //1521.22 * 1.2 * 1.0
        Assertions.assertThat(Math.round(employe.getSalaire() * 100.0) / 100.0).isEqualTo(1825.46);
    }

    @ParameterizedTest()
    @CsvSource({//270
            //matricule,    caTraite,   objectifCa,     currentPerf,    expectedPerf
            "'C12345',      9000,       10000,          5,              3",
            "'C12345',      1000,       1000,           30,             30",
            "'C12345',      12000,      10000,          50,             51",
            "'C12345',      14000,      10000,          10,             14",
    })
    void testCalculPerformanceCommercialAllCase(String _matricule, Long _caTraite, Long _objectifCa, Integer _currentPerf, Integer _expectedPerf) throws EmployeException{
        //Given
        populateDBWithCommercialWithPerformance("C00001", 10);
        populateDBWithCommercialWithPerformance("C00002", 30);
        populateDBWithCommercialWithPerformance("C00003", 80);
        populateDBWithCommercialWithPerformance("C00004", 150);

        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("Jhon");
        employe.setSalaire(2000.00);
        employe.setMatricule(_matricule);
        employe.setDateEmbauche(LocalDate.now());
        employe.setPerformance(_currentPerf);

        employeRepository.save(employe);

        //When
        employeService.calculPerformanceCommercial(_matricule, _caTraite, _objectifCa);
        employe = this.employeRepository.findByMatricule(_matricule);

        //Then
        Assertions.assertThat(employe.getPerformance()).isEqualTo(_expectedPerf);
    }

    void populateDBWithCommercialWithPerformance(String _matricule, int _performance) {
        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("Jhon");
        employe.setMatricule(_matricule);
        employe.setPerformance(_performance);
        employe.setDateEmbauche(LocalDate.now());

        employeRepository.save(employe);
    }

}
