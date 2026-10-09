package com.incentives.prototype;

public class ItemsList {
    final private int imageItem;
    final private String nameItem;
    final private String descriptionItem;

    public ItemsList(int _imageItem, String _nameItem, String _descriptionItem) {
        this.imageItem = _imageItem;
        this.nameItem = _nameItem;
        this.descriptionItem = _descriptionItem;
    }

    public int getImageItem() {
        return imageItem;
    }

    public String getNameItem() {
        return nameItem;
    }

    public String getDescriptionItem() {
        return descriptionItem;
    }
}
