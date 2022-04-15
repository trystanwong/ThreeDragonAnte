package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;

/**
 *
 * Represents an action of a player confirming game info
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class ConfirmAction extends GameAction {
    private static final long serialVersionUID = -826903031997792L;
    private int index;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public ConfirmAction(GamePlayer player) {
        super(player);
    }
    public int getIndex(){
        return index;
    }
}
