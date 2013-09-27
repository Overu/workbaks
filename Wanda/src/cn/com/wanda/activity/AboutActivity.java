package cn.com.wanda.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**关于页面 AboutActivity*/
public class AboutActivity extends Activity implements OnClickListener{
	/**跳转*/
	private Intent activity_about_intent;
	/**关于页面 AboutActivity 顶部title的返回键*/
	private ImageButton activity_about_layout_title_layout_btn_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		this.initView();
	}
	
	/**初始化控件*/
	private void initView(){
		this.activity_about_intent = new Intent();
		
		this.activity_about_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_about_layout_title_layout_btn_back);
		this.activity_about_layout_title_layout_btn_back.setOnClickListener(AboutActivity.this);
	}

	@Override
	public void onClick(View v) {
		if(R.id.activity_about_layout_title_layout_btn_back == v.getId()){
			this.activity_about_intent.setClass(AboutActivity.this, HomeActivity.class);
			startActivity(this.activity_about_intent);
			AboutActivity.this.finish();
		}
	}
	
	
}
