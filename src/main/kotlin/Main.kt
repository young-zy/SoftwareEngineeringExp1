import java.io.File
import java.io.OutputStreamWriter
import kotlin.concurrent.timer
import kotlin.math.exp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.system.measureTimeMillis

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

/**
 * 将实数通过公式：
 * 1/(1+exp(-x))
 * 映射到0~1
 *
 * @param [x] 待映射的实数
 * @return 映射完成后的结果
 */
fun sigmoid(x:Double):Double = 1/(1+exp(-x))

/**
 * 通过 [ma1] 和 [ma2] 计算 alpha
 *
 *
 * @param ma1 第一张图。
 * @param ma2 第二张图。
 * @return 返回alpha的值
 */
fun calAlpha(ma1:Map<Pair<Int,Int>,Double>,ma2:Map<Pair<Int,Int>,Double>):Double{
    var res = 0.0
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
    res += n0*n0*(sig+mu.pow(2))
    ma1.forEach{
        res+=it.value*it.value
        res -= 2*it.value*mu
    }
    res = n0*n0*sig/res
    return res
}

/**
 * 从 [path] 读入图到 Map 中
 * 并将权值sigmoid
 *
 * @param [path] 读入的文件路径
 * @return 返回Sigmoid后的图，以Map的形式
 */
fun readFile(path:String):Map<Pair<Int,Int>,Double>{
    val res: MutableMap<Pair<Int,Int>, Double> = mutableMapOf()
    var file: File?
    val time = measureTimeMillis {
        try {
            file = File(path)
            file!!.forEachLine {
                if(it != "Source,Target,Weight") {
                    val temp = it.split(',')
                    res[Pair<Int,Int>(temp[0].toInt(),temp[1].toInt())] = sigmoid(temp[2].toDouble())
                }
            }
        }catch (e:Exception){
            print(e.stackTrace)
        }
    }
    println("正在读入$path")
    println("读入${res.size}行数据")
    println("耗费${time}毫秒")
    return res
}

/**
 * 将 [ma] 写入 [path]
 *
 * @param [ma] 要写入的图
 * @param [path] 写入的文件路径
 */
fun writeFile(ma:Map<Pair<Int,Int>,Double>,path:String){
    var writer: OutputStreamWriter? = null
    val time = measureTimeMillis {
        try {
            writer = File(path).writer()
            writer!!.append("Source,Target,Weight\n")
            ma.forEach {
                writer!!.append("${it.key.first},${it.key.second},${it.value}\n")
            }
        }catch (e:Exception){
            print(e.printStackTrace())
        }finally {
            writer?.close()
        }
    }
    println("输出到$path")
    println("输出${ma.size}行数据")
    println("耗费${time}毫秒")
}

/**
 * 将 [ma1] 和 [ma2] 通过平滑公式：
 * MA=α*[ma1]+(1-α)*[ma2]
 * 合并为一张图。
 *
 * @param ma1 第一张图。
 * @param ma2 第二张图。
 * @return 返回合并后的图
 */
fun merge(ma1:Map<Pair<Int,Int>,Double>,ma2:Map<Pair<Int,Int>,Double>):Map<Pair<Int,Int>,Double>{
    var alpha = 0.0
    var time = measureTimeMillis {
        alpha = calAlpha(ma1,ma2)
    }
    println("计算alpha花费${time}毫秒")
    val res: MutableMap<Pair<Int,Int>, Double> = mutableMapOf()
    time = measureTimeMillis {
        ma1.forEach{
            res[it.key] = it.value*alpha
        }
        ma2.forEach{
            res[it.key] = res.getOrDefault(it.key,0.0)+(it.value*(1-alpha))
        }
    }
    println("合并花费${time}毫秒")
    return res
}

