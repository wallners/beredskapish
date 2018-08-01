public class Card {
    final Rank rank;
    final Suit suit;
    private boolean visibleToOthers;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        visibleToOthers = false;
    }

    public String getSuit() {
        return suit.toString();
    }

    public int getRank() {
        return rank.getRank();
    }

    @Override
    public String toString() {
        String string;
        return Integer.toString(rank.getRank()) + " of " + getSuit();
    }

    public boolean isSameSuit(Card other) {
        return suit.equals(other.suit);
    }

    public boolean isVisibleToOthers() {
        return visibleToOthers;
    }

    public void setVisibleToOthers(boolean visible) {
        this.visibleToOthers = visible;
    }
}
