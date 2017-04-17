package rafal.maksim.druga;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPhoneActivity extends AppCompatActivity
{

    EditText modelEditText, makeEditText, websiteEditText;
    int currentPhoneId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        makeEditText = ((EditText) findViewById(R.id.makeEditLabel));
        modelEditText = ((EditText) findViewById(R.id.modelEditLabel));
        websiteEditText = ((EditText) findViewById(R.id.websiteEditLabel));

        //próbujemy odczytać dane przekazane z aktywnoći Main
        try
        {   //jeśli się powiodło - przypisujemy zmiennej currentPhoneId id edytowanego telefonu
            Intent intent = getIntent();
            currentPhoneId = intent.getIntExtra("id", -1); //-1 to wartość domyślna
        } catch (Exception ex)
        {
            //jeśli nie udalo się odczytać wartości - wyświetlamy stosowny komunikat
            Toast.makeText(this, "Błąd podczas przekazania wartości z poprzedniego okna!" + ex.getMessage(), Toast.LENGTH_LONG);
        }

        //dodatkowe zabezpieczenie - w przypadku gdy blok try catch nie złapał wyjątku, ale id telefonu jest niepoprawne. Wyświetlamy wówczas stosowny komunikat
        if (-1 == currentPhoneId)
        {
            Toast.makeText(this, "Błąd podczas przekazania wartości z poprzedniego okna!", Toast.LENGTH_LONG);
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase(); //otwieramy bazę w trybie tylko do odczytu
        Cursor cursor = getContentResolver().query(Provider.URI_CONTENT, new String[]{dbHelper.COLUMN_MAKE, dbHelper.COLUMN_MODEL, dbHelper.COLUMN_WWW}, "_id = " + currentPhoneId, null, null); //pobieramy z bazy dane edytowanego telefonu
        cursor.moveToFirst(); //przechodzimy do pierwszego wiersza zwróconego wyniku

        //--------- wyłuskujemy dane telefonu (model, marka, strona www) z obiektu cursor i wyświetlamy je w odpowiednich polach -----------
        makeEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MAKE)));
        modelEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_MODEL)));
        websiteEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_WWW)));
        //----------------------------------------------------------------------------------------------------------------------------
        database.close();//zamykamy połączenie z bazą

    }

    public void saveChanges(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        ContentValues values = new ContentValues();

        String make = ((EditText) findViewById(R.id.makeEditLabel)).getText().toString();
        String model = ((EditText) findViewById(R.id.modelEditLabel)).getText().toString();
        String website = ((EditText) findViewById(R.id.websiteEditLabel)).getText().toString();

        // ---------------- Prosta walidacja wprowadzonych danych -----------------------------------------
        if (makeEditText.getText().toString().isEmpty() || makeEditText.getText().toString() == null)
        {
            Toast toast = Toast.makeText(this, "Pole 'Producent' nie może być puste!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (modelEditText.getText().toString().isEmpty() || modelEditText.getText().toString() == null)
        {
            Toast toast = Toast.makeText(this, "Pole 'Model' nie może być puste!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if(!validateURL())
            return;

        //--------------------------------------------------------------------------------------------------
        // ------- zapiujemy wprowadzone dane na liście ----------------------
        values.put(dbHelper.COLUMN_MODEL, model);
        values.put(dbHelper.COLUMN_MAKE, make);
        values.put(dbHelper.COLUMN_WWW, website);

//        Provider provider = new Provider();
//        provider.update(Provider.URI_CONTENT,values,null,null); //todo: nie działa
        //getContentResolver().update(ContentUris.withAppendedId(Provider.URI_CONTENT, currentPhoneId),values,null,null); //Karol


        database.update(dbHelper.TABLE_NAME, values, dbHelper.ID + " = " + currentPhoneId, null); //aktuallizujemy dane bieżącego telefonu w bazie danych
        database.close(); //zaykamy połączenie z bazą danych

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }

    //metoda otwierająca okno przeglądarki i wyświetla adres strony danego telefonu
    public void openBrowser(View view)
    {
        if(!validateURL())
            return;

        String url = websiteEditText.getText().toString();
        if (!url.startsWith("http://") && !url.startsWith("https://")) //jeśli adres nie zaczyna sie od frazy http:// lub https:// - dodajemy ją na początku
            url = "http://" + url;

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url)); //tworzymy intent przeglądarki
        startActivity(intent); //w tym miejscu uruchamia sie przeglądarka
    }

    //metoda usuwająca telefon z bazy
    public void deletePhone(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        //getContentResolver().delete(ContentUris.withAppendedId(Provider.URI_CONTENT,(long)currentPhoneId, null, null));
        database.delete(dbHelper.TABLE_NAME, dbHelper.ID + " = " + currentPhoneId, null); //usuwamy z bazy telefon o podanym id
        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }

    public boolean validateURL()
    {
        // --------- sprawdzamy, czy adres strony jest wprowadzony prawidłowo ----------------------------
        if(websiteEditText.getText().toString().isEmpty() || websiteEditText.getText().toString() == null)
        {
            Toast toast = Toast.makeText(this, "Adres strony nie jest uzupełniony!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        else if(!android.util.Patterns.WEB_URL.matcher(websiteEditText.getText().toString()).matches())
        {
            Toast toast = Toast.makeText(this, "Adres jest wprowadzony niepoprawnie!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        else
            return true;
        //-----------------------------------------------------------------------------------------------
    }


//    public boolean validateURL(String url)
//    {
//        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(url);
//        return matcher.matches();
//    }
}
