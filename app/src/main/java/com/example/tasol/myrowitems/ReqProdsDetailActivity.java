package com.example.tasol.myrowitems;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class ReqProdsDetailActivity extends AppCompatActivity {

    CircleImageView imgProPic;
    TextView txtByUser, txtTitle, txtDesc, txtFrom, txtTo, txtDays, txtByPhone;
    ContentValues ROW;
    Toolbar toolbar;
    Button btnComment, btnReport;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvComments;
    TextView txtLabelComment, txtNCY;
    private AQuery aQuery;
    private SweetAlertDialog pDialog;
    private DialogPlus dialogPlusReport;
    private JSONObject loginParams;
    private SweetAlertDialog pDialogVisit;
    private ArrayList<ContentValues> cvAllCommentsData;
    private smart.caching.SmartCaching smartCaching;
    private RecyclerViewCommentsAdapter recyclerViewCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_prods_detail);
        aQuery = new AQuery(this);
        smartCaching = new SmartCaching(this);
        ROW = getIntent().getParcelableExtra("ROW");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Requested Ad Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });

        btnComment = (Button) findViewById(R.id.btnComment);
        btnReport = (Button) findViewById(R.id.btnReport);
        imgProPic = (CircleImageView) findViewById(R.id.imgProfilePicture);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtByUser = (TextView) findViewById(R.id.txtByUser);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        txtDays = (TextView) findViewById(R.id.txtDays);
        txtByPhone = (TextView) findViewById(R.id.txtByPhone);

        txtTitle.setText(ROW.getAsString("title"));
        txtDesc.setText(ROW.getAsString("description"));
        txtFrom.setText("From " + getString(R.string.rs) + " " + ROW.getAsString("budget_from"));
        txtTo.setText("To " + getString(R.string.rs) + " " + ROW.getAsString("budget_to"));
        txtDays.setText("(For " + ROW.getAsString("days") + " days)");
        try {
            JSONObject userData = new JSONObject(ROW.getAsString("userData"));
            txtByUser.setText(userData.getString("user_name"));
            txtByPhone.setText(userData.getString("user_phone"));
            if (userData.getString("user_pic").equals("")) {
                imgProPic.setImageResource(R.drawable.man);
            } else {
                aQuery.id(imgProPic).image(userData.getString("user_pic"), true, true);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(ReqProdsDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.comments_layout);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(lp);


                final EditText edtComments;

                Button btnSubmitComments, btnCloseComments;
                rvComments = (RecyclerView) dialog.findViewById(R.id.rvComments);
                linearLayoutManager = new LinearLayoutManager(ReqProdsDetailActivity.this);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                rvComments.setHasFixedSize(true);
                rvComments.setLayoutManager(linearLayoutManager);
                txtNCY = (TextView) dialog.findViewById(R.id.txtNCY);
                txtLabelComment = (TextView) dialog.findViewById(R.id.txtLabelComment);
                btnCloseComments = (Button) dialog.findViewById(R.id.btnCloseComments);
                edtComments = (EditText) dialog.findViewById(R.id.edtComment);
                btnSubmitComments = (Button) dialog.findViewById(R.id.btnSubmitComment);
                getAllComments(ROW.getAsString("product_id"));
                dialog.show();

                btnCloseComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnSubmitComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pDialog = new SweetAlertDialog(ReqProdsDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                        pDialog.setTitleText("Adding Comments...");
                        pDialog.setCancelable(true);
                        pDialog.show();

                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ReqProdsDetailActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fetch Comments");
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(TASK, "submitCommentRP");
                            JSONObject taskData = new JSONObject();
                            try {
                                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                                        .getString(SP_LOGGED_IN_USER_DATA, ""));
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime = sdf.format(new Date());
                                SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-dd-MMM HH:mm:ss");
                                String currentTime = sdfTime.format(new Date());
                                taskData.put("product_id", ROW.getAsString("product_id"));
                                taskData.put("user_id", loginParams.getString("id"));
                                taskData.put("comment", edtComments.getText().toString());
                                taskData.put("user_name", loginParams.getString("name"));
                                taskData.put("time", currentTime);
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
                                        getAllComments(ROW.getAsString("product_id"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(ReqProdsDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onResponseError() {

                                SmartUtils.hideProgressDialog();
                            }
                        });


                        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                    }
                });
            }
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlusReport = DialogPlus.newDialog(ReqProdsDetailActivity.this)
                        .setContentHolder(new ViewHolder(R.layout.report_ad_layout))
                        .setCancelable(true)
                        .setGravity(Gravity.TOP)
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialogPlusReport.show();
                final EditText edtReport;
                Button btnSubmit;
                View v = dialogPlusReport.getHolderView();
                edtReport = (EditText) v.findViewById(R.id.edtReport);
                btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pDialog = new SweetAlertDialog(ReqProdsDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                        pDialog.setTitleText("Posting Your Ad...");
                        pDialog.setCancelable(true);
                        pDialog.show();

                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ReqProdsDetailActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Submit Report");
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(TASK, "submitReportRP");
                            JSONObject taskData = new JSONObject();
                            try {
                                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                                        .getString(SP_LOGGED_IN_USER_DATA, ""));
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime = sdf.format(new Date());
                                SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-dd-MMM HH:mm:ss");
                                String currentTime = sdfTime.format(new Date());

                                taskData.put("user_id", loginParams.getString("id"));
                                taskData.put("product_id", ROW.getAsString("product_id"));
                                taskData.put("report", edtReport.getText().toString());
                                taskData.put("time", currentTime);
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
                                dialogPlusReport.dismiss();
                                if (responseCode == 200) {
                                    try {
                                        Log.d("RESULT = ", String.valueOf(response));
                                        pDialogVisit = new SweetAlertDialog(ReqProdsDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                        pDialogVisit.setTitleText("KUDOS");
                                        pDialogVisit.setContentText("Ad Reported Successfully!");

                                        pDialogVisit.setConfirmText("Done");

                                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();

                                            }
                                        });
                                        pDialogVisit.setCancelable(true);
                                        pDialogVisit.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(ReqProdsDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onResponseError() {

                                SmartUtils.hideProgressDialog();
                            }
                        });


                        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                    }
                });
            }
        });
    }

    private void getAllComments(String product_id) {
        pDialog = new SweetAlertDialog(ReqProdsDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Fetching Comments...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ReqProdsDetailActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fetch Comments");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "fetchCommentsRP");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("product_id", product_id);

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
                    txtNCY.setVisibility(View.GONE);
                    rvComments.setVisibility(View.VISIBLE);
                    try {
                        Log.d("RESULT = ", String.valueOf(response));
                        cvAllCommentsData = smartCaching.parseResponse(response.getJSONArray("commentsData"), "ALLCOMMENTS", null).get("ALLCOMMENTS");

                        if (cvAllCommentsData != null && cvAllCommentsData.size() > 0) {
                            txtLabelComment.setText("Comments(" + String.valueOf(cvAllCommentsData.size()) + ")");
                            recyclerViewCommentsAdapter = new RecyclerViewCommentsAdapter();
                            rvComments.setAdapter(recyclerViewCommentsAdapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 204) {
                    txtLabelComment.setText("Comments(0)");
                    txtNCY.setVisibility(View.VISIBLE);
                    rvComments.setVisibility(View.GONE);
//                    Toast.makeText(RentItAdDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private class RecyclerViewCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;
            ContentValues row = cvAllCommentsData.get(position);
            holder.txtUsername.setText(row.getAsString("user_name"));
            holder.txtDate.setText(row.getAsString("time"));
            holder.txtComment.setText(row.getAsString("comment"));

        }

        @Override
        public int getItemCount() {
            return cvAllCommentsData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtUsername, txtDate, txtComment;

            public ViewHolder(View itemView) {
                super(itemView);
                txtComment = (TextView) itemView.findViewById(R.id.txtComment);
                txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);

                txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            }
        }
    }
}
