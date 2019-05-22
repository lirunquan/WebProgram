package FileDownload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultithreadDownload {
	public static final int THREAD_COUNT = 4;
	DownloadUI ui;
	private String download_url;
	private String filePath;
	private int[] totals = {0,0,0,0,0};
	private long begin;
	private long[] end = new long[THREAD_COUNT];
	public MultithreadDownload(DownloadUI ui) {
		this.ui = ui;
		this.download_url = ui.fileUrlField.getText();
		this.filePath = ui.saveUrlField.getText();
		this.begin = System.currentTimeMillis();
	}
	private String getFileName(URL url) {
		String filename = url.getFile();
		return filename.substring(filename.lastIndexOf("/")+1);
	}
	private void cleanCache(File file) {
		file.delete();
	}
	public void show(String string, int num) {
		this.ui.labels[num].setText(string);
		System.out.println(string);
	}
	public void download() {
		HttpURLConnection connection = null;
		long size = 0;
		try {
			URL url = new URL(download_url);
			
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(10000);
			show("Connecting...", 0);
			if (connection.getResponseCode()==200) {
				show("Start downloading.", 0);
				String filename = filePath+getFileName(url);
				RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rw");
				size = connection.getContentLength();
				show("Total file size: "+size, 0);
				randomAccessFile.setLength(size);
				randomAccessFile.close();
				long block = size/THREAD_COUNT;
				for(int i=0; i<THREAD_COUNT; i++) {
					long start = i*block;
					long ending = (i+1)*block-1;
					if(i==THREAD_COUNT-1) {
						ending = size;
					}
					Runnable runnable = new DownloadThread(download_url, filename, i, start, ending);
					new Thread(runnable).start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null!=connection) {
				connection.disconnect();
			}
		}
		
	}
	class DownloadThread implements Runnable{
		long startIndex;
		long endIndex;
		int threadId;
		private String url;
		private String filename;
		private RandomAccessFile randomAccessFile;
		private InputStream inputStream;
		private HttpURLConnection connection;
		public DownloadThread(String url, String filename, int threadID, long start, long end) {
			// TODO Auto-generated constructor stub
			super();
			this.url = url;
			this.filename = filename;
			this.threadId = threadID;
			this.startIndex = start;
			this.endIndex = end;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			RandomAccessFile downAccessFile = null;
			File threadFile = new File(filePath, "thread_stream_"+threadId+".dt");
			try {
				if(threadFile.exists()) {
					downAccessFile = new RandomAccessFile(threadFile, "rwd");
					String startString = downAccessFile.readLine();
					if(!(null==startString||"".equals(startString))) {
						this.startIndex = Long.parseLong(startString)-1;
					}
				}
				else {
					downAccessFile = new RandomAccessFile(threadFile, "rwd");
				}
				connection = (HttpURLConnection) new URL(url+"?ts="+System.currentTimeMillis()).openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(10000);
				connection.setRequestProperty("RANGE", "bytes="+startIndex+"-"+endIndex);
				if (connection.getResponseCode()==206) {
					inputStream = connection.getInputStream();
					randomAccessFile = new RandomAccessFile(this.filename, "rwd");
					randomAccessFile.seek(startIndex);
					byte[] bytes = new byte[1024];
					int len;
					int total = 0;
					while ((len=inputStream.read(bytes))!=-1) {
						total += len;
						randomAccessFile.write(bytes, 0, len);
						downAccessFile.seek(0);
						downAccessFile.write((startIndex+total+"").getBytes("UTF-8"));
						totals[threadId] = total;
						show("Thread No."+(threadId+1)+": "+total+".", threadId+1);
					}
				}
				else {
					show("Download failed, Response code: "+connection.getResponseCode()+".", 0);
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				try {
					if (null!=downAccessFile) {
						downAccessFile.close();
					}
					if (null!=connection) {
						connection.disconnect();
					}
					if (null!=inputStream) {
						inputStream.close();
					}
					if (null!=randomAccessFile) {
						randomAccessFile.close();
					}
					cleanCache(threadFile);
					String string = ui.labels[threadId+1].getText();
					end[threadId] = System.currentTimeMillis();
					show(string+"Download success, cost "+((end[threadId]-begin)/1000)+"s.", threadId+1);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}
}
