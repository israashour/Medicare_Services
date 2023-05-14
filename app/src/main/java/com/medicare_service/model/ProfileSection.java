package com.medicare_service.model;


import com.medicare_service.controller.interfaces.ItemListener;

public class ProfileSection {
    private String title;
    private int icon;
    private ItemListener itemListener;

    public ProfileSection(String title, int icon, ItemListener itemListener) {
        this.title = title;
        this.icon = icon;
        this.itemListener = itemListener;
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
