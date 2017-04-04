package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

/**
 * Created by tasol on 4/4/17.
 */

public class MyReqAdsFragment extends Fragment {

    RecyclerView rvUserAds;
    AQuery aQuery;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewUserAdsAdapter recyclerViewUserAdsAdapter;
    private smart.caching.SmartCaching smartCaching;
    private TextView txtNotYet;
    private int IN_POS;
    private ArrayList<ContentValues> userAdsData = new ArrayList<>();
    private JSONObject loginParams = null;
    private SweetAlertDialog pDialogVisit;
    private SweetAlertDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_posted_ads, container, false);
        smartCaching = new SmartCaching(getActivity());
        aQuery = new AQuery(getActivity());
        txtNotYet = (TextView) v.findViewById(R.id.txtNotYet);
        rvUserAds = (RecyclerView) v.findViewById(R.id.rvUserAds);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUserAds.setHasFixedSize(true);
        rvUserAds.setLayoutManager(linearLayoutManager);
        getAllUserAds();

        return v;
    }

    private void getAllUserAds() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "openMyReqAds");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "openMyReqAds");
            JSONObject taskData = new JSONObject();
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, ""));
            taskData.put("userid", loginParams.getString("id"));
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
                        rvUserAds.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        userAdsData = smartCaching.parseResponse(response.getJSONArray("reqProdData"), "REQPRODDATA", "userData").get("REQPRODDATA");
                        if (userAdsData != null && userAdsData.size() > 0) {
                            recyclerViewUserAdsAdapter = new RecyclerViewUserAdsAdapter();
                            rvUserAds.setAdapter(recyclerViewUserAdsAdapter);
                        }
                    } else if (responseCode == 204) {
                        rvUserAds.setVisibility(View.GONE);
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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
        SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }

    private void deleteAd(String product_id) {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
        pDialog.setTitleText("Deleting Your Ad...");
        pDialog.setCancelable(true);
        pDialog.show();

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "deleteReqAd");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "deleteReqAd");
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
                        pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
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
                    Toast.makeText(getActivity(), "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });


        SmartWebManager.getInstance(getActivity().getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private class RecyclerViewUserAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
            final ContentValues row = userAdsData.get(position);

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


            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), PostAdActivity.class).putExtra("ROW", row).putExtra("FROM", "PROFILE"));
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pDialogVisit = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                    pDialogVisit.setTitleText("Manage Ads");
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
            return userAdsData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imgProfilePicture;
            TextView txtTitle, txtFrom, txtTo, txtdays;
            Button btnEdit, btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
                btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
                txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                txtFrom = (TextView) itemView.findViewById(R.id.txtFrom);
                txtTo = (TextView) itemView.findViewById(R.id.txtTo);
                txtdays = (TextView) itemView.findViewById(R.id.txtDays);
                imgProfilePicture = (ImageView) itemView.findViewById(R.id.imgProfilePicture);
            }
        }
    }
}
