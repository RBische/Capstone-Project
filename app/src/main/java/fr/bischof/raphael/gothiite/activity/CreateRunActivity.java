package fr.bischof.raphael.gothiite.activity;

import android.os.Bundle;
import android.app.Activity;

import fr.bischof.raphael.gothiite.R;

public class CreateRunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_run);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
