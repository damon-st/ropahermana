package com.damon.ropa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

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