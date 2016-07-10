package com.myandroid.bigdigb.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.myandroid.bigdigb.R;
import com.myandroid.bigdigb.activity.MainActivity;


public class AppCloseFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView textViewNotFromA;
    private Handler handler;
    private Runnable updateCurrentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_will_close, container, false);
        textViewNotFromA = (TextView) view.findViewById(R.id.textViewNotFromBigDigA);
        showCloseView();
        return view;
    }

    private void showCloseView() {
        Toast.makeText(getContext(), R.string.not_from_bigdig_a, Toast.LENGTH_LONG).show();
        final String timer1 = getResources().getString(R.string.close_in);

        handler = new Handler();
        updateCurrentTime = new Runnable() {
            int i = 10; //10 second
            @Override
            public void run() {
                textViewNotFromA.setText(String.format(timer1, i));
                handler.postDelayed(this, 1000);
                if (i == 0)
                    getActivity().finish();
                i--;
            }
        };
        handler.postDelayed(updateCurrentTime, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(updateCurrentTime);
    }
}
