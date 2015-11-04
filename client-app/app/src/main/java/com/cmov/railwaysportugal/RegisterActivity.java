package com.cmov.railwaysportugal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

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
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mCCName;
    private EditText mCCNumber;
    private EditText mCCMonth;
    private EditText mCCYear;
    private EditText mCCCVC;

    private RequestQueue queue;
    private JsonObjectRequest  jsObjRequest ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mCCName = (AutoCompleteTextView) findViewById(R.id.ccname);
        mCCNumber = (AutoCompleteTextView) findViewById(R.id.ccnumber);
        mCCMonth = (AutoCompleteTextView) findViewById(R.id.ccmonth);
        mCCYear = (AutoCompleteTextView) findViewById(R.id.ccyear);
        mCCCVC = (AutoCompleteTextView) findViewById(R.id.cccvc);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
        /*
             //cc-cvc 3
            //cc-number 17
            //cc-year 2 > 15
            //cc-month > 1 12
         */
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cccvc = mCCCVC.getText().toString();
        String ccname = mCCName.getText().toString();
        String ccnumber = mCCNumber.getText().toString();
        String ccmonth = mCCMonth.getText().toString();
        String ccyear = mCCYear.getText().toString();

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

        if (TextUtils.isEmpty(ccname)) {
            mCCName.setError(getString(R.string.error_field_required));
            focusView = mCCName;
            cancel = true;
        }

        if (TextUtils.isEmpty(ccnumber)) {
            mCCNumber.setError(getString(R.string.error_field_required));
            focusView = mCCNumber;
            cancel = true;
        } else if (!isCCNumberValid(ccnumber)) {
            mCCNumber.setError(getString(R.string.error_invalid_ccnumber));
            focusView = mCCNumber;
            cancel = true;
        }

        if (TextUtils.isEmpty(cccvc)) {
            mCCCVC.setError(getString(R.string.error_field_required));
            focusView = mCCCVC;
            cancel = true;
        } else if (!isCCCVCValid(cccvc)) {
            mCCCVC.setError(getString(R.string.error_invalid_ccCVC));
            focusView = mCCCVC;
            cancel = true;
        }

        if (TextUtils.isEmpty(ccyear) || TextUtils.isEmpty(ccmonth)) {
            mCCYear.setError(getString(R.string.error_field_required));
            mCCMonth.setError(getString(R.string.error_field_required));
            focusView = mCCYear;
            cancel = true;
        } else if (!isDateValid(ccyear, ccmonth)) {
            mCCYear.setError(getString(R.string.error_invalid_date));
            mCCMonth.setError(getString(R.string.error_invalid_date));
            focusView = mCCYear;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserRegisterTask(email, password, ccname, ccnumber, cccvc, ccyear, ccmonth);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //return password.length() >= 6;
        return true;
    }

    private boolean isCCCVCValid(String number){
        Log.e("OK OK",number.length()+" "+number);
        if(number.length()==3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isCCNumberValid(String number){
        if(number.length()==17)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isDateValid(String year, String month){
        if ( year.length() != 2 || month.length() != 2)
        {
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        if (Integer.parseInt(month) < 1 || Integer.parseInt(month) >12)
        {
            return false;
        }
        if(Integer.parseInt(year)+2000 > gc.YEAR)
        {
            return true;
        }
        else if (Integer.parseInt(year)+2000 == gc.YEAR && Integer.parseInt(month) > gc.MONTH)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll("REGISTER");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mCCName;
        private final String mCCNumber;
        private final String mCCCVC;
        private final String mCCYear;
        private final String mCCMonth;


        UserRegisterTask(String email, String password, String ccname, String ccnumber, String cccvc, String ccyear, String ccmonth) {
            mEmail = email;
            mPassword = password;
            mCCName = ccname;
            mCCNumber = ccnumber;
            mCCCVC = cccvc;
            mCCYear = ccyear;
            mCCMonth = ccmonth;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            // Instantiate the RequestQueue.
            queue = Volley.newRequestQueue(RegisterActivity.this);
            String url ="http://54.186.113.106/register";
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("email",mEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("password",mPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("cc-name",mCCName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("cc-number",mCCNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("cc-cvc",mCCCVC);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("cc-month",mCCMonth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                parameters.put("cc-year",mCCYear);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Request a string response from the provided URL.

            jsObjRequest  = new JsonObjectRequest(Request.Method.POST, url, parameters,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            String token = null;
                            try {
                                Log.e("Resquest", "Cheguei aqui token" + response.get("token").toString());
                                token = response.get("token").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i.putExtra("TOKEN", token);

                            startActivity(i);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                    alertDialog.setTitle("Credentials");
                    alertDialog.setMessage("Error, try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = new Intent(RegisterActivity.this, RegisterActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    alertDialog.show();
                }

            });

            jsObjRequest.setTag("REGISTER");

            // Add the request to the RequestQueue.
            queue.add(jsObjRequest );


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

