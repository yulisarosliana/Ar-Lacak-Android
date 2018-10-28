package org.inkubator.radinaldn;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

  

	public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
        	
            vi = inflater.inflate(R.layout.list_row, null);
        	
        	TextView id = (TextView)vi.findViewById(R.id.kode);
	        TextView title = (TextView)vi.findViewById(R.id.title);
	        TextView address = (TextView)vi.findViewById(R.id.address);
	        TextView number = (TextView)vi.findViewById(R.id.number);
	        TextView lat = (TextView)vi.findViewById(R.id.lat);
	        TextView lng = (TextView)vi.findViewById(R.id.lng);
	        ImageView image=(ImageView)vi.findViewById(R.id.image);
//	        TextView jarak = (TextView)vi.findViewById(R.id.jarak);
//	        TextView miles = (TextView)vi.findViewById(R.id.km);
	        
	        HashMap<String, String> berita = new HashMap<String, String>();
	        berita = data.get(position);
	        
	        id.setText(berita.get(ListActivity.AR_ID));
	        title.setText(berita.get(ListActivity.in_title));
	        address.setText(berita.get(ListActivity.in_address));
	        number.setText(berita.get(ListActivity.in_number));
	        lat.setText(berita.get(ListActivity.in_lat));
	        lng.setText(berita.get(ListActivity.in_lng));
//	        jarak.setText(berita.get(ListActivity.in_jarak));
//	        miles.setText(berita.get(ListActivity.miles));
	        imageLoader.DisplayImage(berita.get(ListActivity.in_image), image);
	        return vi;
    }
}