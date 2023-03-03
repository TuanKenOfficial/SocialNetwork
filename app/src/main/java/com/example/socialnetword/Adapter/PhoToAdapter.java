package com.example.socialnetword.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetword.Fragment.PostDetailFragment;
import com.example.socialnetword.Model.Posts;
import com.example.socialnetword.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhoToAdapter extends RecyclerView.Adapter<PhoToAdapter.ViewHolder> {

    private Context context;
    private List<Posts> mPosts;

    public PhoToAdapter(Context mContext, List<Posts> mPosts) {
        this.context = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Posts post = mPosts.get(position);

        /* Picasso thư viện hổ trợ hình ảnh , load hình ảnh mục profile */
        Picasso.get().load(post.getImage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.post_image);

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",post.getPostid()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
