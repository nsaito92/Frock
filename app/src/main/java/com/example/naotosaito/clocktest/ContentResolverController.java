package com.example.naotosaito.clocktest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * ContentResolverを使用した処理を行うクラス。
 * Created by nsaito on 2020/08/19.
 */

class ContentResolverController {
    private static final String TAG = ContentResolverController.class.getSimpleName();
    private ContentResolver contentResolver;
    public FileInputStream inputStream = null;

    public ContentResolverController () {
        contentResolver = MyApplication.getContext().getContentResolver();
    }

    /**
     * 渡されたURIにファイルが実在しているかチェックする。
     * @param uri
     */
    public boolean isReallyFile(Uri uri) {
        boolean result = false;

        if (uri == null) {
            return result;
        }

        String string = getCorrectFilePathFromUri(uri);

        if (string == null) {
            return result;
        }

        try {
            inputStream = new FileInputStream(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        }
        result = true;
        return result;
    }

    /**
     * ContentResolverを使用して、URIから正確なファイルパスを取得する。
     * @param uri ファイルパスを取得するURI
     * @return
     */
    private String getCorrectFilePathFromUri(Uri uri) {
        Log.d(TAG, "getFilePathFromUri");
        Log.d(TAG, "uri = " + uri);

        String filepath = null;

        // URIにより、取得方法が異なるため処理を分ける。
        if (ClockUtil.PackageNames.COM_ANDROID_EXTERNALSTORAGE_DOCUMENTS.
                equals(uri.getAuthority())) {
            Log.d(TAG, "uri.getAuthority() = " + uri.getAuthority());

            //
            String docID = DocumentsContract.getDocumentId(uri);
            String split[] = docID.split(":");
            String type =  split[0];

            Log.d(TAG, "type = " + type);
            Log.d(TAG, "split[1] = " + split[1]);

            //
            if ("primary".equalsIgnoreCase(type)) {
                Log.d(TAG, "test : 1");
                filepath = Environment.getExternalStorageDirectory() + "/" + split[1];

            } else {
                //
                filepath =  "/stroage/" + type + "/" + split[1];
            }
        }
        Log.d(TAG, "filepath = " + filepath);
        return filepath;
    }

    /**
     * FileDescriptorオブジェクトを返却する。
     * @return
     * @throws IOException
     */
    public FileDescriptor getFileDescriptor() throws IOException {
        return inputStream.getFD();
    }
}
