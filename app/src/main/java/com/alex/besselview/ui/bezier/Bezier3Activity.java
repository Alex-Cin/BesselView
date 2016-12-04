package com.alex.besselview.ui.bezier;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alex.besselview.R;
import com.alex.besselview.ui.base.BVActivity;

import org.alex.besselview.BezierView3;
import org.alex.besselview.BezierView3_2;
import org.alex.callback.SimpleOnCheckedChangeListener;

/**
 * 作者：Alex
 * 时间：2016年12月01日
 * 简述：
 */

public class Bezier3Activity extends BVActivity {
    private BezierView3 bv1;
    private BezierView3_2 bv2;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_bezier3;
    }

    @Override
    public void onCreateData(Bundle bundle) {
        bv1 = findView(R.id.bv_1);
        bv2 = findView(R.id.bv_2);
        RadioGroup radioGroup = findView(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new MySimpleOnCheckedChangeListener());
        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }

    private final class MySimpleOnCheckedChangeListener extends SimpleOnCheckedChangeListener {
        @Override
        public void onPositionChanged(RadioGroup radioGroup, int position) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                radioButton.setChecked(i == position);
            }
            bv1.setMode(position);
            bv2.setMode(position);
        }
    }
}
