package fr.bischof.raphael.gothiite.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.activity.CreateSessionTypeActivity;
import fr.bischof.raphael.gothiite.activity.DetailsActivity;
import fr.bischof.raphael.gothiite.activity.SessionTypeActivity;
import fr.bischof.raphael.gothiite.adapter.RunAdapter;
import fr.bischof.raphael.gothiite.adapter.RunTypeAdapter;
import fr.bischof.raphael.gothiite.data.RunContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class SessionTypeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,RunTypeAdapter.RunTypeAdapterOnClickHandler {

    private static final int RUNS_LOADER = 0;
    private static final String[] RUN_TYPES_PROJECTION = {RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry._ID,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_NAME,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_ICON,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_CAN_BE_DELETED,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_DESCRIPTION};
    @InjectView(R.id.rvRunTypes)
    RecyclerView mRvRunTypes;
    private RunTypeAdapter mRunTypeAdapter;

    public SessionTypeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session_type, container, false);
        ButterKnife.inject(this, v);
        mRvRunTypes.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRunTypeAdapter = new RunTypeAdapter(getActivity(),this);
        mRvRunTypes.setAdapter(mRunTypeAdapter);
        v.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CreateSessionTypeActivity.class);
                startActivity(i);
            }
        });
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(RUNS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = RunContract.RunTypeEntry.COLUMN_NAME+ " ASC";

        Uri runsWithRunTypeUri = RunContract.RunTypeEntry.buildRunTypesUri();

        return new CursorLoader(getActivity(),
                runsWithRunTypeUri,
                RUN_TYPES_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRunTypeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(String runTypeId, RunTypeAdapter.RunTypeAdapterViewHolder vh) {
        /*Intent i = new Intent(getActivity(), DetailsActivity.class);
        i.setData(RunContract.RunIntervalEntry.buildRunIntervalsWithRunUri(runId));
        startActivity(i);*/
    }

    @Override
    public void onEditClick(String runTypeId, RunTypeAdapter.RunTypeAdapterViewHolder vh) {
        Intent i = new Intent(getActivity(), CreateSessionTypeActivity.class);
        i.putExtra(CreateSessionTypeFragment.RUN_TYPE_ID,runTypeId);
        startActivity(i);
    }
}
