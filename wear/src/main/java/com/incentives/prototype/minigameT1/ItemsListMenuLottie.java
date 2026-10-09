package com.incentives.prototype.minigameT1;

public class ItemsListMenuLottie {
    final private int imageItem1;
    final private String nameItem;

    private final String lottieFile;

    public ItemsListMenuLottie(int _imageItem1, String _nameItem, String _lottieFile) {
        this.imageItem1 = _imageItem1;
        this.nameItem = _nameItem;
        this.lottieFile = _lottieFile;
    }

    public int getImageItem1() {
        return imageItem1;
    }

    public String getLottieFile() {
        return lottieFile;
    }
    public String getNameItem() {
        return nameItem;
    }
}
