package com.ofirelarat.memofun;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.net.MalformedURLException;
import java.net.URL;

public class AdManager {
    private final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9869565169779070/2849198775";

    private final String ADMOB_PUBLISHER_ID = "pub-9869565169779070";

    private InterstitialAd interstitialAd;

    private static AdManager instance;
    private static Object lock_object = new Object();

    private ConsentForm form;
    private boolean isAdPersonlized = true;

    private AdManager(final Context context){
        final ConsentInformation consentInformation = ConsentInformation.getInstance(context);

        final String[] publisherIds = {ADMOB_PUBLISHER_ID};

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(final InitializationStatus initializationStatus) {
                consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                        // User's consent status successfully updated.
                        if(ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()){
                            switch (consentStatus){
                                case UNKNOWN:
                                    displayConsentForm(context);
                                    break;
                                case PERSONALIZED:
                                    isAdPersonlized = true;
                                    createAndLoadInterstitialAd(context);
                                    break;
                                case NON_PERSONALIZED:
                                    isAdPersonlized = false;
                                    createAndLoadInterstitialAd(context);
                                    break;
                            }
                        }else {
                            createAndLoadInterstitialAd(context);
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(String reason) {
                        // User's consent status failed to update.
                        Log.d("consent failed", reason);
                    }
                });

            }
        });
    }

    public static AdManager getInstance(Context context){
        if(instance == null){
            synchronized (lock_object){
                if(instance == null){
                    instance = new AdManager(context);
                }
            }
        }

        return instance;
    }

    private void createAndLoadInterstitialAd(Context context){
        if(interstitialAd == null) {
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
        }

        AdRequest adRequest;
        if(isAdPersonlized){
            adRequest = new AdRequest.Builder().build();
        }else{
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        }

        interstitialAd.loadAd(adRequest);
    }

    public void showInterstitialAd(Context context){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
            createAndLoadInterstitialAd(context);
        }else{
            createAndLoadInterstitialAd(context);
        }
    }

    private void displayConsentForm(final Context context){
        URL privacyUrl = null;
        try {
            privacyUrl = new URL("");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        form  = new ConsentForm.Builder(context, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        super.onConsentFormLoaded();
                        form.show();
                    }

                    @Override
                    public void onConsentFormError(String reason) {
                        super.onConsentFormError(reason);
                    }

                    @Override
                    public void onConsentFormOpened() {
                        super.onConsentFormOpened();
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        super.onConsentFormClosed(consentStatus, userPrefersAdFree);
                        if(consentStatus.equals(ConsentStatus.PERSONALIZED)){
                            isAdPersonlized = true;
                        }else{
                            isAdPersonlized = false;
                        }

                        createAndLoadInterstitialAd(context);
                    }
                }).withNonPersonalizedAdsOption().withPersonalizedAdsOption()
                .build();

        form.load();
    }
}
