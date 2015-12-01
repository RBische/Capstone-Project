package fr.bischof.raphael.gothiite.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.activity.RunActivity;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.ui.ColorPart;
import fr.bischof.raphael.gothiite.ui.IntervalView;

/**
 * A fragment containing all necessary things to go for a run
 */
public class CreateRunFragment extends GPSFineLocationFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int RUN_TYPE_LOADER = 1;
    private static final String[] RUN_TYPE_INTERVALS_PROJECTION = {RunContract.RunTypeIntervalEntry._ID,
            RunContract.RunTypeIntervalEntry.COLUMN_EFFORT,
            RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO,
            RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO};
    public static final String EXTRA_VVO2MAX = "ExtraVVO2Max";
    @InjectView(R.id.ivGPS)
    public ImageView mIvGPS;
    @InjectView(R.id.tvRunType)
    public TextView mTvRunType;
    @InjectView(R.id.tvDescription)
    public TextView mTvDescription;
    @InjectView(R.id.invRunType)
    public IntervalView mInvRunType;
    @InjectView(R.id.tvLastSpeed)
    public TextView mTvLastSpeed;
    @InjectView(R.id.etSpeedChoosed)
    public EditText mEtSpeedChoosed;

    public CreateRunFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_run, container, false);
    }

    /*difficulté des séances en fonction du nombre de période de run

    ajouter niveau de difficulté des séances*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        Uri currentUri  = getActivity().getIntent().getData();
        String txtLastSpeed = getString(R.string.last_speed_start);
        Cursor cursorLastRun = getActivity().getContentResolver().query(RunContract.RunEntry.buildLastRunUri(), new String[]{RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT}, null, null, null);
        if (cursorLastRun!=null){
            if (cursorLastRun.getCount()>0){
                cursorLastRun.moveToFirst();
                double lastSpeed = cursorLastRun.getDouble(cursorLastRun.getColumnIndex(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT));
                txtLastSpeed += " ("+getString(R.string.last_speed_mid)+" "+lastSpeed+getString(R.string.last_speed_end)+")";
                mEtSpeedChoosed.setText(""+lastSpeed);
            }
            cursorLastRun.close();
        }
        mTvLastSpeed.setText(txtLastSpeed);
        if (currentUri!=null){
            Cursor cursorRunType = getActivity().getContentResolver().query(currentUri, new String[]{RunContract.RunTypeEntry.COLUMN_NAME, RunContract.RunTypeEntry.COLUMN_DESCRIPTION}, null, null, null);
            if (cursorRunType!=null){
                if (cursorRunType.getCount()>0){
                    cursorRunType.moveToFirst();
                    mTvRunType.setText(cursorRunType.getString(cursorRunType.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME)));
                    mTvDescription.setText(cursorRunType.getString(cursorRunType.getColumnIndex(RunContract.RunTypeEntry.COLUMN_DESCRIPTION)));
                }
                cursorRunType.close();
            }
            getLoaderManager().initLoader(RUN_TYPE_LOADER, null, this);
        }
        if (getActivity()!=null&& getActivity() instanceof AppCompatActivity){
            Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar!=null){
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RunContract.RunTypeIntervalEntry.TABLE_NAME+"."+RunContract.RunTypeIntervalEntry.COLUMN_ORDER + " ASC";
        Uri uri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri();
        String runTypeId = RunContract.RunTypeEntry.getRunTypeIdFromUri(getActivity().getIntent().getData());
        return new CursorLoader(getActivity(),
                uri,
                RUN_TYPE_INTERVALS_PROJECTION,
                RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + "= ?",
                new String[]{runTypeId},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<ColorPart> partsToDraw = new ArrayList<>();
        while (data.moveToNext()) {
            boolean effort = data.getInt(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT))==1;
            long time = data.getLong(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO));
            int timeInSec = (int)time;
            ColorPart part = new ColorPart(timeInSec,!effort);
            partsToDraw.add(part);
        }
        mInvRunType.updateParts(partsToDraw);
        data.moveToFirst();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onLocationUpdate(Location location) {
        super.onLocationUpdate(location);
        if (location.hasAccuracy()){
            if (mIvGPS!=null){
                if (location.getAccuracy()>80){
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_low));
                }else if (location.getAccuracy()>30){
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_medium));
                }else{
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_high));
                }
            }
        }
    }

    public void launchSession() {
        Intent intent = new Intent(getActivity(), RunActivity.class);
        intent.setData(getActivity().getIntent().getData());
        intent.putExtra(EXTRA_VVO2MAX, Double.parseDouble(mEtSpeedChoosed.getText().toString()));
        startActivity(intent);
    }
}
