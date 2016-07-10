package com.myandroid.bigdigb.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.myandroid.bigdigb.fragment.AppCloseFragment;
import com.myandroid.bigdigb.R;
import com.myandroid.bigdigb.fragment.ShowPictureFragment;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String EXTRA_NAME = "BigDigA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(EXTRA_NAME, false)) {
                Log.v(LOG_TAG,"App opened from BigDigA");
                Toast.makeText(getApplicationContext(), "From BigDigA", Toast.LENGTH_SHORT).show();
                transaction.add(R.id.container, new ShowPictureFragment());
            } else {
                Log.v(LOG_TAG,"App opened from launcher");
                transaction.add(R.id.container, new AppCloseFragment());
            }
            transaction.commit();
        }
    }
}

