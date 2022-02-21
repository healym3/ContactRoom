package com.example.contactroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactroom.model.Contact;
import com.example.contactroom.model.ContactViewModel;
import com.google.android.material.snackbar.Snackbar;

public class NewContact extends AppCompatActivity {
    public static final String NAME_REPLY = "name_reply";
    public static final String OCCUPATION_REPLY = "occupation_reply";
    private EditText enterName;
    private EditText enterOccupation;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private ContactViewModel contactViewModel;
    private int contactId = 0;
    private Boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        enterName = findViewById(R.id.enterName);
        enterOccupation = findViewById(R.id.enterOccupation);
        saveButton = findViewById(R.id.saveButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        contactViewModel = new ViewModelProvider.AndroidViewModelFactory(NewContact.this
                .getApplication())
                .create(ContactViewModel.class);


        if (getIntent().hasExtra(MainActivity.CONTACT_ID)){
            isEdit = true;
            contactId = getIntent().getIntExtra(MainActivity.CONTACT_ID, 0);
            contactViewModel.get(contactId).observe(this, contact -> {
                if (contact != null){
                    enterName.setText(contact.getName());
                    enterOccupation.setText(contact.getOccupation());
                }

            });
        }

        //Hide buttons
        if (isEdit){
            saveButton.setVisibility(View.GONE);
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }



        saveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (!TextUtils.isEmpty(enterName.getText())
                    && !TextUtils.isEmpty(enterOccupation.getText())){
                Contact contact = new Contact(enterName.getText().toString().trim(),
                        enterOccupation.getText().toString().trim());
                String name = enterName.getText().toString().trim();
                String occupation = enterOccupation.getText().toString().trim();
                replyIntent.putExtra(NAME_REPLY, name);
                replyIntent.putExtra(OCCUPATION_REPLY, occupation);
                setResult(RESULT_OK, replyIntent);

                //ContactViewModel.insert(contact);

            } else {
                //Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT);
                setResult(RESULT_CANCELED, replyIntent);
            }
            finish();

        });

        //Update button

        updateButton.setOnClickListener(view -> {

            edit(false);

        });

        //Delete button
        deleteButton.setOnClickListener(view -> {
            edit(true);

        });


    }

    private void edit(Boolean isDelete) {
        String name = enterName.getText().toString().trim();
        String occupation = enterOccupation.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(occupation)){
            Snackbar.make(enterName, R.string.empty, Snackbar.LENGTH_SHORT).show();
        } else {
            Contact contact = new Contact();
            contact.setId(contactId);
            contact.setName(name);
            contact.setOccupation(occupation);
            if (isDelete)
                ContactViewModel.delete(contact);
            else
                ContactViewModel.update(contact);
            finish();
        }
    }
}