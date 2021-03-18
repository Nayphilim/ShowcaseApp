package com.nayphilim.showcaseapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class SearchFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView searchUserName, searchLocation, searchCategory;
    AdapterSearchFeed.OnProfileListener onProfileListener;

    public SearchFeedViewHolder(@NonNull View itemView) {
        super(itemView);

        searchUserName = itemView.findViewById(R.id.searchUser);
        searchLocation = itemView.findViewById(R.id.searchCountry);
        searchCategory = itemView.findViewById(R.id.searchSpecialization);


        this.onProfileListener = onProfileListener;

        itemView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        onProfileListener.onProfileClick(getAdapterPosition());

    }

    public void setDetails(String userName,String location,String category){
        searchUserName.setText(userName);
    }
}