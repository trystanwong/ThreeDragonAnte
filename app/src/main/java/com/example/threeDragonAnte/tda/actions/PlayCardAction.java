package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.Card;

public class PlayCardAction extends GameAction {
    private static final long serialVersionUID = -9540192844L;

    private int index; //index of the card in the given place
    private int placement; //placement of the card

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PlayCardAction(GamePlayer player, int i, int p) {
        super(player);
        placement = p;
        index = i;
    }
    public int getPlacement(){
        return placement;
    }

    public int getIndex(){
        return index;
    }
}
