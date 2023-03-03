package com.example.socialnetword.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.Adapter.GroupChatListsAdapter;
import com.example.socialnetword.CreateGroupActivity;
import com.example.socialnetword.Model.GroupChatsList;
import com.example.socialnetword.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupsFragment extends Fragment {
    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private List<GroupChatsList> groupChatsLists;
    private GroupChatListsAdapter groupChatListsAdapter;
    EditText search_bar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        groupsRv = view.findViewById(R.id.groupsRv);
        search_bar = view.findViewById(R.id.search_bar);


        firebaseAuth = FirebaseAuth.getInstance();


        //get Users
        getAllUsers();
        loadGroupChatsList(); // hiện người dùng và tin nhắn trên fragment nằm sau getAllUsers

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString()); // search theo username , fullname
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        

        return view;

    }

    private void loadGroupChatsList() {
        groupChatsLists = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatsLists.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        GroupChatsList model = ds.getValue(GroupChatsList.class);
                        groupChatsLists.add(model);


                    }
                }
                groupChatListsAdapter = new GroupChatListsAdapter(getActivity(),groupChatsLists);
                groupsRv.setAdapter(groupChatListsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // hiện tất cả thông tin người dùng
    private void getAllUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")){
                    groupChatsLists.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        GroupChatsList model = snapshot.getValue(GroupChatsList.class);
                        groupChatsLists.add(model);
                    }
                    groupChatListsAdapter = new GroupChatListsAdapter(getActivity(),groupChatsLists);
                    groupsRv.setAdapter(groupChatListsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
     * Phần dưới đây là search user bao gồm đọc user, get user*/


    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("groupTitle")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatsLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChatsList model = snapshot.getValue(GroupChatsList.class);
                    groupChatsLists.add(model);
                }
                groupChatListsAdapter.notifyDataSetChanged(); // báo lỗi dòng này bên GroupInfoActivity

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // menu trong fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);

        //search view
        menu.findItem(R.id.item4).setVisible(false);
        menu.findItem(R.id.item5).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item4){
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.item5){
//            Intent intent = new Intent(getActivity(), GroupAddUsersActivity.class);
//            startActivity(intent);
//        }
        return super.onOptionsItemSelected(item);
    }
}