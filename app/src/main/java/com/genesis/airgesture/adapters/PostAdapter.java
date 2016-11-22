package com.genesis.airgesture.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.genesis.airgesture.R;
import com.squareup.picasso.Picasso;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by slaure on 21/11/2016.
 */

public class PostAdapter extends RecyclerView.Adapter {

    private ArrayList<PhotoPost> posts;

    public PostAdapter(ArrayList<PhotoPost> posts){
        super();
        this.posts = posts;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_post_cell, parent, false);

        PostHolder ph = new PostHolder(v);
        v.setBackgroundColor(v.getContext().getColor(android.R.color.transparent));
        ph.ivPhoto = (ImageView) v.findViewById(R.id.photo_post_iv);
        ph.tvTitle = (TextView)  v.findViewById(R.id.photo_post_tv_title);
        v.setTag(ph);
        return ph;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PostHolder realHolder = (PostHolder)holder;
        PhotoPost post = posts.get(position);
        Context context = realHolder.viewPost.getContext();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height =(int) context.getResources().getDimension(R.dimen.photo_height);

        Picasso.with(context).load(post.getPhotos().get(0).getOriginalSize().getUrl()).resize(width, height).into(realHolder.ivPhoto);
        realHolder.tvTitle.setText("title : " + position);


    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    public static class PostHolder extends  RecyclerView.ViewHolder{
        public View viewPost;
        public ImageView ivPhoto;
        public TextView tvTitle;

        public PostHolder(View itemView) {
            super(itemView);
            viewPost = itemView;
        }
    }
}
