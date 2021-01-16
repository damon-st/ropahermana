package com.damon.ropa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.holder.ProductCardViewHolder;
import com.damon.ropa.models.ImagesList;
import com.damon.ropa.models.ProductEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter used to show a simple grid of products.
 */
public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private List<ProductEntry> productList;

    ProductCardRecyclerViewAdapter(List<ProductEntry> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shr_product_card, parent, false);
        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        if (productList != null && position < productList.size()) {
            ProductEntry product = productList.get(position);
            holder.productTitle.setText(product.title);
            holder.productPrice.setText("$"+product.price);
            for (ImagesList  url: product.getUrl()){

            }
            Picasso.get().load(product.url.get(position).getUrl()).into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}