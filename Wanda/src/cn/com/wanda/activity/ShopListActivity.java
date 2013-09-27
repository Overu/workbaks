package cn.com.wanda.activity;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.macrowen.macromap.TitlebarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

///**店铺列表页面 ShopListActivity*/
//public class ShopListActivity extends Activity implements OnClickListener{
//	/**跳转*/
//	private Intent activity_shoplist_intent;
//	/**店铺列表页面 ShopListActivity 顶部title的返回键*/
//	private ImageButton activity_shoplist_layout_title_layout_btn_back;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_shoplist);
//		this.initView();
//	}
//
//	/**初始化控件*/
//	private void initView(){
//		this.activity_shoplist_intent = new Intent();
//		
//		this.activity_shoplist_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_shoplist_layout_title_layout_btn_back);
//		this.activity_shoplist_layout_title_layout_btn_back.setOnClickListener(ShopListActivity.this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		if(R.id.activity_shoplist_layout_title_layout_btn_back == v.getId()){
//			this.activity_shoplist_intent.setClass(ShopListActivity.this, HomeActivity.class);
//			startActivity(this.activity_shoplist_intent);
//			ShopListActivity.this.finish();
//		}
//	}
//}

public class ShopListActivity extends TitlebarActivity
{
	String		mMallid	= "3";
	String		mUrl;
	JSONObject	mJson;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_list);
		setTitle("商铺列表");
		mUrl = "http://apitest.palmap.cn/mall/" + mMallid + "/shops";
		// downloadJson(mUrl, new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// setData(getJson(mUrl));
		// }
		// });
		setData(getJson(mUrl));
		ListView list = (ListView) findViewById(R.id.shop_list);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String shopid = mJson.optJSONArray("shops").optJSONObject(position).optString("id");
				Intent intent = new Intent(getBaseContext(), ShopDetailActivity.class);
				intent.putExtra("shopid", "" + shopid);
				startActivity(intent);
			}
		});
		Button button = (Button) findViewById(R.id.navigate);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mUrl = "http://apitest.palmap.cn/navi?fx=1&fy=1&ff=2&tx=20000000&ty=2000000000&tf=2";
				downloadJson(mUrl, new Runnable()
				{
					@Override
					public void run()
					{
						JSONObject json = getJson(mUrl);
						if (json != null)
						{
							Intent intent = new Intent(getBaseContext(), HomeActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("navigation", json.toString());
							intent.putExtra("bundle", bundle);
							startActivity(intent);
						}
						else
						{
							Intent intent = new Intent(getBaseContext(), HomeActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("navigation", "[{\"x\":9816.588899999857,\"y\":-3808.8916999995708,\"floor_id\":2},"
									+ "{\"x\":19825.16139999777,\"y\":-3808.8916999995708,\"floor_id\":2},"
									+ "{\"x\":19831.084899999201,\"y\":-3808.8916999995708,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-800.5494000017643,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-792.0298000015318,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-786.0989999994636,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-778.1074000000954,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-772.6702000014484,\"floor_id\":2},"
//									+ "{\"x\":9831.084899999201,\"y\":-766.9827999994159,\"floor_id\":2},"
									+ "{\"x\":19831.084899999201,\"y\":-5764.6319999992847,\"floor_id\":2},"
									+ "{\"x\":19829.195799998939,\"y\":-5762.2494999989867,\"floor_id\":2},"
									+ "{\"x\":9829.150004870644,\"y\":-5762.1917700896119,\"floor_id\":2},"
									+ "{\"x\":9828.123599998653,\"y\":-8760.8973000012338,\"floor_id\":2},"
									+ "{\"x\":9823.4496830274,\"y\":-8755.0026134552776,\"floor_id\":2},"
									+ "{\"x\":9823.220485848096,\"y\":-8754.713552794268,\"floor_id\":2},"
									+ "{\"x\":9818.11930000037,\"y\":-8748.2800000011921,\"floor_id\":2}]");
							intent.putExtra("bundle", bundle);
							startActivity(intent);
						}
					}
				});
			}
		});
	}

	void setData(JSONObject json)
	{
		if (json == null)
		{
			return;
		}
		mJson = json;

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		JSONArray shops = json.optJSONArray("shops");
		for (int i = 0; i < shops.length(); i++)
		{
			JSONObject shop = shops.optJSONObject(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("logo", R.drawable.foli);
			String name = shop.optString("name");
			if (name == null || name.isEmpty())
			{
				name = shop.optString("english_name");
			}
			map.put("name", name);
			map.put("info", shop.optString("floor"));
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.shop_list_item, new String[] { "logo", "name", "info" },
				new int[] { R.id.logo, R.id.title, R.id.info });

		ListView list = (ListView) findViewById(R.id.shop_list);
		list.setAdapter(listItemAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_list, menu);
		return true;
	}

}

