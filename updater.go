package main

import (
	"archive/zip"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"os/user"
	"path/filepath"
	"runtime"
)

type Updater struct{}

const URL string = "https://bitbucket.org/drvirtuozov/minecraft-client-mods-1710/get/master.zip"

func (u *Updater) UpdateMods() {
	go func() {
		minePath := getMinepath()
		modsPath := filepath.Join(minePath, "mods")

		if !isExist(minePath) {
			fmt.Println("Minecraft not installed")
			return
		}

		file := downloadZip()
		defer file.Close()
		defer os.Remove(file.Name())
		removeDir(modsPath)
		unzip(file, modsPath)
	}()
}

func downloadZip() *os.File {
	fmt.Println("Downloading new mods...")
	res, err := http.Get(URL)
	checkError(err)
	defer res.Body.Close()
	file, err := ioutil.TempFile("", "minecraft-mods-")
	checkError(err)
	data, err := ioutil.ReadAll(res.Body)
	checkError(err)
	file.Write(data)
	return file
}

func unzip(zipFile *os.File, destPath string) {
	if !isExist(destPath) {
		err := os.MkdirAll(destPath, 0777)
		checkError(err)
	}

	stat, err := zipFile.Stat()
	checkError(err)
	zipReader, err := zip.NewReader(zipFile, stat.Size())
	checkError(err)

	for _, file := range zipReader.File {
		if !file.FileInfo().IsDir() {
			fmt.Println("Extracting...", filepath.Join(destPath, file.FileInfo().Name()))
			writer, err := os.Create(filepath.Join(destPath, file.FileInfo().Name()))
			checkError(err)
			defer writer.Close()
			reader, err := file.Open()
			checkError(err)
			defer reader.Close()
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

func getOSUsername() string {
	user, err := user.Current()
	checkError(err)
	return user.Username
}

func getMinepath() string {
	switch runtime.GOOS {
	case "linux":
		return "/home/" + getOSUsername() + "/.minecraft/"
	case "windows":
		return "C:\\Users\\" + getOSUsername() + "\\AppData\\Roaming\\.minecraft\\"
	case "darwin":
		return "/Users/" + getOSUsername() + "/Library/Application Support/minecraft/"
	default:
		panic("Unable to detect os")
	}
}

func isExist(path string) bool {
	if _, err := os.Stat(path); err != nil {
		if os.IsNotExist(err) {
			return false
		}

		checkError(err)
	}

	return true
}

func removeDir(path string) {
	fmt.Println("Removing old mods...")
	err := os.RemoveAll(path)
	checkError(err)
}
