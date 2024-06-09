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
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyWebSocketClientTest {

    private MyWebSocketClient webSocketClient;
    private DataStorage dataStorage;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws URISyntaxException, InterruptedException {
        dataStorage = mock(DataStorage.class);
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                webSocketClient = new MyWebSocketClient(new URI("ws://localhost:8080"), dataStorage);
                webSocketClient.connect();
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @AfterEach
    void restoreStreams() {
        // Restore the original System.out stream
        System.setOut(originalOut);
    }

    @Test
    void testOnOpen() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        ServerHandshake handshake = mock(ServerHandshake.class);
        webSocketClient.onOpen(handshake);

        System.setOut(originalOut);

        assertTrue(outContent.toString().contains("Connected to WebSocket server."));
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

        verify(dataStorage, times(0)).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    void testOnClose() {
       
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        // Call the onClose method with normal closure
        webSocketClient.onClose(1000, "Normal closure", true);
        assertFalse(webSocketClient.isOpen());

      
        System.setOut(originalOut);

        assertTrue(outContent.toString().contains("WebSocket connection closed: Normal closure (Code: 1000)"), "Expected closure message not found in the output");

    }

   @Test
void testOnError() throws InterruptedException {
    // Set up streams to capture System.err
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(errContent));

    // Use a CountDownLatch to wait for the reconnect attempt
    CountDownLatch latch = new CountDownLatch(1);

    // Wrap the webSocketClient to spy on the reconnect method
    MyWebSocketClient spyClient = spy(webSocketClient);

    // Simulate the reconnection attempt
    doAnswer(invocation -> {
        latch.countDown(); // Signal the latch when reconnect is called
        return null;
    }).when(spyClient).reconnectBlocking();

    // Call the onError method with an exception
    Exception ex = new Exception("Test exception");
    spyClient.onError(ex);

    // Wait for the reconnect attempt
    assertTrue(latch.await(5, TimeUnit.SECONDS), "Reconnection attempt was not made");

    // Restore the original System.err
    System.setErr(originalErr);

    // Verify the expected error messages
    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("WebSocket error: Test exception"), "Expected error message 'Test exception' not found in System.err");
    assertTrue(errOutput.contains("Connection refused: connect"), "Expected error message 'Connection refused: connect' not found in System.err");
}

}
