package com.itschool.neobrain.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itschool.neobrain.API.models.Corona;
import com.itschool.neobrain.API.models.CoronaModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.utils.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Адаптер для стран по коронавирусу */
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
                        .inflate(R.layout.recycler_view_empty_item_corona, parent, false));
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
        TextView allAdmitted;
        @BindView(R.id.new_admitted)
        TextView newAdmitted;
        @BindView(R.id.deaths)
        TextView deaths;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            flag.setImageDrawable(null);
            name.setText("");
            allAdmitted.setText("");
            newAdmitted.setText("");
            deaths.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Corona mCorona = mCoronaList.get(position);

            Call<CoronaModel> coronaCall = DataManager.getInstance().getOneCoronaCountry(mCorona.getId());
            coronaCall.enqueue(new Callback<CoronaModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<CoronaModel> call, @NotNull Response<CoronaModel> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Corona corona = response.body().getCorona();
                        name.setText(corona.getName());
                        allAdmitted.setText(corona.getAll_admitted() + "");
                        newAdmitted.setText("+" + corona.getNew_admitted());
                        deaths.setText(corona.getAll_deaths() + "");
                        Picasso.get().load(corona.getUri()).into(flag);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CoronaModel> call, @NotNull Throwable t) {
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
