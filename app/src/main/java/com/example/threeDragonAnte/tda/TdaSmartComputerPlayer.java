package com.example.threeDragonAnte.tda;

import com.example.threeDragonAnte.game.GameComputerPlayer;
import com.example.threeDragonAnte.game.infoMsg.GameInfo;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;

import java.util.ArrayList;

public class TdaSmartComputerPlayer extends GameComputerPlayer {

    private TdaGameState tda;

    /**
     * Smart A.I
     *
     * @param name the player's name (e.g., "John")
     */
    public TdaSmartComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof TdaGameState) {
            tda = new TdaGameState((TdaGameState) info);
            if (tda.getCurrentPlayer() != playerNum) {
                return;
            }
            //only moves if it's the computer's turn
            else if (tda.getCurrentPlayer() == playerNum) {
                //AI takes 1 second to make decision
                super.sleep(1000);

                //variables to be used
                int computer = tda.getCurrentPlayer(); //current player
                int opponent = Math.abs(computer-1); //opponent of the current player
                int computerHoard = tda.getHoards()[computer]; //current hoard of the player
                int opponentHoard = tda.getHoards()[opponent]; //current hoard of the opponent
                ArrayList<Card> computerFlight = tda.getFlights()[computer]; //current players flight
                ArrayList<Card> oppFlight = tda.getFlights()[opponent];//opponent of the current players flight
                ArrayList<Card> computerHand = tda.getHands()[computer]; //gets the hand of the computer
                ArrayList<Card> oppHand = tda.getHands()[opponent]; //gets the hand of the opponent
                int oppStrength = 0; //strength of opponent
                int compStrength = 0; //strength of computer

                //different decisions based on the phase of the game
                switch (tda.getPhase()) {

                    //smart A.I chooses the strongest card in hand in the ANTE phase
                    case TdaGameState.ANTE:
                        //find the strongest card in the A.Is hand
                        int strongestCard = 0;
                        int indexOfCard = 0;
                        for (Card c: computerHand) {
                            if (c.getStrength() > strongestCard && c.getType()!=Card.MORTAL ) {
                                //set the strongest card to the strength found and the index of card
                                strongestCard = c.getStrength();
                                indexOfCard = computerHand.indexOf(c);
                            }
                        }
                        PlayCardAction pca = new PlayCardAction(this,indexOfCard,Card.ANTE);
                        super.game.sendAction(pca);
                        break;

                        //smart A.I plays a card based on it's situation
                    case TdaGameState.ROUND:
                        //create an array list for viable moves so in the end a random generator
                        //will pick a smart move to play
                        ArrayList<PlayCardAction> viableMoves = new ArrayList<>();

                        //requirements to play a black dragon if the AI has 35 gold or less
                        if (computerHoard <= 35) {
                            for (Card c: computerHand) {
                                //check to see if the computer has a black dragon
                                if (c.getName().equals("Black Dragon")) {
                                    //if there is a black dragon place it into the viable moves arraylist
                                    int index = computerHand.indexOf(c);
                                    PlayCardAction pca1 = new PlayCardAction(this, index, Card.FLIGHT);
                                    viableMoves.add(pca1);
                                }
                            }
                        }

                        //requirements for the blue dragon if there is 1 or more evil dragons in flight
                        for (Card d: computerFlight) {
                            //Look to see if a card in the flight is an evil dragon
                            if (d.getType() == Card.EVIL) {
                                //if so than look in computers hand for a blue dragon
                                for (Card e: computerHand) {
                                    //check to see if the computer has a blue dragon
                                    if (e.getName().equals("Blue Dragon")) {
                                        //if there is a blue dragon than place the dragon into the viable moves arraylist
                                        int index = computerHand.indexOf(e);
                                        PlayCardAction pca2 = new PlayCardAction(this, index, Card.FLIGHT);
                                        viableMoves.add(pca2);
                                    }
                                }
                            }
                        }

                        //requirements for the brass dragon if the opponent has a stronger flight than computer
                        for (Card f: oppFlight) {
                            oppStrength += f.getStrength();
                        }
                        for (Card g: computerFlight) {
                            compStrength += g.getStrength();
                        }
                        if (oppStrength > compStrength) {
                            for (Card c: computerHand) {
                                //check to see if the computer has a brass dragon
                                if (c.getName().equals("Brass Dragon")) {
                                    //if there is a brass dragon than place the dragon into the viablemoves arraylist
                                    int index = computerHand.indexOf(c);
                                    PlayCardAction pca3 = new PlayCardAction(this, index, Card.FLIGHT);
                                    viableMoves.add(pca3);
                                }
                            }
                        }
                        //reset the strengths back to 0
                        oppStrength = 0;
                        compStrength = 0;

                        //requirements for the Bronze Dragon is if the computer has 3 cards or less in hand
                        if (computerHand.size() <= 3) {
                            for (Card c: computerHand) {
                                //check to see if computer has a bronze dragon
                                if (c.getName().equals("Bronze Dragon")) {
                                    //if there is a brass dragon than place the dragon into the viablemoves arraylist
                                    int index = computerHand.indexOf(c);
                                    PlayCardAction pca4 = new PlayCardAction(this, index, Card.FLIGHT);
                                    viableMoves.add(pca4);
                                }
                            }
                        }

                        //requirements for copper dragon if computer has a weaker flight than the player
                        for (Card c: oppFlight) {
                            oppStrength += c.getStrength();
                        }
                        for (Card d: computerFlight) {
                            compStrength += d.getStrength();
                        }
                        if (compStrength < oppStrength) {
                            for (Card c: computerHand) {
                                //check to see if computer has a bronze dragon
                                if (c.getName().equals("Copper Dragon")) {
                                    //if there is a copper dragon than place the dragon into the viablemoves arraylist
                                    int index = computerHand.indexOf(c);
                                    PlayCardAction pca5 = new PlayCardAction(this, index, Card.FLIGHT);
                                    viableMoves.add(pca5);
                                }
                            }
                        }

                        //requirements for gold dragon if there is 1 or more good dragons in computers flight

                }
            }
        }
    }
}