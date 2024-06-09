/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package alerts.strategy;

import com.alerts.Alert;
import com.alerts.strategy.BloodPressureStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Jack
 */
public class BloodPressureStrategyTest {

    private DataStorage dataStorage;
    private BloodPressureStrategy strategy;
    private long currentTime;
     private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() {
        // Create a mock DataStorage instance
        dataStorage = mock(DataStorage.class);
        strategy = new BloodPressureStrategy(dataStorage);
        currentTime = System.currentTimeMillis();
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
    public void testForIncreasingTrend() {
        // Create mock patient
        Patient mockPatient = new Patient(1);

        // Mocking dataStorage to return the relevant records
        List<PatientRecord> records = List.of(
                // Systolic records
                new PatientRecord(1, 130, "SystolicPressure", currentTime),
                new PatientRecord(1, 145, "SystolicPressure", currentTime),
                new PatientRecord(1, 160, "SystolicPressure", currentTime),
                // Diastolic records
                new PatientRecord(1, 90, "DiastolicPressure", currentTime),
                new PatientRecord(1, 105, "DiastolicPressure", currentTime),
                new PatientRecord(1, 116, "DiastolicPressure", currentTime)
        );
        
         when(dataStorage.getRecords(Mockito.eq(1), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(records);

        // Evaluate the data to check for alerts
        strategy.checkAlert(mockPatient);

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

        strategy.checkAlert(mockPatient);

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
        strategy.checkAlert(mockPatient);

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
        strategy.checkAlert(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        // Assert the upper threshold alert
        assertTrue(output.contains("Diastolic Blood Pressure Critical High Alert"),
                "Expected: 'Diastolic Blood Pressure Critical High Alert'\nReceived: " + output);

        // Assert the lower threshold alert
        assertTrue(output.contains("Diastolic Blood Pressure Critical Low Alert"),
                "Expected: 'Diastolic Blood Pressure Critical Low Alert'\nReceived: " + output);
    }
}
