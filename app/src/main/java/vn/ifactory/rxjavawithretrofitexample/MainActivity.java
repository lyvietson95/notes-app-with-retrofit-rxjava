package vn.ifactory.rxjavawithretrofitexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import vn.ifactory.rxjavawithretrofitexample.app.Const;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.IntentConstUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.MyDividerItemDecoration;
import vn.ifactory.rxjavawithretrofitexample.utils.RecyclerTouchListener;
import vn.ifactory.rxjavawithretrofitexample.view.NotesAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private NotesAdapter mAdapterNote;
    private CompositeDisposable mDispose = new CompositeDisposable();
    private List<ToDo> toDoList;

    @BindView(R.id.rvNote)
    RecyclerView rvNote;

    @BindView(R.id.tvEmptyNote)
    TextView tvEmptyNote;

    @BindView(R.id.fabNote)
    FloatingActionButton fabNote;

    private int mUserId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        rvNote.addOnItemTouchListener(new RecyclerTouchListener(this, rvNote, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, toDoList.get(position), position);
                } else {
                    deleteNote(toDoList.get(position).getTodoId(), position);
                }
            }
        });
        builder.show();
    }



    private void addControls() {
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_title_home));
        setSupportActionBar(toolbar);

        toDoList = new ArrayList<>();
        mAdapterNote = new NotesAdapter(this, toDoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvNote.setLayoutManager(mLayoutManager);
        rvNote.setItemAnimator(new DefaultItemAnimator());
        rvNote.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        rvNote.setAdapter(mAdapterNote);

        // white background notification bar
        //whiteNotificationBar(fabNote);

        Intent intent = getIntent();
        mUserId = intent.getIntExtra(IntentConstUtils.USER_ID, 0);

        fetchAllNotes(mUserId);
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void fetchAllNotes(int userId) {
        if (AppUtils.isAppOnLine(this)) {
           mDispose.add(
                   AppConfig.getApiClient().fetchAllNotes(userId)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .map(new Function<ResponseHelper<List<ToDo>>, List<ToDo>>() {
                               @Override
                               public List<ToDo> apply(ResponseHelper<List<ToDo>> todoResponse) throws Exception {
                                   if (todoResponse.getCode() == Const.RESPONSE_NOT_FOUND) {
                                       toggleEmptyNotes(true);
                                       return null;
                                   }else {
                                       toggleEmptyNotes(false);
                                       List<ToDo> listTodo = todoResponse.getData();
                                       Collections.sort(listTodo, new Comparator<ToDo>() {
                                           @Override
                                           public int compare(ToDo o1, ToDo o2) {
                                               return o2.getTodoId() - o1.getTodoId();
                                           }
                                       });
                                       return listTodo;
                                   }

                               }
                           })
                           .subscribeWith(new DisposableSingleObserver<List<ToDo>>() {
                               @Override
                               public void onSuccess(List<ToDo> toDos) {
                                   toDoList.addAll(toDos);
                                   mAdapterNote.notifyDataSetChanged();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   showError(e);
                               }
                           })
           );
        } else {
            Toast.makeText(this, "Network problem. Check again", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEmptyNotes(boolean isEmpty) {
        tvEmptyNote.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.fabNote)
    public void fabNoteClick(View view) {
        showNoteDialog(false, null, -1);
    }

    private void showNoteDialog(final boolean shouldUpdate, final ToDo todo, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText edtNoteTitle = view.findViewById(R.id.edtNoteName);
        final EditText edtNoteDes = view.findViewById(R.id.edtNoteDes);

        TextView dialogTitle = view.findViewById(R.id.tvTitleNote);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && todo != null) {
            edtNoteTitle.setText(todo.getName());
            edtNoteDes.setText(todo.getDescription());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();



        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (AppUtils.isEmptyString(edtNoteTitle.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                final String noteTitle = edtNoteTitle.getText().toString();
                final String noteDes = edtNoteDes.getText().toString();
                // check if user updating note
                if (shouldUpdate && todo != null) {
                    // update note by it's id
                    updateNote(todo.getTodoId(), noteTitle, noteDes, position);
                } else {
                    // create new note
                    createNote(noteTitle, noteDes, mUserId);
                }
            }
        });
    }

    private void createNote(String noteTitle, String noteDescription, int userId) {
        Toast.makeText(this, "Create Note", Toast.LENGTH_SHORT).show();
        mDispose.add(
                AppConfig.getApiClient().createNote(noteTitle, noteDescription, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<ResponseHelper<ToDo>, ToDo>() {
                            @Override
                            public ToDo apply(ResponseHelper<ToDo> toDoResponseHelper) throws Exception {
                                if (toDoResponseHelper.getCode() == Const.RESPONSE_NOT_FOUND) {
                                    Toast.makeText(MainActivity.this, "Error: " + toDoResponseHelper.getMessage(), Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                                return toDoResponseHelper.getData();
                            }
                        })
                        .subscribeWith(new DisposableSingleObserver<ToDo>() {
                            @Override
                            public void onSuccess(ToDo toDo) {
                                Toast.makeText(MainActivity.this, "New Note created.", Toast.LENGTH_SHORT).show();

                                toDoList.add(0, toDo);
                                mAdapterNote.notifyItemInserted(0);

                                toggleEmptyNotes(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                showError(e);
                            }
                        })
        );
    }

    private void updateNote(int noteId, final String noteTitle, final String noteDescription, final int position) {
        //Toast.makeText(this, "Update Note", Toast.LENGTH_SHORT).show();
        mDispose.add(
                AppConfig.getApiClient().updateNote(noteId, noteTitle, noteDescription)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                ToDo toDo = toDoList.get(position);
                                toDo.setName(noteTitle);
                                toDo.setDescription(noteDescription);

                                mAdapterNote.notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Throwable e) {
                                showError(e);
                            }
                        })
        );
    }

    private void deleteNote(int todoId, final int position) {
        mDispose.add(
                AppConfig.getApiClient().deleteNote(todoId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(MainActivity.this, "Delete success", Toast.LENGTH_SHORT).show();

                                toDoList.remove(position);
                                mAdapterNote.notifyItemRemoved(position);

                                toggleEmptyNotes(toDoList.size() == 0);
                            }

                            @Override
                            public void onError(Throwable e) {
                                showError(e);
                            }
                        })
        );
    }

    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof IOException) {
                message = "No internet connection!";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (AppUtils.isEmptyString(message)) {
            message = "Unknown error occurred! Check LogCat.";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
/*
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDispose.clear();
    }

}
