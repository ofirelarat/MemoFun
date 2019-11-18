package com.ofirelarat.memofun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements IGameStateChangeActions {
    private final int MAX_WIDTH = 6;
    private final int MAX_HEIGHT = 7;
    private final int MAX_PICKED = 10;
    private final int MIN_WIDTH = 2;
    private final int MIN_HEIGHT = 2;
    private final int MIN_PICKED = 1;
    private final int CARD_VIEW_HEIGHT = 55;
    private final int CARD_VIEW_WIDTH = 50;

    private int width = MIN_WIDTH;
    private int height =  MIN_HEIGHT;
    private int numOfPickedCards = MIN_PICKED;
    private CardStatus[] cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        } else if(height > width){
            width++;
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

    private void resetGameSize(){
        width = MIN_WIDTH;
        height = MIN_HEIGHT;
        numOfPickedCards = MIN_PICKED;
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
        Timer timer_interact=new Timer();
        timer_interact.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        updateCardsGrid(cards, GameStatus.InProgress);
                    }
                });
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
            showWinningAnimation();
            updateGameSizes();
            restartGame();
        }else {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.end_game_dialog_view, null, false);
            new AlertDialog.Builder(this)
                    .setTitle("You are a loser!")
                    .setView(view)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetGameSize();
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
    }

    private void showWinningAnimation(){
        final LottieAnimationView winningAnim = findViewById(R.id.lottie_win_anim);
        Random rand = new Random();
        int randomInt = rand.nextInt(3);
        switch(randomInt){
            case 0:
                winningAnim.setAnimation(R.raw.trophy_lottie);
                break;
            case 1:
                winningAnim.setAnimation(R.raw.emoji_wink);
                break;
            case 2:
                winningAnim.setAnimation(R.raw.fireworks_lottie);
                break;
        }
        winningAnim.setVisibility(View.VISIBLE);
        Timer timer_interact=new Timer();
        timer_interact.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        winningAnim.setVisibility(View.GONE);
                    }
                });
            }
        }, 1000);
    }
}
