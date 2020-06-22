package io.bhagat.streams;

import io.bhagat.streams.server.Server;

public class Main {

    public static void main(String[] args) {
         /* Server Testing */
        Server server = new Server(8000, 100);
        server.start();

    }
}
