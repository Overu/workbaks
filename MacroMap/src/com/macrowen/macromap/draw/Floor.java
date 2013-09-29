package com.macrowen.macromap.draw;

import com.macrowen.macromap.draw.data.JSONData;

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
public class Floor extends DrawLayer<JSONObject> {

  HashMap<PointF, Shop> mShops = new HashMap<PointF, Shop>();
  HashMap<PointF, PublicService> mPublicServices = new HashMap<PointF, PublicService>();

  private int mIndex;
  private String mAlias;

  private Canvas shopCanvas;
  private Canvas textCanvas;

  public Floor(String id, String name, int index) {
    setId(id);
    setName(name);
    this.setIndex(index);
    mShops = new HashMap<PointF, Shop>();

    mBorderSize = 3;
    mBorderColor = Color.BLUE;
  }

  @SuppressLint("WrongCall")
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

  @Override
  public PointMessage getPointMessage(float x, float y) {
    DrawMap<?> unit = null;
    PointMessage pointMessage = null;
    if (unit == null) {
      for (Entry<PointF, PublicService> entry : mPublicServices.entrySet()) {
        PublicService u = entry.getValue();
        if (u.mBlockRegion != null && u.mBlockRegion.contains((int) x, (int) y)) {
          unit = u;
          break;
        }
      }
    }
    if (unit == null) {
      for (Entry<PointF, Shop> entry : mShops.entrySet()) {
        Shop u = entry.getValue();
        if (u.mBlockRegion != null && u.mBlockRegion.contains((int) x, (int) y)) {
          unit = u;
          break;
        }
      }
    }
    if (unit != null && unit.mBlockRegion != null && !(unit.mDisplay.equals("") || unit.mDisplay.equalsIgnoreCase("null"))) {
      PointF p = unit.mTextCenter;
      if (p == null) {
        p = unit.mStart;
      }
      p = new PointF(p.x, p.y);
      p.offset(mOffset.x, mOffset.y);
      pointMessage = new PointMessage();
      pointMessage.setId(unit.mId);
      pointMessage.setName(unit.mDisplay);
      pointMessage.setType(unit.mType);
      return pointMessage;
    }
    return pointMessage;
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
      drawLayer(value);
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

  @Override
  public void setData(JSONData<JSONObject> mData) {
    super.setData(mData);
    JSONObject json = mData.getData();
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
      shop.setData(new com.macrowen.macromap.draw.data.JSONArray(obj));
      if (mShops.get(shop.mStart) != null) {
        shop.mStart.x += 0.01;
      }
      mShops.put(shop.mStart, shop);
    }

    objs = json.optJSONArray("public_service").optJSONArray(1);
    for (int i = 0; i < objs.length(); i++) {
      JSONArray obj = objs.optJSONArray(i);
      PublicService publicservice = new PublicService();
      publicservice.setData(new com.macrowen.macromap.draw.data.JSONArray(obj));
      mPublicServices.put(publicservice.mStart, publicservice);
    }
  }

  public void setIndex(int index) {
    this.mIndex = index;
  }

  @Override
  public String toString() {
    return DrawLayer.mMapName + " —— " + getName();
  }

  private void drawFloor(Canvas canvas) {
    this.onDrawBlock(canvas);
    this.onDrawLine(canvas);
  }

}
