package cn.com.wanda.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**主页面 HomeActivity*/
public class HomeActivity extends Activity implements OnClickListener{
	/**跳转*/
	private Intent activity_home_intent;
	/**主页面 HomeActivity 顶部title的关于按钮*/
	private ImageButton acitivity_home_title_layout_btn_about;
	/**主页面 HomeActivity 中右边三个按钮之 地图*/
	private Button activity_home_rightbtns_layout_btn_map;
	/**主页面 HomeActivity 中右边三个按钮之 商铺列表*/
	private Button activity_home_rightbtns_layout_btn_shoplist;
	/**主页面 HomeActivity 中右边三个按钮之 停车*/
	private Button activity_home_rightbtns_layout_btn_parking;
	/**主页面 HomeActivity 中下部 滚动 文字（优惠券列表）*/
	private TextView activity_home_bottom_roller_layout_textview;
	/**主页面 HomeActivity 中底部的 4个小图标*/
	private ImageButton activity_home_bottom_logo_layout_btn_logo_1;
	private ImageButton activity_home_bottom_logo_layout_btn_logo_2;
	private ImageButton activity_home_bottom_logo_layout_btn_logo_3;
	private ImageButton activity_home_bottom_logo_layout_btn_logo_4;
	/**主页面 HomeActivity 中底部的 一个箭头图标（品牌墙）*/
	private ImageButton activity_home_bottom_logo_layout_btn_logo_more;
	
	/**创建一个handler来发送请求*/
	private Handler handler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		this.initView();
	}

	/**初始化控件*/
	private void initView(){
		this.activity_home_intent = new Intent();
		
		this.acitivity_home_title_layout_btn_about = (ImageButton) findViewById(R.id.acitivity_home_title_layout_btn_about);
		this.acitivity_home_title_layout_btn_about.setOnClickListener(HomeActivity.this);
		
		this.activity_home_rightbtns_layout_btn_map = (Button) findViewById(R.id.activity_home_rightbtns_layout_btn_map);
		this.activity_home_rightbtns_layout_btn_map.setOnClickListener(HomeActivity.this);
		this.activity_home_rightbtns_layout_btn_shoplist = (Button) findViewById(R.id.activity_home_rightbtns_layout_btn_shoplist);
		this.activity_home_rightbtns_layout_btn_shoplist.setOnClickListener(HomeActivity.this);
		this.activity_home_rightbtns_layout_btn_parking = (Button) findViewById(R.id.activity_home_rightbtns_layout_btn_parking);
		this.activity_home_rightbtns_layout_btn_parking.setOnClickListener(HomeActivity.this);
		
		this.activity_home_bottom_roller_layout_textview = (TextView) findViewById(R.id.activity_home_bottom_roller_layout_textview);
		this.activity_home_bottom_roller_layout_textview.setOnClickListener(HomeActivity.this);
		
		this.activity_home_bottom_logo_layout_btn_logo_1 = (ImageButton) findViewById(R.id.activity_home_bottom_logo_layout_btn_logo_1);
		this.activity_home_bottom_logo_layout_btn_logo_1.setOnClickListener(HomeActivity.this);
		this.activity_home_bottom_logo_layout_btn_logo_2 = (ImageButton) findViewById(R.id.activity_home_bottom_logo_layout_btn_logo_2);
		this.activity_home_bottom_logo_layout_btn_logo_2.setOnClickListener(HomeActivity.this);
		this.activity_home_bottom_logo_layout_btn_logo_3 = (ImageButton) findViewById(R.id.activity_home_bottom_logo_layout_btn_logo_3);
		this.activity_home_bottom_logo_layout_btn_logo_3.setOnClickListener(HomeActivity.this);
		this.activity_home_bottom_logo_layout_btn_logo_4 = (ImageButton) findViewById(R.id.activity_home_bottom_logo_layout_btn_logo_4);
		this.activity_home_bottom_logo_layout_btn_logo_4.setOnClickListener(HomeActivity.this);
		
		this.activity_home_bottom_logo_layout_btn_logo_more = (ImageButton) findViewById(R.id.activity_home_bottom_logo_layout_btn_logo_more);
		this.activity_home_bottom_logo_layout_btn_logo_more.setOnClickListener(HomeActivity.this);
		
		
	}


	/**主页面 HomeActivity 监听按钮*/
	@Override
	public void onClick(View v) {
		/**主页面 HomeActivity 顶部title的关于按钮*/
		if(R.id.acitivity_home_title_layout_btn_about == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, AboutActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中右边三个按钮之 地图*/
		else if(R.id.activity_home_rightbtns_layout_btn_map == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, MapActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中右边三个按钮之 商铺列表*/
		else if(R.id.activity_home_rightbtns_layout_btn_shoplist == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ShopListActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中右边三个按钮之 停车*/
		else if(R.id.activity_home_rightbtns_layout_btn_parking == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ParkingActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中下部 滚动 文字（优惠券列表）*/
		else if(R.id.activity_home_bottom_roller_layout_textview == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, CouponListActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中底部的 4个小图标*/
		else if(R.id.activity_home_bottom_logo_layout_btn_logo_1 == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ShopDetailActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中底部的 4个小图标*/
		else if(R.id.activity_home_bottom_logo_layout_btn_logo_2 == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ShopDetailActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中底部的 4个小图标*/
		else if(R.id.activity_home_bottom_logo_layout_btn_logo_3 == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ShopDetailActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中底部的 4个小图标*/
		else if(R.id.activity_home_bottom_logo_layout_btn_logo_4 == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, ShopDetailActivity.class);
			startActivity(this.activity_home_intent);
		}
		/**主页面 HomeActivity 中底部的 一个箭头图标（品牌墙）*/
		else if(R.id.activity_home_bottom_logo_layout_btn_logo_more == v.getId()){
			this.activity_home_intent.setClass(HomeActivity.this, BrandWallActivity.class);
			startActivity(this.activity_home_intent);
		}
		
	}

}
