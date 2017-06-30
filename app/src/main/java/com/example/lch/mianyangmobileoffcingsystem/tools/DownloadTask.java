package com.example.lch.mianyangmobileoffcingsystem.tools;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.interfaces.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/16.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;
    public static final int PAUSED = 2;
    public static final int CANCELED = 3;
    private DownloadListener downloadListener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;
    private static final String TAG = "DownloadTask";

    public DownloadTask(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        File file = null;
        try {

            long downloadedLength = 0;//已经下载的文件长度
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            Log.e(TAG, "doInBackground: " + directory+ fileName);
            file = new File(directory + fileName);
            MyCache.setFileName(fileName);
            if (file.exists()) {
                downloadedLength = file.length();//记录已经下载的文件的长度
            }
            long contentLength = getContentLength(downloadUrl);//获得文件总长度
            Log.e(TAG, "doInBackground: contentLength = " +  contentLength   );
            if (contentLength == 0) {
                return FAILED;
            } else if (contentLength == downloadedLength) {//远程文件的长度和本地文件长度相同，已经下载完全
                return SUCCESS;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")//指定从文件哪个位置开始下载，断点续传
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            Log.e(TAG, "doInBackground: " + (response == null) );
            if (response != null) {
                inputStream = response.body().byteStream();
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(downloadedLength);//跳过已经下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int length;
                while ((length = inputStream.read(b)) != -1) {//read()返回-1表示文件流已经完了
                    if (isCanceled) {
                        return CANCELED;
                    } else if (isPaused) {
                        return PAUSED;
                    } else {
                        total += length;
                        randomAccessFile.write(b, 0, length);
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);//计算已经下载的百分比
                        publishProgress(progress);

                    }
                }
                response.body().close();
                return SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: okhttpException " + e );
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();//删除取消任务后留下的缓存文件
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "doInBackground: finally return Failed");
        return FAILED;
    }

    /*返回文件总长度*/
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    /*更新进度*/
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            downloadListener.onProgress(progress);
            lastProgress = progress;
        }
    }

    /*执行完后台任务后更新UI,显示结果*/
    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case SUCCESS:
                downloadListener.onSuccess();
                break;
            case FAILED:
                downloadListener.onFailed();
                break;
            case PAUSED:
                downloadListener.onPaused();
                break;
            case CANCELED:
                downloadListener.onCanceled();
                break;
            default:
                break;
        }
    }
    public void PausedDownload() {
        isPaused = true;
    }
    public void CancelDownload() {
        isCanceled = true;
    }
}
