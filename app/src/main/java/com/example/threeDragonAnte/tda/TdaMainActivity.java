package com.example.threeDragonAnte.tda;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.threeDragonAnte.R;
import com.example.threeDragonAnte.game.GameMainActivity;
import com.example.threeDragonAnte.game.GamePlayer;
import com.example.threeDragonAnte.game.LocalGame;
import com.example.threeDragonAnte.game.config.GameConfig;
import com.example.threeDragonAnte.game.config.GamePlayerType;

import java.util.ArrayList;

public class TdaMainActivity extends GameMainActivity {
    private static final int PORT_NUMBER = 2278;

    /**
     * Create the default configuration for this game:
     * - one human player vs. one computer player
     * - minimum of 2 players
     *
     * @return the new configuration object, representing the default configuration
     */
    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // TDA has three player types:  human regular computer and smart computer player
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new TdaHumanPlayer(name);
            }});

        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new TdaComputerPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Smart Computer Player") {
            public GamePlayer createPlayer(String name) {return new TdaComputerPlayer(name);}});

        // Create a game configuration class for TDA:
        GameConfig defaultConfig = new GameConfig
                (playerTypes, 2, 4, "TDA", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
        defaultConfig.addPlayer("Smart Computer Player", 2); //TODO ASK THE MOFUCKA ABOUT THIS THING RIGHT HERRE
        defaultConfig.setRemoteData("Remote Human Player", "", 0);

        return defaultConfig;
    }//createDefaultConfig

    @Override
    public LocalGame createLocalGame() {
        return new TdaLocalGame();
    }
}