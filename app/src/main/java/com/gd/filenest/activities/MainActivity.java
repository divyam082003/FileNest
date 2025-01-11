package com.gd.filenest.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.gd.filenest.databinding.ActivityMainBinding;
import com.gd.filenest.fragments.MainFragment;
import com.gd.filenest.utils.OpenFile;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    public static int MAIN_CONTAINER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        MAIN_CONTAINER = mainBinding.mainContainer.getId();
        getSupportFragmentManager().beginTransaction().replace(mainBinding.mainContainer.getId(), new MainFragment()).commit();
        OpenFile.init(this);
    }
}