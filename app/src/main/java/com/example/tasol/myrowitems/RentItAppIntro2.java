package com.example.tasol.myrowitems;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Created by tasol on 6/4/17.
 */

public class RentItAppIntro2 extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Note here that we DO NOT use setContentView();
        super.onCreate(savedInstanceState);
        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(new RentItLoginFragment());
//        addSlide(new RentItSignupFragment());
//        addSlide(new MyReqAdsFragment());
//        addSlide(new MyPostedAdsFragment());

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntro2Fragment.newInstance("How to Earn...", "From your Unused Products??", R.drawable.thinking, getResources().getColor(R.color.maroon)));
        addSlide(AppIntro2Fragment.newInstance("Take a Pic", "And Fill Up the Form", R.drawable.photocam, getResources().getColor(R.color.colorYellow)));
        addSlide(AppIntro2Fragment.newInstance("Upload on our App", "And Get Instant Renters!!!", R.drawable.sports, getResources().getColor(R.color.colorGreen)));
//        addSlide(AppIntro2Fragment.newInstance("Upload on our App", "And Get Instant Renters!!!", R.drawable.listout, getResources().getColor(R.color.blue)));
        addSlide(AppIntro2Fragment.newInstance("Rent It", "Rent Anything Online", R.drawable.rent_it_web, getResources().getColor(R.color.blue)));

        // OPTIONAL METHODS
        // Override bar/separator color.

        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));


        //setFadeAnimation();
//        setDepthAnimation();
//        setFlowAnimation();
//        setSlideOverAnimation();
//        setZoomAnimation();

        if (getIntent().getStringExtra("FROM").equalsIgnoreCase("MAIN")) {
            // Hide Skip/Done button.
            showSkipButton(false);
            setProgressButtonEnabled(false);
        } else {
            askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS}, 3);

            // Hide Skip/Done button.
            showSkipButton(true);
            setProgressButtonEnabled(true);
            //  setDoneText("Login");
            setBackButtonVisibilityWithDone(true);
            //setColorDoneText(getResources().getColor(R.color.white));

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
        startActivity(new Intent(RentItAppIntro2.this, RentItLoginActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(RentItAppIntro2.this, RentItLoginActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
