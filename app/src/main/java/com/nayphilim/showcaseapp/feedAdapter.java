package com.nayphilim.showcaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

public class feedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    Context context;
    ArrayList<ProfileFeed> feedArrayList = new ArrayList<>();
    RequestManager glide;
    private OnProjectListener onProjectListener;


    public feedAdapter(Context context, ArrayList<ProfileFeed> profileFeedArrayList, OnProjectListener onProjectListener) {

        this.context = context;
        this.feedArrayList = profileFeedArrayList;
        this.onProjectListener = onProjectListener;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed, parent, false);
            return new ItemViewHolder(view,onProjectListener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {
        return feedArrayList == null ? 0 : feedArrayList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return feedArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView projectTitle, projectCategory, projectDate;
        ImageView postImage;
        OnProjectListener onProjectListener;

        public ItemViewHolder(@NonNull View itemView, OnProjectListener onProjectListener) {
            super(itemView);

            projectTitle = itemView.findViewById(R.id.projectTitle);
            projectCategory = itemView.findViewById(R.id.projectCategory);
            projectDate = itemView.findViewById(R.id.projectDate);

            postImage = itemView.findViewById(R.id.postImage);

            this.onProjectListener = onProjectListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProjectListener.onProjectClick(getAdapterPosition());
        }


    }

    public interface OnProjectListener{
        void onProjectClick(int position);

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadingItemProgressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        viewHolder.progressBar.setVisibility(View.VISIBLE);
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {

        final ProfileFeed profileFeed = feedArrayList.get(position);

        viewHolder.projectTitle.setText(profileFeed.getTitle());
        viewHolder.projectDate.setText(profileFeed.getDate());
        viewHolder.projectCategory.setText(profileFeed.getCategory());


        if (profileFeed.getPostPic() == null) {
            viewHolder.postImage.setVisibility(View.GONE);
        } else {
            viewHolder.postImage.setVisibility(View.VISIBLE);
            glide.load(profileFeed.getPostPic()).into(viewHolder.postImage);

        }

    }
}