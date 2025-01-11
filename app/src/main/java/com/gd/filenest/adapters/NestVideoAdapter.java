package com.gd.filenest.adapters;

import static com.gd.filenest.fragments.SelectVideoFolderFragment.selectedList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gd.filenest.databinding.NestRvItemBinding;
import com.gd.filenest.fragments.ImageFragment;
import com.gd.filenest.fragments.ImageSelectFragment;
import com.gd.filenest.model.ImgModel;
import com.gd.filenest.model.ImgShortModel;
import com.gd.filenest.R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class NestVideoAdapter extends RecyclerView.Adapter<NestVideoAdapter.NestVideoViewHolder> implements VideoAdapter.ImageSelect {

    private Activity activity;
    private ArrayList<ImgShortModel> files = new ArrayList<>();
    private int from;
    private int to;
    private int spanCount;
    RecyclerView rv;
    RequestOptions requestOptions;

    public NestVideoAdapter(Activity activity, int from, int to) {
        this.activity = activity;
        this.from = from;
        this.to = to;
        if (from == 0) {
            spanCount = 4;
        } else {
            spanCount = 3;
        }
        requestOptions =  new RequestOptions().transform(new CenterCrop(), new RoundedCorners(18))
                .skipMemoryCache(true)
                .priority(Priority.LOW)
                .format(DecodeFormat.PREFER_ARGB_8888);
    }

    @NonNull
    @Override
    public NestVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NestVideoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.nest_rv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NestVideoViewHolder holder, int position) {
        ImgShortModel imgShortModel = files.get(position);
        holder.binding.tvDate.setText(imgShortModel.date);
        rv = holder.binding.rv;
        holder.binding.rv.setLayoutManager(new GridLayoutManager(activity, spanCount));
        holder.binding.rv.setAdapter(new VideoAdapter(activity, imgShortModel.imgModels, imgShortModel.date, position, from, this,requestOptions));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addAll(ArrayList<ImgShortModel> videos, Fragment fragment,  LinearLayout linearLayout) {
        this.files.clear();
        this.files.addAll(videos);
        if (fragment != null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (linearLayout != null) {
                activity.runOnUiThread(() -> {
                    if (videos.size() > 0) {
                        linearLayout.setVisibility(View.GONE);
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    public void addToSelected(File file) {
        ImageSelectFragment.selectedList.add(file);
    }

    @Override
    public void imgSelect(ImgModel imgModel, ImageView imageView, int i, int j) {
        if (to == 9) {
            if (ImageSelectFragment.isActionModeOn) {
                if (imageView.getDrawable().getConstantState().equals(activity.getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
                    ImageSelectFragment.selectedList.remove(imgModel.file);
                    imageView.setImageResource(R.drawable.no_select);
                    if (selectedList.size()==0){
                        activity.onBackPressed();
                    }
                } else {
                    ImageSelectFragment.selectedList.add(imgModel.file);
                    imageView.setImageResource(R.drawable.ic_selected);
                }
                ImageSelectFragment.tvCounter.setText(String.format("%d Selected", ImageSelectFragment.selectedList.size()));
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) files);
            bundle.putString("file", imgModel.file.getAbsolutePath());
            bundle.putInt("position", i);
            bundle.getInt("p", j);
            ImageSelectFragment imageSelectFragment = new ImageSelectFragment();
            imageSelectFragment.setArguments(bundle);
            ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction().add(ImageFragment.CON, imageSelectFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onImageBind(ImageView imageView, File file) {
        if (isAvailable(file)) {
            imageView.setImageResource(R.drawable.ic_selected);
        } else {
            imageView.setImageResource(R.drawable.no_select);
        }
    }

    public void scroll(int p) {
        if (rv != null) {
            rv.scrollToPosition(p);
        }
    }

    public class NestVideoViewHolder extends RecyclerView.ViewHolder {
        NestRvItemBinding binding;
        public NestVideoViewHolder(@NonNull NestRvItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private boolean isAvailable(File file) {
        for (File files : ImageSelectFragment.selectedList) {
            if (file.equals(files)) {
                return true;
            }
        }
        return false;
    }

}
