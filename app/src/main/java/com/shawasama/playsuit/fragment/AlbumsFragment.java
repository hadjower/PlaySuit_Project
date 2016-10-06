package com.shawasama.playsuit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawasama.playsuit.R;

public class AlbumsFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.fragment_example;

    public static AlbumsFragment getInstance(Context context) {
        Bundle args = new Bundle();
        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_albums));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
