package rafal.maksim.druga;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditPhoneActivity extends AppCompatActivity {

    EditText modelEditText, makeEditText, websiteEditText;
    int currentPhoneId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        makeEditText = ((EditText)findViewById(R.id.makeEditLabel));
        modelEditText = ((EditText)findViewById(R.id.modelEditLabel));
        websiteEditText = ((EditText)findViewById(R.id.websiteEditLabel));


        try
        {
            Intent intent = getIntent();
            currentPhoneId = intent.getIntExtra("id", -1); //-1 to wartość domyślna
        }
        catch(Exception ex)
        {
            Toast.makeText(this,"Błąd podczas przekazania wartości z poprzedniego okna!" + ex.getMessage(), Toast.LENGTH_LONG);
        }


        if(-1 == currentPhoneId)
        {
            Toast.makeText(this,"Błąd podczas przekazania wartości z poprzedniego okna!" , Toast.LENGTH_LONG);
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase(); //baza w trybie tylko do odczytu
        Cursor cursor = getContentResolver().query(Provider.URI_CONTENT, new String[]{dbHelper.COLUMN_MAKE, dbHelper.COLUMN_MODEL, dbHelper.COLUMN_WWW}, "_id = " + currentPhoneId, null, null);
        cursor.moveToFirst();
        makeEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MAKE)));
        modelEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MODEL)));
        websiteEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_WWW)));
        database.close();

    }

    public void saveChanges(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        ContentValues values = new ContentValues();

        String make = ((EditText)findViewById(R.id.makeEditLabel)).getText().toString();
        String model = ((EditText)findViewById(R.id.modelEditLabel)).getText().toString();
        String website = ((EditText)findViewById(R.id.websiteEditLabel)).getText().toString();

        values.put(dbHelper.COLUMN_MODEL, model);
        values.put(dbHelper.COLUMN_MAKE,make);
        values.put(dbHelper.COLUMN_WWW, website);
//        Provider provider = new Provider();
//        provider.update(Provider.URI_CONTENT,values,null,null);
        //getContentResolver().update(ContentUris.withAppendedId(Provider.URI_CONTENT, currentPhoneId),values,null,null); //Karol
        database.update(dbHelper.TABLE_NAME, values, dbHelper.ID + " = " + currentPhoneId, null);
        database.close();

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }

    public void openBrowser(View view)
    {
        String url = websiteEditText.getText().toString();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(intent);
    }

    public void deletePhone(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować

        database.delete(dbHelper.TABLE_NAME, dbHelper.ID + " = " + currentPhoneId, null);

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }
}
