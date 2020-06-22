package io.bhagat.streams.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{

    private ServerSocket serverSocket;
    private ArrayList<ServerThread> threads;

    private int port;
    private int backlog;

    public Server(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        threads = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port, backlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Server started on port " + port);
        while(true) {
            try {
                Socket s = serverSocket.accept();
//                ServerThread serverThread = new ServerThread(s, this);
//                threads.add(serverThread);
            } catch(IOException e) {
                e.printStackTrace();
            }

        }
    }

    public int getPort() {
        return port;
    }

    public int getBacklog() {
        return backlog;
    }

    public class ServerThread extends Thread {

        private Socket connection;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private Object readObject;

        private volatile boolean stop;

    }

    public class ConnectionIndex {

        private Object object;
        private Socket connection;
        private String hostname;
        private String host;

        private ServerThread parent;
        private Server parentServer;

        public ConnectionIndex(Socket connection, ServerThread parent, Server parentServer) {
            this.connection = connection;
            hostname = connection.getInetAddress().getHostName();
            host = connection.getInetAddress().getHostAddress();
            this.parent = parent;
            this.parentServer = parentServer;
        }

        public int getIndex() {
            return threads.indexOf(parent);
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Socket getConnection() {
            return connection;
        }

        public String getHostname() {
            return hostname;
        }

        public String getHost() {
            return host;
        }

        public ServerThread getParent() {
            return parent;
        }

        public Server getParentServer() {
            return parentServer;
        }
    }

}
