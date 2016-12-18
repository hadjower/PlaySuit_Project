package com.shawasama.playsuit.artists_fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.media_class.Artist;

import java.util.List;

public class ArtistFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.recyclerview_layout;
    private RecyclerView recyclerView;

    public static ArtistFragment getInstance(Context context) {
        Bundle args = new Bundle();
        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_artist));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getActivity().getApplicationContext();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager.setSpanCount(3);
        }
        super.manager = manager;
        recyclerView.setLayoutManager(manager);
        try {
//            AsyncLoadAllArtistsTask asyncTask = (AsyncLoadAllArtistsTask) ((MainActivity) getActivity()).getAsyncTask(Constants.ASYNC_ARTISTS);
//            ArtistAdapter adapter = new ArtistAdapter(asyncTask.get(), this);
            //todo check
            List<Artist> artists = ArtistsManager.getInstance().loadArtists(context);
            if (artists == null)
                throw new Exception();
            ArtistAdapter adapter = new ArtistAdapter(artists, this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
