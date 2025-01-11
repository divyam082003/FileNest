package com.gd.filenest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gd.filenest.R;
import com.gd.filenest.adapters.ImageFolderAdapter;
import com.gd.filenest.databinding.FragmentImageFolderBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class ImageFolderFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentImageFolderBinding binding;
    private ArrayList<MediaModel> f = new ArrayList<>();
    public static ActionModeListener actionModeListener;
    private ImageFolderAdapter imageFolderAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageFolderBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        actionModeListener = this;

        binding.rv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        imageFolderAdapter = new ImageFolderAdapter(getActivity(), this);
        binding.rv.setAdapter(imageFolderAdapter);


        setData();

        return binding.getRoot();
    }

    private void setData() {
        f.clear();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (Utils.EXTERNAL_FILE != null) {
                f.addAll(f(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                f.addAll(f(Utils.SD_CARD_FILE));
            }
            imageFolderAdapter.addAll(f, this, binding.noData);
        });
    }

    private ArrayList<MediaModel> f(File file) {
        ArrayList<MediaModel> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory() && !file1.isHidden() &&!file1.getName().equals("Telegram") ) {
                    File directory = isDirectory(file1);
                    if (directory != null) {
                        int anInt = getInt(directory);
                        fileArrayList.add(new MediaModel(directory, anInt));
                    }
                }
            }
        }
        return fileArrayList;
    }

    private int getInt(File file) {
        int count = 0;
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.getName().toLowerCase().endsWith(".jpg") || file1.getName().toLowerCase().endsWith(".jpeg") || file1.getName().toLowerCase().endsWith(".png")) {
                count += 1;
            }
        }
        return count;
    }

    private File isDirectory(File file) {
        File[] files = file.listFiles();

        if (files != null) {

            for (File file1 : files) {

                if (file1.isDirectory() && !file1.isHidden()) {
                    File dir = isDir(file1);
                    if (dir != null) {
                        return file1;
                    }

                } else {
                    if (file1.getName().toLowerCase().endsWith(".jpg") || file1.getName().toLowerCase().endsWith(".jpeg") || file1.getName().toLowerCase().endsWith(".png")) {
                        return file;
                    }
                }
            }

        }
        return null;
    }

    private File isDir(File file) {
        File[] files = file.listFiles();

        if (files != null) {

            for (File file1 : files) {
                if (file1.isDirectory() && !file1.isHidden()) {
                    isDir(file1);

                } else {
                    if (file1.getName().toLowerCase().endsWith(".jpg") || file1.getName().toLowerCase().endsWith(".jpeg") || file1.getName().toLowerCase().endsWith(".png")) {
                        return file1;
                    }
                }
            }

        }
        return null;
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {
        Bundle bundle = new Bundle();
        bundle.putString("path", file.getAbsolutePath());
        AllImageFragment allImageFragment = new AllImageFragment();
        allImageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(binding.container.getId(), allImageFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("val", (Serializable) files);
        ImgFolderFragment imgFolderFragment = new ImgFolderFragment();
        imgFolderFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(ImageFragment.CON, imgFolderFragment).addToBackStack(null).commit();
    }

    @Override
    public void onEventListener(int event) {
        switch (event) {

            case Utils.EVENT_DELETE:
                setData();
                break;
            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> getActivity().onBackPressed());
                break;

        }
    }
}