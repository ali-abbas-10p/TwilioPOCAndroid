package com.practice.twiliotest.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.practice.twiliotest.R;
import com.practice.twiliotest.postboy.PostBoy;
import com.practice.twiliotest.postboy.PostBoyException;
import com.practice.twiliotest.postboy.PostBoyListener;
import com.practice.twiliotest.postboy.RequestType;
import com.practice.twiliotest.web.WebConstants;
import com.practice.twiliotest.web.parsers.Parser;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setTitle("Rooms List");

        this.findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(MainActivity.this);
                et.setHint("Enter room name");
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setTitle("Create Room")
                        .setView(et)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (et.getText().toString().trim().isEmpty())
                                {
                                    Toast.makeText(MainActivity.this, "Group name can't be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                PostBoy pb = new PostBoy.Builder(MainActivity.this, RequestType.POST_X_WWW_FORM_URLENCODED, WebConstants.BASE_URL+WebConstants.METHOD_ROOMS).create();
                                pb.addPOSTValue("uniqueName",et.getText().toString().trim());
                                pb.setListener(new PostBoyListener() {
                                    private ProgressDialog pd;
                                    @Override
                                    public void onPostBoyConnecting() throws PostBoyException {
                                        pd = new ProgressDialog(MainActivity.this);
                                        pd.setCancelable(false);
                                        pd.setCanceledOnTouchOutside(false);
                                        pd.setMessage("Creating Room");
                                        pd.show();
                                    }

                                    @Override
                                    public void onPostBoyAsyncConnected(String json, int responseCode) throws PostBoyException {
                                    }

                                    @Override
                                    public void onPostBoyConnected(String json, int responseCode) throws PostBoyException {
                                        if (pd!=null)
                                            pd.dismiss();
                                        Parser parser = Parser.create(json);
                                        if (parser.getMetaData().getStatusCode()==200) {
                                            Toast.makeText(MainActivity.this, "Room created successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(MainActivity.this, parser.getMetaData().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPostBoyConnectionFailure() throws PostBoyException {
                                        if (pd!=null)
                                            pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Internet connection fail", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPostBoyError(PostBoyException e) {
                                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                pb.call();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

            }
        });
    }
}
