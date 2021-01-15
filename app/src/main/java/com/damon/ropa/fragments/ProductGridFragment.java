package com.damon.ropa.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.damon.ropa.R;
import com.damon.ropa.activitys.LoginActivity;
import com.damon.ropa.adapters.StaggeredProductCardRecyclerViewAdapter;
import com.damon.ropa.models.ProductEntry;
import com.damon.ropa.utils.NavigationIconClickListener;
import com.damon.ropa.utils.ProductGridItemDecoration;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductGridFragment extends Fragment {

    DatabaseReference reference;
    MaterialButton btn_mujer,btn_hombre,btn_cuenta;
    String typo = "Mujer";
    ArrayList<ProductEntry> productEntryArrayList;
    StaggeredProductCardRecyclerViewAdapter adapter;
    private final AnimatorSet animatorSet = new AnimatorSet();

    private int height;
     int translateY;

    private boolean backdropShown = false;
    private Interpolator interpolator;
    private NestedScrollView scrollView;
    private Drawable openIcon;
    private Drawable closeIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.shr_product_grid_fragment, container, false);

        // Set up the tool bar
        setUpToolbar(view);
        productEntryArrayList= new ArrayList<>();
        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        try {
            recyclerView.setHasFixedSize(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 2 ? 2 : 1;
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Ropa");
        getProducsT(typo);
        recyclerView.setLayoutManager(gridLayoutManager);
         adapter = new StaggeredProductCardRecyclerViewAdapter(productEntryArrayList,getActivity());
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_large);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_small);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid).setBackground(getContext().getDrawable(R.drawable.shr_product_grid_background_shape));
        }

        btn_cuenta = view.findViewById(R.id.cuenta);
        btn_mujer = view.findViewById(R.id.mujer);
        btn_hombre = view.findViewById(R.id.hombre);
        scrollView = view.findViewById(R.id.product_grid);


        closeIcon =  getContext().getResources().getDrawable(R.drawable.shr_close_menu);
        openIcon =  getContext().getResources().getDrawable(R.drawable.shr_branded_menu);
        interpolator = new AccelerateDecelerateInterpolator();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;

        translateY = height -
                getContext().getResources().getDimensionPixelSize(R.dimen.shr_product_grid_reveal_height);

        btn_mujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOrNoMenu();
                typo="Mujer";
                getProducsT(typo);

            }
        });

        btn_hombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOrNoMenu();
                typo="Hombre";
                getProducsT(typo);
            }
        });

        btn_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }


     void   closeOrNoMenu(){
        backdropShown = !backdropShown;
        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();
        updateIcon(toolbar);
         ObjectAnimator animator = ObjectAnimator.ofFloat(scrollView
                 , "translationY", backdropShown ? translateY : 0);

         animator.setDuration(500);
         if (interpolator != null) {
             animator.setInterpolator(interpolator);
         }
         animatorSet.play(animator);
         animator.start();
    }


    void getProducsT(String  typo){
        if (productEntryArrayList.size()>0){
            productEntryArrayList.clear();
        }
        reference.child(typo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductEntry productEntry = dataSnapshot.getValue(ProductEntry.class);
                        productEntryArrayList.add(productEntry);
                    }

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
//        ArrayList<ProductEntry> productEntryArrayList = new ArrayList<>();
//        productEntryArrayList.add(new ProductEntry("Hola","https://i.postimg.cc/59C4TJh2/1.jpg",0,"",1));
//        return productEntryArrayList;
    }

    Toolbar toolbar;
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpToolbar(View view) {
         toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }


        if (toolbar != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeOrNoMenu();
                }
            });
        }


//        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
//                getContext(),
//                view.findViewById(R.id.product_grid),
//                new AccelerateDecelerateInterpolator(),
//                getContext().getResources().getDrawable(R.drawable.shr_branded_menu), // Menu open icon
//                getContext().getResources().getDrawable(R.drawable.shr_close_menu))); // Menu close icon
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    private void updateIcon(Toolbar view) {
        if (openIcon != null && closeIcon != null) {
            if (!(view instanceof Toolbar)) {
                throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
            }
            if (backdropShown) {
                ((Toolbar) view).setNavigationIcon(closeIcon);
            } else {
                ((Toolbar) view).setNavigationIcon(openIcon);
            }
        }
    }
}