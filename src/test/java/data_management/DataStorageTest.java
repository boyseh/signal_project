package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import java.io.IOException;

import java.util.List;
import org.mockito.Mockito;

class DataStorageTest {

    @Test
    void testAddRecords() throws IOException {

        DataStorage storage = new DataStorage();

        //testing adding records
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
        assertEquals(200.0, records.get(1).getMeasurementValue()); // Validate second record

    }

    @Test
    void testGettingRecords() {

        DataStorage storage = new DataStorage();

        //testing getting records 
        String testFilesPath = "src/test/java/data_management/testFiles";
        FileDataReader reader = new FileDataReader(testFilesPath);
        try {
            reader.readData(storage);
        } catch (IOException ex) {
            System.out.println("File not found");
        }

        /*Mock data in file
        Patient ID: 1, Timestamp: 1716683934356, Label: ECG, Data: 0.19370658167173713
        Patient ID: 1, Timestamp: 1716683934356, Label: Saturation, Data: 99.0%
         */
        // Retrieve records for patient ID 1
        List<PatientRecord> fileRecords = storage.getRecords(1,1716683934356L ,1716683934356L);

        // Verify records
        assertEquals(2, fileRecords.size()); // Ensure correct number of records

        // Iterate over each record and make assertions
        for (PatientRecord record : fileRecords) {
            if (record.getRecordType().equals("ECG")) {
                assertEquals(1, record.getPatientId());
                assertEquals("ECG", record.getRecordType());
                assertEquals(0.19370658167173713, record.getMeasurementValue());
            } else if (record.getRecordType().equals("Saturation")) {
                assertEquals(1, record.getPatientId());
                assertEquals("Saturation", record.getRecordType());
                assertEquals(99.0, record.getMeasurementValue());
            }
        }
    }

}
