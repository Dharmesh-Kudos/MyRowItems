package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;

public class ReqProdsDetailActivity extends AppCompatActivity {

    CircleImageView imgProPic;
    TextView txtByUser, txtTitle, txtDesc, txtFrom, txtTo, txtDays, txtByPhone;
    ContentValues ROW;
    Toolbar toolbar;
    Button btnComment, btnReport;
    SmartCaching smartCaching;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_prods_detail);
        aQuery = new AQuery(this);
        smartCaching = new SmartCaching(this);
        ROW = getIntent().getParcelableExtra("ROW");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Requested Ad Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });

        btnComment = (Button) findViewById(R.id.btnComment);
        btnReport = (Button) findViewById(R.id.btnReport);
        imgProPic = (CircleImageView) findViewById(R.id.imgProfilePicture);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtByUser = (TextView) findViewById(R.id.txtByUser);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        txtDays = (TextView) findViewById(R.id.txtDays);
        txtByPhone = (TextView) findViewById(R.id.txtByPhone);

        txtTitle.setText(ROW.getAsString("title"));
        txtDesc.setText(ROW.getAsString("description"));
        txtFrom.setText("From " + getString(R.string.rs) + " " + ROW.getAsString("budget_from"));
        txtTo.setText("To " + getString(R.string.rs) + " " + ROW.getAsString("budget_to"));
        txtDays.setText("(For " + ROW.getAsString("days") + " days)");
        try {
            JSONObject userData = new JSONObject(ROW.getAsString("userData"));
            txtByUser.setText(userData.getString("user_name"));
            txtByPhone.setText(userData.getString("user_phone"));
            if (userData.getString("user_pic").equals("")) {
                imgProPic.setImageResource(R.drawable.man);
            } else {
                aQuery.id(imgProPic).image(userData.getString("user_pic"), true, true);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


}
