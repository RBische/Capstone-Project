package fr.bischof.raphael.gothiite.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

import java.io.File;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.data.RunDbHelper;
import fr.bischof.raphael.gothiite.fragment.MainFragment;
import fr.bischof.raphael.gothiite.service.RunningService;
import fr.bischof.raphael.gothiite.sync.GothiiteSyncAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (getIntent().getAction()!=null&&getIntent().getAction().equals(RunningService.ACTION_SHOW_UI_FROM_RUN)){
            Intent runFromRunningServiceNotification = new Intent(this,SessionTypeActivity.class);
            runFromRunningServiceNotification.setAction(RunningService.ACTION_SHOW_UI_FROM_RUN);
            runFromRunningServiceNotification.putExtra(RunningService.EXTRA_VVO2MAX,getIntent().getDoubleExtra(RunningService.EXTRA_VVO2MAX,0));
            startActivity(runFromRunningServiceNotification);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //TODO: edit IE + edit voice pack
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connection){
            Intent connectionIntent = new Intent(this,ConnectionActivity.class);
            startActivity(connectionIntent);
        }else if (id == R.id.action_wipe_data){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            File dir = getDatabasePath("run.db");
                            if (dir.exists()){
                                if (dir.delete()){
                                    if (ParseUser.getCurrentUser()!=null){
                                        ParseUser.logOut();
                                    }
                                    ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).removeLoader();
                                    RunDbHelper helper = new RunDbHelper(MainActivity.this);
                                    helper.getWritableDatabase();
                                    helper.close();
                                    getContentResolver().notifyChange(RunContract.RunEntry.buildRunsWithRunTypeUri(), null);
                                    ((MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main)).startLoader();
                                }
                            }
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.sure_wipe_data)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        }else if (id == R.id.action_sync){
            GothiiteSyncAdapter.syncImmediately(this);
        }
        return super.onOptionsItemSelected(item);
    }

}
