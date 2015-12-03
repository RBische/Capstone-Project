package fr.bischof.raphael.gothiite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.fragment.CreateRunFragment;
import fr.bischof.raphael.gothiite.service.RunningService;
import fr.bischof.raphael.gothiite.speech.MediaManager;
import fr.bischof.raphael.gothiite.speech.Synthesizer;

public class CreateRunActivity extends AppCompatActivity {

    private MediaManager mSoundPoolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_run);
        if (getIntent().getAction().equals(RunningService.ACTION_SHOW_UI_FROM_RUN)){
            Intent runFromRunningServiceNotification = new Intent(this,RunActivity.class);
            runFromRunningServiceNotification.setAction(RunningService.ACTION_SHOW_UI_FROM_RUN);
            runFromRunningServiceNotification.putExtra(RunningService.EXTRA_VVO2MAX,getIntent().getDoubleExtra(RunningService.EXTRA_VVO2MAX,0));
            startActivity(runFromRunningServiceNotification);
        }
        mSoundPoolManager = new MediaManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_run){
            ((CreateRunFragment)getSupportFragmentManager().findFragmentById(R.id.fragment)).launchSession();
        }else if (id == R.id.action_temp){
            //,((CreateRunFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).getNumber(),((CreateRunFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).getNumber()
            Synthesizer synthetizer = new Synthesizer(this,getString(R.string.mp3_pause));
            String[] number = synthetizer.getSynthesizedParts();
            mSoundPoolManager.addToQueue(number);
            mSoundPoolManager.playSoundsInQueue();
        }
        return super.onOptionsItemSelected(item);
    }

}
