package fr.bischof.raphael.gothiite.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConnectionFragment extends Fragment implements View.OnClickListener {

    private OnConnectionFragmentEventListener mListener;

    @InjectView(R.id.etLogin) EditText mEtLogin;
    @InjectView(R.id.etPassword) EditText mEtPassword;
    @InjectView(R.id.tvError) TextView mTvError;
    @InjectView(R.id.btnSubmit) Button mBtnSubmit;
    public ConnectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConnectionFragmentEventListener){
            mListener = (OnConnectionFragmentEventListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showInterfaceDependingOnCurrentUser();
        mBtnSubmit.setOnClickListener(this);
    }

    private void showInterfaceDependingOnCurrentUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            mBtnSubmit.setText(getString(R.string.action_log_out));
            mEtPassword.setVisibility(View.GONE);
            mEtLogin.setVisibility(View.GONE);
        } else {
            mBtnSubmit.setText(getString(R.string.action_sign_in));
            mEtPassword.setVisibility(View.VISIBLE);
            mEtLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSubmit){
            if (ParseUser.getCurrentUser()!=null){
                ParseUser.logOut();
                showInterfaceDependingOnCurrentUser();
            }else{
                if (mEtLogin.getText().toString().length()>3&&mEtPassword.getText().toString().length()>3){
                    ParseUser user = new ParseUser();
                    user.setUsername(mEtLogin.getText().toString());
                    user.setPassword(mEtPassword.getText().toString());
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                successfulLogin();
                            } else {
                                int code = e.getCode();
                                if (code==202){
                                    ParseUser.logInInBackground(mEtLogin.getText().toString(), mEtPassword.getText().toString(), new LogInCallback() {
                                        public void done(ParseUser user, ParseException e) {
                                            if (user != null) {
                                                successfulLogin();
                                            } else if (e.getCode() == 101) {
                                                mTvError.setText(getString(R.string.error_sign_in));
                                            }else{
                                                mTvError.setText(getString(R.string.error_connection));
                                            }
                                        }
                                    });
                                }else{
                                    mTvError.setText(getString(R.string.error_connection));
                                }
                            }
                        }
                    });
                }else{
                    mTvError.setText(getString(R.string.error_not_enough_length));
                }
            }
        }
    }

    private void successfulLogin() {
        Toast.makeText(getActivity(),getString(R.string.successfully_logged_in),Toast.LENGTH_SHORT).show();
        if (mListener!=null){
            mListener.onConnectionSuccessful();
        }
    }

    public interface OnConnectionFragmentEventListener {
        void onConnectionSuccessful();
    }
}
