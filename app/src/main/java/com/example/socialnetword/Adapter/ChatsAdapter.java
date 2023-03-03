package com.example.socialnetword.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetword.Model.Chats;

import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private static final  int MSG_TYPE_LEFT = 0;
    private static final  int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chats> mChat;
    String imageUrl;
    String  imageUri = null;
    FirebaseUser fUser;
    private String saveCurrentData, saveCurrentTime, postName;



    public ChatsAdapter(Context context, List<Chats> mChat, String imageUrl) {
        this.context = context;
        this.mChat = mChat;
        this.imageUrl = imageUrl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new ViewHolder(view);

        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Chats chat = mChat.get(position);

//        holder.img_message.setImageURI(imageUri);
        holder.txt_message.setText(chat.getMessage());
        holder.txt_chat_time.setText(chat.getTime());
        holder.txt_chat_date.setText(chat.getDate());
        String type = chat.getType();



        //currentDate
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentData = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //currentTime
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());
        // phần name bằng 2 cái current cộng lại
        postName = saveCurrentData + saveCurrentTime;

//        if(chat.getTime()!=null && !chat.getTime().trim().equals("")) {
//            holder.txt_chat_time.setText(holder.convertTime(chat.getTime()));
//        }


        // đoạn hiển thị chữ đã xem và đã nhận
        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Đã xem");
            } else {
                holder.txt_seen.setText("Đã gửi");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
        //dòng này của hình ảnh
        if(type.equals("text")){
            holder.txt_message.setVisibility(View.VISIBLE);
            holder.img_message.setVisibility(View.GONE);

            holder.txt_message.setText(chat.getMessage());

        }else {
            holder.txt_message.setVisibility(View.GONE);
            holder.img_message.setVisibility(View.VISIBLE);

            Picasso.get().load(chat.getMessage()).placeholder(R.drawable.ic_image).into(holder.img_message);
        }


    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileTV,img_message;
        TextView txt_message, txt_chat_time,txt_chat_date, txt_seen;
        RelativeLayout messageLT;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            messageLT = itemView.findViewById(R.id.messageLT);
            profileTV= itemView.findViewById(R.id.profile_image);
            txt_message= itemView.findViewById(R.id.txt_message);
            txt_chat_time= itemView.findViewById(R.id.txt_chat_time);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_chat_date = itemView.findViewById(R.id.txt_chat_date);
            img_message = itemView.findViewById(R.id.img_message);



        }
//        public String convertTime(String time){
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
//            return formatter.format(new Date(Long.parseLong(time)));
//        }
    }
    @Override
    public int getItemViewType(int position) {
        //người dùng đã được đăng nhập thành công
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }
}
