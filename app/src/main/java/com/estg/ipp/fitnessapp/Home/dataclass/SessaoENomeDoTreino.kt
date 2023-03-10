package com.estg.ipp.fitnessapp.Home.dataclass

import java.io.Serializable
import java.time.LocalDateTime

data class SessaoENomeDoTreino (
    var treinoTime : LocalDateTime,
    var treinoNome : String
): Serializable