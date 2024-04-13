package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating patient data
 * @author Jack
 */

public interface PatientDataGenerator {
    /**
     * Generates data for the given patient ID
     * 
     * @param patientId the corresponding patient ID
     * @param outputStrategy the output strategy where the data will be directed to
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
