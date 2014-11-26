package ch.bbbaden.quizme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
 
public class Welcome extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomelayout);
 
        new Handler().postDelayed(new Runnable() {
 
            @Override
            public void run() {
                Intent i = new Intent("ch.bbbaden.quizme.STARTER");
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}
