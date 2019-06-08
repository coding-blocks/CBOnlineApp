package com.codingblocks.cbonlineapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.isotomillisecond
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CricketQuestion
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_throwable.backBtn
import kotlinx.android.synthetic.main.activity_throwable.earningTv
import kotlinx.android.synthetic.main.activity_throwable.infoll
import kotlinx.android.synthetic.main.activity_throwable.matchTime
import kotlinx.android.synthetic.main.activity_throwable.nameTv
import kotlinx.android.synthetic.main.activity_throwable.predictionOverTv
import kotlinx.android.synthetic.main.activity_throwable.ques1Points
import kotlinx.android.synthetic.main.activity_throwable.ques1rad1
import kotlinx.android.synthetic.main.activity_throwable.ques1rad2
import kotlinx.android.synthetic.main.activity_throwable.ques1rad3
import kotlinx.android.synthetic.main.activity_throwable.ques1rad4
import kotlinx.android.synthetic.main.activity_throwable.ques1rad5
import kotlinx.android.synthetic.main.activity_throwable.ques1rad6
import kotlinx.android.synthetic.main.activity_throwable.ques1radGroup
import kotlinx.android.synthetic.main.activity_throwable.ques2Points
import kotlinx.android.synthetic.main.activity_throwable.ques2rad1
import kotlinx.android.synthetic.main.activity_throwable.ques2rad2
import kotlinx.android.synthetic.main.activity_throwable.ques2rad3
import kotlinx.android.synthetic.main.activity_throwable.ques2rad4
import kotlinx.android.synthetic.main.activity_throwable.ques2rad5
import kotlinx.android.synthetic.main.activity_throwable.ques2rad6
import kotlinx.android.synthetic.main.activity_throwable.ques2radGroup
import kotlinx.android.synthetic.main.activity_throwable.ques3
import kotlinx.android.synthetic.main.activity_throwable.ques3Points
import kotlinx.android.synthetic.main.activity_throwable.ques3rad1
import kotlinx.android.synthetic.main.activity_throwable.ques3rad2
import kotlinx.android.synthetic.main.activity_throwable.ques3rad3
import kotlinx.android.synthetic.main.activity_throwable.ques3rad4
import kotlinx.android.synthetic.main.activity_throwable.ques3rad5
import kotlinx.android.synthetic.main.activity_throwable.ques3rad6
import kotlinx.android.synthetic.main.activity_throwable.ques3radGroup
import kotlinx.android.synthetic.main.activity_throwable.question1
import kotlinx.android.synthetic.main.activity_throwable.question2
import kotlinx.android.synthetic.main.activity_throwable.quiz
import kotlinx.android.synthetic.main.activity_throwable.quiz1ll
import kotlinx.android.synthetic.main.activity_throwable.rootLayout
import kotlinx.android.synthetic.main.activity_throwable.submitBtn
import kotlinx.android.synthetic.main.activity_throwable.submitQuizBtn
import kotlinx.android.synthetic.main.activity_throwable.team1Name
import kotlinx.android.synthetic.main.activity_throwable.team1Score
import kotlinx.android.synthetic.main.activity_throwable.team2Name
import kotlinx.android.synthetic.main.activity_throwable.team2Score
import kotlinx.android.synthetic.main.activity_throwable.timeTv
import org.jetbrains.anko.design.snackbar

class ThrowableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwable)

        backBtn.setOnClickListener {
            finish()
        }

        nameTv.text = "Welcome, " + getPrefs().SP_USER_NAME

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
                        fetchUserPrediction(id, cricketCupQuestions)
                        matchTime.text = formatDate(start ?: "")
                        team1Name.text = team1?.name ?: ""
                        Picasso.with(this@ThrowableActivity).load(team1?.flag)
                            .placeholder(R.drawable.flag_placeholder).fit().into(
                                findViewById<CircleImageView>(R.id.team1Flag)
                            )
                        team2Name.text = team2?.name
                        Picasso.with(this@ThrowableActivity).load(team1?.flag)
                            .placeholder(R.drawable.flag_placeholder).fit().into(
                                findViewById<CircleImageView>(R.id.team1Flag)
                            )
                        if (predictionEnd?.isotomillisecond() ?: 0 < System.currentTimeMillis()) {
                            predictionOverTv.visibility = View.VISIBLE
                            quiz.visibility = View.GONE
                        } else {
                            quiz.visibility = View.VISIBLE
                            val timer = object : CountDownTimer(
                                predictionEnd?.isotomillisecond() ?: 0 - System.currentTimeMillis(),
                                1000
                            ) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val seconds = millisUntilFinished / 1000
                                    val minutes = seconds / 60
                                    val hours = minutes / 60
                                    val days = hours / 24
                                    val time =
                                        "  $days   ${hours % 24}   ${minutes % 60}  ${seconds % 60}"
                                    timeTv.text = time
                                    getScore(id)
                                }

                                override fun onFinish() {
                                }
                            }
                            timer.start()
                            predictionOverTv.visibility = View.GONE
                        }
                        setSubmitButton(cricketCupQuestions)
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

    private fun fetchUserPrediction(
        id: String,
        cricketCupQuestions: ArrayList<CricketQuestion>?
    ) {
        var questionId: String = ""
        var choice: String?
        Clients.onlineV2JsonApi.getUserPrediction(id)
            .enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (response?.isSuccessful!!) {
                        it?.forEachIndexed { index, userPrediction ->
                            with(cricketCupQuestions?.filter {
                                it.id == userPrediction.cricketCupQuestion?.id
                            }.apply {
                                questionId = this?.get(0)?.title ?: ""
                            }?.get(0)?.cricketCupChoices?.filter {
                                it.id == userPrediction.choice?.id
                            }) {
                                choice = this?.get(0)?.content
                                Log.e("TAG ANSWER", choice + "  " + questionId)
                            }
                        }
                    }
                }
            })
    }

    private fun getScore(id: String) {
        Clients.api.getScore(id).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful!!) {
                    team1Score.text =
                        it?.getAsJsonObject("score")?.getAsJsonObject("batting")?.get("score")
                            ?.asString
                    team2Score.text =
                        it?.getAsJsonObject("score")?.getAsJsonObject("bowling")?.get("score")
                            ?.asString
                }
            }
        })
    }

    private fun setSubmitButton(cricketCupQuestions: ArrayList<CricketQuestion>?) {
        var id1: String?
        var id2: String?
        var id3: String?
        submitQuizBtn.setOnClickListener {
            if (ques1radGroup.checkedRadioButtonId != -1 && ques2radGroup.checkedRadioButtonId != -1 && ques3radGroup.checkedRadioButtonId != -1) {
                when (ques1radGroup.checkedRadioButtonId) {
                    R.id.ques1rad1 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques1rad2 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques1rad3 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques1rad4 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques1rad5 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques1rad6 -> {
                        id1 = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(5)?.id
                    }
                }
                when (ques2radGroup.checkedRadioButtonId) {
                    R.id.ques2rad1 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques2rad2 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques2rad3 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques2rad4 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques2rad5 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques2rad6 -> {
                        id2 = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(5)?.id
                    }
                }
                when (ques3radGroup.checkedRadioButtonId) {
                    R.id.ques3rad1 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques3rad2 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques3rad3 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques3rad4 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques3rad5 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques3rad6 -> {
                        id2 = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(5)?.id
                    }
                }
//                var prediction=UserPrediction(Match())
//                prediction.choice?.id =
            } else {
                rootLayout.snackbar("You mut answer all the questions")
            }
        }
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
}
