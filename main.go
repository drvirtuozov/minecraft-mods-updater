package main

import (
	"fmt"
	"os"

	"github.com/go-qml/qml"
)

func main() {
	if err := qml.Run(run); err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}
}

func run() error {
	engine := qml.NewEngine()
	context := engine.Context()
	context.SetVar("updater", &Updater{})
	controls, err := engine.LoadFile("qml/app.qml")

	if err != nil {
		return err
	}

	window := controls.CreateWindow(nil)

	window.Show()
	window.Wait()
	return nil
}
