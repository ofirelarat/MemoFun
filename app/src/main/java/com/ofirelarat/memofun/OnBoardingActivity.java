package com.ofirelarat.memofun;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AhoyOnboarderActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(
                "Simple Memory Game",
                "Try to remember which block signed",
                R.drawable.screen1);
        ahoyOnboarderCard1.setIconLayoutParams(550, 750, 60, 25, 25, 100);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(
                "Now it's your turn",
                "Check the blocks you remembered",
                R.drawable.screen2);
        ahoyOnboarderCard2.setIconLayoutParams(550, 750, 60, 25, 25, 100);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(
                "Too easy?",
                "Get as much points as you can while the game become harder and harder",
                R.drawable.screen3);
        ahoyOnboarderCard2.setIconLayoutParams(550, 750, 60, 25, 25, 100);


        List<AhoyOnboarderCard> onboardingPages = new ArrayList<AhoyOnboarderCard>();
        onboardingPages.add(ahoyOnboarderCard1);
        onboardingPages.add(ahoyOnboarderCard2);
        onboardingPages.add(ahoyOnboarderCard3);

        setGradientBackground();
        setOnboardPages(onboardingPages);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent mainIntent = new Intent(this, HomePage.class);
        startActivity(mainIntent);
        finish();
    }
}
