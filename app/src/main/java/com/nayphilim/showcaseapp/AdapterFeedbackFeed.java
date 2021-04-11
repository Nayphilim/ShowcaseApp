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

public class AdapterFeedbackFeed extends RecyclerView.Adapter<AdapterFeedbackFeed.MyViewHolder>{

    Context context;
    ArrayList<FeedbackFeed> feedbackFeedArrayList = new ArrayList<>();
    RequestManager glide;
    private OnFeedbackListener onFeedbackListener;

    public AdapterFeedbackFeed(Context context , ArrayList<FeedbackFeed> feedbackFeedArrayList){
        this.context = context;
        this.feedbackFeedArrayList = feedbackFeedArrayList;
        this.onFeedbackListener = onFeedbackListener;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_row_feed, parent, false);
       MyViewHolder viewHolder = new MyViewHolder(view, onFeedbackListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final FeedbackFeed feedbackFeed = feedbackFeedArrayList.get(position);

        holder.projectTitle.setText(feedbackFeed.getProjectTitle());
        holder.feedbackName.setText(feedbackFeed.getUserName() + "'s");
        holder.feedbackDialog.setText(feedbackFeed.getFeedbackDialog());
        holder.feedbackDate.setText(feedbackFeed.getDate());


        if(feedbackFeed.getPostPic()==null){
            holder.postImage.setVisibility(View.GONE);
        }
        else{
            holder.postImage.setVisibility(View.VISIBLE);
            glide.load(feedbackFeed.getPostPic()).into(holder.postImage);

        }


    }

    @Override
    public int getItemCount() {
        return feedbackFeedArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView feedbackName, feedbackDialog, projectTitle, feedbackDate;
        ImageView postImage;
        OnFeedbackListener onFeedbackListener;

        public MyViewHolder(@NonNull View itemView, OnFeedbackListener onfeedbackListener) {
            super(itemView);


            feedbackName = itemView.findViewById(R.id.feedbackUser);
            feedbackDialog = itemView.findViewById(R.id.feedbackDialog);
            projectTitle = itemView.findViewById(R.id.feedbackProjectTitle);
            feedbackDate = itemView.findViewById(R.id.feedbackDateTime);
            postImage = itemView.findViewById(R.id.feedbackProjectThumbnail);



            this.onFeedbackListener = onFeedbackListener;

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            onFeedbackListener.onProjectClick(getAdapterPosition());

        }
    }

    public interface OnFeedbackListener {
        void onProjectClick(int position);

    }
}
