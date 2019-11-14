package com.example.wallpapertest4;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.PopupMenu;

import java.io.IOException;

import static com.example.wallpapertest4.Constant.ACTION_VOICE_NORMAL;
import static com.example.wallpapertest4.Constant.ACTION_VOICE_SILENCE;

public class VideoWallpaper extends WallpaperService {

    private static final String TAG = VideoWallpaper.class.getName();
    private static String sVideoPath;

    public static void setVoiceSilence(Context context) {
        Intent intent = new Intent(Constant.Video_PARAMS_CONTROL_ACTION);
        intent.putExtra(Constant.ACTION, ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);

    }

    public static void setVoiceNormal(Context context) {
        Intent intent = new Intent(Constant.Video_PARAMS_CONTROL_ACTION);
        intent.putExtra(Constant.ACTION, ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    public static void setToWallPaper(Context context, String videoPath) {

        try {
            context.clearWallpaper();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sVideoPath = videoPath;

         //alert.setTitle("AlertDialog Title");
         //alert.setMessage("AlertDialog Content");

        Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        //intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, VideoWallpaper.class));
        context.startActivity(intent);
    }

    @Override
    public Engine onCreateEngine() {
        try {
            return new VideoWallpaperEngine();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    class VideoWallpaperEngine extends Engine {

        private SurfaceHolder holder;
        private MediaPlayer mMediaPlayer;
        private BroadcastReceiver mVideoVoiceControlReceiver;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {

            super.onCreate(surfaceHolder);
            this.holder = getSurfaceHolder();
            IntentFilter intentFileter = new IntentFilter(Constant.Video_PARAMS_CONTROL_ACTION);
            mVideoVoiceControlReceiver = new VideoVoiceControlReceiver();
            registerReceiver(mVideoVoiceControlReceiver,intentFileter);
            //onSurfaceCreated(this.holder);
        }


        public void onDestory() {
            unregisterReceiver(mVideoVoiceControlReceiver);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {

           // builder.setTitle("AlertDialog Title");
           // builder.setMessage("AlertDialog Content");

            super.onSurfaceCreated(holder);
            //Surface surface;
            if (TextUtils.isEmpty(sVideoPath)) {
                throw new NullPointerException("videoPath must not be null");
            } else {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setSurface(holder.getSurface());

                try {
                    mMediaPlayer.setDataSource(sVideoPath);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setVolume(0f, 0f);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        public void onSurfaceDestoryed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mMediaPlayer != null)
                mMediaPlayer.release();
            mMediaPlayer = null;
        }

        class VideoVoiceControlReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getIntExtra(Constant.ACTION, -1);
                switch (action) {
                    case ACTION_VOICE_NORMAL:
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                        break;

                    case ACTION_VOICE_SILENCE:
                        mMediaPlayer.setVolume(0, 0);
                        break;
                }
            }
        }
    }
}
