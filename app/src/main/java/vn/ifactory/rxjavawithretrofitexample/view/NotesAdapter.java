package vn.ifactory.rxjavawithretrofitexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ifactory.rxjavawithretrofitexample.R;
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;

/**
 * Created by SonLV on 01/15/2019.
 */


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyHolder>{
    private Context mContext;
    private List<ToDo> todoList;

    public NotesAdapter(Context context, List<ToDo> todoList) {
        this.mContext = context;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_list_row, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ToDo toDo = todoList.get(position);

        holder.tvNote.setText(toDo.getName());

        holder.tvDot.setText(Html.fromHtml("&#8226;"));
        holder.tvDot.setTextColor(getRandomMaterialColor("400"));

        // Formatting and displaying timestamp
        holder.tvTimestamp.setText(formatDate(toDo.getCreateDate()));
    }

    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + typeColor, "array", mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.note)
        TextView tvNote;

        @BindView(R.id.dot)
        TextView tvDot;

        @BindView(R.id.timestamp)
        TextView tvTimestamp;

        public MyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
