import java.io.*
import java.lang.Integer.min
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


const val fileName = "flow"
const val MAX_V = 100 + 5
const val MAX_E = 5000 + 5
const val MAX_C = 10_000 + 5
var s = 1
var t = 1

class FastScanner(f: File) {
    private lateinit var br: BufferedReader
    private var st: StringTokenizer? = null

    init {
        try {
            br = BufferedReader(FileReader(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    operator fun next(): String {
        while (st == null || !st!!.hasMoreTokens()) {
            try {
                st = StringTokenizer(br.readLine())
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return st!!.nextToken()
    }

    fun nextInt(): Int {
        return next().toInt()
    }

    fun nextDouble(): Double {
        return next().toDouble()
    }
}

var inp = FastScanner(File("$fileName.in"))
var out = File("$fileName.out").printWriter()

class Tube(val v: Int, val c: Int, var flow: Int)

val edges = Array<ArrayList<Tube>>(MAX_V, { ArrayList() })
var n = 0
var m = 0
val shortestPaths = IntArray(MAX_V, { -1 })
val deletedV = IntArray(MAX_V, { 0 })
val tubeMap = HashMap<Pair<Int, Int>, ArrayList<Tube>>()
var maxFlow = 0
val tubes = ArrayList<Tube>()

fun input() {
    n = inp.nextInt()
    m = inp.nextInt()
    for (i in 1..m) {
        val v = inp.nextInt()
        val u = inp.nextInt()
        val c = inp.nextInt()
        edges[v].add(Tube(u, c, 0))
        edges[u].add(Tube(v, c, 0))
        tubeMap.putIfAbsent(Pair(v, u), ArrayList())
        tubeMap.putIfAbsent(Pair(u, v), ArrayList())
        tubeMap[Pair(v, u)]!!.add(edges[v].last())
        tubeMap[Pair(u, v)]!!.add(edges[u].last())
        tubes.add(edges[v].last())
    }
    s = 1
    t = n
}


fun findShortestPaths(): Boolean {
    shortestPaths.fill(-1)
    val bfsQueue = ArrayDeque<Int>()
    bfsQueue.add(s)
    shortestPaths[s] = 0

    while (bfsQueue.isNotEmpty()) {
        val v = bfsQueue.poll()
        for (to in edges[v]) {
            if (shortestPaths[to.v] == -1 && to.flow < to.c) {
                shortestPaths[to.v] = shortestPaths[v] + 1
                bfsQueue.add(to.v)
            }
        }
    }

    return shortestPaths[t] != -1
}

fun pushFlow(v: Int, flow: Int): Int {

    if (flow == 0) {
        return 0
    }
    if (v == t) {
        return flow
    }

    for (to in deletedV[v]..n) {
        if (shortestPaths[to] != shortestPaths[v] + 1) {
            continue
        }
        if (Pair(v, to) !in tubeMap) {
            continue
        }
        for (i in 0 until tubeMap[Pair(v, to)]!!.size) {
            val tubeVU = tubeMap[Pair(v, to)]!![i]
            val tubeUV = tubeMap[Pair(to, v)]!![i]
            val pushedFlow = pushFlow(to, min(flow, tubeVU.c - tubeVU.flow))
            if (pushedFlow != 0) {
                tubeVU.flow += pushedFlow
                tubeUV.flow -= pushedFlow
                return pushedFlow
            }
        }
        deletedV[v]++
    }
    return 0
}

fun findMaxFlow(): Int {
    while (true) {
        if (!findShortestPaths()) {
            break
        }
        deletedV.fill(0)
        while (true) {
            val pushedFlow = pushFlow(s, 1000_000_000)
            maxFlow += pushedFlow
            if (pushedFlow == 0) {
                break
            }
        }
    }
    return maxFlow
}

fun solve() {
    findMaxFlow()
}

fun output() {
    out.println(maxFlow)
    for (tube in tubes) {
        out.println(tube.flow)
    }
}

fun main(args: Array<String>) {
    try {
        input()
        solve()
        output()
        out.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}