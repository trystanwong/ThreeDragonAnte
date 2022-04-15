package com.example.threeDragonAnte.tda;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Card
 *
 * Representation of what a specific card is within the game
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class Card implements Serializable {

    private static final long serialVersionUID = -4269730007792L;
    //instance variables

    private String name;//name of the card
    private String power;//text found on card
    private int strength;//card strength (1-13)
    private int type;//type of card: (good dragon, evil dragon, or mortal)
    private int placement;//where the card is with respect to the "game"
    private boolean playable;//can this card be played (used for choices)

    //constants for the different placements of each card
    //This will determine the visibility of each card from the perspective of each player
    public static final int DECK = 0;
    public static final int HAND = 1;
    public static final int FLIGHT = 2;
    public static final int ANTE = 3;
    public static final int DISCARD = 4;

    //constant variables for stating what this specific cards type is
    public static final int GOOD = 0;
    public static final int EVIL = 1;
    public static final int MORTAL = 2;

    /**
     * Default constructor for a Card
     */
    public Card(){

        this.name = "";
        this.strength = 0;
        this.type = GOOD;
        this.power = null;
        this.placement = DECK; // card starts off in the deck

    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    /**
     * Card: constructor for initializing the card
     *
     * @param initName: Name of the card
     * @param initStrength: Strength of the card
     *
     */
    public Card(String initName, int initStrength, int initType) {

        name = initName;
        strength = initStrength;
        power = null;//implemented later
        type = initType;
        placement = DECK;//starts in deck
        playable = false;

    }

    /**
     * Card: copy constructor for the card class
     *
     * @return none
     * @param cardCopy: copy of the original card class
     */
    public Card(Card cardCopy) {
        this.playable = cardCopy.playable;
        this.name = cardCopy.name;
        this.strength = cardCopy.strength;
        this.power = cardCopy.power;
        this.type = cardCopy.type;
        this.placement = cardCopy.placement;
    }

    /**
     * getType: A method for determining what type a card is
     *
     * @return String: a String for what the type of card is
     */
    public int getType() {
        return type;
    }

    //getter for card's placement
    public int getPlacement() {
        //A string defining where the card is currently within the game
        return placement;
    }

    //setter for cards placement
    public void setPlacement(int place){
        placement = place;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }
    public int getStrength(){
        return strength;
    }
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * toString: method for instantiating the variables and turning them into a String
     *
     * @return String: Description of the variables
     */
    @Override
    public String toString(){
        return name + " Strength: " +strength;
    }


    /**
     * Building the deck of all possible cards given information from the official rule book
     *
     * @return  ArrayList<Card> - deck of cards
     */
    public static ArrayList<Card> buildDeck(){
        ArrayList<Card> deck = new ArrayList();

        //Copper dragon
        deck.add(new Card("Copper Dragon", 1, GOOD));
        deck.add(new Card("Copper Dragon", 3, GOOD));
        deck.add(new Card("Copper Dragon", 5, GOOD));
        deck.add(new Card("Copper Dragon", 7, GOOD));
        deck.add(new Card("Copper Dragon", 8, GOOD));
        deck.add(new Card("Copper Dragon", 10, GOOD));

        //All Mortals
        deck.add(new Card("The Fool",3,MORTAL));
        deck.add(new Card("The Princess",4,MORTAL));
        deck.add(new Card("The Priest",5,MORTAL));
        deck.add(new Card("The Druid",6,MORTAL));
        deck.add(new Card("The Thief",7,MORTAL));
        deck.add(new Card("The DragonSlayer",8,MORTAL));
        deck.add(new Card("The Archmage",9,MORTAL));

        //All Red Dragons
        deck.add(new Card("Red Dragon", 2, EVIL));
        deck.add(new Card("Red Dragon", 3, EVIL));
        deck.add(new Card("Red Dragon", 5, EVIL));
        deck.add(new Card("Red Dragon", 8, EVIL));
        deck.add(new Card("Red Dragon", 10, EVIL));
        deck.add(new Card("Red Dragon", 12, EVIL));

        //BLACK DRAGONS
        deck.add(new Card("Black Dragon", 1, EVIL));
        deck.add(new Card("Black Dragon", 2, EVIL));
        deck.add(new Card("Black Dragon", 3, EVIL));
        deck.add(new Card("Black Dragon", 5, EVIL));
        deck.add(new Card("Black Dragon", 7, EVIL));
        deck.add(new Card("Black Dragon", 9, EVIL));

        //BLUE DRAGONS
        deck.add(new Card("Blue Dragon", 1, EVIL));
        deck.add(new Card("Blue Dragon", 2, EVIL));
        deck.add(new Card("Blue Dragon", 4, EVIL));
        deck.add(new Card("Blue Dragon", 7, EVIL));
        deck.add(new Card("Blue Dragon", 9, EVIL));
        deck.add(new Card("Blue Dragon", 11, EVIL));

        //BRASS DRAGONS
        deck.add(new Card("Brass Dragon", 1, GOOD));
        deck.add(new Card("Brass Dragon", 2, GOOD));
        deck.add(new Card("Brass Dragon", 4, GOOD));
        deck.add(new Card("Brass Dragon", 5, GOOD));
        deck.add(new Card("Brass Dragon", 7, GOOD));
        deck.add(new Card("Brass Dragon", 9, GOOD));

        //Gold dragons
        deck.add(new Card("Gold Dragon", 2, GOOD));
        deck.add(new Card("Gold Dragon", 4, GOOD));
        deck.add(new Card("Gold Dragon", 6, GOOD));
        deck.add(new Card("Gold Dragon", 9, GOOD));
        deck.add(new Card("Gold Dragon", 11, GOOD));
        deck.add(new Card("Gold Dragon", 13, GOOD));

        //White Dragon
        deck.add(new Card("White Dragon", 1, EVIL));
        deck.add(new Card("White Dragon", 2, EVIL));
        deck.add(new Card("White Dragon", 3, EVIL));
        deck.add(new Card("White Dragon", 4, EVIL));
        deck.add(new Card("White Dragon", 6, EVIL));
        deck.add(new Card("White Dragon", 8, EVIL));

        //Silver Dragons
        deck.add(new Card("Silver Dragon", 2, GOOD));
        deck.add(new Card("Silver Dragon", 3, GOOD));
        deck.add(new Card("Silver Dragon", 6, GOOD));
        deck.add(new Card("Silver Dragon", 8, GOOD));
        deck.add(new Card("Silver Dragon", 10, GOOD));
        deck.add(new Card("Silver Dragon", 12, GOOD));

        //Bronze Dragons
        deck.add(new Card("Bronze Dragon", 1, GOOD));
        deck.add(new Card("Bronze Dragon", 3, GOOD));
        deck.add(new Card("Bronze Dragon", 6, GOOD));
        deck.add(new Card("Bronze Dragon", 7, GOOD));
        deck.add(new Card("Bronze Dragon", 9, GOOD));
        deck.add(new Card("Bronze Dragon", 11, GOOD));

        //Green Dragons
        deck.add(new Card("Green Dragon", 1, EVIL));
        deck.add(new Card("Green Dragon", 2, EVIL));
        deck.add(new Card("Green Dragon", 4, EVIL));
        deck.add(new Card("Green Dragon", 6, EVIL));
        deck.add(new Card("Green Dragon", 8, EVIL));
        deck.add(new Card("Green Dragon", 10, EVIL));

        //God Dragons
        deck.add(new Card("Tiamat", 13, EVIL));
        deck.add(new Card("Dracolich", 10, EVIL));
        deck.add(new Card("Bahamut", 13, GOOD));
        
        return deck;

    }
}