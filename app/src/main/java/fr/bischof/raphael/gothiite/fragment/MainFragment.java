package fr.bischof.raphael.gothiite.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.adapter.RunAdapter;
import fr.bischof.raphael.gothiite.data.RunContract;


/**
 * Main fragment of the app
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RUNS_LOADER = 0;
    private static final String[] RUNS_PROJECTION = {RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry._ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_RUN_TYPE_ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_AVG_SPEED,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_NAME,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_DESCRIPTION};
    @InjectView(R.id.rvRuns) RecyclerView mRvRuns;
    private RunAdapter mRunAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, v);
        mRvRuns.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = v.findViewById(R.id.tvRvRunsEmpty);
        mRunAdapter = new RunAdapter(getActivity(), emptyView);
        mRvRuns.setAdapter(mRunAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        getLoaderManager().initLoader(RUNS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = RunContract.RunEntry.COLUMN_START_DATE + " DESC";

        Uri runsWithRunTypeUri = RunContract.RunEntry.buildRunsWithRunTypeUri();

        return new CursorLoader(getActivity(),
                runsWithRunTypeUri,
                RUNS_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mRunAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
