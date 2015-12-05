package fr.bischof.raphael.gothiite.speech;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.bischof.raphael.gothiite.R;


/**
 * Class in charge to play sounds for the app
 * Created by biche on 02/12/2015.
 */
public class MediaManager {

    private MediaPlayer player;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    ArrayList<String> queue = new ArrayList<>();
    int counter;
    private Context mContext;
    private boolean mPlaying = false;

    /** Called when the mContext is first created. */
    public MediaManager(Context mContext) {
        this.mContext = mContext;
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) mContext.getSystemService(Activity.AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        volume = actVolume / maxVolume;

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;

        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                queue.remove(0);
                playSoundsRecursively();
            }
        });
    }

    /**
     * Add an array of strings that represents a list of mp3 assets. To start playing these assets, then execute {@link MediaManager#playSoundsInQueue()}
     * @param sounds Array of mp3 assets filename
     */
    public void addToQueue(String[] sounds){
        List<String> arraylistSounds = Arrays.asList(sounds);
        queue.addAll(arraylistSounds);
    }

    /**
     * Starts playing alls sounds in queue if the player isn't already playing something
     */
    public void playSoundsInQueue() {
        if(!mPlaying){
            playSoundsRecursively();
        }
    }

    private void playSoundsRecursively(){
        if (queue.size()>0){
            mPlaying = true;
            // Load the sounds
            try {
                player.reset();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                AssetFileDescriptor afd = mContext.getAssets().openFd(preferences.getString(mContext.getString(R.string.pref_language_pack_key),mContext.getString(R.string.pref_language_pack_default))+"/"+queue.get(0));
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.start();
            } catch (IOException e) {
                queue.clear();
                mPlaying = false;
            }
        }else{
            mPlaying = false;
        }
    }

}
