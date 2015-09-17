package fr.bischof.raphael.gothiite.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.dateformat.DateToShowFormat;

/**
 * Adapter to show run sessions
 * Created by biche on 16/09/2015.
 */
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunAdapterViewHolder> {
    private final Context mContext;
    private final View mEmptyView;
    private Cursor mCursor;

    public RunAdapter(Context context, View emptyView) {
        this.mContext = context;
        this.mEmptyView = emptyView;
    }

    @Override
    public RunAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_run_item, viewGroup, false);
            view.setFocusable(true);
            return new RunAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(RunAdapterViewHolder runAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        Calendar runDate = Calendar.getInstance();
        runDate.setTimeInMillis(mCursor.getLong(mCursor.getColumnIndex(RunContract.RunEntry.COLUMN_START_DATE)));
        runAdapterViewHolder.mTvTitle.setText(new DateToShowFormat().format(runDate.getTime()));
        runAdapterViewHolder.mTvSubtitle.setText(""+mCursor.getLong(mCursor.getColumnIndex(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT))+" "+mContext.getString(R.string.kmh_vvo2max));
        runAdapterViewHolder.mTvOtherDetail.setText(mCursor.getString(mCursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME)));
        //TODO: Add icon to Database so that it can be changed for each RunTypes
        runAdapterViewHolder.mIvRun.setImageResource(R.drawable.ico_run);
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class RunAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTvOtherDetail;
        public final TextView mTvTitle;
        public final TextView mTvSubtitle;
        public final ImageView mIvRun;

        public RunAdapterViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);
            mTvOtherDetail = (TextView) itemView.findViewById(R.id.tvOtherDetail);
            mIvRun = (ImageView) itemView.findViewById(R.id.ivRun);
        }
    }
}
