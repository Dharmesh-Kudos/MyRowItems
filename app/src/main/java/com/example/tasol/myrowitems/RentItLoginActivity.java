package com.example.tasol.myrowitems;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;


public class RentItLoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtUsername, edtPassword;
    Button btnSubmit, btnInsert;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_login);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSubmit.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnInsert) {
            progressDialog = ProgressDialog.show(RentItLoginActivity.this, "Rent It", "Authenticating...");

            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.show();


            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItLoginActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(TASK, "register");
                JSONObject taskData = new JSONObject();
                try {

                    taskData.put("user_name", edtUsername.getText().toString().trim());
                    taskData.put("user_age", edtPassword.getText().toString().trim());

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
                    progressDialog.dismiss();
                    if (responseCode == 200) {
                        Toast.makeText(RentItLoginActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 204) {
                        Toast.makeText(RentItLoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RentItLoginActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onResponseError() {

                    SmartUtils.hideProgressDialog();
                }
            });
            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
        } else if (view.getId() == R.id.btnSubmit) {
            progressDialog = ProgressDialog.show(RentItLoginActivity.this, "Rent It", "Authenticating...");

            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.show();


            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItLoginActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(TASK, "login");
                JSONObject taskData = new JSONObject();
                try {

                    taskData.put("user_name", edtUsername.getText().toString().trim());
                    taskData.put("user_age", edtPassword.getText().toString().trim());

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
                    progressDialog.dismiss();
                    if (responseCode == 200) {

                        startActivity(new Intent(RentItLoginActivity.this, MainActivity.class));
                    } else if (responseCode == 204) {
                        Toast.makeText(RentItLoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RentItLoginActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                    }
//                if (responseCode == 200) {
//                    try {
//
//                        //this will store logged user information
//                        try {
//                            JSONObject userData = response.getJSONObject("userData");
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
}
