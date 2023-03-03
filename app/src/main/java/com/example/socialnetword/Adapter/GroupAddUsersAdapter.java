package com.example.socialnetword.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.MainActivity;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupAddUsersAdapter extends RecyclerView.Adapter<GroupAddUsersAdapter.ViewHolder>{

        private Context context;
        private ArrayList<Users> usersList;

        private String groupId, myGroupRole;


    public GroupAddUsersAdapter(Context context, ArrayList<Users> usersList, String groupId, String myGroupRole) {
        this.context = context;
        this.usersList = usersList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_participants_add, parent, false);
            return new  ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Users users = usersList.get(position);
            String username = users.getUsername();
            String country = users.getCountry();
            String image = users.getProfileimage();
            String uid = users.getUid();

            holder.txt_username.setText(username);
            holder.txt_quocgia.setText(country);


            try {
                Picasso.get().load(image).placeholder(R.drawable.users).into(holder.imageUsers);
            }
            catch (Exception e){
                holder.imageUsers.setImageResource(R.drawable.users);
            }


            checkIfAlreadyExits(users,holder);

            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });

            /*
            *Khi bấm vào group sẽ hiện người dùng và mình có thể add vào group
            * Có 2 lựa chọn đó là chọn người dùng đó làm quản trị viên
            * Xoá người dùng đó khỏi group
             */
            //handle click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(groupId).child("Participants").child(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){

                                        // user exits/participant
                                        String hisPreviousRole = ""+dataSnapshot.child("role").getValue();

                                        //options display
                                        String[] options;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Choose option");
                                        if(myGroupRole.equals("creator")){
                                            if(hisPreviousRole.equals("admin")){
                                                // im creator, he is admin
                                                options = new String[] {"Remove Admin", "Remove User"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // handle item click
                                                        if(which==0){
                                                            // remove admin
                                                            removeAdmin(users);
                                                        }else {
                                                            // remove user
                                                            removeUser(users);
                                                        }
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                            else if(hisPreviousRole.equals("participant")){
                                                options = new String[] {"Make Admin", "Remove User"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // handle item click
                                                        if(which==0){
                                                            // make admin
                                                            makeAdmin(users);
                                                        }else {
                                                            // remove user
                                                            removeUser(users);
                                                        }
                                                    }
                                                });
                                                builder.create().show();

                                            }
                                        }
                                        else if (myGroupRole.equals("amdin")){
                                            if (hisPreviousRole.equals("creator")){
                                                // im amdin, he is creator
                                                Toast.makeText(context, "Tạo nhóm", Toast.LENGTH_SHORT).show();
                                            }
                                            else if (hisPreviousRole.equals("admin")){
                                                //im admin, he is admin too
                                                options = new String[] {"Remove Admin", "Remove User"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // handle item click
                                                        if(which==0){
                                                            // remove admin
                                                            removeAdmin(users);
                                                        }else {
                                                            // remove user
                                                            removeUser(users);
                                                        }
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                            else if (hisPreviousRole.equals("participant")){
                                                options = new String[] {"Make Admin", "Remove User"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // handle item click
                                                        if(which==0){
                                                            // make admin
                                                            makeAdmin(users);
                                                        }else {
                                                            // remove user
                                                            removeUser(users);
                                                        }
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                        }
                                    }else {
                                        // user doesn't exits/not-participant: add
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Add Participant");
                                        builder.setMessage("Add this user in this group?");
                                        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    //add user
                                                addUsers(users);
                                            }
                                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });

        }


    /*
     *Add người dùng vào group
     * Cho người dùng làm quản trị viên
     * Xoá người dùng khỏi group
     * Xoá quyền người quản trị viên
     */
    private void addUsers(Users users) {

        // setup user data - add user in group
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",users.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);

        // add that user in Groups>groupId>Participants

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // added successfully
                        Toast.makeText(context,"Đưa người dùng vào group thành công",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdmin(Users users) {

        // setup data - change role
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role","admin");//role are: participant/admin/creator
        //update role in database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //make admin
                        Toast.makeText(context, "Người dùng là quản trị viên", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void removeUser(Users users) {
        //remove user from group
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //remove
                        Toast.makeText(context, "Người dùng đã được xoá", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeAdmin(Users users) {

        //setup data - remove admin - just change role
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role","participant");//role are: participant/admin/creator
        //update role in database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //remove
                        Toast.makeText(context, "Người dùng không còn là quản trị viên", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // từ khi làm group info activity bị lỗi dòng này
    private void checkIfAlreadyExits(Users users, ViewHolder holder) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.child(groupId).child("Participants").child(users.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String hisRole = ""+dataSnapshot.child("role").getValue();
                                holder.statusTv.setText(hisRole);
                            }else {
                                holder.statusTv.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView imageUsers,close;
            private TextView txt_username, txt_quocgia,statusTv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageUsers = itemView.findViewById(R.id.imageUsers);
                txt_username = itemView.findViewById(R.id.txt_username);
                txt_quocgia = itemView.findViewById(R.id.txt_quocgia);
                statusTv = itemView.findViewById(R.id.statusTv);
                close = itemView.findViewById(R.id.close);
            }
    }
}
