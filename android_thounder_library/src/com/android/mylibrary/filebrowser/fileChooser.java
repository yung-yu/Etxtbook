package com.android.mylibrary.filebrowser;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class fileChooser {
    private File currentDirFile;
	String currentDir = "/";
	FileChangedListener sonFileChangedListener;
	public final static String DIRECTORY="directory";
	public final static String FILE="file";
	public interface FileChangedListener{
		void toDir( List<FileItem> dirList,List<FileItem> filelist,String Path);
		void selectFile(FileItem item);
	}
	public void setOnFileChangedListener(FileChangedListener mFileChangedListener){
		sonFileChangedListener = mFileChangedListener;
	}
	public void toDir( List<FileItem> dirList,List<FileItem> filelist,String Path){
		if(sonFileChangedListener!=null)
			sonFileChangedListener.toDir(dirList,filelist, Path);
	}
	public void selectFile(FileItem item){
		if(sonFileChangedListener!=null)
			sonFileChangedListener.selectFile(item);
	}
	public fileChooser( ){
	}
	/**初始化檔案path*/
	public void init(File f){
		AnalysisFile(f);
	}
	private void AnalysisFile(File f)
	{
		currentDirFile = f;
		File[]dirs = f.listFiles();
		List<FileItem>dir = new ArrayList<FileItem>();
		List<FileItem>fls = new ArrayList<FileItem>();
		try{
			for(File ff: dirs)
			{
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){


					File[] fbuf = ff.listFiles();
					long buf = 0;
					if(fbuf != null){
						buf = fbuf.length;
					}
					else buf = 0;
					
					dir.add(new FileItem(ff.getName(),buf,date_modify,ff.getAbsolutePath(),DIRECTORY));
				}
				else
				{
					fls.add(new FileItem(ff.getName(),ff.length(), date_modify, ff.getAbsolutePath(),FILE));
				}
			}
		}catch(Exception e)
		{   

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		toDir(dir,fls,currentDirFile.getAbsolutePath());
	}
	/**返回上一層*/
	public void Back(){
		if(currentDirFile!=null)
		{
			String curPath = currentDirFile.getAbsolutePath();
			int index = curPath.lastIndexOf("/");
			if(index==-1){
				return ;
			}else if(index==0){
				curPath = "/";
				AnalysisFile(new File(curPath));
			}
			else{
				curPath = curPath.substring(0,index);
				AnalysisFile(new File(curPath));
			}
		}
	}
	/**選取FileItem時 呼叫已做選檔或跳下一層處理*/
	public void AnalysisFile(FileItem o){
		if(CheckFileIsDir(o))
		{
			AnalysisFile( new File(o.getPath()));
		}
		else
		{
			selectFile(o);
		}
	}
	/**檢查fileitem是否為資料夾*/
	public boolean CheckFileIsDir(FileItem o){
		if(o.getFileType().equalsIgnoreCase(DIRECTORY))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
