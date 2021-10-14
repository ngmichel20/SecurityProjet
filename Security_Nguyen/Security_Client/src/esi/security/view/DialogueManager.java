package esi.security.view;

import esi.security.network.Client;

/**
 * This class manages all the dialogue with the client.
 *
 * @author Nguyen Khanh-Michel
 */
public class DialogueManager {

    private final Client client;
    private final ConsoleClient console;

    /**
     * This is the contructor of the class
     */
    public DialogueManager() {
        client = Client.getInstance();
        console = new ConsoleClient();
    }

    /**
     * This mehod allows to start the client's console that will communicate
     * with the server.
     */
    public void start() {
        String cmd = console.getCommande();
        while (!cmd.equals("disconnect")) {
            switch (cmd) {
                case "registration":
                    registration();
                    break;
                case "connect":
                    connect();
                    break;
                case "disconnect":
                    disconnect();
                    break;
                case "download":
                    downloadFile();
                    break;
                case "upload":
                    uploadFile();
                    break;
                case "help":
                    console.printHelp();
                    break;
                case "delete":
                    deleteFile();
                    break;
                case "delete user":
                    deleteUser();
                    break;
            }
            System.out.println("==========================================================================================\n");
            cmd = console.getCommande();
        }
        disconnect();
    }

    private void registration() {
        String tab[] = console.getRegistrationParam();
        if (client.registration(tab[1], tab[0])) {
            System.out.println("you are registered");
        } else {
            System.out.println("the operation could not be done");
        }
    }

    private void connect() {
        String st[] = console.getConnectParam();
        if (client.connect(st[1], st[0])) {
            System.out.println("connected");
        } else {
            System.out.println("could not connect");
        }
    }

    private void disconnect() {
        client.disconnect();
    }
    
    private void downloadFile() {
        String pr[] = console.downloadFileParam();
        client.downloadFile(pr[0], pr[1]);
    }

    private void uploadFile() {
        String name = console.uploadFileParam();
        client.uploadFile(name);
    }

    
    private void deleteFile() {
        String fileName = console.deleteFileParam();
        client.delete(fileName);
    }

    private void deleteUser() {
        String userToDelete = console.getUserToDelete();
        client.deleteUser(userToDelete);
    }
}
