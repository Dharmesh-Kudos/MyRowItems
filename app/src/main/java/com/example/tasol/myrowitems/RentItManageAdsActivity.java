package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItManageAdsActivity extends AppCompatActivity {

    RecyclerView rvUserAds;
    AQuery aQuery;
    Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewUserAdsAdapter recyclerViewUserAdsAdapter;
    private smart.caching.SmartCaching smartCaching;
    private TextView txtNotYet;
    private int IN_POS;
    private ArrayList<ContentValues> userAdsData = new ArrayList<>();
    private JSONObject loginParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_manage_ads);

        smartCaching = new SmartCaching(RentItManageAdsActivity.this);
        aQuery = new AQuery(RentItManageAdsActivity.this);
        txtNotYet = (TextView) findViewById(R.id.txtNotYet);
        rvUserAds = (RecyclerView) findViewById(R.id.rvUserAds);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUserAds.setHasFixedSize(true);
        rvUserAds.setLayoutManager(linearLayoutManager);

        // IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Ads");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);

            }
        });

        getAllUserAds();
    }

    private void getAllUserAds() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItManageAdsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "OpenUserAds");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "openUserAds");
            JSONObject taskData = new JSONObject();
            try {
                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                        .getString(SP_LOGGED_IN_USER_DATA, ""));
                taskData.put("userid", loginParams.getString("id"));

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

                try {
                    if (responseCode == 200) {
                        txtNotYet.setVisibility(View.GONE);
                        rvUserAds.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        userAdsData = smartCaching.parseResponse(response.getJSONArray("categoryProdData"), "CategoryProds", "userData").get("CategoryProds");
                        if (userAdsData != null && userAdsData.size() > 0) {
                            recyclerViewUserAdsAdapter = new RecyclerViewUserAdsAdapter();
                            rvUserAds.setAdapter(recyclerViewUserAdsAdapter);
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvUserAds.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RentItManageAdsActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
    private class RecyclerViewUserAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_manage_ads_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new RecyclerViewUserAdsAdapter.ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final RecyclerViewUserAdsAdapter.ViewHolder holder = (RecyclerViewUserAdsAdapter.ViewHolder) viewHolder;

            final ContentValues row = userAdsData.get(position);

            holder.txtTitle.setText(row.getAsString("title"));
            holder.txtPrice.setText(getString(R.string.rs) + row.getAsString("price"));

            List<String> elephantList = Arrays.asList(row.getAsString("photo").split(","));

            if (elephantList.get(0).contains("http")) {
                Picasso.with(RentItManageAdsActivity.this).load(elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image(elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            } else {
                Picasso.with(RentItManageAdsActivity.this).load("http://" + elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image("http://" + elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            }

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(RentItManageAdsActivity.this, PostAdActivity.class).putExtra("ROW", row).putExtra("FROM", "PROFILE"));
                }
            });


//            try {
//                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
//                        .getString(SP_LOGGED_IN_USER_DATA, ""));
//
//                JSONObject userData = new JSONObject(row.getAsString("userData"));
////                if (row.getAsString("user_id").equals(loginParams.getString("id"))) {
////                    holder.txtUsername.setText("Uploaded By YOU");
////                } else {
////                    holder.txtUsername.setText("Uploaded By " + userData.getString("user_name"));
////                }
//                if (userData.getString("user_pic").equals("")) {
//                    holder.imgProfilePicture.setImageResource(R.drawable.man);
//                } else {
//                    aQuery.id(holder.imgProfilePicture).image(userData.getString("user_pic"), true, true);
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(RentItManageAdsActivity.this, RentItAdDetailActivity.class);
//                    intent.putExtra("POS", position);
//                    intent.putExtra("ROW", row);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());
//                        Pair<View, String> p2 = Pair.create((View) holder.imageCat, holder.imageCat.getTransitionName());
//
//                        ActivityOptionsCompat options = ActivityOptionsCompat.
//                                makeSceneTransitionAnimation(RentItManageAdsActivity.this, p1, p2);
//                        startActivity(intent, options.toBundle());
//                    } else {
//                        startActivity(intent);
//                    }
//
//                }
//            });


        }

        @Override
        public int getItemCount() {
            return userAdsData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageCat;
            LinearLayout dataLayout;
            TextView txtTitle, txtPrice, txtUsername;
            LinearLayout lilo;
            Button btnEdit, btnDelete, btnMakeable;

            public ViewHolder(View itemView) {
                super(itemView);
                btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
                btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
                btnMakeable = (Button) itemView.findViewById(R.id.btnMakeable);
                lilo = (LinearLayout) itemView.findViewById(R.id.lilo);
                txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
                txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
                dataLayout = (LinearLayout) itemView.findViewById(R.id.dataLayout);
                imageCat = (ImageView) itemView.findViewById(R.id.imageCat);
            }
        }
    }
}