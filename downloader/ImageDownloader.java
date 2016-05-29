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
package imagedownloader.downloader;

import imagedownloader.core.Downloader;
import imagedownloader.callback.DowloadListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Filippo <filippo.eng@gmail.com>
 * @version 1.0.0
 * @since 5/22/2016
 */
public class ImageDownloader extends Downloader {

    private String defaultFileName;

    public ImageDownloader(DowloadListener listener) {
        super(listener);
    }

    @Override
    protected void onInput(InputStream inputStream) {
        try {
            double total = 0.0;
            int lenght = -1;
            File file = getFile();
            FileOutputStream out = new FileOutputStream(file);
            byte buffer[] = new byte[BLOCK_SIZE];

            while ((lenght = inputStream.read(buffer, 0, BLOCK_SIZE)) != -1) {
                total = total + lenght;
                out.write(buffer, 0, lenght);
                listener.onProgress((int) ((total / fileSize) * 100));
            }
            inputStream.close();
            listener.onComplete(countPosition, file);
            downloadThread.interrupt();
        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }

    @Override
    protected void resolveFileName(String name) {
        defaultFileName = name == null || name.isEmpty() ? "test.jpg" : name;
    }

    private File getFile() throws IOException {
        String name = getFileName(defaultFileName);
        File file = new File(path + name + "." + getFileExtension(defaultFileName));
        String newFilename;
        int version = 1;
        while (file.exists()) {
            newFilename = name + version;
            file = new File(path + newFilename + "." + getFileExtension(defaultFileName));
            version++;
        }
        file.createNewFile();
        return file;
    }
}
