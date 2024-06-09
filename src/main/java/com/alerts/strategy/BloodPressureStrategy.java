/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alerts.strategy;

import com.alerts.Alert;
import static com.alerts.AlertGenerator.DAY_INTERVAL_MS;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jack
 */
public class BloodPressureStrategy extends AlertGenerator implements AlertStrategy{
    private DataStorage dataStorage;


    public BloodPressureStrategy(DataStorage dataStorage) {
        super(dataStorage);
        this.dataStorage = dataStorage;
    }
    /**
     * Makes all the blood pressure evaluations to check if any alerts need to
     * be triggered. Checks for increasing or decreasing trend and if a
     * threshold has been exceeded.
     *
     * @param patient to be evaluated
     */

    @Override
    public void checkAlert(Patient patient) {
        //Get all of the patient records
        long startTime = System.currentTimeMillis() - DAY_INTERVAL_MS; //check records of day for trend check
        long endTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientID(), startTime, endTime);

        // Check if records are available
        if (records.isEmpty()) {
            System.out.println("No records found for the specified time window.");
            return;
        }

        //Filter out the systolic and diastolic blood pressures
        List<PatientRecord> systolicRecords = getRecordsByType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = getRecordsByType(records, "DiastolicPressure");

        //Call all blood pressure checks
        //Check for an increase/decrease in patients blood pressure accross 3 consecutive readings
        trendCheck(systolicRecords, patient, "Systolic Blood Pressure");
        trendCheck(diastolicRecords, patient, "Diastolic Blood Pressure");

        //Check for a reading exceeding or dropping below a given threshold
        criticalTresholdCheck(systolicRecords, patient, "Systolic Blood Pressure", 90, 180);
        criticalTresholdCheck(diastolicRecords, patient, "Diastolic Blood Pressure", 60, 120);
        
    }


    private void trendCheck(List<PatientRecord> records, Patient patient, String type) {
        if (records.size() >= 3) {
            boolean decreasingTrend = true;
            boolean increasingTrend = true;

            for (int i = 0; i < records.size() - 1; i++) {
                double currentValue = records.get(i).getMeasurementValue();
                double nextValue = records.get(i + 1).getMeasurementValue();

                if (currentValue - nextValue <= 10) {
                    decreasingTrend = false;
                }
                if (nextValue - currentValue <= 10) {
                    increasingTrend = false;
                }
            }

            String patientID = Integer.toString(patient.getPatientID());
            if (decreasingTrend == true) {
                Alert alert = new Alert(patientID, type + " Decreasing Trend Alert", records.get(records.size() - 1).getTimestamp());
                triggerAlert(alert);
            }
            if (increasingTrend == true) {
                Alert alert = new Alert(patientID, type + " Increasing Trend Alert", records.get(records.size() - 1).getTimestamp());
                triggerAlert(alert);
            }
        }
    }

    private void criticalTresholdCheck(List<PatientRecord> records, Patient patient, String type, int lowerTreshold, int upperThreshold) {

        for (PatientRecord record : records) {

            if (record.getMeasurementValue() > upperThreshold) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), type + " Critical High Alert: " + type + " has exceeded " + upperThreshold + " mmHg", record.getTimestamp());
                triggerAlert(alert);
            }

            if (record.getMeasurementValue() < lowerTreshold) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), type + " Critical Low Alert: " + type + " has dropped below " + lowerTreshold + " mmHg", record.getTimestamp());
                triggerAlert(alert);
            }
        }

    }



}
