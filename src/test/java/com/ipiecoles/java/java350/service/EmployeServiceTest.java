package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    @Test
    void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        String matricule = "T00346";
        when(employeRepository.findLastMatricule())
                .thenReturn("00345");
        when(employeRepository.findByMatricule(matricule))
                .thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        // Check then catch new 'employe' instance if we pass through 'employeRepository.save()' at least 1 time
        verify(employeRepository, times(1))
                .save(employeCaptor.capture());

        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo(matricule);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        //1521.22 * 1.2 * 1.0
        Assertions.assertThat(Math.round(employe.getSalaire() * 100.0) / 100.0).isEqualTo(1825.46);
    }

    @Test
    void testEmbaucheEmployeTechnicienPleinTempsBtsLimit(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When
        Assertions.assertThatThrownBy(() -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel))
                //Then
                .isInstanceOf(EmployeException.class)
                .hasMessage("Limite des 100000 matricules atteinte !");
    }

    @Test
    public void testEmbaucheEmployeExistant(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findByMatricule(Mockito.anyString()))
                .thenReturn(new Employe());

        //When
        Assertions.assertThatThrownBy(() -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel))
                //Then
                .isInstanceOf(EntityExistsException.class)
                .hasMessageStartingWith("L'employé de matricule")
                .hasMessageEndingWith("existe déjà en BDD");
    }

    @ParameterizedTest(name = "Pour un commercial avec un caTraite de {1} et un caObjectif de {2} alors la performance attendu est de {5}")
    @CsvSource({
            "'C',  9000,       10000,          4,              2,          2",
            "'C',  14000,      10000,          40,             38,         45",
            "'C',  1000,       1000,           30,             16,         31",
            "'C',  12000,      10000,          6,              7,          7",
            "'C',  9000,       10000,          4,              16,         2",
            "'C',  1000,       1000,           30,             80,         30",
            "'C',  9000,       10000,          22,             2,          21",
            "'C',  1000,       1000,           30,             30,         30",
            "'C',  12000,      10000,          80,             90,         81",
            "'C',  14000,      10000,          120,            8,          125",
            "'C',  12000,      10000,          80,             8,          82",
            "'C',  14000,      10000,          2,              38,         6",
            "'C',  9000,       10000,          -22,            2,          1",
            "'C',  12000,      10000,          -16,            9,          -15",
            "'C',  9000,       10000,          -22,            -33,        2",
            "'C',  1000,       1000,           -5,             6,          1",
            "'C',  9000,       10000,          22,             -18,        21",
            "'C',  1000,       1000,           -5,             -7,         2",
            "'C',  12000,      10000,          -16,            -2,         -15",
            "'C',  14000,      10000,          -1,             -6,         4",
            "'C',  14000,      10000,          2,              -15,        7",
            "'C',  14000,      10000,          -2,             1,          3",
            "'C',  1000,       1000,           5,              -9,         6",
            "'C',  12000,      10000,          16,             -28,        18",
    })
    void testCalculPerformanceCommercial(String matricule, Long caTraite, Long objectifCa, int perf, Double perfMoyenne, int perfAttendu) throws EmployeException{
        //Given
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setPerformance(perf);
        when(employeRepository.findByMatricule(any(String.class)))
                .thenReturn(employe);
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C"))
                .thenReturn(perfMoyenne);
        when(employeRepository.save(any(Employe.class)))
                .thenReturn(employe);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //Then
        Assertions.assertThat(employe.getPerformance()).isEqualTo(perfAttendu);
    }
}