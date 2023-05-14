package com.medicare_service.controller.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare_service.R;
import com.medicare_service.controller.interfaces.CategoryInterface;
import com.medicare_service.databinding.ItemCategoryBinding;
import com.medicare_service.model.Category;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    CategoryInterface listener;
    ArrayList<Category> list;
    List<Category> filterList;
    Activity context;

    public void setData(ArrayList<Category> list) {
        this.list = list;
        filterList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public ArrayList<Category> getData() {
        return list;
    }

    public void setListener(CategoryInterface listener) {
        this.listener = listener;
    }

    public CategoryAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category model = list.get(position);
        holder.binding.title.setText(model.getTitle());
        holder.binding.description.setText(model.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(model));
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind(itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Category item : filterList) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
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