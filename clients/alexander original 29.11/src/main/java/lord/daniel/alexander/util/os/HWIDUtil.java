package lord.daniel.alexander.util.os;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;

/**
 * Written by Daniel. on 07/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */

@UtilityClass
public class HWIDUtil {

    // is fine for HWID encryption, but not enough to use in auth
    public String getUnsafeHWID() {
        try{
            String toEncrypt =  System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

}
