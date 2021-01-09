package com.shrimantee.userInfo;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHashHelper extends ContextWrapper {
    public static final String TAG = AppSignatureHashHelper.class.getSimpleName();

    private static final String HASH_TYPE = "SHA-256";
    public static final int NUM_HASHED_BYTES = 9;
    public static final int NUM_BASE64_CHAR = 11;

    public AppSignatureHashHelper(Context context) {
        super(context);
    }

    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    public ArrayList<String> getAppSignatures() {
        ArrayList<String> appSignaturesHashs = new ArrayList<>();
        Signature[] signatures;

        try {
            // Get all package details
            String packageName = getPackageName();
            PackageManager packageManager = getPackageManager();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                signatures = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures;

            }
             else{
                signatures = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.getApkContentsSigners();

            }
            for (Signature signature : signatures) {
                String hash = hash(packageName, signature.toCharsString());
                if (hash != null) {
                    appSignaturesHashs.add(String.format("%s", hash));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Package not found", e);
        }
        return appSignaturesHashs;
    }

    /**
     * @param packageName
     * @param signature
     */
    private static String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            }
            else
                messageDigest.update(appInfo.getBytes());
            byte[] hashSignature = messageDigest.digest();

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            // encode into Base64
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);

            return base64Hash;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No Such Algorithm Exception", e);
        }
        return null;
    }
}