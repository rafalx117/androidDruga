package rafal.maksim.druga;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Random;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView phoneList = (ListView)findViewById(android.R.id.list);

        phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditPhoneActivity.class);
                intent.putExtra("id", (int) id);
                startActivity(intent);
            }
        });

        phoneList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        phoneList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
            {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                MenuInflater pomka = actionMode.getMenuInflater();
                pomka.inflate(R.layout.toolbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
            {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode)
            {

            }
        });

//        String query = "SELECT * FROM clients ORDER BY company_name ASC"; // No trailing ';'
//        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        getLoaderManager().initLoader(0,null,this); //inicjalizowanie loadera

        //------- mapping pomiędzy nazwami kolumn w bazie a etykietami widoku -------
        String [] mapFrom = new String[]
                {
                        DBHelper.COLUMN_MAKE,
                        DBHelper.COLUMN_MODEL
                };

        int [] mapTo = new int[]
                {
                        R.id.makeLabel,
                        R.id.modelLabel
                };

        //---------------------------------------------------------------------------

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.db_row, null, mapFrom, mapTo, 0);
        setListAdapter(simpleCursorAdapter); //metoda z ListActivity, usatwia adapter dla listy

        database.close();
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
        simpleCursorAdapter.swapCursor(data); //ustawiamy kursor z nowymi danymi w adapterze kursora, żeby dane mogly pojawić się na liście
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null); //podczas resetu loadera ustawiamy kursor na null, bo dane nie są już dostępne
    }

    public void addPhone(View view)
    {
        Intent intent = new Intent(this, AddPhoneActivity.class);
        startActivity(intent);
    }

}
