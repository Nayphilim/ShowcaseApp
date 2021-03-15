package com.nayphilim.showcaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class AdapterProfileFeed extends RecyclerView.Adapter<AdapterProfileFeed.MyViewHolder>{

    Context context;
    ArrayList<ProfileFeed> profileFeedArrayList = new ArrayList<>();
    RequestManager glide;
    private OnProjectListener onProjectListener;

    public AdapterProfileFeed(Context context , ArrayList<ProfileFeed> profileFeedArrayList, OnProjectListener onProjectListener){
        this.context = context;
        this.profileFeedArrayList = profileFeedArrayList;
        this.onProjectListener = onProjectListener;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed, parent, false);
       MyViewHolder viewHolder = new MyViewHolder(view, onProjectListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ProfileFeed profileFeed = profileFeedArrayList.get(position);

        holder.projectTitle.setText(profileFeed.getTitle());
        holder.projectDate.setText(profileFeed.getDate());
        holder.projectCategory.setText(profileFeed.getCategory());


        if(profileFeed.getPostPic()==null){
            holder.postImage.setVisibility(View.GONE);
        }
        else{
            holder.postImage.setVisibility(View.VISIBLE);
            glide.load(profileFeed.getPostPic()).into(holder.postImage);

        }


    }

    @Override
    public int getItemCount() {
        return profileFeedArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView projectTitle, projectCategory, projectDate;
        ImageView postImage;
        OnProjectListener onProjectListener;

        public MyViewHolder(@NonNull View itemView, OnProjectListener onProjectListener) {
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
}
