package model.Server;


import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public static void main(String[] args) {
        /*
         * if (args.length < 1) {
         * System.out.println("Syntax: java ChatServer <port-number>"); System.exit(0);
         * }
         */

        int port = 8989;// Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute();
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently
     * connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}