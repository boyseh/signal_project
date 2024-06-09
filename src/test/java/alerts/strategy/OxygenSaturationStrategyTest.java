/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package alerts.strategy;

import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.OxygenSaturationStrategy;
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
public class OxygenSaturationStrategyTest {
    
     private DataStorage dataStorage;
    private OxygenSaturationStrategy strategy;
    private long currentTime;
     private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() {
        // Create a mock DataStorage instance
        dataStorage = mock(DataStorage.class);
        strategy = new OxygenSaturationStrategy(dataStorage);
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
        strategy.checkAlert(mockPatient);

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
        strategy.checkAlert(mockPatient);

        // Retrieve the output
        String output = outContent.toString().trim();

        assertTrue(output.contains("Rapid Drop Alert"),
                "Expected: 'Rapid Drop Alert'\nReceived: " + output);

    }

    
}
