package com.company.server;

import com.company.client.ChatClient;
import com.company.data.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    public static void main(String[] args) {
        new ChatServer(9399);
    }

    public ChatServer(int port){

        // this function initialises code with mock data.
        // in the real world this function may not be needed.
        Data.init();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ChatClient chatClient = new ChatClient(clientSocket);
                chatClient.setDaemon(true);
                chatClient.start();
            }
        } catch (IOException e) {
            System.out.println(e);

        }
    }
}
