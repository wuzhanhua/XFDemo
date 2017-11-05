package com.example.tufei.xfdemo.utils;

import android.widget.Toast;

import com.example.tufei.xfdemo.App;

/**
 * 只弹一次土司
 */

public class ToastUtil {

    public static Toast toast;
    public static void showToast(String showtext) {

        if(toast==null){
            toast=Toast.makeText(App.getApplication(),showtext,Toast.LENGTH_SHORT);
        }else{
            toast.setText(showtext);
        }
        toast.show();
    }
}
