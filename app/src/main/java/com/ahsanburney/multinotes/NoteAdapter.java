package com.ahsanburney.multinotes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "EmployeesAdapter";
    private List<Note> employeeList;
    private MainActivity mainAct;

    public NoteAdapter(List<Note> empList, MainActivity ma) {
        this.employeeList = empList;
        mainAct = ma;

    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note employee = employeeList.get(position);
        holder.mdateTime.setText(employee.getMdateTime());
        holder.mtitle.setText(employee.getMtitle());
        holder.mcontent.setText(employee.getMcontent());
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }
}
