package com.example.neobrain.Adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.Corona;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoronaAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Corona> mCoronaList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public CoronaAdapter(List<Corona> mCoronaList) {
        this.mCoronaList = mCoronaList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CoronaAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_corona, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new CoronaAdapter.EmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_corona_empty, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mCoronaList != null && mCoronaList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mCoronaList != null && mCoronaList.size() > 0) {
            return mCoronaList.size();
        } else {
            return 1;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.flag)
        ImageView flag;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.all_admitted)
        TextView all_admitted;
        @BindView(R.id.new_admitted)
        TextView new_admitted;
        @BindView(R.id.deaths)
        TextView deaths;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            flag.setImageDrawable(null);
            name.setText("");
            all_admitted.setText("");
            new_admitted.setText("");
            deaths.setText("");
        }

        @SuppressLint("ResourceType")
        public void onBind(int position) {
            super.onBind(position);
            final Corona mCorona = mCoronaList.get(position);

            Call<Corona> corona = DataManager.getInstance().getOneCoronaCountry(mCorona.getId());
            corona.enqueue(new Callback<Corona>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<Corona> call, @NotNull Response<Corona> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        name.setText(response.body().getName());
                        all_admitted.setText(response.body().getAll_admitted());
                        new_admitted.setText("+" + response.body().getNew_admitted());
                        deaths.setText(response.body().getAll_deaths());
                        flag.setImageURI(Uri.parse(response.body().getUri()));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Corona> call, @NotNull Throwable t) {
                }
            });
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

}
