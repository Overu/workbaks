package cn.com.wanda.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**优惠券列表页面 CouponListActivity*/
public class CouponListActivity extends Activity implements OnClickListener{

	/**跳转*/
	private Intent activity_couponlist_intent;
	/**优惠券列表页面 CouponListActivity 顶部title的返回键*/
	private ImageButton activity_couponlist_layout_title_layout_btn_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_couponlist);
		this.initView();
	}
	
	/**初始化控件*/
	private void initView(){
		this.activity_couponlist_intent = new Intent();
		
		this.activity_couponlist_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_couponlist_layout_title_layout_btn_back);
		this.activity_couponlist_layout_title_layout_btn_back.setOnClickListener(CouponListActivity.this);
	}

	@Override
	public void onClick(View v) {
		if(R.id.activity_couponlist_layout_title_layout_btn_back == v.getId()){
			this.activity_couponlist_intent.setClass(CouponListActivity.this, HomeActivity.class);
			startActivity(this.activity_couponlist_intent);
			CouponListActivity.this.finish();
		}
	}
}
