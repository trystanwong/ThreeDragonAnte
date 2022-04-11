package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.Card;

public class DiscardCardAction extends GameAction {
    private int discard;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public DiscardCardAction(GamePlayer player, int index) {
        super(player);
        discard=index;
    }
    public int getDiscard(){
        return discard;
    }
}
