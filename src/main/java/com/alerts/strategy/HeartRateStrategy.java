/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 *
 * @author Jack
 */
public class HeartRateStrategy extends AlertGenerator implements AlertStrategy {

    private DataStorage dataStorage;
    private static final long HOUR_INTERVAL_MS = 3600000;

    public HeartRateStrategy(DataStorage dataStorage) {
        super(dataStorage);
        this.dataStorage = dataStorage;
    }

    /**
     * Checks for irregularities in the patients heart rate
     *
     * @param patient
     */
    @Override
    public void checkAlert(Patient patient) {
        long startTime = System.currentTimeMillis() - HOUR_INTERVAL_MS;
        long endTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientID(), startTime, endTime);

        if (records.isEmpty()) {
            System.out.println("No records found for the specified time window.");
            return;
        }

        List<PatientRecord> ECGRecords = getRecordsByType(records, "ECG");

        checkIrregularHeartRate(ECGRecords, patient);
        criticalHeartRateAlert(records, patient);
    }

  
    private void checkIrregularHeartRate(List<PatientRecord> records, Patient patient) {
        int windowSize = 5;
        double thresholdMultiplier = 1.5;

        if (records.size() >= windowSize) {
            for (int i = 0; i <= records.size() - windowSize; i++) {
                double windowSum = 0;
                for (int j = i; j < i + windowSize; j++) {
                    windowSum += records.get(j).getMeasurementValue();
                }

                double windowAvg = windowSum / windowSize;
                double threshold = windowAvg * thresholdMultiplier;

                if (i + windowSize < records.size()) {
                    double nextPoint = records.get(i + windowSize).getMeasurementValue();
                    if (nextPoint > threshold) {
                        Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Irregular Heart Rate Alert", records.get(i).getTimestamp());
                        triggerAlert(alert);
                    }
                }
            }
        }
    }

    private void criticalHeartRateAlert(List<PatientRecord> records, Patient patient) {
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < 50 || record.getMeasurementValue() > 100) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Critical Heart Rate Alert", record.getTimestamp());
                triggerAlert(alert);
            }
        }
    }
}
