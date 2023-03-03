package com.example.socialnetword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class PostDetailActivity extends AppCompatActivity {

    //view
    ImageView pickcherTV,Avatar,like,share,save,post_image;
    TextView txt_username,txt_quocgia,txt_post_date,txt_post_time,txt_description,txt_like,comments;
    ImageButton btn_more,btn_send;
    EditText edt_comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chi tiết bài đăng");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        post_image= findViewById(R.id.post_image);
        pickcherTV = findViewById(R.id.pickcherTV);
        Avatar = findViewById(R.id.Avatar);
        like = findViewById(R.id.like);
        share = findViewById(R.id.share);
        save = findViewById(R.id.save);
        txt_username = findViewById(R.id.txt_username);
        txt_quocgia = findViewById(R.id.txt_quocgia);
        txt_post_date = findViewById(R.id.txt_post_date);
        txt_post_time = findViewById(R.id.txt_post_time);
        txt_description = findViewById(R.id.txt_description);
        txt_like = findViewById(R.id.txt_like);
        comments = findViewById(R.id.comments);
        btn_more = findViewById(R.id.btn_more);
        btn_send = findViewById(R.id.btn_send);
        edt_comment = findViewById(R.id.edt_comment);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = txt_description.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) post_image.getDrawable();
                if (bitmapDrawable == null){
                    shareTextOnly(description);
                }
                else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(description,bitmap);
                }
            }
        });
    }

    //share
    private void shareTextOnly(String description) {
        String shareBody = description;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Chủ đề ở đây");
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private void shareImageAndText(String description, Bitmap bitmap) {
        String shareBody = description;
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Chủ để ở đây");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Chia sẻ qua"));

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this,"com.example.socialnetword.fileprovider",file);
        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}