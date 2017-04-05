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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class AddFAQActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtQue, edtAns;
    Button btnPostFAQ;
    private SweetAlertDialog pDialogVisit;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faq);
        edtQue = (EditText) findViewById(R.id.edtQue);
        edtAns = (EditText) findViewById(R.id.edtAns);
        btnPostFAQ = (Button) findViewById(R.id.btnPostFAQ);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add FAQ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

        btnPostFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addFAQ();
            }
        });
    }

    private void addFAQ() {
        pDialog = new SweetAlertDialog(AddFAQActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("FAQ Submitting...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AddFAQActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Add addFAQ");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "addFAQ");
            JSONObject taskData = new JSONObject();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                taskData.put("que", edtQue.getText().toString());
                taskData.put("ans", edtAns.getText().toString());
                taskData.put("created_at", currentDateandTime);
                taskData.put("updated_at", currentDateandTime);
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
                        pDialogVisit = new SweetAlertDialog(AddFAQActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialogVisit.setTitleText("Thank You!!!");
                        pDialogVisit.setContentText("FAQ Submitted Successfully.");

                        pDialogVisit.setConfirmText("Done");

                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                edtQue.setText("");
                                edtAns.setText("");

                                sweetAlertDialog.dismiss();

                            }
                        });
                        pDialogVisit.setCancelable(true);
                        pDialogVisit.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddFAQActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
