/**
 * Copyright 2016 Filippo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package imagedownloader.core;

import imagedownloader.callback.Downloadable;
import imagedownloader.callback.DowloadListener;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Filippo <filippo.eng@gmail.com>
 * @version 1.0.0
 * @since 5/22/2016
 */
public abstract class Downloader implements Downloadable, Runnable {

    private final static int TIMEOUT = 25000;

    protected Thread downloadThread;
    protected final DowloadListener listener;
    protected final static int BLOCK_SIZE = 4096;
    protected int fileSize;
    protected String fileName;
    protected int countPosition;
    protected String path;

    public Downloader(DowloadListener listener) {
        this.listener = listener;
        this.downloadThread = new Thread(Downloader.this);
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(listener.getUrl()).openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
            connection.setRequestProperty("Accept", "*/*");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                String fileName = "";
                String disposition = connection.getHeaderField("Content-Disposition");
                String contentType = connection.getContentType();
                fileSize = connection.getContentLength();

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + fileSize);
                System.out.println("fileName = " + fileName);

                String name = new File(listener.getUrl()).getName();
                resolveFileName(name);
                onInput(connection.getInputStream());
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            connection.disconnect();

        } catch (Exception e) {
            this.listener.onError(e.getMessage());
        }
    }

    /**
     *
     * @param countPosition
     */
    @Override
    public void startDownload(int countPosition) {
        this.countPosition = countPosition;
        this.downloadThread.start();
    }

    @Override
    public void stopDowload() {
        this.downloadThread.interrupt();
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *
     * @param path
     */
    @Override
    public void setDownloadPath(String path) {
        this.path = path;
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
        } catch (Exception e) {
            listener.onError("Directory Already Exist!!!");
        }
    }

    protected String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    protected String getFileName(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return pos > 0 ? fileName.substring(0, pos) : fileName;
    }

    protected abstract void onInput(InputStream inputStream);

    protected abstract void resolveFileName(String name);
}
