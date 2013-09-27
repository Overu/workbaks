package cn.com.wanda.tools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class JsonTools {
	public static ArrayList<MessageInfo> getList (String http) throws JSONException{
		ArrayList<MessageInfo> orderInfoList = new ArrayList<MessageInfo>();
		JSONObject jsonObject = new JSONObject(http);
		MessageInfo.count = jsonObject.getInt("count");
		MessageInfo.start = jsonObject.getInt("start");
		JSONArray jsonArray = jsonObject.getJSONArray("shops");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.opt(i);
			MessageInfo messageInfo = new MessageInfo();
			messageInfo.setCategory(object.getString("category"));
			messageInfo.setFloor(object.getString("floor"));
			messageInfo.setEnglish_name(object.getString("english_name"));
			messageInfo.setId(object.getString("id"));
			messageInfo.setName(object.getString("name"));
			messageInfo.setRates(object.getString("rates"));
			messageInfo.setShow_name(object.getString("show_name"));
			orderInfoList.add(messageInfo);
		}
		return orderInfoList;
	}
}
