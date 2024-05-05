/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Jack
 */
public class FileDataReader implements DataReader {

    private String directoryPath;

    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Is not a directory");
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                parseData(file, dataStorage);
            }
        }

    }

    public void parseData(File file, DataStorage dataStorage) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String l;

            while ((l = br.readLine()) != null) {
                String[] tmp = l.split(",");
                int patientID = Integer.parseInt(tmp[0].substring(11).trim());
                long timestamp = Long.parseLong(tmp[1].substring(11).trim());
                String recordType = tmp[2].substring(8).trim();
                
                
                if(file.equals("Alert")){
                String data = tmp[3].substring(7).trim();
               
                        }else{
                     double data = Double.parseDouble(tmp[3].substring(7).trim());
                     dataStorage.addPatientData(patientID, data, recordType, timestamp);
                }

            }
        }
    }

}
