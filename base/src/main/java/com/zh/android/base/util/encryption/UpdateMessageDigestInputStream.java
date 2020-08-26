package com.zh.android.base.util.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.encryption <br>
 * <b>Create Date:</b> 2019-08-23  09:37 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
abstract class UpdateMessageDigestInputStream extends InputStream {
    UpdateMessageDigestInputStream() {
    }

    public void updateMessageDigest(MessageDigest messageDigest) throws IOException {
        int data;
        while((data = this.read()) != -1) {
            messageDigest.update((byte)data);
        }

    }

    public void updateMessageDigest(MessageDigest messageDigest, int len) throws IOException {
        int data;
        for(int bytesRead = 0; bytesRead < len && (data = this.read()) != -1; ++bytesRead) {
            messageDigest.update((byte)data);
        }

    }
}