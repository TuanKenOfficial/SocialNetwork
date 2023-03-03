package com.example.socialnetword.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.Fragment.PostDetailFragment;
import com.example.socialnetword.Fragment.ProfileFragment;
import com.example.socialnetword.Model.Notification;
import com.example.socialnetword.Model.Posts;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.mContext = context;
        this.mNotification = notificationList;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_notification, parent ,false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Notification notification = mNotification.get(position);
        viewHolder.text.setText(notification.getText());
        viewHolder.txt_date.setText(notification.getDate());
        viewHolder.txt_time.setText(notification.getTime());
        getUser(viewHolder.image_profile, viewHolder.username, notification.getUserid());
        if (notification.isIspost()){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.post_image, notification.getPostid());
        }else {
            viewHolder.post_image.setVisibility(View.GONE);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()){
                    mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid", notification.getPostid()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();
                }else {
                    mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("profileid", notification.getUserid()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            }
        });


    }

    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile , post_image;
        public TextView username , text, txt_date, txt_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.avatarIv);
            username= itemView.findViewById(R.id.txt_username);
            text = itemView.findViewById(R.id.txt_notification);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            post_image = itemView.findViewById(R.id.post_image);

        }
    }
    private void getUser(final ImageView imageView ,final TextView username , String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Users user = datasnapshot.getValue(Users.class);
                Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.users).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPostImage(ImageView imageView , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Posts post = datasnapshot.getValue(Posts.class);
                Picasso.get().load(post.getImage()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
