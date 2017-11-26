package be.doji.productivity.trambuapp.styles

import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class DefaultTrambuStyle : Stylesheet() {
    companion object {
        // Define css classes
        val heading by cssclass()

        // Define colors
        val mainColor = c("#bdbd22")
    }

    init {

    }
}