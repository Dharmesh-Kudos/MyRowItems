package com.example.tasol.myrowitems;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by tasol on 6/4/17.
 */

public class RentItAppIntro extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(new RentItLoginFragment());
//        addSlide(new RentItSignupFragment());
//        addSlide(new MyReqAdsFragment());
//        addSlide(new MyPostedAdsFragment());

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Have Anything unused?", "Earn from unused Items", R.drawable.bike, getResources().getColor(R.color.colorGreen)));
        addSlide(AppIntroFragment.newInstance("Take a Pic", "Enter Details", R.drawable.sports, getResources().getColor(R.color.maroon)));
        addSlide(AppIntroFragment.newInstance("Upload on our App", "Get your item on Rent", R.drawable.mobile, getResources().getColor(R.color.colorYellow)));
        addSlide(AppIntroFragment.newInstance("Rent It", "Rent Anything Online", R.drawable.demopic, getResources().getColor(R.color.blue)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        if (getIntent().getStringExtra("FROM").equalsIgnoreCase("MAIN")) {
            // Hide Skip/Done button.
            showSkipButton(false);
            setProgressButtonEnabled(false);
        } else {
            // Hide Skip/Done button.
            showSkipButton(true);
            setProgressButtonEnabled(true);
            setDoneText("Login");
            setBackButtonVisibilityWithDone(true);
            setColorDoneText(getResources().getColor(R.color.white));

            // Hide Skip/Done button.
            showSkipButton(true);
            setProgressButtonEnabled(true);

            // Turn vibration on and set intensity.
            // NOTE: you will probably need to ask VIBRATE permission in Manifest.
            setVibrate(true);
            setVibrateIntensity(30);
        }


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        startActivity(new Intent(RentItAppIntro.this, RentItLoginActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(RentItAppIntro.this, RentItLoginActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
