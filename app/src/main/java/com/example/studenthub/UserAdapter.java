package com.example.studenthub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String nom = user.getNom() != null ? user.getNom() : "";
        String prenom = user.getPrenom() != null ? user.getPrenom() : "";
        String email = user.getEmail() != null ? user.getEmail() : "";

        holder.tvCardFullName.setText(nom + " " + prenom);
        holder.tvCardEmail.setText(email);

        // Initials
        String initials = "";
        if (!prenom.isEmpty()) {
            initials += prenom.substring(0, 1).toUpperCase();
        }
        if (!nom.isEmpty()) {
            initials += nom.substring(0, 1).toUpperCase();
        }
        if (initials.isEmpty()) {
            initials = "U";
        }
        holder.tvCardInitials.setText(initials);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardInitials, tvCardFullName, tvCardEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardInitials = itemView.findViewById(R.id.tvCardInitials);
            tvCardFullName = itemView.findViewById(R.id.tvCardFullName);
            tvCardEmail = itemView.findViewById(R.id.tvCardEmail);
        }
    }
}
