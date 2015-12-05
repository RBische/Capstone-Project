package fr.bischof.raphael.gothiite.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.fragment.CreateRunTypeFragment;

public class CreateRunTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_run_type);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            if (getIntent().hasExtra(CreateRunTypeFragment.RUN_TYPE_ID)){
                arguments.putString(CreateRunTypeFragment.RUN_TYPE_ID, getIntent().getStringExtra(CreateRunTypeFragment.RUN_TYPE_ID));
            }
            CreateRunTypeFragment fragment = new CreateRunTypeFragment();
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
            ((CreateRunTypeFragment)getSupportFragmentManager().findFragmentById(R.id.container)).deleteRunType();
        }
        return super.onOptionsItemSelected(item);
    }
}
