package fr.bischof.raphael.gothiite.adapter;

import android.content.Context;
import android.database.Cursor;
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
import fr.bischof.raphael.gothiite.fragment.SessionTypeFragment;

/**
 * Adapter that shows session types
 * Created by biche on 18/09/2015.
 */
public class RunTypeAdapter extends RecyclerView.Adapter<RunTypeAdapter.RunTypeAdapterViewHolder> {
    private final Context mContext;
    private final RunTypeAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;
    public RunTypeAdapter(Context context, RunTypeAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public RunTypeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_run_type_item, viewGroup, false);
            view.setFocusable(true);
            return new RunTypeAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(RunTypeAdapterViewHolder runTypeAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        runTypeAdapterViewHolder.mTvTitle.setText(mCursor.getString(mCursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME)));
        runTypeAdapterViewHolder.mTvSubtitle.setText(mCursor.getString(mCursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_DESCRIPTION)));
        runTypeAdapterViewHolder.mIvRun.setImageResource(mContext.getResources().getIdentifier(mCursor.getString(mCursor.getColumnIndex(RunContract.RunTypeEntry.COLUMN_ICON)), "drawable", mContext.getPackageName()));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public interface RunTypeAdapterOnClickHandler {
        void onClick(String runTypeId, RunTypeAdapterViewHolder vh);
    }

    public class RunTypeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTvTitle;
        public final TextView mTvSubtitle;
        public final ImageView mIvRun;

        public RunTypeAdapterViewHolder(View view) {
            super(view);
            mTvTitle = (TextView) view.findViewById(R.id.tvTitle);
            mTvSubtitle = (TextView) view.findViewById(R.id.tvSubtitle);
            mIvRun = (ImageView) view.findViewById(R.id.ivRun);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(RunContract.RunTypeEntry._ID);
            mClickHandler.onClick(mCursor.getString(dateColumnIndex), this);
        }
    }
}
