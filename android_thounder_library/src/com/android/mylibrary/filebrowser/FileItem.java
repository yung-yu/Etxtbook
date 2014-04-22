package com.android.mylibrary.filebrowser;

public class FileItem implements Comparable<FileItem>{
    private String name;
    private long DataSize;
    private String date;
    private String path;
    private String FileType;
   
	public FileItem(String n,long d, String dt, String p, String FileType)
    {
	    this.name = n;
        this.DataSize = d;
        this.date = dt;
        this.path = p;
        this.FileType = FileType;           
    }
    public String getName()
    {
            return name;
    }
    public long getDataSize()
    {
            return DataSize;
    }
    public String getDate()
    {
            return date;
    }
    public String getPath()
    {
            return path;
    }
    public String getFileType() {
		return FileType;
	}
    public int compareTo(FileItem o) {
            if(this.name != null)
                    return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
            else
                    throw new IllegalArgumentException();
    }
}
