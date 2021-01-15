package com.damon.ropa.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;

import com.damon.ropa.R;
import com.damon.ropa.fragments.ProductGridFragment;
import com.damon.ropa.interfaces.NavigationHost;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState == null){
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.container,new LoginFragement())
//                    .commit();
//        }

        navigateTo(new ProductGridFragment(),false);

//        Fade fade = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            fade = new Fade();
//            View decor = getWindow().getDecorView();
//            fade.excludeTarget(decor.findViewById(R.id.action_bar_container),true);
//            fade.excludeTarget(android.R.id.statusBarBackground,true);
//            fade.excludeTarget(android.R.id.navigationBarBackground,true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setEnterTransition(fade);
//                getWindow().setExitTransition(fade);
//            }
//        }

    }
    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment);

        if (addToBackstack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}