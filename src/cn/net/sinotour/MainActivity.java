package cn.net.sinotour;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

public class MainActivity extends Activity {
	private ScanHelper helper;
	private ListView lvDevices;
	private List<JumaDevice> devices=new ArrayList<JumaDevice>();
	private DevicesAdapter adapter;
	private Button btnDownload;
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
        listenClick();
    }
    
    private void listenClick() {
		btnDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//下载景区离线包
				
			}
		});
		
	}

	private void initView() {
    	btnDownload=(Button) findViewById(R.id.btn_download);
		lvDevices=(ListView) findViewById(R.id.lv_devices);
		adapter=new DevicesAdapter(this, devices);
		lvDevices.setAdapter(adapter);
	}
	/**
	 * 扫描蓝牙设备
	 */
    private void scanDevices(){
    	if(helper==null){
    		Toast.makeText(this, "抱歉，您的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(helper.isEnabled()){
    		if(!helper.isScanning()){
    			helper.startScan(null);
    		}
    	}else{
    		helper.enable();
    		scanDevices();
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
