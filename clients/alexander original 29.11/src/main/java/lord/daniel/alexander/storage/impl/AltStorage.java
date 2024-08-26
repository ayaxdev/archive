package lord.daniel.alexander.storage.impl;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.storage.Storage;
import lord.daniel.alexander.util.crypt.CryptUtil;
import lord.daniel.alexander.util.os.HWIDUtil;

/**
 * Written by Daniel. on 07/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class AltStorage extends Storage<AltStorage.Account> {

    @Getter
    @Setter
    private static AltStorage altStorage;

    private static final CryptUtil encryptor = new CryptUtil();

    @Override
    public void init() {

    }

    public void add(String email, String password) {
        if(email.startsWith("+00=")) {
            email = encryptor.decrypt(email.substring(4), HWIDUtil.getUnsafeHWID());
        }
        if(password.startsWith("+00=")) {
            password = encryptor.decrypt(password.substring(4), HWIDUtil.getUnsafeHWID());
        }
        this.add(new Account(email, password));
    }

    public record Account(String email, String password) {

        public String[] getEncrypted() {
            return new String[] {"+00=" + encryptor.encrypt(email, HWIDUtil.getUnsafeHWID()), "+00=" + encryptor.encrypt(password, HWIDUtil.getUnsafeHWID())};
        }

    }

}
