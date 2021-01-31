package com.damon.ropa.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.adapters.CategorySeleccionAdapter;
import com.damon.ropa.interfaces.FiltrosI;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomBottomSheet extends BottomSheetDialogFragment {

    Context context;

    FiltrosI buscarClick;

    MaterialButton btn_buscar;

    long desde,hasta;
    String selecion ="";

    CategorySeleccionAdapter adapter;

    List<String> list = new ArrayList<>();

    RecyclerView recyclerView;

    public CustomBottomSheet(Context context,FiltrosI buscarClick,List<String> list) {
        this.context = context;
        this.buscarClick = buscarClick;
        this.list = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_ordenar_fecha,container,false);


        btn_buscar = view.findViewById(R.id.btn_buscar);


        recyclerView = view.findViewById(R.id.rcy_category);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        adapter = new CategorySeleccionAdapter(list,view.getContext());
        recyclerView.setAdapter(adapter);

        selecion = adapter.getCategoria();

        calendarioDesde();
        calendarioHasta();

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecion = adapter.getCategoria();
                if (!TextUtils.isEmpty(selecion)){
                    buscarClick.onClikFiltrar(selecion);
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = (FragmentActivity) context;
        super.onAttach(context);
    }

    void calendarioDesde(){
//        final Calendar newCalendar = Calendar.getInstance();
//        DatePickerDialog StartTime = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
////                inputSearch.setText(dateFormatter.format(newDate.getTime()));
//                txv_desde.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy ")
//                        .format(newDate.getTime()));
//                desde = newDate.getTime().getTime();
//            }
//
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//        btn_desde.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StartTime.show();
//            }
//        });
    }

    void calendarioHasta(){
//        final Calendar newCalendar = Calendar.getInstance();
//        DatePickerDialog StartTime = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
////                inputSearch.setText(dateFormatter.format(newDate.getTime()));
//                txv_hasta.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy ")
//                        .format(newDate.getTime()));
//                hasta = newDate.getTime().getTime();
//            }
//
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//        btn_hasta.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StartTime.show();
//            }
//        });
    }
}