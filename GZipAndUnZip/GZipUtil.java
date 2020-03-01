package com.hisense.base.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 使用gzip压缩字符串
 */
public class GZipUtil {


    /**
     * 将字符串压缩后Base64
     *
     * @param primStr 待加压加密函数
     * @return
     */
    public static String gzipString(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = null;
        GZIPOutputStream gout = null;
        try {
            out = new ByteArrayOutputStream();
            gout = new GZIPOutputStream(out);
            gout.write(primStr.getBytes("UTF-8"));
            gout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gout != null) {
                try {
                    gout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String encodeStr = new BASE64Encoder().encode(out.toByteArray());
        //删掉"\r \n \t"
        encodeStr = encodeStr.replaceAll("\r|\n|\t", "");
        return encodeStr;
    }

    /**
     * 将压缩并Base64后的字符串进行解密解压
     *
     * @param compressedStr 待解密解压字符串
     * @return
     */
    public static final String ungzipString(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        GZIPInputStream gin = null;
        String decompressed = null;
        try {
            byte[] compressed = new BASE64Decoder().decodeBuffer(compressedStr);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            gin = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = gin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gin != null) {
                try {
                    gin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return decompressed;
    }
}
