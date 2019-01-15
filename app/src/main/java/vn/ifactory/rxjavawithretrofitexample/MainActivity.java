package vn.ifactory.rxjavawithretrofitexample;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;
import vn.ifactory.rxjavawithretrofitexample.network.model.TokenResponse;
import vn.ifactory.rxjavawithretrofitexample.utils.MyDividerItemDecoration;
import vn.ifactory.rxjavawithretrofitexample.utils.RecyclerTouchListener;
import vn.ifactory.rxjavawithretrofitexample.view.NotesAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<ToDo> toDoList;
    private NotesAdapter mAdapterNote;
    private CompositeDisposable mDispose = new CompositeDisposable();

    @BindView(R.id.rvNote)
    RecyclerView rvNote;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

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
    }

    private void addControls() {
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_title_home));
        setSupportActionBar(toolbar);

        mAdapterNote = new NotesAdapter(this, toDoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvNote.setLayoutManager(mLayoutManager);
        rvNote.setItemAnimator(new DefaultItemAnimator());
        rvNote.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        rvNote.setAdapter(mAdapterNote);

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
    @OnClick
    private void fabNoteClick() {
        showNoteDialog(false, null, -1);
    }

    private void showNoteDialog(boolean b, Object o, int i) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabNote:
                break;
        }
    }
}
