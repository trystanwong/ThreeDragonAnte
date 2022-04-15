package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
import com.example.threeDragonAnte.tda.Card;

/**
 *
 * Represents an action of a player discarding a card from their hand
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class DiscardCardAction extends GameAction {
    private static final long serialVersionUID = -74442128792L;
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
