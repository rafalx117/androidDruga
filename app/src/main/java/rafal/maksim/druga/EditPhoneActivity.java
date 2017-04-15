package rafal.maksim.druga;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        int currentPhoneId = -1;
        String make = null;
        String model = null;
        String website = null;
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
        ((EditText)findViewById(R.id.makeEditLabel)).setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MAKE)));
        ((EditText)findViewById(R.id.modelEditLabel)).setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MODEL)));

        database.close();

    }
}
