package com.shawasama.playsuit.fragment;

//import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractTabFragment extends Fragment {

    private String title;
    protected Context context;
    protected View view;
    protected RecyclerView.LayoutManager manager;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RecyclerView.LayoutManager getRecyclerManager() {
        return manager;
    }
}
