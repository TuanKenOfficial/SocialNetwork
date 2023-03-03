package com.example.socialnetword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {

    private StorageReference Postreference;
    private Uri imageUri;
    private String imageUrl = "" ;
    StorageTask uploadTask;




    private Toolbar toolbar;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private ImageView groupIconIv,close;
    private EditText edt_grouptitle, edt_groupdescription;
    private FloatingActionButton createGroup;

    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        //action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Tạo nhóm");

//        toolbar = findViewById(R.id.toolbar);
//        close = findViewById(R.id.close);
        groupIconIv = findViewById(R.id.groupIconIv);
        edt_grouptitle = findViewById(R.id.edt_grouptitle);
        edt_groupdescription = findViewById(R.id.edt_groupdescription);
        createGroup = findViewById(R.id.createGroup);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();



        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateGroup();

            }
        });
    }

    private void startCreateGroup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tạo nhóm");

        String groupTitle = edt_grouptitle.getText().toString().trim();
        String groupDescription = edt_groupdescription.getText().toString().trim();

        if(TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Vui lòng điền tên nhóm...", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String g_timestamp = ""+System.currentTimeMillis();
        if (imageUri == null){
            createGroups(""+g_timestamp,""+groupTitle,
                    ""+groupDescription,"");
        }else {
            String fileNameAndPath = "Group_Imgs/"+"image"+g_timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPath);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!p_uriTask.isSuccessful());
                            Uri downloadUri = p_uriTask.getResult();
                            if(p_uriTask.isSuccessful()){
                                createGroups(""+g_timestamp,""+groupTitle,
                                        ""+groupDescription,""+downloadUri);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createGroups(String g_timestamp, String groupTitle, String groupDescription, String groupIcon) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId",""+g_timestamp);
        hashMap.put("groupTitle",""+groupTitle);
        hashMap.put("groupDescription",""+groupDescription);
        hashMap.put("groupIcon",""+groupIcon);
        hashMap.put("timestamp",""+g_timestamp);
        hashMap.put("createdBy",""+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(g_timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("uid", firebaseAuth.getUid());
                hashMap1.put("role","creator");
                hashMap1.put("timestamp",g_timestamp);

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                reference1.child(g_timestamp).child("Participants").child(firebaseAuth.getUid())
                        .setValue(hashMap1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateGroupActivity.this,"Tạo group thành công", Toast.LENGTH_SHORT).show();
                                SendToGroupChat();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void SendToGroupChat() {
        Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showImagePickDialog() {
        CropImage.activity().setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(CreateGroupActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            groupIconIv.setImageURI(imageUri);


        } else {
            Toast.makeText(this, "Hình ảnh chưa có, tôi đưa bạn về màn hình chính và thử lại",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void checkUser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            actionBar.setSubtitle(user.getEmail());
        }
    }

}