package com.alerts;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts;

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
         this.triggeredAlerts = new ArrayList<>();
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
        int systolicBP = patient.getSystolicBloodPressure();
        int previousSystolicBP = patient.getPreviousSystolicBP();

        int diastolicBP = patient.getDiastolicBloodPressure();
        int previousDiastolicBP = patient.getPreviousDiastolicBP();

        int saturation = patient.getOxygenSaturation();
        int previousSaturation = patient.getPreviousSaturartion();
        long previousSaturationTime = patient.getPreviousSaturationTime();

        double ecgValue = patient.getECGValue();
        double previousECGValue = patient.getPreviousECGValue();

        String patientId = patient.getPatientId();
        long currentTime = System.currentTimeMillis();

        // Trigger threshold alert for high/low blood pressure
        if (generateTresholdAlert(systolicBP, diastolicBP)) {
            triggerAlert(new Alert(patientId, "Critical Treshold Alert", currentTime));
        }

        // Trigger trend alert for blood pressure trend
        if (generateTrendAlert(patient, systolicBP, previousSystolicBP, diastolicBP, previousDiastolicBP)) {
            triggerAlert(new Alert(patientId, "Trend Alert", currentTime));
        }

        // Trigger low saturation alert
        if (generateLowSaturationAlert(saturation)) {
            triggerAlert(new Alert(patientId, "Low Saturation Alert", currentTime));
        }

        // Trigger rapid drop alert for oxygen saturation
        if (generateRapidDropAlert(patient, saturation, previousSaturation, currentTime, previousSaturationTime)) {
            triggerAlert(new Alert(patientId, "Rapid Drop Alert", currentTime));
        }

        // Trigger hypotensive hypoxemia alert
        if (generateHypotensiveHypoxemiaAlert(systolicBP, saturation)) {
            triggerAlert(new Alert(patientId, "Hypotensive Hypoxemia Alert", currentTime));
        }

        // Trigger abnormal heart rate alert
        if (abnormalHeartRateAlert(ecgValue)) {
            triggerAlert(new Alert(patientId, "Abnormal Heart Rate Alert", currentTime));
        }
        
        // Trigger irregular heart beat alert
        if (irregularBeatAlert(patient, ecgValue, previousECGValue)) {
            triggerAlert(new Alert(patientId, "Irregular Heart Beat Alert", currentTime));
        }
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
        System.out.println("Alert logged: " + alert.toString());
        System.out.println("Alert notification sent to medical staff: " + alert.toString());
        triggeredAlerts.add(alert);

    }
    
      public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    private boolean generateTrendAlert(Patient patient, int systolicBP, int previousSystolicBP, int diastolicBP, int previousDiastolicBP) {
        // Check for trend in systolic blood pressure
        int systolicCount = patient.getSystolicCount();
        int diastolicCount = patient.getDiastolicCount();

        if (Math.abs(previousSystolicBP - systolicBP) > 10) {
            systolicCount++;
            patient.setPreviousSystolicBP(systolicBP);
            if (systolicCount >= 3) {
                // Reset count after triggering the alert
                patient.setSystolicCount(0);
                return true;
            }
        } else {
            patient.setPreviousSystolicBP(systolicBP);
            patient.setSystolicCount(0);
        }

        // Check for trend in diastolic blood pressure
        if (Math.abs(previousDiastolicBP - diastolicBP) > 10) {
            diastolicCount++;
            patient.setPreviousDiastolicBP(diastolicBP);
            if (diastolicCount >= 3) {
                // Reset count after triggering the alert
                patient.setDiastolicCount(0);
                return true;
            }
        } else {
            patient.setPreviousDiastolicBP(diastolicBP);
            patient.setDiastolicCount(0);
        }

        // Return false if neither systolic nor diastolic trend alert conditions are met
        return false;
    }

    private boolean generateTresholdAlert(int systolicBP, int diastolicBP) {

        return (systolicBP > 180 || systolicBP < 90 || diastolicBP > 120 || diastolicBP < 60);

    }

    private boolean generateLowSaturationAlert(int saturation) {
        return (saturation < 92);
    }

    private boolean generateRapidDropAlert(Patient patient, int saturation, int previousSaturation, long currentTime, long previousSaturationTime) {

        // Rapid Drop Alert
        if (previousSaturation - saturation >= 5
                && System.currentTimeMillis() - previousSaturationTime <= 10 * 60 * 1000) {
            // Update previous saturation value
            patient.setPreviousSaturationTime(currentTime);
            patient.setPreviousSaturation(saturation);
            return true;

        } else {
            // Update previous saturation value
            patient.setPreviousSaturationTime(currentTime);
            patient.setPreviousSaturation(saturation);
            return false;
        }

        
    }

    private boolean generateHypotensiveHypoxemiaAlert(int systolicBP, int saturation) {

        return (systolicBP < 90 && saturation < 92);

    }

    private boolean abnormalHeartRateAlert(double ecgValue) {

        return (ecgValue < 50 || ecgValue > 100);

    }

    private boolean irregularBeatAlert(Patient patient, double ecgValue, double previousEcgValue) {

        
        double ecgVariation = Math.abs(ecgValue - previousEcgValue);
        if (ecgVariation > 0.1) { 
            
            patient.setPreviousECGValue(ecgValue);
            return true;
        } else {
            patient.setPreviousECGValue(ecgValue);
            return false;
        }

    }
}