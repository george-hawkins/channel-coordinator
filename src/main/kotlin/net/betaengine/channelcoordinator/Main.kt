package net.betaengine.channelcoordinator

import java.util.PriorityQueue

enum class Side {
    A,
    B
}

data class Radio(val side: Side, val channel: Int, val time: Long)

const val MAX_PAUSE = 30
const val OVERLAP_TIME = MAX_PAUSE / 10
const val MAX_CHANNEL = 14

fun startTime() = (1..MAX_PAUSE).random().toLong()
fun startChannel() = (1..MAX_CHANNEL).random()

data class Result(val time: Long, val moveCount: Int)

fun find(strategyCreator: () -> Strategy): Result {
    val strategyA = strategyCreator()
    val strategyB = strategyCreator()

    val timeQueue = PriorityQueue<Radio>(compareBy { it.time })

    timeQueue += Radio(Side.A, startChannel(), startTime())
    timeQueue += Radio(Side.B, startChannel(), startTime())

    var maybeRadioA: Radio? = null
    var maybeRadioB: Radio? = null
    var prevState = false
    var foundTime = -1L
    var foundCount = -1

    generateSequence(1, Int::inc).forEach { moveCount ->
        val radio = timeQueue.remove()
        val nowTime = radio.time

        when (radio.side) {
            Side.A -> maybeRadioA = radio
            Side.B -> maybeRadioB = radio
        }

        maybeRadioA?.let { radioA ->
            maybeRadioB?.let { radioB ->
                val state = radioA.channel == radioB.channel

                if (state != prevState) {
                    if (state) {
                        foundTime = nowTime
                        foundCount = moveCount
                    } else {
                        val diffTime = nowTime - foundTime
                        // The two radios overlapped on the same channel for at OVERLAP_TIME.
                        if (diffTime >= OVERLAP_TIME) {
                            return Result(foundTime, foundCount)
                        }
                    }
                }

                prevState = state
            }
        }

        val strategy = if (radio.side == Side.A) strategyA else strategyB

        timeQueue += strategy.createNext(radio)
    }

    error("infinite sequence exited unexpectedly")
}

fun median(list: List<Long>): Long {
    require(list.isNotEmpty())
    val middle = list.size / 2

    return if (list.size % 2 == 0)
        (list[middle] + list[middle - 1]) / 2
    else
        list[middle]
}

fun main() {
    val strategyCreator = ::RandomChannelRandomWait

    while (true) {
        val times = mutableListOf<Long>()
        val moves = mutableListOf<Long>()

        repeat(100_000) {
            find(strategyCreator).let { (time, moveCount) ->
                times += time
                moves += moveCount.toLong()
            }
        }
        println()
        times.sort()
        moves.sort()

        val timesMedian = median(times)
        val timesAvg = times.average().toLong()
        val timesWorst = times.last()
        val movesMedian = median(moves)
        val movesAvg = moves.average().toLong()
        val movesWorst = moves.last()
        println("Time: median=$timesMedian, avg=$timesAvg, worst=$timesWorst")
        println("Moves: median=$movesMedian, avg=$movesAvg, worst=$movesWorst")
    }
}