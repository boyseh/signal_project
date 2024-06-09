package com.alerts.decorator;

import com.alerts.Alert;



public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " (Repeated " + repeatCount + " times)";
    }

    // Method to simulate the repeated check
    public void repeatCheck() {
        for (int i = 0; i < repeatCount; i++) {
            // Simulate a delay for the repeated check
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Rechecking alert: " + getCondition() + " at " + System.currentTimeMillis());
        }
    }
}
