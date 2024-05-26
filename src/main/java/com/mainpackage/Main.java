/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mainpackage;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import java.io.IOException;

/**
 *
 * @author Jack
 */
public class Main {


     public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(new String[]{});
        }
    
    
}
    
}
