package com.macro.palmap;

import org.json.JSONObject;

import com.macrowen.macromap.TitlebarActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShopDetailActivity extends TitlebarActivity
{
	String		mShopid;
	String		mUrl;
	JSONObject	mJson;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		setTitle("店铺详情");
		mShopid = getIntent().getStringExtra("shopid");
		mUrl = "http://apitest.palmap.cn/shop/" + mShopid;
		downloadJson(mUrl, new Runnable()
		{
			@Override
			public void run()
			{
				setData(getJson(mUrl));
			}
		});
		ImageButton button = (ImageButton) findViewById(R.id.button_location);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String floorid = mJson.optString("floor_id");
				if (floorid == null || floorid.isEmpty())
				{
					floorid = mJson.optString("flooor_id");
				}
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("shopid", "" + mShopid);
				bundle.putString("json", "" + mJson);
				bundle.putString("floorid", "" + floorid);
				intent.putExtra("bundle", bundle);
				startActivity(intent);
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
		TextView text = (TextView) findViewById(R.id.name);
		text.setText(mJson.optString("show_name"));
		text = (TextView) findViewById(R.id.addr);
		text.setText(mJson.optString("address"));
		text = (TextView) findViewById(R.id.tel);
		text.setText(mJson.optString("tel"));
		text = (TextView) findViewById(R.id.web);
		text.setText(mJson.optString("website"));
		text = (TextView) findViewById(R.id.weibo);
		text.setText(mJson.optString("microblog"));
		text = (TextView) findViewById(R.id.desc);
		text.setText(mJson.optString("description"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_detail, menu);
		return true;
	}

}
