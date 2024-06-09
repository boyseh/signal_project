package data_management;

import com.data_management.DataStorage;
import com.data_management.MyWebSocketClient;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class MyWebSocketClientTest {

    private DataStorage dataStorage;
    private MyWebSocketClient webSocketClient;
    
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws Exception {
        dataStorage = mock(DataStorage.class);
        webSocketClient = new MyWebSocketClient(new URI("ws://localhost:8080"), dataStorage);
         
        // Set up the output stream to capture System.out prints
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testOnOpen() {
        
        ServerHandshake handshake = mock(ServerHandshake.class);
        webSocketClient.onOpen(handshake);
        // Ensure no exceptions are thrown and a proper message is logged
        System.setOut(originalOut);
        //Check if output is as expected
        assertTrue(outContent.toString().contains("Connected to server"));
        
    }

    @Test
    void testOnMessageValidData() {
        String message = "Patient ID: 1, Timestamp: 1700000000000, Label: ECG, Data: 0.19370658167173713";
        webSocketClient.onMessage(message);

        verify(dataStorage, times(1)).addPatientData(eq(1), eq(0.19370658167173713), eq("ECG"), eq(1700000000000L));
    }

    @Test
    void testOnMessageInvalidData() {
        String message = "Invalid data format";
        webSocketClient.onMessage(message);

        // Ensure no data is added for invalid messages
        verify(dataStorage, times(0)).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    void testOnClose() {
        webSocketClient.onClose(1000, "Normal closure", true);
        // Ensure no exceptions are thrown and a proper message is logged

        webSocketClient.onClose(1001, "Unexpected closure", true);
        // Simulate reconnection logic and ensure no exceptions are thrown
    }

    @Test
    void testOnError() {
        
        // Set up the output stream to capture System.err prints
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outErr));
        
      
        Exception exception = new Exception("Test exception");
        webSocketClient.onError(exception);
        
        
   
        System.setErr(originalErr);
        //Check if output is as expected
        assertTrue(outContent.toString().contains("WebSocket error"));
    }
}