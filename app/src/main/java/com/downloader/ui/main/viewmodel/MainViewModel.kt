package com.downloader.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.downloader.data.repository.MainRepository
import com.downloader.utils.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Created by Rahul Abrol on 4/9/20.
 */
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getUsers() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getImage()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}
