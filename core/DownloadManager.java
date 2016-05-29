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

import imagedownloader.callback.DowloadListener;
import imagedownloader.callback.Downloadable;
import imagedownloader.downloader.ImageDownloader;

/**
 * @author Filippo <filippo.eng@gmail.com>
 * @version 1.0.0
 * @since 5/22/2016
 */
public class DownloadManager<T> implements DowloadListener<T> {

    private final String url;
    private String fileName;
    private Downloadable downloadable;
    private int downloadCount;
    private int finished;

    public DownloadManager(String url) {
        this.url = url;
        init();
    }

    public DownloadManager(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        init();
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void onProgress(int progress) {
        System.out.print("\r" + progress);
    }

    @Override
    public void onError(String message) {
        System.err.println("Error -> " + message);
    }

    @Override
    public void onComplete(int position, T file) {
        System.out.println((++finished) + ") File Download Completed! -> Position :: " + position);
    }

    public void download() {
        this.downloadable.startDownload(downloadCount++);
    }

    public void cancel() {
        this.downloadable.stopDowload();
    }

    public void setDownloadPath(String path) {
        this.downloadable.setDownloadPath(path);
    }

    private void init() {
        this.downloadable = new ImageDownloader(this);
        this.downloadable.setFileName(fileName);
    }
}
