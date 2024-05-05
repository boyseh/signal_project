package com.data_management;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient and manages their medical records. This class stores
 * patient-specific data, allowing for the addition and retrieval of medical
 * records based on specified criteria.
 */
public class Patient {

    private int patientId;
    private List<PatientRecord> patientRecords;
    public int systolicCount;
    public int diastolicCount;

    /**
     * Constructs a new Patient with a specified ID. Initializes an empty list
     * of patient records.
     *
     * @param patientId the unique identifier for the patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
        this.systolicCount = 0;
        this.diastolicCount = 0;
    }

    /**
     * Adds a new record to this patient's list of medical records. The record
     * is created with the specified measurement value, record type, and
     * timestamp.
     *
     * @param measurementValue the measurement value to store in the record
     * @param recordType the type of record, e.g., "HeartRate", "BloodPressure"
     * @param timestamp the time at which the measurement was taken, in
     * milliseconds since UNIX epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall
     * within a specified time range. The method filters records based on the
     * start and end times provided.
     *
     * @param startTime the start of the time range, in milliseconds since UNIX
     * epoch
     * @param endTime the end of the time range, in milliseconds since UNIX
     * epoch
     * @return a list of PatientRecord objects that fall within the specified
     * time range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : patientRecords) {
            if (record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }

    public int getSystolicBloodPressure() {
        List<PatientRecord> records = getRecords(System.currentTimeMillis(), System.currentTimeMillis() + 100);
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) {
                return (int) record.getMeasurementValue();
            }

        }
        return -1;
    }

    public int getPreviousSystolicBP() {

    }

    public void setPreviousSystolicBP(int systolicBP) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getDiastolicBloodPressure() {
        List<PatientRecord> records = getRecords(System.currentTimeMillis(), System.currentTimeMillis() + 100);
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("DiastolicPressure")) {
                return (int) record.getMeasurementValue();
            }

        }
        return -1;
    }

    public int getPreviousDiastolicBP() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPreviousDiastolicBP(int diastolicBP) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getSystolicCount() {
        return systolicCount;
    }

    public int getDiastolicCount() {
        return diastolicCount;
    }

    public void setSystolicCount(int count) {
        systolicCount = count;
    }

    public void setDiastolicCount(int count) {
        diastolicCount = count;
    }

    public int getOxygenSaturation() {
        List<PatientRecord> records = getRecords(System.currentTimeMillis(), System.currentTimeMillis() + 100);
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                return (int) record.getMeasurementValue();
            }

        }
        return -1;
    }

    public int getPreviousSaturartion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPreviousSaturation(int saturation) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getPreviousSaturationTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPreviousSaturationTime(long currentTime) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public double getECGValue() {
        List<PatientRecord> records = getRecords(System.currentTimeMillis(), System.currentTimeMillis() + 100);
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG")) {
                return (int) record.getMeasurementValue();
            }

        }
        return -1;
    }

    public int getPreviousECGValue() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPreviousECGValue(double ecgValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getPatientId() {
        return patientId + "";
    }

}
