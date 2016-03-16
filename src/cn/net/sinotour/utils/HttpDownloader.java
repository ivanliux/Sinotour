package cn.net.sinotour.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.util.Log;

/**
 * 	下载
 */
public class HttpDownloader {
	/**下载成功**/
	public final static String DOWNLOAD_SUCCESS="success";
	/**下载失败**/
	public final static String DOWNLOAD_FAILURE="failure";
	/**下载的任务存入Map记录，下载完成删除记录**/
	private Map<String, DownLoadThread> DownLoadThreads=new HashMap<String, DownLoadThread>();
	/**类单例**/
	private Context context;
	private volatile static HttpDownloader mInstance;
	public static HttpDownloader getInstance(Context context) {
		if (null == mInstance) {
			synchronized (HttpDownloader.class) {
				if (null == mInstance) {
					mInstance = new HttpDownloader(context);
				}
			}
		}
		return mInstance;
	}
	private HttpDownloader(Context context){
		this.context=context;
	}
	/**
	 * 添加一个下载任务
	 * @param urlPath
	 * @param filePath
	 * @param fileName
	 * @param iDownload
	 */
	public void addDownloadTask(String urlPath,String filePath,IDownload iDownload){
		DownLoadThread thread = DownLoadThreads.get(urlPath);
		Log.e("downloadmap", DownLoadThreads.toString());
		if(thread==null){
			DownLoadThread downLoadThread=new DownLoadThread(urlPath, filePath, iDownload);
			downLoadThread.start();
		}
	}
	public void addDownloadTask(String urlPath,String filePath,String filename,IDownload iDownload){
		DownLoadThread thread = DownLoadThreads.get(urlPath);
		Log.e("downloadmap", DownLoadThreads.toString());
		if(thread==null){
			DownLoadThread downLoadThread=new DownLoadThread(urlPath, filePath,filename,iDownload);
			downLoadThread.start();
		}
	}
	/**
	 * 下载线程
	 * @author Orson
	 *
	 */
	private class DownLoadThread extends Thread {
		private String urlPath;
		private String filePath;
		private IDownload iDownload;
		private int timeoutCount;
		private String fileName;
		public DownLoadThread(String urlPath,String filePath,IDownload iDownload){
			this.urlPath=urlPath;
			this.filePath=filePath;
			this.iDownload=iDownload;
			this.timeoutCount=0;
		}
		public DownLoadThread(String urlPath,String filePath,String fileName,IDownload iDownload){
			this.urlPath=urlPath;
			this.filePath=filePath;
			this.fileName=fileName;
			this.iDownload=iDownload;
			this.timeoutCount=0;
		}
		@Override
		public void run() {
			downloadFile(urlPath, filePath,fileName,iDownload);
			super.run();
		}
		/**
		 * 
		 *  下载任务
		 * @param urlPath
		 * @param filepath
		 * @param fileName
		 * @param upload
		 */
		public  void downloadFile(String urlPath, String filePath,String fileName,
				IDownload iDownload) {
			DownLoadThreads.put(urlPath, this);
			Log.e("downloadmap", DownLoadThreads.toString());
			Double double1;
			Double double2;
			HttpURLConnection conn = null;
			InputStream is = null;
			try {
				URL url = new URL(URLString2utf8(urlPath));
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setReadTimeout(5 * 1000);
				conn.setRequestMethod("GET");
				int length2 = conn.getContentLength();
				Log.e("length", length2 + "文件大小");
				int code = conn.getResponseCode();
				if (code == 200) {
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					File f=null;
					if(fileName==null){
						f= new File(file, getFileName(urlPath));
					}else{
						f=new File(file, fileName);
					}
					
					FileOutputStream outstream = new FileOutputStream(f);
					is = conn.getInputStream();
					byte[] buffer = new byte[1024];
					int length = -1;
					int total = 0;
					while ((length = is.read(buffer)) != -1) {
						outstream.write(buffer, 0, length);
						total += length;
						double1 = (double) length2;
						double2 = (double) total;
						Double progress = (double2 / double1);
						iDownload.loading((int) (progress * 100));
					}
					outstream.close();
					iDownload.message(DOWNLOAD_SUCCESS);
					DownLoadThreads.remove(urlPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				iDownload.message(DOWNLOAD_FAILURE);
				if(timeoutCount<5){
					if(NetUtils.isNetworkConnected(context)){
						timeoutCount++;
						DownLoadThreads.remove(urlPath);
						Log.e("main", this.getId()+"timeoutCount"+timeoutCount);
						downloadFile(urlPath, filePath,fileName,iDownload);
					}
				}else{
					timeoutCount=0;
					DownLoadThreads.remove(urlPath);
					Log.e("downloadmap", "finally"+DownLoadThreads.toString());
				}
			}finally{
				try {
					Log.e("downloadmap", "finally"+DownLoadThreads.toString());
					if(is!=null){
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					iDownload.message(DOWNLOAD_FAILURE);
				}
			}

		}
		 private String getFileName(String path)
		    {
		        int separatorIndex = path.lastIndexOf("/");
		        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
		    }

		/**
		 * 	中文转换成Unicode，只转换中文
		 * @param String 字符
		 */
		private  String URLString2utf8(String s) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= 0 && c <= 255) {
					sb.append(c);
				} else {
					try {
						sb.append(URLEncoder.encode(c + "", "utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			return sb.toString();
		}
	}
	public interface IDownload{
		public void message(String msg);
		public void loading(int progress);
	}
}
