package com.estg.ipp.fitnessapp.regist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.estg.ipp.fitnessapp.Database.DatabaseDao
import com.estg.ipp.fitnessapp.Database.Entities.User
import com.estg.ipp.fitnessapp.Database.Entities.Weight
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.MainActivity
import com.estg.ipp.fitnessapp.databinding.ActivityRegistoBinding
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class RegistoActivity : AppCompatActivity() {
    private var dao: DatabaseDao? = null
    private lateinit var executors9: ExecutorService
    private lateinit var binding: ActivityRegistoBinding
    private lateinit var settings: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = getPreferences(MODE_PRIVATE)
        sharedEditor = settings.edit()

        if (settings.getBoolean("primeira_vez", true)) {

            dao = FitnessDB.getInstance(this)?.fitnessDao()
            executors9 = Executors.newFixedThreadPool(2)
            binding = ActivityRegistoBinding.inflate(layoutInflater)

            binding.guardarButton.setOnClickListener {
                if (guardarDados()) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    sharedEditor.putBoolean("primeira_vez", false)
                    sharedEditor.commit()
                    sharedEditor.apply()
                    this.finish()
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                }
            }


            setContentView(binding.root)
        }else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
            }
        }


    override fun onBackPressed() {
        //Impedir voltar para tras
        return
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun guardarDados(): Boolean {
        val dao = FitnessDB.getInstance(this)!!.fitnessDao()
        val executors9 = Executors.newFixedThreadPool(2)
        var idPeso: Long

        if (binding.editAltura.text.isNotEmpty() && binding.editNome.text.isNotEmpty() && binding.editIdade.text.isNotEmpty() && binding.editPeso.text.isNotEmpty()) {
            if (binding.editAltura.text.toString()
                    .toInt() in 1..400 && binding.editIdade.text.toString()
                    .toInt() in 1..200 && binding.editIdade.text.toString()
                    .toFloat() < 700F && binding.editIdade.text.toString().toFloat() > 0F
            ) {
                executors9.execute {
                    idPeso = dao.insertWeight(
                        Weight(
                            weight = binding.editPeso.text.toString().toFloat(),
                            date = LocalDateTime.now()
                        )
                    )
                    dao.insertUser(
                        User(
                            name = binding.editNome.text.toString(),
                            age = binding.editIdade.text.toString().toInt(),
                            height = binding.editAltura.text.toString().toFloat(),
                            actualWeightId = idPeso,
                            maxWeightId = idPeso,
                            minWeightId = idPeso,
                            overtimeWeight = 0F,
                            trainNumber = 0,
                            bmi = binding.editPeso.text.toString()
                                .toFloat() / (((binding.editAltura.text.toString()
                                .toFloat()) / 100).pow(2))
                        )
                    )
                }
                executors9.shutdown()
                try {
                    executors9.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                } catch (e: InterruptedException) {
                    throw e
                }
                Toast.makeText(this, "Dados Guardados", Toast.LENGTH_LONG).show()
                return true
            }else{
                return false
            }
        } else {
            return false
        }
    }
}

