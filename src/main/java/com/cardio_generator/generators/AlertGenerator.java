package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates alerts for patients which can be resolved at a given probability
 * 
 * @author Jack
 */

public class AlertGenerator implements PatientDataGenerator {
    //Declaring RANDOM_GENERATOR as public static final
    public static final Random RANDOM_GENERATOR = new Random();
    //variable name --> lowerCamelCase
    private boolean[] alertStates; // false = resolved, true = pressed
    
    /**
     * Constructs the alert generator.
     * Initializes the alert states for each of the patients
     * @param patientCount 
     */

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }
    
    /**
 * Generates alerts for patients.
 * Alerts can be triggered or resolved randomly based on a predefined probability.
 * Output is directed to the specified output strategy.
 * 
 * @param patientId       The unique identifier of the patient.
 * @param outputStrategy  The output strategy for directing the generated alert data.
 */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
