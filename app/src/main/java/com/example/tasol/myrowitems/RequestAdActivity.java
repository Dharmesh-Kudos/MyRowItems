package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.caching.SmartCaching;
import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RequestAdActivity extends AppCompatActivity {


    private SmartCaching smartCaching;
    private EditText edtTitle;
    private EditText edtDesc;
    private EditText edtBudgetFrom;
    private EditText edtBudgetTo;
    private EditText edtDays;
    private Button btnCategory;
    private Button btnPostAd;
    private ImageView closeIV;
    private ArrayList<ContentValues> cvCatData;
    private ArrayList<String> subCatData;
    private ArrayList<String> catData;
    private CustomCatAdapter customCatAdapter;
    private DialogPlus dialogPlusCat;
    private String CATID;
    private String CATNAME;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialogVisit;
    private boolean ISUPDATE = false;
    private JSONObject loginParams;
    private ContentValues ROW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ad);
        smartCaching = new SmartCaching(RequestAdActivity.this);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtBudgetFrom = (EditText) findViewById(R.id.edtBudgetFrom);
        edtBudgetTo = (EditText) findViewById(R.id.edtBudgetTo);
        edtDays = (EditText) findViewById(R.id.edtDays);
        btnCategory = (Button) findViewById(R.id.spinnerCategory);
        btnCategory.setEnabled(false);
        btnCategory = (Button) findViewById(R.id.spinnerCategory);
        btnPostAd = (Button) findViewById(R.id.btnPostAd);
        closeIV = (ImageView) findViewById(R.id.closeIV);
        fillCategory();

        if (getIntent().getStringExtra("FROM").equalsIgnoreCase("PROFILE")) {
            ISUPDATE = true;
            ROW = getIntent().getParcelableExtra("ROW");
            btnCategory.setText(ROW.getAsString("cat"));
            edtTitle.setText(ROW.getAsString("title"));
            edtDesc.setText(ROW.getAsString("description"));
            edtBudgetFrom.setText(ROW.getAsString("budget_from"));
            edtBudgetTo.setText(ROW.getAsString("budget_to"));
            edtDays.setText(ROW.getAsString("days"));
        }


        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlusCat.show();
            }
        });
        btnPostAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new SweetAlertDialog(RequestAdActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                if (ISUPDATE) {
                    pDialog.setTitleText("Updating Your Ad...");
                } else {
                    pDialog.setTitleText("Posting Your Ad...");
                }
                pDialog.setCancelable(true);
                pDialog.show();


                HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RequestAdActivity.this);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                final JSONObject jsonObject = new JSONObject();
                try {
                    if (ISUPDATE) {
                        jsonObject.put(TASK, "updateReqAds");
                    } else {
                        jsonObject.put(TASK, "submitRequestAd");
                    }

                    JSONObject taskData = new JSONObject();
                    try {
                        loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                                .getString(SP_LOGGED_IN_USER_DATA, ""));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-dd-MMM HH:mm:ss");
                        String currentTime = sdfTime.format(new Date());

                        if (ISUPDATE) {
                            taskData.put("product_id", ROW.getAsString("product_id"));
                            taskData.put("cat", btnCategory.getText().toString().trim());
                            taskData.put("title", edtTitle.getText().toString().trim());
                            taskData.put("desc", edtDesc.getText().toString().trim());
                            taskData.put("budget_from", edtBudgetFrom.getText().toString().trim());
                            taskData.put("budget_to", edtBudgetTo.getText().toString().trim());
                            taskData.put("days", edtDays.getText().toString().trim());
                            taskData.put("updated_at", currentDateandTime);
                        } else {

                            taskData.put("user_id", loginParams.getString("id"));
                            taskData.put("cat", btnCategory.getText().toString().trim());
                            taskData.put("title", edtTitle.getText().toString().trim());
                            taskData.put("desc", edtDesc.getText().toString().trim());
                            taskData.put("budget_from", edtBudgetFrom.getText().toString().trim());
                            taskData.put("budget_to", edtBudgetTo.getText().toString().trim());
                            taskData.put("days", edtDays.getText().toString().trim());
                            taskData.put("time", currentTime);
                            taskData.put("created_at", currentDateandTime);
                            taskData.put("updated_at", currentDateandTime);
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
                                pDialogVisit = new SweetAlertDialog(RequestAdActivity.this, SweetAlertDialog.SUCCESS_TYPE);

                                if (ISUPDATE) {
                                    pDialogVisit.setTitleText("Ad Updated Successfully");
                                    pDialogVisit.setContentText("Lets have a look at your Ad!!!");
                                    pDialogVisit.setCancelText("Back");
                                } else {

                                    pDialogVisit.setTitleText("Ad Posted Successfully");
                                    pDialogVisit.setContentText("Lets have a look at your Ad!!!");
                                    pDialogVisit.setCancelText("Home");
                                }

                                pDialogVisit.setConfirmText("SEE MY AD");
                                pDialogVisit.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        supportFinishAfterTransition();
                                    }
                                });
                                pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        startActivity(new Intent(RequestAdActivity.this, ReqProdsListActivity.class));
                                        finish();
                                    }
                                });
                                pDialogVisit.setCancelable(true);
                                pDialogVisit.show();
                                Toast.makeText(RequestAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (responseCode == 204) {
                                Toast.makeText(RequestAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (responseCode == 205) {
                                Toast.makeText(RequestAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
            }
        });
    }

    private void fillCategory() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RequestAdActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fill Categories");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "getAllCats");
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
                    btnCategory.setEnabled(true);

                    catData = new ArrayList<String>();
                    subCatData = new ArrayList<String>();
                    if (responseCode == 200) {
                        cvCatData = smartCaching.parseResponse(response.getJSONArray("allCategories"), "ALLCATEGORIES", null).get("ALLCATEGORIES");
                        //catData.add(0, "Choose Category");
                        for (int i = 0; i < cvCatData.size(); i++) {

                            catData.add(cvCatData.get(i).getAsString("cat_name"));
                        }
                        customCatAdapter = new CustomCatAdapter(RequestAdActivity.this, catData);
                        dialogPlusCat = DialogPlus.newDialog(RequestAdActivity.this)
                                .setAdapter(customCatAdapter)
                                .setCancelable(true)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        btnCategory.setText(cvCatData.get(position).getAsString("cat_name"));
                                        CATID = cvCatData.get(position).getAsString("cat_id");
                                        CATNAME = cvCatData.get(position).getAsString("cat_name");
                                        dialog.dismiss();


                                    }
                                })
                                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        // dialogPlusCat.show();
                        //btnUpdate.setAdapter(customCatAdapter);
//                        if (ISUPDATE) {
//                            CATNAME = cvCatData.get(Integer.parseInt(UPDCATID) - 1).getAsString("cat_name");
//                        }

                    } else if (responseCode == 204) {
                        Toast.makeText(RequestAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(RequestAdActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }

    /***** Adapter class extends with ArrayAdapter ******/
    public class CustomCatAdapter extends BaseAdapter {

        LayoutInflater inflater;
        private ArrayList<String> mCatData;

        /*************  CustomAdapter Constructor *****************/
        public CustomCatAdapter(Context applicationContext, ArrayList<String> mCatData) {

            this.mCatData = mCatData;
            inflater = (LayoutInflater.from(applicationContext));
            /***********  Layout inflator to call external xml layout () **********************/

        }

        @Override
        public int getCount() {
            return mCatData.size();
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
            TextView names = (TextView) view.findViewById(R.id.txtItem);
            names.setText(catData.get(i));
            return view;
        }

    }
}
