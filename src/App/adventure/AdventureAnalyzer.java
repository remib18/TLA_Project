package App.adventure;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class AdventureAnalyzer {

    // Global variable to store the selected file path
    private static String selectedFile;
    public static String getFile() {
        // Path to the folder containing .txt files
        String folder = "adventures/";

        // List to store the names of available files
        ArrayList<String> availableFiles = listFiles(folder);

        // Display available files in a dialog
        String[] filesArray = availableFiles.toArray(new String[0]);
        selectedFile = (String) JOptionPane.showInputDialog(
                null,
                "Choose the file to analyze:",
                "Available Files",
                JOptionPane.QUESTION_MESSAGE,
                null,
                filesArray,
                filesArray[0]);
        return selectedFile;

    }

    // Method to list .txt files in the folder
    private static ArrayList<String> listFiles(String folder) {
        ArrayList<String> files = new ArrayList<>();
        File folderFiles = new File(folder);

        if (folderFiles.exists() && folderFiles.isDirectory()) {
            for (File file : folderFiles.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    files.add(file.getName());
                }
            }
        }

        return files;
    }
}