package io.bhagat.streams.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Client extends Thread {

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private Socket connection;

    private Object readObject;

    private boolean stop;

    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        stop = false;
        try {
            connection = new Socket(host, port);
            setupStreams();
            System.out.println("Connected to " + host + ": " + port);
        } catch (IOException e) {
            System.out.println("Could not connect to Server!");
        }
    }

    private void setupStreams() throws IOException
    {
        inputStream = new ObjectInputStream(connection.getInputStream());
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
    }

    public void run() {
        try {
            while(!stop) {
                synchronized (inputStream) {
                    readObject = inputStream.readObject();
                }
            }
        } catch (EOFException | SocketException e) {
            System.out.println("Terminated Connection");
            close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            stop = true;
            System.out.println("Closing Client");
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object obj) {
        synchronized (outputStream) {
            try {
                if(obj != null)
                    outputStream.writeObject(obj);
                outputStream.flush();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object read() {
        return readObject;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
