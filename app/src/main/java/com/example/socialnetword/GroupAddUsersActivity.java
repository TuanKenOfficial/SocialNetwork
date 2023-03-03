package com.example.socialnetword;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.Adapter.GroupAddUsersAdapter;
import com.example.socialnetword.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupAddUsersActivity extends AppCompatActivity {
    private RecyclerView userRv;
    private FirebaseAuth firebaseAuth;
    private String groupId;
    private String myGroupRole="";
    private ArrayList<Users> usersList;
    private GroupAddUsersAdapter groupAddUsersAdapter;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ImageView close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_users);

        firebaseAuth = FirebaseAuth.getInstance();
        userRv = findViewById(R.id.userRv);
        toolbar = findViewById(R.id.toolbar);
        close = findViewById(R.id.close);
        //action bar
        actionBar=getSupportActionBar();
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        groupId = getIntent().getStringExtra("groupId"); // liên kết vs Group Chats Activity dòng 214

//        getAllUser(); // hiện tất cả user
        loadGroupInfo();




    }

    private void getAllUser() {

        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Users users = ds.getValue(Users.class);
                    //yêu cầu tất cả người dùng chấp nhận đăng nhập hiện tại
                    /*Của cái video là !firebaseAuth rồi mới tới users.getId(), này mình
                    đổi lại cho !user.getId()
                     */

                    if (!users.getUid().equals(firebaseAuth.getUid())){ // lỗi dòng này
                        usersList.add(users);
                    }
                }
                groupAddUsersAdapter = new GroupAddUsersAdapter(GroupAddUsersActivity.this, usersList,""+groupId,""+myGroupRole);
                userRv.setAdapter(groupAddUsersAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String groupId = ""+ds.child("groupId").getValue();
                    String groupDescription = ""+ds.child("groupDescription").getValue();
                    String groupIcon = ""+ds.child("groupIcon").getValue();
                    String groupTitle = ""+ds.child("groupTitle").getValue();
                    String timestamp = ""+ds.child("timestamp").getValue();

                    actionBar.setTitle("Add Participants");
                    reference1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        myGroupRole = ""+dataSnapshot.child("role").getValue();
                                        actionBar.setTitle(groupTitle + "("+myGroupRole+")");
                                        getAllUser();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}