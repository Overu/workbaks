package com.macrowen.macromap.utils;

import com.macrowen.macromap.draw.Floor;
import com.macrowen.macromap.draw.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.view.View;

import android.os.Handler;
import android.os.Environment;
import android.util.Base64;

public class MapService {

  public enum MapLoadStatus {
    MapDataLoaded, MapDataInit
  }

  public interface MapLoadStatusListener {
    public void onMapLoadStatusEvent(MapLoadStatus mapLoadStatus, Map map);
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
        mFile = new File(mFile, "/Palmap/MacroMap/"
            + Base64.encodeToString(mUrl.getBytes(), Base64.NO_WRAP));
        if (mFile.length() < 4) {
          mFile.getParentFile().mkdirs();
          mFile.createNewFile();
          downloadJson(mUrl, mFile);
        }
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            if (mFloorid != null) {
              setFloorData(mMallid, mFloorid, mFile);
            } else {
              setMapJson(mMallid, mFile);
            }
          }
        });
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public static MapService INSTANCE = null;

  public static MapService getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MapService();
    }
    return INSTANCE;
  }

  private Map mMap;
  private Handler mHandler = new Handler();
  private MapLoadStatusListener mMapLoadStatusListener;

  private boolean isInit = true;

  private MapService() {
  }

  public void addOffset(float x, float y) {
    if (mMap != null) {
      mMap.translate(x, y);
      // invalidate();
    }
  }

  public void addScale(float scale) {
    if (mMap != null) {
      mMap.scale(scale);
      // invalidate();
    }
  }

  public void destory() {
    mMap = null;
    System.gc();
  }
  
  public void flrushView() {
    mMap.delegate = null;
  }

  public Floor getCurFloor() {
    if (mMap == null) {
      return null;
    }
    return mMap.getCurFloor();
  }

  public Map getMap() {
    return mMap;
  }

  public void initMapData(String mapId, String mapName) {
    if (mMap == null) {
      Map map = new Map();
      map.setId(mapId);
      map.setName(mapName);
      map.setMapName(mapName);
      mMap = map;
      loadMapData(mapId);
    }
    if (mMap.getCurFloor() != null) {
      mMapLoadStatusListener.onMapLoadStatusEvent(MapLoadStatus.MapDataLoaded,
          mMap);
    }
  }

  public void reDraw() {
    mMap.reDraw();
  }

  public int setFloor(String id) {
    if (mMap == null || id == null) {
      return -1;
    }
    return mMap.setFloor(id) == -2 ? loadFloorData(mMap.getId(), id) : this
        .completeData();
  }

  public void setOnMapLoadStatusListener(
      MapLoadStatusListener mMapLoadStatusListener) {
    this.mMapLoadStatusListener = mMapLoadStatusListener;
  }

  public void setPosition(String floorid, float x, float y) {
    setFloor(floorid);
    if (mMap != null) {
      mMap.position(x, -y);
      mMap.reDraw();
      mMap.delegateRefush();
    }
  }

  public void setViewDelegate(View view) {
    if (mMap == null) {
      return;
    }
    mMap.setDelegate(view);
  }

  public void zoomin() {
    addScale(2f);
    mMap.reDraw();
    mMap.delegateRefush();
  }

  public void zoomout() {
    addScale(0.5f);
    mMap.reDraw();
    mMap.delegateRefush();
  }

  protected int downloadJson(String u, File file) {
    try {
      URL url = new URL(u);
      HttpURLConnection urlConnection = (HttpURLConnection) url
          .openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("Accept", "application/json");
      urlConnection.connect();
      InputStream inputStream = urlConnection.getInputStream();
      // int totalSize = urlConnection.getContentLength();
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
      e.printStackTrace();
    }
    return 0;
  }

  protected int loadFloorData(String mallid, String floorid) {
    String url = "http://apitest.palmap.cn/mall/" + mallid + "/floor/"
        + floorid;
    new Thread(new DownloadJson(mallid, floorid, url)).start();
    return 0;
  }

  protected int loadMapData(String mallid) {
    String url = "http://apitest.palmap.cn/mall/" + mallid + "/floors";
    new Thread(new DownloadJson(mallid, url)).start();
    return 0;
  }

  protected int setFloor(JSONArray jsonArray) {
    String floorid = "";
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject json = jsonArray.optJSONObject(i);
      String id = json.optString("id");
      String name = json.optString("name");
      int index = json.optInt("index");
      if (index == 0) {
        floorid = id;
      }
      mMap.setFloor(id, name, index);
    }
    setFloor(floorid);
    return 0;
  }

  protected int setFloorData(String mallid, String floorid, File file) {
    try {
      FileInputStream input = new FileInputStream(file);
      byte[] buf = new byte[input.available()];
      input.read(buf);
      input.close();
      String json = EncodingUtils.getString(buf, "UTF-8");
      JSONObject obj = new JSONObject(json);
      getCurFloor()
          .setData(new com.macrowen.macromap.draw.data.JSONObject(obj));
      this.completeData();
      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  protected int setMapJson(String mallid, File file) {
    try {
      FileInputStream input = new FileInputStream(file);
      byte[] buf = new byte[input.available()];
      input.read(buf);
      input.close();
      String json = EncodingUtils.getString(buf, "UTF-8");
      JSONArray obj = new JSONArray(json);
      mMap.setData(new com.macrowen.macromap.draw.data.JSONArray(obj));
      setFloor(obj);
      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  private int completeData() {
    if (mMapLoadStatusListener != null && isInit) {
      isInit = false;
      mMapLoadStatusListener.onMapLoadStatusEvent(MapLoadStatus.MapDataInit,
          mMap);
    }
    if (mMap.delegate != null) {
      mHandler.post(new Runnable() {
        @Override
        public void run() {
          mMap.reDraw();
          addScale(1);
        }
      });
    }
    return 1;
  }
}
