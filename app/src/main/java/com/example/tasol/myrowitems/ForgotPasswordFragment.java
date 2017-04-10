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
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;

import javax.crypto.Cipher;

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

    KudosEditText edtEmail;
    KudosButton btnSendEmail;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialogVisit;
    private boolean isValid = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        edtEmail = (KudosEditText) v.findViewById(R.id.edtEmail);
        btnSendEmail = (KudosButton) v.findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtEmail.getText().toString().length() > 0) {

                    if (SmartUtils.emailValidator(edtEmail.getText().toString())) {
                        isValid = true;
                    } else {
                        edtEmail.setError("Invalid email address");
                    }
                } else {
                    edtEmail.setError("Enter email address");
                }

                if (isValid) {
                    sendMail(edtEmail.getText().toString());
                }
            }
        });
        return v;
    }

    String decrypter(String password, String pkey) {

        // Generate key pair for 1024-bit RSA encryption and decryption
        Key publicKey = null;
        Key privateKey = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            Log.e("TAG", "RSA key pair error");
        }
        // Encode the original data with RSA private key
//        byte[] encodedBytes = null;
//        try {
//            Cipher c = Cipher.getInstance("RSA");
//            c.init(Cipher.ENCRYPT_MODE, privateKey);
//            encodedBytes = c.doFinal(password.getBytes());
//        } catch (Exception e) {
//            Log.e("TAG", "RSA encryption error");
//        }
//        Log.d("TAG", Base64.encodeToString(encodedBytes, Base64.DEFAULT));
        //  return Base64.encodeToString(encodedBytes, Base64.DEFAULT);\
        // Decode the encoded data with RSA public key
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            decodedBytes = c.doFinal(password.getBytes());
        } catch (Exception e) {
            Log.e("TAG", "RSA decryption error");
        }

        return new String(decodedBytes);
//        String key = "RENTIT";
//        String salt = "LARAVEL";
//        byte[] iv = new byte[16];
//
//        Encryption encryption = null;
//        // we also can generate an entire new Builder
//        String encrypted = "";
//        try {
//            encryption = new Encryption.Builder()
//                    .setKeyLength(128)
//                    .setKeyAlgorithm("AES")
//                    .setCharsetName("UTF8")
//                    .setIterationCount(65536)
//                    .setKey("mor€Z€cr€tKYss")
//                    .setDigestAlgorithm("SHA1")
//                    .setSalt("A beautiful salt")
//                    .setBase64Mode(Base64.DEFAULT)
//                    .setAlgorithm("AES/CBC/PKCS5Padding")
//                    .setSecureRandomAlgorithm("SHA1PRNG")
//                    .setSecretKeyType("PBKDF2WithHmacSHA1")
//                    .setIv(new byte[]{29, 88, -79, -101, -108, -38, -126, 90, 52, 101, -35, 114, 12, -48, -66, -30})
//                    .build();
//            encrypted = encryption.decrypt(password);
//
////            Log.d("ENC = ",""+encrypted);
////            String decrypted = encryption.decrypt(encrypted);
////            Log.d("DEC = ",""+decrypted);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        Encryption encryption = Encryption.getDefault(key, salt, iv);
//
//        return encrypted;


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
                                .withBody("Hello " + email + ",\n\n" + "Here is your password that you forgot - " + decrypter(response.getJSONObject("userData").getString("password"), "") + "\n\n Regards,\n KUDOS INC.")
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
