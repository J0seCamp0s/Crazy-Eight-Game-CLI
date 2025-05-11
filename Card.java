public class Card{
    private String value;
    private String suit;

    //Used when creating cards initially
    public Card(String val, String suitVal) {
        value = val;
        suit = suitVal;
    }

    //Used when reading cards from user file
    public Card(String cardString) {
        value = cardString.substring(0,1);
        suit = cardString.substring(1,2);
    }

    public String getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        String cardString = value + suit;
        return cardString;
    }
}