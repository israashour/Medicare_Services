package com.medicare_service.controller.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare_service.R;
import com.medicare_service.controller.interfaces.ContactInterface;
import com.medicare_service.databinding.ItemContactBinding;
import com.medicare_service.model.User;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CategoryViewHolder> implements Filterable {

    ContactInterface listener;
    ArrayList<User> list;
    List<User> filterList;

    public void setData(ArrayList<User> list) {
        this.list = list;
        filterList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void setListener(ContactInterface listener) {
        this.listener = listener;
    }

    public ContactAdapter() {
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        User model = list.get(position);
        String name = model.getFirstName() + " " + model.getFamilyName();
        holder.binding.userName.setText(name);
        holder.binding.phone.setText(model.getPhone());

        holder.itemView.setOnClickListener(v -> listener.onItemClicked(model));
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemContactBinding binding;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContactBinding.bind(itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : filterList) {
                    if (item.getFamilyName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}