/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class AlertGeneratorTest {
    
    private AlertGenerator alertGenerator;
    private DataStorage mockDataStorage;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private long currentTime;

    @BeforeEach
    void setUp() {
        mockDataStorage = Mockito.mock(DataStorage.class);
        alertGenerator = new AlertGenerator(mockDataStorage);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        currentTime = System.currentTimeMillis();
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    
   
     @Test
    void testEvaluateSystolicPressureCriticalAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> systolicRecord = Arrays.asList(
            new PatientRecord(1, 200, "SystolicPressure", currentTime)  
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(systolicRecord);

        alertGenerator.evaluateData(patient);

        String output = outContent.toString();
        assertTrue(output.contains("Systolic Blood Pressure has exceeded 180 mmHg"), 
            "Expected 'Critical Pressure Threshold Alert' for high systolic pressure but got: " + output);
    }
    
    
      @Test
    void testTrendAlertIncreasing(){
        
        //Create mock patient
        Patient mockPatient = Mockito.mock(Patient.class);
        when(mockPatient.getPatientID()).thenReturn(1);
        long currentTime = System.currentTimeMillis();
        
        //Create increasing test data 
     
           List<PatientRecord> records = Arrays.asList(
            new PatientRecord(1, 100, "SystolicPressure", currentTime-10),
            new PatientRecord(1, 111, "SystolicPressure", currentTime-5),
            new PatientRecord(1, 122, "SystolicPressure", currentTime)
        );
           
            Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(records);

        
        alertGenerator.evaluateData(mockPatient);
        
        String output = outContent.toString();
        
        assertTrue(output.contains("Systolic Blood Pressure Increasing Trend Alert"), 
            "Expected 'Systolic Blood Pressure Increasing Trend Alert' for trend check but got: " + output);
      
    }
        
    
      @Test
    void testEvaluateBloodPressureIncreasingTrends() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = Arrays.asList(
            new PatientRecord(1, 100, "SystolicPressure", currentTime-10),
            new PatientRecord(1, 111, "SystolicPressure", currentTime-5),
            new PatientRecord(1, 122, "SystolicPressure", currentTime)
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertTrue(outContent.toString().contains("Systolic Blood Pressure Increasing Trend Alert"));
    }

    
        

    
    
    

/*
   
    @Test
    void testTrendAlertIncreasingSystolic() {
        Patient patient = new Patient(1);
        

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        patient.addRecord(130, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(140, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(150, "SystolicPressure", System.currentTimeMillis());

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();

        assertEquals(1, alerts.size());
        assertEquals("Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    void testTrendAlertDecreasingSystolic() {
        Patient patient = new Patient(1);
        patient.setPreviousSystolicBP(150);

        AlertGenerator alertGenerator = new AlertGenerator(new DataStorage());

        patient.addRecord(140, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(130, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(120, "SystolicPressure", System.currentTimeMillis());

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();

        assertEquals(1, alerts.size());
        assertEquals("Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    void testTrendAlertIncreasingDiastolic() {
        Patient patient = new Patient(1);
        patient.setPreviousSystolicBP(120);

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        patient.addRecord(130, "DiastolicPressure", System.currentTimeMillis());
        patient.addRecord(140, "DiastolicPressure", System.currentTimeMillis());
        patient.addRecord(150, "DiastolicPressure", System.currentTimeMillis());

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();

        assertEquals(1, alerts.size());
        assertEquals("Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    void testTrendAlertDecreasingDiastolic() {
        Patient patient = new Patient(1);
        patient.setPreviousDiastolicBP(150);

        AlertGenerator alertGenerator = new AlertGenerator(new DataStorage());

        patient.addRecord(140, "DiastolicPressure", System.currentTimeMillis());
        patient.addRecord(130, "DiatolicPressure", System.currentTimeMillis());
        patient.addRecord(120, "DiastolicPressure", System.currentTimeMillis());

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();

        assertEquals(1, alerts.size());
        assertEquals("Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    void testCriticalThresholdAlert() {
        Patient patient = new Patient(1);
        AlertGenerator alertGenerator = new AlertGenerator(new DataStorage());

        // High systolic BP
        patient.addRecord(190, "SystolicPressure", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Critical Threshold Alert", alerts.get(0).getCondition());

        // Low systolic BP
        patient.addRecord(80, "SystolicPressure", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(2, alerts.size());
        assertEquals("Critical Threshold Alert", alerts.get(1).getCondition());

        // High diastolic BP
        patient.addRecord(120, "DiastolicPressure", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Critical Threshold Alert", alerts.get(2).getCondition());

        // Low diastolic BP
        patient.addRecord(50, "DiastolicPressure", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Critical Threshold Alert", alerts.get(3).getCondition());
    }

    @Test
    void testLowSaturation() {
        Patient patient = new Patient(1);
        AlertGenerator alertGenerator = new AlertGenerator(new DataStorage());
        // Add a record with saturation below 92%
        patient.addRecord(85, "Saturation", System.currentTimeMillis());

        // Evaluate the data
        alertGenerator.evaluateData(patient);

        // Assert that a low saturation alert is triggered
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Low Saturation Alert", alerts.get(0).getCondition());
    }

    void testRapidDropAlert() {
        Patient patient = new Patient(1);

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        // Add records with a drop of 5% or more within a 10-minute interval
        long currentTime = System.currentTimeMillis();
        long tenMinutesAgo = currentTime - (10 * 60 * 1000); // 10 minutes ago

        // Initial saturation level
        patient.addRecord(90, "Saturation", tenMinutesAgo);

        // Rapid drop of 5% or more within 10 minutes
        patient.addRecord(83, "Saturation", currentTime);

        // Evaluate the data
        alertGenerator.evaluateData(patient);

        // Assert that a rapid drop alert is triggered
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Rapid Drop Alert", alerts.get(0).getCondition());
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        Patient patient = new Patient(1);

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        // Add records with systolic BP below 90 mmHg and saturation below 92%
        patient.addRecord(80, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(85, "Saturation", System.currentTimeMillis());

        // Evaluate the data
        alertGenerator.evaluateData(patient);

        // Assert that a hypotensive hypoxemia alert is triggered
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Hypotensive Hypoxemia Alert", alerts.get(0).getType());
    }

    @Test
    void testAbnormalHeartRateAlert() {
        Patient patient = new Patient(1);

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        patient.addRecord(45, "HeartRate", System.currentTimeMillis());
        patient.addRecord(105, "HeartRate", System.currentTimeMillis());

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(2, alerts.size());
        assertEquals("Abnormal Heart Rate Alert", alerts.get(0).getCondition());
    }

    @Test
    void testIrregularBeatAlert() {
        Patient patient = new Patient(1);

        DataStorage ds = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(ds);

        // Add records with significant variations in time intervals between consecutive beats
        patient.addRecord(0.8, "ECG", System.currentTimeMillis());
        patient.addRecord(0.7, "ECG", System.currentTimeMillis());

        // Evaluate the data
        alertGenerator.evaluateData(patient);

        // Assert that an irregular beat alert is triggered
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Irregular Heart Beat Alert", alerts.get(0).getCondition());
    }
    */
}