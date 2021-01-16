package com.damon.ropa.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.activitys.DetailsProduct;
import com.damon.ropa.holder.StaggeredProductCardViewHolder;
import com.damon.ropa.models.ProductEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StaggeredProductCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredProductCardViewHolder> {

    private List<ProductEntry> productList;
    private Activity context;

    public StaggeredProductCardRecyclerViewAdapter(List<ProductEntry> productList,Activity context) {
        this.productList = productList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }
    int layoutId;
    @NonNull
    @Override
    public StaggeredProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1) {
            layoutId = R.layout.shr_staggered_product_card_second;
        } else if (viewType == 2) {
            layoutId = R.layout.shr_staggered_product_card_third;
        }else {
             layoutId = R.layout.shr_staggered_product_card_first;

        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StaggeredProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredProductCardViewHolder holder, int position) {
        System.out.println(productList.get(position).getUrl());

            ProductEntry product = productList.get(position);
            holder.productTitle.setText(product.title);
            holder.productPrice.setText("$"+product.price);

            Picasso.get().load(product.url.get(position).getUrl()).resize(250,250).into(holder.productImage);

            holder.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(productList.get(position));
                    Intent intent = new Intent(context, DetailsProduct.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product",product);
                    intent.putExtras(bundle);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context,holder.productImage, ViewCompat.getTransitionName(holder.productImage));
                    context.startActivity(intent,compat.toBundle());
                }
            });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}