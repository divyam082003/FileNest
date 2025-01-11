package com.gd.filenest.adapters;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gd.filenest.R;
import com.gd.filenest.activities.MainActivity;
import com.gd.filenest.databinding.CleanerItemBinding;
import com.gd.filenest.databinding.MainMenuItemBinding;
import com.gd.filenest.databinding.MainMenuRvBinding;
import com.gd.filenest.databinding.SearchbarItemBinding;
import com.gd.filenest.databinding.StorageSpaceItemBinding;
import com.gd.filenest.fragments.CleanerFragment;
import com.gd.filenest.fragments.SearchFragment;
import com.gd.filenest.interfaces.MainMenuAppClickListener;
import com.gd.filenest.interfaces.StorageClickListener;
import com.gd.filenest.utils.Utils;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity mainActivity;
    MainMenuAppAdapter mainMenuAppAdapter;
    int width,sdWidth;
    String[] apps;
    MainMenuAppClickListener mainMenuAppClickListener;
    StorageClickListener storageClickListener;


    public HomeAdapter(Activity mainActivity, String[] apps, MainMenuAppClickListener mainMenuAppClickListener,StorageClickListener storageClickListener) {
        this.apps = apps;
        this.mainActivity = mainActivity;
        this.mainMenuAppClickListener = mainMenuAppClickListener;
        mainMenuAppAdapter = new MainMenuAppAdapter(apps, mainActivity, mainMenuAppClickListener);
        this.storageClickListener = storageClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else if (position == 2) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            SearchbarItemBinding binding = (SearchbarItemBinding) DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.searchbar_item,
                    parent,
                    false
            );
            return new SearchBarViewHolder(binding);
        }
        else if (viewType == 1){
            MainMenuRvBinding binding = (MainMenuRvBinding) DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.main_menu_rv,
                    parent,
                    false
            );
            return new MainMenuViewHolder(binding);
        }
        else if (viewType == 2) {
            StorageSpaceItemBinding binding = (StorageSpaceItemBinding) DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.storage_space_item,
                    parent,
                    false
            );
            return new StorageSpaceViewHolder(binding);
        }
        else {
            CleanerItemBinding binding = (CleanerItemBinding) DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.cleaner_item,
                    parent,
                    false
            );
            return new CleanerViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            SearchBarViewHolder holder1 = (SearchBarViewHolder) holder;
            holder1.onBind();
        }
        else if (holder.getItemViewType() == 1) {
            MainMenuViewHolder holder1 = (MainMenuViewHolder) holder;
            holder1.binding.rv.setLayoutManager(new GridLayoutManager(holder1.itemView.getContext(), 4));
            holder1.binding.rv.setAdapter(mainMenuAppAdapter);
        }
        else if (holder.getItemViewType() == 2) {
            StorageSpaceViewHolder viewHolder = (StorageSpaceViewHolder) holder;
            viewHolder.onBind();
        }
        else {
            CleanerViewHolder cleanerViewHolder = (CleanerViewHolder) holder;
            cleanerViewHolder.onBind();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void notifyAppData() {
        mainMenuAppAdapter.notifyApps();
    }


    class SearchBarViewHolder extends RecyclerView.ViewHolder {

        SearchbarItemBinding binding;

        public SearchBarViewHolder(@NonNull SearchbarItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {
            binding.searchLL.setOnClickListener(v -> {
                ((AppCompatActivity) mainActivity).getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new SearchFragment()).addToBackStack(null).commit();
            });

        }

    }

    class MainMenuViewHolder extends RecyclerView.ViewHolder {
        MainMenuRvBinding binding;

        public MainMenuViewHolder(@NonNull MainMenuRvBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    class StorageSpaceViewHolder extends RecyclerView.ViewHolder {

        StorageSpaceItemBinding binding;

        public StorageSpaceViewHolder(@NonNull StorageSpaceItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {

            ViewTreeObserver treeObserver = binding.lyInternal.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(() -> {
                binding.lyInternal.getViewTreeObserver().removeOnGlobalLayoutListener(null);
                int measuredWidth = binding.lyInternal.getMeasuredWidth();
                width = measuredWidth;
            });


            long freeSpace = Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE;
            long total = Utils.TOTAL_EXTERNAL_SIZE;

            long others = total - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE + Utils.EXT_APPS_SIZE + Utils.EXT_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE);
            long free = freeSpace;
            long img = Utils.EXT_IMAGE_SIZE;
            long audio = Utils.EXT_AUDIO_SIZE;
            long vid = Utils.EXT_VIDEO_SIZE;
            long zips = Utils.EXT_ZIPS_SIZE;
            long apps = Utils.EXT_APPS_SIZE;
            long doc = Utils.EXT_DOCUMENT_SIZE;

            double freePre = ((double) Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE * 100 / (double) total);
            double imgPre = ((double) img * 100 / (double) total);
            double audPre = ((double) audio * 100 / (double) total);
            double vidPre = ((double) vid * 100 / (double) total);
            double zipPre = ((double) zips * 100 / (double) total);
            double appPre = ((double) apps * 100 / (double) total);
            double docPre = ((double) doc * 100 / (double) total);
            double otherPre = ((double) others * 100 / (double) total);


            binding.availableSpaceTV.setText(String.format("%s / %s", Formatter.formatFileSize(mainActivity, Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE), Formatter.formatFileSize(mainActivity, Utils.TOTAL_EXTERNAL_SIZE)));
            binding.tvPr.setText(String.format("%d%%", 100 - (int) (((float) Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE / (float) Utils.TOTAL_EXTERNAL_SIZE) * 100)));

            ViewGroup.LayoutParams params = binding.prImage.getLayoutParams();
            params.width = (int) (width * imgPre) / 100;
            binding.prImage.setLayoutParams(params);

            ViewGroup.LayoutParams paramsAudio = binding.prAudio.getLayoutParams();
            paramsAudio.width = (int) (width * audPre) / 100;
            binding.prAudio.setLayoutParams(paramsAudio);

            ViewGroup.LayoutParams paramsVideo = binding.prVideo.getLayoutParams();
            paramsVideo.width = (int) (width * vidPre) / 100;
            binding.prVideo.setLayoutParams(paramsVideo);

            ViewGroup.LayoutParams paramsZips = binding.prZips.getLayoutParams();
            paramsZips.width = (int) (width * zipPre) / 100;
            binding.prZips.setLayoutParams(paramsZips);

            ViewGroup.LayoutParams paramsApps = binding.prApps.getLayoutParams();
            paramsApps.width = (int) (width * appPre) / 100;
            binding.prApps.setLayoutParams(paramsApps);

            ViewGroup.LayoutParams paramsDoc = binding.prDocument.getLayoutParams();
            paramsDoc.width = (int) (width * docPre) / 100;
            binding.prDocument.setLayoutParams(paramsDoc);

            ViewGroup.LayoutParams paramsOthers = binding.prOthers.getLayoutParams();
            paramsOthers.width = (int) (width * otherPre) / 100;
            binding.prOthers.setLayoutParams(paramsOthers);

            ViewGroup.LayoutParams paramsFree = binding.prBlank.getLayoutParams();
            paramsFree.width = (int) (width * freePre) / 100;
            binding.prBlank.setLayoutParams(paramsFree);

            binding.constraintLayout2.setOnClickListener(v -> {
                storageClickListener.onStorageClicked(0);
            });

            binding.internalNextIV.setOnClickListener(v -> storageClickListener.onStorageClicked(2));


            if (Utils.IS_SD_CARD_EXIST) {

                binding.sd.setVisibility(View.VISIBLE);

                ViewTreeObserver sdTreeObserver = binding.lySd.getViewTreeObserver();
                sdTreeObserver.addOnGlobalLayoutListener(() -> {
                    binding.lySd.getViewTreeObserver().removeOnGlobalLayoutListener(null);
                    int measWidth = binding.lySd.getMeasuredWidth();
                    sdWidth = measWidth;
                });


                long sdFreeSpace = Utils.TOTAL_AVAILABLE_SD_CARD_SIZE;
                long sdTotal = Utils.TOTAL_SD_CARD_SIZE;


                long sdOthers = sdTotal - (Utils.SD_IMAGE_SIZE + Utils.SD_AUDIO_SIZE + Utils.SD_VIDEO_SIZE + Utils.SD_ZIPS_SIZE + Utils.SD_APPS_SIZE + Utils.SD_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_SD_CARD_SIZE);
                long sdFree = sdFreeSpace;
                long sdImg = Utils.SD_IMAGE_SIZE;
                long sdAudio = Utils.SD_AUDIO_SIZE;
                long sdVid = Utils.SD_VIDEO_SIZE;
                long sdZips = Utils.SD_ZIPS_SIZE;
                long sdApps = Utils.SD_APPS_SIZE;
                long sdDoc = Utils.SD_DOCUMENT_SIZE;


                double sdFreePre = ((double) Utils.TOTAL_AVAILABLE_SD_CARD_SIZE * 100 / (double) sdTotal);
                double sdImgPre = ((double) sdImg * 100 / (double) sdTotal);
                double sdAudPre = ((double) sdAudio * 100 / (double) sdTotal);
                double sdVidPre = ((double) sdVid * 100 / (double) sdTotal);
                double sdZipPre = ((double) sdZips * 100 / (double) sdTotal);
                double sdAppPre = ((double) sdApps * 100 / (double) sdTotal);
                double sdDocPre = ((double) sdDoc * 100 / (double) sdTotal);
                double sdOtherPre = ((double) sdOthers * 100 / (double) sdTotal);

                binding.availableSDSpaceTV.setText(String.format("%s / %s", Formatter.formatFileSize(mainActivity, Utils.TOTAL_AVAILABLE_SD_CARD_SIZE), Formatter.formatFileSize(mainActivity, Utils.TOTAL_SD_CARD_SIZE)));
                binding.sdPr.setText(String.format("%d%%", 100 - (int) (((float) Utils.TOTAL_AVAILABLE_SD_CARD_SIZE / (float) Utils.TOTAL_SD_CARD_SIZE) * 100)));


                ViewGroup.LayoutParams sdImageParam = binding.prSdImage.getLayoutParams();
                sdImageParam.width = (int) (sdWidth * sdImgPre) / 100;
                binding.prSdImage.setLayoutParams(sdImageParam);

                ViewGroup.LayoutParams sdAudioParam = binding.prSdAudio.getLayoutParams();
                sdAudioParam.width = (int) (sdWidth * sdAudPre) / 100;
                binding.prSdAudio.setLayoutParams(sdAudioParam);

                ViewGroup.LayoutParams sdVideoParam = binding.prSdVideo.getLayoutParams();
                sdVideoParam.width = (int) (sdWidth * sdVidPre) / 100;
                binding.prSdVideo.setLayoutParams(sdVideoParam);

                ViewGroup.LayoutParams sdZipParam = binding.prSdZips.getLayoutParams();
                sdZipParam.width = (int) (sdWidth * sdZipPre) / 100;
                binding.prSdZips.setLayoutParams(sdZipParam);

                ViewGroup.LayoutParams sdAppsParam = binding.prSdApps.getLayoutParams();
                sdAppsParam.width = (int) (sdWidth * sdAppPre) / 100;
                binding.prSdApps.setLayoutParams(sdAppsParam);

                ViewGroup.LayoutParams sdDocParam = binding.prSdDocument.getLayoutParams();
                sdDocParam.width = (int) (sdWidth * sdDocPre) / 100;
                binding.prSdDocument.setLayoutParams(sdDocParam);

                ViewGroup.LayoutParams sdOtherParam = binding.prSdOthers.getLayoutParams();
                sdOtherParam.width = (int) (sdWidth * sdOtherPre) / 100;
                binding.prSdOthers.setLayoutParams(sdOtherParam);

                ViewGroup.LayoutParams sdFreeParam = binding.prSdBlank.getLayoutParams();
                sdFreeParam.width = (int) (sdWidth * sdOtherPre) / 100;
                binding.prSdBlank.setLayoutParams(sdFreeParam);
                binding.sd.setOnClickListener(v -> {
                    storageClickListener.onStorageClicked(1);
                });
                binding.ivSdNext.setOnClickListener(v -> storageClickListener.onStorageClicked(3));
            }

        }
    }

    class CleanerViewHolder extends RecyclerView.ViewHolder {
        CleanerItemBinding binding;
        public CleanerViewHolder(@NonNull CleanerItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {
            binding.constraintLayout3.setOnClickListener(v -> ((AppCompatActivity)mainActivity).getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new CleanerFragment()).addToBackStack(null).commit());
            ActivityManager actManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long availMem = memInfo.availMem;
            long totalMemory = memInfo.totalMem;
            binding.tvRam.setText(String.format("%s Used / %s",Formatter.formatFileSize(mainActivity,totalMemory),Formatter.formatFileSize(mainActivity,availMem)));

        }

    }

}
