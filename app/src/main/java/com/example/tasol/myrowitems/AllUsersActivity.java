package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView rvAllUsers;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        smartCaching = new SmartCaching(AllUsersActivity.this);
        aQuery = new AQuery(AllUsersActivity.this);
        txtNotYet = (KudosTextView) findViewById(R.id.txtNotYet);
        rvAllUsers = (RecyclerView) findViewById(R.id.rvAllUsers);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAllUsers.setHasFixedSize(true);
        rvAllUsers.setLayoutManager(linearLayoutManager);

        // IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Users");
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AllUsersActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "OpenUserAds");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "adminGetAllUsers");
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
                        rvAllUsers.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        allUsersData = smartCaching.parseResponse(response.getJSONArray("userData"), "USERDATA", null).get("USERDATA");
                        if (allUsersData != null && allUsersData.size() > 0) {
                            recyclerViewAllUserAdapter = new RecyclerViewAllUserAdapter();
                            rvAllUsers.setAdapter(recyclerViewAllUserAdapter);
                        }
                    } else if (responseCode == 204) {
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvAllUsers.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AllUsersActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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

    private void blockUnblockUser(String userid, String isBlocked) {
        pDialog = new SweetAlertDialog(AllUsersActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Changing User Status...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, AllUsersActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "blockUnblockUser");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "blockUnblockUser");
            JSONObject taskData = new JSONObject();
            try {

                taskData.put("id", userid);
                taskData.put("isBlocked", isBlocked);

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
                        pDialogVisit = new SweetAlertDialog(AllUsersActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialogVisit.setTitleText("KUDOS");
                        pDialogVisit.setContentText("User Blocked/Unblocked!");

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
                    Toast.makeText(AllUsersActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private class RecyclerViewAllUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_all_users_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            final ContentValues row = allUsersData.get(position);

            holder.txtName.setText(row.getAsString("name"));

            holder.txtEmail.setText(row.getAsString("email"));

            holder.txtCity.setText(row.getAsString("city"));

            holder.txtPhone.setText(row.getAsString("phone"));
//
//            List<String> elephantList = Arrays.asList(row.getAsString("photo").split(","));
//
            if (row.getAsString("user_pic").contains("http")) {
                Picasso.with(AllUsersActivity.this).load(row.getAsString("user_pic")).placeholder(R.drawable.man).into(holder.imgProPic);
            } else {
                Picasso.with(AllUsersActivity.this).load("http://" + row.getAsString("user_pic")).placeholder(R.drawable.man).into(holder.imgProPic);

            }
//
            if (row.getAsString("varified").equals("1")) {
                holder.txtVerify.setText("User Verified");
                holder.txtVerify.setBackgroundDrawable(getResources().getDrawable(R.drawable.comment_back_design));
//                holder.txtVerify.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            } else {
                holder.txtVerify.setText("User Not Verfied");
                holder.txtVerify.setBackgroundDrawable(getResources().getDrawable(R.drawable.comment_back_design_maroon));
//                holder.txtVerify.setBackgroundColor(getResources().getColor(R.color.maroon));//
            }
//
            if (row.getAsString("is_blocked").equals("1")) {
                holder.btnBlock.setText("Unblock User");
                holder.btnBlock.setTextColor(getResources().getColor(R.color.maroon));
            } else {
                holder.btnBlock.setText("Block User");
                holder.btnBlock.setTextColor(getResources().getColor(R.color.colorGreen));
            }
            holder.btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (row.getAsString("is_blocked").equals("1")) {
                        blockUnblockUser(row.getAsString("id"), "0");
                    } else {
                        blockUnblockUser(row.getAsString("id"), "1");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return allUsersData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CircleImageView imgProPic;
            KudosTextView txtVerify;
            KudosTextView txtName, txtEmail, txtPhone, txtCity;
            KudosTextView btnBlock;

            public ViewHolder(View itemView) {
                super(itemView);
                txtVerify = (KudosTextView) itemView.findViewById(R.id.txtVerify);
                btnBlock = (KudosTextView) itemView.findViewById(R.id.btnBlockUser);
                txtName = (KudosTextView) itemView.findViewById(R.id.txtName);
                txtEmail = (KudosTextView) itemView.findViewById(R.id.txtEmail);
                txtPhone = (KudosTextView) itemView.findViewById(R.id.txtPhone);
                txtCity = (KudosTextView) itemView.findViewById(R.id.txtCity);
                imgProPic = (CircleImageView) itemView.findViewById(R.id.imgProfilePicture);
            }
        }
    }
}
