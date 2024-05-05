/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data_management;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.outputs.OutputStrategy;
import org.junit.Before;
import org.junit.Test;

public class AlertGeneratorTest {
    private AlertGenerator alertGenerator;
    private OutputStrategy outputStrategy;

    @Before
     public void setUp() {
        // Create mock generator classes for blood pressure and saturation data
        BloodPressureDataGenerator bloodPressureGenerator = mock(BloodPressureDataGenerator.class);
        BloodSaturationDataGenerator bloodSaturationGenerator = mock(BloodSaturationDataGenerator.class);

        // Configure mock behavior to return specific values for consecutive readings
        // For simplicity, let's assume a difference of 10 mmHg for blood pressure and 5% for saturation
        when(bloodPressureGenerator.getLastSystolicValue(1)).thenReturn(120, 130, 140); // Difference of 10 mmHg
        when(bloodPressureGenerator.getLastDiastolicValue(1)).thenReturn(80, 90, 100); // Difference of 10 mmHg
        when(bloodSaturationGenerator.getLastSaturationValue(1)).thenReturn(95, 90, 85); // Difference of -5%

        // Create AlertGenerator instance with mock generators
        alertGenerator = new AlertGenerator(1, bloodPressureGenerator, bloodSaturationGenerator);

        outputStrategy = mock(OutputStrategy.class); // Mock OutputStrategy
    }

    @Test
    public void testIncreasingTrendAlert() {
        // Simulate three consecutive increasing blood pressure readings
        alertGenerator.generate(1, outputStrategy); // Generate first reading
        alertGenerator.generate(1, outputStrategy); // Generate second reading
        alertGenerator.generate(1, outputStrategy); // Generate third reading

        // Verify that the Trend Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Trend Alert", "triggered");
    }

    @Test
    public void testDecreasingTrendAlert() {
        // Simulate three consecutive decreasing blood pressure readings
        // You need to adjust the generated values based on your data range
        // For simplicity, we'll just decrement the values by more than 10 mmHg each
        alertGenerator.generate(1, outputStrategy); // Generate first reading
        alertGenerator.generate(1, outputStrategy); // Generate second reading
        alertGenerator.generate(1, outputStrategy); // Generate third reading

        // Verify that the Trend Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Trend Alert", "triggered");
    }

    @Test
    public void testCriticalThresholdsAlert() {
        // Simulate critical systolic and diastolic blood pressure readings
        // You need to adjust the values based on your critical thresholds
        alertGenerator.generate(1, outputStrategy); // Generate critical reading

        // Verify that the Critical Threshold Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Critical Threshold Alert", "triggered");
    }
    
    @Test
    public void testLowSaturationAlert() {
        // Simulate low saturation reading
        // You need to adjust the value based on your threshold
        // For simplicity, let's simulate a saturation value below 92%
        // Ensure your generator returns a value below 92% for this test
        alertGenerator.generate(1, outputStrategy); // Generate low saturation reading

        // Verify that the Low Saturation Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Low Saturation Alert", "triggered");
    }

    @Test
    public void testRapidDropAlert() {
        // Simulate rapid drop in saturation
        // You need to adjust the values based on your criteria
        // For simplicity, let's simulate a drop of 5% or more within 10 minutes
        // Ensure your generator returns appropriate values for this test
        alertGenerator.generate(1, outputStrategy); // Generate rapid drop in saturation

        // Verify that the Rapid Drop Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Rapid Drop Alert", "triggered");
    }
    
    public void testAbnormalHeartRateAlert() {
        // Generate ECG data with heart rate below 50 bpm
        alertGenerator.generate(1, outputStrategy); // Heart rate below 50 bpm
        // Verify that the Abnormal Heart Rate Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Abnormal Heart Rate Alert", "triggered");

        // Generate ECG data with heart rate above 100 bpm
        alertGenerator.generate(1, outputStrategy); // Heart rate above 100 bpm
        // Verify that the Abnormal Heart Rate Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Abnormal Heart Rate Alert", "triggered");
    }

    @Test
    public void testIrregularBeatPatternsAlert() {
        // Generate ECG data with irregular beat patterns
        // For simplicity, let's assume an irregular beat if the ECG value deviates significantly from the previous value
        alertGenerator.generate(1, outputStrategy); // Generate first reading
        alertGenerator.generate(1, outputStrategy); // Generate second reading
        // Verify that the Irregular Beat Patterns Alert is triggered
        verify(outputStrategy).output(1, System.currentTimeMillis(), "Irregular Beat Patterns Alert", "triggered");
    }
}

