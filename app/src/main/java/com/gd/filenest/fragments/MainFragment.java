package com.gd.filenest.fragments;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.gd.filenest.R;
import com.gd.filenest.activities.MainActivity;
import com.gd.filenest.adapters.HomeAdapter;
import com.gd.filenest.databinding.FragmentMainBinding;
import com.gd.filenest.databinding.StorageSpaceItemBinding;
import com.gd.filenest.interfaces.MainMenuAppClickListener;
import com.gd.filenest.interfaces.StorageClickListener;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment implements MainMenuAppClickListener , StorageClickListener{

    FragmentMainBinding fragmentMainBinding;

    private HomeAdapter homeAdapter;
//    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};
    private String[] apps = {"Images", "Audio", "Videos", "Document", "Download", "More"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        initializeStorageData();
        fragmentMainBinding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        homeAdapter = new HomeAdapter(getActivity(),apps, (MainMenuAppClickListener) this, (StorageClickListener) this);
        fragmentMainBinding.rv.setAdapter(homeAdapter);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                calculateStorageSizes();
                homeAdapter.notifyAppData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeAdapter.notifyItemChanged(2);
                    }
                });


            }
        });

        return fragmentMainBinding.getRoot();
    }
    public void getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }
        Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE = availableBlocks * blockSize;

    }
    public void getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long totalBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        }
        Utils.TOTAL_EXTERNAL_SIZE = totalBlocks * blockSize;

    }
    public void getAvailableExternalMemorySize() {
        if (Utils.IS_SD_CARD_EXIST) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(Utils.SD_CARD_FILE.getPath());
            long blockSize = 0;
            long availableBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }
            Utils.TOTAL_AVAILABLE_SD_CARD_SIZE = blockSize * availableBlocks;
        }
    }
    public void getTotalExternalMemorySize() {
        if (Utils.IS_SD_CARD_EXIST) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(Utils.SD_CARD_FILE.getPath());
            long blockSize = 0;
            long totalBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            }
            Utils.TOTAL_SD_CARD_SIZE = blockSize * totalBlocks;
        }
    }
    private ArrayList<File> getFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(getFiles(singleFile));
                } else {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        Utils.EXT_IMAGE_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        Utils.EXT_VIDEO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        Utils.EXT_AUDIO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        Utils.EXT_ZIPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                        Utils.EXT_APPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        Utils.EXT_DOCUMENT_SIZE += singleFile.length();
                    }
                }
            }

        }

        return arrayList;
    }
    private ArrayList<File> countDownload(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(countDownload(singleFile));
                } else {
                    Utils.EXT_DOWNLOAD_SIZE += singleFile.length();
                }
            }
        }
        return arrayList;
    }
    private ArrayList<File> getSdCardFile(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(getFiles(singleFile));
                } else {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        Utils.SD_IMAGE_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        Utils.SD_VIDEO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        Utils.SD_AUDIO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        Utils.SD_ZIPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                        Utils.SD_APPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        Utils.SD_DOCUMENT_SIZE += singleFile.length();
                    }
                }
            }


        }

        return arrayList;
    }
    private void initializeStorageData() {
        getTotalInternalMemorySize();
        getAvailableInternalMemorySize();
        Utils.EXTERNAL_FILE = Environment.getExternalStorageDirectory();
        Utils.aud(getActivity());
        if (Utils.IS_SD_CARD_EXIST) {
            getTotalExternalMemorySize();
            getAvailableExternalMemorySize();
        }
    }
    private void calculateStorageSizes() {
        Utils.EXT_IMAGE_SIZE = 0;
        Utils.EXT_AUDIO_SIZE = 0;
        Utils.EXT_VIDEO_SIZE = 0;
        Utils.EXT_ZIPS_SIZE = 0;
        Utils.EXT_APPS_SIZE = 0;
        Utils.EXT_DOCUMENT_SIZE = 0;
        Utils.EXT_DOWNLOAD_SIZE = 0;

        // Calculate sizes
        getFiles(Utils.EXTERNAL_FILE);
        countDownload(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        if (Utils.IS_SD_CARD_EXIST) {
            Utils.SD_IMAGE_SIZE = 0;
            Utils.SD_AUDIO_SIZE = 0;
            Utils.SD_VIDEO_SIZE = 0;
            Utils.SD_ZIPS_SIZE = 0;
            Utils.SD_APPS_SIZE = 0;
            Utils.SD_DOCUMENT_SIZE = 0;
            getSdCardFile(Utils.SD_CARD_FILE);
        }
    }


    @Override
    public void onMainMenuAppClick(String name) {
        if (apps[0].equals(name) || apps[1].equals(name) || apps[2].equals(name)) {
            Bundle bundle = new Bundle();
            bundle.putString("FILE", name);
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, imageFragment, "AllImageFragment").addToBackStack(null).commit();
        }
//        else if (apps[3].equals(name)) {
//            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new ZipFragment()).addToBackStack(null).commit();
//        }
//        else if (apps[4].equals(name)) {
//            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new AppsFragment()).addToBackStack(null).commit();
//        }
        else if (apps[3].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new DocumentFragment()).addToBackStack(null).commit();
        }
        else if (apps[4].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new DownloadFragment()).addToBackStack(null).commit();
        }
        else if (apps[5].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new MoreFragment()).addToBackStack(null).commit();
        }
    }

    @Override
    public void onStorageClicked(int i) {
        if (i == 0) {
            Bundle bundle = new Bundle();
            bundle.putString("path", Utils.EXTERNAL_FILE.getAbsolutePath());
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        }
        else if (i == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("path", Utils.SD_CARD_FILE.getAbsolutePath());
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        }
        else if (i == 2) {
            Bundle bundle = new Bundle();
            bundle.putInt("FROM", i);
            StorageSpaceFragment storageSpaceFragment = new StorageSpaceFragment();
            storageSpaceFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageSpaceFragment).addToBackStack(null).commit();
        }
        else if (i == 3){
            Bundle bundle = new Bundle();
            bundle.putInt("FROM", i);
            StorageSpaceFragment storageSpaceFragment = new StorageSpaceFragment();
            storageSpaceFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageSpaceFragment).addToBackStack(null).commit();
        }
    }
}