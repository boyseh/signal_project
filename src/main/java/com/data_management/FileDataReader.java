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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    private String directoryPath;

    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }
    
   /**
 * Reads data from all .txt files in the specified directory and stores it in the provided data storage.
 * The files are expected to have lines in the format:
 * "Patient ID: ..., Timestamp: ..., Label: ..., Data:..."
 * If the measurement value contains a '%', it will be removed before parsing.
 *
 * @param dataStorage the storage where the parsed data will be stored
 * @throws IOException if an I/O error occurs while reading the files or if the specified path is not a directory
 */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            throw new IOException("The provided path is not a directory: " + directoryPath);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) {
            throw new IOException("Failed to list files in directory: " + directoryPath);
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String l;
                while ((l = br.readLine()) != null) {
                    System.out.println(l);

                    String[] tmp = l.split(", ");
                    try {
                        // File structure: Patient ID:..., Timestamp:..., Label(Type):..., Data(measurementValue):...
                        int patientID = Integer.parseInt(tmp[0].split(": ")[1]);
                        long timestamp = Long.parseLong(tmp[1].split(": ")[1]);
                        String recordType = tmp[2].split(": ")[1];
                        String measurementValueString = tmp[3].split(": ")[1];

                        if (measurementValueString.contains("%")) {
                            measurementValueString = measurementValueString.substring(0, measurementValueString.length() - 1);
                        }
                        double measurementValue = Double.parseDouble(measurementValueString);
                        dataStorage.addPatientData(patientID, measurementValue, recordType, timestamp);
                    } catch (NumberFormatException ex) {
                        System.out.println("Error parsing line: " + ex.getMessage());
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error reading file: " + file.getAbsolutePath());
            }
        }
    }
}
