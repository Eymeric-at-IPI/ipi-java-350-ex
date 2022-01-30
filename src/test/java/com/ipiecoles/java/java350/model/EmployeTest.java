package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.InputMismatchException;

public class EmployeTest {

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsNull() {
        // Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsSeveralYearsOld() {
        // Given
        int years = 10;
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(years));

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isEqualTo(years);
    }

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsFewMonthsOld() {
        // Given
        int months = 10;
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusMonths(months));

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsNow() {
        // Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsInFewMonths() {
        // Given
        int months = 10;
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusMonths(months));

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    void testGetNombreAnneeAncienneteWhenDateEmbaucheIsInYears() {
        // Given
        int years = 10;
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(years));

        // When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "1, 'T12345', 0, 1.0, 1000.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "2, 'T12345', 0, 1.0, 2300.0",
            "2, 'T12345', 1, 1.0, 2400.0",
            "1, 'M12345', 0, 1.0, 1700.0",
            "1, 'M12345', 5, 1.0, 2200.0",
            "2, 'M12345', 0, 1.0, 1700.0",
            "2, 'M12345', 8, 1.0, 2500.0"
    })
    void testGetPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle){
        //Given
        Employe employe = new Employe(
                "Doe",
                "John",
                matricule,
                LocalDate.now().minusYears(nbYearsAnciennete),
                Entreprise.SALAIRE_BASE,
                performance,
                tempsPartiel
        );

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(prime).isEqualTo(primeAnnuelle);
    }

    @Test
    void testAugmenterSalaireWhenSalaireIsNull() {
        //Given
        Employe employe = new Employe("Doe","Jhon","T12345",LocalDate.now(),null,1,1.0);

        //When
        employe.augmenterSalaire(25.0);

        //Then
        // 1000.00 * 1.25 = 1250.00
        Assertions.assertThat(employe.getSalaire()).isZero();
    }

    @Test
    void testAugmenterSalaireIsComputingWell() {
        //Given
        Employe employe = new Employe("Doe","Jhon","T12345",LocalDate.now(),1000.00,1,1.0);

        //When
        employe.augmenterSalaire(25.0);

        //Then
        // 1000.00 * 1.25 = 1250.00
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1250.00);
    }

    @Test
    void testAugmenterSalaireWhenNegativeAugmentation() {
        //Given
        Employe employe = new Employe("Doe","Jhon","T12345",LocalDate.now(),1000.00,1,1.0);

        //When
        Assertions.assertThatThrownBy(() -> employe.augmenterSalaire(-25.0))
                //Then
                .isInstanceOf(InputMismatchException.class)
                .hasMessage("Impossible de d'augmenter négativement un salaire !");
    }

    @Test
    void testAugmenterSalaireWhenSalaireIsZero() {
        //Given
        Employe employe = new Employe("Doe","Jhon","T12345",LocalDate.now(),0.00,1,1.0);

        //When
        employe.augmenterSalaire(25.0);

        //Then
        Assertions.assertThat(employe.getSalaire()).isZero();
    }

    @Test
    void testAugmenterSalaireWhenSalaireIsNegative() {
        //Given
        Employe employe = new Employe("Doe","Jhon","T12345",LocalDate.now(),-1000.00,1,1.0);

        //When
        employe.augmenterSalaire(25.0);

        //Then
        // |salaire| * augmentation = 1000 * 1.25
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1250.00);
    }

    @ParameterizedTest(name = "En {0} une personne qui travail {1} unité de temps a le droit à {2} RTT")
    @CsvSource({
            "2019, 0, 0",   // ceil( 365 - 218 - (104 + 1) - 25 - 10 ) x 0
            "2019, 0.5, 4", // ceil( 365 - 218 - (104 + 1) - 25 - 10 ) x 0.5
            "2019, 1, 7",   // ceil( 365 - 218 - (104 + 1) - 25 - 10 ) x 1
            "2021, 1, 10",  // ceil( 365 - 218 - (104 + 1) - 25 - 7 ) x 1
            "2022, 0.5, 5", // ceil( 365 - 218 - (104 + 1) - 25 - 7 ) x 0.5
            "2032, 2, 22"   // ceil( 366 - 218 - (104 + 1) - 25 - 7 ) x 2
    })
    void testGetNbRtt(int _yearToCheck, double _tempsPartiel, int _expectedRtt) {
        // Given
        Employe employe = new Employe();
        employe.setTempsPartiel(_tempsPartiel);

        // When
        int nbRtt = employe.getNbRtt(LocalDate.of(_yearToCheck,1, 1));

        // Then
        Assertions.assertThat(nbRtt).isEqualTo(_expectedRtt);
    }
}
