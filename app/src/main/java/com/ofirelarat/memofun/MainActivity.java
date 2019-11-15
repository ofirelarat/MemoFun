package com.ofirelarat.memofun;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements IGameStateChangeActions {
    final int width = 4;
    final int height =  5;
    final int numOfPickedCards = 5;
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

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        refreshGridAfterShowRightCards();
//    }

    public void endGameOnClick(View view) {
        updateCardsGrid(cards, GameStatus.EndGame);
    }


    public void restartOnClick(View view) {
        restartGame();
    }

    public void restartGame() {
        cards = initCards();
        updateCardsGrid(cards, GameStatus.InitGame);
        refreshGridAfterShowRightCards();
    }

    private CardStatus[] initCards(){
        Set<Integer> gamePickedCards = pickGameCards();
        Random rand = new Random();
        CardStatus[] cards = new CardStatus[width * height];
        for(int i = 0; i < cards.length; i++){
            cards[i] = new CardStatus(false, gamePickedCards.contains(i));
        }

        return cards;
    }

    private void updateCardsGrid(CardStatus[] cards, GameStatus gameStatus){
        BaseAdapter adapter = new CardsAdapter(cards, this, gameStatus, this);
        GridView cardsGrid = findViewById(R.id.cardsGrid);
        cardsGrid.setNumColumns(width);
        cardsGrid.setAdapter(adapter);
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
        String title = isUserWin ? "You Win!" : "You loser!";
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Would you like to play again?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
