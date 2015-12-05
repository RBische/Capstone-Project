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
 * Adapter that shows run sessions
 * Created by biche on 16/09/2015.
 */
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunAdapterViewHolder> {
    private final Context mContext;
    private final View mEmptyView;
    private Cursor mCursor;

    final private RunAdapterOnClickHandler mClickHandler;

    public RunAdapter(Context context, RunAdapterOnClickHandler dh, View emptyView) {
        this.mContext = context;
        this.mEmptyView = emptyView;
        mClickHandler = dh;
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
        runAdapterViewHolder.mIvRun.setImageResource(mContext.getResources().getIdentifier(mCursor.getString(mCursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_ICON)), "drawable", mContext.getPackageName()));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    /**
     * Changes the data of the adapter
     * @param data new data
     */
    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
        int itemCount = getItemCount();
        mEmptyView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Interface to raise click events on a run view
     */
    public interface RunAdapterOnClickHandler {
        void onClick(String runId, RunAdapterViewHolder vh);
    }

    /**
     * Basic viewholder
     */
    public class RunAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(RunContract.RunEntry._ID);
            mClickHandler.onClick(mCursor.getString(dateColumnIndex), this);
        }
    }
}
