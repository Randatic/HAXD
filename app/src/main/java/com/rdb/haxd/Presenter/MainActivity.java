package com.rdb.haxd.Presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rdb.haxd.Model.Defaults;
import com.rdb.haxd.Model.Hacker;
import com.rdb.haxd.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";

    private TextView usernameTV;
    private Button hackBtn;

    private BackendlessUser user;
    private Hacker hacker;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog =new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        Backendless.setUrl( Defaults.SERVER_URL );
        //Backendless.initApp(this, "73B8E514-FB28-B17E-FF84-3BF6B88BD000", "929DA8BC-4FDE-CFAF-FF66-0B4B156DCB00", "v1");
        user = Backendless.UserService.CurrentUser();
        Log.d(TAG, "onCreate: " + Hacker.currentUser().info());
        Backendless.Messaging.registerDevice("375070444130", "default", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                progressDialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, "Error: Could not register device\n" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "handleFault: "+fault.getMessage()+"\n"+fault.getCode()+"\n"+fault.getDetail());
                progressDialog.show();
                finish();
            }
        });

        SharedPreferences settings = getSharedPreferences( "com.backendless.settings", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString( "id", Hacker.currentUser().getObjectId() );
        editor.commit();

        wireWidgets();

    }

    private void wireWidgets() {

        usernameTV = (TextView) findViewById(R.id.main_textView_username);
        usernameTV.setText(Hacker.currentUser().getUsername());

        hackBtn = (Button) findViewById(R.id.main_button_hack);
        hackBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.main_button_hack) {
            Intent i = new Intent(this, SelectUserActivity.class);
            startActivity(i);
        }
    }
}
