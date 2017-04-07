package com.example.tasol.myrowitems;

import android.content.ContentValues;
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

public class ViewFeedbackActivity extends AppCompatActivity {

    RecyclerView rvFeedback;
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
        setContentView(R.layout.activity_view_feedback);
        smartCaching = new SmartCaching(ViewFeedbackActivity.this);
        aQuery = new AQuery(ViewFeedbackActivity.this);
        txtNotYet = (KudosTextView) findViewById(R.id.txtNotYet);
        rvFeedback = (RecyclerView) findViewById(R.id.rvFeedback);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFeedback.setHasFixedSize(true);
        rvFeedback.setLayoutManager(linearLayoutManager);

        // IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Feedbacks");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();


            }
        });
        getAllFeedbacks();
    }

    private void getAllFeedbacks() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ViewFeedbackActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "viewFeedback");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "viewFeedback");
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
                        rvFeedback.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        allUsersData = smartCaching.parseResponse(response.getJSONArray("feedbackData"), "FEEDBACKDATA", null).get("FEEDBACKDATA");
                        if (allUsersData != null && allUsersData.size() > 0) {
                            recyclerViewAllUserAdapter = new RecyclerViewAllUserAdapter();
                            rvFeedback.setAdapter(recyclerViewAllUserAdapter);
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvFeedback.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewFeedbackActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_feedback_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            final ContentValues row = allUsersData.get(position);


            holder.txtFeedback.setText(Html.fromHtml("&ldquo; " + row.getAsString("message") + " &rdquo;"));

            holder.txtDate.setText(SmartUtils.getFormattedDate(row.getAsString("created_at")));

            holder.txtByUser.setText(row.getAsString("name"));


        }

        @Override
        public int getItemCount() {
            return allUsersData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            KudosTextView txtDate, txtFeedback, txtByUser;

            public ViewHolder(View itemView) {
                super(itemView);
                txtDate = (KudosTextView) itemView.findViewById(R.id.txtDate);
                txtFeedback = (KudosTextView) itemView.findViewById(R.id.txtFeedback);
                txtByUser = (KudosTextView) itemView.findViewById(R.id.txtByUser);


            }
        }
    }
}
