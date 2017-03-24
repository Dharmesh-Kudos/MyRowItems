package com.example.tasol.myrowitems;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class RentItUserProfileActivity extends AppCompatActivity {

    CircleImageView imgProPic;
    int radiusArr[];
    Toolbar toolbarData;
    ContentValues ROW;
    String USERID = "";
    SmartCaching smartCaching;
    AQuery aQuery;
    TextView txtName, txtLoc, txtEmail, txtMob;
    ArrayList<ContentValues> allData = new ArrayList<>();
    JSONObject usersData = null;
    private ImageView imageview;
    private JSONObject userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_user_profile);
        smartCaching = new SmartCaching(RentItUserProfileActivity.this);
        aQuery = new AQuery(RentItUserProfileActivity.this);

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
        ROW = getIntent().getParcelableExtra("ROW");
        try {
            userData = new JSONObject(ROW.getAsString("userData"));
            USERID = userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getUserDetail(USERID);

        imageview = (ImageView) findViewById(R.id.img);
        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 15);//second parametre is radius
        imageview.setImageBitmap(blurred);
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
                        allData = smartCaching.parseResponse(response.getJSONArray("allData"), "ALLDATA", null).get("ALLDATA");

                        usersData = new JSONObject(String.valueOf(response.getJSONObject("userData")));

                        if (allData != null && allData.size() > 0) {
                            makePage(allData, usersData);
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

    private void makePage(ArrayList<ContentValues> allData, JSONObject usersData) {

        try {
            txtName.setText(usersData.getString("user_name"));
            txtLoc.setText(usersData.getString("user_city"));
            txtEmail.setText(usersData.getString("user_email"));
            txtMob.setText(usersData.getString("user_phone"));
            aQuery.id(imgProPic).image(usersData.getString("user_pic"), true, true);
            aQuery.id(imageview).image(usersData.getString("user_pic"), true, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(RentItUserProfileActivity.this);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }


}
