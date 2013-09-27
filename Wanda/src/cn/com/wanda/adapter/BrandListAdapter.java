package cn.com.wanda.adapter;

import java.util.List;
import java.util.Map;

import cn.com.wanda.activity.R;
import cn.com.wanda.tools.ImageDownloader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class BrandListAdapter extends BaseAdapter {
	
	private List<Map<String, String>> brandList;
	private final ImageDownloader imageDownloader = new ImageDownloader();
	
	public BrandListAdapter(List<Map<String, String>> brandList){
		this.brandList = brandList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return brandList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return brandList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_list, null);
		
		ImageView weiboPic = (ImageView) retval.findViewById(R.id.brand_img);
		String picUrl = brandList.get(position).get("logo");
		Log.d("picUrl", picUrl);
		if(picUrl != null && !picUrl.equals("")){
			imageDownloader.download(picUrl, weiboPic);
		}
		ImageView v1 = (ImageView) retval.findViewById(R.id.imageRating1);
		ImageView v2 = (ImageView) retval.findViewById(R.id.imageRating2);
		ImageView v3 = (ImageView) retval.findViewById(R.id.imageRating3);
		
		float rating = Float.parseFloat(brandList.get(position).get("rating"));
		int score = (int) (rating / 0.5);
		switch ((int) (rating / 0.5)) {
		case 1:
			v1.setImageResource(R.drawable.icon_half_star);
			break;
		case 2:
			v1.setImageResource(R.drawable.icon_golden_star);
			break;
		case 3:
			v1.setImageResource(R.drawable.icon_golden_star);
			v2.setImageResource(R.drawable.icon_half_star);
			break;
		case 4:
			v1.setImageResource(R.drawable.icon_golden_star);
			v2.setImageResource(R.drawable.icon_golden_star);
			break;
		case 5:
			v1.setImageResource(R.drawable.icon_golden_star);
			v2.setImageResource(R.drawable.icon_golden_star);
			v3.setImageResource(R.drawable.icon_half_star);
			break;
		case 6:
			v1.setImageResource(R.drawable.icon_golden_star);
			v2.setImageResource(R.drawable.icon_golden_star);
			v3.setImageResource(R.drawable.icon_golden_star);
			break;
		default:
		}
			
		
		return retval;
	}

}
