import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>(52);
        // Instantiate deck.
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }

        // Shuffle deck.
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.size() <= 0) {
            throw new IndexOutOfBoundsException("The deck is empty.");
        }
        return cards.remove(cards.size() - 1);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }

    public void addCard(Card card){
        cards.add(card);
    }




}