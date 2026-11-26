package com.example.studenthub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    public interface OnSubjectActionListener {
        void onView(Subject subject);
        void onEdit(Subject subject);
        void onDelete(Subject subject);
    }

    private final List<Subject> subjectList;
    private final OnSubjectActionListener listener;

    public SubjectAdapter(List<Subject> subjectList, OnSubjectActionListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvSubjectName.setText(subject.getName());
        holder.tvSubjectCode.setText(subject.getCode());

        String teacher = subject.getTeacher();
        if (teacher != null && !teacher.trim().isEmpty()) {
            holder.tvSubjectTeacher.setText(teacher);
        } else {
            holder.tvSubjectTeacher.setText(holder.itemView.getContext().getString(R.string.teacher_not_specified));
        }

        holder.btnViewSubject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onView(subject);
            }
        });

        holder.btnEditSubject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(subject);
            }
        });

        holder.btnDeleteSubject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectCode, tvSubjectTeacher;
        ImageButton btnViewSubject, btnEditSubject, btnDeleteSubject;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvSubjectTeacher = itemView.findViewById(R.id.tvSubjectTeacher);
            btnViewSubject = itemView.findViewById(R.id.btnViewSubject);
            btnEditSubject = itemView.findViewById(R.id.btnEditSubject);
            btnDeleteSubject = itemView.findViewById(R.id.btnDeleteSubject);
        }
    }
}
