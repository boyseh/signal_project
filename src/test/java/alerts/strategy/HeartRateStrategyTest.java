/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package alerts.strategy;

import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.HeartRateStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
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
public class HeartRateStrategyTest {
    
       private DataStorage dataStorage;
    private HeartRateStrategy strategy;
    private long currentTime;
     private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() {
        // Create a mock DataStorage instance
        dataStorage = mock(DataStorage.class);
        strategy = new HeartRateStrategy(dataStorage);
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
        strategy.checkAlert(mockPatient);

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
        strategy.checkAlert(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Irregular Heart Rate Alert"),
                "Expected: 'Irregular Heart Rate Alert'\nReceived: " + output);
    }
}
