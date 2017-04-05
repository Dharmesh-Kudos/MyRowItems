package com.example.tasol.myrowitems;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

/**
 * Created by tasol on 5/4/17.
 */

public class ForgotPasswordFragment extends Fragment {

    EditText edtEmail;
    Button btnSendEmail;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialogVisit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        btnSendEmail = (Button) v.findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail(edtEmail.getText().toString());
            }
        });
        return v;
    }

    private void sendMail(final String email) {

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Sending Password...");
        pDialog.setCancelable(true);
        pDialog.show();


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "forgotPwd");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "forgotPwd");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("email", edtEmail.getText().toString().trim());

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
                Log.d("RESULT = ", String.valueOf(response));

                try {
                    if (responseCode == 200) {
                        BackgroundMail.newBuilder(getActivity())
                                .withUsername("rentitcontact@gmail.com")
                                .withPassword("rentanything")
                                .withMailto(email)
                                .withSubject("Rent It Forgot Password!!!")
                                .withBody("Hello " + email + ",\n\n" + "Here is your password that you forgot - " + response.getJSONObject("userData").getString("password") + "\n\n Regards,\n KUDOS INC.")
                                .withProcessVisibility(true)
                                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        pDialog.dismiss();
                                        pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                                        pDialogVisit.setTitleText("KUDOS");
                                        pDialogVisit.setContentText("Password Sent to your email successfully!");

                                        pDialogVisit.setConfirmText("Login Again");

                                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

                                                Intent loginIntent = new Intent(getActivity(), RentItLoginActivity.class);
                                                startActivity(loginIntent);
                                            }
                                        });
                                        pDialogVisit.setCancelable(true);
                                        pDialogVisit.show();
                                    }
                                }).withOnFailCallback(new BackgroundMail.OnFailCallback() {
                            @Override
                            public void onFail() {

                                //do some magic
                                Toast.makeText(getActivity(), "Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }).send();


                    } else if (responseCode == 204) {
                        pDialog.dismiss();

                        pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        pDialogVisit.setTitleText("Oops!!!");
                        pDialogVisit.setContentText(response.getString("message"));

                        pDialogVisit.setConfirmText("Try Again");

                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                        pDialogVisit.setCancelable(true);
                        pDialogVisit.show();
                        //Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
}
