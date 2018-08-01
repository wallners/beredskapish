import java.util.ArrayList;

public class Hand {

    private ArrayList<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public String showCards() {
        String string = "";
        for (Card card : cards) {
            if (!card.isVisibleToOthers()) {
                string += card.toString() + "\n";
            }
        }
        return string;
    }

    public Card giveCard(Card card) {
        if (cards.size() <= 0) {
            throw new IndexOutOfBoundsException("The hand is empty.");
        }
        if (cards.contains(card)) {
            return null;
        }
        return cards.remove(cards.indexOf(card));
    }

    public boolean inHand(String selectedCard) {
        for (Card card : cards) {
            if (card.toString().equals(selectedCard)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Card getCardFromString(String string) {
        for (Card card : cards) {
            if (card.toString().equals(string)) {
                return cards.get(cards.indexOf(card));
            }
        }
        return null;
    }

    public boolean containsSuit(String suit) {
        for (Card card : cards) {
            if (card.getSuit() == suit) {
                return true;
            }
        }
        return false;
    }

    public void clearCards(){
        cards.clear();
    }
}