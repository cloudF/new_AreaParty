package com.dkzy.areaparty.phone;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jenny on 2017/11/28.
 */

public class newAES {
    public static final String logalrithm = "AES/CBC/PKCS5Padding";
    public static final String bm = "utf-8";
//    private static byte[] keyValue = new byte[] {
//            22,-35,-45,25,98,-55,-45,10,35,-45,25,26,-95,25,-35,48
//    };
    private static byte[] iv = new byte[] {
            12,35,25,65,45,87,95,22,15,45,55,66,32,5-4,84,55
    };

//    private static Key keySpec;
    private static IvParameterSpec ivSpec;

    static{
//        keySpec = new SecretKeySpec(keyValue, "AES");
        ivSpec = new IvParameterSpec(iv);
    }
//    public static Key generateKey(String msg) {
//         Key keySpec;
//        keySpec = new SecretKeySpec(keyValue, "AES");
//        return keySpec;
//    }
    /**
     * @Title: encrypt
     * @Description: 加密，使用指定数据源生成密钥，使用用户数据作为算法参数进行AES加密
     * @return String    返回类型
     * @param msg 加密的数据
     * @return
     * @date 2015-9-23 上午9:09:20
     * @throws
     */
    public static String encrypt(String msg,byte[] keyValue) {
        byte[] encryptedData = null;
        try {
            Key keySpec;
            keySpec = new SecretKeySpec(keyValue, "AES");
            Cipher ecipher = Cipher.getInstance(logalrithm);
            ecipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encryptedData = ecipher.doFinal(msg.getBytes(bm));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return asHex(encryptedData);
    }


    /**
     * @Title: decrypt
     * @Description: 解密，对生成的16进制的字符串进行解密
     * @return String    返回类型
     * @param value
     * @return
     * @date 2015-9-23 上午9:10:01
     * @throws
     */
    public static String decrypt(String value,byte[] keyValue) {
        try {
            Key keySpec;
            keySpec = new SecretKeySpec(keyValue, "AES");
            Cipher ecipher = Cipher.getInstance(logalrithm);
            ecipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(ecipher.doFinal(asBin(value)));
        } catch (BadPaddingException e) {
            System.out.println("解密错误："+value);
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println("解密错误："+value);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }


    /**
     * @Title: asHex
     * @Description: 将字节数组转换成16进制字符串
     * @return String    返回类型
     * @param buf
     * @return
     * @date 2015-9-23 上午9:10:25
     * @throws
     */
    private static String asHex(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10){
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }


    /**
     * @Title: asBin
     * @Description: 将16进制字符串转换成字节数组
     * @return byte[]    返回类型
     * @param src
     * @return
     * @date 2015-9-23 上午9:10:52
     * @throws
     */
    private static byte[] asBin(String src) {
        if (src == null){return null;}
        if (src.length() < 1){
            return null;
        }
        byte[] encrypted = new byte[src.length() / 2];
        try {
            for (int i = 0; i < src.length() / 2; i++) {
                int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
                encrypted[i] = (byte) (high * 16 + low);
            }
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return encrypted;
    }

//    public static void main(String[] args) {
//        String userid = "897807300@qq.com";
//        String token = "8aa8690f65f080aee595d8781e7044a7eacda7a86520786db0838136554920b6";
//        System.out.println(encrypt(userid));
//        System.out.println(decrypt(encrypt(userid)));
//    }
}
