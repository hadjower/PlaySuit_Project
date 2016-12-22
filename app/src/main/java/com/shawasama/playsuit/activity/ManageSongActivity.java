package com.shawasama.playsuit.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.albums_fragment.AlbumsManager;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.music_playback.MusicService;
import com.shawasama.playsuit.util.BlurBuilder;
import com.shawasama.playsuit.util.Constants;
import com.shawasama.playsuit.util.Util;

import java.lang.ref.WeakReference;

public class ManageSongActivity extends AppCompatActivity {
    private static WeakReference<MainActivity> mainActivityRef;

    private ViewHolder holder;
    private MusicService musicSrv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_song_activity);
        holder = new ViewHolder(this);
        musicSrv = mainActivityRef.get().getMusicSrv();

        setSongCharacteristics();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private void setSongCharacteristics() {
        Song currSong = musicSrv.getCurrSong();

        holder.title.setText(currSong.getTitle());
        holder.artist.setText(currSong.getArtist());
        holder.duration.setText(Util.getTimeInText(currSong.getDuration()));

        holder.setButtons();

        String albumArtPath = AlbumsManager.getInstance().getAlbumArtPathForSong(currSong);
        Glide.with(this)
                .load(albumArtPath)
                .placeholder(R.mipmap.img_album)
                .centerCrop()
                .into(holder.albumImageView);

        if (albumArtPath != null) {
            Glide.with(this)
                    .load(albumArtPath)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.backgroundImageView.setImageBitmap(getBlurBackground(resource));
                        }
                    });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.img_album);
            holder.backgroundImageView.setImageBitmap(getBlurBackground(bitmap));
        }

        if (mHandler == null) {
            mHandler = new Handler();
            updateProgressBar();
        }
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = musicSrv.getDur();
            long currentDuration = musicSrv.getCurrentPosition();

            // Updating progress bar
            int progress = (int)(Util.getProgressPercentage(currentDuration, totalDuration));
            holder.seekBar.setProgress(0);
            holder.seekBar.setMax(100);
            holder.seekBar.setProgress(progress);

            holder.currTime.setText(Util.getTimeInText(currentDuration));

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 500);
        }
    };

    public static void updateActivity(MainActivity mainActivity) {
        mainActivityRef = new WeakReference<>(mainActivity);
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

    private Bitmap getBlurBackground(Bitmap background) {
        background = BlurBuilder.fastBlur(background, 1, 25);
        background = BlurBuilder.doBrightness(background, -15);
//        background = BlurBuilder.doSat(background, 0);
        return background;
    }

    /**
     * The ViewHolder class initializes, stores and manages ManageSong activity's child's views
     * **/

    static class ViewHolder {
        ManageSongActivity activity;
        DrawerLayout drawerLayout;
        NavigationView navigationView;
        Toolbar toolbar;

        TextView title;
        TextView artist;
        TextView currTime;
        TextView duration;

        ImageView backgroundImageView;
        ImageView albumImageView;
        ImageButton shuffle;
        ImageButton prev;
        ImageButton playPause;
        ImageButton next;
        ImageButton repeat;
        SeekBar seekBar;

        public ViewHolder(final ManageSongActivity activity) {
            this.activity = activity;
            drawerLayout = (DrawerLayout) activity.findViewById(R.id.manage_drawer_layout);
            navigationView = (NavigationView) activity.findViewById(R.id.navigation);
            toolbar = (Toolbar) activity.findViewById(R.id.manage_toolbar);

            title = (TextView) activity.findViewById(R.id.manage_title);
            artist = (TextView) activity.findViewById(R.id.manage_subtitle);
            currTime = (TextView) activity.findViewById(R.id.manage_current_time);
            duration = (TextView) activity.findViewById(R.id.manage_duration);

            backgroundImageView = (ImageView) activity.findViewById(R.id.manage_background_image);
            albumImageView = (ImageView) activity.findViewById(R.id.manage_album_image);
            shuffle = (ImageButton) activity.findViewById(R.id.manage_shuffle);
            prev = (ImageButton) activity.findViewById(R.id.manage_prev_btn);
            playPause = (ImageButton) activity.findViewById(R.id.manage_pause_btn);
            next = (ImageButton) activity.findViewById(R.id.manage_next_btn);
            repeat = (ImageButton) activity.findViewById(R.id.manage_repeat_btn);
            seekBar = (SeekBar) activity.findViewById(R.id.manage_seekbar);

            initListeners(activity);
            initToolbar();
            initImageAlbumScale();
        }

        private void initListeners(final ManageSongActivity activity) {
            //NavigationView listeners
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    drawerLayout.closeDrawers();
                    int key = -1;
                    switch (item.getItemId()) {
                        case R.id.albumsItem:
                            key = Constants.TAB_ALBUMS;
                            break;
                        case R.id.artistItem:
                            key = Constants.TAB_ARTIST;
                            break;
                        case R.id.foldersItem:
                            key = Constants.TAB_FOLDERS;
                            break;
                        case R.id.playlistsItem:
                            key = Constants.TAB_PLAYLISTS;
                            break;
                        case R.id.songsItem:
                            key = Constants.TAB_SONGS;
                            break;
                    }
                    if (key != -1) {
                        MainActivity.showTab(key);
                        activity.finish();
                        MainActivity.showTab(key);
                    }
                    return true;
                }
            });

            //ImageButtons listeners
            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id;
                    if (activity.musicSrv.isPng()) {
                        id = R.mipmap.ic_play_arrow_white_36dp;
                        activity.musicSrv.pausePlayer();
                    } else {
                        id = R.mipmap.ic_pause_white_36dp;
                        activity.musicSrv.go();

                    }
                    playPause.setImageDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), id));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.musicSrv.playPrev();
                    activity.setSongCharacteristics();
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.musicSrv.playNext();
                    activity.setSongCharacteristics();
                }
            });

            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isShuffle = activity.musicSrv.isShuffle();
                    activity.musicSrv.setShuffle(!isShuffle);
                    shuffle.setColorFilter(!isShuffle ? Constants.COLOR_BTN_ACTIVE
                            : Constants.COLOR_BTN_DISABLED);
                    Toast.makeText(activity.getApplicationContext(),
                            isShuffle ? "Shuffle is OFF" : "Shuffle is ON", Toast.LENGTH_SHORT).show();
                }
            });

            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isRepeat = activity.musicSrv.isRepeat();
                    activity.musicSrv.setRepeat(!isRepeat);
                    repeat.setColorFilter(!isRepeat ? Constants.COLOR_BTN_ACTIVE
                            : Constants.COLOR_BTN_DISABLED);
                    Toast.makeText(activity.getApplicationContext(),
                            isRepeat ? "Repeat is OFF" : "Repeat is ON", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initImageAlbumScale() {
            float density = activity.getResources().getDisplayMetrics().density;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            int height;
            int width = (int) ((displaymetrics.widthPixels / density) - (35 * 2));
            height = width;

            albumImageView.getLayoutParams().height = (int) (height * density);
            albumImageView.getLayoutParams().width = (int) (width * density);
        }

        private void initToolbar() {
            toolbar.setTitle(R.string.now_playing);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

            toolbar.inflateMenu(R.menu.manage_toolbar_menu);

            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        public void setButtons() {
            playPause.setImageDrawable(ContextCompat.getDrawable(activity.getApplicationContext(),
                    activity.musicSrv.isPng() ? R.mipmap.ic_pause_white_36dp : R.mipmap.ic_play_arrow_white_36dp));
            shuffle.setColorFilter(activity.musicSrv.isShuffle() ? Constants.COLOR_BTN_ACTIVE
                    : Constants.COLOR_BTN_DISABLED);
            repeat.setColorFilter(activity.musicSrv.isRepeat() ? Constants.COLOR_BTN_ACTIVE
                    : Constants.COLOR_BTN_DISABLED);
            //todo onresume mainactivity сменить трек внизу
            //todo oncompletition смена трека тут
        }
    }
}
