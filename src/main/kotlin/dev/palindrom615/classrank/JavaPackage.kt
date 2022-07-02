package dev.palindrom615.classrank

import JavaClass
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class JavaPackage private constructor(val name: String, val parent: JavaPackage?) {
    val subpackages: HashMap<String, JavaPackage> = HashMap()
    val classes: HashMap<String, JavaClass> = HashMap()

    fun addClass(cls: JavaClass) {
        classes.put(cls.name, cls)
    }

    override fun toString(): String {
        val domains = arrayListOf<String>()
        var ptr = this
        while (ptr != root) {
            domains.add(ptr.name)
            ptr = ptr.parent!!
        }
        return domains.reversed().stream().collect(Collectors.joining("."))
    }

    companion object {
        val root = JavaPackage("", null)
        private val memo = ConcurrentHashMap<String, JavaPackage>()

        /**
         * @param pkg valid package string without class only
         *            e.g. java.nio.file           <- ok
         *            java.nio.file.FileSystems    <- no
         *            java.nio.file.*              <- no
         */
        fun of(pkg: String): JavaPackage {
            return memo.computeIfAbsent(pkg) { pkg ->
                val domains = pkg.split(".")
                var ptr = root
                for (domain in domains) {
                    ptr = ptr.subpackages.computeIfAbsent(domain) { d -> JavaPackage(d, ptr) }
                }
                ptr
            }
        }
    }
}