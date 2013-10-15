package com.macrowen.macromap;

import com.macrowen.macromap.draw.DownLoadJson;
import com.macrowen.macromap.draw.Floor;
import com.macrowen.macromap.draw.Map;
import com.macrowen.macromap.draw.PointMessage;
import com.macrowen.macromap.draw.ShopPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.os.Message;

import android.graphics.drawable.Drawable;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

@SuppressLint("HandlerLeak")
public class CopyOfMacroMap extends ScrollView {

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

  public static int what;

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

  public Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      int what = msg.what;
      File file = (File) msg.obj;
      if (file == null) {
        return;
      }
      if (what == 0) {
        loadMapJson(file);
      } else {
        loadFloorJson(file);
      }
    };
  };
  boolean mHasMoved = false;
  boolean mIsMove = false;
  boolean mIsScale = false;
  float mLastScale;
  float mLastX;

  float mLastY;
  private Map mMall;
  // private HashMap<String, Map> mMalls = new HashMap<String, Map>();
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

  private DownLoadJson downLoad;

  public CopyOfMacroMap(Context context) {
    super(context);
    init(null, 0);
  }

  public CopyOfMacroMap(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public CopyOfMacroMap(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  public void hidePosition() {
    mShopPosition.setVisibility(INVISIBLE);
    mShopPosition.mShow = false;
  }

  public void loadMap(final String mapId, String mapName) {
    if (mMall != null) {
      return;
    }
    mMall = new Map();
    mMall.setId(mapId);
    mMall.setName(mapName);
    mMall.setPublicService(mPublicServiceIcons);
    mMall.setMapName(mapName);
    mMall.setDelegate(this);
    mMall.setTypaface(mTypeface);
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

  public void setFloor(String floorId) {
    mMall.setCurFloor(floorId);
    Floor floor = mMall.getCurFloor();
    if (floor.getData() != null) {
      return;
    }
    loadFloorFile(floor.getId());
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
    // logd("canvas=" + canvas);
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

  private void addFloor() {
    JSONArray jsonArray = mMall.getData().getData();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject json = jsonArray.optJSONObject(i);
      String id = json.optString("id");
      String name = json.optString("name");
      int index = json.optInt("index");
      Floor floor = new Floor(id, name, index);
      mMall.addFloor(floor);
    }
    mFloorsAdapter.clear();
    mFloorsAdapter.addAll(mMall.getFloors().values());
    mFloorsAdapter.sort(new Comparator<Floor>() {
      @Override
      public int compare(Floor lhs, Floor rhs) {
        return lhs.getIndex() - rhs.getIndex();
      }
    });
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
    mPublicServiceIcons = new HashMap<String, String>();
    for (int i = 0; i < 100; i++) {
      // mPublicServiceIcons.put(9100 + i, "" + (char) i);
      mPublicServiceIcons.put((9100 + i) + "", "" + (char) (i + 20));
      // logd("key=" + (9100 + i) + ", value=" + ((char) i) + "," + ("" +
      // (char) i));
    }
    mPublicServiceIcons.put("27108", "%"); // 洗手间
    mPublicServiceIcons.put("27125", "#"); // 扶梯
    mPublicServiceIcons.put("27124", "$"); // 楼梯
    mPublicServiceIcons.put("25135", "#"); // 扶梯
    mPublicServiceIcons.put("25136", "$"); // 楼梯
    mPublicServiceIcons.put("27126", "\""); // 电梯
    mPublicServiceIcons.put("27052", "'"); // 出入口
    mPublicServiceIcons.put("27114", "y"); // 自动售货机
    mPublicServiceIcons.put("27010", "("); // ATM
    mPublicServiceIcons.put("27066", "-");
    mPublicServiceIcons.put("27096", "{");
    mPublicServiceIcons.put("27055", "{"); // 问讯处

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
        final String floorid = mFloorsAdapter.getItem(position).getId();
        mMall.reDraw();
        mShopPosition.mShow = false;
        mShopPosition.setVisibility(INVISIBLE);
        setFloor(floorid);
        // invalidate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    // LinearLayout linear = new LinearLayout(getContext());
    // linear.setOrientation(LinearLayout.VERTICAL);
    // ImageView image = new ImageView(getContext());
    // image.setImageResource(R.drawable.logo);
    // image.setAlpha(0.3f);
    // linear.addView(image);
    // image = new ImageView(getContext());
    // image.setImageResource(R.drawable.logo);
    // image.setAlpha(0.3f);
    // linear.addView(image);
    // mRelativeLayout.addView(linear);
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

  private File laodFileByURL(String url) {
    try {
      File file = Environment.getExternalStorageDirectory();
      file = new File(file, "/Palmap/MacroMap/" + Base64.encodeToString(url.getBytes(), Base64.NO_WRAP));
      if (file.length() < 4) {
        file.getParentFile().mkdirs();
        file.createNewFile();
        new DownLoadJson(mHandler).execute(url);
        return null;
      }
      return file;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void loadFloorFile(String floorId) {
    final File file = laodFileByURL(DownLoadJson.getURLbyFloor(mMall.getId(), floorId));
    if (file == null) {
      return;
    }
    // mHandler.post(new Runnable() {
    //
    // @Override
    // public void run() {
    loadFloorJson(file);
    // }
    // });
  }

  private void loadFloorJson(File file) {
    try {
      String json = EncodingUtils.getString(getByte(file), "UTF-8");
      JSONObject obj = new JSONObject(json);
      mMall.getCurFloor().setData(new com.macrowen.macromap.draw.data.JSONObject(obj));
      // setScale(1);
      // mMall.reDraw();
      // dataReady = true;
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void loadMapFile(String mapId) {
    final File file = laodFileByURL(DownLoadJson.getURLbyMall(mapId));
    if (file == null) {
      return;
    }
    // mHandler.post(new Runnable() {
    // @Override
    // public void run() {
    loadMapJson(file);
    // }
    // });
  }

  private void loadMapJson(File file) {
    try {
      String json = EncodingUtils.getString(getByte(file), "UTF-8");
      JSONArray obj = new JSONArray(json);
      mMall.setData(new com.macrowen.macromap.draw.data.JSONArray(obj));
      addFloor();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
