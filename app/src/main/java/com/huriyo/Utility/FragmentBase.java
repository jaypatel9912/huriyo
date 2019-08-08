package com.huriyo.Utility;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.huriyo.Ui.BaseActivity;
import com.huriyo.Ui.HomeActivity;

import java.lang.reflect.Field;

public abstract class FragmentBase<T extends BaseActivity> extends Fragment implements FragmentCycleLife {
    protected T activity;

    public abstract Screen getCode();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = (T) getActivity();
    }

    public boolean onBack() {
        return true;
    }

    public void onResume() {
        super.onResume();
        if (this.activity instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setFragmentBase(this);
        }
    }


    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isNull(String s) {
        if (TextUtils.isEmpty(s) || s.equals("null")) {
            return true;
        }
        return false;
    }
}
