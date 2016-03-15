package cn.net.sinotour;

import java.util.List;

import com.juma.sdk.JumaDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DevicesAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<JumaDevice> devices;
	public DevicesAdapter(Context context,List<JumaDevice> devices){
		this.inflater=LayoutInflater.from(context);
		this.devices=devices;
	}
	public void setData(List<JumaDevice> devices){
		this.devices=devices;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return devices==null||devices.size()==0?0:devices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView deviceName;
		if(convertView==null){
			convertView=inflater.inflate(android.R.layout.simple_list_item_1, null);
			deviceName=(TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(deviceName);
		}else{
			deviceName=(TextView) convertView.getTag();
		}
		deviceName.setText(devices.get(position).getName());
		return convertView;
	}

}
