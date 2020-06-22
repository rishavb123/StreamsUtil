package io.bhagat.streams;

import io.bhagat.streams.server.Client;

import java.util.Scanner;

public class SecondaryMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client("10.0.0.66", 8000);
        client.start();
        String msg;
        while(!(msg = scanner.nextLine()).equals("exit") && client.isAlive()) {
            client.send(msg);
        }
        client.close();
        scanner.close();
    }

}
