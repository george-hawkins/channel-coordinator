Common channel finder
=====================

We split the 2.4GHz band into fourteen channels and have two radios each of which is turned on at a random time and each of which starts on a random channel.

The radios receive and transmit. If they regularly send out small packets what is the best channel hopping strategy for them to employ in order to soonest reach a channel where each sees the traffic of the other?

A major constraint is that they can't assume that any given channel is free enough of noise that they will definitely see each other on that channel, e.g. they cannot just jump to channel 1 and transmit there.

And they cannot assume that noise is consistent over time, e.g. a channel may be noisy for a while but become clear later or it may stay noisy indefinitely.

I tried posing this as a puzzle to ChatGPT but it failed singularly to find a working solution - it suggested many simple approaches that broke down due to the noise related constraints.

So, I started trying to think up and simulate various strategies. This repo contains those strategies and a super simple simulator.

Perhaps oddly, given the noise related constraints that I enforced on ChatGPT, the simulation does cover channels being intermittently or permanently unavailable.

But I think this is OK as I just didn't create strategies that depended on the radios definitely seeing each other if they happened to be on the same channel.

The `main` method can be found in [`Main.kt`](src/main/kotlin/net/betaengine/channelcoordinator/Main.kt) and the various strategies in [`Strategies.kt`](src/main/kotlin/net/betaengine/channelcoordinator/Strategies.kt).

You can change the strategy to be tested by just changing the strategy class in `main`:

```
fun main() {
    val strategyCreator = ::RandomChannelRandomWait
    ...
```

At the moment the winning strategy is very simple - just jump to a random channel and stay there for a random time (up to a small predefined maximum), then repeat until you see the other radio.

Results:

```
Random channel, fixed wait:

Time: median=161, avg=238, worst=4568
Moves: median=11, avg=16, worst=306

Time: median=162, avg=240, worst=4144
Moves: median=11, avg=16, worst=278

Time: median=162, avg=238, worst=4391
Moves: median=11, avg=16, worst=294

Time: median=162, avg=239, worst=4216
Moves: median=11, avg=16, worst=282

---

Random channel, random wait:

Time: median=104, avg=142, worst=1573
Moves: median=13, avg=18, worst=185

Time: median=104, avg=142, worst=1555
Moves: median=13, avg=18, worst=194

Time: median=104, avg=143, worst=1341
Moves: median=13, avg=18, worst=175

Time: median=104, avg=142, worst=1822
Moves: median=13, avg=18, worst=220

---

Simple random channel, random wait:

Time: median=109, avg=149, worst=1640
Moves: median=14, avg=19, worst=214

Time: median=108, avg=149, worst=1474
Moves: median=14, avg=19, worst=197

Time: median=109, avg=149, worst=2072
Moves: median=14, avg=19, worst=259

Time: median=108, avg=149, worst=1719
Moves: median=14, avg=19, worst=230

---

Random up down:

Time: median=119, avg=174, worst=1873
Moves: median=15, avg=22, worst=242

Time: median=120, avg=174, worst=2238
Moves: median=15, avg=22, worst=288

Time: median=120, avg=174, worst=2016
Moves: median=15, avg=22, worst=253

Time: median=120, avg=174, worst=2706
Moves: median=15, avg=22, worst=361
```
