package com.data_management;

import java.io.IOException;
import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocketClient that reads data and stores it in an instance of the DataStorage.
 */
public class MyWebSocketClient extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    public MyWebSocketClient(URI serverUri, DataStorage ds) {
        super(serverUri);
        this.dataStorage = ds;
    }

    /**
     * Connects to the WebSocket and reads the data.
     *
     * @param dataStorage the DataStorage which stores the incoming data
     * @throws IOException thrown in case of an I/O Error
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        this.connect();
    }

    /**
     * Called when there is data incoming from the WebSocket.
     *
     * @param message the incoming message data
     */
    @Override
    public void onMessage(String message) {
        try {
            String[] tmp = message.split(",");
            if (tmp.length == 4) {
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
            } else {
                System.err.println("Invalid format: " + message);
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("Invalid data in message: " + message);
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Error processing message: " + message);
            ex.printStackTrace();
        }
    }

    /**
     * Called when the WebSocket connection opens.
     *
     * @param sh the server handshake
     */
    @Override
    public void onOpen(ServerHandshake sh) {
        System.out.println("Connected to WebSocket server.");
    }

    /**
     * Called when WebSocket connection closes.
     *
     * @param code for closing
     * @param reason for closing
     * @param remote states whether the connection was closed remotely
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed: " + reason + " (Code: " + code + ")");
        // Attempt to reconnect if the connection was closed unexpectedly
        if (code != 1000) { // 1000 indicates a normal closure
            System.out.println("Attempting to reconnect...");
            try {
                this.reconnectBlocking();
            } catch (InterruptedException e) {
                System.err.println("Reconnection attempt failed.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when an error occurs.
     *
     * @param ex the exception that occurred
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
      
            try {
                this.reconnectBlocking();
            } catch (InterruptedException e) {
                System.err.println("Reconnection attempt failed.");
                e.printStackTrace();
            }
    }
}