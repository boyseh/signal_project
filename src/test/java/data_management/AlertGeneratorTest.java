/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.cardio_generator.HealthDataSimulator;
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
    void testForAbnormalECGData() {
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

    @Test
    void testForTriggeredAlert() {

        Patient mockPatient = new Patient(1);
        currentTime = System.currentTimeMillis();

        // Evaluate the data to check for alerts
        HealthDataSimulator mockHealthDataSim = Mockito.spy(new HealthDataSimulator());

        mockHealthDataSim.generateTriggeredAlert(mockPatient, true);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Triggered Alert"),
                "Expected: 'Triggered Alert'\nReceived: " + output);
    }

}
