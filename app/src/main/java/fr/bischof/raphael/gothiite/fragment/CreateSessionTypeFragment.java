package fr.bischof.raphael.gothiite.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.adapter.RunTypeIntervalAdapter;
import fr.bischof.raphael.gothiite.data.RunContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateSessionTypeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, RunTypeIntervalAdapter.OnItemClickDeleteListener {
    private static final int RUN_TYPE_LOADER = 1;
    private static final java.lang.String SAVED_PARSE_ID = "parseId";
    private static final java.lang.String SAVED_EVER_INSERTED = "everInserted";
    private static final String[] RUN_TYPE_INTERVALS_PROJECTION = {RunContract.RunTypeIntervalEntry._ID,
            RunContract.RunTypeIntervalEntry.COLUMN_EFFORT,
            RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO};
    public static final String RUN_TYPE_ID = "runTypeID";
    @InjectView(R.id.etName)
    EditText etName;
    @InjectView(R.id.etDescription)
    EditText etDescription;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RunTypeIntervalAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private String mRunTypeID;
    private Uri mUri;
    private boolean mEverInserted = false;

    public CreateSessionTypeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_session_type, container, false);
        ButterKnife.inject(this, v);
        v.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
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
        if (savedInstanceState!=null){
            mEverInserted = savedInstanceState.getBoolean(SAVED_EVER_INSERTED, false);
            String textualParseId = savedInstanceState.getString(SAVED_PARSE_ID);
            if (textualParseId!=null){
                mRunTypeID = textualParseId;
            }
        }else{
            if (getArguments().containsKey(RUN_TYPE_ID)){
                mRunTypeID = getArguments().getString(RUN_TYPE_ID);
                Cursor cursor = getActivity().getContentResolver().query(RunContract.RunTypeEntry.buildRunTypeUri(mRunTypeID),new String[]{RunContract.RunTypeEntry.COLUMN_NAME, RunContract.RunTypeEntry.COLUMN_DESCRIPTION, RunContract.RunTypeEntry.COLUMN_ICON},null,null,null);
                if (cursor!=null){
                    if (cursor.getCount()>0){
                        cursor.moveToFirst();
                        etName.setText(cursor.getString(cursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME)));
                        etDescription.setText(cursor.getString(cursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_DESCRIPTION)));
                    }
                    cursor.close();
                }
            }else {
                mRunTypeID = UUID.randomUUID().toString();
            }
        }
        return v;
    }

    private void addItem() {
        saveRunType();
        DialogFragment newFragment = CreateSessionTypeIntervalFragment.newInstance(mRunTypeID,mAdapter.getItemCount());
        newFragment.show(getActivity().getSupportFragmentManager(), "runtypeinterval");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_PARSE_ID, mRunTypeID);
        outState.putBoolean(SAVED_EVER_INSERTED, mEverInserted);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName.addTextChangedListener(this);
        etDescription.addTextChangedListener(this);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rvIntervals);
        mLayoutManager = new LinearLayoutManager(getActivity());

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        //mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(getResources().getDrawable(R.drawable.ic_mover));

        mAdapter = new RunTypeIntervalAdapter(getActivity(),this);
        setAdapter();
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            //mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        //mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
        mUri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri();
        getLoaderManager().initLoader(RUN_TYPE_LOADER, null, this);
    }

    private void setAdapter() {
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RunContract.RunTypeIntervalEntry.TABLE_NAME+"."+RunContract.RunTypeIntervalEntry.COLUMN_ORDER + " ASC";

        return new CursorLoader(getActivity(),
                mUri,
                RUN_TYPE_INTERVALS_PROJECTION,
                RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + "= ?",
                new String[]{mRunTypeID},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        saveRunType();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void saveRunType(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RunContract.RunTypeEntry._ID,mRunTypeID);
        //TODO: Make an icon picker
        contentValues.put(RunContract.RunTypeEntry.COLUMN_ICON,"ico_run");
        if (!etName.getText().toString().equals("")){
            contentValues.put(RunContract.RunTypeEntry.COLUMN_NAME,etName.getText().toString());
        }else{
            contentValues.put(RunContract.RunTypeEntry.COLUMN_NAME,getString(R.string.default_run_type_name));
        }
        contentValues.put(RunContract.RunTypeEntry.COLUMN_DISTANCE_GROWING,false);
        contentValues.put(RunContract.RunTypeEntry.COLUMN_DESCRIPTION,etDescription.getText().toString());
        contentValues.put(RunContract.RunTypeEntry.COLUMN_CAN_BE_DELETED,true);
        ContentResolver contentResolver = getContext().getContentResolver();
        if (mEverInserted){
            contentResolver.update(RunContract.RunTypeEntry.buildRunTypeUri(mRunTypeID), contentValues, null, null);
        }else{
            contentResolver.insert(RunContract.RunTypeEntry.buildRunTypeUri(mRunTypeID), contentValues);
            mEverInserted = true;
        }
    }

    @Override
    public void onClick(final String id, RunTypeIntervalAdapter.RunTypeIntervalViewHolder vh) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.confirm)).setMessage(getString(R.string.sure_delete_interval)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContentResolver contentResolver = getContext().getContentResolver();
                contentResolver.delete(RunContract.RunTypeIntervalEntry.buildRunTypeIntervalUri(id),null,null);
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
        dialog.show();
    }

    public void deleteRunType() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.confirm)).setMessage(getString(R.string.sure_delete_run_type)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContentResolver contentResolver = getContext().getContentResolver();
                contentResolver.delete(RunContract.RunTypeEntry.buildRunTypeUri(mRunTypeID),null,null);
                getActivity().finish();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
        dialog.show();
    }
}
