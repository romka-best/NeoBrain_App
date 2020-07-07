package com.itschool.neobrain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itschool.neobrain.API.models.Video;
import com.itschool.neobrain.R;
import com.itschool.neobrain.utils.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "VideosAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Video> mVideoList;

    public VideosAdapter(ArrayList<Video> videoList) {
        this.mVideoList = videoList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_video, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_videos, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mVideoList != null && mVideoList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }


    @Override
    public int getItemCount() {
        if (mVideoList != null && mVideoList.size() > 0) {
            return mVideoList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Video> videoList) {
        mVideoList.addAll(videoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.titleVideo)
        TextView titleVideoTextView;
        @BindView(R.id.emoji)
        ImageView emojiImageView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
