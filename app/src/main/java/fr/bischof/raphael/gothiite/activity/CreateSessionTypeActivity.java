package fr.bischof.raphael.gothiite.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.fragment.CreateSessionTypeFragment;

public class CreateSessionTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session_type);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            if (getIntent().hasExtra(CreateSessionTypeFragment.RUN_TYPE_ID)){
                arguments.putString(CreateSessionTypeFragment.RUN_TYPE_ID, getIntent().getStringExtra(CreateSessionTypeFragment.RUN_TYPE_ID));
            }
            CreateSessionTypeFragment fragment = new CreateSessionTypeFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_session_type_interval, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete){
            ((CreateSessionTypeFragment)getSupportFragmentManager().findFragmentById(R.id.container)).deleteRunType();
        }
        return super.onOptionsItemSelected(item);
    }
}
