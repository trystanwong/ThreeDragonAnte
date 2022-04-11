package com.example.threeDragonAnte.tda.actions;

import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.actionMsg.GameAction;

public class ChoiceAction extends GameAction {
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
