package com.incentives.prototype;

public class ItemsListMenu {
    final private int imageItem1;
    final private String nameItem;
    final private int imageItem2;

    public ItemsListMenu(int _imageItem1, String _nameItem, int _imageItem2) {
        this.imageItem1 = _imageItem1;
        this.nameItem = _nameItem;
        this.imageItem2 = _imageItem2;
    }

    public int getImageItem1() {
        return imageItem1;
    }

    public int getImageItem2() {
        return imageItem2;
    }
    public String getNameItem() {
        return nameItem;
    }
}
