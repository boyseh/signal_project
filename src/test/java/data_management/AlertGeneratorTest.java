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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertGeneratorTest {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
    private Patient mockPatient;
    private long currentTime;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        dataStorage = Mockito.mock(DataStorage.class);
        alertGenerator = new AlertGenerator(dataStorage);

        // Set up the output stream to capture System.out prints
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

    }

    @AfterEach
    void restoreStreams() {
        // Restore the original System.out stream
        System.setOut(originalOut);
    }

    @Test
    void testForIncreasingTrend() {
        // Create mock patient
        Patient mockPatient = new Patient(1);

        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                //Systolic recs
                new PatientRecord(1, 130, "SystolicPressure", currentTime),
                new PatientRecord(1, 145, "SystolicPressure", currentTime),
                new PatientRecord(1, 160, "SystolicPressure", currentTime),
                //Diastolic recs
                new PatientRecord(1, 90, "DiastolicPressure", currentTime),
                new PatientRecord(1, 105, "DiastolicPressure", currentTime),
                new PatientRecord(1, 116, "DiastolicPressure", currentTime)
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString();
        // Assert the condition message
        assertTrue(output.contains("Systolic Blood Pressure Increasing Trend Alert"),
                "Expected: 'Systolic Blood Pressure Increasing Trend Alert'\nReceived: " + output);
        assertTrue(output.contains("Diastolic Blood Pressure Increasing Trend Alert"),
                "Expected: 'Diastolic Blood Pressure Increasing Trend Alert'\nReceived: " + output);

    }

    @Test
    void testForDecreasingTrend() {

        Patient mockPatient = new Patient(1);

        currentTime = System.currentTimeMillis();

        List<PatientRecord> systolicRecords = List.of(
                //Systolic Recs
                new PatientRecord(1, 130, "SystolicPressure", currentTime),
                new PatientRecord(1, 115, "SystolicPressure", currentTime),
                new PatientRecord(1, 100, "SystolicPressure", currentTime),
                //Diastolic recs
                new PatientRecord(1, 110, "DiastolicPressure", currentTime),
                new PatientRecord(1, 95, "DiastolicPressure", currentTime),
                new PatientRecord(1, 84, "DiastolicPressure", currentTime)
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(systolicRecords);

        alertGenerator.evaluateData(mockPatient);

        String output = outContent.toString();

        assertTrue(output.contains("Systolic Blood Pressure Decreasing Trend Alert"),
                "Expected: 'Systolic Blood Pressure Decreasing Trend Alert'\nReceived: " + output);
        assertTrue(output.contains("Diastolic Blood Pressure Decreasing Trend Alert"),
                "Expected: 'Diastolic Blood Pressure Decreasing Trend Alert'\nReceived: " + output);
    }

    @Test
    void testForCriticalThresholdSystolic() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 190, "SystolicPressure", currentTime), // above upper threshold
                new PatientRecord(1, 80, "SystolicPressure", currentTime) // below lower threshold
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        // Assert the upper threshold alert
        assertTrue(output.contains("Systolic Blood Pressure Critical High Alert"),
                "Expected: 'Systolic Blood Pressure Critical High Alert'\nReceived: " + output);

        // Assert the lower threshold alert
        assertTrue(output.contains("Systolic Blood Pressure Critical Low Alert"),
                "Expected: 'Systolic Blood Pressure Critical Low Alert'\nReceived: " + output);
    }

    @Test
    void testForCriticalThresholdDiastolic() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 131, "DiastolicPressure", currentTime), // above upper threshold
                new PatientRecord(1, 59, "DiastolicPressure", currentTime) // below lower threshold
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        // Assert the upper threshold alert
        assertTrue(output.contains("Diastolic Blood Pressure Critical High Alert"),
                "Expected: 'Diastolic Blood Pressure Critical High Alert'\nReceived: " + output);

        // Assert the lower threshold alert
        assertTrue(output.contains("Diastolic Blood Pressure Critical Low Alert"),
                "Expected: 'Diastolic Blood Pressure Critical Low Alert'\nReceived: " + output);
    }

    @Test
    void testForLowSaturation() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 91, "Saturation", currentTime)
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Low Saturation Alert"),
                "Expected: 'Low Saturation Alert'\nReceived: " + output);

    }

    @Test
    void testForRapidDrop() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 97, "Saturation", currentTime - 300000), //five minutes ago
                new PatientRecord(1, 92, "Saturation", currentTime)
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Rapid Drop Alert"),
                "Expected: 'Rapid Drop Alert'\nReceived: " + output);

    }

    @Test
    void testForHypotensiveHypoxemia() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 91, "Saturation", currentTime),
                new PatientRecord(1, 89, "SystolicPressure", currentTime)
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Hypotensive Hypoxemia Alert"),
                "Expected: 'Hypotensive Hypoxemia Alert'\nReceived: " + output);
    }
    @Test
    void testForAbnormalECGData(){
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 20, "ECG", currentTime)
                
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Critical Heart Rate Alert"),
                "Expected: 'Critical Heart Rate Alert'\nReceived: " + output);
    }
    
     @Test
    void testForIrregularECGData() {
        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
            new PatientRecord(1, 60, "ECG", currentTime - 500000),  
            new PatientRecord(1, 60, "ECG", currentTime - 400000), 
            new PatientRecord(1, 60, "ECG", currentTime - 300000),  
            new PatientRecord(1, 60, "ECG", currentTime - 200000),  
            new PatientRecord(1, 60, "ECG", currentTime - 100000),  
            new PatientRecord(1, 100, "ECG", currentTime)           
        );

        when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(records);

        // Evaluate the data to check for alerts
        alertGenerator.evaluateData(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Irregular Heart Rate Alert"),
            "Expected: 'Irregular Heart Rate Alert'\nReceived: " + output);
    }
    
}

/* 
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
    void testForDecreasingTrendSystolic(){
        
        //Create mock patient
        Patient mockPatient = Mockito.mock(Patient.class);
        when(mockPatient.getPatientID()).thenReturn(1);
        long currentTime = System.currentTimeMillis();
        
        //Create increasing test data 
     
           List<PatientRecord> records = Arrays.asList(
            new PatientRecord(1, 130, "SystolicPressure", currentTime),
            new PatientRecord(1, 119, "SystolicPressure", currentTime),
            new PatientRecord(1, 108, "SystolicPressure", currentTime)
        );
        
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
