package cn.com.wanda.activity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.macrowen.macromap.MacroMap;
import com.macrowen.macromap.TitlebarActivity;
import com.macrowen.macromap.MacroMap.OnMapEventListener;
import com.macrowen.macromap.MacroMap.OnMapEventType;
import com.macrowen.macromap.MacroMap.OnMapFloorChangedListener;
import com.macrowen.macromap.draw.Map;
import com.macrowen.macromap.utils.MapService;
import com.macrowen.macromap.utils.MapService.MapLoadStatus;
import com.macrowen.macromap.utils.MapService.MapLoadStatusListener;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

// /**地图页面 MapActivity*/
// public class MapActivity extends Activity implements OnClickListener{
// /**跳转*/
// private Intent activity_map_intent;
// /**地图页面 MapActivity 顶部title的返回键*/
// private ImageButton activity_map_layout_title_layout_btn_back;
//
// @Override
// protected void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// setContentView(R.layout.activity_map);
// this.initView();
// }
//
// /**初始化控件*/
// private void initView(){
// this.activity_map_intent = new Intent();
//
// this.activity_map_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_map_layout_title_layout_btn_back);
// this.activity_map_layout_title_layout_btn_back.setOnClickListener(MapActivity.this);
// }
//
// @Override
// public void onClick(View v) {
// if(R.id.activity_map_layout_title_layout_btn_back == v.getId()){
// this.activity_map_intent.setClass(MapActivity.this, HomeActivity.class);
// startActivity(this.activity_map_intent);
// MapActivity.this.finish();
// }
// }
//
// }
public class MapActivity extends TitlebarActivity implements
    MapLoadStatusListener {
  MacroMap mMacroMap;
  Timer mPositionTimer;

  MapService mapservice = MapService.getInstance();

  @Override
  public void onMapLoadStatusEvent(MapLoadStatus mapLoadStatus, Map map) {
    switch (mapLoadStatus) {
    case MapDataInit:
      mapservice.setViewDelegate(mMacroMap);
      mMacroMap.setMap(mapservice.getMap());
      break;
    case MapDataLoaded:
      mapservice.setViewDelegate(mMacroMap);
      mMacroMap.setMap(mapservice.getMap());
      break;

    default:
      break;
    }

  }

  // @Override
  // protected void onStart()
  // {
  // super.onStart();
  // }

  // @Override
  // public boolean onCreateOptionsMenu(Menu menu) {
  // // Inflate the menu; this adds items to the action bar if it is present.
  // getMenuInflater().inflate(R.menu.main, menu);
  // return true;
  // }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // MacroMap.logd("event=" + event);
    // MacroMap.logd("event.=" + event.getAction());
    // MacroMap mMacroMap = (MacroMap) findViewById(R.id.macroMap1);
    // MacroMap.logd("map.getTop()=" + map.getTop());
    // MacroMap.logd("event.getY()=" + event.getY() + ", map.getY()=" +
    // map.getY());
    // int[] xy = { (int) event.getX(), (int) event.getY() };
    // MacroMap.logd("x=" + xy[0] + ", y=" + xy[1]);
    // mMacroMap.getLocationInWindow(xy);
    // MacroMap.logd("x=" + xy[0] + ", y=" + xy[1]);
    // MacroMap.logd("x=" + (event.getX()- xy[0]) + ", y=" +(event.getY()-
    // xy[1]));
    // event.setLocation(event.getX() - xy[0], event.getY() - xy[1]);
    mMacroMap.dispatchTouchEvent(event);
    return super.onTouchEvent(event);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("商场地图");
    enableSearch(true);
    setOnClickSearchButtonListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), ShopListActivity.class);
        startActivity(intent);
      }
    });
    setContentView(R.layout.activity_map);
    // String fname = "/sdcard/MacroMap/x2.json";
    // JSONObject json = new JSONObject(fname);
    mMacroMap = (MacroMap) findViewById(R.id.macroMap1);
    mapservice.setOnMapLoadStatusListener(this);
    mapservice.initMapData("3", "商场");
    // mMacroMap.setMall("3", "商场");
    // mMacroMap.setMall("3");
    // map.
    // map.setFloor("2", "Floor 2");
    // map.setJson("1", "2", new
    // File(Environment.getExternalStorageDirectory() +
    // "/MacroMap/x2.json"));
    // map.setJsonUrl("1", "1", ACCESSIBILITY_SERVICE)
    // map.setJson("1");
    mMacroMap.setOnMapEventListener(new OnMapEventListener() {
      @Override
      public void OnMapEvent(int id, OnMapEventType type) {
        logd("id=" + id + ", type=" + type);
        if (type == OnMapEventType.MapClickedLeft) {
          Intent intent = new Intent(getBaseContext(), ShopDetailActivity.class);
          intent.putExtra("shopid", "" + id);
          startActivity(intent);
        } else if (type == OnMapEventType.MapClickedRight) {
          Intent intent = new Intent(getBaseContext(), ShopDetailActivity.class);
          intent.putExtra("shopid", "" + id);
          startActivity(intent);
        }
      }
    });
    mMacroMap.setOnMapFloorChangedListener(new OnMapFloorChangedListener() {
      @Override
      public void OnMapFloorChanged(String fromFloorid, String toFloorid) {
        logd("fromFloorid=" + fromFloorid + ", toFloorid=" + toFloorid);
      }
    });
    ImageButton button = (ImageButton) findViewById(R.id.button_position);
    button.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (mapservice.getMap() == null) {
          return;
        }
        String floorid = "18";
        float x = 15202;
        float y = 7447;
        mMacroMap.setFloor(floorid);
        mapservice.setPosition(floorid, x, y);
        // if (mPositionTimer == null) {
        // try {
        // /*
        // * File file = Environment.getExternalStorageDirectory(); file = new
        // File(file, "/Palmap/location_server"); FileInputStream
        // * input = new FileInputStream(file); byte[] buf = new
        // byte[input.available()]; input.read(buf); input.close(); String host
        // =
        // * EncodingUtils.getString(buf, "UTF-8");
        // */
        // WifiManager wifi = (WifiManager)
        // getSystemService(getBaseContext().WIFI_SERVICE);
        // final String mac = wifi.getConnectionInfo().getMacAddress();
        // mPositionTimer = new Timer();
        // mPositionTimer.schedule(new TimerTask() {
        // private Random rand = new Random();
        //
        // @Override
        // public void run() {
        // final String mUrl = "http://10.1.16.93:8080/wanda2/pos?mac=" + mac +
        // "&rand=" + rand.nextLong();
        //
        // downloadJson(mUrl, new Runnable() {
        // @Override
        // public void run() {
        // String floorid = "18";
        // float x = 15202;
        // float y = 7447;
        // JSONObject json = getJson(mUrl);
        // logd(json.toString());
        // if (json != null) {
        // floorid = json.optString("floor_id");
        // x = (float) json.optDouble("x");
        // y = (float) json.optDouble("y");
        // }
        // // mMacroMap.setPosition(floorid, x, y);
        // }
        // });
        // }
        // }, 0, 1000 * 2);
        // } catch (Throwable e) {
        // logd(e);
        // }
        // } else {
        // mPositionTimer.cancel();
        // mPositionTimer = null;
        // }
      }
    });
    button = (ImageButton) findViewById(R.id.button_zoomin);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mapservice.zoomin();
      }
    });
    button = (ImageButton) findViewById(R.id.button_zoomout);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mapservice.zoomout();
      }
    });
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapservice.flrushView();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Bundle bundle = intent.getBundleExtra("bundle");
    if (bundle != null) {
      // JSONArray navigation = (JSONArray) bundle.get("navigation");
      String navigation = bundle.getString("navigation");
      if (navigation != null && !navigation.isEmpty()) {
        try {
          JSONArray json = new JSONArray(navigation);
          // mMacroMap.setNavigation(json);
        } catch (Throwable e) {
          logd(e);
        }
      } else {
        String floorid = bundle.getString("floorid");
        String Shopid = bundle.getString("shopid");
        mapservice.setFloor(floorid);
      }
    }
  }
}