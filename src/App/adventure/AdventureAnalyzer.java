package App.adventure;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class AdventureAnalyzer {

    // Global variable to store the selected file path
    private static String selectedFile;
    public static void main(String[] args) {
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

        // Check the validity of the choice
        if (selectedFile != null) {
            analyzeFile(folder + selectedFile);

        } else {
            System.out.println("No file selected.");
        }
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

    // Method to analyze the content of the file
    private static void analyzeFile(String filePath) {
        // Add the code to analyze the content of the file if needed
        JOptionPane.showMessageDialog(null, "Selected file: " + filePath, "File Analysis", JOptionPane.INFORMATION_MESSAGE);
    }
}