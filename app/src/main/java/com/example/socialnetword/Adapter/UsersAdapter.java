package com.example.socialnetword.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.ChatsActivity;
import com.example.socialnetword.EditProfileActivity;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    Context context;
    List<Users> mUsers;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    String uid;

    private boolean isChat;


    public UsersAdapter(Context context, List<Users> mUsers,boolean isChat){
        this.context=context;
        this.mUsers=mUsers;
        this.isChat = isChat;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users users = mUsers.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        String hisUid = mUsers.get(position).getUid();
        String username = mUsers.get(position).getUsername();
        String profileimage=mUsers.get(position).getProfileimage();
        String country = mUsers.get(position).getCountry();






        //last message trong chats
        if (isChat){
//            lastMessage(users.getId(),holder.last_msg);
        }else {
//            holder.last_msg.setVisibility(View.GONE);
        }

        // online and offline trong chats
        if (isChat){
            if (users.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        //set data
        holder.txt_username.setText(username);
        holder.txt_quocgia.setText(country);
        try {
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(holder.imageUsers);
        }catch (Exception e){

        }




        // phần chat
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Edit Profile","Chat"},(dialog, which) -> {
                    if(which==0){
                        Intent intent = new Intent(context, EditProfileActivity.class);
                        context.startActivity(intent);
                    }
                    if (which==1){
                        Intent intent = new Intent(context, ChatsActivity.class);
                        intent.putExtra("hisUid", hisUid);
                        context.startActivity(intent);

                    }
                });
                builder.create().show();
//                Toast.makeText(context, ""+username, Toast.LENGTH_SHORT).show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageUsers ;
        TextView txt_username, txt_quocgia, last_msg;
        ImageView img_on , img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txt_username = itemView.findViewById(R.id.txt_username);
            txt_quocgia = itemView.findViewById(R.id.txt_quocgia);
            imageUsers = itemView.findViewById(R.id.imageUsers);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);


        }
    }
}
