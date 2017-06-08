package com.rdb.haxd.Presenter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rdb.haxd.R;

public class Hacking extends AppCompatActivity {

    private TextView playerTV;
    private ProgressBar progressBar;
    private Button button1, button2, button3;

    private ProgressDialog progressDialog;

    private String subtopic;
    private String companionNickname;
    private boolean isOwner;
    private boolean acceptationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacking);

        Intent currentIntent = getIntent();
        isOwner = currentIntent.getBooleanExtra( "owner", false );
        subtopic = currentIntent.getStringExtra( "subtopic" );
        companionNickname = isOwner ? subtopic.split( "_" )[ 2 ] : subtopic.split( "_" )[ 0 ];

        wireWidgets();
    }

    private void wireWidgets() {
        playerTV = (TextView) findViewById(R.id.hacking_textView);
        playerTV.setText(companionNickname);

        progressBar = new ProgressBar(this);

        progressDialog = new ProgressDialog(this);

        button1 = (Button) findViewById(R.id.hacking_button);
        button2 = (Button) findViewById(R.id.hacking_button2);
        button3 = (Button) findViewById(R.id.hacking_button3);

    }
}
