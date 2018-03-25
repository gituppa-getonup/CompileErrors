package com.portablecollections.portablecollections;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CollectableAdapter extends RecyclerView.Adapter<CollectableAdapter.ViewHolder> {

    private Cursor mCursor;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCursor.moveToPosition(position)) {
            holder.mText.setText(mCursor.getString(
                    mCursor.getColumnIndexOrThrow("name")
            ));
        }

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void setCollectables(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mText;
        ImageView mView;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_list_item_1, parent, false));
            mText = itemView.findViewById(android.R.id.text1);
        }

    }
}
