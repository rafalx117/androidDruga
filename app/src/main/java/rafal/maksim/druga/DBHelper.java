package rafal.maksim.druga;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    private Context context;
    public static final String DB_NAME = "RafalSQL";
    public final static String ID= "_id";
    public static final int DB_VERSION = 1;
    public final static String COLUMN_MAKE = "Make";
    public final static String COLUMN_MODEL = "Model";
    public final static String COLUMN_WWW = "WWW";
    public final static String TABLE_NAME = "Phones";
    public final static String CREATE_DB_QUERY = "CREATE TABLE "+ TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
            + COLUMN_MAKE + " text not null, " + COLUMN_MODEL + " text, " + COLUMN_WWW+" text);";

    public final static String DROP_DB_QUERY = "DROP TABLE " + TABLE_NAME ;

    public DBHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DB_QUERY);
        onCreate(db);
    }

}
