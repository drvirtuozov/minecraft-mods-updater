package main

import (
	"archive/zip"
	"errors"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"os/user"
	"path/filepath"
	"runtime"
	"strconv"
)

const URL string = "https://bitbucket.org/drvirtuozov/minecraft-client-mods-1710/get/master.zip"

func UpdateMods() error {
	Button.SetText("Updating...")
	Button.SetEnabled(false)
	defer Button.SetText("Update Mods")
	defer Button.SetEnabled(true)
	minePath, err := getMinepath()

	if err != nil {
		return err
	}

	modsPath := filepath.Join(minePath, "mods")
	exists, err := isExist(minePath)

	if err != nil {
		return err
	}

	if !exists {
		return errors.New("Minecraft not installed")
	}

	Label.SetText("Downloading new mods...")
	file, err := downloadZip()

	if err != nil {
		return err
	}

	defer file.Close()
	defer os.Remove(file.Name())
	Label.SetText("Removing old mods...")
	err = removeDir(modsPath)

	if err != nil {
		return err
	}

	err = unzip(file, modsPath)

	if err != nil {
		return err
	}

	Label.SetText("Done!")
	return nil
}

func downloadZip() (*os.File, error) {
	res, err := http.Get(URL)

	if err != nil {
		return nil, err
	}

	defer res.Body.Close()
	file, err := ioutil.TempFile("", "minecraft-mods-")

	if err != nil {
		return nil, err
	}

	data, err := ioutil.ReadAll(res.Body)

	if err != nil {
		return nil, err
	}

	file.Write(data)
	return file, nil
}

func unzip(zipFile *os.File, destPath string) error {
	exists, err := isExist(destPath)

	if err != nil {
		return err
	}

	if !exists {
		err := os.MkdirAll(destPath, 0777)

		if err != nil {
			return err
		}
	}

	stat, err := zipFile.Stat()

	if err != nil {
		return err
	}

	zipReader, err := zip.NewReader(zipFile, stat.Size())

	if err != nil {
		return err
	}

	for i, file := range zipReader.File {
		if !file.FileInfo().IsDir() {
			Label.SetText("Extracting... " + strconv.Itoa(i+1) + " of " + strconv.Itoa(len(zipReader.File)) + " files")
			writer, err := os.Create(filepath.Join(destPath, file.FileInfo().Name()))

			if err != nil {
				return err
			}

			defer writer.Close()
			reader, err := file.Open()

			if err != nil {
				return err
			}

			defer reader.Close()
			_, err = io.Copy(writer, reader)

			if err != nil {
				return err
			}
		}
	}

	return nil
}

func getOSUsername() (string, error) {
	user, err := user.Current()

	if err != nil {
		return "", err
	}

	return user.Username, nil
}

func getMinepath() (string, error) {
	username, err := getOSUsername()

	if err != nil {
		return "", err
	}

	switch runtime.GOOS {
	case "linux":
		return "/home/" + username + "/.minecraft/", nil
	case "windows":
		return "C:\\Users\\" + username + "\\AppData\\Roaming\\.minecraft\\", nil
	case "darwin":
		return "/Users/" + username + "/Library/Application Support/minecraft/", nil
	default:
		return "", errors.New("Unable to detect os")
	}
}

func isExist(path string) (bool, error) {
	if _, err := os.Stat(path); err != nil {
		if os.IsNotExist(err) {
			return false, nil
		}

		return false, err
	}

	return true, nil
}

func removeDir(path string) error {
	err := os.RemoveAll(path)

	if err != nil {
		return err
	}

	return nil
}
