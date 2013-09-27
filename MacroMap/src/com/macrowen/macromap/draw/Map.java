package com.macrowen.macromap.draw;

import java.util.HashMap;

import android.graphics.Rect;
import android.graphics.RectF;

import android.graphics.PointF;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

@SuppressLint("DrawAllocation")
public class Map extends DrawMap {

  private HashMap<String, Floor> floors;

  private Floor mCurFloor;
  private JSONArray mJson;

  private Bitmap mMapBitmap;
  private Bitmap mBmp;

  public Map() {
    floors = new HashMap<String, Floor>();
  }

  public void addFloor(Floor floor) {
    getFloors().put(floor.getId(), floor);
  }

  public void addFloor(String id, String name, int index) {
    Floor floor = new Floor(id, name, index);
    getFloors().put(id, floor);
  }

  public Floor getCurFloor() {
    return mCurFloor;
  }

  public HashMap<String, Floor> getFloors() {
    return floors;
  }

  public JSONArray getJson() {
    return mJson;
  }

  @Override
  public void onDraw(Canvas canvas) {
    Floor mFloor = this.getCurFloor();
    if (mFloor == null) {
      return;
    }
    Paint paint = new Paint();
    if (mRedraw) {
      mRedraw = false;
      // paint.setColor(Color.WHITE);
      mBmp = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
      Canvas cc = new Canvas(mBmp);
      cc.drawPaint(paint);
      cc.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);
      float scale = mFloor.mScale;
      mFloor.mDrawType = DrawType.Draw;
      mFloor.mScale = scale / 2;
      mFloor.onDraw(cc);
      mFloor.mScale = scale;
      mFloor.mDrawType = DrawType.Draw;
      mMapBitmap = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
      Canvas c = new Canvas(mMapBitmap);
      c.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);
      mFloor.onDraw(c);
      canvas.drawBitmap(mMapBitmap, -delegate.getWidth() / 3, -delegate.getHeight() / 3, paint);
      mFloor.mLastScale = mFloor.mScale;
      mFloor.mLastOffset = new PointF(mFloor.mOffset.x, mFloor.mOffset.y);
    } else {
      {
        float lastScale = mFloor.mLastScale / 2;
        Rect rect = new Rect(0, 0, delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3);
        float x =
            (mFloor.mOffset.x - mFloor.mLastOffset.x) * mFloor.mScale - delegate.getWidth() / 2 * (mFloor.mScale - lastScale) / lastScale
                - delegate.getWidth() / 3 * mFloor.mScale / lastScale;
        float y =
            (mFloor.mOffset.y - mFloor.mLastOffset.y) * mFloor.mScale - delegate.getHeight() / 2 * (mFloor.mScale - lastScale) / lastScale
                - delegate.getHeight() / 3 * mFloor.mScale / lastScale;
        RectF rectf =
            new RectF(x, y, x + delegate.getWidth() * 5 / 3 * mFloor.mScale / lastScale, y + delegate.getHeight() * 5 / 3 * mFloor.mScale
                / lastScale);
        canvas.drawBitmap(mBmp, rect, rectf, paint);
      }
      float w = 5;
      Rect rect = new Rect((int) w, (int) w, delegate.getWidth() * 5 / 3 - (int) w, delegate.getHeight() * 5 / 3 - (int) w);
      float x =
          (mFloor.mOffset.x - mFloor.mLastOffset.x) * mFloor.mScale - delegate.getWidth() / 2 * (mFloor.mScale - mFloor.mLastScale)
              / mFloor.mLastScale - delegate.getWidth() / 3 * mFloor.mScale / mFloor.mLastScale + w * mFloor.mScale;
      float y =
          (mFloor.mOffset.y - mFloor.mLastOffset.y) * mFloor.mScale - delegate.getHeight() / 2 * (mFloor.mScale - mFloor.mLastScale)
              / mFloor.mLastScale - delegate.getHeight() / 3 * mFloor.mScale / mFloor.mLastScale + w * mFloor.mScale;
      RectF rectf =
          new RectF(x, y, x + delegate.getWidth() * 5 / 3 * mFloor.mScale / mFloor.mLastScale - 2 * w * mFloor.mScale, y
              + delegate.getHeight() * 5 / 3 * mFloor.mScale / mFloor.mLastScale - 2 * w * mFloor.mScale);
      canvas.drawBitmap(mMapBitmap, rect, rectf, paint);
    }
  }

  public void setCurFloor(String id) {
    mCurFloor = getFloors().get(id);
  }

  public void setJson(JSONArray mJson) {
    this.mJson = mJson;
  }
}
