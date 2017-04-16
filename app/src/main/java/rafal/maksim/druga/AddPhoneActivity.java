package rafal.maksim.druga;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Random;

public class AddPhoneActivity extends AppCompatActivity
{

    EditText makeEditText ;
    EditText modelEditText;
    EditText websiteEditText;
    Switch autogenerateWebsiteSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        makeEditText = ((EditText)findViewById(R.id.makeAddLabel));
        modelEditText =  ((EditText)findViewById(R.id.modelAddLabel));
        websiteEditText = ((EditText)findViewById(R.id.websiteAddLabel));
        autogenerateWebsiteSwitch = ((Switch)findViewById(R.id.autogenerateWebsiteSwitch) );

        autogenerateWebsiteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(((Switch)findViewById(R.id.autogenerateWebsiteSwitch)).isChecked() //metoda wygeneruje stronę internetową, jeśli zaznaczymy switch który był wcześniej wyłączony
                        && !makeEditText.getText().toString().isEmpty()
                        && !modelEditText.getText().toString().isEmpty()) //jeśli zaznaczona jest opccja autogeneracji adresu - generujemy adres
                {
                    websiteEditText.setText(generateWWWAddress());
                }

            }
        });

        makeEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(((Switch)findViewById(R.id.autogenerateWebsiteSwitch)).isChecked())
                    websiteEditText.setText(generateWWWAddress());

            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        modelEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(((Switch)findViewById(R.id.autogenerateWebsiteSwitch)).isChecked())
                    websiteEditText.setText(generateWWWAddress());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

    }

    //metoda generująca link do wyszukiwania danego telefonu w google
    public  String generateWWWAddress()
    {
        String make = ((EditText)findViewById(R.id.makeAddLabel)).getText().toString();
        String model = ((EditText)findViewById(R.id.modelAddLabel)).getText().toString();
        return "https://www.google.pl/search?&q="+make+"+"+ model;
    }

    public void savePhone(View view)
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //metoda getWritableDatabase zwraca obiekt bazy, którą można edytować
        ContentValues values = new ContentValues();

        String make = ((EditText)findViewById(R.id.makeAddLabel)).getText().toString();
        String model = ((EditText)findViewById(R.id.modelAddLabel)).getText().toString();
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
