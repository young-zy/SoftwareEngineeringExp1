import java.io.File
import kotlin.math.exp
import kotlin.math.abs
import kotlin.math.pow

data class Test(val x:Int,val y:Int)



fun main(){
    val a = Test(1,2)
    val b = Test(1,2)
    val tm:MutableMap<Test,Int> = mutableMapOf()
    tm[a] = tm[a]?.plus(1) ?: 1
    tm[b] = tm[b]?.plus(1) ?: 1
    tm.forEach {
        println("${it.key},${it.value}")
    }
    var count = 2
    var ma: Map<Pair<Int,Int>,Double> = mapOf()
    for(i in 5..8){
        for (j in 1..2){
            if(i == 8 && j == 2){
                break
            }else if (i == 5 && j == 1){
                val ma1 = readFile("D:\\Kotlin\\newSigmoid\\edge2015_1.csv")
                val ma2 = readFile("D:\\Kotlin\\newSigmoid\\edge2015_2.csv")
                ma = merge(ma1, ma2)
                writeFile(ma,"D:\\Kotlin\\newSigmoid\\1.csv")
                break
            }else{
                val ma2 = readFile("D:\\Kotlin\\newSigmoid\\edge201${i}_${j}.csv")
                ma = merge(ma,ma2)
                writeFile(ma,"D:\\Kotlin\\newSigmoid\\$count.csv")
                count++
            }

        }
    }
}

fun sigmoid(x:Double):Double = 1/(1+exp(-x))

fun calAlpha(ma1:Map<Pair<Int,Int>,Double>,ma2:Map<Pair<Int,Int>,Double>):Double{
    var res = 0.5           //mocked as 0.5
    val n0 = ma1.size
    var mu = 0.0
    var sig = 0.0
    val se = mutableSetOf<Int>()
    ma1.forEach {
        se.add(it.key.first)
        se.add(it.key.second)
    }
    ma2.forEach{
        mu += it.value
    }
    mu/=ma2.size
    ma2.forEach{
        sig += abs(it.value - mu).pow(2)
    }
    sig /= ma2.size
//    se.forEach{
//        val it1 = it
//        se.forEach{
//            TODO("requirement not acknowledged")
//        }
//    }
    return res
}

fun readFile(path:String):Map<Pair<Int,Int>,Double>{
    val res: MutableMap<Pair<Int,Int>, Double> = mutableMapOf()
    try {
        val file = File(path)
        file.forEachLine {
            if(it != "Source,Target,Weight") {
                val temp = it.split(',')
                res[Pair<Int,Int>(temp[0].toInt(),temp[1].toInt())] = sigmoid(temp[2].toDouble())
            }
        }
    }catch (e:Exception){
        print(e.stackTrace)
    }
    println("正在读入$path")
    println("读入${res.size}行数据")
    return res
}

fun writeFile(ma:Map<Pair<Int,Int>,Double>,path:String){
    try {
        val writer = File(path).writer()
        writer.append("Source,Target,Weight\n")
        ma.forEach {
            writer.append("${it.key.first},${it.key.second},${it.value}\n")
        }
        writer.close()
    }catch (e:Exception){
        print(e.printStackTrace())
    }

    println("输出到$path")
    println("输出${ma.size}行数据")
}

fun merge(ma1:Map<Pair<Int,Int>,Double>,ma2:Map<Pair<Int,Int>,Double>):Map<Pair<Int,Int>,Double>{
    val alpha = calAlpha(ma1,ma2)
    val res: MutableMap<Pair<Int,Int>, Double> = mutableMapOf()
    ma1.forEach{
        res[it.key] = it.value*alpha
    }
    ma2.forEach{
        res[it.key] = res.getOrDefault(it.key,0.0)+(it.value*(1-alpha))
    }
    return res
}

