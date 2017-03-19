package com.example.tasol.myrowitems;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItSignupFragment extends Fragment {

    Button button;
    EditText edtUsername, edtPassword, edtEmail, edtPhone, edtCity;
    private ProgressDialog progressDialog;

    public RentItSignupFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rent_it_signup, container, false);
        button = (Button) v.findViewById(R.id.btnSignUp);
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtPhone = (EditText) v.findViewById(R.id.edtPhone);
        edtCity = (EditText) v.findViewById(R.id.edtCity);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(getActivity(), "Rent It", "Authenticating...");

                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.show();


                HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(TASK, "register");
                    JSONObject taskData = new JSONObject();
                    try {

                        taskData.put("name", edtUsername.getText().toString().trim());
                        taskData.put("password", edtPassword.getText().toString().trim());
                        taskData.put("email", edtEmail.getText().toString().trim());
                        taskData.put("phone", edtPhone.getText().toString().trim());
                        taskData.put("city", edtCity.getText().toString().trim());
                        taskData.put("is_admin", "0");
                        taskData.put("varified", "0");
                        taskData.put("remember_token", "kjdahfjkhfuincskuckdcjsdkuc");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        taskData.put("created_at", currentDateandTime);
                        taskData.put("updated_at", currentDateandTime);
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

                        try {
                            if (responseCode == 200) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                ((RentItLoginActivity) getActivity()).selectFragment(0);
                            } else if (responseCode == 204) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (responseCode == 205) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
            }
        });

        return v;
    }

}
