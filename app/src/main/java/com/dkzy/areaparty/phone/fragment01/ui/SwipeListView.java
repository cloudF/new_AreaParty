package com.dkzy.areaparty.phone.fragment01.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * Created by SnowMonkey on 2017/4/5.
 */

public class SwipeListView extends SwipeMenuListView {
    public SwipeListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }catch (Exception e){e.printStackTrace();}

    }


}
