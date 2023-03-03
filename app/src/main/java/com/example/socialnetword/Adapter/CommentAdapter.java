package com.example.socialnetword.Adapter;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialnetword.MainActivity;
import com.example.socialnetword.Model.Comment;
import com.example.socialnetword.Model.Users;
import com.example.socialnetword.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    String postid;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment,String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);
        holder.comment.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Users user = datasnapshot.getValue(Users.class);
                holder.txt_username.setText(user.getUsername());
                holder.txt_quocgia.setText(user.getCountry());
                    Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.users).into(holder.pickcherTV);//load hình ảnh mục comment
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            //comment
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                intent.putExtra("country", comment.getCountry());
                mContext.startActivity(intent);
            }
        });
            //profile
//        holder.image_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MenuActivity.class);
//                intent.putExtra("publisher", comment.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });
            //delete comment
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comment.getPublisher().endsWith(firebaseUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Bạn có muốn xoá không");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postid)
                                    .child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Bình luận đã xoá thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            };
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView pickcherTV;
        public TextView txt_username, txt_quocgia, comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pickcherTV = itemView.findViewById(R.id.pickcherTV);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_quocgia = itemView.findViewById(R.id.txt_quocgia);
            comment = itemView.findViewById(R.id.comment);
        }
    }

}
