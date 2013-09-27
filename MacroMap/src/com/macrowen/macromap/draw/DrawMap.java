package com.macrowen.macromap.draw;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.graphics.Paint.Align;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Region.Op;
import android.view.View;

public class DrawMap {

  public static String mMapName;
  public static View delegate;
  public static Paint mPaintBlock = new Paint();
  public static Paint mPaintLine = new Paint();
  public static Paint mPaintText = new Paint();

  private String mId;
  private String mName;

  public DrawType mDrawType = DrawType.Draw;

  protected boolean mRedraw = true;

  public Path mPath;
  public Path mTextPath;
  public Path mDrawPath;
  public Path mDrawTextPath;
  public Region mRegion;
  public PointF mStart;
  public String mDisplay = "";
  public RectF mRect;
  public RectF mBorder = null;
  public PointF mOffset = new PointF(0, 0);

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

  public void onDraw(Canvas canvas) {
  }

  public void onDrawBlock(Canvas canvas) {
    if (mDrawType == DrawType.NoDraw) {
      return;
    }
    if (mDrawType == DrawType.Draw) {
      Path path = new Path(mPath);
      Matrix matrix = new Matrix();
      matrix.setTranslate(mOffset.x, mOffset.y);
      path.transform(matrix);
      float scale = mScale;
      matrix.setScale(scale, scale, delegate.getWidth() / 2, delegate.getHeight() / 2);
      path.transform(matrix);
      Rect rect = new Rect(-delegate.getWidth() / 3, -delegate.getHeight() / 3, delegate.getWidth() * 4 / 3, delegate.getHeight() * 4 / 3);
      RectF rectf = new RectF();
      path.computeBounds(rectf, false);
      Region region = new Region(rect);
      region.setPath(path, region);
      region.op(rect, region, Op.INTERSECT);
      if (region.isEmpty()) {
        mRegion = null;
        mDrawType = DrawType.NoDraw;
        return;
      }
      mRegion = region;
      path = mRegion.getBoundaryPath();
      path.close();
      mDrawPath = path;
    }
    Paint paint = mPaintBlock;
    // paint.setAntiAlias(true);
    paint.setColor(mFilledColor);
    canvas.drawPath(mDrawPath, paint);
  }

  public void onDrawLine(Canvas canvas) {
    if (mDrawType == DrawType.NoDraw) {
      return;
    }
    if (mDrawType == DrawType.Draw) {
      if (mRegion == null) {
        return;
      }
    }
    Paint paint = mPaintLine;
    paint.setAntiAlias(true);
    paint.setStyle(Style.STROKE);
    paint.setStrokeWidth(mBorderSize);// * (float)
    // Math.sqrt(scale /
    // mScale));
    paint.setColor(mBorderColor);
    canvas.drawPath(mDrawPath, paint);
  }

  public void onDrawPath(JSONArray jsonArray) {
    mPath = new Path();
    PointF point = new PointF(0, 0);
    int linecount = 0;
    PointF points[] = new PointF[jsonArray.length()];
    // int n = 0;
    int index = 0;
    float length = 0;
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONArray json = jsonArray.optJSONArray(i);
      String t = json.optString(0);
      if (t.equals("M")) {
        point = getPoint(json.optJSONArray(1));
        mStart = point;
        mPath.moveTo(point.x, point.y);
        points[0] = point;
      } else if (t.equals("Z")) {
        mPath.lineTo(mStart.x, mStart.y);
        mPath.close();
      } else if (t.equals("A")) {
        float rx = (float) json.optDouble(1);// * mScale;
        float ry = (float) json.optDouble(2);// * mScale;
        if (rx != ry) {
        }
        float rotation = (float) json.optDouble(3);
        if (rotation != 0) {
        }
        PointF center = getPoint(json.optJSONArray(4));
        float startAngle = -(float) json.optDouble(5);
        float sweepAngle = -(float) json.optDouble(6);
        RectF oval = new RectF(center.x - rx, center.y - ry, center.x + rx, center.y + ry);
        linecount++;
        if (linecount > 0) {// && linecount < 6) {
          point =
              new PointF((float) (center.x + rx * Math.cos(startAngle + sweepAngle)), (float) (center.y + ry
                  * Math.sin(startAngle + sweepAngle)));
          points[linecount] = point;
          if (linecount > 1) {
            if (threepointsoneline(points[linecount], points[linecount - 1], points[linecount - 2])) {
              points[linecount - 1] = points[linecount];
              linecount--;
            }
          }
          if (linecount > 0) {
            float len = distance(points[linecount - 1], points[linecount]);
            if (len > length) {
              index = linecount;
              length = len;
            }
          }
        }
        startAngle = (float) (startAngle * 180 / Math.PI);
        sweepAngle = (float) (sweepAngle * 180 / Math.PI);
        mPath.arcTo(oval, startAngle, sweepAngle / 2);
        mPath.arcTo(oval, startAngle + sweepAngle / 2, sweepAngle / 2);
      } else if (t.equals("L")) {
        point = getPoint(json.optJSONArray(1));
        mPath.lineTo(point.x, point.y);
        linecount++;
        if (linecount > 0) {// && linecount < 6) {
          points[linecount] = point;
          if (linecount > 1) {
            if (threepointsoneline(points[linecount], points[linecount - 1], points[linecount - 2])) {
              points[linecount - 1] = points[linecount];
              linecount--;
            }
          }
          if (linecount > 0) {
            float len = distance(points[linecount - 1], points[linecount]);
            if (len > length) {
              index = linecount;
              length = len;
            }
          }
        }
      }
    }
    if (linecount == 6) {
      if (threepointsoneline(points[0], points[1], points[5])) {
        points[0] = points[5];
        linecount--;
      }
    }
    if (linecount == 5) {
      if (threepointsoneline(points[0], points[1], points[4])) {
        points[0] = points[4];
        linecount--;
      }
    }
    if (linecount == 4) {
      mTextPath = new Path();
      float x1, x2, y1, y2;
      if (distance(points[0], points[1]) < distance(points[1], points[2])) {
        x1 = (points[0].x + points[1].x) / 2;
        y1 = (points[0].y + points[1].y) / 2;
        x2 = (points[2].x + points[3].x) / 2;
        y2 = (points[2].y + points[3].y) / 2;
      } else {
        x1 = (points[2].x + points[1].x) / 2;
        y1 = (points[2].y + points[1].y) / 2;
        x2 = (points[0].x + points[3].x) / 2;
        y2 = (points[0].y + points[3].y) / 2;
      }
      if (x1 < x2 || (x1 == x2 && y1 < y2)) {
        mTextPath.moveTo(x1, y1);
        mTextPath.lineTo(x2, y2);
      } else {
        mTextPath.moveTo(x2, y2);
        mTextPath.lineTo(x1, y1);
      }
      mTextWidth = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    } else if (index > 0 && (!(mDisplay == null || mDisplay.trim().equals("") || mDisplay.equalsIgnoreCase("null")))) {
      Path path = new Path();
      float x1, x2, y1, y2;
      x1 = points[index].x;
      y1 = points[index].y;
      x2 = points[index - 1].x;
      y2 = points[index - 1].y;
      path.moveTo(x1 - (y2 - y1) * 10, y1 + (x2 - x1) * 10);
      path.lineTo(x1 + (y2 - y1) * 10, y1 - (x2 - x1) * 10);
      path.lineTo(x2 + (y2 - y1) * 10, y2 - (x2 - x1) * 10);
      path.lineTo(x2 - (y2 - y1) * 10, y2 + (x2 - x1) * 10);
      path.lineTo(x1 - (y2 - y1) * 10, y1 + (x2 - x1) * 10);
      Path mp = new Path(mPath);
      Matrix matrix = new Matrix();
      matrix.setScale(0.1f, 0.1f);
      mp.transform(matrix);
      path.transform(matrix);
      RectF rf = new RectF();
      Rect rect = new Rect();
      path.computeBounds(rf, false);
      rf.round(rect);
      Region rg = new Region(rect);
      rg.setPath(path, rg);
      path.close();
      mp.computeBounds(rf, false);
      rf.round(rect);
      Region region = new Region(rect);
      region.setPath(mp, region);
      mp.close();
      rg.op(region, rg, Op.INTERSECT);
      rect = rg.getBounds();
      float dx = 1;
      float dy = 0;
      if (y2 - y1 < 0.01) {
        dy = 1;
      }
      Path p = new Path();
      p.moveTo(rect.centerX() - (x2 - x1) / 10, rect.centerY() - (y2 - y1) / 10);
      p.lineTo(rect.centerX() + (x2 - x1) / 10, rect.centerY() + (y2 - y1) / 10);
      p.lineTo(rect.centerX() + (x2 - x1) / 10 + dx, rect.centerY() + (y2 - y1) / 10 + dy);
      p.lineTo(rect.centerX() - (x2 - x1) / 10 + dx, rect.centerY() - (y2 - y1) / 10 + dy);
      rg = new Region(rect);
      rg.setPath(p, region);
      p.close();
      rg.op(region, rg, Op.INTERSECT);
      rect = rg.getBounds();
      boolean k = (x2 - x1) * (y2 - y1) < 0;
      mTextPath = new Path();
      mTextPath.moveTo(rect.left, k ? rect.bottom : rect.top);
      mTextPath.lineTo(rect.right, k ? rect.top : rect.bottom);
      matrix.setScale(10, 10);
      mTextPath.transform(matrix);
      // rect = rg.getBounds();
      mTextWidth = 10 * (float) Math.sqrt(rect.width() * rect.width() + rect.height() * rect.height());
      mTextPath = null;
    }
    mRect = new RectF();
    mPath.computeBounds(mRect, true);
    if (mTextWidth < 0.01) {
      mTextPath = null;
    }
  }

  public void onDrawText(Canvas canvas) {
    if (mDrawType == DrawType.NoDraw) {
      return;
    }
    if (mDisplay == null || mDisplay.trim().equals("") || mDisplay.equalsIgnoreCase("null")) {
      mDrawType = DrawType.ReDraw;
      return;
    }
    Paint paint = mPaintText;
    paint.setTypeface(Typeface.DEFAULT);
    if (mDrawType == DrawType.Draw) {
      mDrawType = DrawType.ReDraw;
      Region region = new Region(mRegion);
      Rect rect = region.getBounds();
      if (!region.contains(rect.centerX(), rect.centerY())) {
        Rect r = new Rect(rect);
        Region rg = new Region(region);
        if (rect.width() > rect.height()) {
          r.right -= rect.width() / 2;
        } else {
          r.bottom -= rect.height() / 2;
        }
        rg.op(r, region, Op.INTERSECT);
        r = rg.getBounds();
        rect = r;
        region = rg;
      }
      float size = 100;
      Path path = new Path();
      paint.setTextSize(size);
      float width = paint.measureText(mDisplay);
      if (mTextPath == null) {
        PointF point = new PointF(rect.centerX(), rect.centerY());
        float w = rect.width();
        float h = rect.height();
        boolean dir = true;
        if (rect.width() < rect.height() * 0.9) {
          w = rect.height();
          h = rect.width();
          dir = false;
        }
        size = (float) Math.sqrt(size * w / width / 20) * 20;
        if (size > h * 0.9) {
          size = h * 0.9f;
        }
        Rect r;
        if (rect.width() > rect.height() * 0.9) {
          r = new Rect((int) rect.left, (int) (point.y - 1), (int) rect.right, (int) (point.y + 1));
        } else {
          r = new Rect((int) (point.x - 1), (int) rect.top, (int) (point.x + 1), (int) rect.bottom);
        }
        Region rg = new Region(region);
        rg.op(r, region, Op.INTERSECT);
        r = rg.getBounds();
        // logd("" + rect + r);
        rect = r;
        // region = rg;
        if (rect.width() > rect.height() * 0.9) {
          w /= rect.width();
        } else {
          w /= rect.height();
        }
        size = size / (float) Math.sqrt(w);
        point = new PointF(rect.centerX(), rect.centerY());
        paint.setTextSize(size);
        width = paint.measureText(mDisplay);
        if (rect.width() > rect.height() * 0.8) {
          r = new Rect((int) (point.x - width / 2), (int) (point.y - size * 4), (int) (point.x + width / 2), (int) (point.y + size * 4));
        } else {
          r = new Rect((int) (point.x - size * 4), (int) (point.y - width / 2), (int) (point.x + size * 4), (int) (point.y + width / 2));
        }
        rg = new Region(region);
        rg.op(r, region, Op.INTERSECT);
        r = rg.getBounds();
        // logd("" + rect + r);
        rect = r;
        region = rg;
        w = (rect.width() > rect.height() * 0.8) ? rect.width() : rect.height();
        width = paint.measureText(mDisplay);
        if (width > w) {
          size *= w / width;
        }
        point = new PointF(rect.centerX(), rect.centerY());
        if (dir) {
          path.moveTo(rect.left, point.y);
          path.lineTo(rect.right, point.y);
          h = rect.height();
        } else {
          path.moveTo(point.x, rect.top);
          path.lineTo(point.x, rect.bottom);
          h = rect.width();
        }
        if (size > h * 0.9) {
          size = h * 0.9f;
        }
        if (size < mMiniumSize) {
          // if (mHighlight) {
          // size = mMiniumSize;
          // } else {
          mDrawTextSize = 0;
          return;
          // }
        }
      } else {
        path = new Path(mTextPath);
        Matrix matrix = new Matrix();
        matrix.setTranslate(mOffset.x, mOffset.y);
        path.transform(matrix);
        float scale = mScale;
        matrix.setScale(scale, scale, delegate.getWidth() / 2, delegate.getHeight() / 2);
        path.transform(matrix);
        size = (float) Math.sqrt(size * mTextWidth * scale / width / 20) * 20;
        if (size < mMiniumSize) {
          // logd("size=" + size);
          // if (mHighlight) {
          // size = mMiniumSize;
          // } else {
          mDrawTextSize = 0;
          return;
          // }
        }
      }
      mDrawTextSize = size;
      mDrawTextPath = path;
    }
    if (mDrawTextSize < mMiniumSize) {
      return;
    }
    paint.setStrokeWidth(1);
    paint.setTextSize(mDrawTextSize);
    paint.setAntiAlias(true);
    paint.setStyle(Style.FILL);
    paint.setTextAlign(Align.CENTER);
    paint.setColor(mTextColor);
    canvas.drawTextOnPath(mDisplay, mDrawTextPath, 0, mDrawTextSize / 2.4f, paint);
  }

  public void onInfo(JSONArray jsonArray) {
  }

  public void reDraw() {
    this.mRedraw = true;
  }

  public void reDraw(boolean reDraw) {
    this.mRedraw = reDraw;
  }

  public void setId(String mId) {
    this.mId = mId;
  }

  public void setName(String mName) {
    this.mName = mName;
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
