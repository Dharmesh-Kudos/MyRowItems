package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItUserProfileActivity extends AppCompatActivity {

    RecyclerView rvImages;
    CircleImageView imgProPic;
    int radiusArr[];
    Toolbar toolbarData;
    ContentValues ROW;
    String USERID = "";
    SmartCaching smartCaching;
    AQuery aQuery;
    TextView txtName, txtLoc, txtEmail, txtMob, txtMessage;
    ArrayList<ContentValues> allData = new ArrayList<>();
    JSONObject usersData = null;
    RecyclerViewUserAdsGridAdapter recyclerViewUserAdsGridAdapter;
    Bitmap icon;
    private ImageView imageview;
    private JSONObject userData = null;
    private StaggeredGridLayoutManager linearLayoutManager;
    private List<String> elephantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_user_profile);
        smartCaching = new SmartCaching(RentItUserProfileActivity.this);
        aQuery = new AQuery(RentItUserProfileActivity.this);
        rvImages = (RecyclerView) findViewById(R.id.rvPhotos);
        linearLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(linearLayoutManager);
        rvImages.setNestedScrollingEnabled(false);

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtMob = (TextView) findViewById(R.id.txtMob);
        txtName = (TextView) findViewById(R.id.txtName);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        toolbarData = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarData);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarData.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });

        if (getIntent().getStringExtra("FROM").equals("MAIN")) {
            USERID = getIntent().getStringExtra("UID");
        } else {
            ROW = getIntent().getParcelableExtra("ROW");
            try {
                userData = new JSONObject(ROW.getAsString("userData"));
                USERID = userData.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        imgProPic = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imageview = (ImageView) findViewById(R.id.img);
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.no_image);
        getUserDetail(USERID);


    }

    private void getUserDetail(String userid) {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItUserProfileActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fetch Comments");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "openUserDetail");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("userid", userid);

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
                if (responseCode == 200) {
                    try {
                        Log.d("RESULT = ", String.valueOf(response));

                        usersData = response.getJSONObject("userData");
                        if (response.getString("prodCode").equals("160")) {
                            makePage(allData, usersData, false);
                            txtMessage.setText(response.getString("prodData"));//No Prod Yet
                        } else {
                            allData = smartCaching.parseResponse(response.getJSONArray("prodData"), "PRODDATA", null).get("PRODDATA");
                            if (allData != null && allData.size() > 0) {
                                makePage(allData, usersData, true);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RentItUserProfileActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private void makePage(ArrayList<ContentValues> allData, JSONObject usersData, boolean hasProducts) {

        try {
            txtName.setText(usersData.getString("user_name"));
            txtLoc.setText(usersData.getString("user_city"));
            txtEmail.setText(usersData.getString("user_email"));
            txtMob.setText(usersData.getString("user_phone"));
            aQuery.id(imgProPic).image(usersData.getString("user_pic"), true, true);
            //aQuery.id(imageview).image(usersData.getString("user_pic"), true, true);

            Glide.with(RentItUserProfileActivity.this).load(usersData.getString("user_pic")).placeholder(R.drawable.no_image).error(R.drawable.no_image)
                    .bitmapTransform(new BlurTransformation(RentItUserProfileActivity.this))
                    .into((ImageView) findViewById(R.id.img));

            if (hasProducts) {
                StringBuilder photo = new StringBuilder();
                for (int i = 0; i < allData.size(); i++) {
                    ContentValues row = allData.get(i);
                    photo.append(row.getAsString("photo") + ",");
                }
                elephantList = Arrays.asList(photo.toString().split(","));

                recyclerViewUserAdsGridAdapter = new RecyclerViewUserAdsGridAdapter();
                rvImages.setAdapter(recyclerViewUserAdsGridAdapter);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class RecyclerViewUserAdsGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_user_gallery_images_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new RecyclerViewUserAdsGridAdapter.ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            RecyclerViewUserAdsGridAdapter.ViewHolder holder = (RecyclerViewUserAdsGridAdapter.ViewHolder) viewHolder;


            holder.pbar.setVisibility(View.GONE);
//            elephantList = Arrays.asList(photo.split(","));
            if (elephantList.get(position).contains("http")) {

                Picasso.with(RentItUserProfileActivity.this).load(elephantList.get(position)).placeholder(R.drawable.no_image).into(holder.ivImages);
//                aQuery.id(holder.ivImages).progress(holder.pbar).image(elephantList.get(position), true, true, 0, 0, icon, Constants.FADE_IN);

            } else {

                Picasso.with(RentItUserProfileActivity.this).load("http://" + elephantList.get(position)).placeholder(R.drawable.no_image).into(holder.ivImages);
                //aQuery.id(holder.ivImages).progress(holder.pbar).image("http://" + elephantList.get(position), true, true, 0, 0, icon, Constants.FADE_IN);

            }

        }

        @Override
        public int getItemCount() {
            return elephantList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImages;
            public ProgressBar pbar;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImages = (ImageView) itemView.findViewById(R.id.ivImages);
                pbar = (ProgressBar) itemView.findViewById(R.id.pbar);
            }
        }
    }

}
