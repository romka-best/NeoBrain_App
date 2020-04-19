package com.example.neobrain.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.Music;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "MusicAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Music> mMusicList;

    public MusicAdapter(List<Music> musicList) {
        mMusicList = musicList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_music, parent, false)
                );
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item_music, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mMusicList != null && mMusicList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mMusicList != null && mMusicList.size() > 0) {
            return mMusicList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Music> musicList) {
        mMusicList.addAll(musicList);
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
        @BindView(R.id.titleMusic)
        TextView titleMusicTextView;
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
