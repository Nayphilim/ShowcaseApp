package com.nayphilim.showcaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class AdapterSearchFeed extends RecyclerView.Adapter<SearchFeedViewHolder>{

    Context context;
    ArrayList<SearchFeed> searchFeedArrayList = new ArrayList<>();
    RequestManager glide;
    private OnProfileListener onProfileListener;

    public AdapterSearchFeed(Context context , ArrayList<SearchFeed> searchFeedArrayList, OnProfileListener onProfileListener){
        this.context = context;
        this.searchFeedArrayList = searchFeedArrayList;
        this.onProfileListener = onProfileListener;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public SearchFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_feed, parent, false);
        SearchFeedViewHolder viewHolder = new SearchFeedViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFeedViewHolder holder, int position) {
        final SearchFeed searchFeed = searchFeedArrayList.get(position);

        holder.searchUserName.setText(searchFeed.getUserName());

        if(searchFeed.getLocation() != null){
            holder.searchLocation.setText(searchFeed.getLocation());
            holder.searchLocation.setVisibility(View.VISIBLE);
        }

        if(searchFeed.getSpecialization() != null){
            holder.searchCategory.setText(searchFeed.getSpecialization());
            holder.searchCategory.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return searchFeedArrayList.size();
    }




    public interface OnProfileListener{
        void onProfileClick(int position);

    }
}
