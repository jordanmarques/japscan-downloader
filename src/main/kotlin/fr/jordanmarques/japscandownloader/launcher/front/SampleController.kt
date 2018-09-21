package fr.jordanmarques.japscandownloader.launcher.front

import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.input.MouseEvent

class SampleController {
    lateinit var radioButtonsGroup: ToggleGroup
    lateinit var fullMode: RadioButton
    lateinit var rangeMode: RadioButton
    lateinit var chapterMode: RadioButton

    lateinit var nameInput: TextField
    lateinit var prefixInput: TextField
    lateinit var fromInput: TextField
    lateinit var toInput: TextField
    lateinit var chapterInput: TextField

    lateinit var downloadButton: Button


    fun download(mouseEvent: MouseEvent) {

    }

}
