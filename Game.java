import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


public class Game {
    private List<Card> drawPile = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();
    private FileManager fManager;
    private UserManager uManager;

    public Game() {
        fManager = new FileManager();
        uManager = new UserManager(fManager, userList, playerList);
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
                addDivision("Intializing Game");
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
                addDivision("Adding/Removing User");
                uManager.editUser(flags[1], flags[3], flags[0].equals("--add-user"));
            }
            //Start game
            case "--start"->{
                if(!checkInputFlagNumber(3, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }

                if(!flags[1].equals("--game")) {
                    System.out.println(String.format("Error! Second flag should be --game, second flag detected: %s", flags[1]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                addDivision("Starting Game");
                startNewGame(flags[2]);
            }
            //Get order of players
            case "--order"->{
                if(!checkInputFlagNumber(5, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }

                if(!flags[1].equals("--user")) {
                    System.out.println(String.format("Error! Second flag should be --user, second flag detected: %s", flags[1]));
                    System.out.println("Aborting, please try again.");
                    return;
                }

                if(!flags[3].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, Third flag detected: %s", flags[3]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                addDivision("Retrieving Player Order");
                getPlayerOrder(flags[2], flags[4]);
            }
            //Make a play for a specific user
            case "--play"->{
                if(!checkInputFlagNumber(6, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[2].equals("--user")) {
                    System.out.println(String.format("Error! Second flag should be --user, second flag detected: %s", flags[2]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[4].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, Third flag detected: %s", flags[4]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                addDivision("Make Play");
                playCard(flags[1], flags[3], flags[5]);
            }
            //See the cards of a specific user
            case "--cards"->{
                if(!checkInputFlagNumber(6, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[2].equals("--user")) {
                    System.out.println(String.format("Error! Second flag should be --user, second flag detected: %s", flags[2]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[4].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, Third flag detected: %s", flags[4]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                addDivision("Requesting Cards");
                seeCards(flags[1], flags[3], flags[5]);
            }
            //Draw a card for a specific user
            case "--draw"->{
                if(!checkInputFlagNumber(5, flags.length, flags[0])) {
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[1].equals("--user")) {
                    System.out.println(String.format("Error! Second flag should be --user, second flag detected: %s", flags[1]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                if(!flags[3].equals("--game")) {
                    System.out.println(String.format("Error! Third flag should be --game, Third flag detected: %s", flags[3]));
                    System.out.println("Aborting, please try again.");
                    return;
                }
                addDivision("Make Draw");
                drawCard(flags[2], flags[4]);
            }
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

    private void generateNewGame(String name) {
        //Set name of game
        String gameName = name;
        System.out.println(String.format("New game name registered: %s", gameName));

        //Check if directory for game aleady exists
        addDivision("Creating Game Directory");
        File path = new File(gameName);
        if(!path.mkdir()) {
            //Preserve previous game if it already exists
            System.out.println(String.format("Error! It seems the directory for game %s already existed!",gameName));
            System.out.println("Aborting creation, please try again.");
            return;
        }
        System.out.println(String.format("Directory for game %s was craeted successfuly!", gameName));

        //Get password for admin user
        addDivision("Retrieving Admin Credentials");
        String hashedAdminPassword = uManager.getHashPassword("admin");
        if(hashedAdminPassword == null) {
            System.out.println("Error! Couldn't retrive hashed admin password!");
            System.out.println("Aborting creation, please try again.");
            return;
        }
        
        addDivision("Storing Admin Credentials");
        //Write admin credentials into users.txt file
        try {
            fManager.editFile("admin,"+ hashedAdminPassword, gameName + "//users.txt", false);
            System.out.println("users.txt file created successfuly!");
        } catch (IOException e) {
            System.out.println("Error! Couldn't create new users.txt file!");
            System.out.println("Aborting creation, please try again.");
        }
        
    }

    private void startNewGame(String gameName) {
        //Chech requirements for task are met
        if(!checkForGameplayRequirements("admin", gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        if(userList.size() < 3) {
            System.out.println("Error! At least two users need to be registered in the users.txt file (excluding the admin)!");
            System.out.println(String.format("Number of non-admin users detected %d", userList.size()));
            System.out.println("Aborting game start process, please try again.");
            return;
        }
        
        List<Card> deck = generateDeck();
        Integer lastIndex = 0;
        //Add players to playerList
        for(int i = 1; i < userList.size(); i++) {
            List<Card> playerHand = new ArrayList<>();
            for(int j = 0; j < 5; j++) {
                playerHand.add(deck.getFirst());
                deck.removeFirst();
            }
            playerList.add(new Player(userList.get(i),playerHand));
            playerList.getLast().setUntilTurn(lastIndex);
            playerList.getLast().setStatus(0);
            lastIndex += 1;
        }

        if(!uManager.writePlayersToFiles(gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        if(!addCardToDiscardPile(deck.getFirst(), gameName, false)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }
        deck.removeFirst();

        if(!addCardsToDrawPile(deck, gameName)) {
            System.out.println("Aborting game start process, please try again.");
            return;
        }

        System.out.println("Game started successfuly!");
    }

    private void getPlayerOrder(String userName, String gameName) {
        //Chech requirements for task are met
        if(!checkForGameplayRequirements(userName, gameName)) {
            System.out.println("Aborting order retrieval process, please try again.");
            return;
        }

        if(!uManager.getPlayers(gameName)) {
            System.out.println("Aborting order retrieval process, please try again.");
            return;
        }

        System.out.println("Player order:");
        for(int i = 0; i < playerList.size(); i++) {
            System.out.println(String.format("#%d: %s", i, playerList.get(i).getName()));
        }

        System.out.println("Order retrieved successfuly!");
    }

    private void playCard(String card, String userName, String gameName) {
        if(!checkForGameplayRequirements(userName, gameName)) {
            System.out.println("Aborting play, please try again.");
            return;
        }

        if(!uManager.getPlayers(gameName)) {
            System.out.println("Aborting play, please try again.");
            return;
        }

        addDivision("Checking Play");
        Player currentPlayer = playerList.getFirst();
        if(!currentPlayer.getName().equals(userName)) {
            System.out.println(String.format("Error! It is not the turn of %s!", userName));
            System.out.println(String.format("Current turn is for player %s!", currentPlayer.getName()));
            System.out.println("Aborting play, please try again.");
            return;
        }

        Card topCard = getTopCard(gameName);
        if(topCard == null) {
            System.out.println("Error! Could retrive top card!");
            System.out.println("Aborting play, please try again.");
            return;
        }

        if(!currentPlayer.checkUserStatus(0)) {
            System.out.println(String.format("Error! User %s cannot make a play, has to draw and/or pass", userName));
            System.out.println("Aborting play, please try again.");
            return;
        }
        
        Card playCard = currentPlayer.checkPlayCard(topCard, card);
        if (playCard == null) {
            System.out.println(String.format("Error! The card %s cannot be played!", card));
            if(!uManager.writePlayersToFiles(gameName)) {
                System.out.println("Aborting play, please try again.");
                return;
            }
            return;
        }
        
        addDivision("Playing Card");
        System.out.println(String.format("User %s plays card %s", userName, playCard.toString()));
        addCardToDiscardPile(playCard, gameName, true);
        uManager.passTurn();
        if(!uManager.writePlayersToFiles(gameName)) {
            System.out.println("Aborting play, please try again.");
            return;
        }
        System.out.println("Play completed successfuly");
    }

    private void seeCards(String checkedUserName, String requesterUserName, String gameName) {
        if(!checkForGameplayRequirements(requesterUserName, gameName)) {
            System.out.println("Aborting card display, please try again.");
            return;
        }

        if(!uManager.getPlayers(gameName)) {
            System.out.println("Aborting card display, please try again.");
            return;
        }

        if(!requesterUserName.equals(checkedUserName) && !requesterUserName.equals("admin")) {
            System.out.println("Error! You cannot see other user's cards (exception for admin)!");
            System.out.println("Aborting car display, please try again.");
            return;
        }

        Boolean checkedPlayerExists = false;
        for(Player player: playerList) {
            if(player.getName().equals(checkedUserName)) {
                player.displayCards();
                checkedPlayerExists = true;
            }
        }
        if(checkedPlayerExists) {
            System.out.println("Card display completed successfuly!");
        }
        else {
            System.out.println(String.format("Error! Player %s does not exist!", checkedUserName));
            System.out.println("Aborting car display, please try again.");
            return;
        }
        
    }

    private void drawCard(String userName, String gameName) {
        if(!checkForGameplayRequirements(userName, gameName)) {
            System.out.println("Aborting card draw, please try again.");
            return;
        }

        if(!uManager.getPlayers(gameName)) {
            System.out.println("Aborting card draw, please try again.");
            return;
        }

        if(!getDrawPile(gameName)) {
            System.out.println("Aborting card draw, please try again.");
            return;
        }

        addDivision("Checking Draw");
        Player currentPlayer = playerList.getFirst();
        if(!currentPlayer.getName().equals(userName)) {
            System.out.println(String.format("Error! It is not the turn of %s!", userName));
            System.out.println(String.format("Current turn is for player %s!", currentPlayer.getName()));
            System.out.println("Aborting draw, please try again.");
            return;
        }

        if(!currentPlayer.checkUserStatus(1)) {
            if(currentPlayer.getStatus() == 0) {
                System.out.println("You are allowed to draw, but you are losing your chances, if any, of playing a card");
            }
            else {
                System.out.println(String.format("Error! User %s cannot make draw, has to pass", userName));
                System.out.println("Aborting draw, please try again.");
                return;
            }
        }

        Card drawnCard = drawPile.getFirst();
        Card topCard = getTopCard(gameName);

        if(topCard == null) {
            System.out.println("Error! Could retrive top card!");
            System.out.println("Aborting draw, please try again.");
            return;
        }

        addDivision("Drawing a Card");
        if(drawnCard.getSuit().equals(topCard.getSuit())||
        drawnCard.getValue().equals(topCard.getValue())|| 
        drawnCard.getValue().equals("8")) {
            System.out.println(String.format("You can play the card you just drew (%s)", drawnCard.toString()));
            addCardToDiscardPile(drawnCard, gameName, true);
        }
        drawPile.remove(drawnCard);
        System.out.println(String.format("Card drawn was %s", drawnCard.toString()));
        currentPlayer.displayCards();
        currentPlayer.setStatus(2);
        if(!uManager.writePlayersToFiles(gameName)) {
            System.out.println("Aborting draw, please try again.");
            return;
        }
        if(!addCardsToDrawPile(drawPile, gameName)) {
            System.out.println("Aborting draw, please try again.");
            return;
        }

        System.out.println("Card was drawn successfuly!");
    }

    private Card getTopCard(String gameName) {
        String discardPile = fManager.readFile(gameName + "//discard.txt");
        if(discardPile.isEmpty()) {
            System.out.println("Error! Could retrive discard pile from discard.txt!");
            return null;
        }
        String[] discardPileCards = discardPile.split("\n");
        Card topCard = new Card(discardPileCards[discardPileCards.length -1]);
        return topCard;
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

    private void addDivision(String sectionTitle) {
        System.out.println(String.format("=================%s=================", sectionTitle));
    }

    private Boolean addCardToDiscardPile(Card discardCard, String gameName, Boolean mode) {
        try {
            fManager.editFile(discardCard.toString() + "\n", gameName + "//" + "discard.txt", mode);
        } catch (IOException e) {
            System.out.println(String.format("Error! Could not add card %s to discard.txt file!", discardCard.toString()));
            return false;
        }
        return true;
    }
    
    private Boolean addCardsToDrawPile(List<Card> drawPile, String gameName) {
        String drawPileString = "";
        for(Card card : drawPile) {
            drawPileString += card.toString() + "\n";
        }

        try {
            fManager.editFile(drawPileString, gameName + "//" + "draw.txt", false);
        } catch (IOException e) {
            System.out.println("Error! Could not update draw.txt file!");
            return false;
        }
        return true;
    }

    private Boolean getDrawPile(String gameName) {
        String drawPileString = fManager.readFile(gameName + "//draw.txt");
        if (drawPileString.isEmpty()) {
            System.out.println("Error! Couldn't retrieve draw pile from draw.txt file!");
            return false;
        }
        String[] drawCardStrings = drawPileString.split("\n");
        for(String cardString: drawCardStrings)  {
            drawPile.add(new Card(cardString));
        }
        return true;
    }

    private Boolean checkInputFlagNumber(Integer expectedNumber, Integer currentNumber, String flag) {
        if(expectedNumber != currentNumber) {
            System.out.println(String.format("Error! Wrong number of flags for %s execution!, %d input parameters expected", flag, expectedNumber));
            System.out.println(String.format("Input parameters detected: %d", currentNumber));
            return false;
        }
        return true;
    }

    private Boolean checkForGameplayRequirements(String userName, String gameName) {
         //Validate game directory exists and that it is a directory
        if(!fManager.checkDirectoryPath(gameName)) {  
            return false;
        }

        //Compare input password with stored password for userName
        System.out.println(String.format("This operation requires the password for %s", userName));
        if(!uManager.comparePasswords(userName, gameName)) {
            return false;
        }

        //Try retrieving users from users.txt
        if(!uManager.getUsers(gameName)) {
            return false;
        } 

        return true;
    }

}