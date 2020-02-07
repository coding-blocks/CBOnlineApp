package com.codingblocks.cbonlineapp.profile

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.User

/**
 * @author aggarwalpulkit596
 */
class ProfileViewModel(private val repo: ProfileRepository) : BaseCBViewModel() {

    fun fetchUser(): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        runIO {
            when (val response = repo.fetchUser()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            user.postValue(it)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
        return user
    }
}
