package com.myandroid.bigdiga.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myandroid.bigdiga.R;
import com.myandroid.bigdiga.Utility;

public class TestFragment extends Fragment {
    private static final String LOG_TAG = TestFragment.class.getSimpleName();

    private Button buttonOk;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_tab, container, false);
        buttonOk = (Button) view.findViewById(R.id.buttonOk);
        editText = (EditText) view.findViewById(R.id.editText);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Clicked on buttonOk");
                if (Utility.isUrlValid(editText.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.enter_url, Toast.LENGTH_SHORT).show();
                } else {
                    Utility.openApp(getContext(), TestFragment.class.getSimpleName(),
                            editText.getText().toString(), 0);
                }
                editText.setText("");
            }
        });
        return view;
    }
}
