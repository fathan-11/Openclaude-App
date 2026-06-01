package com.openclaude.android.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced syntax highlighter with TreeSitter-like grammar rules for multiple languages.
 * Supports: Kotlin, Java, Python, JavaScript/TypeScript, Rust, Go, C/C++, JSON, XML, HTML, CSS, Bash, SQL.
 */
@Singleton
class SyntaxHighlighter @Inject constructor() {
    
    // Dracula-inspired color theme
    private val keywordColor = Color(0xFFBD93F9)   // Purple
    private val stringColor = Color(0xFFF1FA8C)    // Yellow
    private val commentColor = Color(0xFF6272A4)   // Gray
    private val numberColor = Color(0xFFBD93F9)    // Purple
    private val functionColor = Color(0xFF50FA7B)  // Green
    private val typeColor = Color(0xFF8BE9FD)      // Cyan
    private val annotationColor = Color(0xFFFFB86C) // Orange
    private val defaultColor = Color(0xFFF8F8F2)   // White
    private val tagColor = Color(0xFFFF79C6)       // Pink (for HTML/XML tags)
    private val attributeColor = Color(0xFF50FA7B) // Green (for HTML/XML attributes)
    private val operatorColor = Color(0xFFFF79C6)  // Pink
    private val regexColor = Color(0xFFF1FA8C)     // Yellow
    private val propertyColor = Color(0xFF66D9EF)  // Light cyan
    private val constantColor = Color(0xFFBD93F9)  // Purple
    private val builtinColor = Color(0xFF8BE9FD)   // Cyan
    
    // Language grammars
    private data class Grammar(
        val keywords: Set<String>,
        val builtins: Set<String>,
        val constants: Set<String>,
        val typeKeywords: Set<String>,
        val singleLineComment: String? = "//",
        val multiLineCommentStart: String? = "/*",
        val multiLineCommentEnd: String? = "*/",
        val stringDelimiters: List<Char> = listOf('"'),
        val charDelimiter: Char? = '\'',
        val hasTemplateStrings: Boolean = false,
        val hashLineComment: Boolean = false,
        val tagBased: Boolean = false,
    )
    
    private val kotlinGrammar = Grammar(
        keywords = setOf(
            "fun", "val", "var", "class", "object", "interface", "when", "if", "else",
            "for", "while", "return", "import", "package", "is", "as", "in", "out",
            "override", "abstract", "open", "sealed", "data", "enum", "companion",
            "suspend", "private", "public", "protected", "internal", "const", "lateinit",
            "by", "lazy", "this", "super", "try", "catch", "finally", "throw", "new",
            "break", "continue", "do", "typeof", "typealias", "inline", "noinline",
            "crossinline", "reified", "annotation", "vararg", "get", "set",
            "expect", "actual", "value", "inner", "tailrec", "operator", "infix"
        ),
        builtins = setOf("println", "print", "listOf", "mapOf", "setOf", "arrayOf", "mutableListOf", "mutableMapOf", "mutableSetOf"),
        constants = setOf("true", "false", "null", "it"),
        typeKeywords = setOf(
            "Int", "Long", "Short", "Byte", "Float", "Double", "Boolean", "Char", "String",
            "Unit", "Nothing", "Any", "Array", "List", "Map", "Set", "Pair", "Triple",
            "Sequence", "Iterable", "Collection", "ArrayList", "HashMap", "HashSet"
        ),
        hasTemplateStrings = true,
    )
    
    private val javaGrammar = Grammar(
        keywords = setOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "continue", "default", "do", "double", "else", "enum", "extends",
            "final", "finally", "float", "for", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void",
            "volatile", "while", "var", "yield", "record", "sealed", "permits"
        ),
        builtins = setOf("System", "String", "Object", "Integer", "Long", "Double", "Float", "Boolean", "Character"),
        constants = setOf("true", "false", "null"),
        typeKeywords = setOf("int", "long", "short", "byte", "float", "double", "boolean", "char", "void"),
    )
    
    private val pythonGrammar = Grammar(
        keywords = setOf(
            "and", "as", "assert", "async", "await", "break", "class", "continue", "def",
            "del", "elif", "else", "except", "finally", "for", "from", "global", "if",
            "import", "in", "is", "lambda", "nonlocal", "not", "or", "pass", "raise",
            "return", "try", "while", "with", "yield", "match", "case"
        ),
        builtins = setOf("print", "len", "range", "type", "int", "str", "float", "list", "dict", "set", "tuple", "bool", "input", "open", "isinstance", "issubclass", "super", "property", "staticmethod", "classmethod", "enumerate", "zip", "map", "filter", "sorted", "reversed", "any", "all", "min", "max", "sum", "abs", "round", "pow", "hex", "oct", "bin", "chr", "ord", "repr", "format"),
        constants = setOf("True", "False", "None", "self", "cls", "__name__", "__init__", "__main__"),
        singleLineComment = null,
        hashLineComment = true,
        stringDelimiters = listOf('"', '\''),
        hasTemplateStrings = true,
    )
    
    private val jsGrammar = Grammar(
        keywords = setOf(
            "async", "await", "break", "case", "catch", "class", "const", "continue",
            "debugger", "default", "delete", "do", "else", "export", "extends", "finally",
            "for", "from", "function", "if", "import", "in", "instanceof", "let", "new",
            "of", "return", "static", "super", "switch", "this", "throw", "try", "typeof",
            "var", "void", "while", "with", "yield", "enum", "implements", "interface",
            "package", "private", "protected", "public", "abstract", "boolean", "byte",
            "char", "double", "final", "float", "goto", "int", "long", "native", "short",
            "synchronized", "throws", "transient", "volatile", "type", "namespace", "as",
            "readonly", "keyof", "infer", "declare", "module", "require"
        ),
        builtins = setOf("console", "window", "document", "Array", "Object", "String", "Number", "Boolean", "Map", "Set", "Promise", "JSON", "Math", "Date", "RegExp", "Error", "Symbol", "Proxy", "WeakMap", "WeakSet", "setTimeout", "setInterval", "clearTimeout", "clearInterval", "fetch", "parseInt", "parseFloat"),
        constants = setOf("true", "false", "null", "undefined", "NaN", "Infinity", "this"),
        hasTemplateStrings = true,
    )
    
    private val rustGrammar = Grammar(
        keywords = setOf(
            "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else",
            "enum", "extern", "fn", "for", "if", "impl", "in", "let", "loop", "match",
            "mod", "move", "mut", "pub", "ref", "return", "self", "Self", "static",
            "struct", "super", "trait", "type", "unsafe", "use", "where", "while", "yield",
            "macro_rules", "union"
        ),
        builtins = setOf("println", "eprintln", "print", "format", "vec", "Vec", "String", "Box", "Rc", "Arc", "Option", "Result", "Some", "None", "Ok", "Err", "HashMap", "HashSet", "BTreeMap", "BTreeSet"),
        constants = setOf("true", "false"),
        singleLineComment = "//",
        multiLineCommentStart = "/*",
        multiLineCommentEnd = "*/",
    )
    
    private val goGrammar = Grammar(
        keywords = setOf(
            "break", "case", "chan", "const", "continue", "default", "defer", "else",
            "fallthrough", "for", "func", "go", "goto", "if", "import", "interface",
            "map", "package", "range", "return", "select", "struct", "switch", "type", "var"
        ),
        builtins = setOf("append", "cap", "close", "complex", "copy", "delete", "imag", "len", "make", "new", "panic", "print", "println", "real", "recover"),
        constants = setOf("true", "false", "nil", "iota"),
        typeKeywords = setOf("bool", "byte", "complex64", "complex128", "error", "float32", "float64", "int", "int8", "int16", "int32", "int64", "rune", "string", "uint", "uint8", "uint16", "uint32", "uint64", "uintptr"),
    )
    
    private val cGrammar = Grammar(
        keywords = setOf(
            "auto", "break", "case", "char", "const", "continue", "default", "do",
            "double", "else", "enum", "extern", "float", "for", "goto", "if", "inline",
            "int", "long", "register", "restrict", "return", "short", "signed", "sizeof",
            "static", "struct", "switch", "typedef", "union", "unsigned", "void", "volatile",
            "while", "_Alignas", "_Alignof", "_Atomic", "_Bool", "_Complex", "_Generic",
            "_Imaginary", "_Noreturn", "_Static_assert", "_Thread_local"
        ),
        builtins = setOf("printf", "fprintf", "sprintf", "snprintf", "scanf", "fscanf", "sscanf", "malloc", "calloc", "realloc", "free", "memcpy", "memset", "memmove", "strlen", "strcpy", "strncpy", "strcmp", "strncmp", "strcat", "strncat", "fopen", "fclose", "fread", "fwrite", "fgets", "fputs", "getc", "putc", "getchar", "putchar", "abs", "sqrt", "pow", "sin", "cos", "tan", "log", "exp", "floor", "ceil", "rand", "srand", "exit", "abort", "atexit", "system", "getenv", "atoi", "atof", "strtol", "strtod"),
        constants = setOf("NULL", "EOF", "stdin", "stdout", "stderr", "SEEK_SET", "SEEK_CUR", "SEEK_END"),
    )
    
    private val sqlGrammar = Grammar(
        keywords = setOf(
            "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP",
            "ALTER", "TABLE", "INDEX", "VIEW", "DATABASE", "SCHEMA", "INTO", "VALUES",
            "SET", "JOIN", "INNER", "LEFT", "RIGHT", "OUTER", "CROSS", "FULL", "ON",
            "AS", "AND", "OR", "NOT", "IN", "BETWEEN", "LIKE", "IS", "NULL", "EXISTS",
            "HAVING", "GROUP", "BY", "ORDER", "ASC", "DESC", "LIMIT", "OFFSET", "UNION",
            "ALL", "DISTINCT", "CASE", "WHEN", "THEN", "ELSE", "END", "BEGIN", "COMMIT",
            "ROLLBACK", "TRANSACTION", "GRANT", "REVOKE", "PRIMARY", "KEY", "FOREIGN",
            "REFERENCES", "UNIQUE", "CHECK", "DEFAULT", "AUTO_INCREMENT", "CONSTRAINT",
            "CASCADE", "RESTRICT", "TRIGGER", "FUNCTION", "PROCEDURE", "RETURN",
            "RETURNS", "DECLARE", "IF", "WHILE", "FOR", "LOOP", "EXECUTE"
        ),
        builtins = setOf("COUNT", "SUM", "AVG", "MIN", "MAX", "COALESCE", "NULLIF", "CAST", "CONVERT", "CONCAT", "SUBSTRING", "TRIM", "UPPER", "LOWER", "LENGTH", "ROUND", "FLOOR", "CEIL", "NOW", "CURRENT_TIMESTAMP", "CURRENT_DATE", "DATE", "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "ABS", "POWER", "SQRT", "ROW_NUMBER", "RANK", "DENSE_RANK", "LAG", "LEAD", "FIRST_VALUE", "LAST_VALUE"),
        constants = setOf("TRUE", "FALSE", "NULL"),
        singleLineComment = "--",
        hashLineComment = true,
    )
    
    private val htmlGrammar = Grammar(
        keywords = setOf("DOCTYPE", "html", "head", "body", "div", "span", "p", "a", "img", "ul", "ol", "li", "h1", "h2", "h3", "h4", "h5", "h6", "table", "tr", "td", "th", "form", "input", "button", "select", "option", "textarea", "script", "style", "link", "meta", "title", "header", "footer", "nav", "main", "section", "article", "aside", "figure", "figcaption", "video", "audio", "source", "canvas", "svg", "path", "br", "hr", "pre", "code", "em", "strong", "i", "b", "u", "s", "small", "sub", "sup", "mark", "abbr"),
        builtins = emptySet(),
        constants = emptySet(),
        tagBased = true,
        singleLineComment = null,
        stringDelimiters = listOf('"', '\''),
    )
    
    private val cssGrammar = Grammar(
        keywords = setOf(
            "import", "charset", "media", "keyframes", "font-face", "supports", "layer",
            "property", "namespace", "page", "container", "scope", "nest"
        ),
        builtins = setOf(
            "inherit", "initial", "unset", "revert", "auto", "none", "normal", "bold",
            "italic", "block", "inline", "flex", "grid", "absolute", "relative", "fixed",
            "sticky", "static", "hidden", "visible", "scroll", "solid", "dashed", "dotted",
            "transparent", "currentColor", "important"
        ),
        constants = emptySet(),
        singleLineComment = null,
        stringDelimiters = listOf('"', '\''),
    )
    
    private val bashGrammar = Grammar(
        keywords = setOf(
            "if", "then", "else", "elif", "fi", "case", "esac", "for", "while", "until",
            "do", "done", "in", "function", "select", "time", "coproc", "break", "continue",
            "return", "exit", "export", "declare", "typeset", "readonly", "local", "unset",
            "shift", "source", "alias", "unalias", "trap", "wait", "exec", "eval"
        ),
        builtins = setOf("echo", "printf", "read", "cd", "pwd", "ls", "cp", "mv", "rm", "mkdir", "rmdir", "touch", "cat", "grep", "sed", "awk", "find", "sort", "uniq", "wc", "head", "tail", "cut", "tr", "tee", "xargs", "chmod", "chown", "chgrp", "ln", "which", "type", "command", "builtin", "test", "true", "false", "set", "getopts", "kill", "ps", "jobs", "bg", "fg", "nohup", "date", "sleep", "basename", "dirname", "realpath", "mktemp"),
        constants = setOf("true", "false"),
        hashLineComment = true,
        singleLineComment = null,
        stringDelimiters = listOf('"', '\''),
    )
    
    private val languageGrammars = mapOf(
        "kotlin" to kotlinGrammar,
        "java" to javaGrammar,
        "python" to pythonGrammar,
        "py" to pythonGrammar,
        "javascript" to jsGrammar,
        "js" to jsGrammar,
        "typescript" to jsGrammar,
        "ts" to jsGrammar,
        "jsx" to jsGrammar,
        "tsx" to jsGrammar,
        "rust" to rustGrammar,
        "rs" to rustGrammar,
        "go" to goGrammar,
        "golang" to goGrammar,
        "c" to cGrammar,
        "cpp" to cGrammar,
        "c++" to cGrammar,
        "h" to cGrammar,
        "hpp" to cGrammar,
        "sql" to sqlGrammar,
        "html" to htmlGrammar,
        "xml" to htmlGrammar,
        "css" to cssGrammar,
        "scss" to cssGrammar,
        "sass" to cssGrammar,
        "less" to cssGrammar,
        "bash" to bashGrammar,
        "sh" to bashGrammar,
        "zsh" to bashGrammar,
        "shell" to bashGrammar,
    )
    
    fun highlight(code: String, language: String = "kotlin"): AnnotatedString {
        val grammar = languageGrammars[language.lowercase()]
        return if (grammar != null) {
            highlightWithGrammar(code, grammar)
        } else {
            // Fallback to basic keyword highlighting
            highlightBasic(code)
        }
    }
    
    private fun highlightWithGrammar(code: String, grammar: Grammar): AnnotatedString {
        return buildAnnotatedString {
            val lines = code.split("\n")
            lines.forEachIndexed { index, line ->
                if (grammar.tagBased) {
                    highlightTagLine(line, grammar)
                } else {
                    highlightLineWithGrammar(line, grammar)
                }
                if (index < lines.size - 1) append("\n")
            }
        }
    }
    
    private fun AnnotatedString.Builder.highlightLineWithGrammar(line: String, grammar: Grammar) {
        var i = 0
        while (i < line.length) {
            when {
                // Hash comments (Python, Bash, SQL)
                grammar.hashLineComment && line[i] == '#' -> {
                    withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i)) }
                    return
                }
                // Single line comments
                grammar.singleLineComment != null && line.startsWith(grammar.singleLineComment, i) -> {
                    withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i)) }
                    return
                }
                // Multi-line comments (treat as single line for now)
                grammar.multiLineCommentStart != null && line.startsWith(grammar.multiLineCommentStart, i) -> {
                    val end = line.indexOf(grammar.multiLineCommentEnd ?: "*/", i + grammar.multiLineCommentStart.length)
                    if (end != -1) {
                        withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i, end + (grammar.multiLineCommentEnd?.length ?: 2))) }
                        i = end + (grammar.multiLineCommentEnd?.length ?: 2)
                    } else {
                        withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i)) }
                        return
                    }
                }
                // Template strings (backtick)
                grammar.hasTemplateStrings && line[i] == '`' -> {
                    val end = findStringEnd(line, i + 1, '`')
                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i)) }
                        return
                    }
                }
                // Strings
                grammar.stringDelimiters.contains(line[i]) -> {
                    val end = findStringEnd(line, i + 1, line[i])
                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i)) }
                        return
                    }
                }
                // Char literals
                grammar.charDelimiter != null && line[i] == grammar.charDelimiter -> {
                    val end = line.indexOf(grammar.charDelimiter, i + 1)
                    if (end != -1 && end - i <= 4) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        append(line[i])
                        i++
                    }
                }
                // Decorators / Annotations
                line[i] == '@' -> {
                    val start = i
                    i++
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_' || line[i] == '.')) i++
                    withStyle(SpanStyle(color = annotationColor)) { append(line.substring(start, i)) }
                }
                // Numbers (hex, binary, float, int)
                line[i].isDigit() || (line[i] == '0' && i + 1 < line.length && (line[i + 1] == 'x' || line[i + 1] == 'b')) -> {
                    val start = i
                    if (line[i] == '0' && i + 1 < line.length) {
                        when (line[i + 1]) {
                            'x', 'X' -> {
                                i += 2
                                while (i < line.length && (line[i].isDigit() || line[i] in 'a'..'f' || line[i] in 'A'..'F')) i++
                            }
                            'b', 'B' -> {
                                i += 2
                                while (i < line.length && (line[i] == '0' || line[i] == '1')) i++
                            }
                            else -> {
                                while (i < line.length && (line[i].isDigit() || line[i] == '.' || line[i] == '_')) i++
                            }
                        }
                    } else {
                        while (i < line.length && (line[i].isDigit() || line[i] == '.' || line[i] == '_' || line[i] == 'e' || line[i] == 'E')) i++
                        if (i < line.length && (line[i] == 'f' || line[i] == 'F' || line[i] == 'L' || line[i] == 'l' || line[i] == 'u' || line[i] == 'U')) i++
                    }
                    withStyle(SpanStyle(color = numberColor)) { append(line.substring(start, i)) }
                }
                // Words (keywords, identifiers, types)
                line[i].isLetter() || line[i] == '_' -> {
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    val word = line.substring(start, i)
                    val color = when {
                        grammar.keywords.contains(word) -> keywordColor
                        grammar.typeKeywords.contains(word) -> typeColor
                        grammar.constants.contains(word) -> constantColor
                        grammar.builtins.contains(word) -> builtinColor
                        word.first().isUpperCase() && word.length > 1 -> typeColor
                        i < line.length && line[i] == '(' -> functionColor
                        else -> defaultColor
                    }
                    withStyle(SpanStyle(color = color)) { append(word) }
                }
                // Operators
                line[i] in "+-*/%=<>!&|^~?:." -> {
                    withStyle(SpanStyle(color = operatorColor, fontWeight = FontWeight.Bold)) { append(line[i]) }
                    i++
                }
                else -> {
                    append(line[i])
                    i++
                }
            }
        }
    }
    
    private fun AnnotatedString.Builder.highlightTagLine(line: String, grammar: Grammar) {
        var i = 0
        while (i < line.length) {
            when {
                // HTML comments
                line.startsWith("<!--", i) -> {
                    val end = line.indexOf("-->", i + 4)
                    if (end != -1) {
                        withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i, end + 3)) }
                        i = end + 3
                    } else {
                        withStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic)) { append(line.substring(i)) }
                        return
                    }
                }
                // Strings
                line[i] == '"' || line[i] == '\'' -> {
                    val end = findStringEnd(line, i + 1, line[i])
                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i)) }
                        return
                    }
                }
                // Tags
                line[i] == '<' -> {
                    withStyle(SpanStyle(color = operatorColor)) { append(line[i]) }
                    i++
                    if (i < line.length && line[i] == '/') {
                        withStyle(SpanStyle(color = operatorColor)) { append(line[i]) }
                        i++
                    }
                    // Tag name
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '-' || line[i] == '_')) i++
                    if (i > start) {
                        withStyle(SpanStyle(color = tagColor, fontWeight = FontWeight.Bold)) { append(line.substring(start, i)) }
                    }
                }
                line[i] == '>' -> {
                    withStyle(SpanStyle(color = operatorColor)) { append(line[i]) }
                    i++
                }
                // Attributes
                line[i].isLetter() || line[i] == '-' || line[i] == '_' -> {
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '-' || line[i] == '_' || line[i] == ':')) i++
                    val word = line.substring(start, i)
                    // Check if followed by = to identify attribute
                    val rest = line.substring(i).trimStart()
                    if (rest.startsWith("=")) {
                        withStyle(SpanStyle(color = attributeColor)) { append(word) }
                    } else if (grammar.keywords.contains(word.lowercase())) {
                        withStyle(SpanStyle(color = tagColor)) { append(word) }
                    } else {
                        withStyle(SpanStyle(color = defaultColor)) { append(word) }
                    }
                }
                else -> {
                    append(line[i])
                    i++
                }
            }
        }
    }
    
    private fun findStringEnd(line: String, start: Int, delimiter: Char): Int {
        var i = start
        while (i < line.length) {
            if (line[i] == '\\' && i + 1 < line.length) {
                i += 2 // Skip escaped character
                continue
            }
            if (line[i] == delimiter) return i
            i++
        }
        return -1
    }
    
    private fun highlightBasic(code: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)
        }
    }
    
    fun detectLanguage(filename: String): String {
        return when {
            filename.endsWith(".kt") -> "kotlin"
            filename.endsWith(".java") -> "java"
            filename.endsWith(".py") -> "python"
            filename.endsWith(".js") -> "javascript"
            filename.endsWith(".ts") -> "typescript"
            filename.endsWith(".jsx") -> "jsx"
            filename.endsWith(".tsx") -> "tsx"
            filename.endsWith(".rs") -> "rust"
            filename.endsWith(".go") -> "go"
            filename.endsWith(".c") -> "c"
            filename.endsWith(".cpp") || filename.endsWith(".cxx") || filename.endsWith(".cc") -> "cpp"
            filename.endsWith(".h") || filename.endsWith(".hpp") -> "c"
            filename.endsWith(".xml") -> "xml"
            filename.endsWith(".json") -> "json"
            filename.endsWith(".md") -> "markdown"
            filename.endsWith(".html") || filename.endsWith(".htm") -> "html"
            filename.endsWith(".css") -> "css"
            filename.endsWith(".scss") -> "scss"
            filename.endsWith(".less") -> "less"
            filename.endsWith(".sh") || filename.endsWith(".bash") -> "bash"
            filename.endsWith(".zsh") -> "zsh"
            filename.endsWith(".sql") -> "sql"
            filename.endsWith(".yaml") || filename.endsWith(".yml") -> "yaml"
            filename.endsWith(".toml") -> "toml"
            filename.endsWith(".gradle.kts") || filename.endsWith(".gradle") -> "kotlin"
            filename.endsWith(".dockerfile") || filename == "Dockerfile" -> "bash"
            filename.endsWith(".rb") -> "ruby"
            filename.endsWith(".swift") -> "swift"
            filename.endsWith(".dart") -> "dart"
            filename.endsWith(".php") -> "php"
            else -> "text"
        }
    }
}
