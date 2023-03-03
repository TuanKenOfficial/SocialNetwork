package com.example.socialnetword.Adapter;

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
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyHolder>{
    Context context;
    List<Users> usersList;
    private HashMap <String , String> last_msg;
    private boolean isChat;


    public ChatListAdapter(Context context, List<Users> usersList, boolean b,boolean isChat) {
        this.context = context;
        this.usersList = usersList;
        last_msg = new HashMap<>();
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);

        return new  MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getProfileimage();
        String username = usersList.get(position).getUsername();
        String lastMessage = last_msg.get(hisUid);

        holder.txt_username.setText(username); // hiện tên username

        // hiện tin nhắn
        if (lastMessage == null || lastMessage.equals("default")){
            holder.last_msg.setVisibility(View.GONE);
        }else {
            holder.last_msg.setVisibility(View.VISIBLE);
            holder.last_msg.setText(lastMessage);
        }

        // hiện hình ảnh profile
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.users).into(holder.imageUsers);
        }catch (Exception e){
            Picasso.get().load(R.drawable.users).into(holder.imageUsers);
        }

        if (usersList.get(position).getStatus().equals("online")){
            //online
            Picasso.get().load(R.drawable.circle_online).into(holder.img_on);

        }else {
            //offline
            Picasso.get().load(R.drawable.circle_offline).into(holder.img_off);
        }

        // online and offline trong chats
//        if (isChat){
//            if (usersList.get(position).getStatus().equals("online")){
//                holder.img_on.setVisibility(View.VISIBLE);
//                holder.img_off.setVisibility(View.GONE);
//            }else {
//                holder.img_on.setVisibility(View.GONE);
//                holder.img_off.setVisibility(View.VISIBLE);
//            }
//        }
//        else {
//            holder.img_on.setVisibility(View.GONE);
//            holder.img_off.setVisibility(View.GONE);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start chat activity with that user
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });



    }

    public  void setLast_msg(String userId, String lastMessage){
        last_msg.put(userId,lastMessage);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView imageUsers,img_off,img_on;
        TextView txt_username,last_msg;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageUsers = (ImageView) itemView.findViewById(R.id.imageUsers);
            img_on = (ImageView) itemView.findViewById(R.id.img_on);
            img_off = (ImageView) itemView.findViewById(R.id.img_off);
            txt_username = (TextView) itemView.findViewById(R.id.txt_username);
            last_msg = (TextView) itemView.findViewById(R.id.last_msg);
        }
    }
}
