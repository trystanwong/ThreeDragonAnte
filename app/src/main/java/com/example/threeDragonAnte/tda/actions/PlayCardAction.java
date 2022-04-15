package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.Card;


/**
 *
 * Represents an action of a player playing a card
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class PlayCardAction extends GameAction {
    private static final long serialVersionUID = -9540192844L;

    private int index; //index of the card in the given place
    private int placement; //placement of the card

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param i - index of the card played (from hand)
     * @param p - placement of the card
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
