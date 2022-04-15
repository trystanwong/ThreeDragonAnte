package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;

/**
 *
 * Represents an action of a player buying cards
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class BuyCardAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public BuyCardAction(GamePlayer player) {
        super(player);
    }
}
