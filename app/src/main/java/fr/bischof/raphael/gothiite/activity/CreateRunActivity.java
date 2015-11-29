package fr.bischof.raphael.gothiite.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.fragment.CreateRunFragment;
import fr.bischof.raphael.gothiite.fragment.CreateSessionTypeFragment;

public class CreateRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_run);
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
        }
        return super.onOptionsItemSelected(item);
    }

}
