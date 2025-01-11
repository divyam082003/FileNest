package com.gd.filenest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.gd.filenest.R;
import com.gd.filenest.activities.MainActivity;
import com.gd.filenest.adapters.AppsAdapter;
import com.gd.filenest.databinding.FragmentAppsBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.OpenFile;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class AppsFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentAppsBinding binding;

    private ArrayList<File> appList = new ArrayList<>();
    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};

    public static ActionModeListener actionModeListener;
    private AppsAdapter appsAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppsBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        appsAdapter = new AppsAdapter(getActivity(), this);
        binding.rv.setAdapter(appsAdapter);
        setData();

        actionModeListener = this;

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.et.addTextChangedListener(null);
    }

    private void setData() {

        appList.clear();

        Executors.newSingleThreadExecutor().execute(() -> {


            if (Utils.EXTERNAL_FILE != null) {
                appList.addAll(getApps(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                appList.addAll(getApps(Utils.SD_CARD_FILE));
            }
            appsAdapter.addAll(appList,this,binding.noData);
        });

    }

    ArrayList<File> getApps(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(getApps(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().endsWith(".apk")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }

        }

        return fileArrayList;
    }


    @Override
    public void onFileSelect(ImageView imageView, File file) {
        OpenFile.open(file);
    }

    @Override
    public void onBind(ImageView imageView, File file) {


    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("List", (Serializable) files);
        bundle.putInt("Position", position);
        bundle.putString("From", "Apps");
        SelectAppsFragment selectAppsFragment = new SelectAppsFragment();
        selectAppsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, selectAppsFragment).addToBackStack(null).commit();

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        switch (event) {


            case Utils.EVENT_CLOSE:
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_DELETE:
                setData();
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> {
                    getActivity().onBackPressed();
                });
                break;
        }
    }
}