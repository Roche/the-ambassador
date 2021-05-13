package pl.filipowm.innersource.ambassador.model.stats

import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun main(args: Array<String>) {
    val timeline = Timeline()

    timeline.add(LocalDate.now(), 2) //kw
    timeline.add(LocalDate.now().minusDays(1), 7) //kw
    timeline.add(LocalDate.now().minusDays(13), 3) //kw
    timeline.add(LocalDate.now().minusDays(26), 5) //kw
    timeline.add(LocalDate.now().minusDays(29), 9) //mar
    timeline.add(LocalDate.now().minusDays(100), 7) // st
    timeline.add(LocalDate.now().minusDays(101), 7) //st
    timeline.add(LocalDate.now().minusDays(370), 7) // kw 2020
    timeline.add(LocalDate.now().minusDays(270), 7) // kw 2020

    // total: 40
    println(timeline)
    println(timeline.average())
    println(timeline.last(1).years().by().weeks().sum())
    println(timeline.last(6).months().by().months().movingAverage(3, 1))
    println(timeline.by().months())
    println(timeline.by().months().average())

    println("LAST")
//    println(timeline.by().months().)
    println(timeline.last(1).years().by().months().movingAverage(2, 1))
    // 14 9 17
    // 11.5 13 17
//    println(timeline.last(1).years().last(4)..average())
}