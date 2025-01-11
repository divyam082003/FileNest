package com.gd.filenest.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import android.widget.Toast;


import com.gd.filenest.R;
import com.gd.filenest.activities.MainActivity;
import com.gd.filenest.adapters.StorageAdapter;
import com.gd.filenest.databinding.DialogCreateFolderBinding;
import com.gd.filenest.databinding.FragmentStorageBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.OpenFile;
import com.gd.filenest.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;


public class StorageFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentStorageBinding binding;
    private String path;
    private StorageAdapter storageAdapter;
    public static ActionModeListener actionModeListener;
    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStorageBinding.inflate(inflater);
        activity = getActivity();
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        storageAdapter = new StorageAdapter(getActivity(), this,"storage");
        binding.rv.setAdapter(storageAdapter);

        setFile();

        binding.createFolder.setOnClickListener(v -> createFolderDialog());

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
                storageAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.et.addTextChangedListener(null);
    }

    private void setFile() {
        if (path != null) {
            ArrayList<File> files = shortFile(getFile(new File(path)));

            storageAdapter.addAll(files,this,null);
        } else {
            if (Utils.EXTERNAL_FILE != null) {
                ArrayList<File> file = getFile(Utils.EXTERNAL_FILE);
                storageAdapter.addAll(file,this,null);
            }
        }
    }

    private void createFolderDialog() {
        Dialog dialog = new Dialog(getActivity());
        DialogCreateFolderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_create_folder, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        binding.btnOk.setOnClickListener(v -> createFolder(binding.editText.getText().toString().trim(), dialog));
        binding.btnCancel.setOnClickListener(v -> dialog.cancel());

    }

    private void createFolder(String text, Dialog dialog) {
        if (text != null && text.length() > 0) {
            File file = new File(new File(path), text);
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                setFile();
                dialog.dismiss();
            }
            else {
                Toast.makeText(getContext(),"Folder cannot be created",Toast.LENGTH_SHORT).show();
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
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        }else {
            OpenFile.open(file);
        }
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", (Serializable) files);
        bundle.putInt("position", position);
        SelectStorageFragment selectStorageFragment = new SelectStorageFragment();
        selectStorageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(binding.container.getId(), selectStorageFragment).addToBackStack(null).commit();
    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }


    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_DELETE) {
            setFile();
            activity.onBackPressed();
        } else if (event == Utils.EVENT_COPY) {
            setFile();
            new Handler(Looper.getMainLooper()).post(() -> {
                activity.onBackPressed();
            });

        } else if (event == Utils.EVENT_MOVE) {
            setFile();
            activity.onBackPressed();
        }
    }
}