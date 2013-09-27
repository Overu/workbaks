package com.macrowen.macromap.draw;

import org.json.JSONArray;

import android.graphics.Canvas;

import android.graphics.Color;

public class Shop extends DrawMap {

  private String mType;

  private JSONArray mJson;

  public Shop() {
    setName(mDisplay);
    mBorderColor = Color.MAGENTA;
    mFilledColor = Color.YELLOW;
    mFilledColor =
        (int) Math.round(Math.random() * 32 + 224) + (int) Math.round(Math.random() * 32 + 224) * 256
            + (int) Math.round(Math.random() * 32 + 224) * 256 * 256 + (int) Math.round(Math.random() * 32 + 224) * 256 * 256 * 256;
    mTextColor = Color.BLACK;
    mBorderSize = 1;
  }

  public JSONArray getJson() {
    return mJson;
  }

  public String getType() {
    return mType;
  }

  @Override
  public void onDraw(Canvas canvas) {
  }

  @Override
  public void onInfo(JSONArray jsonArray) {
    mDisplay = jsonArray.optString(0);
    JSONArray json = jsonArray.optJSONArray(1);
    if (json != null) {
      mTextCenter = getPoint(json);
    }
    setType(jsonArray.optString(3));
    setId(String.valueOf(jsonArray.optInt(4)));
  }

  public void setJson(JSONArray json) {
    this.mJson = json;
    this.callPath(json);
  }

  public void setType(String type) {
    this.mType = type;
  }
}
