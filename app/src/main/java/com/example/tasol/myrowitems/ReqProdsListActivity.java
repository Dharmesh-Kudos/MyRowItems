package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class ReqProdsListActivity extends AppCompatActivity {

    RecyclerView rvReqProds;
    AQuery aQuery;
    Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewReqProdsAdapter recyclerViewReqProdsAdapter;
    private ArrayList<ContentValues> categoryData = new ArrayList<>();
    private smart.caching.SmartCaching smartCaching;
    private JSONObject loginParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_prods_list);
        smartCaching = new SmartCaching(ReqProdsListActivity.this);
        aQuery = new AQuery(ReqProdsListActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Requested Ads");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });
        rvReqProds = (RecyclerView) findViewById(R.id.rvReqProds);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvReqProds.setHasFixedSize(true);
        rvReqProds.setLayoutManager(linearLayoutManager);
        rvReqProds.setNestedScrollingEnabled(false);
        getReqProdsList();

    }

    private void getReqProdsList() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ReqProdsListActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "openRequestedAds");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "openRequestedAds");
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
                        rvReqProds.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        categoryData = smartCaching.parseResponse(response.getJSONArray("reqProdData"), "REQPRODDATA", "userData").get("REQPRODDATA");
                        if (categoryData != null && categoryData.size() > 0) {
                            recyclerViewReqProdsAdapter = new RecyclerViewReqProdsAdapter();
                            rvReqProds.setAdapter(recyclerViewReqProdsAdapter);
                        }
                    } else if (responseCode == 204) {
                        rvReqProds.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReqProdsListActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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


    //    private void setupWindowAnimations() {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Explode explode = (Explode) TransitionInflater.from(this).inflateTransition(R.transition.explode);
//                getWindow().setEnterTransition(explode);
//            }
//
//
//    }
    private class RecyclerViewReqProdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.req_prods_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            final ContentValues row = categoryData.get(position);
            holder.txtTitle.setText(row.getAsString("title"));
            holder.txtFrom.setText("From " + getString(R.string.rs) + row.getAsString("budget_from"));
            holder.txtTo.setText("To " + getString(R.string.rs) + row.getAsString("budget_to"));
            holder.txtdays.setText("(For " + row.getAsString("days") + " days)");

            try {
                JSONObject userData = new JSONObject(row.getAsString("userData"));
                if (userData.getString("user_pic").equals("")) {
                    holder.imgProfilePicture.setImageResource(R.drawable.man);
                } else {
                    aQuery.id(holder.imgProfilePicture).image(userData.getString("user_pic"), true, true);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReqProdsListActivity.this, ReqProdsDetailActivity.class);
                    intent.putExtra("POS", position);
                    intent.putExtra("ROW", row);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ReqProdsListActivity.this, p1);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return categoryData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imgProfilePicture;
            TextView txtTitle, txtFrom, txtTo, txtdays;

            public ViewHolder(View itemView) {
                super(itemView);

                txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                txtFrom = (TextView) itemView.findViewById(R.id.txtFrom);
                txtTo = (TextView) itemView.findViewById(R.id.txtTo);
                txtdays = (TextView) itemView.findViewById(R.id.txtDays);
                imgProfilePicture = (ImageView) itemView.findViewById(R.id.imgProfilePicture);
            }
        }
    }
}
