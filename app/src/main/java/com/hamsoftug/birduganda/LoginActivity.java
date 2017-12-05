package com.hamsoftug.birduganda;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private String server_message, server_status, server_status_2, server_u_names, server_u_number;
    private UserLoginTask mAuthTask = null;
    private SQLDatabaseHelper sqlDatabaseHelper = null;

    private HTTPclass httpRequest = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView reg_pop = null;
    private UserRegTask uReg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        reg_pop = (TextView) findViewById(R.id.sign_up_pop_show);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        httpRequest = new HTTPclass();
        sqlDatabaseHelper = new SQLDatabaseHelper(LoginActivity.this);

        reg_pop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register_view();
            }
        });

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
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    void register_view(){
        final Dialog dialog_view = new Dialog(LoginActivity.this);
        dialog_view.setContentView(R.layout.register);
        dialog_view.setTitle("Please fill all fields");
        Button _reg = (Button) dialog_view.findViewById(R.id.reg_button);

        _reg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReg(dialog_view);
            }
        });

        dialog_view.show();
    }

    private void attemptReg(Dialog pop) {
        if (uReg != null) {
            return;
        }

        // Reset errors.

        final EditText fnames = (EditText) pop.findViewById(R.id.reg_full_names);
        EditText _pw = (EditText) pop.findViewById(R.id.reg_password);
        EditText _email = (EditText) pop.findViewById(R.id.reg_email);
        ProgressBar _progress = (ProgressBar) pop.findViewById(R.id.reg_progress);

        // Store values at the time of the login attempt.
        String u_email = _email.getText().toString();
        String u_password = _pw.getText().toString();
        String u_names = fnames.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if(TextUtils.isEmpty(u_password)){
            _pw.setError("Please, enter password");
            focusView = _pw;
            cancel = true;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(u_email)) {
            _email.setError(getString(R.string.error_field_required));
            focusView = _email;
            cancel = true;
        } else if (!isEmailValid(u_email)) {
            _email.setError(getString(R.string.error_invalid_email));
            focusView = _email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            _progress.setVisibility(View.VISIBLE);
            uReg = new UserRegTask(u_email,u_password,u_names,_progress,pop);
            uReg.execute((Void) null);
        }
    }

    boolean doRegister(String user_names,String user_email, String pw){

        HashMap<String,String> params = new HashMap<>();
        params.put("u_name", user_names);
        params.put("u_email", user_email);
        params.put("u_pw", pw);

        JSONObject jsonObj = httpRequest.PostRequest(httpRequest.base_url()+"mob/?bd=do_register",params);
        try {
            JSONArray resultsArray = jsonObj.getJSONArray("result");
            for(int i=0; i<resultsArray.length(); i++){
                JSONObject result = resultsArray.getJSONObject(i);
                server_message = result.getString("message");
                server_status_2 = result.getString("status");
            }
            return resultsArray.length()>0;

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e){

        }
        return false;
    }

    class UserRegTask extends AsyncTask<Void, Void, Boolean>{

        private String u_email,u_password,u_names;
        private ProgressBar progress;
        private Dialog pop;

        public UserRegTask(String u_email, String u_password, String u_names, ProgressBar progress, Dialog pop) {
            this.u_email = u_email;
            this.u_password = u_password;
            this.u_names = u_names;
            this.progress = progress;
            this.pop = pop;
        }

        @Override
        protected void onPreExecute() {
            pop.setCancelable(false);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return doRegister(u_names,u_email,u_password);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progress.setVisibility(View.GONE);
            pop.setCancelable(true);
            uReg = null;

            if(aBoolean){
                if(server_status_2.trim().equals("1")){
                    Snackbar.make(progress,"Registration completed",Snackbar.LENGTH_INDEFINITE).setAction("Login now", new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            pop.dismiss();

                        }
                    }).show();
                } else {
                    Snackbar.make(progress,server_message,Snackbar.LENGTH_INDEFINITE).setAction("Ok", new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                           // pop.dismiss();

                        }
                    }).show();
                }
            } else {
                Snackbar.make(progress,"Registration failed, check your internet connection",Snackbar.LENGTH_INDEFINITE).setAction("Try again", new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        attemptReg(pop);

                    }
                }).show();
            }

        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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

    boolean doLogin(String user_name, String pw){

        HashMap<String,String> params = new HashMap<>();
        params.put("u_name", user_name);
        params.put("u_pw", pw);

        JSONObject jsonObj = httpRequest.PostRequest(httpRequest.base_url()+"mob/?bd=do_login",params);
        try {
            JSONArray resultsArray = jsonObj.getJSONArray("result");
            for(int i=0; i<resultsArray.length(); i++){
                JSONObject result = resultsArray.getJSONObject(i);
                server_message = result.getString("message");
                server_status = result.getString("status");
                server_u_names = result.getString("u_names");
                server_u_number = result.getString("u_number");
            }
            return resultsArray.length()>0;

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e){

        }
        return false;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return doLogin(mEmail,mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                if(server_status.trim().equals("1")) {

                    sqlDatabaseHelper.removeProfile();
                    sqlDatabaseHelper.saveProfile(mEmail,server_u_names,server_u_number,mPassword);

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    mPasswordView.setError(server_message);
                    mPasswordView.requestFocus();
                }

            } else {

                Snackbar.make(mPasswordView,"Check your network connection and try again",Snackbar.LENGTH_INDEFINITE).setAction("Try again?", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                }).show();

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

