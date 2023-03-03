package com.example.socialnetword.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetword.Adapter.ChatListAdapter;
import com.example.socialnetword.Model.ChatList;
import com.example.socialnetword.Model.Chats;
import com.example.socialnetword.Model.Users;

import com.example.socialnetword.Notifications.Token;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {


    FirebaseAuth    firebaseAuth;
    private RecyclerView recyclerView;

    private ChatListAdapter chatListAdapter;
    private List<Users> usersList;
    private List<ChatList> chatListList;
    DatabaseReference ref;
    FirebaseUser fUser;

    String myUid;


    public MessagesFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);


        //database + auth
        firebaseAuth= FirebaseAuth.getInstance();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        //recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        myUid = fUser.getUid();
        usersList = new ArrayList<>();
        chatListList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                   ChatList chatList = ds.getValue(ChatList.class);
                   chatListList.add(chatList);

                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // update Token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;

    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        reference.child(myUid).setValue(mToken);
    }

    private void readChat() {
        usersList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                //hiển thị 1 người dùng từ cuộc nói chuyện
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);
                    for (ChatList chatList: chatListList){
                        //báo lỗi phần chat dòng này
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())) {
                                usersList.add(user);
                                break;

                        }
                    }
                    chatListAdapter = new ChatListAdapter (getContext(), usersList,true, true);
                    recyclerView.setAdapter(chatListAdapter);

                    for (int i=0; i<usersList.size(); i++){
                        lastMessage(usersList.get(i).getUid());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chats chats = ds.getValue(Chats.class);

                    if (chats==null){
                        continue;
                    }
                    String sender = chats.getSender();
                    String receiver =  chats.getReceiver();
                    if (sender==null || receiver == null){
                        continue;
                    }
                    if (chats.getReceiver().equals(fUser.getUid()) && chats.getSender().equals(userId)
                    || chats.getReceiver().equals(userId)
                    && chats.getSender().equals(fUser.getUid())){
                        if (chats.getType().equals("image")){
                            theLastMessage = "Có 1 bức ảnh đã gửi đến";
                        }else {
                            theLastMessage = chats.getMessage();
                        }


                    }
                }
                chatListAdapter.setLast_msg(userId,theLastMessage);
                chatListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}