package com.example.threeDragonAnte.tda;

import com.example.threeDragonAnte.game.infoMsg.GameState;
import java.util.ArrayList;
import java.util.Random;

public class TdaGameState extends GameState {

    private static final long serialVersionUID = -720211997L;

    //all the places a card can be placed
    private ArrayList<Card> deck;
    private ArrayList<Card> discard;
    private ArrayList<Card> ante;
    private ArrayList<Card>[] hands; //all player hands
    private ArrayList<Card>[] flights; //all player flights
    private int chooseFrom; //list of all the cards a player might have to choose from
    private Card lastPlayed; //the last card that was played
    private Card[] last; //currently selected cards (accounts for two players selecting)
    private boolean mage; //used for the archmage power


    //statistics of the game (hoards and stakes)
    private int[] hoards; //player hoards
    private int stakes; //current stakes

    //logistics for the flow of the game (rounds, gambits, antes)
    private int currentPlayer;
    private String gameText; //what info text is the game displaying
    private int phase; //current phase of the game
    private int roundLeader; //the current leader of the round
    private int round; //how many rounds passed in this gambit
    private int moves; //how many moves have been made this round

    private String[][] choices; //all possible choice texts the player can see
    private String choice1; //first choice displayed
    private String choice2; //second choice
    private String choice3;//third choice (possible when picking from flights)


    //constants for different phases of the game in gamePhase
    public static final int BEGIN_GAME = 0;
    public static final int ANTE = 1;
    public static final int ROUND = 2;
    public static final int CHOICE = 3; //when a player is choosing options from a card
    public static final int CONFIRM = 4; //player is confirming game info (i.e end of a round)
    public static final int FORFEIT = 5;
    public static final int DISCARD = 6;//confirming the end of a gambit

    //conditions that change the game state
    private boolean discarding; //if the player had to make the choice to discard a card
    private boolean choosing; //if the player has to choose a card (see Dracolich's ability)


    /**
     * default constructor for the tdaGameState
     */
    public TdaGameState(){

        //initializing all arrays of cards
        deck = Card.buildDeck();
        discard = new ArrayList<>();
        chooseFrom = 0;
        ante = new ArrayList<>();
        hands = new ArrayList[2];
        flights = new ArrayList[2];

        //initializing the hands and flights
        for(int i = 0; i<2; i++){
            hands[i] = new ArrayList<>();

            //each player starts off with 6 cards
            for(int j = 0; j<6; j++){
                drawCard(i);
            }
            flights[i] = new ArrayList<>();
        }

        lastPlayed = new Card(); //no card played yet at the beginning of the game

        //no cards selected at the beginning of the game
        last = new Card[2];
        last[0] = new Card();
        last[1] = new Card();

        //logistics of game on start up
        this.phase = BEGIN_GAME;
        discarding = false;
        choosing = false;
        mage = false;
        gameText = "Welcome to Three Dragon Ante";
        currentPlayer = 0; //defaulted to player 0
        roundLeader = 0; //defaulted to player 0
        hoards = new int[2];
        hoards[0] = 50; //each player starts with 50 gold
        hoards[1] = 50;
        moves = 0;
        round = 0;
        stakes = 0;//no stakes on start up
        choice1 = "Play";
        choice2 = "";
        choice3 = "";

        //all possible choice texts

        choices = new String[3][2];

    }

    /**
     * copy constructor for the tdaGameState
     * @param copy instance of the current tdaGameState
     */
    public TdaGameState(TdaGameState copy){

        //instance variables that don't require a deep copy
        this.phase = copy.phase;
        discarding = copy.discarding;
        choosing = copy.choosing;
        gameText = copy.gameText;
        mage = copy.mage;
        currentPlayer = copy.currentPlayer;
        roundLeader = copy.roundLeader;
        hoards = new int[2];
        round = copy.round;
        stakes = copy.stakes;//no stakes on start up
        choice1 = copy.choice1;
        choice2 = copy.choice2;
        choice3 = copy.choice3;
        choices = new String[3][2];
        choices = copy.choices;
        moves = copy.moves;

        //Copying selected cards and the card that was last played
        lastPlayed = new Card(copy.lastPlayed);
        last = new Card[2];
        last[0] = new Card(copy.last[0]);
        last[1] = new Card(copy.last[1]);

        //copying the deck
        deck = new ArrayList<>();
        for(Card d : copy.deck){
            this.deck.add(new Card(d));
        }
        //copying the discard pile
        discard = new ArrayList<>();
        for(Card c : copy.discard){
            this.discard.add(new Card(c));
        }

        //copying the chooseFrom amount
        chooseFrom = copy.chooseFrom;

        //copying the ante pile
        ante = new ArrayList<>();
        for(Card a : copy.ante){
            ante.add(new Card(a));
        }
        //copying hands and flights
        hands = new ArrayList[2];
        flights = new ArrayList[2];
        for(int i = 0; i<2; i++){
            hands[i] = new ArrayList<>();
            for(Card h : copy.hands[i]){
                hands[i].add(new Card(h));
            }
            flights[i] = new ArrayList<>();
            for(Card f : copy.flights[i]){
                flights[i].add(new Card(f));
            }
            hoards[i] = copy.hoards[i];
        }
    }

    /**
     * Adds a random card to the player's hand
     * @param player - which hand is it going to
     */
    public void drawCard(int player){
        //seeds for test:
        //9877856 - copper dragon
        //2304 - thief
        //3459 - brass dragon
        //23045 - red dragon
        //533440 - dracolich
        //334055 - tiamat
        //14565435 - dragon slayer
        //2123412556 - fool
        //66644 - gold dragon
        //554676 - arch mage
        //854 - fool
        //765 - princess
        Random r = new Random(765);
        int index = r.nextInt(deck.size());
        Card drawn = deck.get(index);
        deck.remove(index);
        drawn.setPlacement(Card.HAND);
        hands[player].add(drawn);
    }

    public boolean isDiscarding() {
        return discarding;
    }

    public void setDiscarding(boolean discarding) {
        this.discarding = discarding;
    }

    public boolean isChoosing() {
        return choosing;
    }

    public void setChoosing(boolean choosing) {
        this.choosing = choosing;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getDiscard() {
        return discard;
    }

    public void setDiscard(ArrayList<Card> discard) {
        this.discard = discard;
    }

    public ArrayList<Card> getAnte() {
        return ante;
    }

    public void setAnte(ArrayList<Card> ante) {
        this.ante = ante;
    }

    public ArrayList<Card>[] getHands() {
        return hands;
    }

    public void setHands(ArrayList<Card>[] hands) {
        this.hands = hands;
    }

    public ArrayList<Card>[] getFlights() {
        return flights;
    }

    public void setFlights(ArrayList<Card>[] flights) {
        this.flights = flights;
    }

    public Card getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Card lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public Card[] getLast() {
        return last;
    }

    public void setLast(int player, Card c) {
        this.last[player] = c;
    }

    public int[] getHoards() {
        return hoards;
    }

    public void setHoard(int player, int amount) {
        this.hoards[player] += amount;
    }

    public int getStakes() {
        return stakes;
    }

    public void setStakes(int stakes) {
        this.stakes = stakes;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getGameText() {
        return gameText;
    }

    public void setGameText(String gameText) {
        this.gameText = gameText;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getRoundLeader() {
        return roundLeader;
    }

    public void setRoundLeader(int roundLeader) {
        this.roundLeader = roundLeader;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getChoice(int num) {
        switch(num){
            case 0:
                return choice1;
            case 1:
                return choice2;
            case 2:
                return choice3;
        }
        return null;
    }

    public void setChoices(int num, String c){
        switch(num){
            case 0:
                choice1 = c;
                break;
            case 1:
                choice2 = c;
                break;
            case 2:
                choice3 = c;
                break;
        }
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public int getMoves() {
        return moves;
    }
    public void setMoves(int move){
        this.moves= move;
    }

    public void addMove() {
        this.moves++;
    }

    public int getChooseFrom() {
        return chooseFrom;
    }

    public void setChooseFrom(int amount){
        chooseFrom = amount;
    }

    public void addChooseFrom() {
        this.chooseFrom ++;
    }

    public String getChoice3() {
        return choice3;
    }

    public boolean isMage() {
        return mage;
    }

    public void setMage(boolean mage) {
        this.mage = mage;
    }
}
