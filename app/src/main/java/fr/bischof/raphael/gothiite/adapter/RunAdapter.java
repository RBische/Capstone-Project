package fr.bischof.raphael.gothiite.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Adapter to show run sessions
 * Created by biche on 16/09/2015.
 */
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunAdapterViewHolder> {
    private final Context mContext;
    private Cursor mCursor;

    public RunAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RunAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(RunAdapterViewHolder runAdapterViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public class RunAdapterViewHolder extends RecyclerView.ViewHolder {
        public RunAdapterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
