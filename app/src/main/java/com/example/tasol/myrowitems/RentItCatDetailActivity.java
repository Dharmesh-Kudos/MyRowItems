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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItCatDetailActivity extends AppCompatActivity {


    RecyclerView rvCatDetail;
    Toolbar toolbar;
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};
    AQuery aQuery;
    int IN_POS;
    TextView txtNotYet;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewImagesAdapter recyclerViewImagesAdapter;
    private ArrayList<ContentValues> categoryData = new ArrayList<>();
    private smart.caching.SmartCaching smartCaching;
    private JSONObject loginParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentit_cat_detail);
        // setupWindowAnimations();
        txtNotYet = (TextView) findViewById(R.id.txtNotYet);
        smartCaching = new SmartCaching(RentItCatDetailActivity.this);
        aQuery = new AQuery(RentItCatDetailActivity.this);
        IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);

            }
        });
        rvCatDetail = (RecyclerView) findViewById(R.id.rvCatDetail);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCatDetail.setHasFixedSize(true);
        rvCatDetail.setLayoutManager(linearLayoutManager);

        getCategoryList();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    private void getCategoryList() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItCatDetailActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "OpenCategoryDetails");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "openCategory");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("catid", IN_POS);

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
                        rvCatDetail.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        categoryData = smartCaching.parseResponse(response.getJSONArray("categoryProdData"), "CategoryProds", "userData").get("CategoryProds");
                        if (categoryData != null && categoryData.size() > 0) {
                            recyclerViewImagesAdapter = new RecyclerViewImagesAdapter();
                            rvCatDetail.setAdapter(recyclerViewImagesAdapter);
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvCatDetail.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatDetailActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RentItCatDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
    private class RecyclerViewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_cat_detail_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            final ContentValues row = categoryData.get(position);

            holder.txtTitle.setText(row.getAsString("title"));
            holder.txtPrice.setText("Rs." + row.getAsString("price") + "/" + row.getAsString("days") + "days");

            List<String> elephantList = Arrays.asList(row.getAsString("photo").split(","));

            if (elephantList.get(0).contains("http")) {
                //aQuery.id(holder.imageCat).image(elephantList.get(0), true, true).progress(new ProgressDialog(RentItCatDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT));
                Picasso.with(RentItCatDetailActivity.this).load(elephantList.get(0)).into(holder.imageCat,
                        PicassoPalette.with(elephantList.get(0), holder.imageCat)
                                .use(PicassoPalette.Profile.MUTED_DARK)
                                .intoBackground(holder.lilo)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoBackground(holder.lilo, PicassoPalette.Swatch.RGB)
                );
            } else {
                //aQuery.id(holder.imageCat).image("http://" + elephantList.get(0), true, true).progress(new ProgressDialog(RentItCatDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT));
                Picasso.with(RentItCatDetailActivity.this).load("http://" + elephantList.get(0)).into(holder.imageCat,
                        PicassoPalette.with("http://" + elephantList.get(0), holder.imageCat)
                                .use(PicassoPalette.Profile.MUTED_DARK)
                                .intoBackground(holder.lilo)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoBackground(holder.lilo, PicassoPalette.Swatch.RGB)
                );
            }

            try {
                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                        .getString(SP_LOGGED_IN_USER_DATA, ""));

                JSONObject userData = new JSONObject(row.getAsString("userData"));
                if (row.getAsString("user_id").equals(loginParams.getString("id"))) {
                    holder.txtUsername.setText("Uploaded By YOU");
                } else {
                    holder.txtUsername.setText("Uploaded By " + userData.getString("user_name"));
                }
                if (userData.getString("user_pic").equals("")) {
                    holder.imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
                } else {
                    aQuery.id(holder.imgProfilePicture).image(userData.getString("user_pic"), true, true);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (position % 2 == 0) {
//                holder.imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
//            } else {
//                holder.imgProfilePicture.setImageResource(R.drawable.indo_session_avatar);
//            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RentItCatDetailActivity.this, RentItItemDetailActivity.class);
                    intent.putExtra("POS", position);
                    intent.putExtra("ROW", row);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());
                        Pair<View, String> p2 = Pair.create((View) holder.imageCat, holder.imageCat.getTransitionName());
//                        Pair<View, String> p3 = Pair.create((View) holder.dataLayout, holder.dataLayout.getTransitionName());
//                        Pair<View, String> p4 = Pair.create((View) holder.tv1, holder.tv1.getTransitionName());
//                        Pair<View, String> p5 = Pair.create((View) holder.tv2, holder.tv2.getTransitionName());
//                        Pair<View, String> p6 = Pair.create((View) holder.tv3,holder.tv3.getTransitionName());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(RentItCatDetailActivity.this, p1, p2);
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

            public ImageView imageCat;
            CircleImageView imgProfilePicture;
            LinearLayout dataLayout;
            TextView txtTitle, txtPrice, txtUsername;
            LinearLayout lilo;

            public ViewHolder(View itemView) {
                super(itemView);
                lilo = (LinearLayout) itemView.findViewById(R.id.lilo);
                txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
                txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
                dataLayout = (LinearLayout) itemView.findViewById(R.id.dataLayout);
                imgProfilePicture = (CircleImageView) itemView.findViewById(R.id.imgProfilePicture);
                imageCat = (ImageView) itemView.findViewById(R.id.imageCat);
            }
        }
    }



}
