package smart.weservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tasol.myrowitems.RentItLoginActivity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;

public class SmartWebManager implements Constants {

    private static final int TIMEOUT = 100000;
    private static SmartWebManager mInstance;

    ;
    private static Context mCtx;

    ;
    private RequestQueue mRequestQueue;

    private SmartWebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SmartWebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmartWebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueueMultipart(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String filePath,
                                               final String message, final boolean isShowSnackbar) {
        MultipartRequestMedia jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());
        //appendLog("@@@WsParams::::::" + jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new MultipartRequestMedia((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    //appendLog("@@@WSResponse::::::" + response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams());
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {

                                    addToRequestQueueMultipart(requestParams, filePath, message, isShowSnackbar);
                                } else {

                                    //redirect to login page
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

                                    Intent loginIntent = new Intent((Activity) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT),
                                            RentItLoginActivity.class);
                                    SmartUtils.clearActivityStack((Activity) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT),
                                            loginIntent);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        Log.v("@@@WsSessionParams", requestParamsForSession.toString());
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipart(requestParamsForSession, null, filePath, false);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("@@@@@WSResponse", error.toString());
                    //appendLog("@@@WSResponse::::::" + error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueMultipartUpload(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String[] filePath,
                                                     final String message, final boolean isShowSnackBar) {
        MultipartRequestMedia jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());
        //appendLog("@@@@@WsParams:::" + jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new MultipartRequestMedia((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    //appendLog("@@@@@WSResponse:::" + response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams());
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {
                                    addToRequestQueueMultipartUpload(requestParams, filePath, message, isShowSnackBar);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipartUpload(requestParamsForSession, null, "", isShowSnackBar);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //appendLog("@@@@@WSResponse:::" + error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueMultipartFile(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String filePath,
                                                   final String message, final boolean isShowSnackbar) {
        MultipartRequestFile jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());
        //appendLog("@@@WsParams::::::" + jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new MultipartRequestFile((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    //appendLog("@@@WSResponse::::::" + response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams());
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {

                                    addToRequestQueueMultipartFile(requestParams, filePath, message, isShowSnackbar);
                                } else {

                                    //redirect to login page
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

                                    Intent loginIntent = new Intent((Activity) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT),
                                            RentItLoginActivity.class);
                                    SmartUtils.clearActivityStack((Activity) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT),
                                            loginIntent);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        Log.v("@@@WsSessionParams", requestParamsForSession.toString());
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipartFile(requestParamsForSession, null, filePath, false);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("@@@@@WSResponse", error.toString());
                    //appendLog("@@@WSResponse::::::" + error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueMultipleFilesUpload(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final ArrayList<String> imagePath,
                                                         final ArrayList<String> filePath, final String message, final boolean isShowSnackBar) {
        MultipartRequestMedia jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());
        //appendLog("@@@@@WsParams:::" + jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new MultipartRequestMedia((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, imagePath,
                    filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    //appendLog("@@@@@WSResponse:::" + response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams());
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {

                                    addToRequestQueueMultipleFilesUpload(requestParams, imagePath, filePath, message, isShowSnackBar);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipleFilesUpload(requestParamsForSession, null, null, "", isShowSnackBar);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //appendLog("@@@@@WSResponse:::" + error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueFilesUploadWithTag(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String filePath,
                                                        final String fileTag, final String message, final boolean isShowSnackBar) {
        MultipartRequestMedia jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());
        //appendLog("@@@@@WsParams:::" + jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new MultipartRequestMedia((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath,
                    fileTag, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    //appendLog("@@@@@WSResponse:::" + response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams());
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {

                                    addToRequestQueueFilesUploadWithTag(requestParams, filePath, fileTag, message, isShowSnackBar);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipleFilesUpload(requestParamsForSession, null, null, "", isShowSnackBar);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            String errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, message);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackBar && !TextUtils.isEmpty(errorMessage)) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //appendLog("@@@@@WSResponse:::" + error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    /**
     * This method will write any text string to the log file generated by the
     * SmartFramework.
     *
     * @param text = String text is the text which is to be written to the log
     *             file.
     */
    private void appendLog(String text) {
        File logFile = new File("sdcard/rentitReqObj.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append("\n\n");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public enum REQUEST_METHOD_PARAMS {
        CONTEXT, PARAMS, REQUEST_TYPES, TAG, URL, TABLE_NAME,
        UN_NORMALIZED_FIELDS, RESPONSE_LISTENER, SHOW_SNACKBAR
    }


    public enum REQUEST_TYPE {JSON_OBJECT, JSON_ARRAY, IMAGE}

    public interface OnResponseReceivedListener {
        void onResponseReceived(JSONObject tableRows, boolean isValidResponse, int responseCode);

        void onResponseError();
    }
}