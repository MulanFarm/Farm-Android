package com.xcc.mylibrary.widget.photoView;

import android.content.Context;
import android.util.AttributeSet;

public class PhotoView extends RoundedImageView {
	public PhotoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhotoView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setScaleType(ScaleType.FIT_XY);
		setOval(true);
		setRoundBackground(true);
		// setBorderColor(getResources().getColor(R.color.photo_border));
		// setBorderWidth((int)getResources().getDimension(R.dimen.border_size));
	}
}
