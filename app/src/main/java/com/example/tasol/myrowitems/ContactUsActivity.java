package com.example.tasol.myrowitems;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ContactUsActivity extends AppCompatActivity {

    StringBuffer messagestr = new StringBuffer();
    Toolbar toolbar;
    EditText edtName, edtEmail, edtPhone, edtMsg;
    Button btnSend;
    private SweetAlertDialog pDialogVisit;
    private boolean isSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagestr = new StringBuffer();
                messagestr.append("The information is as follows " + "\n" +
                        " Name =  " + edtName.getText().toString() + "\n" +
                        " Email =  " + edtEmail.getText().toString() + "\n" +
                        " Phone =  " + edtPhone.getText().toString() + "\n" +
                        " Message =  " + edtMsg.getText().toString());
                sendEmail(messagestr.toString());


            }
        });


    }

    private void sendEmail(String hello) {
        BackgroundMail.newBuilder(ContactUsActivity.this)
                .withUsername("rentitcontact@gmail.com")
                .withPassword("rentanything")
                .withMailto("rentitcontact@gmail.com")
                .withSubject("New Inquiry Submitted")
                .withBody(hello)
                .withProcessVisibility(true)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic

                        pDialogVisit = new SweetAlertDialog(ContactUsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialogVisit.setTitleText("Rent It !!!");
                        pDialogVisit.setContentText("Thank You For Your Time. We will reply you soon.");
                        pDialogVisit.setConfirmText("Done");
                        pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                edtName.setText("");
                                edtEmail.setText("");
                                edtPhone.setText("");
                                edtMsg.setText("");
                                sweetAlertDialog.dismiss();
                            }
                        });
                        pDialogVisit.setCancelable(true);
                        pDialogVisit.show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {

                        //do some magic
                        //Toast.makeText(CheckoutActivity.this, "Not Sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }
}
