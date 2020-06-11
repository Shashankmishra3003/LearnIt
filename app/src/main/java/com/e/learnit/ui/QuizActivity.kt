package com.e.learnit.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.Gravity

import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.e.learnit.Common.Common
import com.e.learnit.DBHelper.OnlineDBHelper
import com.e.learnit.Interface.MyCallback
import com.e.learnit.R
import com.e.learnit.adapter.GridAnswerAdapter
import com.e.learnit.adapter.MyFragmentAdapter
import com.e.learnit.adapter.QuestionListHelperAdapter
import com.e.learnit.model.CurrentQuestion
import com.e.learnit.model.Question
import com.e.learnit.ui.fragments.QuestionFragment
import com.e.learnit.utility.Utilities
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.content_quiz.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class QuizActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    lateinit var countDownTimer:CountDownTimer
    var time_play = Common.TOTAL_TIME
    var isAnswerModeView = false
    val CODE_GET_RESULT = 9999
    lateinit var txt_wrong_answer:TextView

    lateinit var  categoryName: String

    lateinit var adapter: GridAnswerAdapter
    lateinit var questionHelperAdapter:QuestionListHelperAdapter

    lateinit var recycler_helper_answer_sheet:RecyclerView

    val u = Utilities()

    internal var gotToQuestionNum:BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.action!!.toString() == Common.KEY_GO_TO_QUESTION)
            {
                val question = intent.getIntExtra(Common.KEY_GO_TO_QUESTION,-1)

                if(question != -1)
                {
                    view_pager.currentItem = question
                }
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
        }

    }

    override fun onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(gotToQuestionNum)
        if (countDownTimer != null)
            countDownTimer!!.cancel()
        if(Common.fragmentList != null)
            Common.fragmentList.clear()
        if(Common.answerSheetList != null)
            Common.answerSheetList.clear()

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        LocalBroadcastManager.getInstance(this).registerReceiver(gotToQuestionNum, IntentFilter(Common.KEY_GO_TO_QUESTION))

        categoryName = intent.getStringExtra("category")

        val toggle = ActionBarDrawerToggle(this,drawer_layout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener (this)

        recycler_helper_answer_sheet = nav_view.getHeaderView(0).findViewById<View>(R.id.answer_sheet) as RecyclerView
        recycler_helper_answer_sheet.setHasFixedSize(true)
        recycler_helper_answer_sheet.layoutManager = GridLayoutManager(this,3)
        //recycler_helper_answer_sheet.addItemDecoration(SpaceItemDecoration(2)


        val btn_done = nav_view.getHeaderView(0).findViewById<View>(R.id.btn_done) as Button

        btn_done.setOnClickListener {
            if(!isAnswerModeView)
            {
                MaterialStyledDialog.Builder(this)
                    .setTitle("Finish ?")
                    .setDescription("DO you really want to Finish ?")
                    .setIcon(R.drawable.ic_mood_white_24dp)
                    .setNegativeText("No")
                    .onNegative{dialog, which -> dialog.dismiss() }
                    .setPositiveText("Yes")
                    .onPositive{dialog, which -> finishGame()
                        drawer_layout.closeDrawer(Gravity.LEFT)}
                    .show()
            }
            else
            {
                finishGame()
            }
        }

        //GetQuestion Based on Category
        if(u.isOnline(this))
            genQuestion(categoryName)
        else
            u.showMaterialDialogNoInternet(this)

    }

    private fun setUpQuestion()
    {
        if(Common.questionList.size > 0)
        {
            txt_timer.visibility = View.VISIBLE
            txt_right_answer.visibility = View.VISIBLE

            countTime()

            //Gen Item for Grid Answer
            genItems()
            grid_answer.setHasFixedSize(true)

            if(Common.questionList.size > 0)
            {
                grid_answer.layoutManager = GridLayoutManager(this,
                    if(Common.questionList.size >5) Common.questionList.size/2 else Common.questionList.size)

                //Adapter for Answer

                adapter = GridAnswerAdapter(this,Common.answerSheetList)

                grid_answer.adapter = adapter


                //Generate Fragment List
                genFragmentList()

                val fragmentAdapter = MyFragmentAdapter(supportFragmentManager,this,Common.fragmentList)
                view_pager.offscreenPageLimit = Common.questionList.size
                view_pager.adapter = fragmentAdapter
                sliding_tabs.setupWithViewPager(view_pager)

                //Event
                view_pager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{

                    val SCROLLING_RIGHT =0
                    val SCROLLING_LEFT = 1
                    val SCROLLING_UNDETERMINED = 2

                    var currentScrollDirection = SCROLLING_UNDETERMINED

                    private val isScrollDirectionUndetermined:Boolean
                        get() = currentScrollDirection == SCROLLING_UNDETERMINED

                    private val isScrollDirectionRight:Boolean
                        get() = currentScrollDirection == SCROLLING_RIGHT

                    private val isScrollDirectionLeft:Boolean
                        get() = currentScrollDirection == SCROLLING_LEFT


                    private fun setScrollingDirection(positionOffset:Float)
                    {
                        if(1-positionOffset >=0.5)
                            this.currentScrollDirection = SCROLLING_RIGHT
                        else if(1-positionOffset <=0.5)
                            this.currentScrollDirection = SCROLLING_LEFT
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        if(state == ViewPager.SCROLL_STATE_IDLE)
                            this.currentScrollDirection = SCROLLING_UNDETERMINED
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                        if(isScrollDirectionUndetermined)
                            setScrollingDirection(positionOffset)
                    }

                    override fun onPageSelected(p0: Int) {

                        val questionfragment:QuestionFragment
                        var position = 0
                        if(p0 > 0)
                        {
                            if(isScrollDirectionRight)
                            {
                                questionfragment = Common.fragmentList[p0 -1]
                                position = p0-1
                            }
                            else if(isScrollDirectionLeft)
                            {
                                questionfragment = Common.fragmentList[p0+1]
                                position = p0 +1
                            }
                            else{
                                questionfragment = Common.fragmentList[p0]
                            }
                        }
                        else
                        {
                            questionfragment = Common.fragmentList[0]
                            position = 0
                        }

                        if(Common.answerSheetList[position].type == Common.ANSWER_TYPE.NO_ANSWER)
                        {
                            //If we want to correct answer, just enable it
                            val question_state = questionfragment.selectedAnswer()
                            Common.answerSheetList[position] = question_state

                            adapter.notifyDataSetChanged()
                            questionHelperAdapter.notifyDataSetChanged()

                            countCorrectAnswer()

                            txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size} ")
                       //     txt_wrong_answer.text = "${Common.wrong_answer_count}"

                            if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
                            {
                                questionfragment.showCorrectAnswer()
                                questionfragment.disableAnswer()
                            }
                        }
                    }

                })

                txt_right_answer.text = "${Common.right_answer_count} / ${Common.questionList.size}"
                questionHelperAdapter = QuestionListHelperAdapter(this,Common.answerSheetList)
                recycler_helper_answer_sheet.adapter = questionHelperAdapter

            }

        }
    }

    private fun genQuestion(categoryName: String) {

        OnlineDBHelper.getInstance(this, FirebaseDatabase.getInstance())
            .readData(object : MyCallback{
                override fun setQuestionList(questionList: List<Question>) {
                    Common.questionList.clear()
                    Common.questionList = questionList as MutableList<Question>

                    if(Common.questionList.size == 0)
                    {
                        MaterialStyledDialog.Builder(this@QuizActivity)
                            .setTitle("Oops")
                            .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                            .setDescription("We don't have any question in this category")
                            .setPositiveText("Ok")
                            .onPositive{dialog,which->
                                dialog.dismiss()
                                finish()
                            }.show()
                    }
                    else
                    {
                        setUpQuestion()
                    }

                }
            }, categoryName.replace(" "," ")
                .replace("/","_"))


    }

    private fun countCorrectAnswer() {
        Common.right_answer_count = 0
        Common.wrong_answer_count =0

        for(item in Common.answerSheetList)
        {
            if(item.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                Common.right_answer_count++
            else if(item.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                Common.wrong_answer_count++
        }
    }

    private fun genFragmentList() {
        for(i in Common.questionList.indices)
        {
            val bundle = Bundle()
            bundle.putInt("index",i)
            val fragment = QuestionFragment()
            fragment.arguments = bundle

            Common.fragmentList.add(fragment)

        }
    }

    private fun genItems() {
        for(i in Common.questionList.indices)
        {
            Common.answerSheetList.add(CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER)) //NO answer for all question
        }
    }

    private fun countTime()
    {
        countDownTimer = object :CountDownTimer(Common.TOTAL_TIME.toLong(),1000)
        {
            override fun onFinish() {
                finishGame()
            }

            override fun onTick(interval: Long) {
                txt_timer.text = (java.lang.String.format("%02d:%02d",
                   TimeUnit.MILLISECONDS.toMinutes(interval),
                    TimeUnit.MILLISECONDS.toSeconds(interval) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval))))
                time_play-=1000

            }

        }.start()
    }

    private fun finishGame() {

        val position = view_pager.currentItem
        val questionfragment = Common.fragmentList[position]

        val question_state = questionfragment.selectedAnswer()
        Common.answerSheetList[position] = question_state

        adapter.notifyDataSetChanged()
        questionHelperAdapter.notifyDataSetChanged()

        countCorrectAnswer()

        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size} ")
//        txt_wrong_answer.text = "${Common.wrong_answer_count}"

        if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
        {
            questionfragment.showCorrectAnswer()
            questionfragment.disableAnswer()
        }

        val intent = Intent(this, ResultActivity::class.java)
        Common.timer = Common.TOTAL_TIME - time_play
        Common.no_answer_count = Common.questionList.size - (Common.right_answer_count + Common.wrong_answer_count)
        Common.data_question = StringBuilder(Gson().toJson(Common.answerSheetList))
        startActivityForResult(intent,CODE_GET_RESULT)

    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            this.finishGame()
            //super.onBackPressed()
            val i = Intent()
            i.putExtra("id",categoryName)
            setResult(Activity.RESULT_OK,i)
            finish()
        }

    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        val item = menu!!.findItem(R.id.menu_wrong_answer)
//        val layout = item.actionView as ConstraintLayout
//        txt_wrong_answer = layout.findViewById(R.id.txt_wrong_answer) as TextView
//        txt_wrong_answer.text = 0.toString()
//
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.menu_done->{
                if(!isAnswerModeView)
                {
                    MaterialStyledDialog.Builder(this)
                        .setTitle("Finish ?")
                        .setDescription("DO you really want to Finish ?")
                        .setIcon(R.drawable.ic_mood_white_24dp)
                        .setNegativeText("No")
                        .onNegative{dialog, which -> dialog.dismiss() }
                        .setPositiveText("Yes")
                        .onPositive{dialog, which -> finishGame()
                            drawer_layout.closeDrawer(Gravity.LEFT)}
                        .show()

                }
                else
                    finishGame()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.quiz, menu)
        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE_GET_RESULT)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                val action = data!!.getStringExtra("action")
                if(action == null || TextUtils.isEmpty(action))
                {
                    val questionIndex = data.getIntExtra(Common.KEY_BACK_FROM_RESULY,-1)
                    view_pager.currentItem = questionIndex

                    isAnswerModeView = false
                    countDownTimer!!.cancel()
//                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility  = View.GONE
                    txt_timer.visibility = View.GONE
                }
                else
                {
                    if(action.equals("doquizagain"))
                    {
                        view_pager.currentItem = 0

                        isAnswerModeView = false
   //                     txt_wrong_answer.visibility = View.VISIBLE
                        txt_right_answer.visibility  = View.VISIBLE
                        txt_timer.visibility = View.VISIBLE

                        for(i in Common.fragmentList.indices)
                        {
                            Common.fragmentList[i].resetQuestion()
                        }

                        for(i in Common.answerSheetList.indices)
                        {
                            Common.answerSheetList[i].type = Common.ANSWER_TYPE.NO_ANSWER
                        }

                        adapter.notifyDataSetChanged()
                        questionHelperAdapter.notifyDataSetChanged()

                        countTime()

                    }
                    else if(action.equals("viewanswer"))
                    {
                        view_pager.currentItem = 0

                        isAnswerModeView = true
                        countDownTimer!!.cancel()
    //                    txt_wrong_answer.visibility = View.GONE
                        txt_right_answer.visibility  = View.GONE
                        txt_timer.visibility = View.GONE

                        for(i in Common.fragmentList.indices)
                        {
                            Common.fragmentList[i].showCorrectAnswer()
                            Common.fragmentList[i].disableAnswer()
                        }
                    }
                }

            }
        }
    }
}
