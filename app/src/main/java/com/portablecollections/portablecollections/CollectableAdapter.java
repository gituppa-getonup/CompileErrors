package com.portablecollections.portablecollections;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CollectableAdapter extends RecyclerView.Adapter<CollectableAdapter.ViewHolder> {

    private Context context;
    private long identifier;
    public long getIdentifier() {
        return identifier;
    }
    private void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

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

            String imageUriString = mCursor.getString(mCursor.getColumnIndexOrThrow("imageUri"));
            Bitmap bitmap = pictureHelper.getBitmapFromString(imageUriString, context);
            holder.mView.setImageBitmap(bitmap);

            String nameString = mCursor.getString(mCursor.getColumnIndexOrThrow("name"));
            holder.mText.setText(nameString);

            setIdentifier(getItemId(position));

            //setIdentifier(holder.getLayoutPosition());
            // todo getItemId seems to always return -1
            // and getAdapterPosition() does not seem right, unless it is used to retrieve the identifier in another way.


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

    @Override
    public long getItemId(int position) {
        return mCursor.getLong(mCursor.getColumnIndexOrThrow("id"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mView;
        TextView mText;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.part_of_recycleview
                    , parent
                    , false));
            mView = itemView.findViewById(R.id.recyclerImageView);
            mText = itemView.findViewById(R.id.recyclerTextView);

        }

    }
}
