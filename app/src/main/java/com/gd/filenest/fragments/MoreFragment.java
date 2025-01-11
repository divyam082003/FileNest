package com.gd.filenest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gd.filenest.R;
import com.gd.filenest.activities.MainActivity;
import com.gd.filenest.adapters.MainMenuAppAdapter;
import com.gd.filenest.databinding.FragmentMoreBinding;
import com.gd.filenest.interfaces.MainMenuAppClickListener;
import com.gd.filenest.utils.Utils;


public class MoreFragment extends Fragment implements MainMenuAppClickListener {

    private FragmentMoreBinding binding;
//    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download"};
    private String[] apps = {"Images", "Audio", "Videos",  "Document", "Download"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        binding.rv.setLayoutManager(new GridLayoutManager(getContext(),4));
        MainMenuAppAdapter homeAppAdapter = new MainMenuAppAdapter(apps,getActivity(),this);
        binding.rv.setAdapter(homeAppAdapter);
        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());
        return binding.getRoot();
    }

    @Override
    public void onMainMenuAppClick(String name) {
        if (apps[0].equals(name)||apps[1].equals(name)||apps[2].equals(name)){
            Bundle bundle = new Bundle();
            bundle.putString("FILE", name);
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, imageFragment).addToBackStack(null).commit();
        }
//        else if (apps[3].equals(name)){
//            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new ZipFragment()).addToBackStack(null).commit();
//        }
//        else if (apps[4].equals(name)){
//            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new AppsFragment()).addToBackStack(null).commit();
//        }
        else if (apps[3].equals(name)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new DocumentFragment()).addToBackStack(null).commit();
        }
        else if (apps[4].equals(name)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new DownloadFragment()).addToBackStack(null).commit();
        }
    }
}