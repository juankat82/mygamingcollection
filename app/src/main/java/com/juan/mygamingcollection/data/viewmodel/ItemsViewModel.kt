package com.juan.mygamingcollection.data.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ItemsViewModel : ViewModel() {

    private var _searchItemViewModel = MutableLiveData<List<Item>>()
    val searchItemViewModel: LiveData<List<Item>>
        get() = _searchItemViewModel
    private var _itemsViewModel = MutableLiveData<List<Item>>()
    val itemsList:LiveData<List<Item>>
        get() = _itemsViewModel
    private val _singleItemViewModel = MutableLiveData<Item>()
    private var authenticationImpl: AuthenticationImpl? = null

    fun setAuth(auth: AuthenticationImpl) {
        authenticationImpl = auth
    }

    fun setItemListViewModel(list: List<Item>) {
            _itemsViewModel.value = list
    }

    fun setSingleItem(item: Item) {
        _singleItemViewModel.value = item
    }

    fun eraseItem(item: Item, context: Context) {
        val index = _itemsViewModel.value?.indexOfFirst {
            it.googleDBId.equals(item.googleDBId)
        } ?: -1

        if (index >= -1) {
            val newList:MutableList<Item> = _itemsViewModel.value!!.toMutableList()
            newList.removeAt(index)
            GlobalScope.launch (Dispatchers.Main){
                _itemsViewModel.value = newList
            }
            Toast.makeText(context, "Item successfully removed", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateItem(item:Item, context: Context) {
        val index = _itemsViewModel.value?.indexOfFirst {
            it.googleDBId.equals(item.googleDBId)
        } ?: -1

        if (index >= -1) {
            val newList = mutableListOf<Item>()
            _itemsViewModel.value?.forEach { myitem ->
                if (myitem.googleDBId.equals(item.googleDBId))
                    newList.add(item)
                else
                    newList.add(myitem)
            }
           GlobalScope.launch (Dispatchers.Main){
               _itemsViewModel.value = newList
           }.invokeOnCompletion {
               Toast.makeText(context, "Item successfully updated", Toast.LENGTH_SHORT).show()
           }
        }
    }

    fun setSearchItemList(searchList: List<Item>) {
        GlobalScope.launch (Dispatchers.Main){
            _searchItemViewModel.value = if (searchList.isNotEmpty()) searchList else listOf()
        }
    }
}