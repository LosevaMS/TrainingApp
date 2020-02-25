package com.example.globusproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.view.View.GONE;

public class Training extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training,container,false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      /*  BottomNavigationView menuView = view.findViewById(R.id.nav_host_fragment);
        menuView.findViewById(R.id.action_navigation_list_to_training).setVisibility(View.GONE);*/
      /*BottomNavigationView bottomNavigation = view.findViewById(R.id.nav_view);
        bottomNavigation.getMenu().removeItem(R.id.nav_host_fragment);*/

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(GONE);

    }
}