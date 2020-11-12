package com.stefanos.order.SignUpUserActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.stefanos.order.LoginUserActivity.LoginUserActivity;
import com.stefanos.order.R;
import com.stefanos.order.Users.Users;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpUserActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailUser;
    private EditText usernameUser;
    private EditText passwordUser;
    private EditText passwordConfirmUser;
    private TextInputLayout emailUserTextInputLayout;
    private TextInputLayout usernameUserTextInputLayout;
    private TextInputLayout passwordUserTextInputLayout;
    private TextInputLayout passwordConfirmUserTextInputLayout;
    private RadioButton userButton;
    private RadioButton administratorButton;
    private MaterialButton userSignUpButton;
    private Users user = new Users();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user);

        findViewByIds();
        setUsernameUserGuide();
        setEmailUserGuide();
        setPasswordUserGuide();
        setConfirmationPasswordGuide();
        sendDataFirestore();

    }

    private void findViewByIds() {


        emailUser = findViewById(R.id.userEmailEditText);

        usernameUser = findViewById(R.id.usernameUserEditText);
        passwordUser = findViewById(R.id.userPasswordEditText);
        passwordConfirmUser = findViewById(R.id.userConfirmPasswordEditText);
        userButton = findViewById(R.id.userRadioButton);
        administratorButton = findViewById(R.id.administratorRadioButton);
        emailUserTextInputLayout = findViewById(R.id.usernameXml);
        usernameUserTextInputLayout = findViewById(R.id.usernameTextXml);
        passwordUserTextInputLayout = findViewById(R.id.passwordSignUpTextXml);
        passwordConfirmUserTextInputLayout = findViewById(R.id.confirmPasswordSignUpTextLayout);
        userSignUpButton = findViewById(R.id.signUpUserButtonXml);
        toolbar = findViewById(R.id.toolbarSignUpUser);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sign_up_user_greek_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUsernameUserGuide() {

        usernameUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (usernameUser.getText().toString().isEmpty()) {
                    usernameUserTextInputLayout.setError(getResources().getString(R.string.must_not_be_empty_greek));
                    usernameUserTextInputLayout.setHint(getResources().getString(R.string.enter_your_name_greek));
                } else {
                    usernameUserTextInputLayout.setErrorEnabled(false);
                    usernameUserTextInputLayout.setHint(getResources().getString(R.string.user_name_is_valid_greek));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEmailUserGuide() {

        emailUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailUserTextInputLayout.setErrorEnabled(false);
                if (checkEmailString(emailUser.getText().toString())) {
                    emailUserTextInputLayout.setHint(getResources().getString(R.string.email_name_is_valid_greek));
                } else {
                    emailUserTextInputLayout.setHint(getResources().getString(R.string.enter_your_email_greek));
                    emailUserTextInputLayout.setError(getResources().getString(R.string.invalid_mail_greek));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void setPasswordUserGuide() {

        passwordUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!passwordUser.getText().toString().trim().isEmpty()) {
                    passwordUserTextInputLayout.setErrorEnabled(false);
                    passwordUserTextInputLayout.setHint(getResources().getString(R.string.password_is_valid_greek));

                } else {
                    usernameUserTextInputLayout.setError(getResources().getString(R.string.must_not_be_empty_greek));
                    passwordUserTextInputLayout.setHint(getResources().getString(R.string.enter_password_greek));


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setConfirmationPasswordGuide() {

        passwordConfirmUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isPasswordConfirmed(passwordUser.getText().toString(), passwordConfirmUser.getText().toString())) {
                    passwordConfirmUserTextInputLayout.setErrorEnabled(false);
                    passwordConfirmUserTextInputLayout.setHint(getResources().getString(R.string.password_confirmed_greek));
                } else {
                    passwordConfirmUserTextInputLayout.setError(getResources().getString(R.string.must_confirm_password_greek));
                    passwordConfirmUserTextInputLayout.setHint(getResources().getString(R.string.must_confirm_password_greek));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void sendDataFirestore() {

        userSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean enterNameBoolean = false, enterMailBoolean = false, enterPasswordBoolean = false;
                boolean radioUserButtonChecked = userButton.isChecked();
                boolean radioAdministratorChecked = administratorButton.isChecked();
                user.setUser(radioUserButtonChecked);
                user.setAdministrator(radioAdministratorChecked);

                if (!usernameUser.getText().toString().trim().isEmpty()) {

                    user.setUsername(usernameUser.getText().toString());
                    enterNameBoolean = true;

                }

                if (checkEmailString(emailUser.getText().toString())) {

                    user.setUserEmail(emailUser.getText().toString());
                    enterMailBoolean = true;

                }

                if (!passwordUser.getText().toString().trim().isEmpty() &&
                        isPasswordConfirmed(passwordUser.getText().toString(), passwordConfirmUser.getText().toString())) {

                    user.setUserPassword(passwordUser.getText().toString().trim());
                    enterPasswordBoolean = true;
                }


                if (!checkIfUserAlreadyExists()) {
                    if (enterNameBoolean && enterMailBoolean && enterPasswordBoolean) {


                        String storeName = readPreferences();
                        final Map<String, Object> userData = new HashMap<>();
                        userData.put("userName", user.getUsername());
                        userData.put("userMail", user.getUserEmail());
                        userData.put("userPassword", user.getUserPassword());
                        userData.put("user", user.isUser());
                        userData.put("administrator", user.isAdministrator());
                        CollectionReference particularUser = db.collection("store").document(storeName).collection("user");
                        particularUser.document(user.getUsername()).set(userData);
                        Toast.makeText(SignUpUserActivity.this,"Συγχαρητήρια η εγγραφή ήταν επιτυχής!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpUserActivity.this, LoginUserActivity.class);
                        startActivity(intent);


                    }
                } else {
                    Toast.makeText(SignUpUserActivity.this,"Ο χρήστης υπάρχει ήδη",Toast.LENGTH_LONG).show();
                }
            }


        });

    }


    private boolean checkIfUserAlreadyExists() {

        final boolean[] userExist = {false};
        String storeName = readPreferences();
        CollectionReference particularUser = db.collection("store").document(storeName).collection("user");

        particularUser.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots!=null) {
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        if(documentSnapshot.get("userName").equals(usernameUser.getText().toString().trim())){

                            userExist[0]=true;
                            Log.i("AAA",String.valueOf(userExist[0]));
                        }
                    }
                }
            }
        });
        return userExist[0];
    }

    private boolean checkEmailString(String input) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        boolean emailIsValid = false;
        if (input.matches(emailPattern) && input.length() > 0) {
            emailIsValid = true;
        }

        return emailIsValid;
    }


    private boolean isPasswordConfirmed(String password, String confirmation) {
        boolean passwordConfirmed = false;

        if (password.equals(confirmation)) {
            passwordConfirmed = true;
        }
        return passwordConfirmed;
    }

    private boolean checkPasswordString(String input) {
        String specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
        char currentCharacter;
        boolean numberPresent = false, upperCasePresent = false, lowerCasePresent = false,
                specialCharacterPresent = false, numberOfCharacters = false;

        if (input.length() >= 8) {
            numberOfCharacters = true;
        }

        for (int i = 0; i < input.length(); i++) {
            currentCharacter = input.charAt(i);
            if (Character.isDigit(currentCharacter)) {
                numberPresent = true;
            } else if (Character.isUpperCase(currentCharacter)) {
                upperCasePresent = true;
            } else if (Character.isLowerCase(currentCharacter)) {
                lowerCasePresent = true;
            } else if (specialChars.contains(String.valueOf(currentCharacter))) {
                specialCharacterPresent = true;
            }
        }

        return
                numberOfCharacters && numberPresent && upperCasePresent && lowerCasePresent && specialCharacterPresent;
    }

    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }


}

