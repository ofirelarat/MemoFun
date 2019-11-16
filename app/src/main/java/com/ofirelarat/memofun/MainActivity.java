package com.ofirelarat.memofun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.airbnb.lottie.LottieAnimationView;

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

    public void restartGame() {
        cards = initCards();
        updateCardsGrid(cards, GameStatus.InitGame);
        refreshGridAfterShowRightCards();
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
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.end_game_dialog_view, null, false);
        LottieAnimationView lottieAnim = view.findViewById(R.id.lottie_anim);
        if (isUserWin) {
            lottieAnim.setAnimation(R.raw.trophy_lottie);
            lottieAnim.playAnimation();
        } else {
            lottieAnim.setAnimation(R.raw.failed_lottie);
            lottieAnim.playAnimation();
        }
        String title = isUserWin ? "You are a Win!" : "You are a loser!";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.restart:
                restartGame();
                break;
            case R.id.endGame:
                updateCardsGrid(cards, GameStatus.EndGame);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
