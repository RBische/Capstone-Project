package fr.bischof.raphael.gothiite.speech;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private Activity mActivity;

    /** Called when the activity is first created. */
    public MediaManager(Activity activity) {
        this.mActivity = activity;
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) activity.getSystemService(Activity.AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        volume = actVolume / maxVolume;
        //Hardware buttons setting to adjust the media sound
        activity.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;

        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                queue.remove(0);
                playSoundsInQueue();
            }
        });
        playSoundsInQueue();
    }

    public void addToQueue(String[] sounds){
        List<String> arraylistSounds = Arrays.asList(sounds);
        queue.addAll(arraylistSounds);
    }

    public void playSoundsInQueue() {
        if (queue.size()>0){
            // Load the sounds
            try {
                player.reset();
                AssetFileDescriptor afd = mActivity.getAssets().openFd(queue.get(0));
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
