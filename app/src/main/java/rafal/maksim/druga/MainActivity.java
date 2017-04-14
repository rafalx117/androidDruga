package rafal.maksim.druga;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        String query = "SELECT * FROM clients ORDER BY company_name ASC"; // No trailing ';'
//        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        getLoaderManager().initLoader(0,null,this);
        String [] mapFrom = new String[]
                {
                        DBHelper.COLUMN_MAKE,
                        DBHelper.COLUMN_MODEL,
                        DBHelper.COLUMN_WWW
                };

        int [] mapTo = new int[]
                {
                        R.id.makeLabel,
                        R.id.modelLabel,
                        R.id.wwwLabel
                };


        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //wyświetlenie wyników zapytania na które wskazuje cursor
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.db_row, null, mapFrom, mapTo, 0);
        setListAdapter(simpleCursorAdapter);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection =
                {
                        DBHelper.ID,
                        DBHelper.COLUMN_MAKE,
                        DBHelper.COLUMN_MODEL
                };
        CursorLoader cursorLoader = new CursorLoader(this,Provider.URI_CONTENT,projection,null,null,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
