package com.bigchange.basic

/**
  * Created by C.J.YOU on 2016/4/20.
  */
import org.apache.spark.{SparkConf, SparkContext}

// 我们定义一个 Student class，这里面包含了 Student 的信息，注意 case class 包含了序列化
// case class Student(val name: String, val card: String, val birthday: String)

// 这个 Student class 不是 case class，因此需要 spark register
class Student(val name: String, val card: String, val birthday: String)

object BroadcastTest2 {
  def main(args: Array[String]): Unit = {
    // 使用 kryo classes 序列号，提高性能
    val sparkConf = new SparkConf().setAppName("BroadcastTest2")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array(classOf[Student]))

    val students = Map("qifeng.dai" -> new Student("qifeng.dai", "SA08011084", "02-22"), "yijing.liu" -> new Student("yijing.liu", "BA08011001", "01-05"))

    val ctx = new SparkContext(sparkConf)
    val broadcastVar = ctx.broadcast(students)

    val newStudents = ctx.parallelize(List("jessical", "matrix", "qifeng.dai")).map(x => broadcastVar.value.get(x) match {
      case None => (x, None)
      case Some(e) => (x, e.card + "," + e.birthday)
    })

    newStudents.collect.foreach(println)

    ctx.stop()
  }
}