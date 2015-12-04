package fr.bischof.raphael.gothiite.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.dateformat.DateToShowFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "DetailUri";
    private static final int DETAIL_LOADER = 0;
    private static final String[] RUN_INTERVALS_PROJECTION = {
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_AVG_SPEED,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_ICON,
            RunContract.RunIntervalEntry.TABLE_NAME+"."+RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE
    };
    //TODO: It seems to be a bug, informations are not shown even if data seems to be good
    @InjectView(R.id.tvStartDate) TextView mTvStartDate;
    @InjectView(R.id.ivProgressionIcon) ImageView mIvProgressionIcon;
    @InjectView(R.id.tvDistanceDone) TextView mTvDistanceDone;
    @InjectView(R.id.tvProgressionSpeed) TextView mTvProgressionSpeed;
    @InjectView(R.id.tvDistancePerInterval) TextView mTvDistancePerInterval;
    @InjectView(R.id.tvAverageSpeed) TextView mTvAverageSpeed;
    @InjectView(R.id.tvVVO2maxEquivalent) TextView mTvVVO2maxEquivalent;
    private Uri mUri;


    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.inject(this, v);
        if (getActivity()!=null&& getActivity() instanceof AppCompatActivity){
            Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar!=null){
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE + " DESC";

        return new CursorLoader(getActivity(),
                mUri,
                RUN_INTERVALS_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount()>0){
            data.moveToFirst();
            long date = data.getLong(data.getColumnIndex(RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE));
            String dateText = DateToShowFormat.getFriendlyDayString(getActivity(), date);
            mTvStartDate.setText(dateText);
            mTvAverageSpeed.setText(""+data.getDouble(data.getColumnIndex(RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_AVG_SPEED))+" "+getString(R.string.kmh));
            mTvVVO2maxEquivalent.setText(""+data.getDouble(data.getColumnIndex(RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT))+" "+getString(R.string.kmh));
            double totalMetersDone = 0;
            String iconResource = data.getString(data.getColumnIndex(RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_ICON));
            mIvProgressionIcon.setImageResource(getActivity().getResources().getIdentifier(iconResource, "drawable", getActivity().getPackageName()));
            while (!data.isAfterLast()){
                totalMetersDone += data.getDouble(data.getColumnIndex(RunContract.RunIntervalEntry.TABLE_NAME+"."+RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE));
                data.moveToNext();
            }
            if (totalMetersDone>1000){
                mTvDistanceDone.setText(""+totalMetersDone+" "+getString(R.string.kilometers_abreviation));
            }else{
                mTvDistanceDone.setText(""+totalMetersDone+" "+getString(R.string.meters_abreviation));
            }
            if (totalMetersDone/data.getCount()>1000){
                mTvDistancePerInterval.setText(""+(totalMetersDone/data.getCount())+" "+getString(R.string.kilometers_per_interval_abreviation));
            }else{
                mTvDistancePerInterval.setText(""+(totalMetersDone/data.getCount())+" "+getString(R.string.meters_per_interval_abreviation));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
