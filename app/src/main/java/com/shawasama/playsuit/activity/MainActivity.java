package com.shawasama.playsuit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shawasama.playsuit.Constants;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.adapter.TabsFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int LAYOUT = R.layout.activity_main;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NavigationView navigationView;

    private ImageButton playPauseButton;
    private boolean isPlay = false;

    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultDark);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initToolbar();
        initNavigationView();
        initTabs();
        initButtons();
        ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(40);

        menuButton = (ImageButton) findViewById(R.id.menu);
    }

    private void initButtons() {
        playPauseButton = (ImageButton) findViewById(R.id.play_pause_track);
        playPauseButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_play_arrow_white_36dp));
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlay){
                    playPauseButton.setImageDrawable(
                            ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_play_arrow_white_36dp));
                }else{
                    playPauseButton.setImageDrawable(
                            ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_pause_white_36dp));
                }
                isPlay = !isPlay; // reverse
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setCheckedItem(R.id.artistItem);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.albumsItem:
                        showTab(Constants.TAB_ALBUMS);
                        break;
                    case R.id.artistItem:
                        showTab(Constants.TAB_ARTIST);
                        break;
                    case R.id.foldersItem:
                        showTab(Constants.TAB_FOLDERS);
                        break;
                    case R.id.playlistsItem:
                        showTab(Constants.TAB_PLAYLISTS);
                        break;
                    case R.id.songsItem:
                        showTab(Constants.TAB_SONGS);
                        break;
                }
                return true;
            }
        });
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setSelectedItemOnNavigationDrawer(position);
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedItemOnNavigationDrawer(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void showTab(int tab_number) {
        viewPager.setCurrentItem(tab_number);
    }

    private void setSelectedItemOnNavigationDrawer(int position) {
        switch (position) {
            case Constants.TAB_ALBUMS:
                navigationView.setCheckedItem(R.id.albumsItem);
                break;
            case Constants.TAB_ARTIST:
                navigationView.setCheckedItem(R.id.artistItem);
                break;
            case Constants.TAB_FOLDERS:
                navigationView.setCheckedItem(R.id.foldersItem);
                break;
            case Constants.TAB_PLAYLISTS:
                navigationView.setCheckedItem(R.id.playlistsItem);
                break;
            case Constants.TAB_SONGS:
                navigationView.setCheckedItem(R.id.songsItem);
                break;
        }
    }

    public void checkButtons(View view) {
        Toast.makeText(MainActivity.this, "Button is pressed", Toast.LENGTH_SHORT).show();
    }
}
