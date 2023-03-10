package com.estg.ipp.fitnessapp.Database

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.estg.ipp.fitnessapp.Database.Entities.*
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
@Database(
    entities = [TrainingExercise::class, Exercise::class, Train::class, Session::class,
        User::class, Weight::class, Run::class],
    version = 26
)
@TypeConverters(Converters::class)
abstract class FitnessDB : RoomDatabase() {

    abstract fun fitnessDao(): DatabaseDao

    companion object {

        var instance: FitnessDB? = null

        val executors = Executors.newFixedThreadPool(2)

        fun getInstance(context: Context): FitnessDB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    FitnessDB::class.java, "fitness_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executors.execute {
                                getInstance(context)!!.fitnessDao().insertAllExercises(
                                    getListOfExercise()
                                )
                            }
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            executors.execute {
                                getInstance(context)!!.fitnessDao().insertAllExercises(
                                    getListOfExercise()
                                )
                            }
                        }
                    })
                    .build()
            }
            return instance
        }

        fun getListOfExercise(): List<Exercise> {
            val exerciseList = mutableListOf<Exercise>()

            val timeAndDistance = mutableListOf<ExerciseType>()
            timeAndDistance.add(ExerciseType.Time)
            timeAndDistance.add(ExerciseType.Distance)

            val timeAndQuantity = mutableListOf<ExerciseType>()
            timeAndQuantity.add(ExerciseType.Time)
            timeAndQuantity.add(ExerciseType.Quantity)

            val justTime = mutableListOf<ExerciseType>()
            justTime.add(ExerciseType.Time)

            exerciseList.add(
                Exercise(
                    exerciseName = "Correr",
                    typesAllowed = timeAndDistance,
                    imagePath = "running"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Saltar à corda",
                    typesAllowed = timeAndQuantity,
                    imagePath = "jumping_rope"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Abdominais",
                    typesAllowed = timeAndQuantity,
                    imagePath = "sit_ups"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Flexões",
                    typesAllowed = timeAndQuantity,
                    imagePath = "push_ups"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Prancha",
                    typesAllowed = justTime,
                    imagePath = "plank"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Burpees",
                    typesAllowed = timeAndQuantity,
                    imagePath = "burppes"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Skipping",
                    typesAllowed = timeAndQuantity,
                    imagePath = "skipping"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Montanha",
                    typesAllowed = timeAndQuantity,
                    imagePath = "mountain"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Agachamentos",
                    typesAllowed = timeAndQuantity,
                    imagePath = "squat"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Ponte para Glúteos",
                    typesAllowed = timeAndQuantity,
                    imagePath = "glute_bridge"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Avanço",
                    typesAllowed = timeAndQuantity,
                    imagePath = "advance"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Prancha Lateral",
                    typesAllowed = timeAndQuantity,
                    imagePath = "sideways_plank"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Bicicleta",
                    typesAllowed = timeAndQuantity,
                    imagePath = "bicycle"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Levantamento de Pernas",
                    typesAllowed = timeAndQuantity,
                    imagePath = "leg_lift"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Agachamento com Pulo",
                    typesAllowed = timeAndQuantity,
                    imagePath = "jump_squat"
                )
            )
            exerciseList.add(
                Exercise(
                    exerciseName = "Jumping Jack",
                    typesAllowed = timeAndQuantity,
                    imagePath = "jumping_jack"
                )
            )

            return exerciseList
        }
    }
}