package com.macrowen.macromap;

import com.macrowen.macromap.draw.DrawMap;
import com.macrowen.macromap.draw.Floor;
import com.macrowen.macromap.draw.Map;
import com.macrowen.macromap.draw.PointMessage;
import com.macrowen.macromap.draw.ShopPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.graphics.drawable.Drawable;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

public class MacroMap extends ScrollView {

  public interface OnMapEventListener {
    public void OnMapEvent(int id, OnMapEventType type);
  }

  public enum OnMapEventType {
    MapClickedLeft, MapClickedRight, MapSelected, MapUnselected
  }

  public interface OnMapFloorChangedListener {
    public void OnMapFloorChanged(String fromFloorid, String toFloorid);
  }

  public class Poi {
    public int mId;
    public String mName;
    public boolean mSelected;
    public String mType;

    public Poi(int id, String name, String type, boolean selected) {
      mId = id;
      mName = name;
      mType = type;
      mSelected = selected;
    }
  }

  class ConfigureFile {
    JSONObject mConfigures;

    ConfigureFile(File file) {
      try {
        FileInputStream input = new FileInputStream(file);
        byte[] buf = new byte[input.available()];
        input.read(buf);
        input.close();
        String json = EncodingUtils.getString(buf, "UTF-8");
        mConfigures = new JSONObject(json).optJSONObject("style");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Integer getColor(String cs) {
      Integer c = null;
      try {
        c = Integer.valueOf("0x" + cs, 16);
      } catch (Throwable e) {
        // logd(e);
      }
      return c;
    }

    String getFacilityConfigure(String name) {
      if (mConfigures == null) {
        return "";
      }
      return mConfigures.optJSONObject("facility").optString(name);
    }

    String getFrameConfigure(String name) {
      if (mConfigures == null) {
        return "";
      }
      return mConfigures.optJSONObject("frame").optString(name);
    }

    Integer getInt(String cs) {
      Integer c = null;
      try {
        c = Integer.valueOf(cs);
      } catch (Throwable e) {
        // logd(e);
      }
      return c;
    }

    String getShopCategoryConfigure(String name) {
      if (mConfigures == null || name == null) {
        return "";
      }
      JSONObject json = mConfigures.optJSONObject("shop").optJSONObject("category");
      if (json == null) {
        return "";
      }
      for (int i = 1; i < name.length(); i++) {
        String n = name.substring(0, i);
        String c = json.optString(n);
        if (c != null && !c.isEmpty()) {
          return c;
        }
      }
      return "";
    }

    String getShopConfigure(String name) {
      if (mConfigures == null) {
        return "";
      }
      return mConfigures.optJSONObject("shop").optString(name);
    }

  }

  class DownloadJson implements Runnable {

    File mFile;
    String mFloorid;
    String mMallid;
    String mUrl;

    DownloadJson(String mallid, String url) {
      mMallid = mallid;
      mFloorid = null;
      mUrl = url;
    }

    DownloadJson(String mallid, String floorid, String url) {
      mMallid = mallid;
      mFloorid = floorid;
      mUrl = url;
    }

    @Override
    public void run() {
      try {
        mFile = Environment.getExternalStorageDirectory();
        mFile = new File(mFile, "/Palmap/MacroMap/" + Base64.encodeToString(mUrl.getBytes(), Base64.NO_WRAP));
        logd("url=" + mUrl + ", file=" + mFile.getAbsolutePath());
        if (mFile.length() < 4) {
          mFile.getParentFile().mkdirs();
          mFile.createNewFile();
          downloadJson(mUrl, mFile);
        }
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            if (mFloorid != null) {
              setJson(mMallid, mFloorid, mFile);
            } else {
              setJson(mMallid, mFile);
            }
          }
        });
      } catch (Throwable e) {
        logd(e);
      }
    }
  }

  enum DrawType {
    Draw, NoDraw, ReDraw
  }

  static void logd(String log) {
    StackTraceElement ste = new Throwable().getStackTrace()[1];
    Log.d(ste.getClassName(), "at " + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + "  " + log);
  }

  static void logd(Throwable e) {
    e.printStackTrace();
  }

  private int mAnnotationTextColor = Color.BLACK;

  private int mAnnotationTextHighlightColor = Color.YELLOW;
  private int mAssistantBorderColor = Color.MAGENTA;

  private int mAssistantBorderHightlightColor = Color.GREEN;
  private int mAssistantBorderSize = 3;
  private int mAssistantFilledColor = Color.GREEN;
  private int mAssistantFilledHighlightColor = Color.YELLOW;
  private int mAssistantTextColor = Color.BLACK;
  HashMap<String, Integer> mBorderColors = new HashMap<String, Integer>();

  ConfigureFile mConfigure = new ConfigureFile(new File(Environment.getExternalStorageDirectory(), "/Palmap/configure.json"));
  // private Drawable mEscalatorElevatorIcon;
  // private Drawable mEscalatorEscalatorIcon;
  // private Drawable mEscalatorStairIcon;
  private int mEscalatorTextColor = Color.BLACK;
  private int mEscalatorTextHighlightColor = Color.YELLOW;
  HashMap<String, Integer> mFilledColors = new HashMap<String, Integer>();
  ArrayAdapter<Floor> mFloorsAdapter;
  private int mFrameBorderColor = Color.BLUE;

  private int mFrameBorderSize = 5;
  private int mFrameFilledColor = Color.LTGRAY;

  Handler mHandler = new Handler();
  boolean mHasMoved = false;
  boolean mIsMove = false;
  boolean mIsScale = false;
  float mLastScale;
  float mLastX;

  float mLastY;
  private Map mMall;
  private HashMap<String, Map> mMalls = new HashMap<String, Map>();
  private int mMapBackgroundColor = Color.WHITE;
  int mMiniumSize = 16;

  OnMapEventListener mOnMapEventListener;

  OnMapFloorChangedListener mOnMapFloorChangedListener;
  int mOrientation = getResources().getConfiguration().orientation;
  private Drawable mPublicServiceAtmIcon;

  HashMap<String, String> mPublicServiceIcons = new HashMap<String, String>();

  private int mPublicServiceTextColor = Color.BLACK;

  private int mPublicServiceTextHighlightColor = Color.YELLOW;

  private Drawable mPublicServiceToiletIcon;

  RelativeLayout mRelativeLayout = new RelativeLayout(getContext());

  private int mShopBorderColor = Color.MAGENTA;

  private int mShopBorderHightlightColor = Color.GREEN;
  private int mShopBorderSize = 3;

  private int mShopFilledColor = Color.YELLOW;

  private int mShopFilledHighlightColor = Color.YELLOW;

  // Button mButton = new Button(getContext());
  ShopPosition mShopPosition;

  private int mShopTextColor = Color.BLACK;

  Spinner mSpinner;

  HashMap<String, Integer> mTextColors = new HashMap<String, Integer>();

  Typeface mTypeface = Typeface.createFromAsset(getContext().getAssets(), "PalmapPublic.ttf");

  private PointMessage mPoint;

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

  // public String getFloorid() {
  // if (mMall == null) {
  // return null;
  // }
  // return mMall.getFloorid();
  // }

  // public String getFloorname() {
  // if (mMall == null) {
  // return null;
  // }
  // return mMall.getFloorname();
  // }

  // public String getMallid() {
  // if (mMall == null) {
  // return null;
  // }
  // return mMall.mId;
  // }
  //
  // public String getMallname() {
  // if (mMall == null) {
  // return null;
  // }
  // return mMall.mName;
  // }

  // public Poi getPoi(int x, int y) {
  // if (mMall != null && mMall.mFloor != null) {
  // return mMall.mFloor.getPoi(x, y);
  // }
  // return null;
  // }

  // public Poi[] getPoiList() {
  // if (mMall == null || mMall.mFloor == null) {
  // return null;
  // }
  // List<Poi> pois = new ArrayList<Poi>();
  // for (Mall.Floor.Shop shop : mMall.mFloor.mShops.values()) {
  // Poi poi = new Poi(shop.mId, shop.mName, shop.mType, shop.mHighlight);
  // pois.add(poi);
  // }
  // return (Poi[]) pois.toArray();
  // }

  public String getFloorid() {
    Floor curFloor = mMall.getCurFloor();
    if (curFloor == null) {
      return null;
    }
    return curFloor.getId();
  }

  public String getMallid() {
    if (mMall == null) {
      return null;
    }
    return mMall.getId();
  }

  public void hidePosition() {
    mShopPosition.setVisibility(INVISIBLE);
    mShopPosition.mShow = false;
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
        mPoint = mMall.getPointMessage(ex - getLeft(), ey - getTop());
        if (mPoint != null) {
          this.showPosition(ex - getLeft(), ey - getTop());
        }
      } else if ((Math.abs(ex - mLastX) + Math.abs(ey - mLastY) > 10)
          && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)) {
        this.hidePosition();
        mHasMoved = true;
        // mMap.reDraw(event.getAction() == MotionEvent.ACTION_UP);
        mMall.translate(ex - mLastX, ey - mLastY);
        mLastX = ex;
        mLastY = ey;
      } else if (mHasMoved && event.getAction() == MotionEvent.ACTION_UP) {
        mIsMove = false;
        mMall.reDraw();
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
        mMall.reDraw(event.getAction() == MotionEvent.ACTION_POINTER_UP);
        hidePosition();
        // mRedraw = false;
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float r = (float) Math.sqrt(x * x + y * y);
        mMall.scale(r / mLastScale);
        x = (event.getX(1) + event.getX(0)) / 2;// - mLocationX;
        y = (event.getY(1) + event.getY(0)) / 2;// - mLocationY;
        mMall.translate(x - ((mLastX - getLeft() - getWidth() / 2) * r / mLastScale + getLeft() + getWidth() / 2), y
            - ((mLastY - getTop() - getHeight() / 2) * r / mLastScale + getTop() + getHeight() / 2));
        mLastX = x;
        mLastY = y;
        mLastScale = r;
      } else if (mIsScale
          && (event.getAction() == MotionEvent.ACTION_POINTER_UP || (event.getAction() & 0xFF) == (MotionEvent.ACTION_POINTER_UP))) {
        mMall.reDraw();
      }
    }
    // return super.onTouchEvent(event);
    return true;
  }

  public void redraw() {
    invalidate();
  }

  public void scrollIntoView(int poid) {

  }

  public void scrollIntoView(int x, int y) {

  }

  public void setBorderColor(int color) {
    mFrameBorderColor = color;
  }

  public void setBorderColor(String type, int color) {
    mBorderColors.put(type, color);
  }

  public void setBorderSize(int size) {
    mFrameBorderSize = size;
  }

  public void setFilledColor(String type, int color) {
    mFilledColors.put(type, color);
  }

  public void setFloor(String id) {
    if (mMall == null || id == null) {
      return;
    }
    for (int i = 0; i < mFloorsAdapter.getCount(); i++) {
      String floorid = mFloorsAdapter.getItem(i).getId();
      if (floorid.equals(id)) {
        mSpinner.setSelection(i);
        break;
      }
    }
    logd("id=" + id);
    Floor floor = mMall.getFloors().get(id);
    if (floor == null) {
      return;
    }
    String from = getFloorid();
    if (floor.getData() == null) {
      setJson(mMall.getId(), id);
    }
    mMall.setCurFloor(id);
    if (!id.equals(from)) {
      if (mOnMapFloorChangedListener != null) {
        mOnMapFloorChangedListener.OnMapFloorChanged(from, id);
      }
    }
  }

  // int mLocationX = 0;
  // int mLocationY = 0;
  //
  // @Override
  // protected void onLayout(boolean changed, int l, int t, int r, int b)
  // {
  // super.onLayout(changed, l, t, r, b);
  // int[] xy = { 0, 0 };
  // getLocationInWindow(xy);
  // mLocationX = xy[0];
  // mLocationY = xy[1];
  // }

  public void setFloor(String id, String name, int index) {
    if (mMall == null || id == null) {
      return;
    }
    mMall.addFloor(id, name, index);
  }

  public int setMall(String id) {
    if (id == null) {
      return -1;
    }
    if (id.equals(mMall.getId()) && mMall != null) {
    } else {
      Map mall = mMalls.get(id);
      if (mall == null) {
        return -1;
      }
      mMall = mall;
      // setJson(id);
    }
    if (mMall.getData() == null) {
      setJson(id);
    }
    return 0;
  }

  public int setMall(String id, String name) {
    DrawMap.mMapName = name;
    DrawMap.delegate = this;
    if (id == null) {
      return -1;
    }
    if (id.equals(getMallid()) && mMall != null) {
      mMall.setName(name);
    } else {
      Map mall = mMalls.get(id);
      if (mall == null) {
        mall = new Map();
        mall.setId(id);
        mall.setName(name);
        mMalls.put(id, mall);
      } else {
        mall.setName(name);
      }
      mMall = mall;
      // setJson(id);
    }
    return 0;
  }

  // public void setNavigation(JSONArray json) {
  // if (mMall != null) {
  // mMall.setNavigation(json);
  // if (json != null) {
  // JSONObject obj = json.optJSONObject(0);
  // if (obj != null) {
  // String floorid = obj.optString("floor_id");
  // setFloor(floorid);
  // }
  // }
  // }
  // }

  public void setOffset(float x, float y) {
    addOffset(x, y);
  }

  public void setOnMapEventListener(OnMapEventListener onMapEventListener) {
    mOnMapEventListener = onMapEventListener;
  }

  public void setOnMapFloorChangedListener(OnMapFloorChangedListener onMapFloorChangedListener) {
    mOnMapFloorChangedListener = onMapFloorChangedListener;
  }

  // public void setPosition(String floorid, float x, float y) {
  // setFloor(floorid);
  // if (mMall != null) {
  // mMall.setPosition(x, -y);
  // mRedraw = true;
  // invalidate();
  // }
  // }

  public void setScale(float scale) {
    addScale(scale);
  }

  public void setTextColor(String type, int color) {
    mTextColors.put(type, color);
  }

  public void showPosition(float x, float y) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mShopPosition.getLayoutParams();
    params.leftMargin = (int) x - mShopPosition.getWidth() / 2;
    params.topMargin = (int) y - mShopPosition.getHeight();
    params.width = LayoutParams.WRAP_CONTENT;
    params.height = LayoutParams.WRAP_CONTENT;
    mShopPosition.setShop(mPoint);
    // mShopPosition.setText(mShop.mDisplay);
    mShopPosition.setLayoutParams(params);
    mShopPosition.mShow = true;
    mShopPosition.setVisibility(VISIBLE);
  }

  public void zoomin() {
    setScale(2f);
    mMall.reDraw();
    invalidate();
  }

  public void zoomout() {
    setScale(0.5f);
    mMall.reDraw();
    invalidate();
  }

  @Override
  protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    logd("mOrientation=" + mOrientation + ", newConfig.orientation=" + newConfig.orientation);
    if (mOrientation != newConfig.orientation) {
      mOrientation = newConfig.orientation;
      mMall.reDraw();
      addScale(1);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    logd("canvas=" + canvas);
    if (mMall != null) {
      mMall.onDraw(canvas);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // logd("widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec="
    // + heightMeasureSpec);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  void addOffset(float x, float y) {
    if (mMall != null) {
      mMall.translate(x, y);
      // invalidate();
    }
  }

  void addScale(float scale) {
    if (mMall != null) {
      mMall.scale(scale);
      // invalidate();
    }
  }

  PointMessage changeHighlight(float x, float y) {
    if (mMall != null) {
      return mMall.getPointMessage(x, y);
    }
    return null;
  }

  int downloadJson(String u, File file) {
    try {
      URL url = new URL(u);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Accept", "application/json");
      // urlConnection.setRequestMethod("GET");
      // urlConnection.setDoOutput(true);
      urlConnection.connect();
      InputStream inputStream = urlConnection.getInputStream();
      int totalSize = urlConnection.getContentLength();
      int downloadedSize = 0;
      byte[] buffer = new byte[1024];
      int bufferLength = 0;
      FileOutputStream fileOutput = new FileOutputStream(file);
      while ((bufferLength = inputStream.read(buffer)) > 0) {
        fileOutput.write(buffer, 0, bufferLength);
        downloadedSize += bufferLength;
      }
      fileOutput.close();
      inputStream.close();
    } catch (Throwable e) {
      logd(e);
    }
    return 0;
  }

  int setJson(JSONArray jsonArray) {
    mFloorsAdapter.clear();
    mMall.setData(new com.macrowen.macromap.draw.data.JSONArray(jsonArray));
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject json = jsonArray.optJSONObject(i);
      String id = json.optString("id");
      String name = json.optString("name");
      int index = json.optInt("index");
      setFloor(id, name, index);
    }
    mFloorsAdapter.addAll(mMall.getFloors().values());
    mFloorsAdapter.sort(new Comparator<Floor>() {
      @Override
      public int compare(Floor lhs, Floor rhs) {
        return lhs.getIndex() - rhs.getIndex();
      }
    });
    setFloor(mFloorsAdapter.getItem(0).getId());
    return 0;
  }

  int setJson(String mallid) {
    // setJson(mallid, "6");
    // String url = "http://10.0.0.10/mall/" + mallid + "/floors";
    String url = "http://apitest.palmap.cn/mall/" + mallid + "/floors";
    new Thread(new DownloadJson(mallid, url)).start();
    return 0;
  }

  int setJson(String mallid, File file) {
    try {
      // File file = new File(jsonfile);
      FileInputStream input = new FileInputStream(file);
      byte[] buf = new byte[input.available()];
      input.read(buf);
      input.close();
      String json = EncodingUtils.getString(buf, "UTF-8");
      JSONArray obj = new JSONArray(json);
      setJson(obj);
      logd("file.length()=" + file.length());
      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  int setJson(String mallid, String floorid) {
    String url = "http://apitest.palmap.cn/mall/" + mallid + "/floor/" + floorid;
    new Thread(new DownloadJson(mallid, floorid, url)).start();
    return 0;
  }

  int setJson(String mallid, String floorid, File file) {
    try {
      // File file = new File(jsonfile);
      FileInputStream input = new FileInputStream(file);
      byte[] buf = new byte[input.available()];
      input.read(buf);
      input.close();
      String json = EncodingUtils.getString(buf, "UTF-8");
      JSONObject obj = new JSONObject(json);
      // setMall(mallid);
      Map mall = mMalls.get(mallid);
      mall.getCurFloor().setData(new com.macrowen.macromap.draw.data.JSONObject(obj));
      if (mall == mMall) {
        mall.reDraw();
        addScale(1);
        // addOffset(0, 0);
        // invalidate();
        logd("file.length()=" + file.length());
      }
      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  private void init(AttributeSet attrs, int defStyle) {

    HashMap<String, String> publicServiceIcons = DrawMap.mPublicServiceIcons;
    for (int i = 0; i < 100; i++) {
      // mPublicServiceIcons.put(9100 + i, "" + (char) i);
      publicServiceIcons.put((9100 + i) + "", "" + (char) (i + 20));
      // logd("key=" + (9100 + i) + ", value=" + ((char) i) + "," + ("" +
      // (char) i));
    }
    publicServiceIcons.put("27108", "%"); // 洗手间
    publicServiceIcons.put("27125", "#"); // 扶梯
    publicServiceIcons.put("27124", "$"); // 楼梯
    publicServiceIcons.put("25135", "#"); // 扶梯
    publicServiceIcons.put("25136", "$"); // 楼梯
    publicServiceIcons.put("27126", "\""); // 电梯
    publicServiceIcons.put("27052", "'"); // 出入口
    publicServiceIcons.put("27114", "y"); // 自动售货机
    publicServiceIcons.put("27010", "("); // ATM
    publicServiceIcons.put("27066", "-");
    publicServiceIcons.put("27096", "{");
    publicServiceIcons.put("27055", "{"); // 问讯处

    DrawMap.mTypeface = mTypeface;

    mSpinner = new Spinner(getContext());
    mSpinner.setPrompt("Floors:");
    mFloorsAdapter = new ArrayAdapter<Floor>(getContext(), android.R.layout.simple_spinner_item, 0, new ArrayList<Floor>());
    // (Mall.Floor[]) mMall.mFloors.values().toArray()
    mSpinner.setAdapter(mFloorsAdapter);
    mRelativeLayout.addView(mSpinner);
    addView(mRelativeLayout);
    mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String floorid = mFloorsAdapter.getItem(position).getId();
        mMall.reDraw();
        mShopPosition.mShow = false;
        mShopPosition.setVisibility(INVISIBLE);
        setFloor(floorid);
        invalidate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    LinearLayout linear = new LinearLayout(getContext());
    linear.setOrientation(LinearLayout.VERTICAL);
    ImageView image = new ImageView(getContext());
    image.setImageResource(R.drawable.logo);
    image.setAlpha(0.3f);
    linear.addView(image);
    image = new ImageView(getContext());
    image.setImageResource(R.drawable.logo);
    image.setAlpha(0.3f);
    linear.addView(image);
    mRelativeLayout.addView(linear);
    mShopPosition = new ShopPosition(getContext(), attrs, defStyle);
    mShopPosition.setVisibility(INVISIBLE);
    android.widget.RelativeLayout.LayoutParams params =
        new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    mRelativeLayout.addView(mShopPosition, params);

    final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MacroMap, defStyle, 0);

    mMapBackgroundColor = a.getColor(R.styleable.MacroMap_mapBackgroundColor, mMapBackgroundColor);
    mFrameBorderColor = a.getColor(R.styleable.MacroMap_frameBorderColor, mFrameBorderColor);
    mFrameFilledColor = a.getColor(R.styleable.MacroMap_frameFilledColor, mFrameFilledColor);
    mFrameBorderSize = a.getInt(R.styleable.MacroMap_frameBorderSize, mFrameBorderSize);

    mAssistantBorderColor = a.getColor(R.styleable.MacroMap_assistantBorderColor, mAssistantBorderColor);
    mAssistantBorderHightlightColor = a.getColor(R.styleable.MacroMap_assistantBorderHighlightColor, mAssistantBorderHightlightColor);
    mAssistantFilledColor = a.getColor(R.styleable.MacroMap_assistantFilledColor, mAssistantFilledColor);
    mAssistantFilledHighlightColor = a.getColor(R.styleable.MacroMap_assistantFilledHighlightColor, mAssistantFilledHighlightColor);
    mAssistantTextColor = a.getColor(R.styleable.MacroMap_assistantTextColor, mAssistantTextColor);
    mAssistantBorderSize = a.getInt(R.styleable.MacroMap_assistantBorderSize, mAssistantBorderSize);

    mShopBorderColor = a.getColor(R.styleable.MacroMap_shopBorderColor, mShopBorderColor);
    mShopBorderHightlightColor = a.getColor(R.styleable.MacroMap_shopBorderHighlightColor, mShopBorderHightlightColor);
    mShopFilledColor = a.getColor(R.styleable.MacroMap_shopFilledColor, mShopFilledColor);
    mShopFilledHighlightColor = a.getColor(R.styleable.MacroMap_shopFilledHighlightColor, mShopFilledHighlightColor);
    mShopTextColor = a.getColor(R.styleable.MacroMap_shopTextColor, mShopTextColor);
    mShopBorderSize = a.getInt(R.styleable.MacroMap_shopBorderSize, mShopBorderSize);

    mPublicServiceTextColor = a.getColor(R.styleable.MacroMap_publicServiceTextColor, mPublicServiceTextColor);
    mPublicServiceTextHighlightColor = a.getColor(R.styleable.MacroMap_publicServiceTextHighlightColor, mPublicServiceTextHighlightColor);
    mPublicServiceAtmIcon = a.getDrawable(R.styleable.MacroMap_publicServiceAtmIcon);
    mPublicServiceToiletIcon = a.getDrawable(R.styleable.MacroMap_publicServiceToiletIcon);

    mEscalatorTextColor = a.getColor(R.styleable.MacroMap_escalatorTextColor, mEscalatorTextColor);
    mEscalatorTextHighlightColor = a.getColor(R.styleable.MacroMap_escalatorTextHighlightColor, mEscalatorTextHighlightColor);
    // mEscalatorEscalatorIcon = a
    // .getDrawable(R.styleable.MacroMap_escalatorEscalatorIcon);
    // mEscalatorElevatorIcon = a
    // .getDrawable(R.styleable.MacroMap_escalatorElevatorIcon);
    // mEscalatorStairIcon = a
    // .getDrawable(R.styleable.MacroMap_escalatorStairIcon);

    mAnnotationTextColor = a.getColor(R.styleable.MacroMap_annotationTextColor, mAnnotationTextColor);
    mAnnotationTextHighlightColor = a.getColor(R.styleable.MacroMap_annotationTextHighlightColor, mAnnotationTextHighlightColor);
    a.recycle();
  }
}
