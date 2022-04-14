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
import android.media.MediaPlayer;
import android.provider.MediaStore;
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
import java.util.Hashtable;

import com.example.threeDragonAnte.R;
import com.example.threeDragonAnte.game.GameHumanPlayer;
import com.example.threeDragonAnte.game.GameMainActivity;
import com.example.threeDragonAnte.game.infoMsg.GameInfo;
import com.example.threeDragonAnte.tda.actions.BuyCardAction;
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

    //sounds
    private MediaPlayer dragonRoar;
    private MediaPlayer backgroundMusic;
    private MediaPlayer confirm;
    private MediaPlayer drawCard;

    //text on the board
    private TextView gameText;
    private TextView opponentName;
    private TextView myName;
    private TextView stakes;
    private TextView hoard0;
    private TextView hoard1;
    private TextView discardAmount;
    private TextView deckAmount;
    private TextView yourFlightStrength;
    private TextView opponentFlightStrength;
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
    private ImageView drag;

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
    }

    @Override
    public void receiveInfo(GameInfo info) {

        boolean gameInfo = info instanceof TdaGameState;

        //illegal move flashes the screen red
        if (!gameInfo) {
            super.flash(Color.RED, 100);
        } else {

            //current state of the game
            tda = (TdaGameState) info;

            switch(tda.getPhase()){
                case ANTE: //telling the user what to do in an ante
                    gameText.setText("Move a card from your hand to your ante.");

                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    break;
                case ROUND: //telling the user what to do in a round
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    //user should know what to do if the round has started already
                     if(tda.getRound()>0){
                        gameText.setText("Your turn.");
                     }
                     else {
                        gameText.setText("Move a card from your hand to your flight.");
                     }
                    break;

                case CHOICE: //if a choice is presented to the player

                    //only visible to the player making the choice
                    if(tda.getCurrentPlayer()==playerNum){

                        //text of what choice the player has to make
                        gameText.setText(tda.getGameText());

                        //choices only become visible if necessary
                        choice1.setText(tda.getChoice1());
                        choice2.setText(tda.getChoice2());
                        choice3.setText(tda.getChoice3());
                        choice1.setVisibility(View.GONE);
                        choice2.setVisibility(View.GONE);
                        choice3.setVisibility(View.GONE);

                        //if the player is presented with options to choose from
                        if(tda.isChoosing()){
                            int index = tda.getChooseFrom();
                            //shows all available choices to remove
                            for(int i = 0; i < index; i++){
                                choices[i].setText(tda.getChoice(i));
                                choices[i].setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                        else if(tda.isDiscarding()){
                            int index = tda.getChooseFrom();
                            //shows all available choices to remove
                            for(int i = 0; i < index; i++){
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
                    //when the user is discarding a card (a helping text should appear)
                    gameText.setText(tda.getGameText());
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    break;

                case TdaGameState.CONFIRM: //user needs to confirm game data info

                    //what the user is confirming
                    gameText.setText(tda.getGameText());
                    choice1.setText(tda.getChoice1());

                    //only visible to the player who is confirming
                    if(tda.getCurrentPlayer()==playerNum){
                        choice1.setVisibility(View.VISIBLE);
                    }
                    choice3.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    break;
            }
            //if its not the player's turn the computer is making a decision
            if(tda.getCurrentPlayer()!=playerNum&&tda.getPhase()!=CONFIRM){
                gameText.setText("Opponent is thinking...");
            }

            //displaying all cards on the board
            //all cards in the player's hand
            ArrayList<Card> currentHand = tda.getHands()[playerNum];
            for(int i = 0; i<currentHand.size();i++){
                hand[i].setOnTouchListener(this);
                //setting the image of each card in the hand
                setImage(hand[i],currentHand.get(i).getName(),
                        currentHand.get(i).getStrength());
                hand[i].setVisibility(View.VISIBLE);

                //if the card can be chosen, its highlighted green (used for choices)
                if(tda.getPhase()==DISCARD&&currentHand.get(i).isPlayable()){
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

            //orientation of your hand is dependent on how many cards are in your hand
            cardOrientation();

            //used as the id of the current players opponent
            int opponent = Math.abs(playerNum-1);

            //all cards in each flight
            ArrayList<Card>[] currentFlights = tda.getFlights();
            ArrayList<Card> yourFlight = currentFlights[playerNum];
            ArrayList<Card> oppFlight = currentFlights[opponent];

            //setting your flight
            for(int i = 0; i < yourFlight.size();i++){
                setImage(flights[0][i],yourFlight.get(i).getName(),
                        yourFlight.get(i).getStrength());
                flights[0][i].setOnTouchListener(this);
                flights[0][i].setBackgroundColor(Color.BLACK);
            }
            for(int k = yourFlight.size(); k<3; k++){
                flights[0][k].setImageResource(R.drawable.beige);
                //open flight spot is green if its your turn and its a round
                if(tda.getPhase()==ROUND&&tda.getCurrentPlayer()==playerNum){
                    flights[0][k].setBackgroundColor(Color.GREEN);
                }
                else {
                    flights[0][k].setBackgroundColor(Color.DKGRAY);
                }
            }
            //setting opponents flight
            for(int i = 0; i < oppFlight.size();i++){
                setImage(flights[1][i],oppFlight.get(i).getName(),
                        oppFlight.get(i).getStrength());
                flights[1][i].setOnTouchListener(this);
                flights[1][i].setBackgroundColor(Color.BLACK);
            }
            for(int k = oppFlight.size(); k<3; k++){
                flights[1][k].setImageResource(R.drawable.beige);
                flights[1][k].setBackgroundColor(Color.DKGRAY);
            }

            //all cards in the ante's
            ArrayList<Card> currentAnte = tda.getAnte();
            try {
                if (!currentAnte.get(playerNum).getName().equals("")) {
                    //setting the image of the ante card
                    setImage(ante[0],
                            currentAnte.get(playerNum).getName(),
                            currentAnte.get(playerNum).getStrength());
                    ante[0].setOnTouchListener(this);
                }
            }catch(IndexOutOfBoundsException iob){
                ante[0].setImageResource(R.drawable.beige);
            }
            try {
                if (!currentAnte.get(opponent).getName().equals("")) {
                    setImage(ante[1],
                            currentAnte.get(opponent).getName(),
                            currentAnte.get(opponent).getStrength());
                    ante[1].setOnTouchListener(this);
                }
            }catch(IndexOutOfBoundsException iob){
                ante[1].setImageResource(R.drawable.beige);
            }

            //all the texts on the screen

            //amount in the deck and discard
            deckAmount.setText(Integer.toString(tda.getDeck().size()));

            //stakes
            stakes.setText(Integer.toString(tda.getStakes()));

            //hoards
            hoard0.setText(Integer.toString(tda.getHoards()[playerNum]));
            hoard1.setText(Integer.toString(tda.getHoards()[opponent]));

            //player names
            opponentName.setText(super.allPlayerNames[opponent]);
            myName.setText(super.allPlayerNames[playerNum]);

            //choice texts
            choice1.setOnClickListener(this);
            choice2.setOnClickListener(this);
            choice3.setOnClickListener(this);

            //Displays the strength of each flight
            int strength1 = 0;
            int strength2 = 0;
            for (Card c : tda.getFlights()[0]) {
                strength1 += c.getStrength();
            }
            yourFlightStrength.setText("Your Flight " + Integer.toString(strength1));
            for (Card c : tda.getFlights()[1]) {
                strength2 += c.getStrength();
            }
            opponentFlightStrength.setText(Integer.toString(strength2));
        }
    }


    @Override
    public void onClick(View view) {
        //noise when a button is pressed
        confirm.start();
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
                    backgroundMusic.setVolume(0.3f,0.3f);
                    backgroundMusic.start();
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

        int opponent = Math.abs(playerNum-1);

        //if the player is attempting to buy from the deck
        if(view == drag){
            //accounting for pixel density
            float z = myActivity.getResources().getDisplayMetrics().density;

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams zoomed = (RelativeLayout.LayoutParams)
                            view.getLayoutParams();
                    xDelta = x - zoomed.leftMargin;
                    yDelta = y + zoomed.bottomMargin;
                    break;
                case MotionEvent.ACTION_UP:

                    RelativeLayout.LayoutParams original = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();

                    if(original.leftMargin < (int)(z*650)){
                        if(original.bottomMargin<(int)(z*150)){
                            drawCard.start();
                            super.game.sendAction(new BuyCardAction(this));
                        }
                    }
                    //moving the cardback image back to its original position
                    original.leftMargin = (int)(z*685);
                    original.bottomMargin = (int)(z*409);
                    view.setLayoutParams(original);
                    break;


                case MotionEvent.ACTION_MOVE:

                    //margins of the card
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = x - xDelta;
                    layoutParams.bottomMargin = yDelta - y;
                    layoutParams.rightMargin = 0;
                    layoutParams.topMargin = 0;
                    view.setLayoutParams(layoutParams);
                    break;
            }
        }

        //if an ante is selected
        if(view == ante[0]){
            Card ante = tda.getAnte().get(playerNum);
            setImage(selected,ante.getName(),ante.getStrength());
            strength2.setText(Integer.toString(ante.getStrength()));
            strength1.setText(Integer.toString(ante.getStrength()));
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
        //opponent ante
        else if(view == ante[1]){
            Card ante = tda.getAnte().get(opponent);
            setImage(selected,ante.getName(),ante.getStrength());
            strength2.setText(Integer.toString(ante.getStrength()));
            strength1.setText(Integer.toString(ante.getStrength()));
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

        //if a flight card is touched
        for(int i = 0; i < 2; i++){
            for(int j = 0; j<tda.getFlights()[i].size();j++){
                if(view == flights[i][j]){

                    Card flight;
                    if(i == 0) {
                        flight = tda.getFlights()[playerNum].get(j);
                    }
                    else{
                        flight = tda.getFlights()[opponent].get(j);
                    }

                    setImage(selected,flight.getName(),flight.getStrength());
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
                setImage(selected,hand.getName(),hand.getStrength());
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
                                        dragonRoar = cardSounds(hand.getName());
                                        dragonRoar.start();
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

                        //margins of the view change based on where the card is being dragged
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

    @Override
    public void setAsGui(GameMainActivity activity) {
        // remember the activity
        myActivity = activity;
        mainLayout = (RelativeLayout)activity.findViewById(R.id.topHalf);

        //sounds during the game
        dragonRoar = MediaPlayer.create(myActivity,R.raw.dracolich);
        backgroundMusic = MediaPlayer.create(myActivity,R.raw.backgroundmusic);
        confirm = MediaPlayer.create(myActivity,R.raw.confirm1);
        drawCard = MediaPlayer.create(myActivity,R.raw.card);

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

        //dragged deck card
        drag = activity.findViewById(R.id.dragDeck);
        drag.setImageResource(R.drawable.cardback);
        drag.setOnTouchListener(this);

        //text on the board
        gameText = activity.findViewById(R.id.gameText);
        stakes = activity.findViewById(R.id.stakesAmount);
        hoard0 = activity.findViewById(R.id.hoard0Amount);
        hoard1 = activity.findViewById(R.id.hoard2Amount);
        deckAmount = activity.findViewById(R.id.deckAmount);
        discardAmount = activity.findViewById(R.id.discardAmount);
        deck = activity.findViewById(R.id.dragDeck2);
        deck.setImageResource(R.drawable.deck);
        myName = activity.findViewById(R.id.player0Name);
        opponentName = activity.findViewById(R.id.player2Name);
        yourFlightStrength = activity.findViewById(R.id.flight0Text);
        opponentFlightStrength = activity.findViewById(R.id.opponentFlightStrengthTV);

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
        ante[playerNum] = activity.findViewById(R.id.ante0);
        ante[Math.abs(playerNum-1)] = activity.findViewById(R.id.ante1);

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

    /**
     * setting the image of the imageview of a card based on what card it is
     * @param iv - imageview we are changing
     * @param name - name of the card
     * @param strength - strength of the card
     */
    public void setImage(ImageView iv, String name, int strength){

        //all possible names of cards
        switch (name) {

            case "Silver Dragon":
                switch(strength){
                    case 2:
                        iv.setImageResource(R.drawable.silverdragon2);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.silverdragon3);
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.silverdragon6);
                        break;
                    case 8:
                        iv.setImageResource(R.drawable.silverdragon8);
                        break;
                    case 10:
                        iv.setImageResource(R.drawable.silverdragon10);
                        break;
                    case 12:
                        iv.setImageResource(R.drawable.silverdragon12);
                        break;
                }
                break;
            case "Copper Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.copperdragon1);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.copperdragon3);
                        break;
                    case 5:
                        iv.setImageResource(R.drawable.copperdragon5);
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.copperdragon7);
                        break;
                    case 8:
                        iv.setImageResource(R.drawable.copperdragon8);
                        break;
                    case 10:
                        iv.setImageResource(R.drawable.copperdragon10);
                        break;
                }
                break;
            case "Red Dragon":
                switch(strength){
                    case 2:
                        iv.setImageResource(R.drawable.reddragon2);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.reddragon3);
                        break;
                    case 5:
                        iv.setImageResource(R.drawable.reddragon5);
                        break;
                    case 8:
                        iv.setImageResource(R.drawable.reddragon8);
                        break;
                    case 10:
                        iv.setImageResource(R.drawable.reddragon10);
                        break;
                    case 12:
                        iv.setImageResource(R.drawable.reddragon12);
                        break;
                }
                break;
            case "Gold Dragon":
                switch(strength){
                    case 2:
                        iv.setImageResource(R.drawable.golddragon2);
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.golddragon4);
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.golddragon6);
                        break;
                    case 9:
                        iv.setImageResource(R.drawable.golddragon9);
                        break;
                    case 11:
                        iv.setImageResource(R.drawable.golddragon11);
                        break;
                    case 13:
                        iv.setImageResource(R.drawable.golddragon13);
                        break;
                }
                break;
            case "Brass Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.brassdragon1);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.brassdragon2);
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.brassdragon4);
                        break;
                    case 5:
                        iv.setImageResource(R.drawable.brassdragon5);
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.brassdragon7);
                        break;
                    case 9:
                        iv.setImageResource(R.drawable.brassdragon9);
                        break;
                }
                break;
            case "Black Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.blackdragon1);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.blackdragon2);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.blackdragon3);
                        break;
                    case 5:
                        iv.setImageResource(R.drawable.blackdragon5);
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.blackdragon7);
                        break;
                    case 9:
                        iv.setImageResource(R.drawable.blackdragon9);
                        break;
                }
                break;
            case "Blue Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.bluedragon1);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.bluedragon2);
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.bluedragon4);
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.bluedragon7);
                        break;
                    case 9:
                        iv.setImageResource(R.drawable.bluedragon9);
                        break;
                    case 11:
                        iv.setImageResource(R.drawable.bluedragon11);
                        break;
                }
                break;
            case "Bronze Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.bronzedragon1);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.bronzedragon3);
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.bronzedragon6);
                        break;
                    case 9:
                        iv.setImageResource(R.drawable.bronzedragon9);
                        break;
                    case 11:
                        iv.setImageResource(R.drawable.bronzedragon11);
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.bronzedragon7);
                        break;
                }
                break;
            case "White Dragon":
                switch (strength){
                    case 1:
                        iv.setImageResource(R.drawable.whitedragon1);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.whitedragon2);
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.whitedragon3);
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.whitedragon4);
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.whitedragon6);
                        break;
                    case 8:
                        iv.setImageResource(R.drawable.whitedragon8);
                        break;
                }
                break;
            case "Green Dragon":
                switch(strength){
                    case 1:
                        iv.setImageResource(R.drawable.greendragon1);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.greendragon2);
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.greendragon4);
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.greendragon6);
                        break;
                    case 8:
                        iv.setImageResource(R.drawable.greendragon8);
                        break;
                    case 10:
                        iv.setImageResource(R.drawable.greendragon10);
                        break;
                }
                break;
            case "Bahamut":
                iv.setImageResource(R.drawable.bahamut13);
                break;
            case "Dracolich":
                iv.setImageResource(R.drawable.dracolich10);
                break;
            case "Tiamat":
                iv.setImageResource(R.drawable.tiamat13);
                break;
            case "The Princess":
                iv.setImageResource(R.drawable.princess4);
                break;
            case "The Fool":
                iv.setImageResource(R.drawable.fool3);
                break;
            case "The Druid":
                iv.setImageResource(R.drawable.druid6);
                break;
            case "The Archmage":
                iv.setImageResource(R.drawable.hermit9);
                break;
            case "The DragonSlayer":
                iv.setImageResource(R.drawable.dragonslayer8);
                break;
            case "The Priest":
                iv.setImageResource(R.drawable.priest5);
                break;
            case "The Thief":
                iv.setImageResource(R.drawable.thief7);
                break;
            default:
                iv.setImageResource(R.drawable.cardback);
                break;
        }
    }

    public void cardOrientation(){

        float d = myActivity.getResources().getDisplayMetrics().density;

        RelativeLayout.LayoutParams[] params = new RelativeLayout.LayoutParams[10];

        for(int i = 0; i<10;i++){
            params[i] = (RelativeLayout.LayoutParams)hand[i].getLayoutParams();

        }

        ArrayList<Card> currentHand = tda.getHands()[playerNum];

        //orientation of the cards depended on the size of the hand
        switch(currentHand.size()) {
            case 1:
                params[0].leftMargin = (int) (d * 360);
                leftMargins[0] = 360;
                params[0].bottomMargin = (int) (d * 60);
                bottomMargins[0] = 60;
                hand[0].setRotation(0);
                rotations[0] = 0;
                break;
            case 2:
                for (int i = 0; i < 2; i++) {
                    leftMargins[i] = (330 + (60 * i));
                    bottomMargins[i] = 60;
                    rotations[i] = (int)((Math.pow(-1.0, i + 1)) * 5);
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin = (int) (d * bottomMargins[i]);
                    hand[i].setRotation(rotations[i]);
                }
                break;
            case 4:
                for (int i = 0; i < 4; i++) {
                    leftMargins[i] = 270 + (60 * i);
                    if(i == 0 || i == 3){
                        rotations[i] = ((int)(10*(Math.pow(-1.0, i+1))));
                        hand[i].setRotation(rotations[i]);
                        bottomMargins[i] = 54;
                    }else {
                        rotations[i] = ((int) (Math.pow(-1.0, i)) * 5);
                        hand[i].setRotation(rotations[i]);
                        bottomMargins[i]= 60;
                    }
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin = (int)(d*bottomMargins[i]);
                }
                break;
            case 6:
                for (int i = 0; i < 6; i++) {
                    leftMargins[i] = 210 + (60 * i);
                    if(i == 0 || i == 5) {
                        rotations[i] = ((int) (15 * (Math.pow(-1.0, i+1))));
                        hand[i].setRotation(rotations[i]);
                        bottomMargins[i] = 40;
                    }
                    else if(i == 1 || i == 4){
                        rotations[i] = ((int)(10*(Math.pow(-1.0, i))));
                        hand[i].setRotation(rotations[i]);
                        bottomMargins[i] = 54;
                    }else {
                        rotations[i] = ((int) (Math.pow(-1.0, i+1)) * 5);
                        hand[i].setRotation(rotations[i]);
                        bottomMargins[i]= 60;
                    }
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin = (int)(d * bottomMargins[i]);
                }
                break;
            case 8:
                for (int i = 0; i < 8; i++) {
                    leftMargins[i] = 150 + (60 * i);
                    if(i<4){
                        rotations[i] = -20 + (5*i);
                    }
                    else{
                        rotations[i] = 5 + (5*(i-4));
                    }
                    if(i == 0 || i == 7) {
                        bottomMargins[i] = 24;
                    }
                    else if(i == 1 || i == 6){
                        bottomMargins[i] = 40;
                    }
                    else if(i == 2 || i == 5) {
                        bottomMargins[i] = 54;
                    }
                    else {
                        bottomMargins[i]= 60;
                    }
                    hand[i].setRotation(rotations[i]);
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin = (int)(d * bottomMargins[i]);
                }
                break;
            case 10:
                for (int i = 0; i < 10; i++) {
                    leftMargins[i] = 90 + (60 * i);
                    if(i<5){
                        rotations[i] = -25 + (5*i);
                    }
                    else{
                        rotations[i] = 5 + (5*(i-5));
                    }
                    if(i == 0 || i == 9) {
                        bottomMargins[i] = 2;
                    }
                    else if(i == 1 || i == 8){
                        bottomMargins[i] = 24;
                    }
                    else if(i == 2 || i == 7) {
                        bottomMargins[i] = 40;
                    }
                    else if(i == 3 || i == 6) {
                        bottomMargins[i] = 54;
                    }
                    else {
                        bottomMargins[i]= 60;
                    }
                    hand[i].setRotation(rotations[i]);
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin = (int)(d * bottomMargins[i]);
                }
                break;
            case 3:
            case 5:
            case 7:
            case 9:
                int middle = currentHand.size()/2;
                leftMargins[middle]=360;
                bottomMargins[middle]=65;
                rotations[middle]=0;
                params[middle].leftMargin = (int) (d * leftMargins[middle]);
                params[middle].bottomMargin =  (int) (d *bottomMargins[middle]);
                hand[middle].setRotation(rotations[middle]);

                //right side of the middle card
                for(int i = (middle)+1; i<currentHand.size(); i++){
                    leftMargins[i]=360+(60*(i-middle));
                    bottomMargins[i]=60-(2*i*(i-middle-1));
                    rotations[i]=7*(i-middle);
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin =  (int) (d *bottomMargins[i]);
                    hand[i].setRotation(rotations[i]);
                }
                //left side of the middle card
                for(int i = (middle)-1; i>=0; i--){
                    leftMargins[i]=360-(60*(middle-i));
                    bottomMargins[i]=bottomMargins[middle*2-i];
                    rotations[i]=(7)*(i-middle);
                    params[i].leftMargin = (int) (d * leftMargins[i]);
                    params[i].bottomMargin =  (int) (d *bottomMargins[i]);
                    hand[i].setRotation(rotations[i]);
                }
                break;
        }

        for(int i = 0; i<10; i++){
            hand[i].setLayoutParams(params[i]);
        }

    }

    /**
     * returns a different sound depending on what card is played
     * @param name - name of the card
     * @return sound of the card
     */
    public MediaPlayer cardSounds(String name){
        MediaPlayer sound = new MediaPlayer();
        switch(name){
            case "The Priest":
                sound = MediaPlayer.create(myActivity,R.raw.priest);
                break;
            case "Dracolich":
                sound = MediaPlayer.create(myActivity,R.raw.dracolich);
                break;
            case "Red Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.redroar);
                break;
            case "Bronze Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.bronze);
                break;
            case "Copper Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.copper);
                break;
            case "Gold Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.gold);
                break;
            case "Black Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.black);
                break;
            case "Tiamat":
                sound = MediaPlayer.create(myActivity,R.raw.tiamat);
                break;
            case "The Thief":
                sound = MediaPlayer.create(myActivity,R.raw.thief);
                break;
            case "Silver Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.silver);
                break;
            case "Brass Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.brass);
                break;
            case "Bahamut":
                sound = MediaPlayer.create(myActivity,R.raw.bahamut);
                break;
            case "Green Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.green);
                break;
            case "Blue Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.blue);
                break;
            case "White Dragon":
                sound = MediaPlayer.create(myActivity,R.raw.white);
                break;

        }
        return sound;
    }

    @Override
    public View getTopView() {
        return null;
    }

}
