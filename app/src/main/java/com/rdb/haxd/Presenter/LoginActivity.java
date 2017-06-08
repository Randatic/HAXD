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
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rdb.haxd.Model.Hacker;
import com.rdb.haxd.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    private EditText emailET, passwordET;
    private Button loginBtn, registerBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Backendless.initApp(this, "73B8E514-FB28-B17E-FF84-3BF6B88BD000", "929DA8BC-4FDE-CFAF-FF66-0B4B156DCB00", "v1");

        wireWidgets();

    }

    private void wireWidgets() {
        emailET = (EditText) findViewById(R.id.login_editText_email);
        passwordET = (EditText) findViewById(R.id.login_editText_password);

        loginBtn = (Button) findViewById(R.id.login_button_login);
        registerBtn = (Button) findViewById(R.id.login_button_register);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging In. . . Please Wait");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_button_login) {
            login();
        } else if (id == R.id.login_button_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void login() {
        if(emailET.getText().toString().trim() == null || passwordET.getText().toString().trim() == null) {
            Toast.makeText(this, "Empty Fields!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {
                    Backendless.Persistence.of(Hacker.class).findById((String) response.getProperty("hackerId"), new AsyncCallback<Hacker>() {
                        @Override
                        public void handleResponse(Hacker response) {
                            Hacker.currentUser().set(response);
                            SharedPreferences settings = getSharedPreferences( "com.backendless.settings", Context.MODE_PRIVATE );
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString( "id", Hacker.currentUser().getObjectId() );
                            editor.commit();
                            Log.d(TAG, "handleResponse: "+response.info());
                            Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });


                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}
