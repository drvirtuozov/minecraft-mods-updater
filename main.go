package main

import (
	"os"

	"github.com/therecipe/qt/widgets"
)

var button *widgets.QPushButton
var label *widgets.QLabel

func main() {
	widgets.NewQApplication(len(os.Args), os.Args)
	window := widgets.NewQMainWindow(nil, 0)
	window.SetWindowTitle("Minecraft Mods Updater")
	window.SetMinimumSize2(200, 100)
	window.SetMaximumSize2(200, 100)
	layout := widgets.NewQVBoxLayout()
	widget := widgets.NewQWidget(nil, 0)
	widget.SetLayout(layout)
	button = widgets.NewQPushButton2("Update Mods", nil)
	button.SetFixedHeight(60)

	button.ConnectClicked(func(checked bool) {
		go func() {
			if err := updateMods(); err != nil {
				label.SetText(err.Error())
			}
		}()
	})

	label = widgets.NewQLabel2("", nil, window.WindowType())
	layout.AddWidget(button, 0, 0)
	layout.AddWidget(label, 0, 0)
	window.SetCentralWidget(widget)
	window.Show()
	widgets.QApplication_Exec()
}
