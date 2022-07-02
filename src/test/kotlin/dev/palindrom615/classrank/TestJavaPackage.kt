package dev.palindrom615.classrank

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class TestJavaPackage {
    @Test
    fun testJavaPackageOf() {
        val p1 = JavaPackage.of("dev.palindrom615.classrank")
        val p2 = JavaPackage.of("dev.palindrom615.classrank")
        assertSame(p1, p2)

        val p3 = JavaPackage.of("dev.palindrom615")
        assertSame(p1.parent, p3)
        assertTrue(p3.subpackages.contains("classrank"))
    }
}