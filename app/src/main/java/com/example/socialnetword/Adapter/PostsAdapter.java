package com.example.socialnetword.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.BaoCaoActivity;
import com.example.socialnetword.CommentActivity;
import com.example.socialnetword.FollowsActivity;
import com.example.socialnetword.Fragment.PostDetailFragment;
import com.example.socialnetword.Fragment.ProfileFragment;
import com.example.socialnetword.Model.Posts;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context mContext;
    private List<Posts> mPosts;


    private String saveCurrentData, saveCurrentTime, postName;
    FirebaseUser firebaseUser;
    private DatabaseReference UsersRef;
    FirebaseAuth mAuth;

    private DatabaseReference LikeRef;
    private DatabaseReference PostRef;
    String uid;
    boolean mProcessLike = false;



    public PostsAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.post_user,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //khai báo firebaseUser
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // khai báo Posts bằng posts get nó bằng position để chuyền view
        Posts posts = mPosts.get(position);
        //khai báo lớp string cho nó
        String uid = mPosts.get(position).getUid();
        String postid = mPosts.get(position).getPostid();
        String image = mPosts.get(position).getImage();
        String publisher = mPosts.get(position).getPublisher();
        String date = mPosts.get(position).getDate();
        String time = mPosts.get(position).getTime();
        String description = mPosts.get(position).getDescription();
        String country = mPosts.get(position).getCountry();
        String liked = mPosts.get(position).getLike();

        Picasso.get().load(posts.getImage()).placeholder(R.mipmap.ic_launcher).into(holder.post_image);

        FirebaseDatabase.getInstance().getReference("Users").child(posts.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Users user = datasnapshot.getValue(Users.class);
                //load hình ảnh user ở mục home
                Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.profile).into(holder.pickcherTV);
                holder.txt_username.setText(user.getUsername());
                holder.txt_quocgia.setText(user.getCountry());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* thời gian
        *currentDate
        *currentTime  */
        //currentDate
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentData = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //currentTime
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());
        postName = saveCurrentData+saveCurrentTime;

        //set data
        holder.txt_username.setText(publisher);
        holder.txt_post_date.setText(date);
        holder.txt_post_time.setText(time);
        holder.txt_description.setText(description);
        holder.txt_quocgia.setText(country);
        

        //like
        Liked(posts.getPostid(), holder.img_like);
        notlikes(holder.txt_like, posts.getPostid());

        //comment
        getComments(posts.getPostid(), holder.comments);

        //save
        isSaved(posts.getPostid(), holder.img_save);
        // imageview like
        holder.img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.img_like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    //tạo thông báo
                    addNotification(posts.getPostid(), posts.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }

            }
        });
        // imageview comment
        holder.img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisher", posts.getPublisher());
                intent.putExtra("country", posts.getCountry());
                mContext.startActivity(intent);

//                Intent intent = new Intent(mContext, PostDetailActivity.class);
//                intent.putExtra("postid", postid);
//                mContext.startActivity(intent);

            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisher", posts.getPublisher());
                intent.putExtra("country", posts.getCountry());
                mContext.startActivity(intent);
            }
        });
        // imageview share
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.post_image.getDrawable();
                if (bitmapDrawable == null){
                    shareTextOnly(description);
                }
                else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(description,bitmap);
                }
            }
        });
            // imageview save
        holder.img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //like
//                Toast.makeText(mContext,"Save", Toast.LENGTH_SHORT).show();
                if (holder.img_save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(posts.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(posts.getPostid()).removeValue();
                }
            }
        });


        /* xem likes , likes này bên FollowersActivity */
        holder.txt_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowsActivity.class);
                intent.putExtra("id", posts.getPostid());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });


        /* edit post */
        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(posts.getPostid());
                                return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(posts.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(mContext, "Xoá ảnh thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                return true;
                            case R.id.report:
                                Intent intent = new Intent(mContext, BaoCaoActivity.class);
                                intent.putExtra("postid", postid);  // Truyền một String
                                mContext.startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_edit);
                if (!posts.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.report).setVisible(true);
                }

                popupMenu.show();
            }
        });

        /* hiện tất cả thông tin người dùng như người dùng bấm vào ảnh, username, quốc gia*/

//
        holder.txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("profileid", posts.getPublisher()).apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", posts.getPostid()).apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });
    }

    private void addNotification(String postid, String publisher) {
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
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", publisher);
        hashMap.put("text", "Đã thích ảnh");
        hashMap.put("postid", postid);
        hashMap.put("date", saveCurrentData);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }



    //share
    private void shareTextOnly(String description) {
        String shareBody = description;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bạn đang chia sẻ ảnh");
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        mContext.startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private void shareImageAndText(String description, Bitmap bitmap) {
        String shareBody = description;
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Bạn đang chia sẻ ảnh");
        intent.setType("image/png");
        mContext.startActivity(Intent.createChooser(intent, "Chia sẻ qua"));

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(),"images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext,"com.example.socialnetword.fileprovider",file);
        }catch (Exception e){
            Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }



    //Comment
    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                comments.setText("All  " + datasnapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //like
    private void Liked(String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //notlike
    private void notlikes(TextView img_like, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                img_like.setText(datasnapshot.getChildrenCount() + " like");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Save
    private void isSaved(final String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_saves);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    //edit post
    private void editPost(String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Sửa mô tả ảnh");

        EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);

        getText(postid,editText);
        alertDialog.setPositiveButton("Chỉnh sửa",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description",editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Huỷ bỏ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    //getText
    private void getText(String publisher , final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(publisher);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Posts.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return mPosts.size() ;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_username,txt_quocgia,txt_description,txt_post_date,txt_post_time , txt_like, comments;
        public ImageView post_image;
        public CircleImageView pickcherTV;
        public ImageButton btn_more;
        public ImageView img_like, img_comment, img_share, img_save;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_username = itemView.findViewById(R.id.txt_username);
            txt_quocgia = itemView.findViewById(R.id.txt_quocgia);
            txt_post_date = itemView.findViewById(R.id.txt_post_date);
            txt_post_time = itemView.findViewById(R.id.txt_post_time);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_like = itemView.findViewById(R.id.txt_like);
            pickcherTV = itemView.findViewById(R.id.pickcherTV);
            post_image = itemView.findViewById(R.id.post_image);
            btn_more = itemView.findViewById(R.id.btn_more);
            comments = itemView.findViewById(R.id.comments);
            img_like = itemView.findViewById(R.id.like);
            img_comment = itemView.findViewById(R.id.comment);
            img_share = itemView.findViewById(R.id.share);
            img_save = itemView.findViewById(R.id.save);
        }
    }
}
