import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Game {

    private List<Player> playerList = new ArrayList<>(){};
    private String gameName;
    private HashMap<String, String> flags = new HashMap<>();

    public Game() {
        
    }

    public static void main(String[] args) {
        Game newGame = new Game();

        newGame.parseFlags(args);
    }

    private void parseFlags(String[] flags) {
        if(flags.length == 0) {
            System.out.println("Error! No flags were given for execution!");
            return;
        }

        switch (flags[0]) {
            case "--init"-> {
                if(flags.length != 3) {
                    System.out.println("Error! Wrong number of flags for --init execution!, 2 flags and 1 parameter expected.");
                    System.out.println(String.format("Arguments detected: %d", flags.length));
                    return;
                }
                if(!flags[1].equals( "--game")) {
                    System.out.println(String.format("Error! Second flag should be --game, second flag detected: %s", flags[1]));
                    return;
                }
                generateNewGame(flags[2]);
            }
            case "--add-user"->{}
            case "--remove-user"->{}
            case "--start"->{}
            case "--order"->{}
            case "--play"->{}
            case "--cards"->{}
            case "--draw"->{}
            case "--pass"->{}
            default-> {
                System.out.println(String.format("The flag %s is not a supported initial flag!",flags[0]));
                System.out.println("""
                        The supported intial flags are:
                        --init
                        --add-user
                        --remove-user
                        --start
                        --order
                        --play
                        --cards
                        --draw
                        --pass
                        """);
            }
        }
    }

    private void generateNewGame(String name) {
        gameName = name;
        System.out.println(String.format("New game name registered: %s", gameName));
        File path = new File(gameName);
        if(!path.mkdir()) {
            System.out.println(String.format("Error! It seems the directory %s already existed",gameName));
            System.out.println("Aborting creation.");
            return;
        }
        System.out.println(String.format("Directory for game %s was craeted successfuly", gameName));

        Console cli = System.console();

        //Retreive password from user
        char[] adminPassArray = cli.readPassword();
        String adminPassword = new String(adminPassArray);

        //Hash password
        String hashedAdminPassword;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA3-256");
            byte [] byteHash = hasher.digest(adminPassword.getBytes());
            hashedAdminPassword = HexFormat.of().formatHex(byteHash);
        } catch (Exception NoSuchAlgorithmException) {
            System.out.println("Error! Can't create SHA3-256 hasher!");
        }
    }

    private void editUser(String userName, Boolean mode) {

    }

    private String getPassword() {
        return null;
    }

    private Boolean checkTurn() {
        return true;
    }

    private String readFile(String filePath) {

        String completeFileString = "";
        try {
            File inputFile = new File(filePath);
            Scanner fileReader = new Scanner(inputFile);
            while(fileReader.hasNextLine())
            {
                completeFileString += (fileReader.nextLine()) + "\n";
            }
            fileReader.close();
        } catch(FileNotFoundException e){
            System.out.println("Error, file not found!");
        }
        return completeFileString;   
    }

    private void editFile(String newFileContent, String filePath, Boolean mode) throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, mode));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't write to file!");
            throw e;
        }
    }
}