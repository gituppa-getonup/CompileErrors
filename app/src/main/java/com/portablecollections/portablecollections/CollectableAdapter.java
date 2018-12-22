package com.portablecollections.portablecollections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


public class CollectableAdapter extends RecyclerView.Adapter<CollectableAdapter.ViewHolder> {

    private static final String TAG = CollectableAdapter.class.getName();
    private SortedList<Collectable> sortedList;
    static CollectableAdapter collectableAdapter;
    private Context context;
    private CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(context);

    public static CollectableAdapter getCollectableAdapter(Context context) {
        if (collectableAdapter == null) {
            collectableAdapter = new CollectableAdapter(context.getApplicationContext());
        }
        return collectableAdapter;
    }

    private CollectableAdapter(Context context) {
        this.context = context;

        setHasStableIds(true);

        sortedList = new SortedList<>(Collectable.class, new SortedListAdapterCallback<Collectable>(this) {
            @Override
            public int compare(Collectable c1, Collectable c2) {
                return c1.getName().compareTo(c2.getName());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPostion, int toPosition) {
                notifyItemMoved(fromPostion, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Collectable oldCol, Collectable newCol) {
                return oldCol.getName().equals(newCol.getName());
            }

            @Override
            public boolean areItemsTheSame(Collectable oldCol, Collectable newCol) {
                return oldCol.getId() == newCol.getId();
            }

        }
        );
    }


    public int add(Collectable col) {
        return sortedList.add(col);
    }

    public Collectable get(int index) {
        return sortedList.get(index);
    }

    public int indexOf(Collectable col) {
        return sortedList.indexOf(col);
    }

    public void updateItemAt(int index, Collectable col) {
        sortedList.updateItemAt(index, col);
    }

    public void addAll(List<Collectable> items) {
        sortedList.beginBatchedUpdates();
        for (Collectable item : items) {
            sortedList.add(item);
        }
        sortedList.endBatchedUpdates();
    }

    public int getCount() {
        return sortedList.size();
    }

    public boolean remove(Collectable col) {
        return sortedList.remove(col);
    }

    public void clear() {
        sortedList.beginBatchedUpdates();
        //remove items at end, to avoid unnecessary array shifting
        while (sortedList.size() > 0) {
            sortedList.removeItemAt(sortedList.size() - 1);
        }
        sortedList.endBatchedUpdates();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Collectable collectable = sortedList.get(position);
        String imageUriString = collectable.getImageUri();
        Uri imageUri = Uri.parse(imageUriString);
        String filePathString = imageUri.getPath();
        pictureHelper.setWidthHeight();
        holder.mView.setImageBitmap(pictureHelper.decodeSampledBitmapFromFile(filePathString, imageUriString, pictureHelper.width, pictureHelper.height));
        String nameString = collectable.getName();
        holder.mText.setText(nameString);
        holder.mView.setTag(R.id.TAG_COLLECTABLE, collectable);
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    @Override
    public long getItemId(int position) {
        return sortedList.get(position).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
