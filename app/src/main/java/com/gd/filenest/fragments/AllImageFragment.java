package com.gd.filenest.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gd.filenest.R;
import com.gd.filenest.adapters.NestVideoAdapter;
import com.gd.filenest.databinding.FragmentAllImageBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.model.ImgModel;
import com.gd.filenest.model.ImgShortModel;
import com.gd.filenest.utils.Utils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executors;

public class AllImageFragment extends Fragment implements ActionModeListener {

    private FragmentAllImageBinding binding;
    private ArrayList<ImgModel> imgList = new ArrayList<>();
    private ArrayList<ImgShortModel> imgShortModels = new ArrayList<>();
    private String path;
    public static ActionModeListener actionModeListener;
    private NestVideoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        binding = FragmentAllImageBinding.inflate(inflater);
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NestVideoAdapter(getActivity(), 0, 0);
        binding.rv.setAdapter(adapter);
        setData();
        actionModeListener = this;
        return binding.getRoot();
    }

    private void setData() {
        imgList.clear();
        imgShortModels.clear();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (path != null) {
                getActivity().runOnUiThread(() -> binding.appBarLayout.setVisibility(View.VISIBLE));
                imgList.addAll(img(new File(path)));
                binding.tvTitle.setText(new File(path).getName());
            } else {
                getActivity().runOnUiThread(()->binding.appBarLayout.setVisibility(View.GONE));
                if (Utils.EXTERNAL_FILE != null) {
                    imgList.addAll(img(Utils.EXTERNAL_FILE));}
                if (Utils.SD_CARD_FILE != null) {
                    imgList.addAll(img(Utils.SD_CARD_FILE));
                }
            }
            Collections.sort(imgList, (Comparator<ImgModel>) (o1, o2) -> String.valueOf(o2.file.lastModified()).compareTo(String.valueOf(o1.file.lastModified())));
            ArrayList<String> date = new ArrayList<>();
            for (ImgModel imgModel1 : imgList) {
                if (isCont(date, imgModel1.date)) {
                    date.add(imgModel1.date);
                }
            }
            for (int i = 0; i < date.size(); i++) {
                ArrayList<ImgModel> imgModels = new ArrayList<>();
                for (ImgModel imgModel1 : imgList) {
                    if (date.get(i).equals(imgModel1.date)) {
                        imgModels.add(new ImgModel(imgModel1.file, imgModel1.date, null));
                    }
                }
                if (date.get(i).equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date().getTime()))) {
                    imgShortModels.add(new ImgShortModel(imgModels, "Today"));
                } else if (date.get(i).equals(getYesterday())) {
                    imgShortModels.add(new ImgShortModel(imgModels, "Yesterday"));
                } else {
                    imgShortModels.add(new ImgShortModel(imgModels, date.get(i)));
                }
            }
            if (imgList.size() > 0) {
                adapter.addAll(imgShortModels, this, binding.noData);
            }
        });
    }

    String getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
    }

    ArrayList<ImgModel> img(File file) {
        ArrayList<ImgModel> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden() && !singleFile.getName().contains("Telegram") && !singleFile.getParent().contains("Telegram")) {
                    fileArrayList.addAll(img(singleFile));
                } else {
                    if (!singleFile.isHidden()&& !singleFile.getParent().contains("Telegram")  && singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        fileArrayList.add(new ImgModel(singleFile, String.valueOf(DateFormat.format("dd/MM/yyyy", singleFile.lastModified())), null));
                    }
                }
            }
        }
        return fileArrayList;
    }

    private boolean isCont(ArrayList<String> list, String s) {
        for (String lists : list) {
            if (lists.equals(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onEventListener(int event) {
        switch (event) {
            case Utils.EVENT_DELETE:
                setData();
                break;
            case Utils.EVENT_COPY:

            case Utils.EVENT_CLOSE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> getActivity().onBackPressed());
                break;
        }
    }
}