package com.macrowen.macromap.draw;

import com.macrowen.macromap.R;

import android.view.View;
import android.widget.ImageButton;

import android.widget.TextView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class ShopPosition extends RelativeLayout {

  PointMessage shop;

  public boolean mShow = false;

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

  public void setShop(PointMessage shop) {
    this.shop = shop;
    setText(shop.getName());
  }

  void setText(String display) {
    TextView text = (TextView) findViewById(R.id.shop_display);
    text.setText(display);
  }

  private void init() {
    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.shopposition, this, true);
    ImageButton share = (ImageButton) findViewById(R.id.shop_share);
    share.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // if (mOnMapEventListener != null && mShop != null) {
        // mOnMapEventListener.OnMapEvent(mShop.mId, OnMapEventType.MapClickedLeft);
        // }
      }
    });
    ImageButton more = (ImageButton) findViewById(R.id.shop_more);
    more.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // if (mOnMapEventListener != null && mShop != null) {
        // mOnMapEventListener.OnMapEvent(mShop.mId, OnMapEventType.MapClickedRight);
        // }
      }
    });
  }
}
