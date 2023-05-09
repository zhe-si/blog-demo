package com.zhesi.blog

/**
 * **路由匹配**
 * - 若 "R:" 开头，则为正则模式，采用正则表达式直接匹配
 * - 其他情况，采用标准路由模式[matchStdRoutePattern]进行匹配
 *
 * @author lq
 * @version 1.0
 */
fun matchRoutePattern(routePattern: String, path: String): Boolean {
    return if (routePattern.startsWith("R:")) {
        Regex(routePattern.substring(2)).matches(path)
    } else {
        matchStdRoutePattern(routePattern, path)
    }
}

/**
 * **路由模式**：路由的特定描述表达式，形如 / ** /xxx/ * /xxx/
 *
 * 语法：
 * - 以 '/'([PATH_DELIMITER]) 分隔的路径表达式，要求全匹配路径，首尾的分隔符可以省略
 * - 每一段路径描述默认采用字符串完全匹配方式，也可通过 "r:" 开头标记该段采用正则表达式匹配
 * - 使用通配符 "?" 可以匹配任意一段或 0 段路径，优先不匹配
 * - 使用通配符 "*" 可以匹配任意一段路径
 * - 使用通配符 "**" 可以匹配任意多段路径，最短匹配原则
 * - 使用通配符 "***" 可以匹配任意多段路径，最长匹配原则
 */
fun matchStdRoutePattern(routePattern: String, path: String): Boolean {
    val routeSplit = splitRoute(routePattern)
    val pathSplit = splitPath(path)
    return matchRoutePatternSplit(routeSplit, 0, pathSplit, 0)
}

/**
 * 路由分隔方法
 */
val splitRoute: (String) -> List<String> = ::splitPathSimple
/**
 * 路径分隔方法
 */
val splitPath: (String) -> List<String> = ::splitPathSimple

/**
 * 简单解析路径为路径分段列表
 */
private fun splitPathSimple(routePattern: String): List<String> {
    val pathDelimiter = '/'
    return routePattern.trim(pathDelimiter).split(pathDelimiter).filter { p -> p.isNotEmpty() }
}

private fun matchRoutePatternSplit(routeSplit: List<String>, ri: Int, pathSplit: List<String>, pi: Int): Boolean {
    if (ri >= routeSplit.size) {
        return pi >= pathSplit.size
    }
    if (pi >= pathSplit.size) {
        for (i in ri until routeSplit.size) {
            if (routeSplit[i] !in listOf("?", "**", "***")) return false
        }
        return true
    }
    when (routeSplit[ri].trim()) {
        "?" -> {
            if (matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi)) return true
            return matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi + 1)
        }
        "*" -> {
            return matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi + 1)
        }
        "**" -> {
            for (i in 0 until pathSplit.size - pi) {
                val isShortMatch = matchRoutePatternShort(routeSplit, ri + 1, pathSplit, pi + i, false)
                if (isShortMatch.first) return true
                if (isShortMatch.second) return false
            }
            return matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi + pathSplit.size - pi)
        }
        "***" -> {
            for (i in pathSplit.size - pi downTo 1) {
                if (matchRoutePatternSplit(routeSplit, pi + 1, pathSplit, pi + i)) return true
            }
            return matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi)
        }
        else -> {
            if (!checkRouteSegPattern(routeSplit[ri], pathSplit[pi])) return false
            return matchRoutePatternSplit(routeSplit, ri + 1, pathSplit, pi + 1)
        }
    }
}

/**
 * 最短原则匹配，返回 (是否匹配, 是否已经最短匹配)
 */
private fun matchRoutePatternShort(routeSplit: List<String>, ri: Int, pathSplit: List<String>, pi: Int, isShortMatch: Boolean): Pair<Boolean, Boolean> {
    if (ri >= routeSplit.size) {
        return (pi >= pathSplit.size) to isShortMatch
    }
    if (pi >= pathSplit.size) {
        for (i in ri until routeSplit.size) {
            if (routeSplit[i] !in listOf("?", "**", "***")) return false to isShortMatch
        }
        return true to isShortMatch
    }
    when (routeSplit[ri].trim()) {
        "?" -> {
            val isMatch = matchRoutePatternShort(routeSplit, ri + 1, pathSplit, pi, isShortMatch)
            if (isMatch.first) return isMatch
            return matchRoutePatternShort(routeSplit, ri + 1, pathSplit, pi + 1, isShortMatch)
        }
        "*" -> {
            return matchRoutePatternShort(routeSplit, ri + 1, pathSplit, pi + 1, isShortMatch)
        }
        "**" -> {
            return matchRoutePatternSplit(routeSplit, ri, pathSplit, pi) to isShortMatch
        }
        "***" -> {
            return matchRoutePatternSplit(routeSplit, ri, pathSplit, pi) to isShortMatch
        }
        else -> {
            return if (checkRouteSegPattern(routeSplit[ri], pathSplit[pi])) {
                matchRoutePatternShort(routeSplit, ri + 1, pathSplit, pi + 1, true)
            } else {
                false to isShortMatch
            }
        }
    }
}

private fun checkRouteSegPattern(routeSeg: String, pathSeg: String): Boolean {
    if (routeSeg.startsWith("r:")) {
        if (Regex(routeSeg.substring(2)).matches(pathSeg)) {
            return true
        }
    } else if (routeSeg == pathSeg) return true
    return false
}
