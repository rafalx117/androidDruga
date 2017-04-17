package rafal.maksim.druga;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    SimpleCursorAdapter simpleCursorAdapter;
    ListView phoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneList = (ListView) findViewById(android.R.id.list);

        //definniujemy listenet dla kliknięcia na dany element listy
        phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditPhoneActivity.class); //przechodzimy do okna edytowania danego telefonu
                intent.putExtra("id", (int) id); //przekazujemy id telefonu, który chcemy edytować do aktywności EditPhoneActivity
                startActivity(intent);
            }
        });

        phoneList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); //ustawwiamy możliwość zaznaczenia kilku elementów na liście
        phoneList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() //definiujemy listener dla zaznaczenia kilku elementów
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
            {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                //wyświetlamy pasek akcji z przyciskiem do usuwania wielu elementów
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.toolbar, menu);
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
                //jeśli przycisk usuwania został naciśniety - usuwamy zaznaczone elementy
                switch (menuItem.getItemId())
                {
                    case R.id.deletion:
                        deleteSelected();
                        return true;
                }
                return false;

            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode)
            {

            }
        });

//        String query = "SELECT * FROM clients ORDER BY company_name ASC"; // No trailing ';'
//        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        getLoaderManager().initLoader(0, null, this); //inicjalizowanie loadera

        //------- mapping pomiędzy nazwami kolumn w bazie a etykietami widoku -------
        String[] mapFrom = new String[]
                {
                        DBHelper.COLUMN_MAKE,
                        DBHelper.COLUMN_MODEL
                };

        int[] mapTo = new int[]
                {
                        R.id.makeLabel,
                        R.id.modelLabel
                };

        //---------------------------------------------------------------------------

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.db_row, null, mapFrom, mapTo, 0);
        setListAdapter(simpleCursorAdapter); //metoda z ListActivity, usatwia adapter dla listy

        database.close(); //zamykamy połączenie z bazą
    }

    //metoda usuwająca zaznaczone elementy listy
    private void deleteSelected()
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować

        long selected[] = phoneList.getCheckedItemIds(); //tworzymy listę id zaznaczonych elementów
        for (int i = 0; i < selected.length; ++i)
        {
            database.delete(dbHelper.TABLE_NAME, dbHelper.ID + " = " + selected[i], null); //usuwamy pojedynczy telefon z bazy
        }

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection =
                {
                        DBHelper.ID,
                        DBHelper.COLUMN_MAKE,
                        DBHelper.COLUMN_MODEL
                };
        CursorLoader cursorLoader = new CursorLoader(this, Provider.URI_CONTENT, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        simpleCursorAdapter.swapCursor(data); //ustawiamy kursor z nowymi danymi w adapterze kursora, żeby dane mogly pojawić się na liście
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        simpleCursorAdapter.swapCursor(null); //podczas resetu loadera ustawiamy kursor na null, bo dane nie są już dostępne
    }

    public void addPhone(View view)
    {
        //przechodzimy do aktywności AddPhoneActivity
        Intent intent = new Intent(this, AddPhoneActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //wyświetlamy pasek menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        //jesli przycisk dodający telefon został naciśniety - przechodzimy do aktywności AddPhoneActivity
        switch (item.getItemId())
        {
            case R.id.addPhone:
                Intent zamiar = new Intent(MainActivity.this, AddPhoneActivity.class);
                startActivity(zamiar);
        }
        return true;
    }

}
