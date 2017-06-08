package com.rdb.haxd.Presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "RegisterActivity";

    private EditText emailET, usernameET, passwordET, passwordConfirmET;
    private Button registerBtn;
    private ProgressDialog progressDialog;

    private BackendlessUser user;
    private Hacker hacker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Backendless.initApp(this, "73B8E514-FB28-B17E-FF84-3BF6B88BD000", "929DA8BC-4FDE-CFAF-FF66-0B4B156DCB00", "v1");

        wireWidgets();
    }

    private void wireWidgets() {
        emailET = (EditText) findViewById(R.id.register_editText_email);
        usernameET = (EditText) findViewById(R.id.register_editText_username);
        passwordET = (EditText) findViewById(R.id.register_editText_password);
        passwordConfirmET = (EditText) findViewById(R.id.register_editText_passwordConfirm);

        registerBtn = (Button) findViewById(R.id.register_button_register);

        registerBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account. . . Please Wait");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.register_button_register) {
            register();
        }
    }

    private void register() {
        if(emailET.getText().toString().trim() == null ||
                usernameET.getText().toString().trim() == null ||
                passwordET.getText().toString().trim() == null ||
                passwordConfirmET.getText().toString().trim() == null) {
            Toast.makeText(this, "Empty Fields!", Toast.LENGTH_SHORT).show();
        } else if (!passwordET.getText().toString().trim().equals(passwordConfirmET.getText().toString().trim())) {
            Toast.makeText(this, "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            //get info
            String email = emailET.getText().toString().trim();
            String username = usernameET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            //create user
            user = new BackendlessUser();
            user.setEmail(email);
            user.setPassword(password);
            user.setProperty("username", username);
            //create hacker
            hacker = new Hacker();
            hacker.setLevel(0);
            hacker.setUsername(username);
            hacker.setDeviceId(Build.SERIAL);
            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser responseUser) {
                    user = responseUser;
                    hacker.setOwnerId(user.getObjectId());
                    Backendless.Persistence.of(Hacker.class).save(hacker, new AsyncCallback<Hacker>() {
                        @Override
                        public void handleResponse(final Hacker responseHacker) {
                            user.setProperty("hacker", hacker);
                            user.setProperty("hackerId", hacker.getObjectId());
                            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    Hacker.currentUser().set(responseHacker);
                                    SharedPreferences settings = getSharedPreferences( "com.backendless.settings", Context.MODE_PRIVATE );
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString( "id", Hacker.currentUser().getObjectId() );
                                    editor.commit();
                                    Toast.makeText(RegisterActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.d(TAG, "handleFault: "+fault.getMessage()+"\n"+fault.getCode()+"\n"+fault.getDetail());
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.d(TAG, "handleFault: "+fault.getMessage()+"\n"+fault.getCode()+"\n"+fault.getDetail());
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.d(TAG, "handleFault: "+fault.getMessage()+"\n"+fault.getCode()+"\n"+fault.getDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }
}
