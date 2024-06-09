package com.alerts.decorator;

import com.alerts.Alert;



public class PriorityAlertDecorator extends AlertDecorator {
    private String priority;

    public PriorityAlertDecorator(Alert decoratedAlert, String priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " - Priority: " + priority;
    }
}
