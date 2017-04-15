package rafal.maksim.druga;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

public class AddPhoneActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);

    }

    public void savePhone(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        ContentValues values = new ContentValues();

        String make = ((EditText)findViewById(R.id.makeAddLabel)).getText().toString();
        String model = ((EditText)findViewById(R.id.makeAddLabel)).getText().toString();
        String website = ((EditText)findViewById(R.id.websiteAddLabel)).getText().toString();

        values.put(dbHelper.COLUMN_MODEL, model);
        values.put(dbHelper.COLUMN_MAKE,make);
        values.put(dbHelper.COLUMN_WWW, website);
        database.insert(dbHelper.TABLE_NAME, null, values);
        database.close();

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }
}
