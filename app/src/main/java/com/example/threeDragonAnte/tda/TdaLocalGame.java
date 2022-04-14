package com.example.threeDragonAnte.tda;

import android.app.Activity;

import com.example.threeDragonAnte.game.GameMainActivity;
import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.LocalGame;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.actions.BuyCardAction;
import com.example.threeDragonAnte.tda.actions.ChoiceAction;
import com.example.threeDragonAnte.tda.actions.ConfirmAction;
import com.example.threeDragonAnte.tda.actions.DiscardCardAction;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class TdaLocalGame extends LocalGame implements Serializable {

    private TdaGameState tda;

    public TdaLocalGame(){
        tda = new TdaGameState();

    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        //current game state
        TdaGameState update = new TdaGameState(tda);
        p.sendInfo(update);
    }

    @Override
    protected boolean canMove(int playerIdx) {
        if(tda.getCurrentPlayer() == playerIdx){
            return true;
        }
        return false;
    }

    @Override
    protected String checkIfGameOver() {
        if(tda.getHoards()[0]<=0){
            return playerNames[1]+" has won the game!";
        }
        else if(tda.getHoards()[1]<=0){
            return playerNames[0]+" has won the game!";
        }
        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {

        //info needed for moves
        int player = tda.getCurrentPlayer();
        int opponent = Math.abs(tda.getCurrentPlayer()-1);
        int stakes = tda.getStakes();
        ArrayList<Card> hand = tda.getHands()[player];
        ArrayList<Card> opFlight = tda.getFlights()[opponent];
        ArrayList<Card> playerFlight = tda.getFlights()[player];
        ArrayList<Card> antePile = tda.getAnte();

        //if the player tries to buy a card
        if(action instanceof BuyCardAction){
            if(hand.size()>=10){
                return false;
            }
            else{
                buyCard(player);
                return true;
            }
        }

        //player is discarding a card from their hand
        if(action instanceof DiscardCardAction){

            //index of the card being discarded
            int index = ((DiscardCardAction) action).getDiscard();

            //if you are discarding because of a thief
            if(tda.getLast()[player].getName().equals("The Thief")){
                Card discarded = hand.get(index);
                discarded.setPlacement(Card.DISCARD);
                tda.getDiscard().add(discarded);
                hand.remove(index);
                tda.setDiscarding(false);
                tda.setPhase(TdaGameState.ROUND);
                for (Card c : hand) {
                    c.setPlayable(false);
                }
                return turnHelper();
            }
            else {
                //name of the card the opponent just played
                String name = tda.getLast()[opponent].getName();

                //if a green dragon or tiamat were played by the opponent
                if(name.equals("Green Dragon")||name.equals("Tiamat")){
                    if (hand.get(index).getStrength() >= tda.getLast()[opponent].getStrength()) {
                        return false;
                    }
                    else {
                        Card discarded = hand.get(index); //card being discarded
                        discarded.setPlayable(false);
                        //adding the discarded card to the players hand
                        tda.getHands()[opponent].add(discarded);
                        hand.remove(index);
                        tda.setDiscarding(false);
                        tda.setChoosing(false);
                        tda.setPhase(TdaGameState.ROUND);
                        sendUpdatedStateTo(players[1]);
                        //removing the green background from the playable cards
                        for (Card c : hand) {
                            c.setPlayable(false);
                        }
                        return turnHelper();
                    }
                }
                //if the brass dragon was played by the opponent
                else if(name.equals("Brass Dragon")){
                    if (hand.get(index).getStrength() <= tda.getLast()[opponent].getStrength()) {
                        return false;
                    } else {
                        Card discarded = hand.get(index);//card being discarded
                        discarded.setPlayable(false);

                        //adding the card to the opponents hand
                        tda.getHands()[opponent].add(discarded);
                        hand.remove(index);
                        tda.setDiscarding(false);
                        tda.setChoosing(false);
                        tda.setPhase(TdaGameState.ROUND);
                        //removing the green background from the playable cards
                        for (Card c : hand) {
                            c.setPlayable(false);
                        }
                        return turnHelper();
                    }
                }
            }
        }

        //if the player is confirming a text
        else if(action instanceof ConfirmAction){

            //pressing the play button to begin the game
            if(tda.getPhase()==TdaGameState.BEGIN_GAME){
                tda.setPhase(TdaGameState.ANTE);
                return true;
            }
            //confirming the end of the gambit
            else if(tda.getRound()>=3){
                clearBoard();
                tda.setPhase(TdaGameState.ANTE);
                tda.setCurrentPlayer(0);
                return true;
            }
            //confirming either the end of a round, or the end of the ante
            else {
                tda.setCurrentPlayer(tda.getRoundLeader());
                tda.setPhase(TdaGameState.ROUND);
                return true;
            }
        }
        //a player is making a choice
        else if(action instanceof ChoiceAction){
            int choice = ((ChoiceAction) action).getChoiceNum();

            //if the opponent gave you a choice
            if(!tda.isChoosing()) {
                String name = tda.getLast()[opponent].getName();
                switch (name) {

                    //if either the Tiamat or Green Dragon were played
                    case "Tiamat":
                    case "Green Dragon":
                        //only give them the option if they have cards that they can play
                        if(tda.getChooseFrom()==2){
                            tda.setChooseFrom(0);
                            tda.setChoosing(false);
                            tda.setGameText("Choose a dragon from your hand with" +
                                    " a strength less than "
                                    + tda.getLast()[opponent].getStrength());
                            tda.setPhase(TdaGameState.DISCARD);
                            return true;
                        }
                        //paying 5 gold to the opponent
                        else if(tda.getChooseFrom()==1||choice == 1){
                            tda.setChooseFrom(0);
                            tda.setHoard(opponent, 5);
                            tda.setHoard(player, -5);
                            tda.setChoosing(false);
                            tda.setDiscarding(false);
                            tda.setPhase(TdaGameState.ROUND);
                            return turnHelper();
                        }
                        break;
                    case "Brass Dragon":
                        if (tda.getChooseFrom()==2||choice==0) {
                            tda.setChooseFrom(0);
                            tda.setChoosing(false);
                            tda.setGameText("Choose a dragon from your hand with" +
                                    " a strength greater than "
                                    + tda.getLast()[opponent].getStrength());
                            tda.setPhase(TdaGameState.DISCARD);
                        return true;
                        }
                        //paying 5 gold to the opponent if you don't have any cards to discard
                        else if(tda.getChooseFrom()==1||choice == 1){
                            tda.setHoard(opponent, 5);
                            tda.setHoard(player, -5);
                            tda.setPhase(TdaGameState.ROUND);
                            tda.setChooseFrom(0);
                            tda.setChoosing(false);
                            tda.setDiscarding(false);
                            return turnHelper();
                        }
                }
            }
            //if you played a card that gave you a choice
            else if(tda.isChoosing()) {
                switch (tda.getLast()[player].getName()) {

                    case "The Princess":
                        switch(playerFlight.get(choice).getName()){
                            case "Brass Dragon":
                                tda.setChoosing(false);
                                tda.getLast()[player].setName("Brass Dragon");
                                return brassDragon();
                            default:
                                powers(playerFlight.get(choice).getName());
                                break;
                        }
                        tda.setPhase(TdaGameState.ROUND);
                        tda.setChooseFrom(0);
                        break;
                    case "The Archmage":
                        tda.setLast(player,tda.getAnte().get(choice));
                        tda.setChoosing(false);
                        tda.setDiscarding(false);
                        tda.setChooseFrom(0);
                        tda.setPhase(TdaGameState.ROUND);
                        sendAction(new PlayCardAction(super.players[player],choice,Card.FLIGHT));
                        return true;
                    case "Tiamat":
                        int num = 0;
                        for (Card check : tda.getFlights()[player]) {
                            if (check.getType() == Card.EVIL) {
                                num++;
                            }
                        }
                        if (choice == 0) {
                            tda.setHoard(player, num);
                            tda.setStakes(stakes - num);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        if (choice == 1) {
                            tda.setStakes(stakes + num);
                            tda.setHoard(opponent, -num);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        tda.setChooseFrom(0);
                        tda.setChoosing(false);
                        return greenDragon();

                    case "Dracolich":
                        //evil dragon's power is copied
                        switch(tda.getFlights()[opponent].get(choice).getName()){
                            case "Blue Dragon":
                                tda.getLast()[player].setName("Blue Dragon");
                                blueDragon();
                                return true;
                            case "Green Dragon":
                                tda.getLast()[player].setName("Green Dragon");
                                return greenDragon();
                            default:
                                powers(tda.getFlights()[opponent].get(choice).getName());
                                break;
                        }
                        tda.setPhase(TdaGameState.ROUND);
                        tda.setChooseFrom(0);
                        break;
                    case "The DragonSlayer":
                        //pay one to the stakes
                        tda.setHoard(player, -1);

                        //removing the card from opponent flight
                        Card removed = tda.getFlights()[opponent].get(choice);
                        removed.setPlacement(Card.DISCARD);
                        tda.getDiscard().add(removed);
                        tda.getFlights()[opponent].remove(choice);

                        //returning back to the round
                        tda.setDiscarding(false);
                        tda.setChoosing(false);
                        tda.setPhase(TdaGameState.ROUND);
                        tda.setChooseFrom(0);
                        break;

                    case "Blue Dragon":
                        //checking for the amount of blue dragons in the hand
                        int numEvil = 0;
                        for (Card check : tda.getFlights()[player]) {
                            if (check.getType() == Card.EVIL) {
                                numEvil++;
                            }
                        }
                        if (choice == 0) {
                            tda.setHoard(player, numEvil);
                            tda.setStakes(stakes-numEvil);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        if (choice == 1) {
                            tda.setStakes(stakes + numEvil);
                            tda.setHoard(opponent, -numEvil);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        tda.setChooseFrom(0);
                        tda.setChoosing(false);
                        tda.setDiscarding(false);
                        break;
                }
                tda.setChoosing(false);
            }
            return turnHelper();
        }

        //user tries to play a card (add it to the flight)
        if(action instanceof PlayCardAction){

            //if the played card is going to an ante
            if (((PlayCardAction) action).getPlacement()==Card.ANTE) {

                //adding the card to the ante and removing it from the hand
                int index = ((PlayCardAction) action).getIndex();
                Card ante = hand.get(index);
                ante.setPlacement(Card.ANTE);
                tda.getAnte().add(new Card(hand.get(index)));
                tda.getHands()[player].remove(index);
                //if both players have played an ante the round begins
                if(antePile.size()>1){
                    tda.setRoundLeader(roundLeader());
                    tda.setGameText(playerNames[tda.getRoundLeader()]+" won the ante.");
                    tda.setChoice1("START ROUND");
                    tda.setPhase(TdaGameState.CONFIRM);
                    tda.setCurrentPlayer(0);
                    return true;
                }
                else{
                    //the other player gets to choose their ante if they haven't already
                    tda.setCurrentPlayer(opponent);
                }
                return true;
            }

            //if the card being played is going to a flight
            if (((PlayCardAction) action).getPlacement()==Card.FLIGHT){

                //add the card to the flight (unless the archmage was played)
                int index = ((PlayCardAction) action).getIndex();
                Card flight;
                if(!tda.isMage()) {
                    //counts as a move (used for counting rounds)
                    tda.addMove();
                    flight = hand.get(index);
                    tda.setLast(player, flight); //keeps track of the card that was played
                    flight.setPlacement(Card.FLIGHT);
                    tda.getFlights()[player].add(new Card(hand.get(index)));
                    tda.getHands()[player].remove(index);
                }
                //if the mage was played, the card activated is from the ante pile
                else{
                    flight = antePile.get(index);
                    flight.setStrength(9);
                    tda.setMage(false);
                }

                //triggering the power of a card
                powers(flight.getName());

                //cards that have choices
                switch(flight.getName()){
                    case "The Princess":
                        tda.setGameText("Choose which power to activate first: ");
                        for(int i = 0; i < playerFlight.size();i++){
                            Card p = playerFlight.get(i);
                            //only discards cards that are dragons
                            // and weaker than the dragon slayer
                            if(p.getType()==Card.GOOD){
                                tda.addChooseFrom();
                                tda.setChoices(tda.getChooseFrom()-1,
                                        (i+1)+". "+playerFlight.get(i).toString());
                            }
                        }
                        if(tda.getChooseFrom()==0){
                            break;
                        }
                        else {
                            tda.setPhase(TdaGameState.CHOICE);
                            tda.setChoosing(true);
                            return true;
                        }
                    case "Dracolich":
                        tda.setGameText("Choose a card to copy its power:");
                        //looking through the opponents flight to see what cards you can copy
                        for(int i = 0; i < opFlight.size();i++){
                            Card d = opFlight.get(i);
                            //only copies cards that are evil
                            if(d.getType()==Card.EVIL){
                                tda.addChooseFrom();
                                tda.setChoices(tda.getChooseFrom()-1,
                                        (i+1)+". "+opFlight.get(i).toString());
                            }
                        }
                        if(tda.getChooseFrom()==0){
                            break;
                        }
                        else {
                            tda.setPhase(TdaGameState.CHOICE);
                            tda.setChoosing(true);
                            return true;
                        }

                    case "The DragonSlayer":
                        for(int i = 0; i < opFlight.size();i++){
                            Card d = opFlight.get(i);
                            //only discards cards that are dragons
                            // and weaker than the dragon slayer
                            if(d.getStrength()<8&&d.getType()!=Card.MORTAL){
                                tda.addChooseFrom();
                                //adds possible choices
                                tda.setChoices(tda.getChooseFrom()-1,
                                        (i+1)+". "+opFlight.get(i).toString());
                            }
                        }
                        //looking through the opponents flight to see what cards you can discard
                        if(tda.getFlights()[opponent].size()==0){
                            break;
                        }
                        if(tda.getChooseFrom()==0){
                            break;
                        }
                        else {
                            tda.setGameText("Choose a card to discard from opponent's flight:");
                            tda.setPhase(TdaGameState.CHOICE);
                            tda.setChoosing(true);
                            return true;
                        }
                    case "The Thief":
                        //if there are no cards to discard it does nothing
                        if(playerFlight.size()==0){
                            break;
                        }
                        else {
                            //steal 7 g from the stakes
                            tda.setHoard(player, +7);
                            tda.setStakes(stakes - 7);
                            for (Card h : hand) {
                                h.setPlayable(true);
                            }
                            tda.setDiscarding(true);
                            tda.setGameText("Choose a card from your hand to discard.");
                            tda.setPhase(TdaGameState.DISCARD);
                            return true;
                        }
                    case "The Archmage":
                        //if theres no cards in the ante, cant copy anything
                        if(tda.getAnte().size()==0){
                            break;
                        }
                        else{
                            //player pays 1 to the stakes
                            tda.setHoard(player,-1);
                            tda.setStakes(stakes+1);

                            //getting all the cards in the ante you can choose from
                            ArrayList<Card> checkAnte = tda.getAnte();
                            for(int i = 0; i<checkAnte.size();i++){
                                tda.addChooseFrom();
                                tda.setChoices(tda.getChooseFrom()-1,
                                        (i+1)+". "+checkAnte.get(i).toString());
                            }
                            tda.setGameText("Choose a card from the ante to copy its power:");
                            tda.setChoosing(true);
                            tda.setMage(true);
                            tda.setPhase(TdaGameState.CHOICE);
                            return true;
                        }
                    case "Tiamat":
                        powers("Black Dragon");
                        powers("Red Dragon");
                        powers("White Dragon");
                        return blueDragon();
                    case "Blue Dragon":
                        return blueDragon();
                    case "Green Dragon":
                        return greenDragon();
                    case "Brass Dragon":
                        if(strongestFlight()==opponent) {
                            return brassDragon();
                        }
                        break;
                    case "Copper Dragon":
                        Random rand = new Random();
                        //find where the copper dragon has been placed
                        for (Card d: playerFlight) {
                            //if the card is a copper dragon than remove the card from the flight
                            if (d.getName().equals("Copper Dragon")) {
                                playerFlight.remove(d);
                            }
                        }
                        //get the card from the deck
                        int i = rand.nextInt(tda.getDeck().size());
                        Card c = tda.getDeck().get(i);
                        //remove the card from the deck
                        tda.getDeck().remove(i);
                        c.setPlacement(Card.HAND);
                        tda.getHands()[player].add(c);
                        int size = tda.getHands()[player].size();
                        //add the card to the flight and activate the power
                        sendAction(new PlayCardAction(players[player],size-1,Card.FLIGHT));
                        return true;

                }
                return turnHelper();
            }
        }
        return false;
    }

    /**
     * helper method if a green dragon is played
     * used for the dracolich
     */
    public boolean greenDragon(){

        int opponent = Math.abs(tda.getCurrentPlayer()-1);
        int player = tda.getCurrentPlayer();
        ArrayList<Card> oppHand = tda.getHands()[opponent];
        //if the player doesnt have any playable cards for the power,
        //dont present the first option to them
        boolean canDiscard = false;
        for(int i = 0;i<oppHand.size();i++){
            if(oppHand.get(i).getStrength()<tda.getLast()[player].getStrength()
                    &&oppHand.get(i).getType()!=Card.MORTAL){
                canDiscard = true;
                oppHand.get(i).setPlayable(true);
            }
        }
        //setting the options for the opponent accordingly
        if(canDiscard){
            tda.setChooseFrom(2);
            tda.setGameText("Choose:");
            tda.setChoice1("Give a weaker dragon from your hand to your opponent.");
            tda.setChoice2("Pay your opponent 5 gold.");
        }
        else{
            tda.setChooseFrom(1);
            tda.setGameText("No weaker dragons in your hand, you must:");
            tda.setChoice1("Pay your opponent 5 gold.");
        }
        tda.setPhase(TdaGameState.CHOICE);
        tda.setDiscarding(true);
        tda.setCurrentPlayer(opponent);
        return true;
    }

    /**
     * helper method if a brass dragon is played
     * used for the dracolich
     */
    public boolean brassDragon(){

        int opponent = Math.abs(tda.getCurrentPlayer()-1);
        int player = tda.getCurrentPlayer();
        ArrayList<Card> oppHand = tda.getHands()[opponent];

        //if the player doesnt have any playable cards for the power,
        //dont present the first option to them
        boolean canDiscard = false;
        for(int i = 0;i<oppHand.size();i++){
            //checking for dragons that are stronger than the played brass dragon
            if(oppHand.get(i).getStrength()>tda.getLast()[player].getStrength()
                    &&oppHand.get(i).getType()!=Card.MORTAL){
                canDiscard = true;
                oppHand.get(i).setPlayable(true);
            }
        }
        //setting the options for the opponent accordingly
        if(canDiscard){
            tda.setChooseFrom(2);
            tda.setGameText("Choose:");
            tda.setChoice1("Give a stronger dragon from your hand to your opponent.");
            tda.setChoice2("Pay your opponent 5 gold.");
        }
        else{
            tda.setChooseFrom(1);
            tda.setGameText("No stronger dragons in your hand, you must:");
            tda.setChoice1("Pay your opponent 5 gold.");
        }
        tda.setPhase(TdaGameState.CHOICE);
        tda.setDiscarding(true);
        tda.setCurrentPlayer(opponent);
        return true;
    }


    /**
     * helper method if a blue dragon is played
     * used for the dracolich
     */
    public boolean blueDragon(){
        //checking how many evil cards are in the player's flight to
        //determine how much gold they would be able to steal
        int numEvil = 0;
        for(Card check : tda.getFlights()[tda.getCurrentPlayer()]){
            if(check.getType()==Card.EVIL){
                numEvil++;
            }
        }
        tda.setChooseFrom(2);
        tda.setGameText("Choose one:");
        tda.setChoice1("Steal "+numEvil+" gold from the stakes.");
        tda.setChoice2("Opponent pays "+numEvil+" gold to the stakes.");
        tda.setPhase(TdaGameState.CHOICE);
        tda.setChoosing(true);
        return true;
    }

    /**
     * decides the winner of an ante and who will be leading the road
     * @return the winner of the round
     */
    public int roundLeader(){
        //if its currently an Ante
        if(tda.getPhase() == TdaGameState.ANTE){
            //decides who's ante was stronger
            ArrayList<Card> ante = tda.getAnte();
            //defaulted to first ante card as the strongest
            int strongest = ante.get(0).getStrength();
            if(ante.get(1).getStrength()>strongest){
                strongest = ante.get(1).getStrength();
                //players pay to the stakes
                tda.setHoard(0,-strongest);
                tda.setHoard(1,-strongest);
                tda.setStakes(2*strongest);
                return 1;
            }
            //players pay to the stakes
            tda.setHoard(0,-strongest);
            tda.setHoard(1,-strongest);
            tda.setStakes(2*strongest);
        }
        else{

            //deciding who won the round by looking at the strength of each players flight card
            Card player0 = tda.getLast()[0];
            Card player1 = tda.getLast()[1];

            //if the priest was played that player automatically wins the round
            for(int i =0; i < 2; i++){
                if(tda.getLast()[i].getName().equals("The Priest")){
                    return i;
                }
            }
            //otherwise return the player with the strongest card played
            if(player0.getStrength()>player1.getStrength()){
                return 0;
            }
            return 1;
        }
        return 0;
    }

    /**
     * Decides which of the players has the strongest flight
     * @return - the player with the strongest flight
     */
    public int strongestFlight(){
        //calculating the winner of the gambit based on the strengths of their flight
        int strength0 = 0;
        int strength1 = 0;
        int winner = 0;
        for(int i = 0; i < 2; i++){
            for(Card c : tda.getFlights()[i]){
                if(i==0){
                    strength0+=c.getStrength();
                }
                else{
                    strength1+=c.getStrength();
                }
            }
        }
        if(strength0<strength1){
            winner = 1;
        }
        return winner;
    }
    /**
     * player pays to the stakes to "buy" or draw a card from the deck
     * @param player - player buying
     */
    public void buyCard(int player) {

        //used for where the top card of the deck went in the hand
        int size = tda.getHands()[player].size();
        if(size>1){
            tda.drawCard(player);
        }
        if(tda.getDeck().size()<=4){
            for(int i = tda.getDiscard().size()-1; i>=0; i--) {
                tda.getDeck().add(tda.getDiscard().get(i));
                tda.getDiscard().remove(i);
            }
        }
        while(tda.getHands()[player].size()<4) {
            tda.drawCard(player);
        }

        //player has to pay the top card of the decks strength in gold to the stakes
        int payment = tda.getHands()[player].get(size).getStrength();
        tda.setHoard(player,-payment);
        tda.setStakes(tda.getStakes()+payment);

    }

    /**
     * helps with moving the game along when multiple decisions are made
     * checks if the round or gambit is over
     */
    public boolean turnHelper(){

        int player= tda.getCurrentPlayer();
        int opponent = Math.abs(player-1);

        //if the round is over (two cards have been played)
        //and if the gambit is over
        if(tda.getMoves()%2==0&&!tda.isDiscarding()){
            tda.setRound(tda.getRound()+1);
            tda.setRoundLeader(roundLeader());
            tda.setGameText(super.playerNames[tda.getRoundLeader()] +
                    " won that round");
            tda.setChoice1("OK");
            //if the player has only 1 card at the beginning of their turn, they have to buy
            //a card
            if(tda.getHands()[player].size()<=1){
                buyCard(player);
            }
            if(tda.getHands()[opponent].size()<=1){
                buyCard(opponent);
            }
            tda.setLast(0,new Card());
            tda.setLast(1,new Card());
            if(tda.getRound()>=3){
                tda.setGameText(super.playerNames[endGambit()] +
                        " won that gambit");
                tda.setChoice1("Start new gambit.");
            }
            tda.setPhase(TdaGameState.CONFIRM);
            tda.setCurrentPlayer(0);
            return true;
        }
        //if no card was played yet (used for green dragon)
        else if(tda.getLast()[player].getName().equals("")){
            return true;
        }
        else {
            tda.setCurrentPlayer(opponent);
            return true;
        }
    }

    /**
     * Clears the cards on the board to the discard pile to start a new gambit
     */
    public void clearBoard(){

        //rounds start at 0 again
        tda.setRound(0);
        tda.setLast(0,new Card());
        tda.setLast(1,new Card());
        tda.setMoves(0);

        //clearing the flights for the next gambit
        for (int i = 0; i < 2; i++) {
            for (int j = tda.getFlights()[i].size() - 1; j >= 0; j--) {
                Card flight = tda.getFlights()[i].get(j);
                flight.setPlacement(Card.DISCARD);
                tda.getDiscard().add(flight);
                tda.getFlights()[i].remove(j);
            }
        }
        //clear both antes
        for (int i = tda.getAnte().size() - 1; i >= 0; i--) {
            Card ante = tda.getAnte().get(i);
            ante.setPlacement(Card.DISCARD);
            tda.getDiscard().add(ante);
            tda.getAnte().remove(i);
        }
    }

    /**
     * if the game is at the end of the gambit, a winner is decided
     * @return the winner of the gambit
     */
    public int endGambit() {

        //the winner of the gambit is the one with the strongest flight
        int winner = strongestFlight();

        //checking for cards that affect the winner of a gambit
        boolean good = false;
        boolean bad = false;
        for(int i = 0; i < 2; i++){
            for(Card c : tda.getFlights()[i]){
                if(c.getType()==Card.GOOD){
                    good=true;
                }
                if(c.getType()==Card.EVIL){
                    bad = true;
                }
                //if the druid was played, the weakest flight wins
                if(c.getName().equals("The Druid")){
                    return Math.abs(winner-1);
                }
                //cant win if you have tiamat and a good dragon in your hand
                if(c.getName().equals("Tiamat")&&good==true){
                    return Math.abs(i-1);
                }
                //cant win if you have Bahamut and a bad dragon in your hand
                if(c.getName().equals("Bahamut")&&bad==true){
                    return Math.abs(i-1);
                }
            }
        }

        tda.setHoard(winner,tda.getStakes()); //winner of the gambit gets the stakes
        tda.setStakes(0); //clear the stakes

        //shuffling the deck if there are not enough cards for each player to draw twice
        if(tda.getDeck().size()<=4){
            for(int i = tda.getDiscard().size()-1; i>=0; i--) {
                tda.getDeck().add(tda.getDiscard().get(i));
                tda.getDiscard().remove(i);
            }
        }
        //each player draws twice
        for(int i = 0; i<2;i++){
            if(tda.getHands()[i].size()<=8){
                tda.drawCard(i);
                tda.drawCard(i);
            }
        }
        return winner;
    }

    /**
     * Triggers the power of a card when played
     *
     * @param name - name of the card being played
     */
    public void powers(String name){

        //info needed from the game state for powers
        Random rand = new Random();
        int player = tda.getCurrentPlayer(); //current player
        int opponent = Math.abs(player-1); //opponent of the current player
        int stakes = tda.getStakes(); //current stakes of the gambit
        ArrayList<Card> playerFlight = tda.getFlights()[player]; //current players flight
        ArrayList<Card> oppFlight = tda.getFlights()[opponent];//opponent of the current players flighta

        //power based on name
        switch(name){
            case "Bronze Dragon":
                //removes the two ante cards if there are ante cards and if theres room in the
                //players hand.
                if(tda.getAnte().size()==2&&tda.getHands()[player].size()<=8){
                   for(int i = tda.getAnte().size()-1; i>=0;i--){
                       tda.getHands()[player].add(tda.getAnte().get(i));
                       tda.getAnte().remove(i);
                   }
                }
                break;
            case "The Fool":
                //if the opponent has a stronger flight draw a card
                //and pay 1 to the stakes
                if(strongestFlight()!=player){
                    tda.setHoard(player,-1);
                    tda.drawCard(player);
                }
                break;
            case "Black Dragon":
                //steal 2 gold from the stakes
                tda.setHoard(player,2);
                tda.setStakes(stakes-2);
                break;
            case "The Priest":
                //pay 1 to the stakes, you will automatically win the round
                tda.setHoard(player,-1);
                tda.setStakes(stakes+1);
                break;
            case "Gold Dragon":
                //counter for amount of good dragons
                int numGoodDragons = 0;
                //check current players flight to see how many good dragons are within flight
                for (Card e: playerFlight) {
                    if (e.getType() == Card.GOOD) {
                        numGoodDragons++;
                    }
                }
                //go through a for loop to draw a card for each good dragon
                for (int i = 0; i < numGoodDragons; i++) {
                    tda.drawCard(player);
                }
                break;
            case "Red Dragon":
                //random card from opponents hand
                int indexOfCard = rand.nextInt(tda.getHands()[opponent].size());
                //if the strength of the opponents flight is bigger than the current player do the power
                if (strongestFlight() != player) {
                    tda.setHoard(player, 1);
                    tda.setHoard(opponent, -1);
                    //remove the card from the opponents hand
                    Card takenCard = tda.getHands()[opponent].remove(indexOfCard);
                    //add that card to the players hand
                    tda.getHands()[player].add(takenCard);
                }
                break;
            //Each player with at least one good dragon in their flight draws a card
            case "Silver Dragon":
                //if a good dragon has been found
                boolean goodDragonFound = false;
                //check the flight of the current player for any good dragons
                for (Card d: playerFlight) {
                    //if the first good dragon has been found activate power
                    if (d.getType() == Card.GOOD && !goodDragonFound) {
                        tda.drawCard(player);
                        goodDragonFound = true;
                    }
                }
                //change the boolean back to false to check opponents flight
                goodDragonFound = false;
                //check the opponents flight for a good dragon
                for (Card e: oppFlight) {
                    //if the first good dragon has been found activate power
                    if (e.getType() == Card.GOOD && !goodDragonFound) {
                        tda.drawCard(opponent);
                        goodDragonFound = true;
                    }
                }
                break;
            //If any flight includes a mortal, steal 3 gold from the stakes.
            case "White Dragon":
                //if a mortal has been found set mortal found to true
                boolean mortalFound = false;
                //check the players flight for mortals
                for (int i = 0; i < playerFlight.size(); i++) {
                    Card d = playerFlight.get(i);
                    //if a mortal has been found than continue because the power has already activated
                    if (mortalFound) {
                        continue;
                    }
                    //if the card at that i index of the flight is a mortal initiate power
                    if (d.getType() == Card.MORTAL) {
                        tda.setHoard(player, 3);
                        tda.setStakes(stakes - 3);
                        mortalFound = true;
                    }
                }
                //check the opponents flight for mortals
                for (int j = 0; j < oppFlight.size(); j++) {
                    Card e = oppFlight.get(j);
                    //if a mortal has been found than continue because the power has already activated
                    if (mortalFound) {
                        continue;
                    }
                    //if the card at that j index of the flight is a mortal initiate power
                    if (e.getType() == Card.MORTAL) {
                        tda.setHoard(player, 3);
                        tda.setStakes(stakes - 3);
                        mortalFound = true;
                    }
                }
                break;
            case "Bahamut":
                //checking for good and evil dragons in the opponents flight
                boolean good = false;
                boolean evil = false;
                for(Card b : tda.getFlights()[opponent]){
                    if(b.getType()==Card.EVIL){
                        evil = true;
                    }
                    if(b.getType()==Card.GOOD){
                        good = true;
                    }
                }
                //if the opponent has both a good and evil dragon in their hand
                //they pay the player 10 gold
                if(good&&evil){
                    tda.setHoard(player,10);
                    tda.setHoard(opponent,-10);
                }
                break;
        }
    }
}
