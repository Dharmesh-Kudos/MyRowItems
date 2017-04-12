package com.example.tasol.myrowitems;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;
import static smart.framework.Constants.SP_USERNAME;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItLoginFragment extends Fragment {


    KudosButton button;
    KudosEditText edtEmail, edtPassword;
    KudosTextView btnForgetPassword;
    ImageView imgShowHide;
    boolean isValid = false;
    LoginButton txtFbLogin;
    KudosTextView txtWrongCode;
    private ProgressDialog progressDialog;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialogVisit;
    private Typeface font;
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    private KudosButton actButton;
    private boolean isFacebook = false;
    private ImageView pwd;
    private String selectedImagePath;
    private String CODE;
    private String verifyMsg;
    private String UNAME;
    private JSONObject userData = null;


    public RentItLoginFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rent_it_login, container, false);
        edtEmail = (KudosEditText) v.findViewById(R.id.edtEmail);
        edtPassword = (KudosEditText) v.findViewById(R.id.edtPassword);
        btnForgetPassword = (KudosTextView) v.findViewById(R.id.btnForgetPassword);
        button = (KudosButton) v.findViewById(R.id.btnLogin);
        callbackManager = CallbackManager.Factory.create();
        txtFbLogin = (LoginButton) v.findViewById(R.id.login_button);
        actButton = (KudosButton) v.findViewById(R.id.fb);
        txtFbLogin.setFragment(this);
//        font=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");
//
//        edtEmail.setTypeface(font);
//        edtPassword.setTypeface(font);
//        btnForgetPassword.setTypeface(font);
//        button.setTypeface(font);

        LoginManager.getInstance().logOut();

        actButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFacebook = true;
                txtFbLogin.performClick();
            }
        });
        txtFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.indo_facebook_login_failure_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), getString(R.string.indo_facebook_login_failure_message), Toast.LENGTH_SHORT).show();
            }
        });
        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().length() > 0) {

                    if (SmartUtils.emailValidator(edtEmail.getText().toString())) {
                        if (edtPassword.getText().toString().length() > 0) {
                            isValid = true;
                        } else {
                            edtPassword.setError("Enter password");
                        }
                    } else {
                        edtEmail.setError("Invalid email address");
                    }
                } else {
                    edtEmail.setError("Enter email address");
                }

                if (isValid) {
                    doLogin("0");
                }

            }
        });

        return v;
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            selectedImagePath = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            UNAME = object.getString("name");
                            edtEmail.setText(object.getString("email"));
                            edtPassword.setText(object.getString("id"));
                            doLogin("1");


                            //sendMailAndVerify(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //  doSignup();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void doLogin(String isFacebookLogin) {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Logging in...");
        pDialog.setCancelable(true);
        pDialog.show();


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "login");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("isFacebookLogin", isFacebookLogin);
                taskData.put("email", edtEmail.getText().toString().trim());
                taskData.put("password", edtPassword.getText().toString().trim());

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
                pDialog.dismiss();
                JSONObject userData = null;
                try {
                    if (responseCode == 200) {


                        //this will store logged user information
                        try {
                            userData = response.getJSONObject("userData");
                            Log.d("userData = ", userData.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, userData.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, jsonObject.toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_USERNAME, userData.getString("name"));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);

                        if (userData.getString("is_blocked").equals("1")) {
                            pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                            pDialogVisit.setTitleText("Account Blocked!!!");
                            pDialogVisit.setContentText("Please contact Admin for further inquiry.");
                            pDialogVisit.setConfirmText("Contact Admin");
                            pDialogVisit.setCancelText("Not Now");
                            pDialogVisit.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                            pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    Intent loginIntent2 = new Intent(getActivity(), ContactUsActivity.class);
                                    startActivity(loginIntent2);
                                    getActivity().finish();
                                }
                            });
                            pDialogVisit.setCancelable(true);
                            pDialogVisit.show();

                            // Toast.makeText(getActivity(), "Your Account is blocked. Please Contact the Admin.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (userData.getString("is_admin").equals("1")) {
                                startActivity(new Intent(getActivity(), MainActivityAdmin.class));
                                getActivity().finish();
                            } else {
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }
                            LoginManager.getInstance().logOut();
                        }


                    } else if (responseCode == 204) {
                        if (isFacebook) {
//                            ((RentItLoginActivity) getActivity()).selectFragment(1);
                            //Toast.makeText(getActivity(), "Your Facebook account is not yet linked with our App.", Toast.LENGTH_LONG).show();
                            sendMailAndVerify(true);
                        } else {
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
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

    private void sendMailAndVerify(boolean isShow) {
        CODE = "" + ((int) (Math.random() * 9000) + 1000);//Generating 4-digit Code

        verifyMsg = "Your 4-digit Verification Code is " + CODE;//Making Verification Message

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        if (isShow) {
            pDialog.setTitleText("Sending Verification Code...");
        } else {
            pDialog.setTitleText("Sending Details...");
        }
        pDialog.setCancelable(false);
        pDialog.show();

        BackgroundMail.newBuilder(getActivity())
                .withUsername("rentitcontact@gmail.com")
                .withPassword("rentanything")
                .withMailto(edtEmail.getText().toString())
                .withSubject("Rent It User Verification !!!")
                .withBody(verifyMsg)
                .withProcessVisibility(true)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        pDialog.dismiss();
                        // check("pop3.gmail.com","pop3","sksunny93@gmail.com","talentgoogle65");
                        //do some magic
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.email_verify_layout);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setAttributes(lp);
                        dialog.setCanceledOnTouchOutside(false);
                        final KudosEditText edtCode1, edtCode2, edtCode3, edtCode4;
                        KudosTextView btnResendCode, btnSignNow;
                        txtWrongCode = (KudosTextView) dialog.findViewById(R.id.txtWrongCode);
                        edtCode1 = (KudosEditText) dialog.findViewById(R.id.edtCode1);
                        edtCode2 = (KudosEditText) dialog.findViewById(R.id.edtCode2);
                        edtCode3 = (KudosEditText) dialog.findViewById(R.id.edtCode3);
                        edtCode4 = (KudosEditText) dialog.findViewById(R.id.edtCode4);

                        btnResendCode = (KudosTextView) dialog.findViewById(R.id.btnResendCode);
                        btnSignNow = (KudosTextView) dialog.findViewById(R.id.btnSignNow);


                        edtCode1.addTextChangedListener(new TextWatcher() {

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // TODO Auto-generated method stub
                                if (edtCode1.getText().toString().length() == 1)     //size as per your requirement
                                {
                                    edtCode2.requestFocus();
                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start,
                                                          int count, int after) {
                                // TODO Auto-generated method stub

                            }

                            public void afterTextChanged(Editable s) {
                                // TODO Auto-generated method stub
                            }

                        });
                        edtCode2.addTextChangedListener(new TextWatcher() {

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // TODO Auto-generated method stub
                                if (edtCode2.getText().toString().length() == 1)     //size as per your requirement
                                {
                                    edtCode3.requestFocus();
                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start,
                                                          int count, int after) {
                                // TODO Auto-generated method stub

                            }

                            public void afterTextChanged(Editable s) {
                                // TODO Auto-generated method stub
                            }

                        });
                        edtCode3.addTextChangedListener(new TextWatcher() {

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // TODO Auto-generated method stub
                                if (edtCode3.getText().toString().length() == 1)     //size as per your requirement
                                {
                                    edtCode4.requestFocus();
                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start,
                                                          int count, int after) {
                                // TODO Auto-generated method stub

                            }

                            public void afterTextChanged(Editable s) {
                                // TODO Auto-generated method stub
                            }

                        });


                        btnSignNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String userInputCode = edtCode1.getText().toString() +
                                        edtCode2.getText().toString() +
                                        edtCode3.getText().toString() +
                                        edtCode4.getText().toString();

                                if (CODE.equals(userInputCode)) {
                                    //IS_VERIFIED = true;
                                    doSignup();
                                    dialog.dismiss();
                                } else {
                                    txtWrongCode.setVisibility(View.VISIBLE);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtWrongCode.setVisibility(View.GONE);
                                        }
                                    }, 2000);

                                }
                            }
                        });

                        btnResendCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                verifyMsg = "";
                                CODE = "";

                                CODE = "" + ((int) (Math.random() * 9000) + 1000);//Generating 4-digit Code

                                verifyMsg = "Your 4-digit Verification Code is " + CODE;//Making Verification Message

                                sendMailAndVerify(true);//Resend Code Again

                            }
                        });


                        dialog.show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {

                        //do some magic
                        Toast.makeText(getActivity(), "Try Again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }

    public void doSignup() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Creating Account...");
        pDialog.setCancelable(false);
        pDialog.show();


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

                taskData.put("name", UNAME);
                taskData.put("password", edtPassword.getText().toString());
                taskData.put("email", edtEmail.getText().toString().trim());
                taskData.put("phone", "0");
                taskData.put("city", "Not Selected Yet");
                taskData.put("is_admin", "0");
                taskData.put("varified", "1");
                taskData.put("isFacebookLogin", "1");

                taskData.put("remember_token", CODE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                taskData.put("created_at", currentDateandTime);
                taskData.put("updated_at", currentDateandTime);
                if (isFacebook) {
                    taskData.put("user_img", selectedImagePath);
                } else {
                    taskData.put("user_img", "normal");
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
                pDialog.dismiss();
                try {
                    if (responseCode == 200) {
                        LoginManager.getInstance().logOut();
                        edtEmail.setText("");
                        edtPassword.setText("");
                        // Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        //this will store logged user information
                        try {
                            userData = response.getJSONObject("userData");
                            Log.d("userData = ", userData.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, userData.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, jsonObject.toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_USERNAME, userData.getString("name"));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);

                        if (userData.getString("is_blocked").equals("1")) {
                            pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                            pDialogVisit.setTitleText("Account Blocked!!!");
                            pDialogVisit.setContentText("Please contact Admin for further inquiry.");
                            pDialogVisit.setConfirmText("Contact Admin");
                            pDialogVisit.setCancelText("Not Now");
                            pDialogVisit.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                            pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    Intent loginIntent2 = new Intent(getActivity(), ContactUsActivity.class);
                                    startActivity(loginIntent2);
                                    getActivity().finish();
                                }
                            });
                            pDialogVisit.setCancelable(true);
                            pDialogVisit.show();

                            // Toast.makeText(getActivity(), "Your Account is blocked. Please Contact the Admin.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (userData.getString("is_admin").equals("1")) {
                                startActivity(new Intent(getActivity(), MainActivityAdmin.class));
                                getActivity().finish();
                            } else {
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }
                            LoginManager.getInstance().logOut();
                        }
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
        if (isFacebook) {
            SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
        } else {
            SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, selectedImagePath, null, true);
        }
    }

}
