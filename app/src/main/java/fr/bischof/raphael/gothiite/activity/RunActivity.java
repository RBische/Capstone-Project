package fr.bischof.raphael.gothiite.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
        if (id == R.id.action_stop){
            quitRun(true);
        }else if (id == android.R.id.home){
            quitRun(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void quitRun(final boolean withSave) {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment) instanceof RunFragment){
            final RunFragment fragment = (RunFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            fragment.stopRun(withSave);
                            finish();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.sure_stop_run)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        }
    }

    @Override
    public void onBackPressed() {
        quitRun(false);
    }
}
