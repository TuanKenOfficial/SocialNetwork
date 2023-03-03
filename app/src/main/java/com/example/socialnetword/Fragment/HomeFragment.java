package com.example.socialnetword.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetword.Adapter.PostsAdapter;
import com.example.socialnetword.Adapter.StoryAdapter;
import com.example.socialnetword.Model.Posts;
import com.example.socialnetword.Model.Story;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;

    RecyclerView recyclerView;
    List<Posts> mPosts;
    private List<String> followingList; // phần kết bạn
    PostsAdapter postsAdapter;
    Toolbar toolbar;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private  List<Story> storyList;

    ProgressBar progressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //auth
        mAuth = FirebaseAuth.getInstance();
        //recycleview
        recyclerView = view.findViewById(R.id.postRecyclerView);
        progressBar = view.findViewById(R.id.progress_circular);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show post đầu tiên
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //set layout to recycleview
        recyclerView.setLayoutManager(linearLayoutManager);
        //danh sách post
        mPosts = new ArrayList<>();

        //story
        recyclerView_story = view.findViewById(R.id.recycle_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);
       //phần load ảnh
        /*
        * Nếu muốn load ảnh bài đăng người dùng khác
        * Và xem story người dùng đó
        * Mà ko cần phải kết bạn trước thì mở cái này lên*/
//        loadPost();// nếu muốn ko cần kết bạn vẫn xem ảnh thì mở nó lên
//        readStory();
        checkFollow();


        return view;


    }

    private void checkFollow() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                /*Khi không muốn dùng nó thì đóng lại
                Phải đồng ý kết bạn rồi mới có thể xem ảnh và story người dùng */
                loadPost();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Posts posts = ds.getValue(Posts.class);
//                    mPosts.add(posts);
                    /*nếu như mình follow người dùng đó thì mới thấy bài đăng, còn ko muốn dùng
                    * Thì đóng dòng for bên dưới mở cái mPosts.add(posts) phía trên lên*/
                    for (String id : followingList){
                        if (posts.getPublisher().equals(id)){
                            mPosts.add(posts);
                        }
                    }
                    //adapter
                    postsAdapter = new PostsAdapter(getActivity(),mPosts);
                    recyclerView.setAdapter(postsAdapter);
                    postsAdapter.notifyDataSetChanged(); // thêm vào cùng vs dòng 122
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList){
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot snapshot : datasnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
