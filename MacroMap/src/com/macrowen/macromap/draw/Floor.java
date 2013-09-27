package com.macrowen.macromap.draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Floor extends DrawMap {

  HashMap<PointF, Shop> mShops = new HashMap<PointF, Shop>();

  private int mIndex;
  private String mAlias;

  private JSONObject mJson;

  public Floor(String id, String name, int index) {
    setId(id);
    setName(name);
    this.setIndex(index);
    mShops = new HashMap<PointF, Shop>();

    mBorderSize = 3;
    mBorderColor = Color.BLUE;
  }

  public String getAlias() {
    return mAlias;
  }

  public int getIndex() {
    return mIndex;
  }

  public JSONObject getJson() {
    return mJson;
  }

  @Override
  public void onDraw(Canvas canvas) {
    Paint paint = new Paint();
    paint.setColor(Color.WHITE);
    canvas.drawPaint(paint);
    this.drawFloor(canvas);

    List<DrawMap> units = new ArrayList<DrawMap>();

    for (Entry<PointF, Shop> entry : mShops.entrySet()) {
      units.add(entry.getValue());
    }
    int i = 0;
    for (DrawMap drawMap : units) {
      if (mDrawType == DrawType.Draw) {
        drawMap.mDrawType = DrawType.Draw;
      }
      if (i == 0) {
        Log.e("aaaaaaaaaaaa", drawMap.mDisplay);
      }
      // if (i == 200) {
      // break;
      // }
      i++;
      this.support(drawMap);
      drawMap.onDraw(canvas);
    }
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

  public void setJson(JSONObject json) {
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
