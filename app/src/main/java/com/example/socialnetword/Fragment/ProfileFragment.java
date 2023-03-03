package com.example.socialnetword.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetword.Adapter.PhoToAdapter;
import com.example.socialnetword.EditProfileActivity;
import com.example.socialnetword.FollowsActivity;
import com.example.socialnetword.Model.Posts;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //display post
    RecyclerView recyclerView;
    PhoToAdapter phoToAdapter;
    List<Posts> postList;

    ImageView imageprofile;
    TextView posts,txt_username, txt_fullname, txt_quocgia, txt_email, followers, following;
    Button edit_profile;


    FirebaseUser firebaseUser;
    String profileid;
    ImageButton my_photo, saved_photo;
    Toolbar toolbar; //toolbar

    //save
    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    PhoToAdapter phoToAdapter_saves;
    List<Posts> postList_saves;
    



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // phần follower and followings
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid =  prefs.getString("profileid", "none");



        //views
        imageprofile = view.findViewById(R.id.imageprofile);
        txt_username = view.findViewById(R.id.txt_username);
        txt_fullname = view.findViewById(R.id.txt_fullname);
        txt_quocgia = view.findViewById(R.id.txt_quocgia);
        txt_email = view.findViewById(R.id.txt_email);
        posts = view.findViewById(R.id.posts);

        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        my_photo = view.findViewById(R.id.my_photo);
        saved_photo = view.findViewById(R.id.saved_photo);
        edit_profile = view.findViewById(R.id.edit_profile);
        toolbar = view.findViewById(R.id.toolbar);

        //profile
        recyclerView = view.findViewById(R.id.recycle_view_pitcher);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        phoToAdapter = new PhoToAdapter(getContext(),postList);
        recyclerView.setAdapter(phoToAdapter);

        //save
        recyclerView_saves = view.findViewById(R.id.recycle_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        phoToAdapter_saves = new PhoToAdapter(getContext(),postList_saves);
        recyclerView_saves.setAdapter(phoToAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);



        userInfo();
        getFollower(); //followers
        getFollowing(); //following
        getPosts(); // hiện số lượng ảnh
        MyPhoto(); // hiện ảnh đã đăng
        mysaves(); // save

        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit");
        }else {
            checkFollow();
            saved_photo.setVisibility(View.GONE);
        }



        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();
                if (btn.equals("Edit")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                }else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        //hình ảnh
        my_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });
        //save hình ảnh
        saved_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

          /* Load followers , following từ FollowersActivity*/
        //followers
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowsActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        //following
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowsActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });
        /*
        * chúng tôi phải lấy thông tin về người dùng đã đăng nhập.
        * chúng tôi có thể lấy nó bằng cách sử dụng email hoặc uid của người dùng.
        * Tôi gona truy xuất chi tiết người dùng bằng email
        * Bằng cách sử dụng truy vấn orderBychild,
        * chúng tôi sẽ hiển thị chi tiết từ một nút có khóa có tên email có giá
         trị bằng với email hiện đã đăng nhập. Nó sẽ tìm kiếm tất cả các nút,
         nơi khóa khớp với nó sẽ nhận được thông tin chi tiết của nó */

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //kiểm tra tập dữ liệu bắt buộc
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String username = "" + ds.child("username").getValue();
                    String country = "" + ds.child("country").getValue();
                    String fullname = "" + ds.child("fullname").getValue();
                    String image = "" + ds.child("profileimage").getValue();
                    String email = "" + ds.child("email").getValue();
                    //set data
                    txt_username.setText(username);
                    txt_fullname.setText(fullname);
                    txt_quocgia.setText(country);
                    txt_email.setText(email);
                    try {
                        Picasso.get().load(image).into(imageprofile);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.profile).into(imageprofile);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }

    private void mysaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    Posts post = snapshot.getValue(Posts.class);

                    for (String id : mySaves){
                        if (post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                phoToAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(profileid).exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // số lượng ảnh đăng
    private void getPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    Posts post = snapshot.getValue(Posts.class);
                    if (post.getPublisher().equals(profileid))
                        counter++;
                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // thêm bạn bè
    private void getFollower(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                followers.setText(""+datasnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //huỷ kết bạn
    private void getFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                following.setText(""+datasnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // hiện hình bên profile
    private void MyPhoto(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    Posts post = snapshot.getValue(Posts.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                phoToAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                Users users = dataSnapshot.getValue(Users.class);
                Picasso.get().load(users.getProfileimage()).placeholder(R.drawable.users).into(imageprofile);
                txt_username.setText(users.getUsername());
                txt_quocgia.setText(users.getCountry());
                txt_fullname.setText(users.getFullname());
                txt_email.setText(users.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}