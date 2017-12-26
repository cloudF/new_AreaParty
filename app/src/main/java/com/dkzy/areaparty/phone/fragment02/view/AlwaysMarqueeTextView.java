package com.dkzy.areaparty.phone.fragment02.view;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by ervincm on 2017/11/10.
 */

public class AlwaysMarqueeTextView extends android.support.v7.widget.AppCompatTextView {
          
            /** 
      * constructor 
      * @param context Context 
      */  
            public AlwaysMarqueeTextView(Context context) {
                  super(context);  
            }  
          
            /** 
      * constructor 
      * @param context Context 
      * @param attrs AttributeSet 
      */  
            public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
                  super(context, attrs);  
            }  
          
            /** 
      * constructor 
      * @param context Context 
      * @param attrs AttributeSet 
      * @param defStyle int 
      */  
            public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
                  super(context, attrs, defStyle);  
            }  
          
              
            @Override
            public boolean isFocused() {  
                  return true;  
            }  
            }
