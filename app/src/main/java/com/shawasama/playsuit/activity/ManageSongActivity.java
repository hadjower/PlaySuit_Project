package com.shawasama.playsuit.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.util.BlurBuilder;
import com.shawasama.playsuit.util.Constants;

public class ManageSongActivity extends AppCompatActivity {

    private ImageView backgroundImage;
    private ImageView albumImage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_song_activity);
        initToolbar();
        SeekBar seekBar = (SeekBar) findViewById(R.id.manage_seekbar);
        seekBar.setProgress(80);
        initImageAlbumScale();
        initNavigationView();
        backgroundImage = (ImageView) findViewById(R.id.manage_background_image);
        albumImage = (ImageView) findViewById(R.id.manage_album_image);

        setBlurBackground();

//        initNavigationView();

    }

    private void initImageAlbumScale() {
        float density = getResources().getDisplayMetrics().density;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int height;
        int width = (int)((displaymetrics.widthPixels / density) - (35 * 2));
        height = width;

        ImageView imageView = (ImageView) findViewById(R.id.manage_album_image);
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

    private void setBlurBackground() {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.mipmap.radioactive);
        setAlbumImage(background);
        background = BlurBuilder.fastBlur(background, 1, 25);
        background = BlurBuilder.doBrightness(background, -15);
        background = BlurBuilder.doSat(background, 0);

        backgroundImage.setImageBitmap(background);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_toolbar);
        toolbar.setTitle(R.string.now_playing);
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

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.manage_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.albumsItem:
                        MainActivity.showTab(Constants.TAB_ALBUMS);
                        finish();
                        MainActivity.showTab(Constants.TAB_ALBUMS);
                        break;
                    case R.id.artistItem:
                        MainActivity.showTab(Constants.TAB_ARTIST);
                        finish();
                        MainActivity.showTab(Constants.TAB_ARTIST);
                        break;
                    case R.id.foldersItem:
                        MainActivity.showTab(Constants.TAB_FOLDERS);
                        finish();
                        MainActivity.showTab(Constants.TAB_FOLDERS);
                        break;
                    case R.id.playlistsItem:
                        MainActivity.showTab(Constants.TAB_PLAYLISTS);
                        finish();
                        MainActivity.showTab(Constants.TAB_PLAYLISTS);
                        break;
                    case R.id.songsItem:
                        MainActivity.showTab(Constants.TAB_SONGS);
                        finish();
                        MainActivity.showTab(Constants.TAB_SONGS);
                        break;
                }
                return true;
            }
        });
    }

    private void setAlbumImage(Bitmap image) {
        albumImage.setImageBitmap(image);
    }
}
