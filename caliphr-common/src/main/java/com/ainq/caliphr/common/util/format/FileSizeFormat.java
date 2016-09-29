package com.ainq.caliphr.common.util.format;

import java.io.*;

/**
 * Created by mmelusky on 5/6/2015.
 */
public class FileSizeFormat {
    public static float getStreamSizeinMB(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }

        return getSizeInMB(buf.length);
    }

    public static float getFileSizeInMB(File file) {
        long ret = getFileSizeInBytes(file);
        return getSizeInMB(ret);
    }

    public static long getFileSizeInBytes(File f) {
        long ret = 0;
        if (f.isFile()) {
            return f.length();
        } else if (f.isDirectory()) {
            File[] contents = f.listFiles();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isFile()) {
                    ret += contents[i].length();
                }
            }
        }
        return ret;
    }
    
    public static float getSizeInMB(long size) {
    	return size / (float) (1024 * 1024);
    }
    
    public static float getSizeInKB(long size) {
    	return size / (float) 1024;
    }
}
