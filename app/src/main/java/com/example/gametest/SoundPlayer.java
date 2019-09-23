package com.example.gametest;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {
    //Insert sounds into raw
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 5;

    private static SoundPool soundPool;
    private static int hitOrangeSound,hitPinkSound,hitBlackSound,gameStartMusic,silenceSound;


    public SoundPlayer(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitOrangeSound = soundPool.load(context, R.raw.money, 1);
        //hitBlackSound = soundPool.load(context, R.raw.bomb, 1);
        //hitPinkSound = soundPool.load(context, R.raw.cross, 1);
        //gameStartMusic = soundPool.load(context, R.raw.game,1);
        silenceSound = soundPool.load(context, R.raw.silence,2);
    }

    public void playHitOrangeSound(){
        soundPool.play(hitOrangeSound, 1.0f, 1.0f,1,0,1.0f);
    }
    public void playHitPinkSound(){
        soundPool.play(hitPinkSound, 1.0f, 1.0f,1,0,1.0f);
    }
    public void playHitBLackSound(){
        soundPool.play(hitBlackSound, 1.0f, 1.0f,1,0,1.0f);
    }
    public void playGameStartMusic(){soundPool.play(gameStartMusic,1.0f,1.0f,1,100,1.0f);}
    public void playPauseSound(){ soundPool.play(silenceSound,1.0f,1.0f,2,100,1.0f);}


}
