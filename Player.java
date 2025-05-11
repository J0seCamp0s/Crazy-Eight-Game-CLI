import java.util.ArrayList;
import java.util.List;

public class Player extends User{
    private List<Card> hand = new ArrayList<>(){};
    private Integer untilTurn;
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
        String[] userData = playerString.split("\n");
        for(String data : userData) {
            if(!data.contains("#")) {
                hand.add(new Card(data));
            }
            else {
                data.replace("#", "");
                untilTurn = Integer.valueOf(data);
            }
            
        }
    }

    //Use when creating player initially
    Player(User user, List<Card> handCards) {                  //
        super(user.getName() + "," + user.getPasswordHash()); //
        hand = handCards;
        
    }

    public Card getPlayCard(Card topCard) {
        for(int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if(card.getSuit().equals(topCard.getSuit()) 
            || card.getValue().equals(topCard.getValue()) 
            || card.getValue() == "8") {
                hand.remove(i);
                return card;
            }
        }
        return null;
    }

    public Integer getUntilTurn() {
        return untilTurn;
    }

    public void setUntilTurn(Integer timeLeft) {
        untilTurn = timeLeft;
    }

    public String toString() {
        String playerString = "";
        for(Card card : hand) {
            playerString += card.toString() + "\n";
        }
        return playerString;
    }

}