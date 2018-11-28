package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.fragments.AnnouncementsFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.DoubtsFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_my_course.*
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.*
import kotlin.concurrent.thread


class MyCourseActivity : AppCompatActivity(), AnkoLogger {

    lateinit var attempt_Id: String
    private lateinit var database: AppDatabase
    var writtenToDisk = false
    var writtenToDiskVideo = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("courseName")
        attempt_Id = intent.getStringExtra("attempt_id")
        database = AppDatabase.getInstance(this)

        val runDao = database.courseRunDao()
        val sectionDao = database.setionDao()
        val contentDao = database.contentDao()
        val courseDao = database.courseDao()
        val instructorDao = database.instructorDao()



        runDao.getCourseRun(attempt_Id).observe(this, Observer<CourseRun> {
            info {
                "course$it"
            }
        })
        sectionDao.getCourseSection(attempt_Id).observe(this, Observer<List<CourseSection>> {
            info {
                "sections$it"
            }
        })
        contentDao.getCourseContents(attempt_Id).observe(this, Observer<List<CourseContent>> {
            info {
                "content$it"
            }
        })
        courseDao.getCourse(attempt_Id).observe(this, Observer<Course> {
            info {
                "course$it"
            }
        })
        instructorDao.getInstructors(attempt_Id).observe(this, Observer<List<Instructor>> {
            info {
                "instructor$it"
            }
        })

        setupViewPager()


        Clients.onlineV2PublicClient.enrolledCourseById("JWT " + prefs.SP_JWT_TOKEN_KEY, attempt_Id).enqueue(retrofitcallback { throwable, response ->
            response?.body()?.let {

                val course = it.run?.course?.run {
                    Course(
                            id!!,
                            title!!,
                            subtitle!!,
                            logo!!,
                            summary!!,
                            promoVideo!!,
                            difficulty!!,
                            reviewCount!!,
                            rating!!,
                            slug!!,
                            coverImage!!,
                            attempt_Id,
                            updatedAt!!
                    )
                }

                val run = it.run?.run {
                    CourseRun(
                            id!!,
                            attempt_Id,
                            name!!,
                            description!!,
                            start!!,
                            end!!,
                            price!!,
                            mrp!!,
                            courseId!!,
                            updatedAt!!
                    )
                }

                thread {
                    courseDao.insert(course!!)
                    runDao.insert(run!!)

                    //Course Instructors List
                    for (instructor in it.run?.course!!.instructors!!) {
                        instructorDao.insert(Instructor(instructor.id!!, instructor.name!!,
                                instructor.description!!, instructor.photo!!,
                                instructor.updatedAt!!, attempt_Id, instructor.instructorCourse?.courseId!!))
                    }

                    //Course Sections List
                    for (section in it.run?.sections!!) {
                        sectionDao.insert(CourseSection(section.id!!, section.name!!,
                                section.order!!, section.premium!!, section.status!!,
                                section.run_id!!, attempt_Id, section.updatedAt!!))

                        //Section Contents List
                        val contents: ArrayList<CourseContent> = ArrayList()
                        for (content in section.contents!!) {
                            contents.add(CourseContent(
                                    content.id!!, "UNDONE",
                                    content.title!!, content.duration!!,
                                    content.contentable!!, content.section_content?.order!!,
                                    content.section_content?.sectionId!!, attempt_Id, content.section_content?.updatedAt!!
                            ))
                            if (content.contentable.equals("lecture") && !writtenToDisk) {
                                val url = content.lecture?.video_url?.substring(38, (content.lecture?.video_url?.length!! - 11))
// download lecture index.m3u8,video.key and video.m3u8
                                Clients.apiVideo.getVideoFiles(url!!, "index.m3u8").enqueue(retrofitcallback { index_throwable, index_response ->
                                    index_response?.body()?.let {
                                        writtenToDisk = writeResponseBodyToDisk(it, url, "index.m3u8")
                                        info { "url$url" }
                                        info { "Downloaded file$writtenToDisk" }
                                        Clients.apiVideo.getVideoFiles(url, "video.m3u8").enqueue(retrofitcallback { video_throwable, video_response ->
                                            video_response?.body()?.let {
                                                writtenToDiskVideo = writeResponseBodyToDisk(it, url, "video.m3u8")
                                                if (writtenToDiskVideo) {
                                                    Clients.apiVideo.getVideoFiles(url, "video.key").enqueue(retrofitcallback { key_throwable, key_response ->
                                                        key_response?.body()?.let {
                                                            writtenToDiskVideo = writeResponseBodyToDisk(it, url, "video.key")
                                                            val videoChunks = MediaUtils.getCourseDownloadUrls(url, this@MyCourseActivity)
                                                            videoChunks.forEach { videoName ->
                                                                Clients.apiVideo.getVideoFiles(url, videoName).enqueue(retrofitcallback { throwable, response ->
                                                                    response?.body().let {
                                                                        writeResponseBodyToDisk(it!!, url, videoName)
                                                                    }
                                                                })
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        })


                                    }
                                })
                            }
                        }

                        contentDao.insertAll(contents)
                    }
                }

            }
            info { "error ${throwable?.localizedMessage}" }

        })

    }


    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(OverviewFragment(), "")
        adapter.add(AnnouncementsFragment(), "")
        adapter.add(CourseContentFragment(), "")
        adapter.add(DoubtsFragment(), "")
        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_support)

    }

    private fun writeResponseBodyToDisk(body: ResponseBody, videoUrl: String?, fileName: String): Boolean {
        try {

            val file = getExternalFilesDir(Environment.getDataDirectory().absolutePath)
            val folderFile = File(file, "/$videoUrl")
            val dataFile = File(file, "/$videoUrl/$fileName")
            if (!folderFile.exists()) {
                folderFile.mkdir()
            }
            // todo change the file location/name according to your needs

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(dataFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream!!.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()
                    info { "file download: $fileSizeDownloaded of $fileSize" }
//                    Log.d(FragmentActivity.TAG, "file download: $fileSizeDownloaded of $fileSize")
                }

                outputStream!!.flush()

                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }

                if (outputStream != null) {
                    outputStream!!.close()
                }
            }
        } catch (e: IOException) {
            return false
        }

    }

}
