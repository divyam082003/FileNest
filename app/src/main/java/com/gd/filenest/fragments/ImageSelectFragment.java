package com.gd.filenest.fragments;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gd.filenest.R;
import com.gd.filenest.activities.SelectFileActivity;
import com.gd.filenest.adapters.NestVideoAdapter;
import com.gd.filenest.databinding.DialogDeleteBinding;
import com.gd.filenest.databinding.DialogFileInfoBinding;
import com.gd.filenest.databinding.FragmentImageSelectBinding;
import com.gd.filenest.interfaces.ActionModeListener;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.ImgModel;
import com.gd.filenest.model.ImgShortModel;
import com.gd.filenest.model.MediaModel;
import com.gd.filenest.utils.Utils;


import java.io.File;
import java.util.ArrayList;


public class ImageSelectFragment extends Fragment implements  View.OnClickListener, ActionModeListener {

    private FragmentImageSelectBinding binding;
    private ArrayList<ImgShortModel> imgShortModels;
    private File selectedFile;
    public static boolean isActionModeOn = true;
    public static ArrayList<File> selectedList = new ArrayList<>();
    NestVideoAdapter adapter;
    public static TextView tvCounter;
    public int position;
    public int p;
    public static ActionModeListener actionModeListener;
    private boolean isMoCo = false;

    @Override
    public void onResume() {
        super.onResume();
        if (isMoCo){
            AllImageFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgShortModels = (ArrayList<ImgShortModel>) getArguments().getSerializable("data");
            selectedFile = new File(getArguments().getString("file"));
            position = getArguments().getInt("position");
            p = getArguments().getInt("p");
        }
        Utils.setStatusBarColor(R.color.red,getActivity(),true);
        isActionModeOn = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageSelectBinding.inflate(inflater);
        actionModeListener = this;
        tvCounter = binding.tvCounter;
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NestVideoAdapter(getActivity(), 0,9);
        adapter.addToSelected(selectedFile);
        binding.rv.setAdapter(adapter);
        adapter.addAll(imgShortModels,this,null);
        binding.rv.scrollToPosition(position);
        adapter.scroll(p);
        binding.lyDetails.setOnClickListener(this);
        binding.lyDelete.setOnClickListener(this);
        binding.lyCopy.setOnClickListener(this);
        binding.lyMove.setOnClickListener(this);
        return binding.getRoot();
    }

    private void showInfoDialog() {
        Dialog dialog = new Dialog(getContext());
        DialogFileInfoBinding dialogFileInfoBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_file_info, null, false);
        dialog.setContentView(dialogFileInfoBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        if (selectedList.size()>1){
            dialogFileInfoBinding.location.setVisibility(View.GONE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.GONE);
            dialogFileInfoBinding.date.setText("Contains :");
            dialogFileInfoBinding.tvDate.setText(String.format("%d Files",selectedList.size()));
            dialogFileInfoBinding.size.setVisibility(View.GONE);
            dialogFileInfoBinding.tvSize.setVisibility(View.GONE);
        }else {
            dialogFileInfoBinding.location.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.date.setText("Date :");
            dialogFileInfoBinding.tvDate.setText(DateFormat.format("dd - MM- yyyy",selectedList.get(0).lastModified()));
            dialogFileInfoBinding.size.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvSize.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setText(selectedList.get(0).getPath());
            dialogFileInfoBinding.tvSize.setText(Formatter.formatFileSize(getContext(),selectedList.get(0).length()));
        }

        dialogFileInfoBinding.btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDeleteDialog(){
        Dialog dialog = new Dialog(getActivity());
        DialogDeleteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.dialog_delete,null,false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        binding.btnDelete.setOnClickListener(v -> {
            for (File file : selectedList){
                file.delete();
                Log.d("FileDeleted :", file.getName());
            }
            dialog.dismiss();
            AllImageFragment.actionModeListener.onEventListener(Utils.EVENT_DELETE);
            getActivity().onBackPressed();
        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onClick(View v) {
        if (binding.lyDetails.equals(v)){
            showInfoDialog();
        }else if (binding.lyDelete.equals(v)){
            showDeleteDialog();
        }else if (binding.lyCopy.equals(v)){
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "copy");
            intent.putExtra("from", "allImage");
            startActivity(intent);
        }else if (binding.lyMove.equals(v)){
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "move");
            intent.putExtra("from", "allImage");
            startActivity(intent);
        }
    }

    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_MOVE){
            isMoCo = true;
        }else if (event == Utils.EVENT_COPY){
            isMoCo = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActionModeOn = false;
        isMoCo = false;
        selectedList.clear();
    }

}