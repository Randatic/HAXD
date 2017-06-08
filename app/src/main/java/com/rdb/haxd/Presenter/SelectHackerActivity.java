package com.rdb.haxd.Presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.backendless.Backendless;
import com.rdb.haxd.R;

public class SelectHackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hacker);

        Backendless.initApp(this, "73B8E514-FB28-B17E-FF84-3BF6B88BD000", "929DA8BC-4FDE-CFAF-FF66-0B4B156DCB00", "v1");

        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentByTag("SuperheroFragment")== null)
            fm.beginTransaction()
                    .add(R.id.hacker_list_container, new HackerFragment(), "HackerFragment")
                    .commit();
    }
}
