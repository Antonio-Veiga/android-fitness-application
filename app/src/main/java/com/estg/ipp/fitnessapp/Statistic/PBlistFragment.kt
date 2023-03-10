package com.estg.ipp.fitnessapp.Statistic


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.Statistic.dataclass.ExercicioQuantidadeETipo
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit



class ItemFragment : Fragment() {

    private var columnCount = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pb_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val executors6 = Executors.newFixedThreadPool(2)
                val dao = FitnessDB.getInstance(context)!!.fitnessDao()
                // sessoes com todos os treinos executados
                var listaDeSessoes: MutableList<Session> = ArrayList()
                // Treinos e exercicios
                var listaDeTreinosComExercicios: MutableList<TrainingExercise> = ArrayList()
                //Exercicios e quantidades


                executors6.execute {
                    listaDeSessoes = dao.getAllDoneSession()
                    if (listaDeSessoes.size != 0) {
                        listaDeTreinosComExercicios =
                            dao.getAllTrainingExercises() as MutableList<TrainingExercise>
                    }
                }
                executors6.shutdown()
                try {
                    executors6.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                } catch (e: InterruptedException) {
                    throw e
                }

                val listaDeExerciciosFeitosNasSessoes: MutableList<ExercicioQuantidadeETipo> =
                    getExerciciosFeitosNasSessoes(listaDeTreinosComExercicios, listaDeSessoes)
                adapter = PBRecyclerViewAdapter(listaDeExerciciosFeitosNasSessoes)
            }
        }
        return view
    }

   private fun getExerciciosFeitosNasSessoes(
        lista_de_treinos_com_exercicios: MutableList<TrainingExercise>,
        lista_de_sessoes: MutableList<Session>
    ): MutableList<ExercicioQuantidadeETipo> {
        var listaDeExerciciosFeitosNasSessoes: MutableList<ExercicioQuantidadeETipo> =
            emptyArray<ExercicioQuantidadeETipo>().toMutableList()
        if (lista_de_sessoes.size != 0 && lista_de_treinos_com_exercicios.size != 0) {
            for (a in 0 until lista_de_sessoes.size) {
                for (b in 0 until lista_de_treinos_com_exercicios.size) {
                    if (lista_de_sessoes[a].sessionTrainId == lista_de_treinos_com_exercicios[b].trainId) {
                        listaDeExerciciosFeitosNasSessoes.add(
                            ExercicioQuantidadeETipo(
                                lista_de_treinos_com_exercicios[b].quantity,
                                lista_de_treinos_com_exercicios[b].exerciseName,
                                lista_de_treinos_com_exercicios[b].type
                            )
                        )
                    }
                }
            }
            listaDeExerciciosFeitosNasSessoes.sortByDescending { it.quantidade }
            listaDeExerciciosFeitosNasSessoes =
                listaDeExerciciosFeitosNasSessoes.distinctBy {
                    listOf(it.name, it.tipo.name)
                }.toMutableList()
        }
        return listaDeExerciciosFeitosNasSessoes
    }
}