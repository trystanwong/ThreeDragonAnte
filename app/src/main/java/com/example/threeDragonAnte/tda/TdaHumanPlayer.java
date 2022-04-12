package com.example.threeDragonAnte.tda;

import static com.example.threeDragonAnte.tda.TdaGameState.ANTE;
import static com.example.threeDragonAnte.tda.TdaGameState.BEGIN_GAME;
import static com.example.threeDragonAnte.tda.TdaGameState.CHOICE;
import static com.example.threeDragonAnte.tda.TdaGameState.CONFIRM;
import static com.example.threeDragonAnte.tda.TdaGameState.DISCARD;
import static com.example.threeDragonAnte.tda.TdaGameState.ROUND;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import com.example.threeDragonAnte.R;
import com.example.threeDragonAnte.game.GameHumanPlayer;
import com.example.threeDragonAnte.game.GameMainActivity;
import com.example.threeDragonAnte.game.infoMsg.GameInfo;
import com.example.threeDragonAnte.tda.actions.ChoiceAction;
import com.example.threeDragonAnte.tda.actions.ConfirmAction;
import com.example.threeDragonAnte.tda.actions.DiscardCardAction;
import com.example.threeDragonAnte.tda.actions.PlayCardAction;

public class TdaHumanPlayer extends GameHumanPlayer implements View.OnTouchListener, View.OnClickListener{

    private Activity myActivity;
    private ViewGroup mainLayout;

    private TdaGameState tda;

    //coordinates of moving cards
    private int xDelta;
    private int yDelta;
    private int[] leftMargins;
    private int[] bottomMargins;
    private int[] rotations;

    //text on the board
    private TextView gameText;
    private TextView opponentName;
    private TextView myName;
    private TextView stakes;
    private TextView hoard0;
    private TextView hoard1;
    private TextView discardAmount;
    private TextView deckAmount;
    private Button[] choices;
    private Button choice1;
    private Button choice2;
    private Button choice3;

    //card lists
    private ImageView[] hand;
    private ImageView[] ante;
    private ImageView[][] flights;
    private ImageView discard;
    private ImageView deck;


    //zoomed card
    private ConstraintLayout zoomed;
    private ImageView selected;
    private TextView strength1;
    private TextView strength2;


    /**
     * constructor
     *
     * @param name the name of the player
     */
    public TdaHumanPlayer(String name) {
        super(name);

        //all the margins to keep track of for the cards in the players hand
        //used to move the card ImageView back to their original position
        leftMargins = new int[10];
        bottomMargins = new int[10];
        rotations = new int[10];

        leftMargins[0] = 330;
        bottomMargins[0] = 60;
        rotations[0] = -5;

        leftMargins[1] = 390;
        bottomMargins[1] = 60;
        rotations[1] = 5;

        leftMargins[2] = 448;
        bottomMargins[2] = 54;
        rotations[2] = 10;

        leftMargins[3] = 275;
        bottomMargins[3] = 54;
        rotations[3] = -10;

        leftMargins[4] = 504;
        bottomMargins[4] = 43;
        rotations[4] = 15;

        leftMargins[5] = 225;
        bottomMargins[5] = 43;
        rotations[5] = -15;

        leftMargins[6] = 558;
        bottomMargins[6] = 28;
        rotations[6] = 20;

        leftMargins[7] = 180;
        bottomMargins[7] = 28;
        rotations[7] = -20;

        leftMargins[8] = 610;
        bottomMargins[8] = 9;
        rotations[8] = 25;

        leftMargins[9] = 136;
        bottomMargins[9] = 9;
        rotations[9] = -25;

    }

    @Override
    public void receiveInfo(GameInfo info) {

        boolean gameInfo = info instanceof TdaGameState;

        if (!gameInfo) {
            super.flash(Color.RED, 100);
        } else {

            //current state of the game
            tda = (TdaGameState) info;

            switch(tda.getPhase()){
                case ANTE:
                    gameText.setText("Move a card from your hand to your ante.");

                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    break;
                case ROUND:
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                     if(tda.getRound()>0){
                        gameText.setText("Your turn.");
                     }
                     else {
                        gameText.setText("Move a card from your hand to your flight.");
                     }
                    break;
                case CHOICE:
                    //if a choice is presented to the player
                    if(tda.getCurrentPlayer()==playerNum){

                        gameText.setText(tda.getGameText());

                        choice1.setText(tda.getChoice1());
                        choice2.setText(tda.getChoice2());
                        choice3.setText(tda.getChoice3());

                        // if the dragon slayer was played
                        if(tda.isChoosing()){
                            //shows all available choices to remove
                            for(int i = 0; i < tda.getChooseFrom(); i++){
                                choices[i].setText(tda.getChoice(i));
                                choices[i].setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                        choice1.setVisibility(View.VISIBLE);
                        choice2.setVisibility(View.VISIBLE);
                        break;
                    }
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    break;
                case TdaGameState.DISCARD:

                    gameText.setText(tda.getGameText());
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    break;

                case TdaGameState.CONFIRM:
                        gameText.setText(tda.getGameText());
                        choice1.setText(tda.getChoice1());
                        choice1.setVisibility(View.VISIBLE);
                        choice2.setVisibility(View.GONE);
                        break;

            }
            //if its not the player's turn the computer is making a decision
            if(tda.getCurrentPlayer()!=playerNum){
                gameText.setText("Opponent is thinking...");
            }

            //displaying all cards on the board

            //all cards in the player's hand
            ArrayList<Card> currentHand = tda.getHands()[playerNum];
            for(int i = 0; i<currentHand.size();i++){

                hand[i].setOnTouchListener(this);
                //setting the image of each card in the hand
                setImage(hand[i],currentHand.get(i).getName());
                hand[i].setVisibility(View.VISIBLE);

                //if the card can be chosen, its highlighted green (used for choices)
                if(currentHand.get(i).isPlayable()){
                    hand[i].setBackgroundColor(Color.GREEN);
                }
                else{
                    hand[i].setBackgroundColor(Color.BLACK);
                }
            }
            //hiding the rest of the images in the hand if there are none.
            for(int i = currentHand.size();i<10;i++){
                hand[i].setVisibility(View.GONE);
            }

            //all cards in each flight
            ArrayList<Card>[] currentFlights = tda.getFlights();
            for(int i = 0; i < 2; i++) {
                for (int j = 0; j < currentFlights[i].size(); j++) {
                    setImage(flights[i][j],currentFlights[i].get(j).getName());
                    flights[i][j].setOnTouchListener(this);
                    flights[i][j].setBackgroundColor(Color.BLACK);
                }
                for(int k = currentFlights[i].size(); k<3; k++){
                    flights[i][k].setImageResource(R.drawable.beige);
                    flights[i][k].setBackgroundColor(Color.DKGRAY);
                }
            }

            //all cards in the ante's
            ArrayList<Card> currentAnte = tda.getAnte();
            if(currentAnte.size()>0){
                setImage(ante[0],currentAnte.get(0).getName());
            }
            if(currentAnte.size()>1){
                setImage(ante[1],currentAnte.get(1).getName());
            }
            if(currentAnte.size()==0){
                ante[0].setImageResource(R.drawable.beige);
                ante[1].setImageResource(R.drawable.beige);
            }

            //all the texts on the screen

            //amount in the deck and discard
            deckAmount.setText(Integer.toString(tda.getDeck().size()));
            discardAmount.setText(Integer.toString(tda.getDiscard().size()));

            //stakes
            stakes.setText(Integer.toString(tda.getStakes()));

            //hoards
            hoard0.setText(Integer.toString(tda.getHoards()[0]));
            hoard1.setText(Integer.toString(tda.getHoards()[1]));

            //player names
            opponentName.setText(super.allPlayerNames[1]);
            myName.setText(super.allPlayerNames[0]);

            //choice texts
            choice1.setOnClickListener(this);
            choice2.setOnClickListener(this);
            choice3.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View view) {
        //when the player makes a choice available to them
        switch(tda.getPhase()){
            case CHOICE:
                if(view == choice1){
                    ChoiceAction ca = new ChoiceAction(this, 0);
                    super.game.sendAction(ca);
                }
                if(view == choice2){
                    ChoiceAction ca = new ChoiceAction(this,1);
                    super.game.sendAction(ca);
                }
                if(view == choice3){
                    ChoiceAction ca = new ChoiceAction(this,2);
                    super.game.sendAction(ca);
                }
                break;
            case CONFIRM:
                if(view == choice1) {
                    ConfirmAction confirm = new ConfirmAction(this);
                    super.game.sendAction(confirm);
                }
                break;
            case BEGIN_GAME:
                if(view == choice1) {
                    super.game.sendAction(new ConfirmAction(this));
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //coordinates of the moving cards
        final int x = (int) motionEvent.getRawX();
        final int y = (int) motionEvent.getRawY();

        //if a flight card is touched
        for(int i = 0; i < 2; i++){
            for(int j = 0; j<tda.getFlights()[i].size();j++){
                if(view == flights[i][j]){

                    Card flight = tda.getFlights()[i].get(j);
                    setImage(selected,flight.getName());
                    strength2.setText(Integer.toString(flight.getStrength()));
                    strength1.setText(Integer.toString(flight.getStrength()));
                    zoomed.setVisibility(View.VISIBLE);

                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            zoomed.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams zoomParam = (RelativeLayout.LayoutParams) zoomed
                                    .getLayoutParams();

                            //accounting for display pixels
                            float z = myActivity.getResources().getDisplayMetrics().density;

                            //moving the zoomed card to where the selected card is
                            zoomParam.leftMargin= (int)(270*z);
                            zoomed.setLayoutParams(zoomParam);
                            break;
                        case MotionEvent.ACTION_UP:
                            zoomed.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }
        }

        //if a card in the player's hand is touched
        ArrayList<Card> playerHand = tda.getHands()[playerNum];
        for(int i = 0; i < playerHand.size(); i++){
            if(view == hand[i]){

                Card hand = playerHand.get(i);
                setImage(selected,hand.getName());
                strength2.setText(Integer.toString(hand.getStrength()));
                strength1.setText(Integer.toString(hand.getStrength()));

                zoomed.setVisibility(View.VISIBLE);

                //moving the ImageView of each card in the hand
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y + lParams.bottomMargin;

                        break;

                    case MotionEvent.ACTION_UP:

                        //zoomed image becomes invisible
                        zoomed.setVisibility(View.INVISIBLE);

                        //parameters of the view
                        RelativeLayout.LayoutParams played = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();

                        //density of the pixels on the screen used for margins
                        float d = myActivity.getResources().getDisplayMetrics().density;

                        //cards are placed based on what game phase it is
                        if(tda.getCurrentPlayer()==playerNum) {
                            switch (tda.getPhase()) {
                                case TdaGameState.ANTE:
                                    if (played.bottomMargin > (int) (d * 410)
                                            && played.leftMargin > (int) (d * 300)) {
                                        PlayCardAction playAnte =
                                                new PlayCardAction(this, i, Card.ANTE);
                                        super.game.sendAction(playAnte);
                                    }
                                    break;
                                case ROUND:
                                    if (played.bottomMargin > (int) (d * 200)) {
                                        PlayCardAction playFlight =
                                                new PlayCardAction(this, i, Card.FLIGHT);
                                        super.game.sendAction(playFlight);
                                    }
                                    break;
                                case DISCARD:
                                    if (played.bottomMargin > (int) (d * 200)) {
                                        //shows the user what card was discarded
                                        Toast.makeText(myActivity,
                                                hand.toString()+" discarded.",
                                                Toast.LENGTH_SHORT).show();
                                        DiscardCardAction dc = new DiscardCardAction(this,i);
                                        super.game.sendAction(dc);
                                    }
                            }
                        }

                        //returning view back to original spot after its played
                        int leftValue = leftMargins[i]; // margin in dips
                        int bottomValue = bottomMargins[i]; // margin in dips
                        int leftMargin = (int)(leftValue * d);
                        int bottomMargin = (int)(bottomValue * d);

                        //returning the view back to its original parameters
                        view.setRotationX(-26);
                        view.setRotation(rotations[i]);
                        played.leftMargin = leftMargin;
                        played.bottomMargin = bottomMargin;
                        played.rightMargin = 0;
                        played.topMargin = 0;
                        view.setLayoutParams(played);
                        break;

                        //moving the card with finger
                    case MotionEvent.ACTION_MOVE:


                        //setting the rotation of the card to zero as it moves
                        view.setRotationX(0);
                        view.setRotation(0);

                        //margins of the card
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                    .getLayoutParams();

                        //margins of the zoomed card
                        RelativeLayout.LayoutParams zoomParam = (RelativeLayout.LayoutParams) zoomed
                                .getLayoutParams();

                        //accounting for display pixels
                        float z = myActivity.getResources().getDisplayMetrics().density;

                        //moving the zoomed card to where the selected card is
                        zoomParam.leftMargin= layoutParams.leftMargin - (int)(85*z);
                        zoomed.setLayoutParams(zoomParam);

                        //the zoomed card becomes invisible when the card is moving
                        if (layoutParams.bottomMargin > (int) (z * 80)) {
                            zoomed.setVisibility(View.INVISIBLE);
                        }

                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.bottomMargin = yDelta - y;
                        layoutParams.rightMargin = 0;
                        layoutParams.topMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
            }
        }
        return true;
    }

    /**
     * shows a message to the player based on what card they played
     * @param name - name of the card played
     */
    private void showMessage(String name) {

        String msg = "";

        switch (name){
            case "Black Dragon":
                msg = "Stole 2 gold from the stakes";
                break;
            case "Druid":
                msg = "Weakest flight will win now";
                break;

        }

        if(!msg.equals("")) {
            Toast.makeText(myActivity, msg,
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        // remember the activity
        myActivity = activity;
        mainLayout = (RelativeLayout)activity.findViewById(R.id.topHalf);

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.tda_main);

        //choices
        choices = new Button[3];
        choice1 = activity.findViewById(R.id.choice1);
        choice2 = activity.findViewById(R.id.choice2);
        choice3 = activity.findViewById(R.id.choice3);
        choices[0] = choice1;
        choices[1] = choice2;
        choices[2] = choice3;

        //text on the board
        gameText = activity.findViewById(R.id.gameText);
        stakes = activity.findViewById(R.id.stakesAmount);
        hoard0 = activity.findViewById(R.id.hoard0Amount);
        hoard1 = activity.findViewById(R.id.hoard2Amount);
        deckAmount = activity.findViewById(R.id.deckAmount);
        discardAmount = activity.findViewById(R.id.discardAmount);
        discard = activity.findViewById(R.id.discard);
        discard.setImageResource(R.drawable.cardback);
        deck = activity.findViewById(R.id.deck);
        deck.setImageResource(R.drawable.cardback);
        myName = activity.findViewById(R.id.player0Name);
        opponentName = activity.findViewById(R.id.player2Name);

        //zoomed card
        selected = activity.findViewById(R.id.zoom);
        zoomed = activity.findViewById(R.id.zoomlayout);
        selected.setOnTouchListener(this);
        strength1 = activity.findViewById(R.id.strength);
        strength2 = activity.findViewById(R.id.strength2);


        //flight images
        flights = new ImageView[2][3];
        flights[0][0] = activity.findViewById(R.id.flight0_0);
        flights[0][1] = activity.findViewById(R.id.flight0_1);
        flights[0][2] = activity.findViewById(R.id.flight0_2);
        flights[1][0] = activity.findViewById(R.id.flight2_0);
        flights[1][1] = activity.findViewById(R.id.flight2_1);
        flights[1][2] = activity.findViewById(R.id.flight2_2);

        //ante images
        ante = new ImageView[2];
        ante[0] = activity.findViewById(R.id.ante0);
        ante[1] = activity.findViewById(R.id.ante1);

        //hand images
        hand = new ImageView[10];
        hand[0] = activity.findViewById(R.id.hand0_0);
        hand[1] = activity.findViewById(R.id.hand0_1);
        hand[2] = activity.findViewById(R.id.hand0_2);
        hand[3] = activity.findViewById(R.id.hand0_3);
        hand[4] = activity.findViewById(R.id.hand0_4);
        hand[5] = activity.findViewById(R.id.hand0_5);
        hand[6] = activity.findViewById(R.id.hand0_6);
        hand[7] = activity.findViewById(R.id.hand0_7);
        hand[8] = activity.findViewById(R.id.hand0_8);
        hand[9] = activity.findViewById(R.id.hand0_9);
    }

    public void setImage(ImageView iv, String name){

        //all possible names of cards
        switch (name) {

            case "Silver Dragon":
                iv.setImageResource(R.drawable.silverdragon);
                break;
            case "Copper Dragon":
                iv.setImageResource(R.drawable.copperdragon1);
                break;
            case "Red Dragon":
                iv.setImageResource(R.drawable.reddragon3);
                break;
            case "Gold Dragon":
                iv.setImageResource(R.drawable.golddragon6);
                break;
            case "Brass Dragon":
                iv.setImageResource(R.drawable.brassdragon9);
                break;
            case "Black Dragon":
                iv.setImageResource(R.drawable.blackdragon1);
                break;
            case "Blue Dragon":
                iv.setImageResource(R.drawable.bluedragon1);
                break;
            case "Bronze Dragon":
                iv.setImageResource(R.drawable.bronzedragon1);
                break;
            case "White Dragon":
                iv.setImageResource(R.drawable.whitedragon);
                break;
            case "Green Dragon":
                iv.setImageResource(R.drawable.greendragon);
                break;
            case "Bahamut":
                iv.setImageResource(R.drawable.bahamut);
                break;
            case "Dracolich":
                iv.setImageResource(R.drawable.dracolich);
                break;
            case "Tiamat":
                iv.setImageResource(R.drawable.tiamat);
                break;
            case "The Princess":
                iv.setImageResource(R.drawable.princess);
                break;
            case "The Fool":
                iv.setImageResource(R.drawable.fool);
                break;
            case "The Druid":
                iv.setImageResource(R.drawable.druid);
                break;
            case "The Archmage":
                iv.setImageResource(R.drawable.hermit);
                break;
            case "The DragonSlayer":
                iv.setImageResource(R.drawable.dragonslayer);
                break;
            case "The Priest":
                iv.setImageResource(R.drawable.priest);
                break;
            case "The Thief":
                iv.setImageResource(R.drawable.thief);
                break;
            default:
                iv.setImageResource(R.drawable.cardback);
                break;

        }
    }


    @Override
    public View getTopView() {
        return null;
    }

}
