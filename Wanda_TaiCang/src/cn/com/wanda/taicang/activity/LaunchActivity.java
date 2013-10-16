package cn.com.wanda.taicang.activity;

import com.macrowen.macromap.draw.Map;
import com.macrowen.macromap.utils.MapService;
import com.macrowen.macromap.utils.MapService.MapLoadStatus;
import com.macrowen.macromap.utils.MapService.MapLoadStatusListener;

import cn.com.wanda.activity.HomeActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.widget.ImageView;

/** 启动页面 LaunchActivity */
public class LaunchActivity extends Activity implements MapLoadStatusListener {
  /** 启动页面的背景图 */
  private ImageView activity_launch_layout_imageview;
  /** 跳转 */
  private Intent activity_launch_intent;
  /** 启动页面停留时异步加载数据（1，数据从本地来，就加载数据库；2，数据从网上来就下载数据） */
  private Handler activity_launch_handler;

  private MapService mMapService = MapService.getInstance();

  @Override
  public void onMapLoadStatusEvent(MapLoadStatus mapLoadStatus, Map map) {
    if (mapLoadStatus == MapLoadStatus.MapDataInit) {
      loadDataTime();
    } else if (mapLoadStatus == MapLoadStatus.MapDataLoaded) {
      loadDataTime();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);
    this.initView();

    mMapService.setOnMapLoadStatusListener(this);
    mMapService.initMapData("3", "商场");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  /** 初始化控件 */
  private void initView() {
    this.activity_launch_layout_imageview = (ImageView) findViewById(R.id.activity_launch_layout_imageview);
    this.activity_launch_intent = new Intent();
    this.activity_launch_handler = new Handler() {
      /** 接受消息 */
      @Override
      public void handleMessage(Message msg) {
        skipActivity();
      }
    };
    // loadDataTime();
  }

  /**
   * 加载数据
   * 
   * 1，如果数据来自本地，启动页面停留多久，就取决于读取本地数据要多久；
   * 
   * 2，如果数据来自网络，启动页面停留多久，就取决于读取网络数据要多久；
   */
  private void loadDataTime() {
    new Thread() {

      @Override
      public void run() {
        // /**返回以毫秒为单位的当前时间*/
        // long time = System.currentTimeMillis();
        // /**写一些加载数据库的方法*/
        // AssetManager assetManager = InitActivity.this.getAssets();
        // /**初始化数据库 SystemParam---自定义的类，一些静态变量 */
        // SystemParam.initSystemParam(assetManager);
        // /**这里的time是用 加载完数据库后 的“当前时间”减去“上一个当前时间” */
        // time = System.currentTimeMillis() - time;
        // /**如果加载完数据库所耗的时间小于加载启动页的基数，就让当前线程休眠 加载启动页基础 减去 加载完数据库所耗时间 只差（保证启动页所耗时间怎么也大于2秒）*/
        // if(time < INIT_TIME){
        // this.sleep(INIT_TIME - time);
        // }
        // /**对于不需要传送额外数据的直接发送空消息就好Handler.sendEmptyMessage(int)*/
        // handler.sendEmptyMessage(1);

        /** 这里先暂时人工设置启动页面停留时间3000（3秒） */

        /** 对于不需要传送额外数据的直接发送空消息就好Handler.sendEmptyMessage(int) */
        activity_launch_handler.sendEmptyMessageDelayed(1, 1000);

        super.run();
      }

    }.start();
  }

  /** 跳转到主页面 HomeActivity */
  private void skipActivity() {
    this.activity_launch_intent.setClass(LaunchActivity.this, HomeActivity.class);
    this.startActivity(this.activity_launch_intent);
    LaunchActivity.this.finish();
  }

  // @Override
  // public boolean onCreateOptionsMenu(Menu menu) {
  // // Inflate the menu; this adds items to the action bar if it is present.
  // getMenuInflater().inflate(R.menu.main, menu);
  // return true;
  // }

}
