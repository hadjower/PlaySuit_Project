package com.shawasama.playsuit.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.adapter.TabsFragmentAdapter;
import com.shawasama.playsuit.asynctask.AsyncLoadAllAlbumsTask;
import com.shawasama.playsuit.asynctask.AsyncLoadAllSongsTask;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.music_playback.MusicService;
import com.shawasama.playsuit.song_control_panel_fragment.SongControlPanelFragment;
import com.shawasama.playsuit.songs_fragment.SongsManager;
import com.shawasama.playsuit.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.shawasama.playsuit.util.Constants.REQUEST_PERMISSION;


public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;
    private DrawerLayout drawerLayout;
    private static ViewPager viewPager;
    private NavigationView navigationView;
    private SongControlPanelFragment panelFragment;

    private Map<Integer, AsyncTask> asyncTaskHashMap;
    private TabsFragmentAdapter adapter;

    private Intent songIntent;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    //connect to the service
    private ServiceConnection musicConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultDark);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initNavigationView();
        initTabs();
        //TODO Пофиксить укорачивание текста и тест вью на панели трека
        initSongPanel();

        //upload song list
        checkPermissionAndLoadMediaContent();

        musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("MUSIC", "Connecting service");
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setActivity(MainActivity.this);
                musicSrv.setListAndPrepareSong(SongsManager.getInstance().getSongList());
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };

//        setMusicController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MUSIC", "Entered onStart");

        if (playIntent == null && !musicBound) {
            Log.i("MUSIC", "Entered isIntentNull");
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getApplicationContext().startService(playIntent);
        }

        if (songIntent == null) {
            songIntent = new Intent(MainActivity.this, ManageSongActivity.class);
        }
    }

    private void initSongPanel() {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.song_cp_fragment);
        if (currentFragment instanceof SongControlPanelFragment) {
            panelFragment = (SongControlPanelFragment) currentFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(MainActivity.this, "Search item", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(MainActivity.this, "Setting item", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(navigationView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                executeLoadAsyncTasks();
            } else {
                // User refused to grant permission.
                showWarningDialog();
            }
        }
    }

    private void executeLoadAsyncTasks() {
        Set<Integer> keySet = asyncTaskHashMap.keySet();
        for (Integer key : keySet) {
            switch (key) {
                case Constants.ASYNC_SONGS:
                    ((AsyncLoadAllSongsTask) asyncTaskHashMap.get(key)).execute();
                    break;
                case Constants.ASYNC_ALBUMS:
                    ((AsyncLoadAllAlbumsTask) asyncTaskHashMap.get(key)).execute();
                    break;
            }
        }
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permission not granted!!!")
                .setMessage("Permission to read external storage hasn't been granted." +
                        " Program is unable to load songs from device.")
                .setCancelable(true)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermissionAndLoadMediaContent();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        System.exit(0);
                    }
                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (viewPager.getCurrentItem()) {
            case Constants.TAB_ALBUMS:
            case Constants.TAB_ARTIST:
                switch (newConfig.orientation) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        ((GridLayoutManager) ((AbstractTabFragment) adapter.getItem(viewPager.getCurrentItem())).getRecyclerManager()).setSpanCount(3);
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:
                        ((GridLayoutManager) ((AbstractTabFragment) adapter.getItem(viewPager.getCurrentItem())).getRecyclerManager()).setSpanCount(2);
                        break;
                }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setCheckedItem(R.id.albumsItem);

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
        adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                setSelectedItemOnNavigationDrawer(position);
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedItemOnNavigationDrawer(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void checkPermissionAndLoadMediaContent() {
        asyncTaskHashMap = new HashMap<>();
        asyncTaskHashMap.put(Constants.ASYNC_SONGS, new AsyncLoadAllSongsTask(this));
        asyncTaskHashMap.put(Constants.ASYNC_ALBUMS, new AsyncLoadAllAlbumsTask(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else
            executeLoadAsyncTasks();
    }

    public static void showTab(int tab_number) {
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

    public void openManageActivity(View view) {
        ManageSongActivity.updateActivity(this);
        startActivity(songIntent);
    }

    public AsyncTask getAsyncTask(int key) {
        return asyncTaskHashMap.get(key);
    }

    public MusicService getMusicSrv() {
        return musicSrv;
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        if (musicBound) {
            unbindService(musicConnection);
        }
        musicSrv = null;
        panelFragment.stopHandler();
        super.onDestroy();
    }

    public SongControlPanelFragment getPanelFragment() {
        return panelFragment;
    }

    public void playSong(List<Song> songs, int songPos) {
        getPanelFragment().setSongOnPanel(songs.get(songPos), true);
        musicSrv.playSong(songs, songPos);
        Log.i("MUSIC", "Activity: index[" + songPos + "] + song[" + songs.get(songPos).getTitle() + "]");
    }

    public boolean isMusicBound() {
        return musicBound;
    }
}
