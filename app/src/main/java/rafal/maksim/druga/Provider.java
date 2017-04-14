package rafal.maksim.druga;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.URI;

/**
 * Created by student on 28.03.17.
 */
public class Provider extends ContentProvider {

    private DBHelper dbHelper;
    private static final String ID = "rafal.maksim.druga.Provider";
    public static final Uri URI_CONTENT = Uri.parse("content://" + ID + "/" + DBHelper.TABLE_NAME);
    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW= 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ID,DBHelper.TABLE_NAME, WHOLE_TABLE);
        uriMatcher.addURI(ID, DBHelper.TABLE_NAME + "/#", SELECTED_ROW);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = null;

        switch(uriType)
        {
            case WHOLE_TABLE:
                cursor = database.query(false, DBHelper.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder,null,null);
                break;
            case SELECTED_ROW:
                cursor = database.query(false,DBHelper.TABLE_NAME,projection, addToSelection(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private String addToSelection(String selection, Uri uri) {
        if(selection!=null && !TextUtils.isEmpty(selection))
        {
            selection += " and " + DBHelper.ID + " = " + uri.getLastPathSegment();
        }
        else
        {
            selection = DBHelper.ID + " = " + uri.getLastPathSegment();
        }
        return selection;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int updatedItemsCount = 0;

        switch(uriType)
        {
            case WHOLE_TABLE:
                updatedItemsCount = database.update(DBHelper.TABLE_NAME,values,selection,selectionArgs);
                break;
            case SELECTED_ROW:
                updatedItemsCount = database.update(DBHelper.TABLE_NAME,values,addToSelection(selection,uri),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);
        return updatedItemsCount;
    }
}


