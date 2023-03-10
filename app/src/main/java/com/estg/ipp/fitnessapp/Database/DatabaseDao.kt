package com.estg.ipp.fitnessapp.Database

import androidx.room.*
import com.estg.ipp.fitnessapp.Database.Entities.*
import com.estg.ipp.fitnessapp.Database.Queries.*
import java.time.LocalDateTime

@Dao
interface DatabaseDao {
    /* POPULATE DATABASE */
    @Insert
    fun insertAllExercises(exercises: List<Exercise>)

    /* USER OPERATIONS */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(users: User)

    @Update
    fun updateUser(users: User)

    @Delete
    fun deleteUser(users: User)
    //////////////////////

    /* RUN OPERATIONS */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: Run)

    //////////////////////

    /* EXERCISE OPERATIONS*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(exercises: TrainingExercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercises(vararg trainingExercises: TrainingExercise)

    @Update
    fun updateExercise(exercises: TrainingExercise)

    @Delete
    fun deleteExercise(exercises: TrainingExercise)
    ////////////////////////

    /* SESSION OPERATIONS*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: Session): Long

    @Update
    fun updateSession(session: Session)

    @Delete
    fun deleteSession(session: Session)
    ////////////////////////

    /* TRAIN OPERATIONS*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTraining(train: Train): Long

    @Update
    fun updateTraining(train: Train)

    @Delete
    fun deleteTraining(train: Train)
    ////////////////////////

    /*Weight OPERATIONS */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeight(weight: Weight): Long

    /* TRAIN AND EXERCISE INSERT OPERATION*/
    @Transaction
    fun insertTrainingWithExercises(train: Train, trainingExercises: List<TrainingExercise>): Long {
        val trainId = insertTraining(train)

        for (exercise in trainingExercises) {
            exercise.trainId = trainId
            insertExercise(exercise)
        }
        return trainId
    }

    @Query("DELETE FROM Session WHERE sessionTrainId = :trainId")
    fun deleteAllExercisesOfTrain(trainId: Long)

    /* QUERIES */
    @Query("SELECT * FROM user")
    fun loadAllUsers(): List<User>

    @Transaction
    @Query("SELECT * FROM user")
    fun getUsersAndMinWeight(): List<UserWithMinWeight>

    @Transaction
    @Query("SELECT * FROM user")
    fun getUsersAndMaxWeight(): List<UserWithMaxWeight>

    @Transaction
    @Query("SELECT * FROM user")
    fun getUsersAndActualWeight(): List<UserWithActualWeight>

    @Query("SELECT * FROM session")
    fun getAllSessions(): MutableList<Session>

    @Query("SELECT day FROM session")
    fun getAllSessionsDate(): MutableList<LocalDateTime>

    @Query("SELECT day FROM session WHERE day >= :day")
    fun getAllSessionsDateAfterADay(day: LocalDateTime): MutableList<LocalDateTime>

    @Query("SELECT * FROM session WHERE day >= :day")
    fun getAllSessionsAfterADay(day: LocalDateTime): MutableList<Session>

    @Transaction
    @Query("SELECT * FROM session WHERE day >= :day")
    fun getAllSessionsWithTrainingAfterADay(day: LocalDateTime): MutableList<SessionWithTrain>

    @Transaction
    @Query("SELECT * FROM session")
    fun getAllSessionsAndTrains(): MutableList<SessionWithTrain>

    @Query("SELECT * FROM train")
    fun getAllTrains(): MutableList<Train>

    @Transaction
    @Query("SELECT * FROM train")
    fun getAllTrainsAndExercises(): MutableList<TrainWithExercises>

    @Transaction
    @Query("SELECT * FROM TrainingExercise")
    fun getAllTrainingExercises(): List<TrainingExercise>

    @Transaction
    @Query("SELECT * FROM train WHERE trainId = :id")
    fun getTrainWithExercise(id: Int): TrainWithExercises

    @Transaction
    @Query("SELECT * FROM trainingexercise WHERE trainId = :id")
    fun getExercisesOfTrain(id: Long): List<TrainingExercise>

    @Query("SELECT * FROM exercise")
    fun getAllExercises(): List<Exercise>

    @Query("SELECT exerciseName FROM exercise")
    fun getAllExercisesName(): List<String>

    @Transaction
    @Query("SELECT * FROM Weight")
    fun getAllWeight(): Array<Weight>

    @Transaction
    @Query("SELECT * FROM session WHERE sessionId == :id")
    fun getSessionWithTrainById(id: Long): SessionWithTrain?

    @Query("SELECT * FROM run")
    fun getAllRuns(): List<Run>

    @Transaction
    @Query("SELECT * FROM session WHERE day BETWEEN :startDate AND :endDate ")
    fun getAllSessionsAndTrainsOnADay(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): MutableList<SessionWithTrain>

    @Query("SELECT * FROM session WHERE day BETWEEN :startDate AND :endDate ")
    fun getAllSessionsOnADay(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): MutableList<Session>

    @Query("SELECT * FROM session WHERE day < :date ")
    fun getAllSessionsBeforeDate(
        date: LocalDateTime
    ): MutableList<Session>

    @Query("SELECT * FROM session WHERE status == 1 ")
    fun getAllDoneSession() : MutableList<Session>
}