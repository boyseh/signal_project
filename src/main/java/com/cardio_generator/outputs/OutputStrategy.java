package com.cardio_generator.outputs;

/**
 * Interface which defines output strategies for a patient
 * 
 * @author Jack
 */

public interface OutputStrategy {
    
    /**
     * Function for outputting patient data
     * 
     * @param patientId the patients ID
     * @param timestamp the timestamp when the data was generated
     * @param label the data type
     * @param data the data that is being output
     */
    void output(int patientId, long timestamp, String label, String data);
}
