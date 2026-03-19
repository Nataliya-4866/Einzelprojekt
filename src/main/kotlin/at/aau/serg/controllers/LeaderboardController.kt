package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.lang.Integer.max
import java.lang.Integer.min

@RestController
@RequestMapping("/leaderboard")

class LeaderboardController(private val gameResultService: GameResultService) {

    /*@GetMapping
    fun getLeaderboard(): List<GameResult> = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.id }))*/

    // (höher ist besser) absteigend sortiert
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?=null): List<GameResult> {

        val leaderboard = gameResultService.getGameResults()
            .sortedWith(
                compareByDescending<GameResult> { it.score }
                    .thenBy { it.timeInSeconds }
            )

        // Wenn kein rank angegeben wurde → gesamtes Leaderboard
        if (rank == null) {
            return leaderboard
        }

        // Ungültiger rank → HTTP 400
        if (rank < 1 || rank > leaderboard.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        val index = rank - 1

        val start = max(0, index - 3)
        val end = min(leaderboard.size, index + 4)

        return leaderboard.subList(start, end)
    }

}
