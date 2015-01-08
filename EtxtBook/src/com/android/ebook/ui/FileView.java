package com.android.ebook.ui;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.android.ebook.R;
import com.android.ebook.data.SharePerferenceHelper;
import com.android.ebook.filebrowser.FileItem;
import com.android.ebook.filebrowser.FileManager;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileView {
	private ListView lv;
	private ImageView bt_back;
	TextView tv;
	Context mContext;	  
	View convertView;
	private FileAdapter fadapter;
	private List<FileItem> fList =new ArrayList<FileItem>();
	private FileManager mfileChooser;
	OnFileEventListener sOnFileEventListener;


	public FileView(Context context){
		mContext = context;

		convertView = LayoutInflater.from(context).inflate(R.layout.file_actionheader, null);
		lv = (ListView)convertView.findViewById(android.R.id.list);
		bt_back = (ImageView)convertView.findViewById(R.id.bt_back);
		tv = (TextView)convertView.findViewById(R.id.textView1);
		fadapter = new FileAdapter(context);
		lv.setAdapter(fadapter);

		mfileChooser = new FileManager();
		mfileChooser.setOnFileChangedListener(new FileEvent());
		bt_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mfileChooser.Back();
			}
		});
		File f = new File(SharePerferenceHelper.getIntent(context).getString("fileSrc", "/"));
		if(!f.exists()){
			mfileChooser.init(new File("/"));
		}
		else{
			mfileChooser.init(f);
		
		}

	}
	public View getView(){
		return convertView;
	}
	
	public void setFileSrc(File f){
		if(mfileChooser!=null)
			mfileChooser.init(f);
	}
	public interface OnFileEventListener{
		void selectedFile(FileItem file);   
	}
	public void setOnFileEventListener(OnFileEventListener mOnFileEventListener) {
		this.sOnFileEventListener = mOnFileEventListener;
	}
	private class FileEvent implements FileManager.FileChangedListener{

		@Override
		public void selectFile(FileItem item) {
			// TODO Auto-generated method stub
			if(sOnFileEventListener!=null)
				sOnFileEventListener.selectedFile(item);

		}

		@Override
		public void toDir(List<FileItem> fileList,final String path) {
			// TODO Auto-generated method stub
			SharePerferenceHelper.getIntent(mContext).setString("fileSrc",path);
			fList.clear();
			fList.addAll(fileList);
			fadapter.notifyDataSetChanged();
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tv.setText(path);
				}
			});
		}

	}

	private class FileAdapter extends BaseAdapter{
		LayoutInflater inflater;  
		class viewholder{
			TextView tv;
			ImageView iv;
		}
		public FileAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			viewholder vh;
			if(v == null){
				vh = new viewholder();
				v = inflater.inflate(R.layout.fileitem, null);
				vh.tv = (TextView)v.findViewById(R.id.textView1);
				vh.iv = (ImageView)v.findViewById(R.id.imageView1);
				v.setTag(vh);
			}else{
				vh = (viewholder) v.getTag();
			}
			vh.tv.setText(fList.get(position).getName());
			if(fList.get(position).getFileType().equals(FileManager.DIRECTORY)){
				vh.iv.setVisibility(View.VISIBLE);
			}else{
				vh.iv.setVisibility(View.GONE);
			}
			v.setOnClickListener(new  View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					mfileChooser.analysisFile(fList.get(position));
				}
				
			});
			return v;
		}

	}
}
