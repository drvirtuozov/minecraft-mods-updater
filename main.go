package main

import (
	"os"

	"github.com/therecipe/qt/widgets"
)

var Button *widgets.QPushButton
var Label *widgets.QLabel

func main() {
	widgets.NewQApplication(len(os.Args), os.Args)

	window := widgets.NewQMainWindow(nil, 0)
	window.SetWindowTitle("Minecraft Mods Updater")
	window.SetMinimumSize2(200, 100)
	window.SetMaximumSize2(200, 100)

	layout := widgets.NewQVBoxLayout()

	widget := widgets.NewQWidget(nil, 0)
	widget.SetLayout(layout)

	Button = widgets.NewQPushButton2("Update Mods", nil)
	Button.SetFixedHeight(60)
	Button.ConnectClicked(func(checked bool) {
		go func() {
			err := UpdateMods()

			if err != nil {
				Label.SetText(err.Error())
			}
		}()
	})

	Label = widgets.NewQLabel2("", nil, window.WindowType())

	layout.AddWidget(Button, 0, 0)
	layout.AddWidget(Label, 0, 0)
	window.SetCentralWidget(widget)
	window.Show()

	widgets.QApplication_Exec()
}
