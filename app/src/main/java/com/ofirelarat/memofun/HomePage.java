package com.ofirelarat.memofun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutionException;

public class HomePage extends AppCompatActivity {

    SharedPreferencesMgr preferencesMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        preferencesMgr = new SharedPreferencesMgr(this);
        setUpView();

        GoogleAccountMgr.init(this);
        AdManager.getInstance(this);

        openRateThisAppDialog();
    }

    public void onClickStart(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    private static final int RC_LEADERBOARD_UI = 9004;

    public void onClickLeaderBoard(View view) {
        try {
            GoogleSignInAccount signInAccount = GoogleAccountMgr.getSignInAccount();
            Games.getLeaderboardsClient(getApplicationContext(), signInAccount)
                    .getLeaderboardIntent("CgkIwPus0NQdEAIQAQ")
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomePage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Throwable ex) {
            Toast.makeText(HomePage.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSetSilent(View view) {
        preferencesMgr.setIsSilent();
        setSilentModeImageButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            GoogleSignInAccount signInAccount = result.getSignInAccount();
            GoogleAccountMgr.setSignInAccount(signInAccount);
        }
    }

    private void setUpView(){
        View scoreContainer = findViewById(R.id.scoreContainer);
        int highScore = preferencesMgr.getHighScore();
        if(highScore != 0){
            scoreContainer.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.scoreText)).setText(String.valueOf(highScore));
        }else{
            scoreContainer.setVisibility(View.GONE);
        }

        setSilentModeImageButton();
    }

    private void openRateThisAppDialog(){
        final String appURL = "https://play.google.com/store/apps/details?id=com.ofirelarat.memofun";

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .playstoreUrl(appURL)
                .session(4)
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {
                        Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
                    }
                }).build();

        ratingDialog.show();
    }

    private void setSilentModeImageButton(){
        boolean isSilent = preferencesMgr.getIsSilent();
        ImageButton silentModeBtn = findViewById(R.id.silentModeBtn);

        if(isSilent){
            silentModeBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);
        }else{
            silentModeBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }
}

