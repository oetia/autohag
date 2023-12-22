package com.magicalhag.autohag.auto.games.epicseven

object EpicSevenState {
    enum class Task {
        Startup, Home, Hunt, Sanctuary, Arena
    }

    var task: Task = Task.Startup
}
