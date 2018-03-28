package com.portablecollections.portablecollections;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;


public class CollectableAdapter extends RecyclerView.Adapter<CollectableAdapter.ViewHolder> {

    private Context context;

    CollectableAdapter(Context context) {
        this.context = context;
    }

    private Cursor mCursor;
    CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCursor.moveToPosition(position)) {
            // send String, receive Bitmap

            String imageUriString = mCursor.getString(mCursor.getColumnIndexOrThrow("imageUri"));
            Bitmap bitmap = pictureHelper.getBitmapFromString(imageUriString, context);
            holder.mView.setImageBitmap(bitmap);

            String nameString = mCursor.getString(mCursor.getColumnIndexOrThrow("name"));
            holder.mText.setText(nameString);
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

        ImageView mView;
        TextView mText;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.part_of_recycleview, parent, false));
            mView = itemView.findViewById(R.id.recyclerImageView);
            mText = itemView.findViewById(R.id.recyclerTextView);

        }

    }
}
