package com.gd.filenest.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gd.filenest.R;
import com.gd.filenest.databinding.ImageFolderItemBinding;
import com.gd.filenest.fragments.ImgFolderFragment;
import com.gd.filenest.interfaces.FileSelectedListener;
import com.gd.filenest.model.MediaModel;


import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.ImgFolderViewHolder> {

    Activity activity;
    ArrayList<MediaModel> folderList = new ArrayList<>();
    FileSelectedListener fileSelectedListener;

    public ImageFolderAdapter(Activity activity, FileSelectedListener fileSelectedListener) {
        this.activity = activity;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public ImgFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImgFolderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImgFolderViewHolder holder, int position) {
        holder.onBind(folderList.get(position));
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public void addAll(ArrayList<MediaModel> folderList, Fragment fragment,  LinearLayout linearLayout) {
        this.folderList.clear();
        this.folderList.addAll(folderList);
        if (fragment != null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (linearLayout != null) {
                activity.runOnUiThread(() -> {
                    if (folderList.size() > 0 ){
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });

            }
        }
    }

    public class ImgFolderViewHolder extends RecyclerView.ViewHolder {
        ImageFolderItemBinding binding;

        public ImgFolderViewHolder(@NonNull ImageFolderItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(MediaModel mediaModel) {
            if (ImgFolderFragment.isActionModeOn) {
                fileSelectedListener.onBind(binding.icSelect, mediaModel.file);
            }
            binding.tvFolder.setText(mediaModel.file.getName());
            binding.tvCounter.setText(String.format("%d photos", mediaModel.count));

            binding.icSelect.setOnClickListener(v -> fileSelectedListener.onIvMediaSelectClick(folderList, getAdapterPosition(), binding.icSelect));
            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.icSelect, mediaModel.file));

            for (File file : Objects.requireNonNull(mediaModel.file.listFiles())) {
                if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".png")) {
                    Glide.with(itemView.getContext())
                            .load(file)
                            .apply(new RequestOptions()
                                    .transform(new CenterCrop(), new RoundedCorners(18))
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW)
                                    .format(DecodeFormat.PREFER_ARGB_8888))
                            .into(binding.iv);
                    return;
                }
            }
        }
    }
}
