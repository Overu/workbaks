package cn.com.wanda.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

/**品牌墙页面 BrandWallActivity*/
public class BrandWallActivity extends Activity implements OnClickListener{
	/**跳转*/
	private Intent activity_brandwall_intent;
	/**品牌墙页面 BrandWallActivity 顶部title的返回键*/
	private ImageButton activity_brandwall_layout_title_layout_btn_back;
	/**品牌墙页面 BrandWallActivity 展示品牌墙的GridView控件*/
	private GridView activity_brandwall_layout_gridview;
	/**品牌墙页面 BrandWallActivity 展示品牌墙的GridView的适配器*/
//	private BrandListAdapter activity_brandwall_layout_gridview_adapter;
	/**品牌墙页面 BrandWallActivity 品牌墙资源*/
	private List<Map<String, Object>> activity_brandwall_layout_gridview_list;
	
	/**品牌墙 logo图片资源*/
	private int activity_brandwall_layout_gridview_list_logo[] = {
			R.drawable.logo_converse,
			R.drawable.logo_costa_coffee,
			R.drawable.logo_dairy_queen,
			R.drawable.logo_dairy_queen,
			R.drawable.logo_emoi,
			R.drawable.logo_esprit,
			R.drawable.logo_folli_follie,
			R.drawable.logo_gap,
			R.drawable.logo_guess,
			R.drawable.logo_haagen_dazs,
			R.drawable.logo_hm,
			R.drawable.logo_kfc,
			R.drawable.logo_moschino,
			R.drawable.logo_pizza_hut,
			R.drawable.logo_prada,
			R.drawable.logo_sasa,
			R.drawable.logo_sephora,
			R.drawable.logo_shundian,
			R.drawable.logo_starbucks,
			R.drawable.logo_suxie,
			R.drawable.logo_swarovski,
			R.drawable.logo_swatch,
			R.drawable.logo_vans,
			R.drawable.logo_versace,
			R.drawable.logo_watsons,
			R.drawable.logo_zara,
			R.drawable.logo_zuczug,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brandwall);
		this.initView();
		this.setData();
	}
	
	/**初始化控件*/
	private void initView(){
		this.activity_brandwall_intent = new Intent();
		
		this.activity_brandwall_layout_title_layout_btn_back = (ImageButton) findViewById(R.id.activity_brandwall_layout_title_layout_btn_back);
		this.activity_brandwall_layout_title_layout_btn_back.setOnClickListener(BrandWallActivity.this);
		
		this.activity_brandwall_layout_gridview = (GridView) findViewById(R.id.activity_brandwall_layout_gridview);
	}

	@Override
	public void onClick(View v) {
		if(R.id.activity_brandwall_layout_title_layout_btn_back == v.getId()){
			this.activity_brandwall_intent.setClass(BrandWallActivity.this, HomeActivity.class);
			startActivity(this.activity_brandwall_intent);
			BrandWallActivity.this.finish();
		}
	}
	
	/**给GridView添加数据*/
	private void setData(){
		activity_brandwall_layout_gridview_list = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < activity_brandwall_layout_gridview_list_logo.length; i++){
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("logo", activity_brandwall_layout_gridview_list_logo[i]);
//			hm.put("rating", Math.random() * 3 + "");
			hm.put("rating1", R.drawable.icon_golden_star);
			hm.put("rating2", R.drawable.icon_half_star);
			hm.put("rating3", R.drawable.icon_gray_star);
			activity_brandwall_layout_gridview_list.add(hm);
		}
		
		/**生成适配器的Item和动态数组对应的元素 */
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, 
				activity_brandwall_layout_gridview_list, /**数据源*/
				R.layout.item_brand_list, /**ListItem的XML布局*/
				new String[] {"logo","rating1","rating2","rating3"},
				new int[] {R.id.brand_img,R.id.imageRating1,R.id.imageRating2,R.id.imageRating3} /***/
				);
		
//		activity_brandwall_layout_gridview_adapter = new BrandListAdapter(activity_brandwall_layout_gridview_list);
		/**给GridView加适配器*/
		activity_brandwall_layout_gridview.setAdapter(simpleAdapter);
		activity_brandwall_layout_gridview.setOnItemClickListener(onItemClickListener);
		
	}
	
	/**ListView点击事件*/
	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			/**跳转到详细界面*/
			activity_brandwall_intent = new Intent(BrandWallActivity.this, ShopDetailActivity.class);
			startActivity(activity_brandwall_intent);
		}
	};
	
	

}
