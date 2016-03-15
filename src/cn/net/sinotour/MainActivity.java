package cn.net.sinotour;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
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
	ScanCallback callback=new ScanCallback() {
		
		@Override
		public void onScanStateChange(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDiscover(JumaDevice arg0, int arg1) {
			System.out.println(arg0.getName());
			devices.add(arg0);
			adapter.setData(devices);
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper=new ScanHelper(this, callback);
        initView();
    }
    
    private void initView() {
		lvDevices=(ListView) findViewById(R.id.lv_devices);
		adapter=new DevicesAdapter(this, devices);
		lvDevices.setAdapter(adapter);
	}
	
    private void scanDevices(){
    	if(helper==null){
    		Toast.makeText(this, "该设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(helper.isEnabled()){
    		helper.startScan(null);
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
    
}
