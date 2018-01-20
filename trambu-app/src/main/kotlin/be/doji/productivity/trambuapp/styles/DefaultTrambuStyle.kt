package be.doji.productivity.trambuapp.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class DefaultTrambuStyle : Stylesheet() {
    companion object {
        val todo by cssclass()
        val done by cssclass()
        val alert by cssclass()
        val separatorLabel by cssclass()
        val warningLabel by cssclass()
        val default by cssclass()
        val buttonBold by cssclass()
        val activityOverlay by cssclass()

        val mainColor = c("#505050")
        val defaultTextColor = c("#ffffff")
    }

    init {
        root {
            baseColor = mainColor
            backgroundColor += c("transparent")
            textFill = defaultTextColor
            backgroundImage += this.javaClass.classLoader.getResource("images/repeating-pattern-rocks-opace.png").toURI()
            backgroundPosition += BackgroundPosition.CENTER
        }

        todo {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor
            backgroundColor += c("#3598c1")
            baseColor = c("#3598c1")

            separatorLabel {
                textFill = c("#820d98")
            }
        }

        activityOverlay {
            baseColor = mainColor
            backgroundColor += c("#8598a6")
            baseColor = c("#8598a6")
        }


        done {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor
            backgroundColor += c("#383f38")
            baseColor = c("#383f38")
            title { fontStyle = FontPosture.ITALIC }
        }

        alert {
            skin = com.sun.javafx.scene.control.skin.TitledPaneSkin::class
            textFill = defaultTextColor
            backgroundColor += c("#a8431a")
            baseColor = c("#a8431a")
        }

        separatorLabel {
            textFill = c("orange")
        }

        warningLabel {
            textFill = c("#a8431a")
        }

        default {
            baseColor = mainColor
            backgroundColor += c(mainColor.toString(), 0.65)
        }

        splitPane {
            backgroundColor += c(mainColor.toString(), 0.65)
        }

        scrollPane {
            backgroundColor += c("transparent")
            baseColor = c("transparent")
            content {
                backgroundColor += c("transparent")
            }

        }

        accordion {
            backgroundColor += c("transparent")

            titledPane {
                content {
                    backgroundColor += c("transparent")
                }

            }
        }
    }
}