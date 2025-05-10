public abstract class BaseCard implements Card{
    private BaseCard card;

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public abstract Object getCardVal();
}