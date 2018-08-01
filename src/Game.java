import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean gameRunning = true;
        final int ROUNDS = 5;
        Card trumpCard;
        String leadingCardSuit;
        String highScoreString = "highscore.txt";
        int numberOfPlayers;
        Player[] players = new Player[3];
        players[0] = new Player();
        players[1] = new Player();
        players[2] = new Player();


        Map<String, Integer> highScore = new HashMap();
        try {
            highScore = readFile(highScoreString);
        } catch (Exception e) {
            System.out.println("Error reading file");
        }


        while (true) {
            gameRunning = menuSelection(gameRunning, highScore, scanner);
            if (!gameRunning) {
                break;
            }
            numberOfPlayers = initiatePlayers(players, scanner);
            Deck deck = new Deck();

            // play all rounds.
            for (int round = 1; round <= ROUNDS; round++) {
                deck.shuffle();
                dealCards(round, players, deck);
                System.out.println("Round#: " + round);
                trumpCard = deck.drawCard();
                placeBids(players, round, scanner, trumpCard);
                showBids(players);
                playAllTricks(players, trumpCard, round, scanner);
                calculateScores(players);
                showScores(players);
                returnCards(deck, players, trumpCard);
                resetAllTricks(players);
            }
            addWinnerToHighScore(players, highScore);
            try {
                saveFile(highScore, "highscore.txt");
            } catch (Exception e) {
                System.out.println("Error saving file...");
            }
        }
    }

    public static boolean menuSelection(boolean gameRunning, Map<String, Integer> highScore, Scanner scanner) {
        int menuSelection;
        int numberOfPlayers = 0;
        menuLoop:
        while (true) {
            System.out.println("1. Play Beredskapish");
            System.out.println("2. Show rules");
            System.out.println("3. Highscore");
            System.out.println("4. Exit");

            menuSelection = scanner.nextInt();
            if (menuSelection < 1 || menuSelection > 4) {
                System.out.println("Invalid input.");
            } else {
                switch (menuSelection) {
                    case 1:
                        break menuLoop;
                    case 2:
                        showRules();
                        break;
                    case 3:
                        showHighscore(highScore);
                        break;
                    case 4:
                        gameRunning = false;
                        break menuLoop;
                }
            }
        }
        return gameRunning;
    }

    public static int initiatePlayers(Player[] players, Scanner scanner) {
        System.out.println("How many players?");
        int numberOfPlayers;
        while (true) {
            numberOfPlayers = scanner.nextInt();
            if (numberOfPlayers > 0 && numberOfPlayers <= 3) {
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("Player " + (i + 1) + ": name? ");
            players[i].setName(scanner.next());
        }

        for (int i = numberOfPlayers; i <= 2; i++) {
            players[i].makeBot();
            players[i].setName("Bot" + (i - numberOfPlayers + 1));
        }

        System.out.println();
        return numberOfPlayers;
    }

    public static void showRules() {
        try (BufferedReader br = new BufferedReader(new FileReader("rules.txt"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Print rules error");
        }
    }

    public static void showHighscore(Map<String, Integer> highScore) {
        int i = highScore.size();
        System.out.println("~HIGH SCORE~");
        for (Map.Entry<String, Integer> entry : highScore.entrySet()) {
            System.out.print(i + ". ");
            System.out.println(entry.getKey() + ": " + entry.getValue());
            i--;
        }
        System.out.println(" ");
    }


    public static void dealCards(int round, Player[] players, Deck deck) {
        for (int i = 0; i < round; i++) {
            for (Player player : players) {
                player.giveCardTo(deck.drawCard());
            }
        }
    }

    public static int giveBid(Player player, int round) {
        return ThreadLocalRandom.current().nextInt(0, round + 1);
    }

    public static void placeBids(Player[] players, int round, Scanner scanner, Card trumpCard) {
        int bid;
        for (Player player : players) {
            System.out.println(player.getName() + "'s cards:");
            System.out.println(player.getHand().showCards());
            System.out.println("Trump: " + trumpCard);
            if (player.isBot()) {
                player.setBid(giveBid(player, round));
            } else {
                try {
                    while (true) {
                        System.out.println("Place a bid:");
                        bid = scanner.nextInt();

                        if (bid >= 0 && bid <= round) {
                            player.setBid(bid);
                            break;
                        } else {
                            System.out.println("Invalid input. Your bid must be between 0 and " + round + ".");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    scanner.next();
                }
            }
        }
    }

    public static Card giveCard(Player player) {
        return player.getHand().getCards().get(0);
    }

    public static Card giveCardFromSuit(Player player, String leadingCardSuit) {
        for (Card card : player.getHand().getCards()) {
            if (isValidCard(player, card.toString(), leadingCardSuit)) {
                return card;
            }
        }
        System.out.println("Error. Bot can't find a proper card.");
        return null;
    }

    public static void playAllTricks(Player[] players, Card trumpCard, int round, Scanner scanner) {
        Card highestCard;
        Card leadingCard = trumpCard;
        String leadingCardSuit = "";
        ArrayList<Card> selectedCards = new ArrayList<>(3);
        String selectedCardString;
        for (int i = 1; i <= round; i++) {
            selectedCards.clear();
            for (Player player : players) {
                selectedCardString = "";
                Card selectedCard;
                String selectedSuit = "";
                boolean validCard = false;

                printCardsOnTable(selectedCards);
                System.out.println(player.getName() + ", it's your turn.");

                // if current player is the dealer. leading card is determined.
                if (player == players[0]) {
                    if (player.isBot()) {
                        selectedCard = giveCard(player);
                    } else {
                        System.out.println("Select a card from your hand:");
                        System.out.println(player.getHand().showCards());
                        System.out.println("Trump: " + trumpCard.getSuit());
                        while (!player.getHand().inHand(selectedCardString) && scanner.hasNext()) {
                            selectedCardString = scanner.nextLine();
                            System.out.println("Invalid input.");
                        }
                        ;
                        selectedCard = player.getHand().getCardFromString(selectedCardString);
                    }
                    selectedCard.setVisibleToOthers(true);
                    leadingCard = selectedCard;
                    leadingCardSuit = selectedCard.getSuit();

                    // if current player is not the dealer. make sure the player plays a valid suit.
                } else {
                    if (player.isBot()) {
                        selectedCard = giveCardFromSuit(player, leadingCardSuit);

                    } else {
                        while (!validCard) {
                            System.out.println("Select a card from your hand:");
                            System.out.println(player.getHand().showCards());
                            System.out.println("Trump: " + trumpCard.getSuit() +
                                    "\nLeading card suit: " + leadingCardSuit);

                            selectedCardString = scanner.nextLine();
                            validCard = isValidCard(player, selectedCardString, leadingCardSuit);
                        }
                        selectedCard = player.getHand().getCardFromString(selectedCardString);
                        selectedCard.setVisibleToOthers(true);
                    }
                }
                selectedCards.add(selectedCard);
            }
            highestCard = getHighestCard(selectedCards, trumpCard, leadingCard);
            giveTrickTo(highestCard, players);
            printTricks(players);
        }
    }

    public static void printCardsOnTable(ArrayList<Card> selectedCards) {
        if (selectedCards.size() > 0) {
            System.out.println("Cards on table:");
            for (Card card : selectedCards) {
                System.out.print(card.toString() + "           ");
            }
            System.out.println(" ");
        }
    }

    public static void printTricks(Player[] players) {
        System.out.println("Tricks:");
        for (Player player : players) {
            System.out.println(player.getName() + " - " + player.getTricks());
        }
    }

    public static void returnCards(Deck deck, Player[] players, Card trumpCard) {
        for (Player player : players) {
            for (Card card : player.getHand().getCards()) {
                deck.addCard(card);
            }
            player.getHand().clearCards();
        }
        deck.addCard(trumpCard);
    }


    public static boolean compareCards(Card card1, Card card2, Card leadingCard, Card trumpCard) {
        Card highestCard;
        if (card1.suit == trumpCard.suit && card2.suit == trumpCard.suit) {
            return compareRank(card1, card2);
        } else if (card1.suit == trumpCard.suit)
            return true;
        else if (card2.suit == trumpCard.suit) {
            return false;
        } else if (card1.suit == leadingCard.suit && card2.suit == leadingCard.suit) {
            return compareRank(card1, card2);
        } else if (card1.suit == leadingCard.suit)
            return true;
        else if (card2.suit == leadingCard.suit) {
            return false;
        } else {
            return compareRank(card1, card2);
        }
    }

    public static boolean compareRank(Card card1, Card card2) {
        return card1.getRank() > card2.getRank();
    }

    public static boolean isValidCard(Player player, String selectedCardString, String leadingCardSuit) {
        String selectedSuit = selectedCardString.substring(selectedCardString.lastIndexOf(" ") + 1);
        if (player.getHand().inHand(selectedCardString)) {
            if (!player.getHand().containsSuit(leadingCardSuit)) {
                return true;
            }
            if (!selectedSuit.equals(leadingCardSuit)) {
                if (!player.isBot()) {
                    System.out.println("Incorrect suit");
                }
            } else {
                return true;
            }
        } else {
            if (!player.isBot()) {
                System.out.println("Invalid card.");
            }
        }

        return false;
    }

    public static Card getHighestCard(ArrayList<Card> selectedCards, Card trumpCard, Card leadingCard) {
        Card highestCard = selectedCards.get(0);
        if (compareCards(selectedCards.get(1), highestCard, leadingCard, trumpCard)) {
            highestCard = selectedCards.get(1);
        }
        if (compareCards(selectedCards.get(2), highestCard, leadingCard, trumpCard)) {
            highestCard = selectedCards.get(2);
        }
        return highestCard;
    }

    public static void giveTrickTo(Card highestCard, Player[] players) {
        for (Player player : players) {
            if (player.getHand().getCards().contains(highestCard)) {
                player.incrementTricks();
            }
        }
    }

    public static void calculateScores(Player[] players) {
        for (Player player : players) {
            if (player.getTricks() == player.getBid()) {
                player.incrementScore(player.getBid());
            }
        }
    }

    public static void showScores(Player[] players) {
        System.out.println("Round completed. Scores:");
        for (Player player : players) {
            System.out.print(player.getName() + ": " + player.getScore() + "      ");
        }
    }

    public static HashMap<String, Integer> readFile(String highscore)
            throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(highscore))) {
            is.close();
            return (HashMap<String, Integer>) is.readObject();
        }
    }


    public static void saveFile(Map<String, Integer> highScore, String highScoreString)
            throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(highScoreString))) {
            os.writeObject(highScore);
            os.close();
        }
    }

    public static void addWinnerToHighScore(Player[] players, Map<String, Integer> highScore) {
        Player winner = players[0];
        for (Player player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        highScore.put(winner.getName(), winner.getScore());
        highScore = MapUtil.sortByValue(highScore);

        try {
            saveFile(highScore, "highscore.txt");
        } catch (Exception e) {
            System.out.println("Error saving file...");
        }
    }

    static void showBids(Player[] players) {
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " bid " + player.getBid());
        }
    }
    static void resetAllTricks(Player[] players){
        for(Player player : players){
            player.resetTricks();
        }
    }
}