package tbject.com.smstocalendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import tbject.com.smstocalendar.R;

public class OpeningScreen extends CommonActivity {
    private final int TIME_OUT = 2000;
    private static OpeningScreen instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_opening_screen);
        final View myLayout = findViewById(R.id.activity_opening_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(OpeningScreen.this, Menu.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);

    }



    public static OpeningScreen getInstance() {
        return instance;
    }

}