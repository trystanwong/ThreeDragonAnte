package com.example.threeDragonAnte.tda;

import com.example.threeDragonAnte.game.GameComputerPlayer;
import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.infoMsg.GameInfo;
import com.example.threeDragonAnte.tda.actions.ChoiceAction;
import com.example.threeDragonAnte.tda.actions.DiscardCardAction;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;

public class TdaComputerPlayer extends GameComputerPlayer {

    private TdaGameState tda;

    /**
     * Dumb A.I
     *
     * @param name the player's name (e.g., "John")
     */
    public TdaComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof TdaGameState){
            tda = new TdaGameState((TdaGameState)info);
            if(tda.getCurrentPlayer()!=playerNum){
                return;
            }
            //only moves if it's the computer's turn
            else if(tda.getCurrentPlayer()==playerNum){
                //AI takes 1 second to make decision
                super.sleep(1000);

                //different decisions based on the phase of the game
                switch(tda.getPhase()){

                        //dumb A.I always chooses first card in their hand to be the ante
                    case TdaGameState.ANTE:
                        PlayCardAction pca = new PlayCardAction(this,0,Card.ANTE);
                        super.game.sendAction(pca);
                        break;

                        //dumb A.I always plays first card in hard
                    case TdaGameState.ROUND:
                        PlayCardAction pca1 = new PlayCardAction(this, 0, Card.FLIGHT);
                        super.game.sendAction(pca1);
                        break;

                        //dumb A.I always takes option 1
                    case TdaGameState.CHOICE:
                        super.game.sendAction(new ChoiceAction(this,0));
                        break;
                    case TdaGameState.DISCARD:
                        //dumb A.I chooses the first playable card to discard
                        int index = 0;
                        for(int i = 0; i < tda.getFlights()[playerNum].size();i++){
                            if(tda.getFlights()[playerNum].get(i).isPlayable()){
                                index = i;
                                break;
                            }
                        }
                        super.game.sendAction(new DiscardCardAction(this,index));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
