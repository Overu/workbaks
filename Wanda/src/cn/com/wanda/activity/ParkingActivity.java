package cn.com.wanda.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**停车位页面 ParkingActivity*/
public class ParkingActivity extends Activity implements OnClickListener{
	/**跳转*/
	private Intent activity_parking_intent;
	/**停车位页面 ParkingActivity 顶部title的返回键*/
	private ImageButton activity_parking_layout_title_layout_btn_back;
	
	private TextView activity_parking_layout_record_tip;
	private Button activity_parking_layout_record_btn;
	private TextView activity_parking_layout_record_record;
	private Button activity_parking_layout_navigate_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking);
		this.initView();
	}
	
	/**初始化控件*/
	private void initView(){
		this.activity_parking_intent = new Intent();
		
		this.activity_parking_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_parking_layout_title_layout_btn_back);
		this.activity_parking_layout_title_layout_btn_back.setOnClickListener(ParkingActivity.this);
	
		this.activity_parking_layout_record_tip = (TextView) findViewById(R.id.activity_parking_layout_record_tip);
		
		this.activity_parking_layout_record_btn = (Button) findViewById(R.id.activity_parking_layout_record_btn);
		this.activity_parking_layout_record_btn.setOnClickListener(ParkingActivity.this);
		
		this.activity_parking_layout_record_record = (TextView) findViewById(R.id.activity_parking_layout_record_record);
		
		this.activity_parking_layout_navigate_btn = (Button) findViewById(R.id.activity_parking_layout_navigate_btn);
		this.activity_parking_layout_navigate_btn.setOnClickListener(ParkingActivity.this);
	
	
	}

	@Override
	public void onClick(View v) {
		if(R.id.activity_parking_layout_title_layout_btn_back == v.getId()){
			this.activity_parking_intent.setClass(ParkingActivity.this, HomeActivity.class);
			startActivity(this.activity_parking_intent);
			ParkingActivity.this.finish();
		}
		else if(R.id.activity_parking_layout_record_btn == v.getId()){
			Toast.makeText(ParkingActivity.this, "已经记录了您的停车地址！", Toast.LENGTH_SHORT).show();
			
			this.activity_parking_layout_record_tip.setVisibility(View.INVISIBLE);
			this.activity_parking_layout_record_btn.setVisibility(View.INVISIBLE);
			
			this.activity_parking_layout_record_record.setText("您的停车地址在，地下一层！点击下面按钮将带您去停车的位置！");
			this.activity_parking_layout_record_record.setVisibility(View.VISIBLE);
			this.activity_parking_layout_navigate_btn.setVisibility(View.VISIBLE);
		}
		else if(R.id.activity_parking_layout_navigate_btn == v.getId()){
			Toast.makeText(ParkingActivity.this, "开始导航！", Toast.LENGTH_SHORT).show();
			this.activity_parking_intent.setClass(ParkingActivity.this, MapActivity.class);
			startActivity(this.activity_parking_intent);
			ParkingActivity.this.finish();
		
		}
	}
}
