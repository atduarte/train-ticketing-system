package com.cmov.railwaysportugalback.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.util.Base64;

import com.cmov.railwaysportugalback.Security;

public class TicketValidator {
    private PublicKey publicKey;

    public TicketValidator(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public boolean validate(Ticket ticket) {
//        PublicKey pub = get("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAO3VWP2LVsIX81GR7lVyVI2Regsms0Xg\n" +
//                "Eeqk8RVV8Dp9gbhIbrK7YwXuEtaHj/lsE73uXY81ODQARoKrGapnYk0CAwEAAQ==");

        try {
            return Security.verifySignature(ticket.getSignable(), publicKey, ticket.getSignature());
        } catch (Exception e) {
            return false;
        }
    }
}
