package main

import (
	"fmt"
	"os"

	"github.com/go-qml/qml"
)

var Button qml.Object
var Label qml.Object

func main() {
	if err := qml.Run(run); err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}
}

func run() error {
	engine := qml.NewEngine()
	context := engine.Context()
	context.SetVar("updater", new(Updater))
	component, err := engine.LoadFile("window.qml")

	if err != nil {
		return err
	}

	window := component.CreateWindow(nil)
	layout := window.ObjectByName("mainLayout")
	Button = layout.ObjectByName("button")
	Label = layout.ObjectByName("label")
	window.Show()
	window.Wait()
	return nil
}
