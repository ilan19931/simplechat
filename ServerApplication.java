package com.simplechat;

import com.simplechat.Client.ClientDescriptor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApplication {

    public static int ServerPort = 1300;

    public static void main(String args[]) {

        ServerSocket server = null;
        MessageBoard mb = new MessageBoard();

        try {
            server = new ServerSocket(ServerPort, 5);

        } catch (IOException e) {

        }

        ClientDescriptor client = null;
        ConnectionProxy connection = null;

        while(true)
        {
            try {
                System.out.println("server listening...");
                Socket socket = server.accept();
                System.out.println("new connection...");
                connection = new ConnectionProxy(socket);
                client = new ClientDescriptor();
                connection.addConsumer(client);
                client.addConsumer(mb);
                mb.addConsumer(connection);

                connection.start();

            } catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
                break;
            }
        }

        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
