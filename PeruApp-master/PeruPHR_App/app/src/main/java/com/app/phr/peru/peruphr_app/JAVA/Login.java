package com.app.phr.peru.peruphr_app.JAVA;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phr.peru.peruphr_app.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    // preference to preserve user's status for networkless environment

    private SharedPreferences preferences;
    private ProgressDialog progressDoalog;
    private String id;
    private String password;
    private boolean flag = false;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mIDView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Set up the login form.
        mIDView = (AutoCompleteTextView) findViewById(R.id.id);
        populateAutoComplete();
        preferences = getSharedPreferences(PreferencePutter.PREF_FILE_NAME, Activity.MODE_PRIVATE);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mIDSignInButton = (Button) findViewById(R.id.login_button);
        mIDSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_DONE)
                    attemptLogin();
                return true;
            }

        });


        //mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mIDView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        id = mIDView.getText().toString();
        password = mPasswordView.getText().toString();
        if (NetworkUtil.getConnectivityStatusBoolean(this)) {  //check network status
            if (mAuthTask != null) {
                return;
            }

            // Reset errors.
            mIDView.setError(null);
            mPasswordView.setError(null);

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (password.equals("")) {
              //  mPasswordView.setError("this file is required");
                Log.d("check", "pw");
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid ID.
            if (id.equals("")) {
               // mIDView.setError(getString(R.string.error_field_required));
                focusView = mIDView;
                cancel = true;
            }
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();

            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                mAuthTask = new UserLoginTask();
                mAuthTask.execute((Void) null);
                progressDoalog = ProgressDialog.show(this, "", "wait", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (!flag) {
                            }
                            progressDoalog.dismiss();
                            flag = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } else {  //login without Network
            if (preferences.getString(PreferencePutter.PREF_ID, "").equals("")) {
                Toast.makeText(getApplicationContext(),
                        "deberá conectar a la red", Toast.LENGTH_SHORT).show();
                hideKeyboard();
            } else {
                String savedID = preferences.getString(PreferencePutter.PREF_ID, "");
                String savedPW = preferences.getString(PreferencePutter.PREF_PW, "");
                if (!savedID.equals(id)) {
                    Toast.makeText(getApplicationContext(), "Id del desajuste\n\n" +
                            "que comprobar la red", Toast.LENGTH_SHORT).show();
                    hideKeyboard();
                } else {
                    if (savedID.equals(id) && savedPW.equals(password)) {
                        //start to next page
                        Intent myAct1 = new Intent(Login.this, MainTab.class);
                        startActivity(myAct1);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "contraseña diferente.", Toast.LENGTH_SHORT).show();
                        mPasswordView.setText("");
                        mPasswordView.requestFocus();

                    }
                }

            }
        }
    }
    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        Log.d("err", "phr hide keyboard");

    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only ID.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> IDs = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IDs.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private HTTPClient client;
        private String result;
        XmlParser parser;

        UserLoginTask() {
            result = "";
            XmlWriter writer = new XmlWriter();
            client = new HTTPClient();
            client.setDoc(writer.getLoginXml(id, password));
        }

        @Override
        protected String doInBackground(Void... params) {
            // Simulate network access.
            result = client.connect();

            //get keyCD  result = client.getKey();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            flag = true;
            //showProgress(false);
            parser = new XmlParser();

            if (result.equals("connection error")) {

                Toast.makeText(getApplicationContext(),
                        "error de conexión", Toast.LENGTH_SHORT).show();
            } else if (parser.resForLogin(result)) {
                Log.d("check", result);
                String savedID = preferences.getString(PreferencePutter.PREF_ID, "null");
                SharedPreferences.Editor editor = preferences.edit();

                if (!savedID.equals(id) && !savedID.equals("null")) {  //기존의 login 계정이 아닌 새로운 계정으로 로그인 헀다면 기존의 저장한 기록들은 삭제
                    editor.clear().commit();
                }
                editor.putBoolean(PreferencePutter.LOG_IN, true);
                editor.putString(PreferencePutter.PREF_ID, id);
                editor.putString(PreferencePutter.PREF_PW, password);
                editor.putString(PreferencePutter.PREF_KEY, parser.getKey());
                editor.putString(PreferencePutter.PATIENT_NAME, parser.getPName());
                editor.commit();
                Log.d("http get", parser.getKey() + "/" + parser.getPName());
                // server에게 login 인증 후 phr data 요청


                Intent myAct1 = new Intent(Login.this, MainTab.class);
                startActivity(myAct1);
                finish();
                //start next Activity
            } else {
           /*     mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
                Toast.makeText(getApplicationContext(),"contraseña diferente.", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            flag = true;
            //showProgress(false);
        }
    }
}

