package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;
/**?
 *
 * Represents an action of a player making a choice
 *
 * @author Trystan Wong
 * @author Kawika Suzuki
 * @author Mohammad Surur
 * @author Marcus Rison
 */
public class ChoiceAction extends GameAction {
    private static final long serialVersionUID = -1110916892027578792L;
    private int choiceNum;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public ChoiceAction(GamePlayer player, int choice) {
        super(player);
        choiceNum = choice;
    }

    public int getChoiceNum(){
        return choiceNum;
    }
}
