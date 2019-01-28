package vn.ifactory.rxjavawithretrofitexample.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import vn.ifactory.rxjavawithretrofitexample.AppConfig;
import vn.ifactory.rxjavawithretrofitexample.MainActivity;
import vn.ifactory.rxjavawithretrofitexample.R;
import vn.ifactory.rxjavawithretrofitexample.base.BaseActivity;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.TokenResponse;
import vn.ifactory.rxjavawithretrofitexample.network.model.User;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.IntentConstUtils;

public class LoginActivity extends BaseActivity {

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
        final String userName = edtUserName.getText().toString();
        final String password = edtPassword.getText().toString();

        if (AppUtils.isEmptyString(userName) || AppUtils.isEmptyString(password)) {
            Toast.makeText(this, "Username or password not empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (AppUtils.isAppOnLine(this)) {
            mDispose.add(
                    AppConfig.getApiClient().getToken(userName, password, GRANT_TYPE)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(new Function<TokenResponse, Observable<User>>() {
                                @Override
                                public Observable<User> apply(TokenResponse tokenResponse) throws Exception {
                                    // assign token
                                    AppConfig.getInstance().setToken(tokenResponse.getAccessToken());

                                    return getUserInfoObservable(userName, password);
                                }
                            })
                            .subscribeWith(new DisposableObserver<User>() {
                                @Override
                                public void onNext(User user) {
                                    Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                                    int userId = user.getUserId();
                                    intentMain.putExtra(IntentConstUtils.USER_ID, userId);
                                    startActivity(intentMain);
                                    finish();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            })
            );
        } else {
            Toast.makeText(this, "Network problem. Check again", Toast.LENGTH_SHORT).show();
        }
    }

    private Observable<User> getUserInfoObservable(final String userName, final String password) {
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(final ObservableEmitter<User> emitter) throws Exception {
                AppConfig.getApiClient().getUser(userName, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseHelper<User>>() {
                            @Override
                            public void onSuccess(ResponseHelper<User> userResponse) {
                                if (userResponse.getData() != null) {
                                    if (!emitter.isDisposed()) {
                                        emitter.onNext(userResponse.getData());
                                    }

                                    if (!emitter.isDisposed()) {
                                        emitter.onComplete();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Can not found User info", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
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
