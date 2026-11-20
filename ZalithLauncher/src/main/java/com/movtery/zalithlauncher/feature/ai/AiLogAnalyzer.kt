package com.movtery.zalithlauncher.feature.ai

import com.movtery.zalithlauncher.utils.path.PathManager
import java.io.File

/**
 * 启动日志分析辅助：读取最近一次启动日志、脱敏并生成摘要。
 *
 * 需求 10.9：自动隐藏 Access Token / Player Token / API Key / UUID / 路径用户名，
 * 输出可能原因与建议修复步骤所需的原文片段。
 */
object AiLogAnalyzer {

    /** 读取最近一次的启动器日志文件。 */
    fun latestLogFile(): File? {
        val dir = File(PathManager.Companion.DIR_LAUNCHER_LOG)
        return dir.listFiles { f ->
            f.isFile && f.name.startsWith("log") && f.name.endsWith(".txt")
        }?.maxByOrNull { it.lastModified() }
    }

    /** 读取、脱敏并生成用于分析的摘要文本。 */
    fun analyze(manualText: String? = null): Result<String> = runCatching {
        val raw = manualText?.takeIf { it.isNotBlank() }
            ?: latestLogFile()?.readText()
            ?: throw IllegalStateException("No launch log available")

        // 先脱敏，再摘要，避免摘要片段泄露敏感信息
        val masked = AiSafety.maskSensitive(raw)
        summarize(masked)
    }

    /**
     * 摘要策略：保留前若干行环境信息 + 所有 ERROR/WARN/Exception/Caused by 行，
     * 限制总长度避免超出上下文。
     */
    private fun summarize(masked: String): String {
        val lines = masked.lineSequence().toList()
        val header = lines.take(20)
        val problemLines = lines.filter { line ->
            val u = line.uppercase()
            u.contains("ERROR") || u.contains("WARN") ||
                u.contains("EXCEPTION") || u.contains("CAUSED BY") ||
                u.contains("FATAL") || u.contains("CRASH")
        }.takeLast(120)

        val sb = StringBuilder()
        sb.append(header.joinToString("\n"))
        sb.append("\n\n---- Key diagnostics (auto extracted) ----\n")
        sb.append(if (problemLines.isEmpty()) "No explicit ERROR/WARN lines found." else problemLines.joinToString("\n"))
        val out = sb.toString()
        return if (out.length > 12000) out.takeLast(12000) else out
    }
}
