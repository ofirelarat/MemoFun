package com.ofirelarat.memofun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IGameStateChangeActions {
    private final int MAX_WIDTH = 6;
    private final int MAX_HEIGHT = 7;
    private final int MAX_PICKED = 10;
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

    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetGameSizeAndScore();
        cards = initCards();
        updateCardsGrid(cards, GameStatus.InitGame);
        rewardedAd = createAndLoadRewardedAd();
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
            showWinningAnimation();
            updateGameSizes();
            restartGame();
        }else {
            showRewardedAd();

            SharedPreferencesMgr preferencesMgr = new SharedPreferencesMgr(this);
            preferencesMgr.writeNewScoerifIsHigher(gameScore);

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
        winningAnim.playAnimation();
        winningAnim.setVisibility(View.VISIBLE);
    }

    private void showEndGameDialog(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.end_game_dialog_view, null, false);
        new AlertDialog.Builder(this)
                .setTitle("You are a loser!")
                .setMessage("your score is: " + gameScore)
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

    private RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    private void showRewardedAd(){
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    rewardedAd = createAndLoadRewardedAd();
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                }

                @Override
                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display
                }
            };
            rewardedAd.show(this, adCallback);
        } else {
            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
        }
    }
}
