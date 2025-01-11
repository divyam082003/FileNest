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
import com.gd.filenest.adapters.DocumentAdapter;
import com.gd.filenest.databinding.FragmentDocumentBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.OpenFile;
import com.gd.filenest.utils.Utils;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class DocumentFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentDocumentBinding binding;
    private ArrayList<File> doc = new ArrayList<>();
    private DocumentAdapter docAdapter;
    private boolean cbPdf = false;
    private boolean cbDoc = false;
    private boolean cbXls = false;
    private boolean cbPpt = false;
    private boolean cbTxt = false;
    private boolean cbOthers = false;
    public static ActionModeListener actionModeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDocumentBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        docAdapter = new DocumentAdapter(getActivity(), this);
        binding.rv.setAdapter(docAdapter);

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
                docAdapter.getFilter().filter(s);
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
        doc.clear();

        Executors.newSingleThreadExecutor().execute(() -> {
            if (Utils.EXTERNAL_FILE != null) {
                doc.addAll(doc(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                doc.addAll(doc(Utils.SD_CARD_FILE));
            }

            docAdapter.addAll(doc,this,binding.noData);

        });
    }

    ArrayList<File> doc(File file) {

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(doc(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
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
        bundle.putString("From", "Doc");
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