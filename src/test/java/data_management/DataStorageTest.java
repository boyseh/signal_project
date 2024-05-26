package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.io.IOException;
import java.util.List;

class DataStorageTest {
/*
    @Test
    void testAddAndGetRecords() {

        DataReader reader = new FileDataReader("src/test/java/data_management/testFiles");
        
        DataStorage storage = new DataStorage();
        
        try{
            reader.readData(storage);
        } catch(Exception e){
            System.out.println("smth went wrong");
        }

        List<PatientRecord> records = storage.getRecords(10, 1716683934356L, 1716683934357L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(0.19370658167173713, records.get(0).getMeasurementValue()); // Validate first record
        
        List<Patient> allRecords = storage.getAllPatients();
        assertEquals(40, allRecords.get(1).getPatientID()); // Check the first patient id 
        assertEquals(32, allRecords.get(7).getPatientID()); // Check the last patient id 
    }

    @Test
    void testGetRecordsNullBehaviour() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(10, 1714748468033L, 1714748468034L);
        assertEquals(0, records.size()); // Check if the new empty Array was created
    }

    @Test
    void testReadDataFileReadingError() {
        DataReader reader = new FileDataReader("invalid/directory/path");
        DataStorage storage = new DataStorage();

        Exception exception = assertThrows(IOException.class, () -> {
            reader.readData(storage);
        });

        String expectedMessage = "Error reading file";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
*/
}