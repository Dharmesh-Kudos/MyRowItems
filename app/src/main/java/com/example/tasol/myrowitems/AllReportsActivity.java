package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class AllReportsActivity extends AppCompatActivity {

    RecyclerView rvAllReports;
    AQuery aQuery;
    Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAllUserAdapter recyclerViewAllUserAdapter;
    private smart.caching.SmartCaching smartCaching;
    private KudosTextView txtNotYet;
    private int IN_POS;
    private ArrayList<ContentValues> allUsersData = new ArrayList<>();
    private JSONObject loginParams = null;
    private SweetAlertDialog pDialogVisit;
    private SweetAlertDialog pDialog;
    private ArrayList<ContentValues> categoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);
        smartCaching = new SmartCaching(AllReportsActivity.this);
        aQuery = new AQuery(AllReportsActivity.this);
        txtNotYet = (KudosTextView) findViewById(R.id.txtNotYet);
        rvAllReports = (RecyclerView) findViewById(R.id.rvAllReports);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAllReports.setHasFixedSize(true);
        rvAllReports.setLayoutManager(linearLayoutManager);

        // IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();


            }
        });
        getAllUserAds();
    }

    private void getAllUserAds() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AllReportsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "fetchAllReport");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "fetchAllReport");
            JSONObject taskData = new JSONObject();

            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {

                try {
                    if (responseCode == 200) {
                        txtNotYet.setVisibility(View.GONE);
                        rvAllReports.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        allUsersData = smartCaching.parseResponse(response.getJSONArray("allReportData"), "ALLREPORTDATA", "userData").get("ALLREPORTDATA");
                        if (allUsersData != null && allUsersData.size() > 0) {
                            recyclerViewAllUserAdapter = new RecyclerViewAllUserAdapter();
                            rvAllReports.setAdapter(recyclerViewAllUserAdapter);
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvAllReports.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AllReportsActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }

    private void deleteAd(String product_id) {
        pDialog = new SweetAlertDialog(AllReportsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Deleting  Ad...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AllReportsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Submit Report");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "deleteAd");
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
                    try {
                        Log.d("RESULT = ", String.valueOf(response));
                        pDialogVisit = new SweetAlertDialog(AllReportsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialogVisit.setTitleText("KUDOS");
                        pDialogVisit.setContentText("Ad Deleted Successfully!");

                        pDialogVisit.setConfirmText("Done");

                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                getAllUserAds();
                            }
                        });
                        pDialogVisit.setCancelable(true);
                        pDialogVisit.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AllReportsActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private void openReportedAd(String product_id) {
        pDialog = new SweetAlertDialog(AllReportsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Opening  Ad...");
        pDialog.setCancelable(true);
        pDialog.show();
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AllReportsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "getReportedAd");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "getReportedAd");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("pid", product_id);

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
                try {
                    if (responseCode == 200) {
                        txtNotYet.setVisibility(View.GONE);
                        Log.d("RESULT = ", String.valueOf(response));
                        categoryData = smartCaching.parseResponse(response.getJSONArray("reportedAdData"), "REPORTEDADDATA", "userData").get("REPORTEDADDATA");
                        if (categoryData != null && categoryData.size() > 0) {
                            ContentValues ROW = categoryData.get(0);
                            startActivity(new Intent(AllReportsActivity.this, RentItAdDetailActivity.class).putExtra("ROW", ROW));
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);

                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AllReportsActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }

    private class RecyclerViewAllUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_reports_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            final ContentValues row = allUsersData.get(position);

            try {
                JSONObject userData = new JSONObject(row.getAsString("userData"));
                holder.txtName.setText(userData.getString("user_name"));

                holder.txtReport.setText(Html.fromHtml("&ldquo; " + row.getAsString("report") + " &rdquo;"));

                holder.txtPID.setText(row.getAsString("title"));

                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.layoutMgr.getVisibility() == View.GONE) {
                            holder.layoutMgr.setVisibility(View.VISIBLE);
                        } else {
                            holder.layoutMgr.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openReportedAd(row.getAsString("product_id"));
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pDialogVisit = new SweetAlertDialog(AllReportsActivity.this, SweetAlertDialog.WARNING_TYPE);
                    pDialogVisit.setTitleText("All Ads");
                    pDialogVisit.setContentText("Are You Sure you want to Delete?");
                    pDialogVisit.setCancelText("Cancel");
                    pDialogVisit.setConfirmText("Yes");
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
                            deleteAd(row.getAsString("product_id"));
                        }
                    });
                    pDialogVisit.setCancelable(true);
                    pDialogVisit.show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return allUsersData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout layoutMgr;
            KudosTextView txtName, txtReport, txtPID;
            KudosButton btnEdit, btnDelete, btnView;

            public ViewHolder(View itemView) {
                super(itemView);
                btnDelete = (KudosButton) itemView.findViewById(R.id.btnDeleteAd);
                btnView = (KudosButton) itemView.findViewById(R.id.btnViewAd);
                layoutMgr = (LinearLayout) itemView.findViewById(R.id.layoutManager);
                btnEdit = (KudosButton) itemView.findViewById(R.id.btnEdit);
                txtName = (KudosTextView) itemView.findViewById(R.id.txtName);
                txtReport = (KudosTextView) itemView.findViewById(R.id.txtReport);
                txtPID = (KudosTextView) itemView.findViewById(R.id.txtPID);

            }
        }
    }

}
