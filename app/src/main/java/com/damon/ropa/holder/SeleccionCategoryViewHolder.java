package com.damon.ropa.holder;

import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;

public class SeleccionCategoryViewHolder  extends RecyclerView.ViewHolder {

    public RadioButton radioButton;
    public SeleccionCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        radioButton = itemView.findViewById(R.id.seleccion);
    }
}
