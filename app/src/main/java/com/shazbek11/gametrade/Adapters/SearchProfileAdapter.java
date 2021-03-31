package com.shazbek11.gametrade.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shazbek11.gametrade.Activities.profilevisit;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SearchProfile;

import java.util.ArrayList;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ViewHolder> {

    public SearchProfileAdapter(ArrayList<SearchProfile> profileArray, Context mContext) {
        ProfileArray = profileArray;
        this.mContext = mContext;
    }

    ArrayList<SearchProfile> ProfileArray;
    private Context mContext;


    @NonNull
    @Override
    public SearchProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_profile, viewGroup, false));

        viewHolder.recycler_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, profilevisit.class);
                int position = viewHolder.getAdapterPosition();
                intent.putExtra("username", ProfileArray.get(position).getmUsername());
                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProfileAdapter.ViewHolder viewHolder, int i) {

        if (ProfileArray.get(i).getmUserProfilePic().equals("null")){
            viewHolder.recycler_UserPhoto.setImageDrawable(mContext.getDrawable(R.drawable.avatar_1577909_1280));
        }else{
            Glide.with(mContext)
                    .asBitmap()
                    .load(ProfileArray.get(i).getmUserProfilePic())
                    .into(viewHolder.recycler_UserPhoto);
        }
        viewHolder.recycler_Username.setText(ProfileArray.get(i).getmUsername());
        viewHolder.recycler_UserEmail.setText(ProfileArray.get(i).getmUserEmail());
        viewHolder.recycler_UserPhoneNumber.setText(ProfileArray.get(i).getmUserPhoneNumber());
        viewHolder.recycler_UserLocation.setText(ProfileArray.get(i).getmUserLocation());
    }

    @Override
    public int getItemCount() {
        return ProfileArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView recycler_UserPhoto;
        TextView recycler_Username;
        TextView recycler_UserLocation;
        TextView recycler_UserPhoneNumber;
        TextView recycler_UserEmail;
        CardView recycler_CardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recycler_CardView = itemView.findViewById(R.id.SP_cardview);
            recycler_UserPhoto = itemView.findViewById(R.id.SP_userpic);
            recycler_Username = itemView.findViewById(R.id.SP_username);
            recycler_UserEmail = itemView.findViewById(R.id.SP_useremail);
            recycler_UserLocation = itemView.findViewById(R.id.SP_userlocation);
            recycler_UserPhoneNumber = itemView.findViewById(R.id.SP_userphonenumber);
        }
    }

    public void clear() {
        ProfileArray.clear();
        notifyDataSetChanged();
    }
}
