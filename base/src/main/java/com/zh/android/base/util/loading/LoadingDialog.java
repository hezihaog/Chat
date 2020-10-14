package com.zh.android.base.util.loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.zh.android.base.R;


/**
 * <b>Project:</b> yqwteacher <br>
 * <b>Create Date:</b> 2018/12/7 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> Loading <br>
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_loading_dialog);
    }
}