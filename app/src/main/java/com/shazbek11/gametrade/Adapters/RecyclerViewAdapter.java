package com.shazbek11.gametrade.Adapters;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shazbek11.gametrade.Activities.MainActivity;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.VideoGamePost;
import com.shazbek11.gametrade.Activities.profilevisit;
import com.shazbek11.gametrade.utils.SquareImageView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    ArrayList<VideoGamePost> VideoGame ;
    private Context mContext;

    String toNumber;

    public RecyclerViewAdapter(ArrayList<VideoGamePost> videoGame, Context mContext) {
        VideoGame = videoGame;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imageupload, viewGroup, false));

        if (mContext instanceof MainActivity){
            viewHolder.recycler_Username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, profilevisit.class);
                    int position = viewHolder.getAdapterPosition();
                    intent.putExtra("username",VideoGame.get(position).getmUsername());
                    mContext.startActivity(intent);
                }
            });
            viewHolder.recycler_UserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, profilevisit.class);
                    int position = viewHolder.getAdapterPosition();
                    intent.putExtra("username",VideoGame.get(position).getmUsername());
                    mContext.startActivity(intent);
                }
            });
        }
        viewHolder.WAMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress;
                progress = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
                progress.setMessage("Please Wait");
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();

                int position = viewHolder.getAdapterPosition();
                String messagereciever = VideoGame.get(position).getmUsername();
                String getphonenumber = "http://shazbekgametrade.000webhostapp.com/getnumber.php/?user="+messagereciever;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, getphonenumber, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.cancel();
                        toNumber = "961"+response;
                        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                        if (isWhatsappInstalled) {

                            Intent sendIntent = new Intent("android.intent.action.MAIN");
                            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(toNumber) + "@s.whatsapp.net");

                            mContext.startActivity(sendIntent);
                        } else {
                            Uri uri = Uri.parse("market://details?id=com.whatsapp");
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            Toast.makeText(mContext, "WhatsApp not Installed",
                                    Toast.LENGTH_SHORT).show();
                            mContext.startActivity(goToMarket);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.cancel();
                        Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
                Volley.newRequestQueue(mContext).add(stringRequest);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!VideoGame.get(i).getmUserImage().equals("null")) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(VideoGame.get(i).getmUserImage())
                    .into(viewHolder.recycler_UserPhoto);
        }else{
            viewHolder.recycler_UserPhoto.setImageDrawable(mContext.getDrawable(R.drawable.avatar_1577909_1280));
        }
        Glide.with(mContext)
                .asBitmap()
                .load(VideoGame.get(i).getmUploadImage().trim())
                .into(viewHolder.recycler_UploadedImage);

        viewHolder.recycler_Username.setText(VideoGame.get(i).getmUsername());
        viewHolder.recycler_UserLocation.setText(VideoGame.get(i).getmLocation());
        viewHolder.recycler_GameName.setText(VideoGame.get(i).getmGamename());
        viewHolder.recycler_price.setText(VideoGame.get(i).getmPrice());
    }

    @Override
    public int getItemCount() {
        return VideoGame.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView recycler_UserPhoto;
        SquareImageView recycler_UploadedImage;
        TextView recycler_Username;
        TextView recycler_UserLocation;
        TextView recycler_GameName;
        TextView recycler_price;
        ImageButton WAMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            WAMessage = itemView.findViewById(R.id.messagebutton);
            recycler_UserPhoto = itemView.findViewById(R.id.user_photo);
            recycler_UploadedImage = itemView.findViewById(R.id.user_upload);
            recycler_Username = itemView.findViewById(R.id.user_username);
            recycler_GameName = itemView.findViewById(R.id.gamename);
            recycler_price = itemView.findViewById(R.id.price);
            recycler_UserLocation = itemView.findViewById(R.id.user_location);

        }
    }

    public void clear() {
        VideoGame.clear();
        notifyDataSetChanged();
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
