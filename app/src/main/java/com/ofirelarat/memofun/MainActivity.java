package com.ofirelarat.memofun;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.games.Games;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IGameStateChangeActions {
    private final int MAX_WIDTH = 6;
    private final int MAX_HEIGHT = 7;
    private final int MAX_PICKED = 11;
    private final int MIN_WIDTH = 2;
    private final int MIN_HEIGHT = 2;
    private final int MIN_PICKED = 1;
    private final int CARD_VIEW_HEIGHT = 55;
    private final int CARD_VIEW_WIDTH = 50;

    private int width;
    private int height;
    private int numOfPickedCards;
    private int gameScore;
    private CardStatus[] cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetGameSizeAndScore();
        cards = initCards();
        updateCardsGrid(cards, GameStatus.InitGame);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshGridAfterShowRightCards();
    }

    public void restartGame() {
        cards = initCards();
        updateCardsGrid(cards, GameStatus.InitGame);
        refreshGridAfterShowRightCards();
    }

    private void updateGameSizes(){
        if(width == 2 && height == 2){
            height++;
            numOfPickedCards++;
        } else if(height > width && width < MAX_WIDTH){
            width++;
        } else  if(height == width && height < MAX_HEIGHT && numOfPickedCards < MAX_PICKED){
            height++;
            numOfPickedCards++;
        } else {
            if (width < MAX_WIDTH) {
                width++;
            }
            if (height < MAX_HEIGHT) {
                height++;
            }
            if (numOfPickedCards < MAX_PICKED) {
                numOfPickedCards++;
            }
        }
    }

    private void resetGameSizeAndScore(){
        width = MIN_WIDTH;
        height = MIN_HEIGHT;
        numOfPickedCards = MIN_PICKED;
        gameScore = 0;
        ((TextView)findViewById(R.id.scoreText)).setText(String.valueOf(gameScore));
    }

    private CardStatus[] initCards(){
        Set<Integer> gamePickedCards = pickGameCards();
        CardStatus[] cards = new CardStatus[width * height];
        for(int i = 0; i < cards.length; i++){
            cards[i] = new CardStatus(false, gamePickedCards.contains(i));
        }

        return cards;
    }

    private void updateCardsGrid(CardStatus[] cards, GameStatus gameStatus){
        BaseAdapter adapter = new CardsAdapter(cards, this, gameStatus, this);
        GridView cardsGrid = findViewById(R.id.cardsGrid);
        setGridSize(cardsGrid, height * CARD_VIEW_HEIGHT, width * CARD_VIEW_WIDTH);
        cardsGrid.setNumColumns(width);
        cardsGrid.setAdapter(adapter);
    }

    private void setGridSize(GridView cardsGrid, int newHeight, int newWidth){
        ViewGroup.LayoutParams layoutParams = cardsGrid.getLayoutParams();
        layoutParams.height = convertDpToPixels(newHeight);
        layoutParams.width = convertDpToPixels(newWidth);
        cardsGrid.setLayoutParams(layoutParams);
    }

    public int convertDpToPixels(float dp){
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    private void refreshGridAfterShowRightCards(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            findViewById(R.id.lottie_win_anim).setVisibility(View.GONE);
            updateCardsGrid(cards, GameStatus.InProgress);
            }
        }, 2000);
    }

    private Set<Integer> pickGameCards(){
        Random random = new Random();
        Set<Integer> integerSet = new HashSet<>();
        while (integerSet.size() < numOfPickedCards){
            integerSet.add(random.nextInt(width * height));
        }

        return integerSet;
    }

    @Override
    public void gameEnd(boolean isUserWin) {
        if (isUserWin) {
            makeSoundEffect(R.raw.tada_sound);
            showWinningAnimation();
            updateGameSizes();
            restartGame();
        }else {
            makeSoundEffect(R.raw.sad_sound);

            AdManager.getInstance(this).showInterstitialAd(this);

            SharedPreferencesMgr preferencesMgr = new SharedPreferencesMgr(this);
            preferencesMgr.writeNewScoerifIsHigher(gameScore);
            try {
                Games.getLeaderboardsClient(this, GoogleAccountMgr.getSignInAccount())
                        .submitScore("CgkIwPus0NQdEAIQAQ", gameScore);
            }catch (Exception ex){
                Log.d("error", ex.getMessage());
            }

            showEndGameDialog();
        }
    }

    @Override
    public void updateScoreView() {
        gameScore += 100;
        ((TextView)findViewById(R.id.scoreText)).setText(String.valueOf(gameScore));
    }

    private void showWinningAnimation(){
        final LottieAnimationView winningAnim = findViewById(R.id.lottie_win_anim);
        Random rand = new Random();
        int randomInt = rand.nextInt(5);
        switch(randomInt){
            case 0:
                winningAnim.setSpeed(1.0f);
                winningAnim.setAnimation(R.raw.trophy_lottie);
                break;
            case 1:
                winningAnim.setSpeed(1.0f);
                winningAnim.setAnimation(R.raw.emoji_wink);
                break;
            case 2:
                winningAnim.setSpeed(1.0f);
                winningAnim.setAnimation(R.raw.fireworks_lottie);
                break;
            case 3:
                winningAnim.setSpeed(2.0f);
                winningAnim.setAnimation(R.raw.tick_reveal);
                break;
            case 4:
                winningAnim.setSpeed(1.2f);
                winningAnim.setAnimation(R.raw.loading_crown);
                break;

        }
        winningAnim.playAnimation();
        winningAnim.setVisibility(View.VISIBLE);
    }

    private void showEndGameDialog(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.end_game_dialog_view, null, false);
        new AlertDialog.Builder(this)
                .setTitle("You are a loser!")
                .setMessage("your score is: " + gameScore)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetGameSizeAndScore();
                        restartGame();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.startActivity(new Intent(MainActivity.this, HomePage.class));
                    }
                })
                .show();
    }

    private void makeSoundEffect(int soundRes){
        SharedPreferencesMgr preferencesMgr = new SharedPreferencesMgr(this);
        if(!preferencesMgr.getIsSilent()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundRes);
            mediaPlayer.start();
        }
    }
}
