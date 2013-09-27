package com.macrowen.macromap.draw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class ShopPosition extends RelativeLayout {

	public ShopPosition(Context context) {
		super(context);
		init();
	}

	public ShopPosition(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShopPosition(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
}
