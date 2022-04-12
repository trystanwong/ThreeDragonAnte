package com.example.threeDragonAnte.tda;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.LocalGame;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.actions.ChoiceAction;
import com.example.threeDragonAnte.tda.actions.ConfirmAction;
import com.example.threeDragonAnte.tda.actions.DiscardCardAction;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;
import java.util.ArrayList;
import java.util.Random;

public class TdaLocalGame extends LocalGame{

    private TdaGameState tda;

    public TdaLocalGame(){
        tda = new TdaGameState();

    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        //current game state
        TdaGameState update = new TdaGameState(tda);
        System.out.println(tda.getPhase());
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
            }
            else {
                String name = tda.getLast()[opponent].getName();

                //if a green dragon or tiamat were played by the opponent
                if(name.equals("Green Dragon")||name.equals("Tiamat")){
                    if (hand.get(index).getStrength() >= tda.getLast()[opponent].getStrength()) {
                        return false;
                    } else {
                        Card discarded = hand.get(index);
                        tda.getHands()[opponent].add(discarded);
                        hand.remove(index);
                        tda.setDiscarding(false);
                        tda.setPhase(TdaGameState.ROUND);
                    }
                }
                //if the brass dragon was played by the opponent
                else if(name.equals("Brass Dragon")){
                    if (hand.get(index).getStrength() <= tda.getLast()[opponent].getStrength()) {
                        return false;
                    } else {
                        Card discarded = hand.get(index);
                        tda.getHands()[opponent].add(discarded);
                        hand.remove(index);
                        tda.setDiscarding(false);
                        tda.setPhase(TdaGameState.ROUND);
                    }
                }
            }
            //removing the green background from the playable cards
            for (Card c : hand) {
                c.setPlayable(false);
            }
            return turnHelper();
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
                        if (choice == 0) {
                            //checking if there are any cards that are weaker to give
                            boolean hasWeak = false;
                            for (Card h : hand) {
                                if (h.getStrength() < tda.getLast()[opponent].getStrength()) {
                                    hasWeak = true;
                                    h.setPlayable(true);
                                }
                            }
                            if (hasWeak) {
                                tda.setGameText("Choose a dragon from your hand with" +
                                        " a strength less than "
                                        + tda.getLast()[opponent].getStrength());
                                tda.setPhase(TdaGameState.DISCARD);
                            } else {
                                tda.setGameText("No weaker dragons to give");
                            }
                            return true;
                        }
                        //paying 5 gold to the opponent
                        else if (choice == 1) {
                            tda.setHoard(opponent, 5);
                            tda.setHoard(player, -5);
                            tda.setPhase(TdaGameState.ROUND);
                            break;
                        }
                }
            }
            //if you played a card that gave you a choice
            else if(tda.isChoosing()) {
                switch (tda.getLast()[player].getName()) {

                    case "Tiamat":
                        int num = 0;
                        for (Card check : tda.getFlights()[player]) {
                            if (check.getType() == Card.EVIL) {
                                num++;
                            }
                        }
                        if (choice == 0) {
                            tda.setHoard(player, num);
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
                                blueDragon();
                                break;
                            case "Green Dragon":
                                tda.getLast()[player].setName("Green Dragon");
                                greenDragon();
                                return true;
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
                        tda.setDiscarding(true);
                        tda.setPhase(TdaGameState.ROUND);
                        tda.setChooseFrom(0);
                        break;
                    case "Blue Dragon":
                        int numEvil = 0;
                        for (Card check : tda.getFlights()[player]) {
                            if (check.getType() == Card.EVIL) {
                                numEvil++;
                            }
                        }
                        if (choice == 0) {
                            tda.setHoard(player, numEvil);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        if (choice == 1) {
                            tda.setStakes(stakes + numEvil);
                            tda.setHoard(opponent, -numEvil);
                            tda.setPhase(TdaGameState.ROUND);
                        }
                        tda.setChooseFrom(0);
                        break;
                }
                tda.setChoosing(false);
            }
            return turnHelper();
        }
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

                //counts as a move (used for counting rounds)
                tda.addMove();

                //add the card to the flight
                int index = ((PlayCardAction) action).getIndex();
                Card flight = hand.get(index);
                tda.setLast(player,flight); //keeps track of the card that was played
                flight.setPlacement(Card.FLIGHT);
                tda.getFlights()[player].add(new Card(hand.get(index)));
                tda.getHands()[player].remove(index);

                //triggering the power of a card
                powers(flight.getName());

                //if the player has 1 card or less in their hand they have to draw cards
                if(hand.size()<=1){
                    buyCard(player);
                }

                //cards that have choices
                switch(flight.getName()){
                    case "Dracolich":
                        tda.setGameText("Choose a card to copy its power:");
                        //looking through the opponents flight to see what cards you can copy
                        for(int i = 0; i < opFlight.size();i++){
                            Card d = opFlight.get(i);
                            //only discards cards that are dragons
                            // and weaker than the dragon slayer
                            if(d.getType()==Card.EVIL){
                                tda.addChooseFrom();
                                tda.setChoices(i,(i+1)+". "+opFlight.get(i).toString());
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
                        tda.setGameText("Choose a card to discard from opponent's flight:");

                        //looking through the opponents flight to see what cards you can discard
                        for(int i = 0; i < opFlight.size();i++){
                            Card d = opFlight.get(i);
                            //only discards cards that are dragons
                            // and weaker than the dragon slayer
                            if(d.getStrength()<8&&d.getType()!=Card.MORTAL){
                                tda.addChooseFrom();
                                tda.setChoices(i,(i+1)+". "+opFlight.get(i).toString());
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
                    case "The Thief":
                        //if there are no cards to discard it does nothing
                        if(playerFlight.size()==0){
                            break;
                        }
                        else {
                            //steal 7 g from the stakes
                            tda.setHoard(player, +7);
                            tda.setStakes(stakes + 7);
                            for (Card h : hand) {
                                h.setPlayable(true);
                            }
                            tda.setDiscarding(true);
                            tda.setGameText("Choose a card from your hand to discard.");
                            tda.setPhase(TdaGameState.DISCARD);
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
        tda.setGameText("Choose one:");
        tda.setChoice1("Give a weaker dragon from your hand to your opponent.");
        tda.setChoice2("Pay your opponent 5 gold.");
        tda.setPhase(TdaGameState.CHOICE);
        tda.setDiscarding(true);
        tda.setCurrentPlayer(Math.abs(tda.getCurrentPlayer()-1));
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
        int index = tda.getHands()[player].size();

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
        int payment = tda.getHands()[player].get(index).getStrength();
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
        if(tda.getMoves()%2==0){
            tda.setRound(tda.getRound()+1);
            tda.setRoundLeader(roundLeader());
            tda.setGameText(super.playerNames[tda.getRoundLeader()] +
                    " won that round");
            tda.setChoice1("OK");
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
        int playerHoard = tda.getHoards()[player]; //current hoard of the player
        int opponentHoard = tda.getHoards()[opponent]; //current hoard of the opponent
        int stakes = tda.getStakes(); //current stakes of the gambit
        ArrayList<Card> playerFlight = tda.getFlights()[player]; //current players flight
        ArrayList<Card> oppFlight = tda.getFlights()[opponent];//opponent of the current players flighta

        //power based on name
        switch(name){
            case "Black Dragon":
                tda.setHoard(player,2);
                tda.setStakes(stakes-2);
                break;
            case "The Priest":
                tda.setHoard(player,-1);
                tda.setStakes(stakes+1);
                break;
            case "The Archmage":
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
                boolean good = false;
                boolean evil = false;
                //checking for good and evil dragons in the opponents flight
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
