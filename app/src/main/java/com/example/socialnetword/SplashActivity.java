package com.example.socialnetword;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView icon , icon2;
    TextView txt_mangxahoi;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        icon = (ImageView) findViewById(R.id.icon);
        icon2 = (ImageView) findViewById(R.id.icon2);
        linearLayout = (LinearLayout)findViewById(R.id.liner_layout);
        txt_mangxahoi = (TextView) findViewById(R.id.txt_mangxahoi);

        linearLayout.animate().alpha(0f).setDuration(1);
        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1500);
        animation.setDuration(5000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAminationListener());
        icon.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {

        // Sử dụng trình xử lý với postDelayed được gọi là phương thức runnable run

            @Override

            public void run() {

                Intent i = new Intent(SplashActivity.this, LoginActivity.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, 8*1000); // Thời gian đợi 8s

    }
    // tạo hiệu ứng hình ảnh
    private class MyAminationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon.clearAnimation();
            icon.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
