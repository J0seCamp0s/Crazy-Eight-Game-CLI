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
import java.util.Base64;
import java.util.Collections;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Game {
    private List<User> userList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>(){};
    private List<Card> drawPile = new ArrayList<>();

    public Game() {
        
    }

    public static void main(String[] args) {
        Game newGame = new Game();

        newGame.parseFlags(args);
    }

    private void parseFlags(String[] flags) {
        //Check that flags were passed into program
        if(flags.length == 0) {
            System.out.println("Error! No flags were given for execution!");
            return;
        }

        switch (flags[0]) {
            //Create new game
            case "--init"-> {
                if(!checkInputFlagNumber(3, flags.length, flags[0])) {
                    return;
                }
                if(!flags[1].equals( "--game")) {
                    System.out.println(String.format("Error! Second flag should be --game, second flag detected: %s", flags[1]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                generateNewGame(flags[2]);
            }
            //Add new user/Remove user
            case "--add-user", "--remove-user"->{
                if(!checkInputFlagNumber(4, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[2].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, second flag detected: %s", flags[2]));
                    System.out.println("Aborting, please try again.");
                    return;
                }

                editUser(flags[1], flags[3], flags[0].equals("--add-user"));
            }
            //Start game
            case "--start"->{
                if(!checkInputFlagNumber(3, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }

                if(!flags[1].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, second flag detected: %s", flags[1]));
                    System.out.println("Aborting, please try again.");
                    return;
                }

                startNewGame(flags[2]);
            }
            //Get order of players
            case "--order"->{}
            //Make a play for a specific user
            case "--play"->{}
            //See the cards of a specific user
            case "--cards"->{}
            //Draw a card for a specific user
            case "--draw"->{}
            //Pass turn of a specific user
            case "--pass"->{}
            //Unsupported flag given for game operation
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

    private Boolean checkInputFlagNumber(Integer expectedNumber, Integer currentNumber, String flag) {
        if(expectedNumber != currentNumber) {
            System.out.println(String.format("Error! Wrong number of flags for %s execution!, %d input parameters expected", flag, expectedNumber));
            System.out.println(String.format("Input parameters detected: %d", currentNumber));
            return false;
        }
        return true;
    }

    private void generateNewGame(String name) {
        //Set name of game
        String gameName = name;
        System.out.println(String.format("New game name registered: %s", gameName));

        //Check if directory for game aleady exists
        File path = new File(gameName);
        if(!path.mkdir()) {
            //Preserve previous game if it already exists
            System.out.println(String.format("Error! It seems the directory for game %s already existed!",gameName));
            System.out.println("Aborting creation, please try again.");
            return;
        }
        System.out.println(String.format("Directory for game %s was craeted successfuly!", gameName));

        //Get password for admin user
        String hashedAdminPassword = getHashPassword("admin");
        if(hashedAdminPassword == null) {
            System.out.println("Error! Couldn't retrive hashed admin password!");
            System.out.println("Aborting creation, please try again.");
            return;
        }

        //Write admin credentials into users.txt file
        try {
            editFile("admin,"+ hashedAdminPassword, gameName + "//users.txt", false);
            System.out.println("users.txt file created successfuly!");
        } catch (Exception IOException) {
            System.out.println("Error! Couldn't create new users.txt file!");
            System.out.println("Aborting creation, please try again.");
        }
        
    }

    private String getHashPassword(String userName) {
        Console cli = System.console();

        //Retreive password from user
        char[] passArray = cli.readPassword(String.format("Please enter the password for %s:", userName));
        String password = new String(passArray);
        System.out.println("Password retrieved successfuly!");

        //Hash password
        String hashedPassword = null;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA3-256");
            byte [] byteHash = hasher.digest(password.getBytes());
            hashedPassword = Base64.getEncoder().encodeToString(byteHash);                    //
        } catch (Exception NoSuchAlgorithmException) {
            System.out.println("Error! Can't create SHA3-256 hasher!");
        }
        return hashedPassword;
    }

    private void editUser(String userName, String gameName, Boolean mode) {
        //Validate game directory exists and that it is a directory
        if(!validateDirectoryPath(gameName)) {
            System.out.println("Aborting user addition/deletion, please try again.");
            return;
        }

        //Compare input password with stored password for admin
        System.out.println("Adding a user is a priviledge operation, requires admin permissions");
        if(!comparePasswords("admin", gameName)) {
            System.out.println("Aborting user addition/deletion, please try again.");
            return;
        }

        System.out.println("Admin user validated, proceeding with adding/removal operation");

        //Adding user mode
        if(mode) {
            System.out.println(String.format("Create a password for the new user %s", userName));
            String userPassword = getHashPassword(userName);

            //Check that the retrieval and hashing of the password was succesful
            if(userPassword == null) {
                System.out.println(String.format("Error! Couldn't retrive hashed %s password!", userName));
                System.out.println("Aborting user addition/deletion, please try again.");
                return;
            }

            //Add new user to users.txt file
            try {
                editFile("\n"+ userName + "," + userPassword, gameName + "//users.txt", true);
            } catch (Exception IOException) {
                System.out.println("Error! Could not add new user to users.txt file!");
                System.out.println("Aborting user addition/deletion, please try again.");
                return;
            }
        }
        //Removing user mode
        else {
            
        }
        
    }

    private Boolean validateDirectoryPath(String gameName) {
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

    private Boolean comparePasswords(String userName, String gameName) {
        //Retrive userName password from console
        String consoleAdminPassword = getHashPassword(userName);

        //Check if retrieval and hashing was successful
        if(consoleAdminPassword == null) { 
            System.out.println(String.format("Error! Couldn't retrive hashed password for %s user!", userName));
            return false;
        }

        //Retrieve userName password from users.txt
        String fileAdminPassword = retrievePasswordFromFile("admin", gameName);

        //Check if retrieval was successful
        if(fileAdminPassword == null) { 
            System.out.println(String.format("Error! Couldn't retrive %s password from users.txt!", userName));
            return false;
        }

        //Check if password hashes match
        if(!fileAdminPassword.equals(consoleAdminPassword)) {
            System.out.println(String.format("Error! Typed %s password and stored %s password do not match!", userName, userName));
            return false;
        }
        return true;
    }

    private Boolean getUsers(String gameName) {
        //Retreive users from users.txt
        String userCredentials = readFile(gameName + "//users.txt");
        if(userCredentials.isEmpty()) {
            System.out.println(String.format("Error! Couldn't open users.txt file for game %s", gameName));
            return false;
        }

        //Split userCredentials, one element per line
        String[] users = userCredentials.split("\n");
        for(String user : users) {
            userList.add(new User(user));
        }
        return true;
    }

    private Boolean validateUserName(String newName) {
        return true;
    }

    private String retrievePasswordFromFile(String userName, String gameName) {
        //Set userPassword to null as default
        //Value is null if password was not found
        String userPassword = null;
        
        //Retrieve users from users.txt and validate the file exists and that is not empty
        if(!getUsers(gameName) || userList.size() == 0) {
            System.out.println(String.format("Error! Couldn't retrieve users for game %s", gameName));
        }

        //Find password for user with name == userName
        for(User user: userList) {
            if(user.getName().equals(userName)) {
                userPassword = user.getPasswordHash();
                break;
            }
        }
        return userPassword;
    }

    private void startNewGame(String gameName) {
        //Validate game directory exists and that it is a directory
        if(!validateDirectoryPath(gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        //Compare input password with stored password for admin
        System.out.println("Adding a user is a priviledge operation, requires admin permissions");
        if(!comparePasswords("admin", gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        if(!getUsers(gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        } 

        if(userList.size() > 3) {
            System.out.println("Error! At least two users need to be registered in the users.txt file (excluding the admin)!");
            System.out.println("Aborting game start process, please try again.");
            return;
        }
        
        List<Card> deck = generateDeck();

        //Add players to playerList
        for(int i = 1; i < userList.size(); i++) {
            List<Card> playerHand = deck.subList(0, 4);
            playerList.add(new Player(userList.get(i),playerHand));
        }

        if(!writePlayersToFiles(gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        if(!addCardToDiscardPile(deck.getFirst().toString())) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }
    }

    private Boolean writePlayersToFiles(String gameName) {
        for(int i = 0; i < playerList.size(); i++) {
            String playerString = playerList.get(i).toString();
            playerString += "\n#" + String.valueOf(i);
            try {
                editFile(playerString, gameName + "//" + playerList.get(i).getName() + ".txt", false);
            } catch (Exception e) {
                System.out.println(String.format("Error! Could not create %s.txt file!", playerList.get(i).getName()));
                return false;
            }
        }
        return true;
    }

    private Boolean addCardToDiscardPile(String discardCard) {
        return true;
    }

    private List<Card> generateDeck() {
        List<Card> deck = new ArrayList<>();
        for(String value: CardStaticValue.VALUES) {
            deck.add(new Card(value, CardStaticValue.CLUBS));
        }
        for(String value: CardStaticValue.VALUES) {
            deck.add(new Card(value, CardStaticValue.DIAMONDS));
        }
        for(String value: CardStaticValue.VALUES) {
            deck.add(new Card(value, CardStaticValue.HEARTS));
        }
        for(String value: CardStaticValue.VALUES) {
            deck.add(new Card(value, CardStaticValue.SPADES));
        }

        Collections.shuffle(deck);
        return deck;
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
            System.out.println(String.format("Error! File %s not found!", filePath));
        }
        return completeFileString;   
    }

    private void editFile(String newFileContent, String filePath, Boolean mode) throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, mode));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println(String.format("Error! Couldn't write to file %s!", filePath));
            throw e;
        }
    }
}