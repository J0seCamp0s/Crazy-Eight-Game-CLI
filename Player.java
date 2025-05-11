import java.util.ArrayList;
import java.util.List;

public class Player extends User{
    private List<Card> hand = new ArrayList<>(){};
    private Integer untilTurn;
    private Integer status;
    /*
     * Prompt to Copilot using ChatGPT 4.0:
     * I want Player to inherit the methods and attributes
     * from User, but I don't want to have to use the parameter
     * "userString", I want to pass in a User object instead, how
     * can I do it?
     * <Passed in this file and User.java as context>
     */
    //Use when creating player from existing data
    Player(User user, String playerString) {                  //
        super(user.getName() + "," + user.getPasswordHash()); //

        //Split playerString into array of strings
        String[] userData = playerString.split("\n");
        for(String data : userData) {
            //If card string
            if(!data.contains("#") && !data.contains("$")) {
                hand.add(new Card(data));
            }
            //If player order string
            else if (data.contains("#")) {
                String formattedData = data.replace("#", "");
                untilTurn = Integer.valueOf(formattedData);
            }
            //If player status string
            else {
                String formattedData = data.replace("$", "");
                status = Integer.valueOf(formattedData);
            }
            
        }
    }

    //Use when creating player initially
    Player(User user, List<Card> handCards) {                  //
        super(user.getName() + "," + user.getPasswordHash()); //
        hand = handCards;
    }
    
    public void displayCards() {
        System.out.println(String.format("%s's current hand:", name));
        for(int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            System.out.println(String.format("Card #%d: %s", i, card.toString()));
        }
    }

    public Card checkPlayCard(Card topCard, String playCardString) {
        Boolean cardInHand = false;
        List<Card> playableCards = new ArrayList<>();
        //Check playCard exists and that it can be played
        Card playCard;
        for(int i = 0; i < hand.size(); i++) {
            //Check if play card exists in player's hand
            playCard = hand.get(i);
            if (playCard.toString().equals(playCardString)) {
                cardInHand = true;
            }
            //Check if current card is playable;
            if(playCard.getSuit().equals(topCard.getSuit()) 
            || playCard.getValue().equals(topCard.getValue()) 
            || playCard.getValue() == "8") {
                playableCards.add(playCard);
            }
        }
        if(!cardInHand) {
            System.out.println(String.format("The card %s is not part of your hand!", playCardString));
        }
        //If player has no playable cards
        if(!playableCards.isEmpty()) {
            status = 1;
            System.out.println("You have no playable cards in your hand!");
            System.out.println("You must draw and then pass your turn.");
            return null;
        }
        //If player has playable cards
        else {
            for(int i = 0; i < playableCards.size(); i++) {
                //Check if playCardString is a playable card
                if(playCardString.equals(playableCards.get(i).toString())) {
                    playCard = hand.get(i);
                    hand.remove(i);
                    return playCard;
                }
            }
            System.out.println("But you do have playable cards in you hand, try another card.");
            return null;
        }
    }

    public Integer getUntilTurn() {
        return untilTurn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setUntilTurn(Integer timeLeft) {
        untilTurn = timeLeft;
    }
    
    public void setStatus(Integer newStatus) {
        status = newStatus;
    }

    public Boolean checkUserStatus(Integer expectedStatus) {
        if(status.equals(expectedStatus)) {
            return true;
        }
        else {
            return false;
        }
    }

    public String handToString() {
        String handString = "";
        for(Card card : hand) {
            handString += card.toString() + "\n";
        }
        return handString;
    }

    public String dataToString() {
        String playerString = name + ",#" + untilTurn + ",$" + status;
        return playerString;

    }

}