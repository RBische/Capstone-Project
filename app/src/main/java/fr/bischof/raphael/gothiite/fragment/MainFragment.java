package fr.bischof.raphael.gothiite.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RUNS_LOADER = 0;
    private static final String[] RUNS_PROJECTION = {RunContract.RunEntry._ID,
            RunContract.RunEntry.COLUMN_RUN_TYPE_ID,
            RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunEntry.COLUMN_AVG_SPEED};
    @InjectView(R.id.rvRuns) RecyclerView mRvRuns;
    private RunAdapter mRunAdapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        mRunAdapter = new RunAdapter(getActivity());
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

        Uri weatherForLocationUri = RunContract.RunEntry.buildRunsUri();

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
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
