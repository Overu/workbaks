package cn.com.wanda.data;

public class CommonData {
	
	/**HTTP请求的Accept --- 指定客户端能够接收的内容类型*/
	public final static String WANDAHTTPHEADER ="Accept:application/json";
	
	public static String getShopList(String whichData,int map_id){
		String getDataLink = null;
		/**如果想获得商铺列表信息*/
		if("shops" == whichData){
			getDataLink = "http://apitest.palmap.cn/mall/"+map_id+"/shops";
		}
		
		return getDataLink;
		
	}
}
