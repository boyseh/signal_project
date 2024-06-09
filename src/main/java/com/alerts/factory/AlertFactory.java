/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alerts.factory;

import com.alerts.Alert;

/**
 *
 * @author Jack
 */
public abstract class AlertFactory {
    
    public abstract Alert createAlert(String patientID, String condition, long timestamp);
    
}
