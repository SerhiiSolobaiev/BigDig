package com.myandroid.bigdiga.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myandroid.bigdiga.AppPreferences;
import com.myandroid.bigdiga.DividerItemDecoration;
import com.myandroid.bigdiga.R;
import com.myandroid.bigdiga.adapter.PictureAdapter;
import com.myandroid.bigdiga.model.Picture;
import com.myandroid.bigdiga.model.Status;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String LOG_TAG = TestFragment.class.getSimpleName();

    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_URL = "url";

    private TextView textViewNoPictures;
    private RecyclerView recyclerView;
    private PictureAdapter adapter;

    private AlertDialog sortMethodDialog;

    final Uri PICTURE_URI = Uri
            .parse("content://com.myandroid.bigdiga.provider.PicturesContentProvider/pictures");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_tab, container, false);
        initViews(view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setRecyclerViewAdapter();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePicturesList();//(
        if (recyclerView.getAdapter().getItemCount() == 0) {
            isTextViewNoPicturesVisible(true);
        } else {
            isTextViewNoPicturesVisible(false);
        }
    }

    private void setRecyclerViewAdapter() {
        adapter = new PictureAdapter(getActivity(), getPicturesFromBD());
        recyclerView.setAdapter(adapter);
    }

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        textViewNoPictures = (TextView) view.findViewById(R.id.textViewNoPictures);
    }

    private void isTextViewNoPicturesVisible(boolean isVisible) {
        textViewNoPictures.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void updatePicturesList() {
        adapter.updateList(getPicturesFromBD());
    }

    private List<Picture> getPicturesFromBD() {
        List<Picture> pictures = new ArrayList<>();
        Cursor c = getActivity().getContentResolver().query(PICTURE_URI, null, null, null,
                AppPreferences.getSortMethod(getActivity()));
        if (c.moveToFirst()) {
            do {
                Picture picture = new Picture();
                picture.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
                picture.setStatus(Status.values()[c.getInt(c.getColumnIndex(COLUMN_STATUS)) - 1]);

                pictures.add(picture);
            } while (c.moveToNext());
        }
        return pictures;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortBy) {
            showSortAlertDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortAlertDialog() {
        final String[] items = {getResources().getString(R.string.sort_by_date),
                getResources().getString(R.string.sort_by_status)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setSingleChoiceItems(items, AppPreferences.getSortPosition(getActivity()),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    AppPreferences.setSortMethod(getActivity(), COLUMN_TIME);
                                }
                                if (item == 1) {
                                    AppPreferences.setSortMethod(getActivity(), COLUMN_STATUS);
                                }
                                AppPreferences.setSortPosition(getActivity(), item);

                                //better to use comparator (not new query to bd)
                                updatePicturesList();
                                sortMethodDialog.dismiss();
                            }
                        });
        sortMethodDialog = builder.create();
        sortMethodDialog.show();
    }
}
