package com.example.socialnetword.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.GroupChatsActivity;
import com.example.socialnetword.Model.GroupChatsList;
import com.example.socialnetword.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatListsAdapter extends RecyclerView.Adapter<GroupChatListsAdapter.ViewHolder> {

    private Context context;
    private List<GroupChatsList> groupChatsLists;

    public GroupChatListsAdapter(Context context, List<GroupChatsList> groupChatsLists) {
        this.context = context;
        this.groupChatsLists = groupChatsLists;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_group_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChatsList model = groupChatsLists.get(position);
        String groupId = model.getGroupId();
        String groupTitle = model.getGroupTitle();
        String groupDescription = model.getGroupDescription();
        String groupIcon = model.getGroupIcon();


        holder.nameTv.setText("");
        holder.timeTv.setText("");
        holder.messageTv.setText("");

        // load last message : tải tin nhắn cuối cùng
        loadLastMessage(model,holder);

        holder.txt_grouptitle.setText(groupTitle);

        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.users).into(holder.groupIconIv);
        }catch (Exception e){
            holder.groupIconIv.setImageResource(R.drawable.users);
        }

        //chuyển qua activity chats group
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatsActivity.class);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);
            }
        });


    }

    private void loadLastMessage(GroupChatsList model,ViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(model.getGroupId()).child("Messages").limitToLast(1)// nhận được tin nhắn cuối cùng
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String message = ""+ds.child("message").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String sender = ""+ds.child("sender").getValue();
                            String messageType = ""+ds.child("type").getValue();


                            // thời gian

                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(timestamp));
                            String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

                            if (messageType.equals("image")){
                                holder.messageTv.setText("Bạn đã gửi 1 ảnh vào groups chat");
                            }else {
                                holder.messageTv.setText(message);
                            }

                            // load message ra ngoài
                            holder.timeTv.setText(datetime); // load thời gian

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.orderByChild("uid").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String username = ""+ds.child("username").getValue();
                                                holder.nameTv.setText(username); // load username ra ngoài
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return groupChatsLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView groupIconIv;
        private TextView txt_grouptitle,nameTv,messageTv,timeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupIconIv = itemView.findViewById(R.id.groupIconIv);
            txt_grouptitle = itemView.findViewById(R.id.txt_grouptitle);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);

        }
    }
}
