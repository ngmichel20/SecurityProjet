package esi.security.view;

import java.util.HashMap;
import java.util.Scanner;

/**
 * This class manages all the interactions with the server.
 *
 * @author Nguyen Khanh-Michel
 */
public class ConsoleClient {

    private Scanner clavier = new Scanner(System.in);
    private final HashMap<String, String> commandes;

    /**
     * This is the constrcutor.
     */
    public ConsoleClient() {
        clavier = new Scanner(System.in);
        commandes = new HashMap<>();
        initCommandes();
    }

    /**
     * This method allows to initialize the command list that a user can type in
     * the console.
     */
    private void initCommandes() {
        commandes.put("registration", "syntax is: registration name and then password");
        commandes.put("connect", "syntax is: connect name and then password");
        commandes.put("disconnect", "command without param");
        commandes.put("download", "syntax is: download filename and then destinationDirectory");
        commandes.put("upload", "syntax is: upload filename(absolute path)");
        commandes.put("delete", "syntax is: delete filename(file name)");
        commandes.put("help", "syntax is: help without param");
    }

    /**
     * This method allows to obtain a string character typed on the keyboard.
     *
     * @param msg the message from the client.
     * @return the string
     */
    private String getString(String msg) {
        String str;
        System.out.println("" + msg);
        str = clavier.next();
        while (str.length() < 3) {
            System.out.println("" + msg);
            str = clavier.next();
        }

        return str.toLowerCase();
    }

    /**
     * This method will be use in the user's connection.
     *
     * @return parameters of the connection
     */
    public String[] getConnectParam() {
        String tab[] = new String[2];
        tab[0] = getString("Enter your user name :");

        tab[1] = getString("Enter your password :");
        return tab;
    }

    /**
     * This method will be use in the user's registration.
     *
     * @return parameters of the registration.
     */
    public String[] getRegistrationParam() {
        return getConnectParam();
    }

    /**
     * This method will be use in the user's connection.
     *
     * @return parameters of the connection
     */
    public String getUserToDelete() {
        String userToDelete = getString("Enter the user to delete :");
        return userToDelete;
    }

    /**
     * This method will be use when the user upload a file.
     *
     * @return parameters of the upload.
     */
    public String[] downloadFileParam() {
        String tab[] = new String[2];
        tab[0] = getString("Enter the fileName :");

        tab[1] = getString("Enter the destination directory :");
        return tab;
    }

    /**
     * This method will be use when the user downloads a file.
     *
     * @return parameters of the download file.
     */
    public String uploadFileParam() {
        return getString("Enter the file name with his absolute path :");
    }

    /**
     * This method will be use when the user delete a file.
     *
     * @return parameters of the delete file.
     */
    public String deleteFileParam() {
        return getString("Enter the file name that you want to delete :");
    }

    /**
     * This method allows to the client to have all the possibilities of
     * commands that he can do. 
     *
     * @return the command he has chosen.
     */
    public String getCommande() {
        String cmd = getString("Enter a command : (registration,connect,disconnect,download,upload,delete,deleteUser,help)");
        while (!commandes.containsKey(cmd)) {
            System.out.println("Please enter a valid command");
            cmd = getString("Please enter a valid command");
        }

        return cmd;
    }

    /**
     * This method allows to print the help command.
     */
    void printHelp() {
        commandes.keySet().forEach(e -> System.out.println(e + ": " + commandes.get(e)));
    }
}
