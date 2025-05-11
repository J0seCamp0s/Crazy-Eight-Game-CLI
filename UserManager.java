import java.io.Console;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;

public class UserManager {
    private List<User> userList;
    private List<Player> playerList;
    private FileManager fManager;

    public UserManager(FileManager fileManager, List<User> users, List<Player> players) {
        fManager = fileManager;
        userList = users;
        playerList = players;
    }

    public String getHashPassword(String userName) {
        Console cli = System.console();

        // Retrieve password from user
        char[] passArray = cli.readPassword(String.format("Please enter the password for %s:", userName));
        String password = new String(passArray);
        System.out.println("Password retrieved successfully!");

        // Hash password
        String hashedPassword = null;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA3-256");
            byte[] byteHash = hasher.digest(password.getBytes());
            hashedPassword = Base64.getEncoder().encodeToString(byteHash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error! Can't create SHA3-256 hasher!");
        }
        return hashedPassword;
    }

    public String getPasswordFromFile(String userName, String gameName) {
        String userPassword = null;

        // Retrieve users from users.txt
        if (!getUsers(gameName) || userList.size() == 0) {
            System.out.println(String.format("Error! Couldn't retrieve users for game %s", gameName));
        }

        // Find password for user with name == userName
        for (User user : userList) {
            if (user.getName().equals(userName)) {
                userPassword = user.getPasswordHash();
                break;
            }
        }
        return userPassword;
    }

    public Boolean comparePasswords(String userName, String gameName) {
        String consolePassword = getHashPassword(userName);
        if (consolePassword == null) {
            System.out.println(String.format("Error! Couldn't retrieve hashed password for %s user!", userName));
            return false;
        }

        String filePassword = getPasswordFromFile(userName, gameName);
        if (filePassword == null) {
            System.out.println(String.format("Error! Couldn't retrieve %s password from users.txt!", userName));
            System.out.println(String.format("User %s might not exist!", userName));
            return false;
        }

        if (!filePassword.equals(consolePassword)) {
            System.out.println(String.format("Error! Typed %s password and stored %s password do not match!", userName, userName));
            return false;
        }
        return true;
    }

    public Boolean getUsers(String gameName) {
        String userCredentials = fManager.readFile(gameName + "//users.txt");
        if (userCredentials.isEmpty()) {
            System.out.println(String.format("Error! Couldn't open users.txt file for game %s", gameName));
            return false;
        }

        String[] users = userCredentials.split("\n");
        userList.clear();
        for (String user : users) {
            userList.add(new User(user));
        }
        return true;
    }

    public void editUser(String userName, String gameName, Boolean mode) {
        //Validate game directory exists and that it is a directory
        if(!fManager.checkDirectoryPath(gameName)) {
            System.out.println("Aborting user addition/deletion, please try again.");
            return;
        }

        //Compare input password with stored password for admin
        System.out.println("Adding/Removing a user is a priviledge operation, requires admin permissions");
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
                fManager.editFile("\n"+ userName + "," + userPassword, gameName + "//users.txt", true);
            } catch (IOException e) {
                System.out.println("Error! Could not add new user to users.txt file!");
                System.out.println("Aborting user addition/deletion, please try again.");
                return;
            }
        }
        //Removing user mode
        else {
            
        }
        
    }

    public Boolean getPlayers(String gameName) {
        String gameStatuString = fManager.readFile(gameName + "//" + "gameStatus.txt");
        if (gameStatuString .isEmpty()) {
            System.out.println(String.format("Error! Couldn't retrieve game data for %s!", gameName));
            return false;
        }
        String[] playerStatus = gameStatuString.split("\n");
        for (int i = 1; i < userList.size(); i++) {
            String playerString = fManager.readFile(gameName + "//" + userList.get(i).getName() + ".txt");
            if (playerString.isEmpty()) {
                System.out.println(String.format("Error! Couldn't retrieve data of player %s!", userList.get(i).getName()));
                return false;
            }
            String currentStatus = "";
            for(int j = 0; j < playerStatus.length; j++) {
                if(playerStatus[j].contains(userList.get(i).getName())) {
                    currentStatus = playerStatus[j];
                }
            }
            currentStatus = currentStatus.replace(",", "\n");
            currentStatus = currentStatus.replace(userList.get(i).getName()+"\n", "");
            playerString += currentStatus;
            playerList.add(new Player(userList.get(i), playerString));
        }

        // Sort players by `untilTurn`
        Collections.sort(playerList, Comparator.comparingInt(Player::getUntilTurn));    //
        return true;
    }

    public void passTurn() {
        Player currentPlayer = playerList.getFirst();
        playerList.remove(currentPlayer);
        for(int i = 0; i < playerList.size(); i++) {
            playerList.get(i).setUntilTurn(i);
        }
        currentPlayer.setStatus(0);
        currentPlayer.setUntilTurn(playerList.size());
        playerList.add(currentPlayer);
    }

    public Boolean writePlayersToFiles(String gameName) {
        String gameStatusString = "";
        for (Player player : playerList) {
            String playerString = player.handToString();
            gameStatusString += player.dataToString() + "\n";
            try {
                fManager.editFile(playerString, gameName + "//" + player.getName() + ".txt", false);
            } catch (IOException e) {
                System.out.println(String.format("Error! Could not create %s.txt file!", player.getName()));
                return false;
            }
        }

        try {
            fManager.editFile(gameStatusString, gameName + "//" + "gameStatus.txt", false);
        } catch (IOException e) {
            System.out.println(String.format("Error! Could not create gameStatus.txt file for game %s!", gameName));
            return false;
        }

        return true;
    }
}