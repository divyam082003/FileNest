package com.gd.filenest.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gd.filenest.R;
import com.gd.filenest.adapters.SearchAdapter;
import com.gd.filenest.databinding.FragmentSearchBinding;
import com.gd.filenest.utils.OpenFile;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment implements SearchAdapter.onFileClick{
    private FragmentSearchBinding fragmentSearchbinding;
    private ArrayList<File> fileList = new ArrayList<>();

    private SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSearchbinding = FragmentSearchBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        searchAdapter = new SearchAdapter(getActivity(),this);
        fragmentSearchbinding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentSearchbinding.rv.setAdapter(searchAdapter);

        getData();

        fragmentSearchbinding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());
        return fragmentSearchbinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentSearchbinding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentSearchbinding.et.addTextChangedListener(null);
    }

    private void getData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (Utils.EXTERNAL_FILE != null) {
                fileList.addAll(shortFile(getFile(Utils.EXTERNAL_FILE)));
            } else if (Utils.SD_CARD_FILE != null) {
                fileList.addAll(shortFile(getFile(Utils.SD_CARD_FILE)));
            }

            searchAdapter.addAll(fileList);

        });
    }

    ArrayList<File> shortFile(ArrayList<File> files) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::lastModified));
        }
        return files;
    }

    ArrayList<File> getFile(File file) {

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(getFile(singleFile));
                }                                           else {
                    if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        fileArrayList.add(singleFile);
                    } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        fileArrayList.add(singleFile);
                    } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        fileArrayList.add(singleFile);
                    } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        fileArrayList.add(singleFile);
                    } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".apk")) {
                        fileArrayList.add(singleFile);
                    } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }

        }

        return fileArrayList;
    }

    @Override
    public void onClick(File file) {
        if (file.isDirectory()){
//            Bundle bundle = new Bundle();
//            bundle.putString("path", file.getAbsolutePath());
//            StorageFragment storageFragment = new StorageFragment();
//            storageFragment.setArguments(bundle);
//            getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        }else {
            OpenFile.open(file);
        }
    }

}
