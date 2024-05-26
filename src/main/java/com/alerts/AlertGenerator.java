package com.alerts;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private DataStorage dataStorage;
    private static final long TIME_WINDOW_MS = 100000;// 100 seconds, configurable
    private static final long HOUR_INTERVAL_MS = 3600000;
    private static final long DAY_INTERVAL_MS = 86400000;
    

    /**
     * Constructs an {@code AlertGenerator} with a specified
     * {@code DataStorage}. The {@code DataStorage} is used to retrieve patient
     * data that this class will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to
     * patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;

    }

    /**
     * Evaluates the specified patient's data to determine if any alert
     * conditions are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method. This method should define the specific
     * conditions under which an alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            throw new NullPointerException("No patient data");
        }

        //Call all the methods to evaluate patient data
        bloodPressureEvaluations(patient);
        bloodSaturationEvaluations(patient);
        hypotensiveHypoxemiaCheck(patient);
        ECGCheck(patient);

    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended
     * to notify medical staff, log the alert, or perform other actions. The
     * method currently assumes that the alert information is fully formed when
     * passed as an argument.
     *
     * @param alert the alert object containing details about the alert
     * condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.println("Patient ID: " + alert.getPatientId()
                + ", Condition: " + alert.getCondition()
                + ", Timestamp: " + alert.getTimestamp());

    }

    /**
     * Makes all the blood pressure evaluations to check if any alerts need to be triggered.
     * Checks for increasing or decreasing trend and if a threshold has been exceeded.
     *
     * @param patient to be evaluated
     */
    private void bloodPressureEvaluations(Patient patient) {

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
            boolean increasing = true;
            boolean decreasing = true;

            for (int i = 0; i < records.size() - 1; i++) {
                double currentValue = records.get(i).getMeasurementValue();
                double nextValue = records.get(i + 1).getMeasurementValue();

                if (currentValue - nextValue <= 10) {
                    increasing = false;
                }
                if (nextValue - currentValue <= 10) {
                    decreasing = false;
                }
            }

            String patientID = Integer.toString(patient.getPatientID());
            if (increasing) {
                Alert alert = new Alert(patientID, type + " Increasing Trend Alert", records.get(records.size() - 1).getTimestamp());
                triggerAlert(alert);
            }
            if (decreasing) {
                triggerAlert(new Alert(patientID, type + " Decreasing Trend Alert", records.get(records.size() - 1).getTimestamp()));
            }
        }
    }

    private void criticalTresholdCheck(List<PatientRecord> records, Patient patient, String type, int lowerTreshold, int upperThreshold) {

        for (PatientRecord record : records) {

            if (record.getMeasurementValue() > upperThreshold) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), type + " has exceeded " + upperThreshold + " mmHg", record.getTimestamp());
                triggerAlert(alert);
            }

            if (record.getMeasurementValue() < lowerTreshold) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), type + " has dropped below " + lowerTreshold + " mmHg", record.getTimestamp());
                triggerAlert(alert);
            }
        }

    }

    /**
     * Evaluates the patients blood saturation records and determines if any alerts need to be triggered.
     * Checks if there is a lower than usual blood saturation and if there was a rapid drop during a given interval.
     *
     * @param patient to be evaluated
     */
    private void bloodSaturationEvaluations(Patient patient) {

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
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Low Saturation Alert%", record.getTimestamp());
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

    /**
     * Checks saturation and systolic blood pressure for a potential hypotensive hypoxemia alert.
     *
     * @param patient
     */
    private void hypotensiveHypoxemiaCheck(Patient patient) {
        long startTime = System.currentTimeMillis() - TIME_WINDOW_MS; //time window set to 100s
        long endTime = System.currentTimeMillis();
        //store records in a recent interval
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientID(), startTime, endTime);

        if (records.isEmpty()) {
            System.out.println("No records found for the specified time window.");
            return;
        }

        //retrieve the recent systolic and saturation records
        List<PatientRecord> recentSystolicRecords = getRecordsByType(records, "SystolicPressure");
        List<PatientRecord> recentSaturationRecords = getRecordsByType(records, "Saturation");

        boolean systolic = false;
        for (PatientRecord record : recentSystolicRecords) {
            if (record.getMeasurementValue() < 90) {
                systolic = true;
            }
        }
        boolean saturation = false;
        for (PatientRecord record : recentSaturationRecords) {
            if (record.getMeasurementValue() < 92) {
                saturation = true;
            }
        }

        if (saturation && systolic) {
            Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Hypotensive Hypoxemia Alert", endTime);
            triggerAlert(alert);
        }
    }

    /**
     * Checks for irregularities in the patients heart rate
     *
     * @param patient
     */
    private void ECGCheck(Patient patient) {

        long startTime = System.currentTimeMillis() - HOUR_INTERVAL_MS;
        long endTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientID(), startTime, endTime);

        if (records.isEmpty()) {
            System.out.println("No records found for the specified time window.");
            return;
        }

        List<PatientRecord> ECGRecords = getRecordsByType(records, "ECG");

        abnormalHeartRateCheck(ECGRecords, patient);

    }

    private void abnormalHeartRateCheck(List<PatientRecord> records, Patient patient) {

        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < 50 || record.getMeasurementValue() > 100) {
                Alert alert = new Alert(String.valueOf(patient.getPatientID()), "Abnormal Heart Rate Alert", record.getTimestamp());
                triggerAlert(alert);
            }
        }

        if (records.size() >= 3) {
            // Define the sliding window size
            int windowSize = 5; 
            double thresholdMultiplier = 1.5; // Define how much above the average is considered abnormal

            // Iterate through the records, starting from the window size index
            for (int i = windowSize - 1; i < records.size(); i++) {
                double sum = 0.0;
                for (int j = i - windowSize + 1; j <= i; j++) {
                    sum += records.get(j).getMeasurementValue();
                }

                // Calculate the average for the current window
                double average = sum / windowSize;
                double currentValue = records.get(i).getMeasurementValue();

                // Check if the current value exceeds the threshold
                if (currentValue > average * thresholdMultiplier) {
                    triggerAlert(new Alert(Integer.toString(patient.getPatientID()),"ECG Abnormal Data Alert",  records.get(i).getTimestamp()
                    ));
                }
            }
        }
    }

    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String type) {
        List<PatientRecord> filteredRecords = new ArrayList<PatientRecord>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equals(type)) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
}
