package com.shawasama.playsuit.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.shawasama.playsuit.R;

public class ManageSongActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_song_activity);
        initToolbar();
        SeekBar seekBar = (SeekBar) findViewById(R.id.manage_seekbar);
        seekBar.setProgress(80);
        initImageAlbumScale();
    }

    private void initImageAlbumScale() {
        float density = getResources().getDisplayMetrics().density;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int height;
        int width = (int)((displaymetrics.widthPixels / density) - (35 * 2));
        height = width;

        ImageView imageView = (ImageView) findViewById(R.id.album_image);
        imageView.getLayoutParams().height = (int) (height * density);
        imageView.getLayoutParams().width = (int) (width * density);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_toolbar);
        toolbar.setTitle("");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.manage_toolbar_menu);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
