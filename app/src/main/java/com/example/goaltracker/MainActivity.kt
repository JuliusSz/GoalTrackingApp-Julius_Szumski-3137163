package com.example.goaltracker

import DatabaseManager
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.fonts.Font
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner.current
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.annotation.XmlRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambdaInstance
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.Date


class MainActivity : ComponentActivity(),SensorEventListener {

    private var listOfGoals = ArrayList<Goal>()
    private lateinit var  databaseManager :DatabaseManager
    private lateinit var  database: SQLiteDatabase
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            listOfGoals.add(Goal("Test","",GoalType.CheckMark,null,Date(2023,11,29)))
            Column {
                Text(
                    "Goals",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp)
                )
                LazyColumn {
                    itemsIndexed(
                        listOfGoals
                    ){index, goal ->
                        val currentDate = Date(LocalDate.now().year,LocalDate.now().monthValue,LocalDate.now().dayOfMonth)
                        if(goal.enddate < currentDate || goal.completed){
                            goal.archived = true
                        }
                        if(goal.archived == false){
                            CreateGoalItem(goal = goal)
                        }

                    }

                }

                Spacer(modifier = Modifier.weight(1f))
                AddGoalButton()
                Navigation()
            }
        }
        databaseManager = DatabaseManager(this, "tasks.db",null , version = 1)
        database = databaseManager.writableDatabase
    }

    override fun onResume(){
        super.onResume()
        listOfGoals = retriveData()

    }


    private fun retriveData() : ArrayList<Goal>{
        val table = "tasks"
        val columns: Array<String> = arrayOf("Id","task")
        val cursor: Cursor = database.query(table,columns,null,null,null,null, null )
        val goalList = ArrayList<Goal>()
        cursor.moveToFirst()
        for (i in 0 until cursor.count){
            goalList.add(cursor.getBlob(1) as Goal)
        }
        return goalList
    }
    fun addData(task: Goal){
        val taskToBeAdded : ContentValues = ContentValues().apply{
            put("task", task as ByteArray)
        }
        database.insert("tasks",null, taskToBeAdded)
    }
    private fun updateData(updatedTask: Goal,id: Int){
        val taskToBeUpdated : ContentValues = ContentValues().apply{
            put("task", updatedTask as ByteArray)
        }
        database.update("tasks", taskToBeUpdated, "ID =",arrayOf(id.toString()))
    }

    @Composable
    fun CreateGoalItem(goal: Goal){
        Row(
            modifier = Modifier.border( 2.dp,color = Color.Black,  shape = RoundedCornerShape(4.dp)).padding(PaddingValues(horizontal = 8.dp))
        ) {
            val thisItemGoal = goal
            Text(text = goal.title, Modifier.fillMaxWidth(0.75f))
            Button(onClick = {  }) {
               Icon(Icons.Filled.Check, "Completed" )
            }
        }
    }
    @Preview
    @Composable
    fun AddGoalButton(){
        var current = LocalContext.current
        Row (modifier = Modifier
            .fillMaxWidth()
            .then(Modifier.padding(20.dp)),
            horizontalArrangement = Arrangement.End){
            FloatingActionButton(
                onClick = {
                    var intent = Intent(current,GoalAdderActivity::class.java)
                    startActivity(intent)
                }) {
                Icon(Icons.Filled.Add, "Add a New Goal" )
                
            }
        }
    }

    @Composable
    fun trackStepsForGoal(){
        val context = LocalContext.current
        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepCounter == null){
            Toast.makeText(context, "Step Sensor not Found", Toast.LENGTH_LONG).show()
        }else{
            sensorManager.registerListener(this,stepCounter,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    @Preview
    @Composable
    fun Navigation(){
        var current = LocalContext.current
        Row( modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly){
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                enabled = false,
                onClick = {
                    var intent = Intent(current,MainActivity::class.java)
                    startActivity(intent)
                },
                shape = RectangleShape
            ) {
                Text(text = "Goals")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    var intent = Intent(current,Statistics::class.java)
                    startActivity(intent)
                },
                shape = RectangleShape
            ) {
                Text(text = "History")

            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.getType() == Sensor.TYPE_STEP_COUNTER){

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}

enum class GoalType(val goalType : String) : MutableState<GoalType> {
    CheckMark("checked"),
    stepCounter("stepCounter");

    override var value: GoalType = this
        set(value){
        }

    override fun component1(): GoalType = value

    override fun component2(): (GoalType) -> Unit = { newValue -> value = newValue }

}


class Goal(val title: String, val desc: String, val glType: GoalType, val stepGoal: Int?, val enddate: Date){
    var archived: Boolean = false
    private var sensorManager: SensorManager?= null
    var completed: Boolean = false
    var progress: Int = 0
}