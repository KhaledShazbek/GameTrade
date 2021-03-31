package com.shazbek11.gametrade.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.ProfileGamePost;
import com.shazbek11.gametrade.utils.SquareImageView;


import java.util.ArrayList;


public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder> {


    ArrayList<ProfileGamePost> VideoGame ;
    private Context mContext;

    public ProfileRecyclerAdapter(ArrayList<ProfileGamePost> videoGame, Context mContext) {
        VideoGame = videoGame;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_profile_posts, viewGroup, false));


        final Resources res = viewHolder.itemView.getContext().getResources();
        viewHolder.profilerecycler_deletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ExitMessage = new AlertDialog.Builder(mContext);
                ExitMessage.setMessage("Are you sure you want to Delete this post?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "http://shazbekgametrade.000webhostapp.com/deletepost.php?ID="+VideoGame.get(i).getmID();
                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("DELETED")){
                                            notifyItemRemoved(i);
                                            Toast toast = Toast.makeText(mContext, "Post Deleted!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }else{
                                            Toast errortoast = Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT);
                                            errortoast.show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast toast = Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                queue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = ExitMessage.create();
                dialog.show();
                Button positivebutton;
                positivebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positivebutton.setTextColor(res.getColor(R.color.indigo));
                Button negativebuutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativebuutton.setTextColor(res.getColor(R.color.indigo));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Glide.with(mContext)
                .asBitmap()
                .load(VideoGame.get(i).getmUploadImage())
                .into(viewHolder.profilerecycler_UploadedImage);
        viewHolder.profilerecycler_GameName.setText(VideoGame.get(i).getmGamename());
        viewHolder.profilerecycler_price.setText(VideoGame.get(i).getmPrice());
    }

    @Override
    public int getItemCount() {
        return VideoGame.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView profilerecycler_UploadedImage;
        TextView profilerecycler_GameName;
        TextView profilerecycler_price;
        ImageButton profilerecycler_deletepost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilerecycler_UploadedImage = itemView.findViewById(R.id.profile_post);
            profilerecycler_GameName = itemView.findViewById(R.id.profile_gamename);
            profilerecycler_price = itemView.findViewById(R.id.profile_price);
            profilerecycler_deletepost = itemView.findViewById(R.id.delete_post);

        }
    }
}


