package be.doji.productivity.trambuapp.styles

import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class DefaultTrambuStyle : Stylesheet() {
    companion object {
        val heading by cssclass()
        val todo by cssclass()
        val done by cssclass()
        val alert by cssclass()
        val itemtitle by cssclass()

        val mainColor = c("#505050")
        val defaultTextColor = c("#ffffff")
    }

    init {

        root {
            baseColor = mainColor
            backgroundColor += mainColor
            textFill = defaultTextColor
        }

        todo {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor

            and(itemtitle) {
                backgroundInsets += box(0.px, 1.px, 2.px, 2.px)
                backgroundRadius += box(5.px, 5.px, 0.px, 0.px)
                padding = box(0.166667.em, 0.833333.em, 0.25.em, 0.833333.em)
                backgroundColor += c("#3598c1")
                baseColor = c("#3598c1")
                textFill = defaultTextColor
                fontStyle = FontPosture.REGULAR
                fontWeight = FontWeight.BOLD
            }
        }

        done {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor

            and(itemtitle) {
                backgroundInsets += box(0.px, 1.px, 2.px, 2.px)
                backgroundRadius += box(5.px, 5.px, 0.px, 0.px)
                padding = box(0.166667.em, 0.833333.em, 0.25.em, 0.833333.em)
                backgroundColor += c("#383f38")
                baseColor = c("#383f38")
                textFill = c("#969696")
                fontStyle = FontPosture.ITALIC
            }
        }

        alert {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor

            and(itemtitle) {
                backgroundInsets += box(0.px, 1.px, 2.px, 2.px)
                backgroundRadius += box(5.px, 5.px, 0.px, 0.px)
                padding = box(0.166667.em, 0.833333.em, 0.25.em, 0.833333.em)
                backgroundColor += c("#a8431a")
                baseColor = c("#a8431a")
                textFill = defaultTextColor
            }
        }

    }
}