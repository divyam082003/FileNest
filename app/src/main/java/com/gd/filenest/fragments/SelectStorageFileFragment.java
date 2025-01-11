package com.gd.filenest.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.apache.commons.io.FileUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gd.filenest.R;
import com.gd.filenest.activities.SelectFileActivity;
import com.gd.filenest.adapters.StorageAdapter;
import com.gd.filenest.databinding.FragmentSelectStorageFileBinding;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class SelectStorageFileFragment extends Fragment implements FileSelectedListener {

    private FragmentSelectStorageFileBinding binding;
    private StorageAdapter storageAdapter;
    private String path;
    private String action;
    private String from;
    private ArrayList<File> selected;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");
            action = getArguments().getString("action");
            from = getArguments().getString("from");
        }
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSelectStorageFileBinding.inflate(inflater);

        if (action.equals("move")) {
            binding.icAction.setImageResource(R.drawable.ic_move);
            binding.tvAction.setText("Move");
        } else if (action.equals("copy")) {
            binding.icAction.setImageResource(R.drawable.ic_copy);
            binding.tvAction.setText("Copy");
        }

        if (from.equals("storage")) {
            selected = SelectStorageFragment.selectedList;
        } else if (from.equals("Apps") || from.equals("Doc") || from.equals("Dow") || from.equals("Zip")) {
            selected = SelectAppsFragment.selectedList;
        } else if (from.equals("allImage")) {
            selected = ImageSelectFragment.selectedList;
        } else if (from.equals("allSong")) {
            selected = SongSelectFragment.audioList;
        } else if (from.equals("vidFolder")) {
            selected = SelectVideoFolderFragment.selectedList;
        }else if (from.equals("imgFolder")){
            selected = ImgFolderFragment.selectedList;
        }

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        storageAdapter = new StorageAdapter(getActivity(), this,"storage");
        binding.rv.setAdapter(storageAdapter);

        if (path != null) {
            ArrayList<File> files = shortFile(getFile(new File(path)));
            storageAdapter.addAll(files,this,null);
        }

        binding.lyAction.setOnClickListener(v -> {
            if (action.equals("move")) {
                moveFile();
            } else if (action.equals("copy")) {
                copyFile();
            }
        });

        binding.lyCancel.setOnClickListener(v -> getActivity().finish());

        return binding.getRoot();
    }

    private void copyFile() {
        if (selected != null ||selected.size() > 0) {
            for (File file : selected) {
                if (file.isDirectory()) {
                    try {
                        FileUtils.copyDirectoryToDirectory(file, new File(path));
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileUtils.copyFileToDirectory(file, new File(path), true);
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
            getActivity().finish();
            if (from.equals("storage")) {
                SelectStorageFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            } else if (from.equals("Apps") || from.equals("Doc") || from.equals("Dow") || from.equals("Zip")) {
                SelectAppsFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            } else if (from.equals("allImage")) {
                ImageSelectFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            } else if (from.equals("allSong")) {
                SongSelectFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            } else if (from.equals("vidFolder")) {
                SelectVideoFolderFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            }else if (from.equals("imgFolder")){
                ImgFolderFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
            }
        }
    }

    private void moveFile() {
        if (selected != null) {
            for (File file : selected) {
                if (file.isDirectory()) {
                    try {
                        FileUtils.copyDirectoryToDirectory(file, new File(path));
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileUtils.copyFileToDirectory(file, new File(path), true);
                        file.delete();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
            getActivity().finish();
            if (from.equals("storage")) {
                SelectStorageFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            } else if (from.equals("Apps") || from.equals("Doc") || from.equals("Dow") || from.equals("Zip")) {
                SelectAppsFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            } else if (from.equals("allImage")) {
                ImageSelectFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            } else if (from.equals("allSong")) {
                SongSelectFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            } else if (from.equals("vidFolder")) {
                SelectVideoFolderFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            }else if (from.equals("imgFolder")){
                ImgFolderFragment.actionModeListener.onEventListener(Utils.EVENT_MOVE);
            }
        }
    }

    ArrayList<File> shortFile(ArrayList<File> files) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::getName));
        }
        return files;
    }

    ArrayList<File> getFile(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (!singleFile.isHidden()) {
                    fileArrayList.add(singleFile);
                }
            }
        }
        return fileArrayList;
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            bundle.putString("action", action);
            bundle.putString("from", from);
            SelectStorageFileFragment selectStorageFileFragment = new SelectStorageFileFragment();
            selectStorageFileFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(SelectFileActivity.SELECT_FILE_CON, selectStorageFileFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }
}