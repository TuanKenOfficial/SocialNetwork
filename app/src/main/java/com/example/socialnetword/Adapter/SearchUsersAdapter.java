package com.example.socialnetword.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.ChatsActivity;
import com.example.socialnetword.EditProfileActivity;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {
    private Context mContext;
    private List<Users> mUsers;
    private String saveCurrentData, saveCurrentTime, postName;


    private FirebaseUser firebaseUser;
    private boolean isfragment;

    String uid;

    public SearchUsersAdapter(Context mContext, List<Users> mUsers,boolean isfragment ){
        this.mUsers= mUsers;
        this.mContext=mContext;
        this.isfragment=isfragment;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_users, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Users user = mUsers.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewHolder.btn.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.fullname.setText(user.getFullname());
        viewHolder.country.setText(user.getCountry());
         String userid = mUsers.get(position).getUid();//hisUID của chatactivity
        /*Picasso là thư viện tải hình ảnh
         //load hình ảnh user trong mục search */
        Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.users).into(viewHolder.imageUsers);


        isFollowed(user.getUid(), viewHolder.btn);
        if (user.getUid().equals(firebaseUser.getUid())) {
            viewHolder.btn.setVisibility(View.GONE);
        }
        //trong mục search khi chúng ta nhấp vào người dùng thì sẽ chuyển qua mục thông tin người dùng
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(new String[]{"Edit Profile","Chat"},(dialog, which) -> {
                    if(which==0){
                        Intent intent = new Intent(mContext, EditProfileActivity.class);
                        mContext.startActivity(intent);

                    }
                    if (which==1){
                        Intent intent = new Intent(mContext, ChatsActivity.class);
                        intent.putExtra("hisUid",user.getUid());
                        mContext.startActivity(intent);
                    }
                });
                builder.create().show();

            }
        });

        //follow and bỏ follow
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.btn.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    //tạo thông báo
                    addNotification(user.getUid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }

            }
        });

        /*trong mục search khi chúng ta nhấp vào người dùng thì sẽ chuyển qua mục thông tin người dùng
        * Đây là cách di chuyển giữa 2 fragment */
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isfragment) {
//                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("profileid", user.getId()).apply();
//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileFragment()).commit();
//
//                }else {
//                    Intent intent = new Intent(mContext, MenuActivity.class);
//                    intent.putExtra("publisherid",user.getId());
//                    mContext.startActivity(intent);
//                }
//            }
//        });



    }


    //Notification
    private void addNotification(String userid){

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userid",userid);
        hashMap.put("text", "Đã bắt đầu theo dõi");
        hashMap.put("postid", "");
        hashMap.put("date", saveCurrentData);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return  mUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageUsers;
        public TextView username;
        public TextView fullname, country;
        public Button btn;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imageUsers = itemView.findViewById(R.id.imageUsers);
            username = itemView.findViewById(R.id.txt_username);
            fullname = itemView.findViewById(R.id.txt_fullname);
            country = itemView.findViewById(R.id.txt_quocgia);
            btn = itemView.findViewById(R.id.btn);

        }
    }
    private void isFollowed(String id, Button btn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists())
                    btn.setText("following");
                else
                    btn.setText("follow");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
