package net.betaengine.channelcoordinator

import kotlin.random.Random

abstract class Strategy {
    protected abstract fun nextChannel(channel: Int): Int

    protected abstract fun nextTime(now: Long): Long

    fun createNext(radio: Radio) =
        radio.copy(channel = nextChannel(radio.channel), time = nextTime(radio.time))
}

class RandomChannelFixedWait : Strategy() {
    override fun nextChannel(channel: Int) = generateSequence { (1..MAX_CHANNEL).random() }.dropWhile { it == channel }.first()

    override fun nextTime(now: Long) = now + MAX_PAUSE
}

class RandomChannelRandomWait : Strategy() {
    override fun nextChannel(channel: Int) = generateSequence { (1..MAX_CHANNEL).random() }.dropWhile { it == channel }.first()

    override fun nextTime(now: Long) = now + (1..MAX_PAUSE).random().toLong()
}

class SimpleRandomChannelRandomWait : Strategy() {
    override fun nextChannel(channel: Int) = (1..MAX_CHANNEL).random()

    override fun nextTime(now: Long) = now + (1..MAX_PAUSE).random().toLong()
}

class RandomUpDownChannelFixedWait : Strategy() {
    private var up = Random.nextBoolean()

    override fun nextChannel(channel: Int) =
        if (up) {
            if (channel == MAX_CHANNEL) randomFlip(channel) else channel + 1
        } else {
            if (channel == 1) randomFlip(channel) else channel - 1
        }

    private fun randomFlip(channel: Int): Int {
        up = Random.nextBoolean()

        return if (up) {
            if (channel != 1) 1 else 2
        } else {
            if (channel != MAX_CHANNEL) MAX_CHANNEL else (MAX_CHANNEL - 1)
        }
    }

    // Often results in pathological case where two radios are only ever briefly on the same channel.
    override fun nextTime(now: Long) = now + MAX_PAUSE
}

class RandomUpDownChannelRandomWait : Strategy() {
    private var up = Random.nextBoolean()

    override fun nextChannel(channel: Int) =
        if (up) {
            if (channel == MAX_CHANNEL) randomFlip(channel) else channel + 1
        } else {
            if (channel == 1) randomFlip(channel) else channel - 1
        }

    private fun randomFlip(channel: Int): Int {
        up = Random.nextBoolean()

        return if (up) {
            if (channel != 1) 1 else 2
        } else {
            if (channel != MAX_CHANNEL) MAX_CHANNEL else (MAX_CHANNEL - 1)
        }
    }

    override fun nextTime(now: Long) = now + (1..MAX_PAUSE).random().toLong()
}