package com.example.goaltracker

import DatabaseManager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.Date


class MainActivity : ComponentActivity(),SensorEventListener {

    private var listOfActiveGoals = mutableListOf<Goal>()   // List of Active Goals that will be Displayed
    private lateinit var  databaseManager :DatabaseManager  // Database Manager
    private lateinit var  database: SQLiteDatabase          //Database
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                manageStepCounter()                         //registers and Tracks Steps for Step counter
                Text(
                    "Goals",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp)
                )
                LazyColumn {                                // Lazy Column to have a scrollable Column
                    items(listOfActiveGoals.filterNot { goal ->             //loops through all tasks and Filters for: the enddate is over, the goal is completed or if the Goal was Archived
                        val currentDate = Date(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
                        val shouldShow = goal.enddate.after(currentDate) || goal.completed || goal.archived
                        if (shouldShow) {                                                                                     // if any of those conditions are true, the Goal gets Archived.
                            goal.archived = true
                            updateData(goal)
                        }
                        shouldShow
                    }) { goal ->
                        CreateGoalItem(goal = goal)                                                                           //if none of the conditions are true, A Row with the Goalitem gets created.
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                AddGoalButton()                                                                                                // creates button that allows to add new Goals
                Navigation()                                                                                                   // creates Navigation
            }
        }
        databaseManager = DatabaseManager(this, "tasks.db",null , version = 1)                              //Initialising Database and Database Manager
        database = databaseManager.writableDatabase
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume(){                                                                                                //Recomposes the Application, Code basically the same as in OnCreate
        super.onResume()
        listOfActiveGoals = retriveActiveData()
        setContent {
            Column {
                manageStepCounter()
                Text(
                    "Goals",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp)
                )
                LazyColumn {
                    items(listOfActiveGoals.filterNot { goal ->
                        val currentDate = Date(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
                        val shouldShow = goal.enddate.after(currentDate) || goal.completed || goal.archived
                        if (shouldShow) {
                            goal.archived = true
                            updateData(goal)
                        }
                        shouldShow
                    }) { goal ->
                        CreateGoalItem(goal = goal)
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
    @Composable
    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageStepCounter(){                                                                //Manages the Stepcounter, Creates a new one when the previous one expired
        val table = "Tasks"
        val columns: Array<String> = arrayOf("Id","title","description","Goaltype","EndDate","Progress","taskGoal","completed","Archived")
        val cursor: Cursor = database.query(table,columns, "Archived = ? AND Goaltype = ?", arrayOf("0","stepCounter"),null,null, null )
        if(cursor.moveToFirst()) {                                                                  // checks if any Active Step counter is in the Database
                var dateString: String = cursor.getString(4)
                var date = Date(dateString)
                val currentDate = Date(LocalDate.now().year-1900, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
                if(date.before(currentDate)){
                    val newDate = Date(LocalDate.now().year-1900, LocalDate.now().monthValue, LocalDate.now().dayOfMonth + 1)
                    addData(Goal(0,"Step Goal","Your Step Goal",GoalType.stepCounter,1000,newDate))         // Creates a new Step counter if only an expired one has been found
                }
        }else{
            val newDate = Date(LocalDate.now().year-1900, LocalDate.now().monthValue, LocalDate.now().dayOfMonth + 1)
            addData(Goal(0,"Step Goal","Your Step Goal: ",GoalType.stepCounter,1000,newDate))               // Creates a new Step counter if no Stepcounter has been found has been found
        }
        trackStepsForGoal()                                                                                                      // tracks the Steps with the Sensor-Event Listener
    }
    private fun retriveActiveData() : ArrayList<Goal>{                                                                          //retrives every None-Archived Goal from the Database
        val table = "Tasks"
        val columns: Array<String> = arrayOf("Id","title","description","Goaltype","EndDate","Progress","taskGoal","completed","Archived")
        val cursor: Cursor = database.query(table,columns, "Archived = ?", arrayOf("0"),null,null, null )
        val goalList = ArrayList<Goal>()
        if(cursor.moveToFirst())
            do {
                var dateString: String = cursor.getString(4)
                var glType: GoalType = GoalType.valueOf(cursor.getString(3))
                var date: Date = Date(dateString)
                var addedGoal = Goal(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    glType,
                    cursor.getInt(6),
                    date
                )
                goalList.add(addedGoal)

            } while (cursor.moveToNext())
        return goalList
    }
    fun addData(task: Goal){                                                                        //Adds new Data to the Database
        val taskToBeAdded : ContentValues = ContentValues().apply{
            put("title",task.title )
            put("description",task.desc)
            put("Goaltype",task.glType.toString())
            put("EndDate",task.enddate.toString())
            put("Progress",task.progress )
            put("taskGoal",task.taskGoal )
            put("completed",task.completed)
            put("Archived",task.archived )
        }
        database.insert("tasks",null, taskToBeAdded)
    }
    private fun updateData(updatedTask: Goal) {                                                     // Updates a given Goal in the Database
        val taskToBeUpdated: ContentValues = ContentValues().apply {
            put("title", updatedTask.title)
            put("description", updatedTask.desc)
            put("Goaltype", updatedTask.glType.toString());
            put("EndDate", updatedTask.enddate.toString())
            put("Progress", updatedTask.progress)
            put("taskGoal", updatedTask.taskGoal)
            put("completed", updatedTask.completed)
            put("Archived", updatedTask.archived)
        }

        val whereClause = "Id = ?"
        val whereArgs = arrayOf(updatedTask.id.toString())

        database.update("Tasks", taskToBeUpdated, whereClause, whereArgs)
    }


    @Composable
    fun CreateGoalItem(goal: Goal){                                                                 // Creates the Visual Goal Item
        Row(
            modifier = Modifier
                .border(2.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if(goal.glType == GoalType.CheckMark){                                                  //Creates an Item for Checkmark Goals
                Text(text = goal.title, Modifier.fillMaxWidth(0.75f))
                Button(onClick = {                                                                  //On Button Press Archives and Completes the Goal, also Updates the Goal in the Database
                    val thisGoal = goal
                    thisGoal.completed = true
                    thisGoal.archived = true
                    updateData(thisGoal)
                }) {
                    Icon(Icons.Filled.Check, "Completed" )
                }
            }else if(goal.glType == GoalType.stepCounter){                                          // Creates an item for an Stepcounter Goal

                Text(text = goal.title +" "+ goal.progress + "/" + goal.taskGoal, Modifier.fillMaxWidth(0.75f))
            }

        }
    }
    @Preview
    @Composable
    fun AddGoalButton(){                                                                            //Creates A Button that leads to the GoalAdderActivity
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
    fun trackStepsForGoal(){                                                                        //registers the Listener for the step counter
        val context = LocalContext.current
        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepCounter == null){
            Toast.makeText(context, "Step sensor not found", Toast.LENGTH_LONG).show()
        }else{
            sensorManager.registerListener(this,stepCounter,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    @Preview
    @Composable
    fun Navigation(){                                                                               //adds Navigation that allows the User to view archived Goals
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
                    var intent = Intent(current,TaskHistory::class.java)
                    startActivity(intent)
                },
                shape = RectangleShape
            ) {
                Text(text = "History")

            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {                                                //triggers everytime a step has been recorded and counts it up in the Step counter Goal
        if(event?.sensor?.getType() == Sensor.TYPE_STEP_COUNTER){
            listOfActiveGoals.forEach { goal ->
                if(goal.glType == GoalType.stepCounter){
                    if(goal.progress > goal.taskGoal!!){
                        goal.completed = true
                        goal.archived = true
                        updateData(goal)
                    }else{
                        goal.progress++
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}
@Serializable
enum class GoalType(val goalType : String) : MutableState<GoalType> {                               // Determinants the Goal Type
    CheckMark("checked"),
    stepCounter("stepCounter");
    override var value: GoalType = this
        set(value){
        }
    override fun component1(): GoalType = value
    override fun component2(): (GoalType) -> Unit = { newValue -> value = newValue }
}
class Goal(val id:Int, val title: String, val desc: String, val glType: GoalType, val taskGoal: Int?,@Contextual val enddate: Date) { //The Goal class that Temporarily stores Data for quick Usage
    var completed = false
    var archived = false
    var progress = 0
}
