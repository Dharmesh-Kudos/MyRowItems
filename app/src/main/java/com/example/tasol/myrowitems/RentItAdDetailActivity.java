package com.example.tasol.myrowitems;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static android.view.View.GONE;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItAdDetailActivity extends AppCompatActivity {

    public ImageView imageCat;
    int[] IMAGESOFCATS = {R.drawable.mobiles,
            R.drawable.electronics,
            R.drawable.cars,
            R.drawable.bike,
            R.drawable.jobs,
            R.drawable.furniture,
            R.drawable.book,
            R.drawable.fashion,
            R.drawable.sports,
            R.drawable.services,
            R.drawable.estate,
            R.drawable.pets};
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};
    CircleImageView imgProfilePicture;
    Toolbar toolbarData;
    RecyclerView rvOtherImages;
    //  int[] IMAGESOFCATS = {R.drawable.cat_books, R.drawable.cat_cars, R.drawable.cat_cycle, R.drawable.cat_decor, R.drawable.cat_electronic, R.drawable.cat_fashion, R.drawable.cat_furniture, R.drawable.cat_mobile, R.drawable.cat_real, R.drawable.cat_sports, R.drawable.cat_toys, R.drawable.cats_bikes};
    RecyclerViewCategoryGridAdapter recyclerViewCategoryGridAdapter;
    RecyclerViewCommentsAdapter recyclerViewCommentsAdapter;
    Animation slide_down, slide_up;
    ContentValues ROW;
    TextView txtByUsername, txtByPhone, txtTitle, txtDesc, txtPrice, txtDeposit, txtCondition, txtTime;
    JSONObject userData = null;
    List<String> elephantList;
    Button btnComment, btnReport;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvComments;
    TextView txtLabelComment, txtNCY;
    private int POS = 0;
    private AQuery aQuery;
    private TextView txtDays;
    private DialogPlus dialogPlusReport;
    private JSONObject loginParams = null;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialogVisit;
    private DialogPlus dialogPlusComment;
    private smart.caching.SmartCaching smartCaching;
    private ArrayList<ContentValues> cvAllCommentsData = new ArrayList<>();
    private int IN_POS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_ad_detail);
        aQuery = new AQuery(RentItAdDetailActivity.this);
        smartCaching = new SmartCaching(RentItAdDetailActivity.this);
        ROW = getIntent().getParcelableExtra("ROW");
        IN_POS = getIntent().getIntExtra("IMGPOS", 1);
        toolbarData = (Toolbar) findViewById(R.id.toolbarData);
        setSupportActionBar(toolbarData);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(ROW.getAsString("title"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarData.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });
        Bitmap image = BitmapFactory.decodeResource(getResources(),
                IMAGESOFCATS[IN_POS - 1]);
        Palette.from(image).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {

                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                Palette.Swatch vibrantSwatch2 = palette.getDarkMutedSwatch();
                Log.d("COLOR = ", String.valueOf(vibrantSwatch));
                if (vibrantSwatch != null) {

                    toolbarData.setBackgroundColor(vibrantSwatch.getRgb());
//                    outerLayout.setBackgroundColor(vibrantSwatch.getRgb());
//                    titleText.setTextColor(vibrantSwatch.getTitleTextColor());
//                    bodyText.setTextColor(vibrantSwatch.getBodyTextColor());
                } else {
                    if (vibrantSwatch2 != null) {
                        toolbarData.setBackgroundColor(vibrantSwatch2.getRgb());
                    } else {

                    }
                }
            }
        });
        btnComment = (Button) findViewById(R.id.btnComment);
        btnReport = (Button) findViewById(R.id.btnReport);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtDeposit = (TextView) findViewById(R.id.txtDeposit);
        txtCondition = (TextView) findViewById(R.id.txtCondition);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDays = (TextView) findViewById(R.id.txtDays);
        txtByPhone = (TextView) findViewById(R.id.txtByPhone);
        rvOtherImages = (RecyclerView) findViewById(R.id.rvOtherImages);
        linearLayoutManager = new LinearLayoutManager(this);
        rvOtherImages.setHasFixedSize(true);
        rvOtherImages.setLayoutManager(linearLayoutManager);
        rvOtherImages.setNestedScrollingEnabled(true);

        imgProfilePicture = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imageCat = (ImageView) findViewById(R.id.imageCat);


        int i = getIntent().getIntExtra("POS", 0);
        POS = i;
        elephantList = Arrays.asList(ROW.getAsString("photo").split(","));

        if (elephantList.get(0).contains("http")) {
            Picasso.with(RentItAdDetailActivity.this).load(elephantList.get(0)).placeholder(R.drawable.no_image).into(imageCat);
//            aQuery.id(imageCat).image(elephantList.get(0), true, true).progress(new ProgressDialog(RentItAdDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT));
        } else {
            Picasso.with(RentItAdDetailActivity.this).load("http://" + elephantList.get(0)).placeholder(R.drawable.no_image).into(imageCat);
//            aQuery.id(imageCat).image("http://" + elephantList.get(0), true, true).progress(new ProgressDialog(RentItAdDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT));
        }

        // aQuery.id(imageCat).image(ROW.getAsString("photo"), true, true);

        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RentItAdDetailActivity.this, RentItUserProfileActivity.class);
                intent.putExtra("FROM", "DETAIL");
                intent.putExtra("POS", POS);
                intent.putExtra("ROW", ROW);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> p1 = Pair.create((View) imgProfilePicture, imgProfilePicture.getTransitionName());

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(RentItAdDetailActivity.this, p1);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
//                startActivity(intent);
            }
        });


        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, ""));
            userData = new JSONObject(ROW.getAsString("userData"));
            txtByPhone.setText(userData.getString("user_phone"));
            if (loginParams.getString("id").equals(userData.getString("user_id"))) {
                getSupportActionBar().setSubtitle("Uploaded By YOU");
            } else {
                getSupportActionBar().setSubtitle("Uploaded By " + userData.getString("user_name"));
            }
            if (userData.getString("user_pic").equals("")) {
                imgProfilePicture.setImageResource(R.drawable.man);
            } else {
                aQuery.id(imgProfilePicture).image(userData.getString("user_pic"), true, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtTitle.setText(ROW.getAsString("title"));
        txtDesc.setText(ROW.getAsString("description"));
        txtPrice.setText(getString(R.string.rs) + ROW.getAsString("price"));
        txtDeposit.setText(getString(R.string.rs) + ROW.getAsString("deposite") + "(Security Deposit)");
        txtCondition.setText(ROW.getAsString("condition") + " in condition");
        txtTime.setText(ROW.getAsString("time"));
        txtDays.setText(ROW.getAsString("days"));


        txtByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int checkPermission = ContextCompat.checkSelfPermission(RentItAdDetailActivity.this, Manifest.permission.CALL_PHONE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            RentItAdDetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 201);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    try {
                        callIntent.setData(Uri.parse("tel:" + userData.getString("user_phone")));//change the number
                        startActivity(callIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        //imageCat.setImageResource(IMAGESRRAY[i]);
//        if (i % 2 == 0) {
//            imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
//        } else {
//            imgProfilePicture.setImageResource(R.drawable.indo_session_avatar);
//        }

        if (elephantList.size() == 1) {
            POS = 0;
            rvOtherImages.setVisibility(GONE);
        } else {
            rvOtherImages.setVisibility(View.VISIBLE);
            recyclerViewCategoryGridAdapter = new RecyclerViewCategoryGridAdapter();
            rvOtherImages.setAdapter(recyclerViewCategoryGridAdapter);

        }


        imageCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RentItAdDetailActivity.this, FullImageActivity.class);
                intent.putExtra("POS", POS);
                intent.putExtra("ROW", ROW);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> p1 = Pair.create((View) imageCat, imageCat.getTransitionName());

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(RentItAdDetailActivity.this, p1);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }


            }
        });


        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

//        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.slide_up);

        // Start animation
        rvOtherImages.startAnimation(slide_down);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(RentItAdDetailActivity.this);
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
                linearLayoutManager = new LinearLayoutManager(RentItAdDetailActivity.this);
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

                        if (edtComments.getText().toString().length() > 0) {


                            pDialog = new SweetAlertDialog(RentItAdDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                            pDialog.setTitleText("Adding Comments...");
                            pDialog.setCancelable(true);
                            pDialog.show();

                            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItAdDetailActivity.this);
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fetch Comments");
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                            final JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(TASK, "submitComment");
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
                                        Toast.makeText(RentItAdDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onResponseError() {

                                    SmartUtils.hideProgressDialog();
                                }
                            });


                            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                        } else {
                            edtComments.setError("Please enter comments");
                        }
                    }
                });
            }
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlusReport = DialogPlus.newDialog(RentItAdDetailActivity.this)
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
                        if (edtReport.getText().toString().length() > 0) {
                        pDialog = new SweetAlertDialog(RentItAdDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                        pDialog.setTitleText("Posting Your Ad...");
                        pDialog.setCancelable(true);
                        pDialog.show();

                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItAdDetailActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Submit Report");
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(TASK, "submitReport");
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
                                        pDialogVisit = new SweetAlertDialog(RentItAdDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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
                                    Toast.makeText(RentItAdDetailActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onResponseError() {

                                SmartUtils.hideProgressDialog();
                            }
                        });


                        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                        } else {
                            edtReport.setError("Please fill report");
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getAllComments(String PROD_ID) {
        pDialog = new SweetAlertDialog(RentItAdDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Fetching Comments...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItAdDetailActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fetch Comments");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "fetchComments");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("product_id", PROD_ID);

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


    private class RecyclerViewCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_gallery_images_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;


            if (elephantList.get(position).contains("http")) {
                //aQuery.id(holder.ivImages).image(elephantList.get(position), true, true);
                Picasso.with(RentItAdDetailActivity.this).load(elephantList.get(position)).placeholder(R.drawable.no_image).into(holder.ivImages);
            } else {
                // aQuery.id(holder.ivImages).image("http://" + elephantList.get(position), true, true);
                Picasso.with(RentItAdDetailActivity.this).load("http://" + elephantList.get(position)).placeholder(R.drawable.no_image).into(holder.ivImages);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (elephantList.get(position).contains("http")) {
                        //        aQuery.id(imageCat).image(elephantList.get(position), true, true);
                        Picasso.with(RentItAdDetailActivity.this).load(elephantList.get(position)).placeholder(R.drawable.no_image).into(imageCat);
                    } else {
                        //      aQuery.id(imageCat).image("http://" + elephantList.get(position), true, true);
                        Picasso.with(RentItAdDetailActivity.this).load("http://" + elephantList.get(position)).placeholder(R.drawable.no_image).into(imageCat);
                    }
                    POS = position;
                }
            });

        }

        @Override
        public int getItemCount() {
            return elephantList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImages;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImages = (ImageView) itemView.findViewById(R.id.ivImages);
            }
        }
    }
}
