package com.cmov.railwaysportugalback;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    public static PublicKey generatePublicKey(String string) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(string, Base64.DEFAULT));
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(spec);
    }

    public static boolean verifySignature(String data, PublicKey key, String signature) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initVerify(key);
        signer.update(data.getBytes());

        return (signer.verify(Base64.decode(signature, Base64.DEFAULT)));
    }
}
