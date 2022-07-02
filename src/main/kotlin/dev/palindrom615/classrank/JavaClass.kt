import com.github.javaparser.StaticJavaParser
import dev.palindrom615.classrank.JavaPackage
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

class JavaClass private constructor(val path: Path) {
    val name: String
    val pkg: JavaPackage

    init {
        val compilationUnit = StaticJavaParser.parse(path)
        name = path.fileName.toString().replace(".java", "")
        pkg = JavaPackage.of(compilationUnit.packageDeclaration.get().name.asString())
        pkg.addClass(this)
    }

    override fun toString(): String {
        return "${pkg}.${name}"
    }

    companion object {
        private val memo = ConcurrentHashMap<Path, JavaClass>()
        val classMap = ConcurrentHashMap<String, JavaClass>()

        fun of(path: Path): JavaClass {
            return memo.computeIfAbsent(path) { path ->
                val res = JavaClass(path)
                classMap[res.toString()] = res
                res
            }
        }
    }
}