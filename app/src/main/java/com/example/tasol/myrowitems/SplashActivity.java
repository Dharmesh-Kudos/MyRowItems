package com.example.tasol.myrowitems;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SmartUtils.getLoginParams() != null
                        && !TextUtils.isEmpty(SmartUtils.getLoginParams().toString())) {
                    if (!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(SP_ISLOGOUT, true)) {
                        authentication();
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, RentItLoginActivity.class));
                    finish();
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, jsonObject.toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);

                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else if (responseCode == 204) {
                        Toast.makeText(SplashActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (responseCode == 200) {
//                    try {
//
//                        //this will store logged user information
//                        try {
//                            JSONObject userData = response.getJSONObject("userDaa");
//                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, userData.toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, jsonObject.toString());
//                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_USERNAME, edtUsername.getText().toString().trim());
//                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Log.v("@@@WWE", "response " + response);
//                    String message = "";
//                    try {
//                        message = response.getString("message");
//                    } catch (JSONException je) {
//                        je.printStackTrace();
//                    }
//
//                    SmartUtils.hideProgressDialog();
//                    SmartUtils.showSnackBar(RentItLoginActivity.this, message, Snackbar.LENGTH_LONG);
//                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }


}
