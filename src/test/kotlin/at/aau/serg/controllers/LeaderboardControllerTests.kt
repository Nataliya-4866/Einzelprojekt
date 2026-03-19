package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_withRank_coversAllBounds() {
        val results = (1..10).map { GameResult(it.toLong(), "Player$it", 100 - it, it.toDouble()) }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Fall 1: Rank 1
        val top = controller.getLeaderboard(1)
        assertEquals(4, top.size) // Spieler 1 plus 3 darunter

        // Fall 2: Rank 5
        val middle = controller.getLeaderboard(5)
        assertEquals(7, middle.size) // 3 darüber, Spieler selbst, 3 darunter

        // Fall 3: Rank 10
        val bottom = controller.getLeaderboard(10)
        assertEquals(4, bottom.size) // Spieler 10 plus 3 darüber
    }

    @Test
    fun test_getLeaderboard_withInvalidRankTooSmall() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 10.0)))

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(0)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_withInvalidRankTooBig() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 10.0)))

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(2)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

}