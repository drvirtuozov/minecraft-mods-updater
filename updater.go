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

type PassThruReader struct {
	io.Reader
	total  int64
	length int64
}

func (pt *PassThruReader) Read(p []byte) (int, error) {
	n, err := pt.Reader.Read(p)

	if n > 0 {
		pt.total += int64(n)
		percentage := float64(pt.total) / float64(pt.length) * float64(100)
		Label.SetText("Downloading new mods... " + strconv.Itoa(int(percentage)) + "%")
	}

	return n, err
}

func UpdateMods() error {
	Button.SetText("Updating...")
	Button.SetEnabled(false)
	Label.SetText("Removing old mods...")
	defer Button.SetText("Update Mods")
	defer Button.SetEnabled(true)
	defer Label.SetText("Done!")
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
		return errors.New("Minecraft is not installed")
	}

	file, err := downloadZip()

	if err != nil {
		return err
	}

	defer file.Close()
	defer os.Remove(file.Name())

	if err := removeDir(modsPath); err != nil {
		return err
	}

	if err := unzip(file, modsPath); err != nil {
		return err
	}

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

	reader := &PassThruReader{Reader: res.Body, length: res.ContentLength}
	data, err := ioutil.ReadAll(reader)

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
		if err := os.MkdirAll(destPath, 0777); err != nil {
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

			if _, err = io.Copy(writer, reader); err != nil {
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
	if err := os.RemoveAll(path); err != nil {
		return err
	}

	return nil
}
