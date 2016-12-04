package com.alex.besselview.ui;

import android.os.Bundle;
import android.view.View;

import com.alex.besselview.R;
import com.alex.besselview.ui.base.BVActivity;
import com.alex.besselview.ui.bezier.Bezier2Activity;
import com.alex.besselview.ui.bezier.Bezier3Activity;
import com.alex.besselview.ui.draghelper.DragHelperActivity;

import org.alex.helper.IntentHelper;

public class MainActivity extends BVActivity {


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateData(Bundle bundle) {

    }

    @Override
    public void onClick(View v, int id) {
        super.onClick(v, id);
        if (R.id.bt_1 == id) {
            IntentHelper.getInstance().startActivity(Bezier2Activity.class);
        } else if (R.id.bt_2 == id) {
            IntentHelper.getInstance().startActivity(Bezier3Activity.class);
        }   else if (R.id.bt_5 == id) {
            IntentHelper.getInstance().startActivity(DragHelperActivity.class);
        }
    }
}
