package com.estg.ipp.fitnessapp.Statistic


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.User
import com.estg.ipp.fitnessapp.Database.Entities.Weight
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.UserWithActualWeight
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow



class StatisticFragment : Fragment() {
    private lateinit var binding: FragmentStatisticBinding


    @SuppressLint("JavascriptInterface")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticBinding.inflate(inflater, container, false)


        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        var user: User? = null
        var pesoDoUser: UserWithActualWeight?
        var peso: Weight? = null
        var listaSessoesFeitas: MutableList<Session> = emptyArray<Session>().toMutableList()



        executors.execute {
            val listaUsers = dao.loadAllUsers()
            val listaPesos = dao.getUsersAndActualWeight()
            if (listaUsers.isNotEmpty() && listaPesos.isNotEmpty()) {
                user = listaUsers.first()
                pesoDoUser = listaPesos.first()
                peso = pesoDoUser!!.weight
                listaSessoesFeitas = dao.getAllDoneSession()
            }

        }
        executors.shutdown()
        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }



        if (user != null && peso != null) {
            binding.altura.text = user!!.height.toString()
            binding.numeroDeTreinos.text = listaSessoesFeitas.size.toString()
            binding.pesoAtual.text = peso!!.weight.toString()
            bmi(binding.pesoAtual.text.toString(), binding.altura.text.toString())
        }
/*

*/
        binding.addAltura.setOnClickListener {
            showdialog("Altura", user!!)

        }
        binding.addPesoAtual.setOnClickListener {
            showdialog("Peso Atual", user!!)
        }



        binding.pbList.setOnClickListener {
            findNavController().navigate(R.id.action_nav_statistic_to_itemFragment)
        }


        binding.maisEstatisticas.setOnClickListener {
            if (user != null) {
                findNavController().navigate(
                    StatisticFragmentDirections.actionNavStatisticToMaisEstatisticasFragment(
                        binding.pesoAtual.text.toString(),
                        user!!
                    )
                )
            }
        }


        barChart()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun barChart() {
        val executors8 = Executors.newFixedThreadPool(2)
        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        var pesosDoUser: Array<Weight> = emptyArray()
        executors8.execute {
            pesosDoUser = dao.getAllWeight()
        }
        executors8.shutdown()
        try {
            executors8.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }

        pesosDoUser = pesosDoUser.sortedByDescending { it.weightId }.toTypedArray()
        val values: ArrayList<BarEntry> = ArrayList()
        if (pesosDoUser.size > 7) {
            for (i in 0..6) {
                val a = BarEntry(
                    pesosDoUser[i].date.dayOfMonth.toFloat(),
                    pesosDoUser[i].weight
                )
                values.add(a)
            }
        } else {
            for (i in pesosDoUser.indices) {
                val a = BarEntry(
                    pesosDoUser[i].date.dayOfMonth.toFloat(),
                    pesosDoUser[i].weight
                )
                values.add(a)
            }
        }
        if (values.size != 0) {
            val barDataSet = BarDataSet(values, "Últimos 7 pesos registados por ordem")
            barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            val data = BarData(barDataSet)

            binding.barChart.data = data
            binding.barChart.axisRight.isEnabled = false
            binding.barChart.axisLeft.setDrawGridLines(false)
            binding.barChart.xAxis.labelCount = 0
            binding.barChart.xAxis.setDrawGridLines(false)
            binding.barChart.xAxis.setDrawAxisLine(false)
            binding.barChart.description.isEnabled = false
            binding.barChart.animateY(3000)
        }

        binding.barChart.invalidate()
    }


    private fun bmi(pesoAtual: String, altura: String): Float {
        val bmi = pesoAtual.toFloat() / (((altura.toFloat()) / 100).pow(2))
        binding.BmiNumber.text = bmi.toString()
        if (bmi > 0.toFloat() && bmi < 18.5.toFloat()) {
            val underweight = "Abaixo do peso"
            binding.BmiText.text = underweight
        } else if (bmi >= 18.5.toFloat() && bmi < 25.0.toFloat()) {
            val normal = "Normal"
            binding.BmiText.text = normal
        } else if (bmi >= 25.0.toFloat() && bmi < 30.0.toFloat()) {
            val overweight = "Acima do peso"
            binding.BmiText.text = overweight
        } else if (bmi >= 30.0.toFloat() && bmi < 34.9.toFloat()) {
            val obese = "Obeso"
            binding.BmiText.text = obese
        } else {
            val extremelyObese = "Obesidade Morbida"
            binding.BmiText.text = extremelyObese
        }
        return bmi
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showdialog(titulo: String, user: User) {
        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        val executors2 = Executors.newFixedThreadPool(2)
        val executors3 = Executors.newFixedThreadPool(2)
        val executors4 = Executors.newFixedThreadPool(2)
        val executors5 = Executors.newFixedThreadPool(2)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.context)
        builder.setTitle(titulo)
        val input = EditText(this.context)
        input.hint = "Introduza " + titulo
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Guardar") { dialog, _ ->
            //peso < 700 > 0 .. altura <400 >0
            val mText = input.text.toString()
            try {
                if (titulo == "Peso Atual") {
                    mText.toFloat()
                }
                if (titulo == "Altura") {
                    mText.toInt()
                }
                if (titulo == "Peso Atual" && 0 < mText.toFloat() && mText.toFloat() < 700) {
                    var idPeso: Long = 0
                    binding.pesoAtual.text = mText
                    val weight2 = Weight(
                        weight = mText.toFloat(),
                        date = LocalDateTime.now()
                    )
                    executors3.execute {
                        idPeso = dao.insertWeight(weight2)
                    }
                    executors3.shutdown()
                    try {
                        executors3.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                    } catch (e: InterruptedException) {
                        throw e
                    }
                    user.actualWeightId = idPeso

                    executors4.execute {
                        dao.updateUser(user)
                    }
                    executors4.shutdown()
                    try {
                        executors4.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                    } catch (e: InterruptedException) {
                        throw e
                    }
                    val bmi2 =
                        bmi(binding.pesoAtual.text.toString(), binding.altura.text.toString())
                    user.bmi = bmi2
                    executors2.execute {
                        dao.updateUser(user)
                    }
                    barChart()
                } else if (titulo == "Altura" && 0 < mText.toInt() && mText.toInt() < 400) {
                    binding.altura.text = mText
                    user.height = mText.toFloat()
                    executors5.execute {
                        dao.updateUser(user)
                    }
                    executors5.shutdown()
                    try {
                        executors5.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                    } catch (e: InterruptedException) {
                        throw e
                    }
                    val bmi2 =
                        bmi(binding.pesoAtual.text.toString(), binding.altura.text.toString())
                    user.bmi = bmi2
                    executors2.execute {
                        dao.updateUser(user)
                    }
                } else {
                    Toast.makeText(
                        this.context,
                        "Introduza valores válidos", Toast.LENGTH_SHORT
                    ).show()
                    dialog.cancel()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    this.context,
                    "Introduza valores válidos", Toast.LENGTH_SHORT
                ).show()
                dialog.cancel()
            }

        }
        builder.setNegativeButton(
            "Cancelar"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}