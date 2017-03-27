package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItCatItemsActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {


    RecyclerView rvCatDetail;
    Toolbar toolbar;
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};

    int IN_POS;
    TextView txtNotYet;
    Button btnByCity;
    LinearLayoutManager linearLayoutManagerHori;
    LinearLayout layoutCity;
    ContextMenuDialogFragment mMenuDialogFragment;
    AQuery aQuery;
    LinearLayout liloSearch;
    EditText edtSearch;
    ImageView ivSearch;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewImagesAdapter recyclerViewImagesAdapter;
    private ArrayList<ContentValues> categoryData = new ArrayList<>();
    private smart.caching.SmartCaching smartCaching;
    private JSONObject loginParams = null;
    private ArrayList<String> subCityData;
    private CustomCityAdapter customSubCatAdapter;
    private ArrayList<ContentValues> cvSubCatData;
    private DialogPlus dialogPlusSubCat;
    private FragmentManager fragmentManager;
    private String CITYNAME;
    private RecyclerViewFilterAdapter recyclerViewFilterAdapter;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentit_cat_detail);
        fragmentManager = getSupportFragmentManager();
        // setupWindowAnimations();
        txtNotYet = (TextView) findViewById(R.id.txtNotYet);
        smartCaching = new SmartCaching(RentItCatItemsActivity.this);
        aQuery = new AQuery(RentItCatItemsActivity.this);
        IN_POS = getIntent().getIntExtra("IN_POS", 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initMenuFragment();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);

            }
        });
        liloSearch = (LinearLayout) findViewById(R.id.liloSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearchName);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        rvCatDetail = (RecyclerView) findViewById(R.id.rvCatDetail);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCatDetail.setHasFixedSize(true);
        rvCatDetail.setLayoutManager(linearLayoutManager);
        rvCatDetail.setNestedScrollingEnabled(false);
        getCategoryList(false);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtSearch.getText().length() > 0) {
                    pDialog = new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                    pDialog.setTitleText("Searching...");
                    pDialog.setCancelable(true);
                    pDialog.show();
                    filterByPriceAndRecent("searchByName");

                } else {
                    Toast.makeText(RentItCatItemsActivity.this, "Please enter something", Toast.LENGTH_SHORT).show();
                }
            }
        });


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (edtSearch.getText().length() > 0) {
                        pDialog = new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                        pDialog.setTitleText("Searching...");
                        pDialog.setCancelable(true);
                        pDialog.show();
                        filterByPriceAndRecent("searchByName");
                        return true;
                    } else {
                        Toast.makeText(RentItCatItemsActivity.this, "Please enter something", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();


        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject searchBy = new MenuObject("Search by name");
        searchBy.setResource(R.drawable.wish_search);

        MenuObject send = new MenuObject("By City");
        send.setResource(R.drawable.ic_action_skyline);

        MenuObject like = new MenuObject("Price: Low to High");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_upload);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Price: High to Low");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_download));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Most Recent");
        addFav.setResource(R.drawable.ic_action_calendar);

//        MenuObject block = new MenuObject("Block user");
//        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(searchBy);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        //menuObjects.add(block);
        return menuObjects;
    }

    private void getCategoryList(final boolean isShow) {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItCatItemsActivity.this);
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
                if (isShow) {
                    pDialog.dismiss();
                }
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
                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RentItCatItemsActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMenuItemClick(View clickedView, int position) {

        switch (position) {
            case 1:
                //Search by name
//                Animation slide_down= AnimationUtils.loadAnimation(RentItCatItemsActivity.this,R.anim.slide_down);
//
//                liloSearch.startAnimation(slide_down);
                liloSearch.setVisibility(View.VISIBLE);
                break;
            case 2:
                //CIty Call
                fetchCity();
                break;
            case 3:
                //Low to high
                filterByPriceAndRecent("lowToHigh");
                break;
            case 4:
                //High to low
                filterByPriceAndRecent("highToLow");
                break;
            case 5:
                //Most Recent
                filterByPriceAndRecent("mostRecent");
                break;
        }

    }

    private void filterByPriceAndRecent(final String priceSortType) {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItCatItemsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "filterData");
            JSONObject taskData = new JSONObject();
            if (priceSortType.equals("searchByName")) {
                taskData.put("type", priceSortType);//searchByName
                taskData.put("name", edtSearch.getText().toString());
            } else {
                taskData.put("type", priceSortType);//LowToHigh,HighToLow,MostRecent
            }

            taskData.put("catid", IN_POS);

            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (priceSortType.equals("searchByName")) {
                    pDialog.dismiss();
                }
                Log.d("RESULT = ", String.valueOf(response));
                try {
                    if (responseCode == 200) {
                        txtNotYet.setVisibility(View.GONE);
                        rvCatDetail.setVisibility(View.VISIBLE);
                        cvSubCatData = smartCaching.parseResponse(response.getJSONArray("filterData"), "FILTERDATA", "userData").get("FILTERDATA");
                        recyclerViewFilterAdapter = new RecyclerViewFilterAdapter();
                        rvCatDetail.setAdapter(recyclerViewFilterAdapter);
                    } else if (responseCode == 204) {
                        recyclerViewImagesAdapter.notifyDataSetChanged();
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvCatDetail.setVisibility(View.GONE);
//                        Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(RentItCatItemsActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }

    private void fetchCity() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItCatItemsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "fetchCity");
            JSONObject taskData = new JSONObject();
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                Log.d("RESULT = ", String.valueOf(response));
                try {
                    subCityData = new ArrayList<String>();
                    if (responseCode == 200) {
                        cvSubCatData = smartCaching.parseResponse(response.getJSONArray("cityData"), "CITYDATA", null).get("CITYDATA");
                        //subCityData.add("Choose Sub Category");
                        for (int i = 0; i < cvSubCatData.size(); i++) {
                            subCityData.add(cvSubCatData.get(i).getAsString("name"));
                        }
                        customSubCatAdapter = new CustomCityAdapter(RentItCatItemsActivity.this, subCityData);
                        dialogPlusSubCat = DialogPlus.newDialog(RentItCatItemsActivity.this)
                                .setAdapter(customSubCatAdapter)
                                .setCancelable(true)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        CITYNAME = cvSubCatData.get(position).getAsString("name");
                                        filterByCity(CITYNAME);
                                        dialog.dismiss();
                                    }
                                })
                                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        dialogPlusSubCat.show();
                    } else if (responseCode == 204) {
                        Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(RentItCatItemsActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }

    private void filterByCity(String cityname) {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, RentItCatItemsActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "filterData");
            JSONObject taskData = new JSONObject();
            taskData.put("type", "city");
            taskData.put("city_name", cityname);
            taskData.put("catid", IN_POS);
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                Log.d("RESULT = ", String.valueOf(response));
                try {
                    if (responseCode == 200) {
                        txtNotYet.setVisibility(View.GONE);
                        rvCatDetail.setVisibility(View.VISIBLE);
                        cvSubCatData = smartCaching.parseResponse(response.getJSONArray("filterData"), "FILTERDATA", "userData").get("FILTERDATA");
                        recyclerViewFilterAdapter = new RecyclerViewFilterAdapter();
                        rvCatDetail.setAdapter(recyclerViewFilterAdapter);
                    } else if (responseCode == 204) {
                        recyclerViewImagesAdapter.notifyDataSetChanged();
                        txtNotYet.setVisibility(View.VISIBLE);
                        rvCatDetail.setVisibility(View.GONE);
//                        Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(RentItCatItemsActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }


    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:

                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
//                mMenuDialogFragment.setShowsDialog(true);
                break;

            case R.id.rotate_menu:
                pDialog = new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#009688"));
                pDialog.setTitleText("Back to normal...");
                pDialog.setCancelable(true);
                pDialog.show();
                getCategoryList(true);
                liloSearch.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
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
            holder.txtPrice.setText(getString(R.string.rs) + row.getAsString("price"));

            List<String> elephantList = Arrays.asList(row.getAsString("photo").split(","));

            if (elephantList.get(0).contains("http")) {
                Picasso.with(RentItCatItemsActivity.this).load(elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image(elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            } else {
                Picasso.with(RentItCatItemsActivity.this).load("http://" + elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image("http://" + elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            }

            try {
                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                        .getString(SP_LOGGED_IN_USER_DATA, ""));

                JSONObject userData = new JSONObject(row.getAsString("userData"));
//                if (row.getAsString("user_id").equals(loginParams.getString("id"))) {
//                    holder.txtUsername.setText("Uploaded By YOU");
//                } else {
//                    holder.txtUsername.setText("Uploaded By " + userData.getString("user_name"));
//                }
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
                    Intent intent = new Intent(RentItCatItemsActivity.this, RentItAdDetailActivity.class);
                    intent.putExtra("POS", position);
                    intent.putExtra("ROW", row);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());
                        Pair<View, String> p2 = Pair.create((View) holder.imageCat, holder.imageCat.getTransitionName());

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(RentItCatItemsActivity.this, p1, p2);
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

    private class RecyclerViewFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

            final ContentValues row = cvSubCatData.get(position);

            holder.txtTitle.setText(row.getAsString("title"));
            holder.txtPrice.setText(getString(R.string.rs) + row.getAsString("price"));

            List<String> elephantList = Arrays.asList(row.getAsString("photo").split(","));

            if (elephantList.get(0).contains("http")) {
                Picasso.with(RentItCatItemsActivity.this).load(elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image(elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            } else {
                Picasso.with(RentItCatItemsActivity.this).load("http://" + elephantList.get(0)).placeholder(R.drawable.no_image).into(holder.imageCat);
                //aQuery.id(holder.imageCat).image("http://" + elephantList.get(0), true, true).progress(new SweetAlertDialog(RentItCatItemsActivity.this, SweetAlertDialog.PROGRESS_TYPE));

            }

            try {
                loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                        .getString(SP_LOGGED_IN_USER_DATA, ""));

                JSONObject userData = new JSONObject(row.getAsString("userData"));
//                if (row.getAsString("user_id").equals(loginParams.getString("id"))) {
//                    holder.txtUsername.setText("Uploaded By YOU");
//                } else {
//                    holder.txtUsername.setText("Uploaded By " + userData.getString("user_name"));
//                }
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
                    Intent intent = new Intent(RentItCatItemsActivity.this, RentItAdDetailActivity.class);
                    intent.putExtra("POS", position);
                    intent.putExtra("ROW", row);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());
                        Pair<View, String> p2 = Pair.create((View) holder.imageCat, holder.imageCat.getTransitionName());

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(RentItCatItemsActivity.this, p1, p2);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return cvSubCatData.size();
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

    /***** Adapter class extends with ArrayAdapter ******/
    public class CustomCityAdapter extends BaseAdapter {

        LayoutInflater inflater;
        private ArrayList<String> mCityData;

        /*************  CustomAdapter Constructor *****************/
        public CustomCityAdapter(Context applicationContext, ArrayList<String> mCatData) {

            this.mCityData = mCatData;
            inflater = (LayoutInflater.from(applicationContext));
            /***********  Layout inflator to call external xml layout () **********************/

        }

        @Override
        public int getCount() {
            return mCityData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_rows, null);
            TextView names = (TextView) view.findViewById(R.id.txtItem);
            names.setText(mCityData.get(i));

            return view;
        }

    }
}
