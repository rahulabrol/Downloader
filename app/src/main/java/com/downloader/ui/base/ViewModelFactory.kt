//package com.downloader.ui.base
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.downloader.data.api.ApiHelper
//import com.downloader.data.repository.MainRepository
//import com.downloader.ui.main.viewmodel.MainViewModel
//
///**
// * Created by Rahul Abrol on 4/9/20.
// */
//class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return MainViewModel(MainRepository(apiHelper)) as T
//        }
//        throw IllegalArgumentException("Unknown class name")
//    }
//
//}