package com.macrowen.macromap.draw;

import java.util.HashMap;
import java.util.Map.Entry;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

@SuppressLint("DrawAllocation")
public class Floor extends DrawLayer {

  HashMap<PointF, Shop> mShops = new HashMap<PointF, Shop>();
  HashMap<PointF, PublicService> mPublicServices = new HashMap<PointF, PublicService>();

  private int mIndex;
  private String mAlias;

  private Canvas shopCanvas;
  private Canvas textCanvas;

  private JSONObject mJson;

  public Floor(String id, String name, int index) {
    setId(id);
    setName(name);
    this.setIndex(index);
    mShops = new HashMap<PointF, Shop>();

    mBorderSize = 3;
    mBorderColor = Color.BLUE;
  }

  public void drawLayer(DrawLayer draw) {
    this.support(draw);
    draw.onDraw(shopCanvas);
    draw.onDraw(textCanvas);
    draw.onDraw(textCanvas);
  }

  public String getAlias() {
    return mAlias;
  }

  public int getIndex() {
    return mIndex;
  }

  public JSONObject getInitJson() {
    return mJson;
  }

  @Override
  public void onDraw(Canvas canvas) {
    Paint paint = new Paint();
    paint.setColor(Color.WHITE);
    floorLayer = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
    Canvas c = new Canvas(floorLayer);
    c.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);
    canvas.drawPaint(paint);
    this.drawFloor(c);
    canvas.drawBitmap(floorLayer, -delegate.getWidth() / 3, -delegate.getHeight() / 3, paint);

    shopLayer = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
    shopCanvas = new Canvas(shopLayer);
    shopCanvas.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);

    textLayer = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
    textCanvas = new Canvas(textLayer);
    textCanvas.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);

    for (Entry<PointF, Shop> entry : mShops.entrySet()) {
      Shop value = entry.getValue();
      if (mDrawType == DrawType.Draw) {
        value.mDrawType = DrawType.Draw;
      }
      // drawLayer(value);
    }

    for (Entry<PointF, PublicService> entry : mPublicServices.entrySet()) {
      PublicService value = entry.getValue();
      if (mDrawType == DrawType.Draw) {
        value.mDrawType = DrawType.Draw;
      }
      drawLayer(value);
    }

    canvas.drawBitmap(shopLayer, -delegate.getWidth() / 3, -delegate.getHeight() / 3, null);
    canvas.drawBitmap(textLayer, -delegate.getWidth() / 3, -delegate.getHeight() / 3, null);
  }

  @Override
  public void onInfo(JSONArray jsonArray) {
  }

  public void setAlias(String alias) {
    this.mAlias = alias;
  }

  public void setIndex(int index) {
    this.mIndex = index;
  }

  public void setInitJson(JSONObject json) {
    this.mJson = json;
    setId(json.optString("id"));
    setName(json.optString("name"));
    setAlias(json.optString("alias"));
    setIndex(json.optInt("index"));

    json = json.optJSONObject("layers");

    JSONArray objs = json.optJSONArray("frame").optJSONArray(1);
    for (int i = 0; i < objs.length(); i++) {
      JSONArray obj = objs.optJSONArray(i);
      callPath(obj);
      mBorder = mRect;
    }

    objs = json.optJSONArray("shop").optJSONArray(1);
    for (int i = 0; i < objs.length(); i++) {
      JSONArray obj = objs.optJSONArray(i);
      Shop shop = new Shop();
      shop.setJson(obj);
      if (mShops.get(shop.mStart) != null) {
        shop.mStart.x += 0.01;
      }
      mShops.put(shop.mStart, shop);
    }

    objs = json.optJSONArray("public_service").optJSONArray(1);
    for (int i = 0; i < objs.length(); i++) {
      JSONArray obj = objs.optJSONArray(i);
      PublicService publicservice = new PublicService();
      publicservice.setJson(obj);
      mPublicServices.put(publicservice.mStart, publicservice);
    }
  }

  @Override
  public String toString() {
    return mMapName + " —— " + getName();
  }

  private void drawFloor(Canvas canvas) {
    this.onDrawBlock(canvas);
    this.onDrawLine(canvas);
  }

}
