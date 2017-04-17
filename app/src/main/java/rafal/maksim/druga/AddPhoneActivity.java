package rafal.maksim.druga;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPhoneActivity extends AppCompatActivity
{

    EditText makeEditText;
    EditText modelEditText;
    EditText websiteEditText;
    Switch autogenerateWebsiteSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);

        makeEditText = ((EditText) findViewById(R.id.makeAddLabel));
        modelEditText = ((EditText) findViewById(R.id.modelAddLabel));
        websiteEditText = ((EditText) findViewById(R.id.websiteAddLabel));
        autogenerateWebsiteSwitch = ((Switch) findViewById(R.id.autogenerateWebsiteSwitch));

        /* metoda wygeneruje adres strony internetowej, jeśli włączymy switch który był wcześniej wyłączony oraz jeśli chociaż jedno pole do wprowadzania nie jest puste  */
        autogenerateWebsiteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (((Switch) findViewById(R.id.autogenerateWebsiteSwitch)).isChecked()
                        && !makeEditText.getText().toString().isEmpty()
                        && !modelEditText.getText().toString().isEmpty())
                {
                    websiteEditText.setText(generateWWWAddress());
                }

            }
        });

        // jeśli tekst w labelce makeEditText się zmienia - generujemy adres strony www
        makeEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (((Switch) findViewById(R.id.autogenerateWebsiteSwitch)).isChecked())
                    websiteEditText.setText(generateWWWAddress());

            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        // jeśli tekst w labelce modelEditLabel się zmienia - generujemy adres strony www
        modelEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (((Switch) findViewById(R.id.autogenerateWebsiteSwitch)).isChecked())
                    websiteEditText.setText(generateWWWAddress());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

    }

    //metoda generująca link do wyszukiwania danego telefonu w google
    public String generateWWWAddress()
    {
        String make = ((EditText) findViewById(R.id.makeAddLabel)).getText().toString();
        String model = ((EditText) findViewById(R.id.modelAddLabel)).getText().toString();
        return "https://google.pl/search?&q=" + make + "+" + model;
    }

    public void savePhone(View view)
    {
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
        //--------------------------------------------------------------------------------------------------


        // --------- sprawdzamy, czy adres strony jest wprowadzony prawidłowo ----------------------------
        if(websiteEditText.getText().toString().isEmpty() || websiteEditText.getText().toString() == null)
        {
            Toast toast = Toast.makeText(this, "Adres strony nie jest uzupełniony!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        else if(!android.util.Patterns.WEB_URL.matcher(websiteEditText.getText().toString()).matches())
        {
            Toast toast = Toast.makeText(this, "Adres jest wprowadzony niepoprawnie!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //-----------------------------------------------------------------------------------------------


        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        ContentValues values = new ContentValues(); //tworzymy listę wartości, które chcemy dodać do bazy

        String make = ((EditText) findViewById(R.id.makeAddLabel)).getText().toString();
        String model = ((EditText) findViewById(R.id.modelAddLabel)).getText().toString();
        String website = ((EditText) findViewById(R.id.websiteAddLabel)).getText().toString();

        //dodajemy do listy wartości, które chcemy zapisać w bazie
        values.put(dbHelper.COLUMN_MODEL, model);
        values.put(dbHelper.COLUMN_MAKE, make);
        values.put(dbHelper.COLUMN_WWW, website);
        database.insert(dbHelper.TABLE_NAME, null, values); //zapisujemy w bazie żądane wartości
        database.close(); //zamykamy połączenie z bazą

        Intent intent = new Intent(this, MainActivity.class); //wracamy do strony głównej
        startActivity(intent);
    }



}
