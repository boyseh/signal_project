package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 *Class for writing patient data to files    
 * @author Jack
 */

//filenames should be in UpperCamelCase (starting letter in uppercase)
public class FileOutputStrategy implements OutputStrategy {
    
//variable names should be in lowerCamelCase (starting letter in lowercase)
    private String baseDirectory;

    //changed file_map to FILE_MAP because its a final
    public final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>();
    
    /**
     * 
     * @param baseDirectory directory where the outputs will be stored
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }
    
    //see interface for description
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        //variable names lowerCamelCase
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}