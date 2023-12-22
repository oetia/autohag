package com.magicalhag.autohag.auto.games.epicseven

object EpicSevenState {
    enum class Task {
        Startup,
        Home,
        Hunt,
        SanctuaryHeart,
        SanctuaryForestPenguin, SanctuaryForestSpirit, SanctuaryForestMola,
        Arena
    }

    var task: Task = Task.SanctuaryHeart
}
