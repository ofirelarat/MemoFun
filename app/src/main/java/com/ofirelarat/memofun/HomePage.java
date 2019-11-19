package com.ofirelarat.memofun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        View scoreContainer = findViewById(R.id.scoreContainer);

        SharedPreferencesMgr preferencesMgr = new SharedPreferencesMgr(this);
        int highScore = preferencesMgr.getHighScore();
        if(highScore != 0){
            scoreContainer.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.scoreText)).setText(String.valueOf(highScore));
        }else{
            scoreContainer.setVisibility(View.GONE);
        }
    }

    public void onClickStart(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
