package com.example.threeDragonAnte.tda;

import com.example.threeDragonAnte.game.GameComputerPlayer;
import com.example.threeDragonAnte.game.infoMsg.GameInfo;
import com.example.threeDragonAnte.tda.actions.ChoiceAction;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;

import java.util.ArrayList;
import java.util.Random;

public class TdaSmartComputerPlayer extends GameComputerPlayer {

    private TdaGameState tda;
    private ArrayList<PlayCardAction> viableMoves = new ArrayList<>();
    private Random rand = new Random();
    private int computer; //current player
    private int opponent; //opponent of the current player
    private int computerHoard; //current hoard of the player
    private int opponentHoard; //current hoard of the opponent
    private ArrayList<Card> computerFlight; //current players flight
    private ArrayList<Card> oppFlight; //opponent of the current players flight
    private ArrayList<Card> computerHand; //gets the hand of the computer
    private ArrayList<Card> oppHand; //gets the hand of the opponent
    private int oppStrength; //strength of opponent
    private int compStrength; //strength of computer

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
                Random rand = new Random();
                this.computer = playerNum; //current player
                this.opponent = Math.abs(computer-1); //opponent of the current player
                this.computerHoard = tda.getHoards()[computer]; //current hoard of the player
                this.opponentHoard = tda.getHoards()[opponent]; //current hoard of the opponent
                this.computerFlight = tda.getFlights()[computer]; //current players flight
                this.oppFlight = tda.getFlights()[opponent];//opponent of the current players flight
                this.computerHand = tda.getHands()[computer]; //gets the hand of the computer
                this.oppHand = tda.getHands()[opponent]; //gets the hand of the opponent
                this.oppStrength = 0; //strength of opponent
                this.compStrength = 0; //strength of computer

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
                        //requirements to play a black dragon if the AI has 35 gold or less
                        if (computerHoard <= 30) {
                            if (hasCard("Black Dragon") != -1) {
                                PlayCardAction pca1 = new PlayCardAction(this, hasCard("Black Dragon"), Card.FLIGHT);
                                viableMoves.add(pca1);
                            }
                        }

                        //requirements for the blue dragon if there is 1 or more evil dragons in flight
                        for (Card d: computerFlight) {
                            //Look to see if a card in the flight is an evil dragon
                            if (d.getType() == Card.EVIL) {
                                if (hasCard("Blue Dragon") != -1) {
                                    PlayCardAction pca2 = new PlayCardAction(this, hasCard("Blue Dragon"), Card.FLIGHT);
                                    viableMoves.add(pca2);
                                }
                            }
                        }

                        //requirements for the brass dragon if the opponent has a stronger flight than computer
                        if (strongestFlight() == opponent) {
                            if (hasCard("Brass Dragon") != -1) {
                                PlayCardAction pca3 = new PlayCardAction(this, hasCard("Brass Dragon"), Card.FLIGHT);
                                viableMoves.add(pca3);
                            }
                        }

                        //requirements for the Bronze Dragon is if the computer has 3 cards or less in hand
                        if (computerHand.size() <= 3) {
                            if (hasCard("Bronze Dragon") != -1) {
                                PlayCardAction pca4 = new PlayCardAction(this, hasCard("Bronze Dragon"), Card.FLIGHT);
                                viableMoves.add(pca4);
                            }
                        }

                        //requirements for copper dragon if computer has a weaker flight than the player
                        if (strongestFlight() != computer) {
                            if (hasCard("Copper Dragon") != -1) {
                                PlayCardAction pca5 = new PlayCardAction(this, hasCard("Copper Dragon"), Card.FLIGHT);
                                viableMoves.add(pca5);
                            }
                        }

                        //requirements for gold dragon if there is 1 or more good dragons in computers flight
                        //good dragon counter
                        int numGoodDragons = 0;
                        for (Card c: computerFlight) {
                            if (c.getType() == Card.GOOD) {
                                numGoodDragons++;
                            }
                        }
                        if (numGoodDragons >= 1) {
                            if (hasCard("Gold Dragon") != -1) {
                                PlayCardAction pca6 = new PlayCardAction(this, hasCard("Gold Dragon"), Card.FLIGHT);
                                viableMoves.add(pca6);
                            }
                        }

                        //requirements for green dragon if the computer low on cards or is low on gold in hoard
                        if (computerHoard <= 30 || computerHand.size() <= 3) {
                            if (hasCard("Green Dragon") != -1) {
                                PlayCardAction pca7 = new PlayCardAction(this, hasCard("Green Dragon"), Card.FLIGHT);
                                viableMoves.add(pca7);
                            }
                        }

                        //requirements for red dragon if the human has a stronger flight than the computer
                        if (strongestFlight() != computer) {
                            if (hasCard("Red Dragon") != -1) {
                                PlayCardAction pca8 = new PlayCardAction(this, hasCard("Red Dragon"), Card.FLIGHT);
                                viableMoves.add(pca8);
                            }
                        }

                        //requirements for silver dragon if the computer has 1 or more good dragons in their flight
                        for (Card c: computerFlight) {
                            if (c.getType() == Card.GOOD) {
                                if (hasCard("Silver Dragon") != -1) {
                                    PlayCardAction pca9 = new PlayCardAction(this, hasCard("Silver Dragon"), Card.FLIGHT);
                                    viableMoves.add(pca9);
                                }
                            }
                        }

                        //requirements for white dragon is if any flight has a mortal
                        for (Card c: computerFlight) {
                            if (c.getType() == Card.MORTAL) {
                                if (hasCard("White Dragon") != -1) {
                                    PlayCardAction pca10 = new PlayCardAction(this, hasCard("White Dragon"), Card.FLIGHT);
                                    viableMoves.add(pca10);
                                }
                            }
                        }

                        //requirements for mortal is priority
                        for (Card c: computerHand) {
                            if (c.getType() == Card.MORTAL) {
                                PlayCardAction pca11 = new PlayCardAction(this, computerHand.indexOf(c), Card.FLIGHT);
                                viableMoves.add(pca11);
                            }
                        }

                        //create a random number generator to choose which of the smart moves to play at random
                        int choice = rand.nextInt(viableMoves.size());
                        super.game.sendAction(viableMoves.get(choice));
                        break;
                    //dumb A.I always takes option 1
                    case TdaGameState.CHOICE:
                        super.game.sendAction(new ChoiceAction(this,1));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public int strongestFlight() {
        int strongestPlayer = 0;
        int computerStrength = 0;
        int humanStrength = 0;
        for (Card c: computerFlight) {
            computerStrength += c.getStrength();
        }
        for (Card d: oppFlight) {
            humanStrength += d.getStrength();
        }
        if (computerStrength >= humanStrength) {
            strongestPlayer = playerNum;
        }
        else if (humanStrength > computerStrength) {
            strongestPlayer = opponent;
        }
        return strongestPlayer;
    }

    public int hasCard (String name) {
        int index = -1;
        for (int i = 0; i < computerHand.size(); i++) {
            if (computerHand.get(i).getName().equals(name)) {
                return i;
            }
        }
        return index;
    }
}