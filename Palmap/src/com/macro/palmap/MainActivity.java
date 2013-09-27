package com.macro.palmap;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.macrowen.macromap.MacroMap;
import com.macrowen.macromap.TitlebarActivity;
import com.macrowen.macromap.MacroMap.OnMapEventListener;
import com.macrowen.macromap.MacroMap.OnMapEventType;
import com.macrowen.macromap.MacroMap.OnMapFloorChangedListener;

import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends TitlebarActivity
{
	MacroMap	mMacroMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle("商场地图");
		enableSearch(true);
		setOnClickSearchButtonListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getBaseContext(), ShopListActivity.class);
				startActivity(intent);
			}
		});
		setContentView(R.layout.activity_main);
		// String fname = "/sdcard/MacroMap/x2.json";
		// JSONObject json = new JSONObject(fname);
		mMacroMap = (MacroMap) findViewById(R.id.macroMap1);
		mMacroMap.setMall("3", "商场");
		mMacroMap.setMall("3");
		// map.
		// map.setFloor("2", "Floor 2");
		// map.setJson("1", "2", new
		// File(Environment.getExternalStorageDirectory() +
		// "/MacroMap/x2.json"));
		// map.setJsonUrl("1", "1", ACCESSIBILITY_SERVICE)
		// map.setJson("1");
		mMacroMap.setOnMapEventListener(new OnMapEventListener()
		{
			@Override
			public void OnMapEvent(int id, OnMapEventType type)
			{
				logd("id=" + id + ", type=" + type);
				if (type == OnMapEventType.MapClickedLeft)
				{
					Intent intent = new Intent(getBaseContext(), ShopDetailActivity.class);
					intent.putExtra("shopid", "" + id);
					startActivity(intent);
				}
				else if (type == OnMapEventType.MapClickedRight)
				{
					Intent intent = new Intent(getBaseContext(), ShopDetailActivity.class);
					intent.putExtra("shopid", "" + id);
					startActivity(intent);
				}
			}
		});
		mMacroMap.setOnMapFloorChangedListener(new OnMapFloorChangedListener()
		{
			@Override
			public void OnMapFloorChanged(String fromFloorid, String toFloorid)
			{
				logd("fromFloorid=" + fromFloorid + ", toFloorid=" + toFloorid);
			}
		});
		ImageButton button = (ImageButton) findViewById(R.id.button_position);
		button.setOnClickListener(new OnClickListener()
		{
			String	mUrl;

			@Override
			public void onClick(View v)
			{
				try
				{
					File file = Environment.getExternalStorageDirectory();
					file = new File(file, "/Palmap/location_server.server");
					FileInputStream input = new FileInputStream(file);
					byte[] buf = new byte[input.available()];
					input.read(buf);
					input.close();
					String host = EncodingUtils.getString(buf, "UTF-8");
					WifiManager wifi = (WifiManager) getSystemService(getBaseContext().WIFI_SERVICE);
					String mac = wifi.getConnectionInfo().getMacAddress();
					mUrl = "http://" + host + "/pos?mac=" + mac;
					downloadJson(mUrl, new Runnable()
					{
						@Override
						public void run()
						{
							String floorid = "18";
							float x = 15202;
							float y = 7447;
							JSONObject json = getJson(mUrl);
							if (json != null)
							{
								floorid = json.optString("floor_id");
								x = (float) json.optDouble("x");
								y = (float) json.optDouble("y");
							}
//							mMacroMap.setPosition(floorid, x, y);
						}
					});
				}
				catch (Throwable e)
				{
					logd(e);
				}
			}
		});
		button = (ImageButton) findViewById(R.id.button_zoomin);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mMacroMap.zoomin();
			}
		});
		button = (ImageButton) findViewById(R.id.button_zoomout);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mMacroMap.zoomout();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Bundle bundle = intent.getBundleExtra("bundle");
		if (bundle != null)
		{
			// JSONArray navigation = (JSONArray) bundle.get("navigation");
			String navigation = bundle.getString("navigation");
			if (navigation != null && !navigation.isEmpty())
			{
				try
				{
					JSONArray json = new JSONArray(navigation);
//					mMacroMap.setNavigation(json);
				}
				catch (Throwable e)
				{
					logd(e);
				}
			}
			else
			{
				String floorid = bundle.getString("floorid");
				String Shopid = bundle.getString("shopid");
				mMacroMap.setFloor(floorid);
			}
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
	public boolean onTouchEvent(MotionEvent event)
	{
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
}
