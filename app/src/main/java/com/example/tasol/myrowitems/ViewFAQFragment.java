package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

/**
 * Created by tasol on 5/4/17.
 */

public class ViewFAQFragment extends Fragment {

    RecyclerView rvFAQ;
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
        View v = inflater.inflate(R.layout.fragment_view_faq, container, false);
        smartCaching = new SmartCaching(getActivity());
        aQuery = new AQuery(getActivity());
        txtNotYet = (TextView) v.findViewById(R.id.txtNotYet);
        rvFAQ = (RecyclerView) v.findViewById(R.id.rvFAQ);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFAQ.setHasFixedSize(true);
        rvFAQ.setLayoutManager(linearLayoutManager);
        getAllUserAds();

        return v;
    }

    private void getAllUserAds() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "viewFAQ");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "viewFAQ");
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
                        rvFAQ.setVisibility(View.VISIBLE);
                        Log.d("RESULT = ", String.valueOf(response));
                        userAdsData = smartCaching.parseResponse(response.getJSONArray("faqData"), "faqData", null).get("faqData");
                        if (userAdsData != null && userAdsData.size() > 0) {
                            recyclerViewUserAdsAdapter = new RecyclerViewUserAdsAdapter();
                            rvFAQ.setAdapter(recyclerViewUserAdsAdapter);
                        }
                    } else if (responseCode == 204) {
                        rvFAQ.setVisibility(View.GONE);
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

    private class RecyclerViewUserAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            final ContentValues row = userAdsData.get(position);

            holder.txtQue.setText("Q. " + row.getAsString("que"));
            holder.txtAns.setText("Answer. " + row.getAsString("ans"));


        }

        @Override
        public int getItemCount() {
            return userAdsData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView txtQue, txtAns;

            public ViewHolder(View itemView) {
                super(itemView);

                txtQue = (TextView) itemView.findViewById(R.id.txtQue);
                txtAns = (TextView) itemView.findViewById(R.id.txtAns);

            }
        }
    }
}
