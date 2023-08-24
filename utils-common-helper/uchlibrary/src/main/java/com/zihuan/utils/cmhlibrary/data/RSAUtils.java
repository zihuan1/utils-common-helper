
package com.zihuan.utils.cmhlibrary.data;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAUtils {
    private static RSAPublicKey publicKey = null;
    private static final int MAX_ENCRYPT_BLOCK = 117;

    public RSAUtils() {
    }

    public static void loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey, 0);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static String encryptWithRSA(String plainData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        } else {
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, publicKey);
            byte[] data = plainData.getBytes("utf-8");
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;

            for(int i = 0; inputLen - offSet > 0; offSet = i * 117) {
                byte[] cache;
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(data, offSet, 117);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                ++i;
            }

            byte[] encryptedData = out.toByteArray();
            out.close();
            return Base64.encodeToString(encryptedData, 0);
        }
    }

    public static String decryptWithRSA(String encryedData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("decrypt PublicKey is null !");
        } else {
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(2, publicKey);
            byte[] output = cipher.doFinal(Base64.decode(encryedData, 0));
            return new String(output);
        }
    }
}
