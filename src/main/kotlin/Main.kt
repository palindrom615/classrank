import dev.palindrom615.classrank.PageRank
import dev.palindrom615.classrank.StochasticGraph
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool

fun main(args: Array<String>) {
    val f = JavaFileFinder()
    Files.walkFileTree(Path.of(args[0]), f)
    val pool = ForkJoinPool.commonPool()

    val makeJavaClassJobs = f.matches().map { m -> Callable { JavaClass.of(m) } }
    pool.invokeAll(makeJavaClassJobs)

    val graph = StochasticGraph<String>()
    val makeGraphJobs = JavaClass.classMap.values.map { cls ->
        Callable {
            graph.addNode(
                cls.toString(),
                cls.imports().filter { i -> JavaClass.classMap.containsKey(i) })
        }
    }
    pool.invokeAll(makeGraphJobs)

    val p = PageRank(graph)

    val a = p.iterate(10000, pool).entries.toList().sortedBy { t -> -t.value }
    println("\n${"Class".padEnd(60)}| scaled pagerank")
    println("-".repeat(80))
    for (t in a.stream().limit(10)) {
        println("${t.key.padEnd(60)}: ${t.value * graph.size()}")
    }

}

class JavaFileFinder : SimpleFileVisitor<Path>() {
    private val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.java")
    private var matches: ArrayList<Path> = arrayListOf()

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        if (file == null) {
            return FileVisitResult.CONTINUE
        }
        if (!file.toString().contains("src/main")) {
            return FileVisitResult.CONTINUE
        }
        if (matcher.matches(file)) {
            matches.add(file)
        }
        return super.visitFile(file, attrs)
    }

    fun matches() = matches
}

