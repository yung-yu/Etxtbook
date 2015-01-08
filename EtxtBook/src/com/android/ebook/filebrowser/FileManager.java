package com.android.ebook.filebrowser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;




public class FileManager {

	private File currentDirFile;
	String currentDir = "/";
	FileChangedListener sonFileChangedListener;
	public final static String DIRECTORY="directory";
	public final static String FILE="file";
	private List<FileItem> tmplist = new ArrayList<FileItem>();
	//	public boolean containTmpFile(FileItem item){
	//		for(FileItem mFileItem:tmplist)
	//			if(item.getPath().equals(mFileItem.getPath()))
	//				return true;
	//		return false;
	//	}
	//	public void addTmpFile(FileItem item){
	//			tmplist.add(item);
	//	}
	//	public void removeTmpFile(FileItem item){
	//			tmplist.remove(item);
	//	}
	//	public void clearTmpFile(){
	//			tmplist.clear();
	//	}
	//	public List<FileItem> getTmpFiles(){
	//		return tmplist;
	//	}
	private FileItem tmpfile;

	public FileItem getTmpFileItem() {
		return tmpfile;
	}
	public void setTmpFileItem(FileItem fileItem) {
		this.tmpfile = fileItem;
	}
	public interface FileChangedListener{
		void toDir( List<FileItem> fileList,String Path);
		void selectFile(FileItem item);
	}
	public void setOnFileChangedListener(FileChangedListener mFileChangedListener){
		sonFileChangedListener = mFileChangedListener;
	}
	public void toDir( List<FileItem> dirList,String Path){
		if(sonFileChangedListener!=null)
			sonFileChangedListener.toDir(dirList, Path);
	}
	public void selectFile(FileItem item){
		if(sonFileChangedListener!=null)
			sonFileChangedListener.selectFile(item);
	}
	public FileManager( ){
	}
	/**初始化檔案path*/
	public void init(File f){
		analysisFile(f);
	}
	private void analysisFile(File f)
	{
		currentDirFile = f;
		File[]dirs = f.listFiles();
		List<FileItem>dir = new ArrayList<FileItem>();
		List<FileItem>fls = new ArrayList<FileItem>();
		try{
			for(File ff: dirs){
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
				else{
					fls.add(new FileItem(ff.getName(),ff.length(), date_modify, ff.getAbsolutePath(),FILE));
				}
			}
		}
		catch(Exception e){   
			e.printStackTrace();
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		toDir(dir,currentDirFile.getAbsolutePath());
	}
	/**返回上一層*/
	public void Back(){
		if(currentDirFile!=null){
			String curPath = currentDirFile.getParent();
			if(curPath!=null){
				analysisFile(new File(curPath));
			}
		}
	}
	public File getCurDirFile(){
		return currentDirFile;
	}
	/**重新整理*/
	public void refreach(){
		analysisFile(currentDirFile);
	}
	/**選取FileItem時 呼叫已做選檔或跳下一層處理*/
	public void analysisFile(FileItem o){
		if(checkFileIsDir(o)){
			analysisFile( new File(o.getPath()));
		}
		else{
			selectFile(o);
		}
	}
	/**檢查fileitem是否為存在*/
	public static boolean checkFileIsExist(String path){
		File f = new File(path);
		return f.exists();	
	}
	/**檢查fileitem是否為資料夾*/
	public boolean checkFileIsDir(FileItem o){
		if(o.getFileType().equalsIgnoreCase(DIRECTORY)){
			return true;
		}
		else{
			return false;
		}
	}
	/**複製*/
	public static boolean copy(File src, File dst){
		try{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	/**更名**/
	public static boolean rename(File oldfile,String newname){
		File newfile =new File(oldfile.getParent(),newname);
		if(oldfile.renameTo(newfile)){
			oldfile.delete();
			return true;
		}else{
			return false;
		}
	}
	/**剪下**/
	public static boolean cut(File file,String dstPath){
		File desfile = new File(dstPath,file.getName());
		try{
			if (!desfile.exists()) {
				desfile.createNewFile();
			}
			if(copy(file, desfile)){
				file.delete();
				return true;
			}
		}catch(Exception e){
            e.printStackTrace();
		}

		return false;

	}
	/**
	 * 新增資料夾
	 */
	public static boolean createNewDir(String dstPath,String dirName){
		File desfile = new File(dstPath);
		String[] filelist = desfile.list();
		int count=0;
		for(int i=0;i<filelist.length;i++){
			if(dirName.equals(filelist[i])){
				count++;
			}
		}
		if(count>0){
			dirName+="("+count+")";
		}
		File newdesfile = new File(dstPath,dirName);
		if(!newdesfile.exists()){
			if(newdesfile.mkdirs()){
				return true;
			}
		}
		return false;
	}
}
