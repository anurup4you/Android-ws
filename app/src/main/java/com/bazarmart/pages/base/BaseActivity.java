package com.bazarmart.pages.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import com.bazarmart.R;
import com.bazarmart.databinding.ActivityBaseBinding;

public class BaseActivity extends AppCompatActivity {

    private ActivityBaseBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this, R.layout.activity_base);
    }
}