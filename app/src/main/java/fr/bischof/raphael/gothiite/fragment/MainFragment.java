package fr.bischof.raphael.gothiite.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
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
import fr.bischof.raphael.gothiite.activity.DetailsActivity;
import fr.bischof.raphael.gothiite.activity.RunTypeActivity;
import fr.bischof.raphael.gothiite.adapter.RunAdapter;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.sync.GothiiteSyncAdapter;


/**
 * Main fragment of the app that displays runs done
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,RunAdapter.RunAdapterOnClickHandler {

    private static final int RUNS_LOADER = 0;
    private static final String[] RUNS_PROJECTION = {RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry._ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_RUN_TYPE_ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_AVG_SPEED,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_NAME,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_ICON,
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
        mRunAdapter = new RunAdapter(getActivity(),this, emptyView);
        mRvRuns.setAdapter(mRunAdapter);
        v.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RunTypeActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load an ad into the AdMob banner view.
        View v = view.findViewById(R.id.adView);
        if (v!=null){
            AdView adView = (AdView) v;
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").addTestDevice("EA386A99E50D1A299261A06D370FE069").build();
            adView.loadAd(adRequest);
        }
        startLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = RunContract.RunEntry.COLUMN_START_DATE + " DESC";

        GothiiteSyncAdapter.syncImmediately(getActivity());
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

    @Override
    public void onClick(String runId, RunAdapter.RunAdapterViewHolder vh) {
        Intent i = new Intent(getActivity(), DetailsActivity.class);
        i.setData(RunContract.RunIntervalEntry.buildRunIntervalsWithRunUri(runId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), vh.mIvRun, vh.mIvRun.getTransitionName());
            getActivity().startActivity(i, options.toBundle());
        }else{
            startActivity(i);
        }
    }

    /**
     * Clears the loader
     */
    public void removeLoader() {
        getLoaderManager().destroyLoader(RUNS_LOADER);
    }

    /**
     * Start a new fresh loader
     */
    public void startLoader() {
        getLoaderManager().initLoader(RUNS_LOADER, null, this);
    }
}
