package br.com.source.model.service

import java.awt.Desktop
import java.lang.Exception
import java.net.URI
import java.util.*

class BrowserService {

    fun openInBrowser(uri: URI) {
        try {
            val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name").lowercase(Locale.getDefault()) }
            val desktop = Desktop.getDesktop()
            when {
                Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(uri)
                "mac" in osName -> Runtime.getRuntime().exec("open $uri")
                "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec("xdg-open $uri")
                else -> throw RuntimeException("cannot open $uri")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}