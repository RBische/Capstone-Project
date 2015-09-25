package fr.bischof.raphael.gothiite.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;

/**
 * Adapter that displays item of RunIntervalType
 * Created by biche on 19/09/2015.
 */
public class RunTypeIntervalAdapter extends RecyclerView.Adapter<RunTypeIntervalAdapter.RunTypeIntervalViewHolder>
        implements DraggableItemAdapter<RunTypeIntervalAdapter.RunTypeIntervalViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";
    private final Context mContext;
    private final OnItemClickDeleteListener mListener;
    private Cursor mCursor;

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public class RunTypeIntervalViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {
        public ImageView mIvDelete;
        public RelativeLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public RunTypeIntervalViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.flContainer);
            mIvDelete = (ImageView) v.findViewById(R.id.ivDelete);
            mDragHandle = v.findViewById(R.id.handler);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mIvDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(RunContract.RunTypeIntervalEntry._ID);
            if (mListener!=null){
                mListener.onClick(mCursor.getString(dateColumnIndex), this);
            }
        }
    }

    public RunTypeIntervalAdapter(Context context, OnItemClickDeleteListener listener) {
        mContext = context;
        mListener = listener;
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex(RunContract.RunIntervalEntry._ID)).hashCode();
    }

    @Override
    public RunTypeIntervalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.adapter_run_type_interval_item, parent, false);
        return new RunTypeIntervalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunTypeIntervalViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        boolean effort = mCursor.getInt(mCursor.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT))==1;
        long time = mCursor.getLong(mCursor.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO));
        holder.mTextView.setText(""+(time/1000)+mContext.getString(effort ? R.string.second_effort : R.string.txt_rest));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(RunTypeIntervalViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(RunTypeIntervalViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    public interface OnItemClickDeleteListener {
        void onClick(String id, RunTypeIntervalViewHolder vh);
    }
}