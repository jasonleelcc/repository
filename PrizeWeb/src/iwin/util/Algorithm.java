package iwin.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 加解密演算法
 * @author yang
 * @version 1.0.0
 * @updatedate 2013/08/10
 */
public class Algorithm {

	private String key3DES = "86380802UBOT1006592E863AA29334BD65AE1932A821502D9E5673CDE3C713ACFE53E2103CD40ED6BEBB101B484CAE83D537806C6CB611AEE86ED2CA8C97BBE95CF8476066D419E8E833376B850172107844D394016715B2E47E0A6EECB3E83A361FA75FA44693F90D38C6F62029FCD8EA395ED868F9D718293E9C0E63194E87";
	
	/**
	 * 3DES加密
	 * @param String 要加密的文字
	 */
	public String encryptThreeDESECB(String src){
		
		try{
		    DESedeKeySpec dks = new DESedeKeySpec(this.key3DES.getBytes("UTF-8"));
		    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		    SecretKey securekey = keyFactory.generateSecret(dks);
		    Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		    cipher.init(Cipher.ENCRYPT_MODE, securekey);
		    byte[] b = cipher.doFinal(src.getBytes());
		    BASE64Encoder encoder = new BASE64Encoder();
		    return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 3DES解密
	 * @param String 加密後文字
	 */
	public String decryptThreeDESECB(String src){
		
		try{
		    BASE64Decoder decoder = new BASE64Decoder();
		    byte[] bytesrc = decoder.decodeBuffer(src);
		    DESedeKeySpec dks = new DESedeKeySpec(this.key3DES.getBytes("UTF-8"));
		    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		    SecretKey securekey = keyFactory.generateSecret(dks);  
		    Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		    cipher.init(Cipher.DECRYPT_MODE, securekey);
		    byte[] retByte = cipher.doFinal(bytesrc);
		    return new String(retByte);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public String AesDecrypt(String sSrc, String sKey) throws Exception {
    	
        try {
        	
            if (sKey == null) {
                System.out.print("Key不能為null");
                return null;
            }
            
            if (sKey.length() != 16) {
                System.out.print("Key不是16位");
                return null;
            }
            
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
}
