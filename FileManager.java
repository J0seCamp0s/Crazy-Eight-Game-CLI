import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileManager {

    public String readFile(String filePath) {
        String completeFileString = "";
        try {
            File inputFile = new File(filePath);
            Scanner fileReader = new Scanner(inputFile);
            while (fileReader.hasNextLine()) {
                String newLine = fileReader.nextLine();
                if(!newLine.isBlank()) {
                    completeFileString += (newLine) + "\n";
                } 
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Error! File %s not found!", filePath));
        }
        return completeFileString;
    }

    public void editFile(String newFileContent, String filePath, Boolean mode) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, mode));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println(String.format("Error! Couldn't write to file %s!", filePath));
            throw e;
        }
    }

    public Boolean checkDirectoryPath(String gameName) {
        //Check if game directory exists
        File gameDirectory = new File(gameName);
        if(!gameDirectory.exists()) {
            System.out.println(String.format("Error! Directory for game %s does not exist yet!", gameName));
            return false;

        //Check if path to directory actually points to a directory
        } else if(!gameDirectory.isDirectory()) {
            System.out.println(String.format("Error! %s is not a directory!", gameName));
            return false;
        }
        return true;
    }
}