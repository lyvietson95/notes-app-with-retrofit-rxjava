package vn.ifactory.rxjavawithretrofitexample.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.http2.Http2Connection;
import vn.ifactory.rxjavawithretrofitexample.AppConfig;
import vn.ifactory.rxjavawithretrofitexample.MainActivity;
import vn.ifactory.rxjavawithretrofitexample.R;
import vn.ifactory.rxjavawithretrofitexample.app.Const;
import vn.ifactory.rxjavawithretrofitexample.base.BaseActivity;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.TokenResponse;
import vn.ifactory.rxjavawithretrofitexample.network.model.User;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.PrefUtils;

public class LoginActivity extends BaseActivity{

    private static final Logger logger = Logger.getLogger(LoginActivity.class.getSimpleName());
    private final String GRANT_TYPE = "password";

    @BindView(R.id.edtUserName)
    EditText edtUserName;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.tvRegister)
    TextView tvRegister;

    @BindView(R.id.btLogin)
    Button btLogin;

    CompositeDisposable mDispose = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControls();
    }

    @OnClick(R.id.btLogin)
    public void login(View view) {
        String userName = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();

        if (AppUtils.isEmptyString(userName) || AppUtils.isEmptyString(password)) {
            Toast.makeText(this, "Username or password not empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (AppUtils.isAppOnLine(this)) {
            mDispose.add(
                    AppConfig.getApiClient().getToken(userName, password, GRANT_TYPE)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<TokenResponse>() {
                                @Override
                                public void onSuccess(TokenResponse tokenResponse) {
                                    if (tokenResponse != null) {
                                        AppConfig.getInstance().setToken(tokenResponse.getAccessToken());

                                        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intentMain);
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (((HttpException) e).code() == 400) {
                                        Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
            );
        }else {
            Toast.makeText(this, "Network problem. Check again", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.tvRegister)
    public void showRegisterDialog(View view) {
        RegisterFragment registerDialog = RegisterFragment.newInstance();

        registerDialog.show(getSupportFragmentManager(), "register-dialog");
        registerDialog.setRegisterListener(new RegisterFragment.onRegisterListener() {
            @Override
            public void onSuccess(String userName, String password) {
                edtUserName.setText(userName);
                edtPassword.setText(password);
            }
        });
    }

    private void addControls() {
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDispose.clear();
    }

}
