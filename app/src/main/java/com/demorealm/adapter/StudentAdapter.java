package com.demorealm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demorealm.R;
import com.demorealm.database.model.MyStudent;
import com.demorealm.listener.ItemStudentListener;

import java.util.List;

/**
 * Created by HP on 06/08/2017.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    private List<MyStudent> students;
    private ItemStudentListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public ImageView ic_remove;
        public LinearLayout layout_student;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            ic_remove = (ImageView) view.findViewById(R.id.ic_remove);
            layout_student = (LinearLayout) view.findViewById(R.id.layout_student);
        }
    }


    public StudentAdapter(List<MyStudent> students, ItemStudentListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyStudent student = students.get(position);
        holder.tv_name.setText(student.getName());
        holder.layout_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemStudent(student.getBooks());
            }
        });

        holder.ic_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemoveStudent(student.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
