package com.android.ebook.filebrowser;



public class FileItem implements Comparable<FileItem>{
    private String name;
    private long dataSize;
    private String date;
    private String path;
    private String fileType;
   
	public FileItem(String name,long dataSize, String date, String path, String fileType)
    {
	    this.name = name;
        this.dataSize = dataSize;
        this.date = date;
        this.path = path;
        this.fileType = fileType;           
    }
    public String getName()
    {
            return name;
    }
    public long getDataSize()
    {
            return dataSize;
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
		return fileType;
	}
    public int compareTo(FileItem o) {
            if(this.name != null)
                    return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
            else
                    throw new IllegalArgumentException();
    }
}
