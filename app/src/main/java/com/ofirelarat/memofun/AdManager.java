package com.ofirelarat.memofun;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager {
    private final String AD_UNIT_ID = "ca-app-pub-9869565169779070/9163056845";

    private RewardedAd rewardedAd;
    private static AdManager instance;
    private static Object lock_object = new Object();

    private AdManager(){

    }

    public static AdManager getInstance(){
        if(instance == null){
            synchronized (lock_object){
                if(instance == null){
                    instance = new AdManager();
                }
            }
        }

        return instance;
    }

    public void loadAd(Context context){
        rewardedAd = createAndLoadRewardedAd(context);
    }

    private RewardedAd createAndLoadRewardedAd(Context context) {
        RewardedAd rewardedAd = new RewardedAd(context, AD_UNIT_ID);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                int err = errorCode;
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        return rewardedAd;
    }

    public void showRewardedAd(final Activity activity){
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    rewardedAd = createAndLoadRewardedAd(activity);
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                }

                @Override
                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display
                    rewardedAd = createAndLoadRewardedAd(activity);
                }
            };
            rewardedAd.show(activity, adCallback);
        } else {
            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
            rewardedAd = createAndLoadRewardedAd(activity);
        }
    }
}
