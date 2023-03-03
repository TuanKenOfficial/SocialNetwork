package com.example.socialnetword;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.Adapter.GroupChatsAdapter;
import com.example.socialnetword.Model.GroupChat;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupChatsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;


    private String groupId,myGroupRole="";

    private ActionBar actionBar;
    private Toolbar toolbar;
    private ImageView groupIconIv,close;
    private ImageButton attachBtn,btn_send;
    private TextView txt_grouptitle;
    private EditText messageTv;
    private RecyclerView chatRv;
    private RelativeLayout relativeLayout;

    private List<GroupChat> groupChats;
    private GroupChatsAdapter groupChatsAdapter;

    // hình ảnh upload lên group
    // upload hình trong chat
    StorageReference chatsReference;
    private Uri imageUri;
    private String imageUrl = "" ;
    StorageTask uploadTask;
    private String saveCurrentDate, saveCurrentTime, postName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);



        toolbar = findViewById(R.id.toolbar);
        groupIconIv = findViewById(R.id.groupIconIv);
        attachBtn = findViewById(R.id.attachBtn);
        txt_grouptitle = findViewById(R.id.txt_grouptitle);
        messageTv = findViewById(R.id.messageTv);
        btn_send = findViewById(R.id.btn_send);
        chatRv = findViewById(R.id.chatRv);
        close = findViewById(R.id.close);
        close = findViewById(R.id.close);

        relativeLayout = findViewById(R.id.relativeLayout);

        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        //đẩy len csdl storage của firebase
        chatsReference = FirebaseStorage.getInstance().getReference("UploadImageGroupChats");



        firebaseAuth= FirebaseAuth.getInstance();

        loadGroupInfo();
        loadGroupMessages(); // liên quan Group Chats Adapter
        loadGroupRole(); // liên quan Group Participants Adapter



        //close quay về màn hình
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // button gửi tin nhắn
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageTv.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatsActivity.this,"Không thể gửi tin nhắn trống", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(GroupChatsActivity.this);
            }
        });


    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tải lên");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference filereference = chatsReference.child(System.currentTimeMillis()
                    +"."+ getFileExtension(imageUri));
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        String timestamp = ""+System.currentTimeMillis();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender",firebaseAuth.getUid());
                        hashMap.put("message", ""+myUrl);
                        hashMap.put("timestamp", ""+timestamp);
                        hashMap.put("type","" + "image");


                        reference.child("Groups").child(groupId).child("Messages").push().setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(GroupChatsActivity.this,"Gửi ảnh vào group chats thành công",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(GroupChatsActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
//                        progressDialog.dismiss();

                    }else {
                        Toast.makeText(GroupChatsActivity.this,"Xin thử lại",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GroupChatsActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this,"Không có hình ảnh nào được chọn",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        }else {
            Toast.makeText(this, "Lỗi!", Toast.LENGTH_SHORT ).show();
        }
    }

    // có liên quan GroupChatsAdapter
    private void loadGroupMessages() {
        groupChats = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    GroupChat model = ds.getValue(GroupChat.class);
                    groupChats.add(model);

                }
                groupChatsAdapter = new GroupChatsAdapter(GroupChatsActivity.this,groupChats);
                chatRv.setAdapter(groupChatsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // có liên quan Group Add User Adapter
    private void loadGroupRole() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        // nếu sửa chỗ orderByChild thành id thì nút button add người dùng không hiện trong chats
        reference.child(groupId).child("Participants").orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            invalidateOptionsMenu();//refresh menu items

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {
        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",""+firebaseAuth.getUid());
        hashMap.put("message",""+message);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("type",""+"text");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        messageTv.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChatsActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // hiện tên group
    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String groupTitle = ""+ds.child("groupTitle").getValue();
                            String groupDescription = ""+ds.child("groupDescription").getValue();
                            String groupIcon = ""+ds.child("groupIcon").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String createdBy = ""+ds.child("createdBy").getValue();




                            txt_grouptitle.setText(groupTitle);


                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.users).into(groupIconIv);
                            }
                            catch (Exception e){
                                groupIconIv.setImageResource(R.drawable.users);
                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    // liên kết với GroupAddUserActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options,menu);


        //các menu item này hiện vô phần chats group
        menu.findItem(R.id.item1).setVisible(false);
        menu.findItem(R.id.item2).setVisible(false);
        menu.findItem(R.id.item3).setVisible(false);
        menu.findItem(R.id.item4).setVisible(false);
        menu.findItem(R.id.item6).setVisible(true);
        menu.findItem(R.id.item7).setVisible(false);


        // lựu chọn options khi bấm vào người dùng mình có thể add và cho người dùng đó là quản trị viên
        if(myGroupRole.equals("creator")|| myGroupRole.equals("admin")){
            menu.findItem(R.id.item5).setVisible(true);
        }else {
            menu.findItem(R.id.item5).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item5){
            Intent intent = new Intent(this,GroupAddUsersActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        }
        else if (id == R.id.item6){
            Intent intent = new Intent(this,GroupInfoActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}