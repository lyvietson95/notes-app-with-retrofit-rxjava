package vn.ifactory.rxjavawithretrofitexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import vn.ifactory.rxjavawithretrofitexample.app.Const;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.IntentConstUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.MyDividerItemDecoration;
import vn.ifactory.rxjavawithretrofitexample.utils.RecyclerTouchListener;
import vn.ifactory.rxjavawithretrofitexample.view.NotesAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private NotesAdapter mAdapterNote;
    private CompositeDisposable mDispose = new CompositeDisposable();
    private List<ToDo> toDoList;

    @BindView(R.id.rvNote)
    RecyclerView rvNote;

    @BindView(R.id.coordinator_layout)
    ConstraintLayout coordinatorLayout;

    @BindView(R.id.tvEmptyNote)
    TextView tvEmptyNote;

    @BindView(R.id.fabNote)
    FloatingActionButton fabNote;


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
                //showActionsDialog(position);
            }
        }));
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

        Intent intent = getIntent();
        int userId = intent.getIntExtra(IntentConstUtils.USER_ID, 0);

        fetchAllNotes(userId);
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
                                       tvEmptyNote.setVisibility(View.VISIBLE);
                                       return null;
                                   }else {
                                       tvEmptyNote.setVisibility(View.GONE);
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
                                   Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           })
           );
        } else {
            Toast.makeText(this, "Network problem. Check again", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabNote)
    public void fabNoteClick(View view) {
        showNoteDialog(false, null, -1);
    }

    private void showNoteDialog(boolean b, Object o, int i) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabNote:
                break;
        }
    }
}
