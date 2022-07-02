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
    Files.walkFileTree(Path.of("/Users/user/repo"), f)
    val pool = ForkJoinPool.commonPool()

    val rs = f.matches().map{m -> Callable { JavaClass.of(m) }}
    pool.invokeAll(rs)
    JavaClass.classMap
    return
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

