package vn.ifactory.rxjavawithretrofitexample.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import vn.ifactory.rxjavawithretrofitexample.AppConfig;
import vn.ifactory.rxjavawithretrofitexample.R;
import vn.ifactory.rxjavawithretrofitexample.app.Const;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.User;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;

/**
 * Created by SonLV on 01/15/2019.
 */


public class RegisterFragment extends DialogFragment {

    CompositeDisposable mDispose = new CompositeDisposable();
    private onRegisterListener mListener;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    interface onRegisterListener {
        void onSuccess(String userName, String password);
    }

    public void setRegisterListener(onRegisterListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.register_dialog, null);
        ButterKnife.bind(getActivity(), view);*/
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.register_dialog, null);

        final EditText edtUserName = view.findViewById(R.id.edtUserName);
        final EditText edtPassword = view.findViewById(R.id.edtPassword);
        final EditText edtFullName = view.findViewById(R.id.edtFullName);
        final EditText edtAddress = view.findViewById(R.id.edtAddress);

        builder.setTitle("Register User");
        builder.setView(view);
        builder.setCancelable(false).
                setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                final String userName = edtUserName.getText().toString();
                final String password = edtPassword.getText().toString();
                final String fullName = edtFullName.getText().toString();
                final String address = edtAddress.getText().toString();

                if (AppUtils.isEmptyString(userName) ||
                        AppUtils.isEmptyString(password) ||
                        AppUtils.isEmptyString(fullName)) {
                    Toast.makeText(getActivity(), "You must input info user!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if user call alertDialog dismiss will be occurs error unsubscribed before subscription
                /* else {
                    alertDialog.dismiss();
                }*/
                registerUser(userName, password, fullName, address, alertDialog);

            }
        });

        return alertDialog;
    }

    private void registerUser(final String userName, final String password, String fullName, String address, final AlertDialog alertDialog) {
        if (AppUtils.isAppOnLine(getActivity())) {
            mDispose.add(
                    AppConfig.getApiClient().registerUser(userName, password, fullName, address)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<ResponseHelper<User>>() {
                                @Override
                                public void onSuccess(ResponseHelper<User> userResponseHelper) {
                                    if (userResponseHelper.getCode() == Const.RESPONSE_CREATED) {

                                        if (alertDialog != null) {
                                            alertDialog.dismiss();
                                        }

                                        if (mListener != null) {
                                            mListener.onSuccess(userName, password);
                                        }
                                        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();

                                    } else if (userResponseHelper.getCode() == Const.RESPONSE_EXIST) {
                                        Toast.makeText(getActivity(), "User has exist on system.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            })
            );
        }else {
            Toast.makeText(getActivity(), "Network problem. Check again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDispose.clear();
    }
}
