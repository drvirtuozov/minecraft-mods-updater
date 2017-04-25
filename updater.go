package main

import "net/http"
import "os"

type Updater struct{}

const url string = "https://bitbucket.org/drvirtuozov/minecraft-client-mods-1710/get/master.zip"

func (u *Updater) UpdateMods() {
	go func() {
		downloadZip(url)
	}()
}

func downloadZip(url string) (*os.File, error) {
	res, err := http.Get(url)

	if err != nil {
		return nil, err
	}

	file, err := os.Create("mods.zip")

	if err != nil {
		return nil, err
	}

	err = res.Write(file)

	if err != nil {
		return nil, err
	}

	return file, nil
}
