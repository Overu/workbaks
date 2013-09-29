package com.macrowen.macromap.draw;

import com.macrowen.macromap.draw.data.JSONData;

import org.json.JSONArray;

import java.util.HashMap;

import android.graphics.Bitmap.Config;

import android.graphics.Typeface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.View;

public class DrawMap<T> {

  public static HashMap<String, String> mPublicServiceIcons = new HashMap<String, String>();
  public static Typeface mTypeface;
  public static String mMapName;
  public static View delegate;

  protected static Paint mPaintBlock = new Paint();
  protected static Paint mPaintLine = new Paint();
  protected static Paint mPaintText = new Paint();
  protected static Bitmap mainLayer;
  protected static Bitmap floorLayer;
  protected static Bitmap shopLayer;
  protected static Bitmap textLayer;

  protected String mId;
  protected String mName;
  protected String mType;
  public String mDisplay = "";
  private JSONData<T> mData;

  public DrawType mDrawType = DrawType.Draw;

  protected boolean mRedraw = true;

  public Path mPath;
  public Path mTextPath;
  public Path mDrawPath;
  public Path mDrawTextPath;
  public Region mBlockRegion;
  public Region mTextRegion;
  public PointF mStart;
  public RectF mRect;
  public RectF mBorder = null;
  public PointF mOffset = new PointF(0, 0);

  public PointF mDrawTextPoint;

  public float mTextWidth;
  public PointF mTextCenter;
  public float mScale = 0.01f;
  public int mTextColor = Color.BLACK;
  public float mDrawTextSize;
  int mMiniumSize = 16;

  public float mLastScale = 1;
  public PointF mLastOffset = new PointF(0, 0);

  public int mMapMargin = 100;
  public int mBorderSize = 3;
  public int mFilledColor = Color.LTGRAY;
  public int mBorderColor = Color.BLACK;

  public void addOffset(float x, float y) {
    setOffset(mOffset.x + x / mScale, mOffset.y + y / mScale);
  }

  public void addScale(float scale) {
    setScale(scale * mScale);
  }

  public void callPath(JSONArray jsonArray) {
    if (jsonArray.length() == 2) {
      onInfo(jsonArray.optJSONArray(1));
      onDrawPath(jsonArray.optJSONArray(0));
    }
  }

  public void delegateRefush() {
    delegate.invalidate();
  }

  public float distance(PointF p, PointF q) {
    float x = p.x - q.x;
    float y = p.y - q.y;
    return x * x + y * y;
  }

  public JSONData<T> getData() {
    return mData;
  }

  public String getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }

  public PointF getPoint(JSONArray jsonArray) {
    double x = jsonArray.optDouble(0);// * mScale;
    double y = -jsonArray.optDouble(1);// * mScale;
    PointF point = new PointF((float) x, (float) y);
    // point.offset(mOffset.x, mOffset.y);
    return point;
  }

  public PointMessage getPointMessage(float x, float y) {
    return null;
  }

  public String getType() {
    return mType;
  }

  public void onDraw(Canvas canvas) {
  }

  public void onDrawBlock(Canvas canvas) {
  }

  public void onDrawLine(Canvas canvas) {
  }

  public void onDrawPath(JSONArray jsonArray) {
  }

  public void onDrawText(Canvas canvas) {
  }

  public void onInfo(JSONArray jsonArray) {
  }

  public void reDraw() {
    this.mRedraw = true;
  }

  public void reDraw(boolean reDraw) {
    this.mRedraw = reDraw;
  }

  public void setData(JSONData<T> mData) {
    this.mData = mData;
  }

  public void setId(String mId) {
    this.mId = mId;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public void setType(String type) {
    this.mType = type;
  }

  public boolean threepointsoneline(PointF p, PointF q, PointF r) {
    float a = (p.x - q.x) * (q.y - r.y);
    float b = (p.y - q.y) * (q.x - r.x);
    if (a == b) {
      // logd("a=" + a + ", b=" + b + p + q + r);
      return true;
    }
    return Math.abs((b + a) / (b - a)) > 5;
  }

  protected Canvas genLayer(Bitmap bitmap) {
    bitmap = Bitmap.createBitmap(delegate.getWidth() * 5 / 3, delegate.getHeight() * 5 / 3, Config.ARGB_8888);
    Canvas c = new Canvas(bitmap);
    c.translate(delegate.getWidth() / 3, delegate.getHeight() / 3);
    return c;
  }

  protected void support(DrawMap drawMap) {
    drawMap.mOffset = this.mOffset;
    drawMap.mScale = this.mScale;
  }

  private void setOffset(float x, float y) {
    if (mBorder == null) {
      return;
    }
    // float margin = 5 / 12f;
    // logd("mRect.width()=" + mRect.width() + ", getWidth()=" +
    // getWidth());
    if (mBorder.width() * mScale <= delegate.getWidth()) {
      x = -mBorder.left + (delegate.getWidth() - mBorder.width()) / 2;
    } else {
      // x = Math.min(x, -mBorder.left + getWidth() / 2 - getWidth() /
      // mScale * margin);
      // x = Math.max(x, -mBorder.right + getWidth() / 2 + getWidth() /
      // mScale * margin);
      x = Math.min(x, -mBorder.left + delegate.getWidth() / 2 - mMapMargin / mScale);
      x = Math.max(x, -mBorder.right + delegate.getWidth() / 2 + mMapMargin / mScale);
    }
    if (mBorder.height() * mScale <= delegate.getHeight()) {
      y = -mBorder.top + (delegate.getHeight() - mBorder.height()) / 2;
    } else {
      // y = Math.min(y, -mBorder.top + getHeight() / 2 - getHeight() /
      // mScale * margin);
      // y = Math.max(y, -mBorder.bottom + getHeight() / 2 + getHeight() /
      // mScale * margin);
      y = Math.min(y, -mBorder.top + delegate.getHeight() / 2 - mMapMargin / mScale);
      y = Math.max(y, -mBorder.bottom + delegate.getHeight() / 2 + mMapMargin / mScale);
    }
    mOffset = new PointF(x, y);
    mDrawType = DrawType.Draw;
    this.delegateRefush();
    // setPath();
  }

  private void setScale(float scale) {
    if (mBorder == null) {
      return;
    }
    scale =
        Math.max(scale, Math.min((delegate.getWidth() - mMapMargin) / mBorder.width(), (delegate.getHeight() - mMapMargin)
            / mBorder.height()));
    if (Float.isInfinite(scale) || Float.isNaN(scale)) {
      return;
    }
    mScale = scale;
    setOffset(mOffset.x, mOffset.y);
  }
}
