enum Suit {
    SPADES("Spades"), CLUBS("Clubs"), DIAMOND("Diamonds"), HEART("Hearts");

    private final String suitText;

    Suit(String suitText) {
        this.suitText = suitText;
    }

    public String toString() {
        return suitText;
    }
}

