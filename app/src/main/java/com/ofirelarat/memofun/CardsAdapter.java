package com.ofirelarat.memofun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;

import androidx.cardview.widget.CardView;

public class CardsAdapter extends BaseAdapter {
    private CardStatus[] cards;
    private GameStatus gameStatus;
    private Context context;
    private IGameStateChangeActions gameStateChangeActions;

    public CardsAdapter(CardStatus[] cards, Context context, GameStatus gameStatus, IGameStateChangeActions iGameStateChangeActions){
        this.cards = cards;
        this.gameStatus = gameStatus;
        this.context = context;
        this.gameStateChangeActions = iGameStateChangeActions;
    }

    @Override
    public int getCount() {
        return cards.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null ){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_item_view, null, false);
            final CardView cardView = view.findViewById(R.id.card);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gameStatus == GameStatus.InProgress) {
                        cards[i].setCheckedByUser(!cards[i].isCheckedByUser());
                        if (cards[i].isCheckedByUser() && cards[i].isCheckedByGame()) {
                            flipCardAnimation(cardView, R.color.CheckedCardByUser);
                            gameStateChangeActions.updateScoreView();
                            if (isGameEnded()) {
                                gameStateChangeActions.gameEnd(true);
                            }
                        } else if (cards[i].isCheckedByUser() && !cards[i].isCheckedByGame()) {
                            flipCardAnimation(cardView, R.color.CheckedCardError);
                            gameStateChangeActions.gameEnd(false);
                        }
                    }
                }
            });

            switch (gameStatus){
                case InitGame:
                    if(cards[i].isCheckedByGame()) {
                        flipCardAnimation(cardView, R.color.CheckedCardByGame);
                    }
                    break;
                case InProgress:
                    if(cards[i].isCheckedByUser()) {
                        flipCardAnimation(cardView, R.color.CheckedCardByUser);
                    }else{
                        flipCardAnimation(cardView, R.color.NotCheckedCard);
                    }
                    break;
                case EndGame:
                    if((cards[i].isCheckedByGame() && !cards[i].isCheckedByUser())
                        || (!cards[i].isCheckedByGame() && cards[i].isCheckedByUser())){
                        flipCardAnimation(cardView, R.color.CheckedCardError);
                        gameStateChangeActions.gameEnd(false);
                    }
                    break;
            }
        }

        return view;
    }

    private void flipCardAnimation(final CardView cardView, final int cardNewColor){
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f);

        oa1.setDuration(150);
        oa2.setDuration(150);

        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cardView.setCardBackgroundColor(context.getResources().getColor(cardNewColor));
                oa2.start();
            }
        });
        oa1.start();
    }

    private boolean isGameEnded() {
        boolean isGameEnded = true;
        for (CardStatus card : cards) {
            if ((card.isCheckedByGame() && !card.isCheckedByUser())
                    || (!card.isCheckedByGame() && card.isCheckedByUser())) {
                isGameEnded = false;
            }
        }

        return isGameEnded;
    }
}
