package com.example.tasol.myrowitems;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class SplashActivity extends AppCompatActivity {

    KudosTextView txtRent, txtIt, txtDesc;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);
        SmartUtils.setNetworkStateAvailability(this);

        txtRent = (KudosTextView) findViewById(R.id.txtRent);
        txtIt = (KudosTextView) findViewById(R.id.txtIt);
        txtDesc = (KudosTextView) findViewById(R.id.txtDesc);

//        Typeface font=Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
//
//        txtRent.setTypeface(font);
//        txtIt.setTypeface(font);
//        txtDesc.setTypeface(font);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean("IS_FIRST", true)) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_FIRST", false);
                    startActivity(new Intent(SplashActivity.this, RentItAppIntro2.class).putExtra("FROM", "SPLASH"));
                    finish();
                } else {
                    if (SmartUtils.getLoginParams() != null
                            && !TextUtils.isEmpty(SmartUtils.getLoginParams().toString())) {
                        if (!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(SP_ISLOGOUT, true)) {
                            if (SmartUtils.isNetworkAvailable()) {
                                authentication();
                            } else {
                                Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, RentItLoginActivity.class));
                        finish();
                    }
                }
            }
        }, 2000);
    }


    private void authentication() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SplashActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "login");
            JSONObject taskData = new JSONObject();
            try {

                JSONObject loginParams = null;
                try {
                    loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                            .getString(SP_LOGIN_REQ_OBJECT, ""));
                    JSONObject userData = loginParams.getJSONObject("taskData");
                    taskData.put("email", userData.get("email"));
                    taskData.put("password", userData.get("password"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Throwable e) {
            }
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                Log.d("RESULT = ", String.valueOf(response));
                try {

                    if (responseCode == 200) {
                        //this will store logged user information
                        try {
                            JSONObject userData = response.getJSONObject("userData");
                            Log.d("userData = ", userData.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, userData.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, jsonObject.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);
                            if (userData.getString("is_blocked").equals("1")) {
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, "");
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                Toast.makeText(SplashActivity.this, "Your Account is blocked. Please Contact the Admin.", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(SplashActivity.this, RentItLoginActivity.class));
                                finish();
                            } else {
                                if (userData.getString("is_admin").equals("1")) {
                                    startActivity(new Intent(SplashActivity.this, MainActivityAdmin.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (responseCode == 204) {
                        Toast.makeText(SplashActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }


}
