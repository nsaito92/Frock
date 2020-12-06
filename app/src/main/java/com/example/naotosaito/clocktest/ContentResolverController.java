package com.example.naotosaito.clocktest;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * ContentResolverを使用した処理を行うクラス。
 * Created by nsaito on 2020/08/19.
 */

class ContentResolverController {
    private static final String TAG = ContentResolverController.class.getSimpleName();
    private ContentResolver contentResolver;
    public FileInputStream inputStream = null;

    public ContentResolverController () {
        Log.d(TAG, "ContentResolverController");

        contentResolver = MyApplication.getContext().getContentResolver();
    }

    /**
     * 渡されたURIにファイルが実在しているかチェックし、存在しなかった場合は、DBから該当のデータを削除する。
     * @param entity
     * @param disableUri
     */
    public boolean isReallyFileAndFileDisable(AlarmSettingEntity entity, boolean disableUri) {
        Log.d(TAG, "isReallyFileAndFileDelete");

        boolean result = false;

        Uri uri = null;
        uri = Uri.parse(entity.getmSoundUri());

        if (isReallyFile(uri)) {
            result = true;
        } else if (disableUri){
            // ファイルを読み取れなかった場合は、DBの該当のURIを無効化する。
            // 削除しないままアラームが鳴動しても、Exceptionをcatchしているためクラッシュはしないが、
            // 無駄なExceptionを発生させないため、無効化する。
            // disableUri フラグが立つのは、原則アラーム鳴動時のみ。
            FrockSettingsHelperController controller = new FrockSettingsHelperController();
            controller.disableUri(entity);

            // 永続的なアクセス許可設定を行なっていたURIをリリースする。
            ContentResolverController resolverController = new ContentResolverController();
            resolverController.releasePersistableUriPermission(Uri.parse(entity.getmSoundUri()));
        }
        return result;
    }

    /**
     * 渡されたURIにファイルが実在しているかチェックする。
     * @param uri
     * @return true : URI読み取りに成功し、 FileInputStream をインスタンスを作成出来た。
     *         false : URI読み取りに失敗し、 FileInputStream の生成に失敗した。
     */
    private boolean isReallyFile(Uri uri) {
        Log.d(TAG, "isReallyFile");

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
        Log.d(TAG, "getFileDescriptor");

        return inputStream.getFD();
    }

    /**
     * URIからファイル名を取得する。
     * @param uri
     * @return
     */
    public String getFileNameFromUri(Uri uri) {
        Log.d(TAG, "getFileNameFromUri");

        if (uri == null) {
            return null;
        }

        // shemeにより取得処理を分ける。
        String sheme = uri.getScheme();
        String filename = null;

        switch (sheme) {
            case "content":
                // Mediaのproviderからカラム「_display_name」のデータを取得する。
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor = null;

                try {
                    // 条件
                    cursor = contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null
                    );

                    if (cursor != null && cursor.moveToFirst()) {
                        filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;

            case "file":
                filename = new File(uri.getPath()).getName();
                break;

            default:
                break;
        }

        return filename;
    }

    /**
     * ファイラーから取得したファイルURIの永続的パーミッションを得る。
     */
    public void takePersistableUriPermission(Uri uri) {
        Log.d(TAG, "takePersistableUriPermission");

        // アクセス権限未付与のURIの場合、権限付与APIを実行。
        if (!isPersistedUriPermissions(uri)) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * 指定されたURIの永続的パーミッションをリリースする。
     */
    public void releasePersistableUriPermission(Uri uri) {
        Log.d(TAG, "releasePersistableUriPermission");

        // アクセス権限済みのURIの場合、権限リリースAPIを実行。
        if (isPersistedUriPermissions(uri)) {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * 渡されたURIが永続的なURI権限が保持されているかチェックする。
     * @return true : 権限付与済み、false : 権限未付与
     */
    public boolean isPersistedUriPermissions(Uri uri) {
        Log.d(TAG, "isPersistedUriPermissions");

        boolean result = false;

        if (uri != null) {
            // 永続的なアクセス許可済みのURI一覧を取得。
            List<UriPermission> uriPermissionList = contentResolver.getPersistedUriPermissions();

            // 対象のURIがすでにアクセス許可済みだった場合は、true。
            if (uriPermissionList != null) {
                for (UriPermission uriPermission : uriPermissionList) {
                    if (String.valueOf(uriPermission.getUri()).equals(String.valueOf(uri))) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
