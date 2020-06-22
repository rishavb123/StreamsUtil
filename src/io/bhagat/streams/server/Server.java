package io.bhagat.streams.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread{

    private ServerSocket serverSocket;
    private final ArrayList<ServerThread> threads;

    private final int port;
    private final int backlog;

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
        try {
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    ServerThread serverThread = new ServerThread(s, this);
                    threads.add(serverThread);
                    serverThread.start();
                } catch (EOFException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        for(int i = threads.size() - 1; i >= 0; i--) {
            threads.get(i).close();
        }
    }

    public int getPort() {
        return port;
    }

    public int getBacklog() {
        return backlog;
    }

    public class ServerThread extends Thread {

        private final Socket connection;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private Object readObject;

        private final ConnectionIndex connectionIndex;

        private volatile boolean stop;

        public ServerThread(Socket socket, Server parentServer) {
            stop = false;
            connection = socket;
            setupStreams();
            connectionIndex = new ConnectionIndex(connection, this, parentServer);
            System.out.println("Connection " + threads.size() + ": " + connectionIndex.getHostname() + " joined");
        }

        private void setupStreams() {
            try {
                outputStream = new ObjectOutputStream(connection.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Object read() {
            return readObject;
        }

        public void run() {
            while(!stop) {
                try {
                    synchronized (inputStream) {
                        readObject = inputStream.readObject();
                        connectionIndex.setObject(readObject);
                        System.out.println("Connection " + connectionIndex.getIndex() + ": " + readObject);
                    }
                } catch(EOFException | SocketException e) {
                    System.out.println("Connection " + connectionIndex.getIndex() + ": " + connectionIndex.getHostname() + " left");
                    close();
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(Object obj) {
            synchronized(outputStream) {
                try {
                    if(obj != null)
                        outputStream.writeObject(obj);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            stop = true;
            try {
                inputStream.close();
                outputStream.close();
                connection.close();
                threads.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ConnectionIndex getConnectionIndex() {
            return connectionIndex;
        }
    }

    public class ConnectionIndex {

        private Object object;
        private final Socket connection;
        private final String hostname;
        private final String host;

        private final ServerThread parent;
        private final Server parentServer;

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
