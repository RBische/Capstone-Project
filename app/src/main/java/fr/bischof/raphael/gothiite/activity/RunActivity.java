package fr.bischof.raphael.gothiite.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.fragment.RunFragment;

public class RunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //TODO: Create alert dialog to confirm quit
        if (id == R.id.action_stop){
            if (getSupportFragmentManager().findFragmentById(R.id.fragment) instanceof RunFragment){
                RunFragment fragment = (RunFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.stopRun();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //TODO: Ask sure if want to quit run
    }
}
