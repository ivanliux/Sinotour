package cn.net.sinotour;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.net.sinotour.utils.HttpDownloader;
import cn.net.sinotour.utils.HttpDownloader.IDownload;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

public class MainActivity extends Activity {
	private ScanHelper helper;
	private ListView lvDevices;
	private List<JumaDevice> devices=new ArrayList<JumaDevice>();
	private DevicesAdapter adapter;
	private Button btnDownload;
	private ProgressBar pbDownload;
	private String filePath;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				pbDownload.setProgress((Integer) msg.obj);
				btnDownload.setText("正在下载%"+msg.obj);
			}else if(msg.what==100){
				btnDownload.setText("离线包已下载完成");
			}
		};
	};
	ScanCallback callback=new ScanCallback() {
		
		@Override
		public void onScanStateChange(int arg0) {
			System.out.println("scanStateChange==="+arg0);
			
		}
		
		@Override
		public void onDiscover(JumaDevice arg0, int arg1) {
			System.out.println(arg0.getName());
			devices.add(arg0);
			adapter.setData(devices);
			//判断本地离线包中是否有相同uuid，如果有播放该景点介绍
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper=new ScanHelper(this, callback);
        initView();
        filePath=getCacheDir()+File.separator+"Sintour"+File.separator;
        listenClick();
    }
    
    private void listenClick() {
		btnDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//下载景区离线包
				HttpDownloader.getInstance(getApplicationContext()).addDownloadTask
				("http://192.168.0.103:8080/Two%20Steps%20From%20Hell-El%20Dorado%20Dubstep%20(Remix)%20-%20remix.mp3", filePath,"a.mp3", new IDownload() {
					
					@Override
					public void message(String msg) {
						if(msg.equals(HttpDownloader.DOWNLOAD_SUCCESS)){
							handler.sendEmptyMessage(100);
							
						}
						
					}
					
					@Override
					public void loading(int progress) {
						Message msg = handler.obtainMessage();
						msg.what=1;
						msg.obj=progress;
						handler.sendMessage(msg);
						
					}
				});
			}
		});
		
	}

	private void initView() {
		pbDownload=(ProgressBar) findViewById(R.id.pb_progress);
    	btnDownload=(Button) findViewById(R.id.btn_download);
		lvDevices=(ListView) findViewById(R.id.lv_devices);
		adapter=new DevicesAdapter(this, devices);
		lvDevices.setAdapter(adapter);
	}
	/**
	 * 扫描蓝牙设备
	 */
    private void scanDevices(){
    	if(helper.isEnabled()){
    		if(!helper.isScanning()){
    			helper.startScan(null);
    		}
    	}else{
    		boolean enable = helper.enable();
    		if(enable){
    			scanDevices();
    		}else{
    			Toast.makeText(this, "抱歉，您的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
    			return;
    		}
    	}
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	scanDevices();
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	if(helper!=null){
    		helper.stopScan();
    	}
    }
}
