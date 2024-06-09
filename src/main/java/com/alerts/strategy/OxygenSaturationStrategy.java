package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class OxygenSaturationStrategy extends AlertGenerator implements AlertStrategy  {

   private DataStorage dataStorage;
   private static final long HOUR_INTERVAL_MS = 3600000;
   public OxygenSaturationStrategy(DataStorage dataStorage) {
       super(dataStorage);
       this.dataStorage = dataStorage;
   }

    @Override
    public void checkAlert(Patient patient) {

        long startTime = System.currentTimeMillis() - HOUR_INTERVAL_MS / 6; //past ten minutes for rapid drop check
        long endTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientID(), startTime, endTime);

        if (records.isEmpty()) {
            System.out.println("No records found for the specified time window.");
            return;
        }

        List<PatientRecord> saturationRecords = getRecordsByType(records, "Saturation");

        lowSaturationCheck(saturationRecords, patient);
        rapidDropCheck(saturationRecords, patient);
    }

    private void lowSaturationCheck(List<PatientRecord> records, Patient patient) {
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < 92) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Low Saturation Alert", record.getTimestamp());
                triggerAlert(alert);
            }
        }
    }

    private void rapidDropCheck(List<PatientRecord> records, Patient patient) {

        for (int i = 1; i < records.size(); i++) {
            double dropPercentage = 100.0 * (records.get(i - 1).getMeasurementValue() - records.get(i).getMeasurementValue()) / records.get(i - 1).getMeasurementValue();
            if (dropPercentage >= 5) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Saturation Rapid Drop Alert", records.get(i).getTimestamp());
                triggerAlert(alert);

            }
        }

    }
   
}
