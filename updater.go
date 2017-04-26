package main

import (
	"archive/zip"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

type Updater struct{}

const url string = "https://bitbucket.org/drvirtuozov/minecraft-client-mods-1710/get/master.zip"

func (u *Updater) UpdateMods() {
	go func() {
		//downloadZip(url)
		unzip("test.zip", filepath.Join(dirname(), "output"))
	}()
}

func downloadZip(url string) *os.File {
	res, err := http.Get(url)
	checkError(err)
	file, err := os.Create("mods.zip")
	defer file.Close()
	checkError(err)
	err = res.Write(file)
	checkError(err)
	return file
}

func unzip(srcPath string, destPath string) {
	if _, err := os.Stat(destPath); err != nil {
		if os.IsNotExist(err) {
			err := os.MkdirAll(destPath, 0777)
			checkError(err)
		}
	}

	zipReader, err := zip.OpenReader(srcPath)
	defer zipReader.Close()
	checkError(err)

	for _, file := range zipReader.File {
		if !file.FileInfo().IsDir() {
			fmt.Println("Unzipping...", filepath.Join(destPath, file.FileInfo().Name()))
			writer, err := os.Create(filepath.Join(destPath, file.FileInfo().Name()))
			defer writer.Close()
			checkError(err)
			reader, err := file.Open()
			defer reader.Close()
			checkError(err)
			_, err = io.Copy(writer, reader)
			checkError(err)
		}
	}
}

func checkError(e error) {
	if e != nil {
		panic(e)
	}
}

func dirname() string {
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	checkError(err)
	return dir
}
