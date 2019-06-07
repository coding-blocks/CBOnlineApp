package com.codingblocks.cbonlineapp.activities

//import jdk.nashorn.internal.objects.NativeDate.getTime
//import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.isotomillisecond
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_throwable.backBtn
import kotlinx.android.synthetic.main.activity_throwable.earningTv
import kotlinx.android.synthetic.main.activity_throwable.infoll
import kotlinx.android.synthetic.main.activity_throwable.predictionOverTv
import kotlinx.android.synthetic.main.activity_throwable.ques1Points
import kotlinx.android.synthetic.main.activity_throwable.ques1rad1
import kotlinx.android.synthetic.main.activity_throwable.ques1rad2
import kotlinx.android.synthetic.main.activity_throwable.ques1rad3
import kotlinx.android.synthetic.main.activity_throwable.ques1rad4
import kotlinx.android.synthetic.main.activity_throwable.ques1rad5
import kotlinx.android.synthetic.main.activity_throwable.ques1rad6
import kotlinx.android.synthetic.main.activity_throwable.ques2Points
import kotlinx.android.synthetic.main.activity_throwable.ques2rad1
import kotlinx.android.synthetic.main.activity_throwable.ques2rad2
import kotlinx.android.synthetic.main.activity_throwable.ques2rad3
import kotlinx.android.synthetic.main.activity_throwable.ques2rad4
import kotlinx.android.synthetic.main.activity_throwable.ques2rad5
import kotlinx.android.synthetic.main.activity_throwable.ques2rad6
import kotlinx.android.synthetic.main.activity_throwable.ques3
import kotlinx.android.synthetic.main.activity_throwable.ques3Points
import kotlinx.android.synthetic.main.activity_throwable.ques3rad1
import kotlinx.android.synthetic.main.activity_throwable.ques3rad2
import kotlinx.android.synthetic.main.activity_throwable.ques3rad3
import kotlinx.android.synthetic.main.activity_throwable.ques3rad4
import kotlinx.android.synthetic.main.activity_throwable.ques3rad5
import kotlinx.android.synthetic.main.activity_throwable.ques3rad6
import kotlinx.android.synthetic.main.activity_throwable.question1
import kotlinx.android.synthetic.main.activity_throwable.question2
import kotlinx.android.synthetic.main.activity_throwable.quiz
import kotlinx.android.synthetic.main.activity_throwable.quiz1ll
import kotlinx.android.synthetic.main.activity_throwable.submitBtn
import kotlinx.android.synthetic.main.activity_throwable.team1Name
import kotlinx.android.synthetic.main.activity_throwable.team2Name


class ThrowableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwable)

        backBtn.setOnClickListener {
            finish()
        }

        getEarning()
        getMatch()
        submitBtn.setOnClickListener {
            infoll.animate()
                .translationY(infoll.height.toFloat())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        infoll.visibility = View.GONE
                        quiz1ll.animate()
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    quiz1ll.visibility = View.VISIBLE
                                }

                            })
                    }

                })
        }
    }

    private fun getMatch() {
        Clients.onlineV2JsonApi.getMatch().enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful!!) {
                    it!![0].run {

                        team1Name.text = team1.name
                        Picasso.with(this@ThrowableActivity).load(team1.flag)
                            .placeholder(R.drawable.flag_placeholder).fit().into(
                                findViewById<CircleImageView>(R.id.team1Flag)
                            )
                        team2Name.text = team2.name
                        Picasso.with(this@ThrowableActivity).load(team1.flag)
                            .placeholder(R.drawable.flag_placeholder).fit().into(
                                findViewById<CircleImageView>(R.id.team1Flag)
                            )
                        if (predictionEnd.isotomillisecond() > System.currentTimeMillis()) {
                            predictionOverTv.visibility = View.VISIBLE
                            quiz.visibility = View.GONE
                        } else {
                            quiz.visibility = View.VISIBLE
                            predictionOverTv.visibility = View.GONE
                        }
                        cricketCupQuestions?.forEachIndexed { index, cricketQuestion ->
                            when (index) {
                                0 -> {
                                    question1.text = cricketQuestion.title
                                    ques1Points.text = cricketQuestion.score + " Points"
                                    cricketQuestion.cricketCupChoices?.forEachIndexed { choiceIndex, cricketChoice ->
                                        when (choiceIndex) {
                                            0 -> {
                                                ques1rad1.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            1 -> {
                                                ques1rad2.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            2 -> {
                                                ques1rad3.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            3 -> {
                                                ques1rad4.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            4 -> {
                                                ques1rad5.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            5 -> {
                                                ques1rad6.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                        }
                                    }
                                }

                                1 -> {
                                    question2.text = cricketQuestion.title
                                    ques2Points.text = cricketQuestion.score + " Points"
                                    cricketQuestion.cricketCupChoices?.forEachIndexed { choiceIndex, cricketChoice ->
                                        when (choiceIndex) {
                                            0 -> {
                                                ques2rad1.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            1 -> {
                                                ques2rad2.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            2 -> {
                                                ques2rad3.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            3 -> {
                                                ques2rad4.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            4 -> {
                                                ques2rad5.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            5 -> {
                                                ques2rad6.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                        }
                                    }
                                }

                                2 -> {
                                    ques3.text = cricketQuestion.title
                                    ques3Points.text = cricketQuestion.score + " Points"
                                    cricketQuestion.cricketCupChoices?.forEachIndexed { choiceIndex, cricketChoice ->
                                        when (choiceIndex) {
                                            0 -> {
                                                ques3rad1.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            1 -> {
                                                ques3rad2.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            2 -> {
                                                ques3rad3.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            3 -> {
                                                ques3rad4.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            4 -> {
                                                ques3rad5.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                            5 -> {
                                                ques3rad6.apply {
                                                    visibility = View.VISIBLE
                                                    text = cricketChoice.content
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }


        })
    }

    private fun getEarning() {
        Clients.api.getEarning().enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful!!) {
                    earningTv.text = "Your Winnings: INR " + it?.get("earnings")?.asInt
                }
            }

        })
    }


    override fun onStart() {
        super.onStart()


    }
}
