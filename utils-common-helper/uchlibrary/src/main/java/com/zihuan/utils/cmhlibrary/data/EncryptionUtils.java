
package com.zihuan.utils.cmhlibrary.data;

import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

public class EncryptionUtils {
    public EncryptionUtils() {
    }

    public static final String MD5(String argString) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] btInput = argString.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    public static final String MD5_16(String argString) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] btInput = argString.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            String strResult = new String(str);
            return strResult.substring(8, 24);
        } catch (Exception var10) {
            Log.i("----", var10.toString());
            var10.printStackTrace();
            return null;
        }
    }

    public static String getSha1(String str) {
        if (str != null && str.length() != 0) {
            char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

            try {
                MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
                mdTemp.update(str.getBytes("UTF-8"));
                byte[] md = mdTemp.digest();
                int j = md.length;
                char[] buf = new char[j * 2];
                int k = 0;

                for(int i = 0; i < j; ++i) {
                    byte byte0 = md[i];
                    buf[k++] = hexDigits[byte0 >>> 4 & 15];
                    buf[k++] = hexDigits[byte0 & 15];
                }

                return new String(buf);
            } catch (Exception var9) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String base64Decode(String m) {
        return new String(Base64.decode(m, 0));
    }

    public static String base64Encode(String m) {
        return new String(Base64.encode(m.getBytes(), 0));
    }

    public static void randomAccessEncryption(File file, int key) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        byte[] buff = new byte[8192];

        int n;
        while((n = raf.read(buff)) != -1) {
            for(int i = 0; i < n; ++i) {
                buff[i] = (byte)(buff[i] ^ key);
            }

            raf.seek(raf.getFilePointer() - (long)n);
            raf.write(buff, 0, n);
        }

        raf.close();
    }
}
