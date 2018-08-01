public class Player {
    private String name;
    private Hand hand;
    private int tricks;
    private int bid;
    private int score;
    private boolean bot;

    Player() {
        this.name = "";
        this.hand = new Hand();
        this.tricks = 0;
        this.bid = 0;
        this.score = 0;
    }

    public void giveCardTo(Card card) {
        hand.addCard(card);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public void incrementTricks() {
        this.tricks++;
    }

    public void clearTrick() {
        this.tricks = 0;
    }

    public int getTricks() {
        return tricks;
    }

    public boolean isBot() {
        return bot;
    }

    public void incrementScore(int addToScore) {
        this.score += addToScore;
    }

    public int getScore() {
        return this.score;
    }

    public void makeBot() {
        this.bot = true;
    }

    public void resetTricks() {
        this.tricks = 0;
    }
}
