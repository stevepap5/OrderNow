package com.stefanos.order.SignUpStoreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.stefanos.order.LoginStoreActivity.LoginStoreActivity;
import com.stefanos.order.LoginStoreActivity.StoreInfo;
import com.stefanos.order.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpStoreActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();
    private EditText storeNameEditText;
    private EditText storeEmailEditText;
    private EditText passwordStoreEditText;
    private EditText passwordConfirmStoreEditText;
    private MaterialButton storeSignUpButton;
    private StoreInfo storeInfo;
    private TextInputLayout nameStoreTextLayout;
    private TextInputLayout emailStoreTextLayout;
    private TextInputLayout passwordTextLayout;
    private TextInputLayout confirmPasswordTextLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_store);


        initFirestore();
        findViewsByids();
        setEnterStoreNameGuide();
        setPasswordCorrectionGuide();
        setEnterEmailStoreGuide();
        setConfirmationPasswordGuide();
        sendDataFirestore();


    }

    private void findViewsByids(){

        storeNameEditText = findViewById(R.id.enterStoreNameXml);
        storeEmailEditText =findViewById(R.id.enterEmailStoreXml);
        passwordStoreEditText=findViewById(R.id.enterPasswordStoreXml);
        passwordConfirmStoreEditText =findViewById(R.id.confirmPasswordStoreXml);
        storeSignUpButton=findViewById(R.id.signUpStoreButtonXml);
        nameStoreTextLayout=findViewById(R.id.nameStoreTextXml);
        emailStoreTextLayout=findViewById(R.id.usernameXml);
        passwordTextLayout=findViewById(R.id.passwordStoreTextXml);
        confirmPasswordTextLayout=findViewById(R.id.confirmPasswordSignUpTextLayout);
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }


    private void setEnterStoreNameGuide(){



        storeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               if(storeNameEditText.getText().toString().isEmpty()){
                   nameStoreTextLayout.setError(getResources().getString(R.string.must_not_be_empty));
                   nameStoreTextLayout.setHint(getResources().getString(R.string.enter_store_name));
               }
               else {
                   nameStoreTextLayout.setErrorEnabled(false);
                   nameStoreTextLayout.setHint(getResources().getString(R.string.store_name_is_valid));
               }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEnterEmailStoreGuide(){

        storeEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailStoreTextLayout.setErrorEnabled(false);
                if (checkEmailString(storeEmailEditText.getText().toString())){
                    emailStoreTextLayout.setHint(getResources().getString(R.string.email_name_is_valid));
                }
                else{
                    emailStoreTextLayout.setHint(getResources().getString(R.string.enter_your_email));
                    emailStoreTextLayout.setError(getResources().getString(R.string.invalid_mail));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPasswordCorrectionGuide(){

        passwordStoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence input, int i, int i1, int i2) {
                if (checkPasswordString(passwordStoreEditText.getText().toString())){
                    passwordTextLayout.setErrorEnabled(false);
                    passwordTextLayout.setHint(getResources().getString(R.string.password_is_valid));

                }
                else {
                    passwordTextLayout.setError(getResources().getString(R.string.password_rules));
                    passwordTextLayout.setHint(getResources().getString(R.string.enter_password));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setConfirmationPasswordGuide(){

        passwordConfirmStoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isPasswordConfirmed(passwordStoreEditText.getText().toString(), passwordConfirmStoreEditText.getText().toString())){
                    confirmPasswordTextLayout.setErrorEnabled(false);
                    confirmPasswordTextLayout.setHint(getResources().getString(R.string.password_confirmed));}
                else {
                    confirmPasswordTextLayout.setError(getResources().getString(R.string.must_confirm_password));
                    confirmPasswordTextLayout.setHint(getResources().getString(R.string.must_confirm_password));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean checkEmailString(String input){

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        boolean emailIsValid=false;
        if (input.matches(emailPattern) && input.length() > 0)
        {
           emailIsValid=true;
        }

        return emailIsValid;
    }

    private boolean isPasswordConfirmed(String password,String confirmation){
        boolean passwordConfirmed=false;

        if (password.equals(confirmation)) {
            passwordConfirmed=true;
        }
        return passwordConfirmed;
    }

    private  boolean checkPasswordString(String input) {
        String specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
        char currentCharacter;
        boolean numberPresent = false, upperCasePresent = false,lowerCasePresent = false,
                specialCharacterPresent = false, numberOfCharacters=false;

        if(input.length()>=8){
            numberOfCharacters=true;
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
                numberOfCharacters&&numberPresent && upperCasePresent && lowerCasePresent && specialCharacterPresent;
    }


    private void sendDataFirestore(){

        storeSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                storeInfo=new StoreInfo();
                boolean enterNameBoolean=false,enterMailBoolean=false,enterPasswordBoolean=false;

           if (!storeNameEditText.getText().toString().isEmpty()){

               storeInfo.setNameStore(storeNameEditText.getText().toString());
               enterNameBoolean=true;

           }

            if (checkEmailString(storeEmailEditText.getText().toString())) {

                storeInfo.setEmailStore(storeEmailEditText.getText().toString());
                enterMailBoolean=true;

            }

                if (checkPasswordString(passwordStoreEditText.getText().toString())&&
                        isPasswordConfirmed(passwordStoreEditText.getText().toString(), passwordConfirmStoreEditText.getText().toString())) {

                    storeInfo.setPasswordStore(passwordStoreEditText.getText().toString().trim());
                    enterPasswordBoolean=true;
                }


                if(enterNameBoolean&&enterMailBoolean&&enterPasswordBoolean&&!checkIfStoreAlreadyExists()){

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    final Map<String, Object> storeData = new HashMap<>();
                    storeData.put("storeName",storeInfo.getNameStore());
                    storeData.put("storeEmail",storeInfo.getEmailStore());
                    storeData.put("storePassword",storeInfo.getPasswordStore());
                    CollectionReference store=db.collection("store");
                    store.document(storeInfo.getNameStore()).set(storeData);
                    Intent intent=new Intent(SignUpStoreActivity.this, LoginStoreActivity.class);
                    startActivity(intent);

                }
            }




        });

    }

    private boolean checkIfStoreAlreadyExists(){

        final boolean[] storeExists = {false};

        DocumentReference documentReference=mFirestore.collection("store").document(storeNameEditText.getText().toString());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                        storeExists[0] =true;

                    }

                }
            }
        });
        return storeExists[0];
    }



}
