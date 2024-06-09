/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package alerts.factory;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Jack
 */
public class AlertFactoryTest {

    @Test
    public void testForBloodOxygenAlertFactory() {
        // Create a factory instance for blood oxygen alerts
        AlertFactory factory = new BloodOxygenAlertFactory();

        // Call the factory method to create an alert
        long timestamp = System.currentTimeMillis();
        Alert mockAlert = factory.createAlert("1", "Low Oxygen Saturation", timestamp);

        // Verify that the created alert matches the expected properties
        Assert.assertNotNull(mockAlert);
        Assert.assertEquals("1", mockAlert.getPatientId());
        Assert.assertEquals("Low Oxygen Saturation", mockAlert.getCondition());
        Assert.assertEquals(timestamp, mockAlert.getTimestamp());
    }

    @Test
    public void testForBloodPressureAlertFactory() {
        // Create a factory instance for blood pressure alerts
        AlertFactory factory = new BloodPressureAlertFactory();

        // Call the factory method to create an alert
        long timestamp = System.currentTimeMillis();
        Alert mockAlert = factory.createAlert("2", "High Blood Pressure", timestamp);

        // Verify that the created alert matches the expected properties
        Assert.assertNotNull(mockAlert);
        Assert.assertEquals("2", mockAlert.getPatientId());
        Assert.assertEquals("High Blood Pressure", mockAlert.getCondition());
        Assert.assertEquals(timestamp, mockAlert.getTimestamp());
    }
    
    @Test 
      public void testForECGAlertFactory(){
        // Create a factory instance for ECG alerts
        AlertFactory factory = new ECGAlertFactory();

        // Call the factory method to create an alert
        long timestamp = System.currentTimeMillis();
        Alert mockAlert = factory.createAlert("3", "Irregular Heart Rate", timestamp);

        // Verify that the created alert matches the expected properties
        Assert.assertNotNull(mockAlert);
        Assert.assertEquals("3", mockAlert.getPatientId());
        Assert.assertEquals("Irregular Heart Rate", mockAlert.getCondition());
        Assert.assertEquals(timestamp, mockAlert.getTimestamp());
    }



}
