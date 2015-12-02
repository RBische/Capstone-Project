package fr.bischof.raphael.gothiite.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.service.RunningService;

public class SessionTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_type);
        if (getIntent().getAction()== RunningService.ACTION_SHOW_UI_FROM_RUN){
            Intent runFromRunningServiceNotification = new Intent(this,CreateRunActivity.class);
            runFromRunningServiceNotification.setAction(RunningService.ACTION_SHOW_UI_FROM_RUN);
            runFromRunningServiceNotification.putExtra(RunningService.EXTRA_VVO2MAX,getIntent().getDoubleExtra(RunningService.EXTRA_VVO2MAX,0));
            startActivity(runFromRunningServiceNotification);
        }
    }
}
