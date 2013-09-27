package com.macrowen.macromap.draw;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.view.MotionEvent;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ScrollView;

public class MacroMap extends ScrollView {

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      int what = msg.what;
      File file = (File) msg.obj;
      if (what == 0) {
        loadMapJson(file);
      } else {
        loadFloorJson(file);
      }
    };
  };

  private DownLoad downLoad;

  private Map mMap;

  private boolean mIsScale = false;
  private boolean mHasMoved = false;
  private boolean mIsMove = false;
  private float mLastScale;
  private float mLastX;
  private float mLastY;

  public MacroMap(Context context) {
    super(context);
    init(null, 0);
  }

  public MacroMap(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public MacroMap(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  public void loadMap(final String mapId, String mapName) {
    if (mMap != null) {
      return;
    }
    mMap = new Map();
    mMap.setId(mapId);
    mMap.setName(mapName);
    DrawMap.mMapName = mapName;
    DrawMap.delegate = this;
    loadMapFile(mapId);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getPointerCount() == 1) {
      float ex = event.getX();// - mLocationX;
      float ey = event.getY();// - mLocationY;
      mIsScale = false;
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        mIsMove = true;
        mHasMoved = false;
        mLastX = ex;
        mLastY = ey;
      } else if (!mIsMove) {

      } else if (!mHasMoved && event.getAction() == MotionEvent.ACTION_UP) {

      } else if ((Math.abs(ex - mLastX) + Math.abs(ey - mLastY) > 10)
          && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)) {
        mHasMoved = true;
        // mMap.reDraw(event.getAction() == MotionEvent.ACTION_UP);
        mMap.getCurFloor().addOffset(ex - mLastX, ey - mLastY);
        mLastX = ex;
        mLastY = ey;
      } else if (mHasMoved && event.getAction() == MotionEvent.ACTION_UP) {
        mIsMove = false;
        mMap.reDraw();
        invalidate();
      }
    } else {
      mIsMove = false;
      mHasMoved = false;
      if (!mIsScale) {
        mIsScale = true;
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float r = (float) Math.sqrt(x * x + y * y);
        mLastScale = r;
        mLastX = (event.getX(1) + event.getX(0)) / 2;// - mLocationX;
        mLastY = (event.getY(1) + event.getY(0)) / 2;// - mLocationY;
      } else if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
        mMap.reDraw(event.getAction() == MotionEvent.ACTION_POINTER_UP);
        // mShopPosition.setVisibility(INVISIBLE);
        // mShopPosition.mShow = false;
        // mRedraw = false;
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float r = (float) Math.sqrt(x * x + y * y);
        mMap.getCurFloor().addScale(r / mLastScale);
        x = (event.getX(1) + event.getX(0)) / 2;// - mLocationX;
        y = (event.getY(1) + event.getY(0)) / 2;// - mLocationY;
        mMap.getCurFloor().addOffset(x - ((mLastX - getLeft() - getWidth() / 2) * r / mLastScale + getLeft() + getWidth() / 2),
            y - ((mLastY - getTop() - getHeight() / 2) * r / mLastScale + getTop() + getHeight() / 2));
        mLastX = x;
        mLastY = y;
        mLastScale = r;
      } else if (mIsScale
          && (event.getAction() == MotionEvent.ACTION_POINTER_UP || (event.getAction() & 0xFF) == (MotionEvent.ACTION_POINTER_UP))) {
        mMap.reDraw();
      }
    }
    // return super.onTouchEvent(event);
    return true;
  }

  public void setFloor(String floorId) {
    mMap.setCurFloor(floorId);
    Floor floor = mMap.getCurFloor();
    if (floor.getJson() != null) {
      return;
    }
    loadFloorFile(floor.getId());
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // if (!dataReady) {
    // return;
    // }
    mMap.onDraw(canvas);
  }

  private void addFloor() {
    JSONArray jsonArray = mMap.getJson();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject json = jsonArray.optJSONObject(i);
      String id = json.optString("id");
      String name = json.optString("name");
      int index = json.optInt("index");
      Floor floor = new Floor(id, name, index);
      mMap.addFloor(floor);
    }
    setFloor("18");
  }

  private byte[] getByte(File file) {
    try {
      FileInputStream input = new FileInputStream(file);
      byte[] buf = new byte[input.available()];
      input.read(buf);
      input.close();
      return buf;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void init(AttributeSet attrs, int defStyle) {
    downLoad = new DownLoad(mHandler);
  }

  private File laodFileByURL(String url) {
    try {
      File file = Environment.getExternalStorageDirectory();
      file = new File(file, "/Palmap/MacroMap/" + Base64.encodeToString(url.getBytes(), Base64.NO_WRAP));
      if (file.length() < 4) {
        file.getParentFile().mkdirs();
        file.createNewFile();
        downLoad.execute(url);
        return null;
      }
      return file;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void loadFloorFile(String floorId) {
    final File file = laodFileByURL(DownLoad.getURLbyFloor(mMap.getId(), floorId));
    if (file == null) {
      return;
    }
    mHandler.post(new Runnable() {

      @Override
      public void run() {
        loadFloorJson(file);
      }
    });
  }

  private void loadFloorJson(File file) {
    try {
      String json = EncodingUtils.getString(getByte(file), "UTF-8");
      JSONObject obj = new JSONObject(json);
      mMap.getCurFloor().setJson(obj);
      mMap.getCurFloor().addScale(1);
      mMap.reDraw();
      // dataReady = true;
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void loadMapFile(String mapId) {
    final File file = laodFileByURL(DownLoad.getURLbyMall(mapId));
    if (file == null) {
      return;
    }
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        loadMapJson(file);
      }
    });
  }

  private void loadMapJson(File file) {
    try {
      String json = EncodingUtils.getString(getByte(file), "UTF-8");
      JSONArray obj = new JSONArray(json);
      mMap.setJson(obj);
      addFloor();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
