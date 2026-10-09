package com.incentives.prototype.minigameT1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.incentives.prototype.ItemsListMenu

class ItemListSharedVMlottie : ViewModel() {
    var itemsList1: MutableLiveData<ArrayList<ItemsListMenu>> = MutableLiveData<ArrayList<ItemsListMenu>>()
    var itemsList2: MutableLiveData<ArrayList<ItemsListMenuLottie>> = MutableLiveData<ArrayList<ItemsListMenuLottie>>()
}
