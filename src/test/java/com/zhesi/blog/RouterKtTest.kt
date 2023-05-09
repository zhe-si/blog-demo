package com.zhesi.blog

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * **RouterKtTest**
 *
 * @author lq
 * @version 1.0
 */
class RouterKtTest {

    @Test
    fun testMatchRoutePattern() {
        assertTrue(matchRoutePattern("a/?/c", "a/b/c"))
        assertTrue(matchRoutePattern("a/?/c", "a/c/c"))
        assertFalse(matchRoutePattern("a/?/c", "a/c/d"))
        assertTrue(matchRoutePattern("a/?/c", "a//c"))
        assertTrue(matchRoutePattern("a/?/c", "a/c"))
        assertTrue(matchRoutePattern("a/*/c", "a/b/c"))
        assertFalse(matchRoutePattern("a/*/c", "a/c"))

        assertFalse(matchRoutePattern("a/b/*", "a/b"))
        assertTrue(matchRoutePattern("a/b/*", "a/b/d"))
        assertFalse(matchRoutePattern("a/b/*", "a/b/"))
        assertTrue(matchRoutePattern("a/b/?", "a/b"))
        assertTrue(matchRoutePattern("a/b/?", "a/b/t"))

        assertTrue(matchRoutePattern("?/a/b", "/a/b"))
        assertFalse(matchRoutePattern("?/a/b", "a/b/c"))
        assertTrue(matchRoutePattern("?/a/b", "c/a/b"))

        assertTrue(matchRoutePattern("a/*/b", "a/c/b"))
        assertFalse(matchRoutePattern("a/*/b", "a/c/b/b"))
        assertFalse(matchRoutePattern("a/*/b", "a/b"))
        assertTrue(matchRoutePattern("*/c/b", "c/c/b"))
        assertFalse(matchRoutePattern("*/c/b", "c/b"))

        assertTrue(matchRoutePattern("***/c/b", "c/b/c/b"))
        assertTrue(matchRoutePattern("a/***/c/b", "a/c/b/c/b"))

        assertTrue(matchRoutePattern("**/c/b", "c/b"))
        assertTrue(matchRoutePattern("a/***/c/b/**", "a/c/b/c/b/kk"))
        assertFalse(matchRoutePattern("ab/?/a/b", "abc/a/b"))
        assertTrue(matchRoutePattern("?/a/b", "a/b"))
        assertFalse(matchRoutePattern("a/**/c/b", "a/c/b/c/b"))
        assertFalse(matchRoutePattern("**/c/b", "c/b/c/b"))

        assertTrue(matchRoutePattern("a/**/c/b/**", "a/c/b/c/b/kk/tt"))

        assertTrue(matchRoutePattern("a/***/c/*", "a/c/c"))
        assertTrue(matchRoutePattern("a/***/c/*", "a/c/b/c/d"))
        assertTrue(matchRoutePattern("a/**/c/*", "a/c/c"))
        assertFalse(matchRoutePattern("a/**/c/*", "a/c/b/c/d"))
    }

}