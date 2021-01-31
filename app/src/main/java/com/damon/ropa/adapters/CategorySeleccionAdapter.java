package com.damon.ropa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.holder.SeleccionCategoryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CategorySeleccionAdapter  extends RecyclerView.Adapter<SeleccionCategoryViewHolder> {

    List<String> list = new ArrayList<>();

    Context context;

    String categoria;

    public CategorySeleccionAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SeleccionCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_category,parent,false);
        return new SeleccionCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeleccionCategoryViewHolder holder, int position) {
        holder.radioButton.setText(list.get(position));
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = holder.radioButton.getText().toString();
            }
        });
    }

    public String getCategoria(){
        return categoria;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
