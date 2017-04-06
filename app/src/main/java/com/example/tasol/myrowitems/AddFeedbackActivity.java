package com.example.tasol.myrowitems;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class AddFeedbackActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtName, edtEmail, edtPhone, edtMsg;
    Button btnSend;
    private SweetAlertDialog pDialogVisit;
    private SweetAlertDialog pDialog;
    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtName.getText().toString().length() > 0) {
                    if (edtEmail.getText().toString().length() > 0) {

                        if (SmartUtils.emailValidator(edtEmail.getText().toString())) {

                            if (edtPhone.getText().toString().length() == 10) {

                                if (edtMsg.getText().toString().length() > 0) {
                                    isValid = true;
                                } else {
                                    edtMsg.setError("Write some feedback");
                                }

                            } else {
                                edtPhone.setError("Invalid Mobile number");
                            }
                        } else {
                            edtEmail.setError("Invalid email address");
                        }

                    } else {
                        edtEmail.setError("Enter email address");
                    }
                } else {
                    edtName.setError("Enter username");
                }

                if (isValid) {

                    addFeedback();
                }

            }
        });
    }

    private void addFeedback() {
        pDialog = new SweetAlertDialog(AddFeedbackActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Feedback Sending...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AddFeedbackActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Add Feedback");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "addFeedback");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("name", edtName.getText().toString());
                taskData.put("phone", edtPhone.getText().toString());
                taskData.put("email", edtEmail.getText().toString());
                taskData.put("feedback", edtMsg.getText().toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                pDialog.dismiss();
                if (responseCode == 200) {
                    try {
                        Log.d("RESULT = ", String.valueOf(response));
                        pDialogVisit = new SweetAlertDialog(AddFeedbackActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialogVisit.setTitleText("Thank You!!!");
                        pDialogVisit.setContentText("Feedback Sent Successfully.");

                        pDialogVisit.setConfirmText("Done");

                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                edtName.setText("");
                                edtEmail.setText("");
                                edtPhone.setText("");
                                edtMsg.setText("");
                                sweetAlertDialog.dismiss();

                            }
                        });
                        pDialogVisit.setCancelable(true);
                        pDialogVisit.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddFeedbackActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }
}
