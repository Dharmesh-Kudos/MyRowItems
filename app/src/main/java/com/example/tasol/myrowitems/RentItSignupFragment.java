package com.example.tasol.myrowitems;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import android.widget.BaseAdapter;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import se.simbio.encryption.Encryption;
import smart.caching.SmartCaching;
import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;
import third.part.android.util.Base64;

import static android.app.Activity.RESULT_OK;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItSignupFragment extends Fragment {

    CircleImageView imgProPic;
    KudosButton btnSignup;
    KudosEditText edtUsername, edtPassword, edtEmail, edtPhone;
    KudosButton btnCity;
    KudosTextView txtWrongCode;
    LoginButton txtFbLogin;
    LoginManager loginManager;
    private ProgressDialog progressDialog;
    private String imgPath;
    private int PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 601;
    private int SELECT_PICTURE = 1;
    private String selectedImagePath = "";
    private SweetAlertDialog pDialog;
    private String CODE;
    private boolean IS_VERIFIED = false;
    private String verifyMsg;
    private DialogPlus dialogPlusSubCat;
    private ArrayList<String> subCityData;
    private ArrayList<ContentValues> cvSubCatData;
    private String CITYNAME = "null";
    private smart.caching.SmartCaching smartCaching;
    private CustomCityAdapter customSubCatAdapter;
    private boolean isValid = false;
    private Typeface font;
    private AccessToken mAccessToken;
    private String email;
    private String name;
    private String password;
    private CallbackManager callbackManager;
    private boolean isFacebook = false;
    private KudosButton actButton;
    private boolean isData = false;
    private Key publicKey;

    public RentItSignupFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rent_it_signup, container, false);

        callbackManager = CallbackManager.Factory.create();

//        loginManager=LoginManager.getInstance();
//
//        loginManager.registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        mAccessToken = loginResult.getAccessToken();
//                        getUserProfile(mAccessToken);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                        Toast.makeText(getActivity(), getString(R.string.indo_facebook_login_failure_message), Toast.LENGTH_SHORT).show();
////                        SmartUtils.showSnackBar(getActivity(), getString(R.string.indo_facebook_login_failure_message), Snackbar.LENGTH_LONG);
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                        Toast.makeText(getActivity(), getString(R.string.indo_facebook_login_failure_message), Toast.LENGTH_SHORT).show();
//
////                        SmartUtils.showSnackBar(getActivity(), getString(R.string.indo_facebook_login_failure_message), Snackbar.LENGTH_LONG);
//                    }
//                });
        smartCaching = new SmartCaching(getActivity());

        btnSignup = (KudosButton) v.findViewById(R.id.btnSignUp);
        edtUsername = (KudosEditText) v.findViewById(R.id.edtUsername);
        edtPassword = (KudosEditText) v.findViewById(R.id.edtPassword);
        edtEmail = (KudosEditText) v.findViewById(R.id.edtEmail);
        edtPhone = (KudosEditText) v.findViewById(R.id.edtPhone);
        btnCity = (KudosButton) v.findViewById(R.id.btnCity);
        // txtFbLogin=(LoginButton)v.findViewById(R.id.txtFbLogin);
        actButton = (KudosButton) v.findViewById(R.id.fb);
        txtFbLogin = (LoginButton) v.findViewById(R.id.login_button);
        txtFbLogin.setFragment(this);
        imgProPic = (CircleImageView) v.findViewById(R.id.imgProfilePicture);

        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");

        edtEmail.setTypeface(font);
        edtPassword.setTypeface(font);
        edtUsername.setTypeface(font);
        edtPhone.setTypeface(font);
        btnCity.setTypeface(font);
        btnSignup.setTypeface(font);

        actButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

//
//        txtFbLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAccessToken = AccessToken.getCurrentAccessToken();
//                if (mAccessToken != null) {
//
//                    getUserProfile(mAccessToken);
//                } else {
//                    loginManager.logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
//                }
//            }
//        });

        fetchCity();
        imgProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckPermissionForWriteStorage()) {

                    OpenImageChooser();
                }
            }
        });

        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isData) {
                    dialogPlusSubCat.show();

                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

//                encrypter(edtPassword.getText().toString());

                if (edtUsername.getText().toString().length() > 0) {
                    if (edtPassword.getText().toString().length() >= 6) {
                        if (edtEmail.getText().toString().length() > 0) {
                            if (SmartUtils.emailValidator(edtEmail.getText().toString())) {
                                if (edtPhone.getText().toString().length() == 10) {

                                    if (!CITYNAME.equalsIgnoreCase("null")) {
                                        isValid = true;
                                    } else {
                                        btnCity.setError("Select City");
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
                        edtPassword.setError("Enter Password (Min. 6 letters)");
                    }
                } else {
                    edtUsername.setError("Enter username");
                }

                if (isValid) {
                    sendMailAndVerify(false);
                }


            }
        });


        return v;
    }


    String encrypt(String password) {
        String key = "RENTIT";
        String salt = "LARAVEL";
        byte[] iv = new byte[16];

        Encryption encryption = null;
        // we also can generate an entire new Builder
        String encrypted = "";
        try {
            encryption = new Encryption.Builder()
                    .setKeyLength(128)
                    .setKeyAlgorithm("AES")
                    .setCharsetName("UTF8")
                    .setIterationCount(65536)
                    .setKey("mor€Z€cr€tKYss")
                    .setDigestAlgorithm("SHA1")
                    .setSalt("A beautiful salt")
                    .setBase64Mode(Base64.DEFAULT)
                    .setAlgorithm("AES/CBC/PKCS5Padding")
                    .setSecureRandomAlgorithm("SHA1PRNG")
                    .setSecretKeyType("PBKDF2WithHmacSHA1")
                    .setIv(new byte[]{29, 88, -79, -101, -108, -38, -126, 90, 52, 101, -35, 114, 12, -48, -66, -30})
                    .build();
            encrypted = encryption.encrypt(password);

//            Log.d("ENC = ",""+encrypted);
//            String decrypted = encryption.decrypt(encrypted);
//            Log.d("DEC = ",""+decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Encryption encryption = Encryption.getDefault(key, salt, iv);

        return encrypted;

    }

    String encrypter(String theTestText) {
        // Generate key pair for 1024-bit RSA encryption and decryption
        publicKey = null;
        Key privateKey = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            Log.e("TAG", "RSA key pair error");
        }
        // Encode the original data with RSA private key
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(theTestText.getBytes());
        } catch (Exception e) {
            Log.e("TAG", "RSA encryption error");
        }
        Log.d("TAG", Base64.encodeToString(encodedBytes, Base64.DEFAULT));
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
//        tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes) + "\n");
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            edtUsername.setText(object.getString("name"));
                            edtEmail.setText(object.getString("email"));
                            edtPassword.setText(object.getString("id"));
                            email = object.getString("email");
                            name = object.getString("name");
                            password = object.getString("id");

                            selectedImagePath = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            isFacebook = true;
                            Picasso.with(getActivity()).load(selectedImagePath).placeholder(R.drawable.no_image).into(imgProPic);
//                            Glide.with(getActivity()).load(selectedImagePath).placeholder(R.drawable.man).error(R.drawable.no_image)
//                                    .into(imgProPic);
                            Log.d("OPATH =", selectedImagePath);
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

    private void fetchCity() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "fetchCity");
            JSONObject taskData = new JSONObject();
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
                    subCityData = new ArrayList<String>();
                    if (responseCode == 200) {
                        cvSubCatData = smartCaching.parseResponse(response.getJSONArray("cityData"), "CITYDATA", null).get("CITYDATA");
                        //subCityData.add("Choose Sub Category");
                        for (int i = 0; i < cvSubCatData.size(); i++) {
                            subCityData.add(cvSubCatData.get(i).getAsString("name"));
                        }
                        customSubCatAdapter = new CustomCityAdapter(getActivity(), subCityData);
                        dialogPlusSubCat = DialogPlus.newDialog(getActivity())
                                .setAdapter(customSubCatAdapter)
                                .setCancelable(true)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                                        CITYNAME = cvSubCatData.get(position).getAsString("name");
                                        btnCity.setText(CITYNAME);
                                        dialog.dismiss();
                                    }
                                })
                                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        isData = true;

                    } else if (responseCode == 204) {
                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(getActivity(), "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
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

                taskData.put("name", edtUsername.getText().toString().trim());
                taskData.put("password", encrypter(edtPassword.getText().toString()));
                taskData.put("email", edtEmail.getText().toString().trim());
                taskData.put("phone", edtPhone.getText().toString().trim());
                taskData.put("city", btnCity.getText().toString().trim());
                taskData.put("is_admin", "0");
                taskData.put("varified", "1");
                taskData.put("remember_token", publicKey);
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
                        edtEmail.setText("");
                        edtUsername.setText("");
                        edtPassword.setText("");
                        edtPhone.setText("");
                        btnCity.setText("");
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
        if (isFacebook) {
            SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
        } else {
            SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, selectedImagePath, null, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            isCamera = true;
                        } else {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }
                }

                if (isCamera) {
                    selectedImagePath = imgPath;
                    scaleImage(selectedImagePath);
                    imgProPic.setImageURI(Uri.parse(selectedImagePath));

                } else {
                    selectedImagePath = getAbsolutePath(data.getData());
                    selectedImagePath = getRightAngleImage(selectedImagePath);
                    scaleImage(selectedImagePath);
                    imgProPic.setImageURI(Uri.parse(selectedImagePath));

                }


            }
        }
    }

    public String scaleImage(String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 800, 800, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 800, 800, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/myTmpDir");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(path);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    /**
     * This method is used to get image uri from file path.
     *
     * @param path represents image path of SD card.
     * @return Uri
     */
    public Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    /**
     * This method is used to decode image file path to bitmap.
     *
     * @param path represents selected image path.
     * @return Bitmap
     */
    public Bitmap decodeFileFromPath(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getActivity().getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is used to change orietation and rotate image as per needed as per its aspect ratio.
     *
     * @param degree represents degree to rotate the image.
     * @param path   represents selected image path.
     * @return String
     */
    public String rotateImage(int degree, String path) {
        try {
            Bitmap b = decodeFileFromPath(path);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * This method is used to get right angle of image, this method will automatically make image oriented as per its aspect ratio.
     *
     * @param photoPath represents selected image path.
     * @return String
     */
    public String getRightAngleImage(String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(90, photoPath);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(180, photoPath);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(270, photoPath);
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }

    /**
     * This method used to get absolute path from uri.
     *
     * @param uri represented uri
     * @return represented {@link String}
     */
    public String getAbsolutePath(Uri uri) {
        if (Build.VERSION.SDK_INT < 11)
            return RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            return RealPathUtil.getRealPathFromURI_API11to18(getActivity(), uri);

            // SDK > 19 (Android 4.4)
        else
            return RealPathUtil.getRealPathFromURI_API19(getActivity(), uri);
    }

    private void OpenImageChooser() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            OpenImageChooser();
        } else {
            Toast.makeText(getActivity(), "Permission not given", Toast.LENGTH_SHORT).show();
        }

    }

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        imgPath = file.getAbsolutePath();
        return imgUri;
    }

    public boolean CheckPermissionForWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);

            return false;
        }

        return true;
    }

    /***** Adapter class extends with ArrayAdapter ******/
    public class CustomCityAdapter extends BaseAdapter {

        LayoutInflater inflater;
        private ArrayList<String> mCityData;

        /*************  CustomAdapter Constructor *****************/
        public CustomCityAdapter(Context applicationContext, ArrayList<String> mCatData) {

            this.mCityData = mCatData;
            inflater = (LayoutInflater.from(applicationContext));
            /***********  Layout inflator to call external xml layout () **********************/

        }

        @Override
        public int getCount() {
            return mCityData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_rows, null);

            KudosTextView names = (KudosTextView) view.findViewById(R.id.txtItem);
            names.setTypeface(font);
            names.setText(mCityData.get(i));

            return view;
        }

    }

//    public void check(String host, String storeType, String user,
//                             String password)
//    {
//        try {
//
//            //create properties field
//            Properties properties = new Properties();
//
//            properties.put("mail.pop3.host", host);
//            properties.put("mail.pop3.port", "995");
//            properties.put("mail.pop3.starttls.enable", "true");
//            Session emailSession = Session.getDefaultInstance(properties);
//
//            //create the POP3 store object and connect with the pop server
//            Store store = emailSession.getStore("pop3s");
//
//            store.connect(host, user, password);
//
//            //create the folder object and open it
//            Folder emailFolder = store.getFolder("INBOX");
//            emailFolder.open(Folder.READ_ONLY);
//
//            // retrieve the messages from the folder in an array and print it
//            Message[] messages = emailFolder.getMessages();
//            Log.d("HOLA","messages.length---" + messages.length);
//
//            for (int i = 0, n = messages.length; i < n; i++) {
//                Message message = messages[i];
//                Log.d("HOLA","---------------------------------");
//                Log.d("HOLA","Email Number " + (i + 1));
//                Log.d("HOLA","Subject: " + message.getSubject());
//                Log.d("HOLA","From: " + message.getFrom()[0]);
//                Log.d("HOLA","Text: " + message.getContent().toString());
//
//            }
//
//            //close the store and folder objects
//            emailFolder.close(false);
//            store.close();
//
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
