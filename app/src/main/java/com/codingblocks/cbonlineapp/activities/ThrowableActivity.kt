package com.codingblocks.cbonlineapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.isotomillisecond
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CricketChoicePost
import com.codingblocks.onlineapi.models.CricketQuestion
import com.codingblocks.onlineapi.models.CricketQuestionPost
import com.codingblocks.onlineapi.models.MatchPost
import com.codingblocks.onlineapi.models.UserPredictionPost
import com.squareup.picasso.Picasso
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
import kotlinx.android.synthetic.main.activity_throwable.ques1submission
import kotlinx.android.synthetic.main.activity_throwable.ques2Points
import kotlinx.android.synthetic.main.activity_throwable.ques2rad1
import kotlinx.android.synthetic.main.activity_throwable.ques2rad2
import kotlinx.android.synthetic.main.activity_throwable.ques2rad3
import kotlinx.android.synthetic.main.activity_throwable.ques2rad4
import kotlinx.android.synthetic.main.activity_throwable.ques2rad5
import kotlinx.android.synthetic.main.activity_throwable.ques2rad6
import kotlinx.android.synthetic.main.activity_throwable.ques2radGroup
import kotlinx.android.synthetic.main.activity_throwable.ques2submission
import kotlinx.android.synthetic.main.activity_throwable.ques3
import kotlinx.android.synthetic.main.activity_throwable.ques3Points
import kotlinx.android.synthetic.main.activity_throwable.ques3rad1
import kotlinx.android.synthetic.main.activity_throwable.ques3rad2
import kotlinx.android.synthetic.main.activity_throwable.ques3rad3
import kotlinx.android.synthetic.main.activity_throwable.ques3rad4
import kotlinx.android.synthetic.main.activity_throwable.ques3rad5
import kotlinx.android.synthetic.main.activity_throwable.ques3rad6
import kotlinx.android.synthetic.main.activity_throwable.ques3radGroup
import kotlinx.android.synthetic.main.activity_throwable.ques3submission
import kotlinx.android.synthetic.main.activity_throwable.question1
import kotlinx.android.synthetic.main.activity_throwable.question2
import kotlinx.android.synthetic.main.activity_throwable.quiz
import kotlinx.android.synthetic.main.activity_throwable.quiz1ll
import kotlinx.android.synthetic.main.activity_throwable.rootLayout
import kotlinx.android.synthetic.main.activity_throwable.submitBtn
import kotlinx.android.synthetic.main.activity_throwable.submitQuizBtn
import kotlinx.android.synthetic.main.activity_throwable.team1Flag
import kotlinx.android.synthetic.main.activity_throwable.team1Name
import kotlinx.android.synthetic.main.activity_throwable.team1Score
import kotlinx.android.synthetic.main.activity_throwable.team2Flag
import kotlinx.android.synthetic.main.activity_throwable.team2Name
import kotlinx.android.synthetic.main.activity_throwable.team2Score
import kotlinx.android.synthetic.main.activity_throwable.timeTv
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class ThrowableActivity : AppCompatActivity() {

    var timer: CountDownTimer? = null

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
                        team2Name.text = team2?.name
                        if (team1?.logo?.takeLast(3) == "png")
                            Picasso.with(this@ThrowableActivity).load(team1?.logo).into(team1Flag)
                        else
                            team1Flag.loadSvg(team1?.logo ?: "")

                        if (team2?.logo?.takeLast(3) == "png")
                            Picasso.with(this@ThrowableActivity).load(team2?.logo).into(team2Flag)
                        else
                            team2Flag.loadSvg(team2?.logo ?: "")

                        getScore(id, team1?.id, team2?.id)
                        if (predictionEnd?.isotomillisecond() ?: 0 < System.currentTimeMillis()) {
                            predictionOverTv.visibility = View.VISIBLE
                            quiz.visibility = View.GONE
                        } else {
                            quiz.visibility = View.VISIBLE
                            timer = object : CountDownTimer(
                                (predictionEnd?.isotomillisecond()
                                    ?: 0) - System.currentTimeMillis(),
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
                                }

                                override fun onFinish() {
                                }
                            }
                            predictionOverTv.visibility = View.GONE
                        }
                        setSubmitButton(id, cricketCupQuestions)
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
        var choice: String?
        var score: String = "0"
        var correct: String? = null
        var countScore: Int = 0
        Clients.onlineV2JsonApi.getUserPrediction(id)
            .enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (response?.isSuccessful == true) {
                        if (it != null) {
                            if (it.size > 0) {
                                quiz.visibility = View.VISIBLE
                                submitQuizBtn.visibility = View.GONE
                                predictionOverTv.visibility = View.VISIBLE
                                predictionOverTv.text = "Your Predictions for today"
                            }
                        }
                        it?.forEachIndexed { index, userPrediction ->
                            with(cricketCupQuestions?.filter {
                                it.id == userPrediction.cricketCupQuestion?.id
                            }.apply {
                                correct = this?.get(0)?.correctChoiceId
                                score = this?.get(0)?.score ?: "0"
                            }?.get(0)?.cricketCupChoices?.filter {
                                it.id == userPrediction.choice?.id
                            }) {
                                choice = this?.get(0)?.content
                                when (index) {
                                    0 -> {
                                        ques1radGroup.visibility = View.GONE
                                        ques1submission.apply {
                                            visibility = View.VISIBLE
                                            if (correct != null)
                                                if (correct == id) {
                                                    countScore += Integer.parseInt(score)
                                                    text = "Correct $choice"
                                                } else
                                                    text = "Incorrect $choice"
                                            else
                                                text = choice
                                        }
                                    }
                                    1 -> {
                                        ques2radGroup.visibility = View.GONE
                                        ques2submission.apply {
                                            visibility = View.VISIBLE
                                            if (correct != null)
                                                if (correct == id) {
                                                    countScore += Integer.parseInt(score)
                                                    text = "Correct $choice"
                                                } else
                                                    text = "Incorrect $choice"
                                            else
                                                text = choice
                                        }
                                    }
                                    2 -> {
                                        ques3radGroup.visibility = View.GONE
                                        ques3submission.apply {
                                            visibility = View.VISIBLE
                                            if (correct != null)
                                                if (correct == id) {
                                                    countScore += Integer.parseInt(score)
                                                    text = "Correct $choice"
                                                } else
                                                    text = "Incorrect $choice"
                                            else
                                                text = choice
                                        }
                                    }
                                    else -> return@let
                                }
                            }
                        }
                    } else {
                        timer?.start()
                    }
                }
            })
    }

    private fun getScore(id: String, id1: String?, id2: String?) {
        val handler = Handler()

        val r = object : Runnable {
            override fun run() {
                Clients.api.getScore(id).enqueue(retrofitCallback { throwable, response ->
                    response?.body().let {
                        if (response?.isSuccessful == true) {

                            if (it?.getAsJsonObject("score")?.getAsJsonObject("batting")?.get("id")?.asString == id1) {
                                team1Score.text =
                                    it?.getAsJsonObject("score")?.getAsJsonObject("batting")
                                        ?.get("score")?.asString
                                team2Score.text =
                                    it?.getAsJsonObject("score")?.getAsJsonObject("bowling")
                                        ?.get("score")?.asString
                            } else {
                                team2Score.text =
                                    it?.getAsJsonObject("score")?.getAsJsonObject("batting")
                                        ?.get("score")
                                        ?.asString
                                team1Score.text =
                                    it?.getAsJsonObject("score")?.getAsJsonObject("bowling")
                                        ?.get("score")
                                        ?.asString
                            }
                        }
                    }
                })
                handler.postDelayed(this, 10000)
            }
        }

        handler.postDelayed(r, 5000)
    }

    private fun setSubmitButton(
        id: String,
        cricketCupQuestions: ArrayList<CricketQuestion>?
    ) {
        val questionId = arrayOfNulls<String>(3) // returns Array<String?>
        val choiceId = arrayOfNulls<String>(3) // returns Array<String?>
        submitQuizBtn.setOnClickListener {
            if (ques1radGroup.checkedRadioButtonId != -1 && ques2radGroup.checkedRadioButtonId != -1 && ques3radGroup.checkedRadioButtonId != -1) {
                questionId[0] = cricketCupQuestions?.get(0)?.id
                questionId[1] = cricketCupQuestions?.get(1)?.id
                questionId[2] = cricketCupQuestions?.get(2)?.id

                when (ques1radGroup.checkedRadioButtonId) {
                    R.id.ques1rad1 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques1rad2 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques1rad3 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques1rad4 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques1rad5 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques1rad6 -> {
                        choiceId[0] = cricketCupQuestions?.get(0)?.cricketCupChoices?.get(5)?.id
                    }
                }
                when (ques2radGroup.checkedRadioButtonId) {
                    R.id.ques2rad1 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques2rad2 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques2rad3 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques2rad4 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques2rad5 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques2rad6 -> {
                        choiceId[1] = cricketCupQuestions?.get(1)?.cricketCupChoices?.get(5)?.id
                    }
                }
                when (ques3radGroup.checkedRadioButtonId) {
                    R.id.ques3rad1 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(0)?.id
                    }
                    R.id.ques3rad2 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(1)?.id
                    }
                    R.id.ques3rad3 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(2)?.id
                    }
                    R.id.ques3rad4 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(3)?.id
                    }
                    R.id.ques3rad5 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(4)?.id
                    }
                    R.id.ques3rad6 -> {
                        choiceId[2] = cricketCupQuestions?.get(2)?.cricketCupChoices?.get(5)?.id
                    }
                }
                questionId.forEachIndexed { index, s ->
                    val userPrediction = UserPredictionPost(
                        CricketQuestionPost(s ?: ""),
                        CricketChoicePost(choiceId[index] ?: ""),
                        MatchPost((id))
                    )
                    Clients.onlineV2JsonApi.setUserPrediction(userPrediction).enqueue(
                        retrofitCallback { throwable, response ->
                            if (response?.isSuccessful == true) {
                                response.body().let {
                                    fetchUserPrediction(id, cricketCupQuestions)
                                }
                            }
                        })
                }
            } else {
                rootLayout.snackbar(Html.fromHtml("<font color=\"#ffffff\">You mut answer all the questions</font>"))
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

    fun startHome(v: View) {
        startActivity(intentFor<HomeActivity>().singleTop())
    }
}
