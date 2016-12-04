package org.alex.callback;

import android.widget.RadioGroup;

/**
 * 作者：Alex
 * 时间：2016年10月03日
 * 简述：
 */
@SuppressWarnings("all")
public abstract class SimpleOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (radioGroup.getChildCount() <= 0) {
            return;
        }
        int id0 = radioGroup.getChildAt(0).getId();
        onPositionChanged(radioGroup, checkedId - id0);
    }

    public abstract void onPositionChanged(RadioGroup radioGroup, int position);
}
