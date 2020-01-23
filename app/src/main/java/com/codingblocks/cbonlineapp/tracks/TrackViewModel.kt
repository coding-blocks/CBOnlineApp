package com.codingblocks.cbonlineapp.tracks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.CareerTracks
import com.codingblocks.onlineapi.models.Course

/**
 * @author aggarwalpulkit596
 */
class TrackViewModel(private val repo: TracksRepository) : ViewModel() {

    lateinit var id: String
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var track = MutableLiveData<CareerTracks>()
    var courses = MutableLiveData<List<Course>>()

    fun fetchTracks() {
        runIO {
            when (val response = repo.getTracks(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        track.postValue(body())
                        body()?.run {
                            fetchTrackCourses(coursesLinks?.related?.href?.substring(8) ?: "")
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    private fun fetchTrackCourses(id: String) {
        runIO {
            when (val response = repo.getTrackCourses(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        courses.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }
}
