import QtQuick 2.2
import QtQuick.Controls 1.1
import QtQuick.Layouts 1.0


ApplicationWindow {
  visible: true
  title: "Minecraft Mods Updater"
  property int margin: 11
  width: 200
  height: 100
  minimumWidth: 200
  minimumHeight: 100
  maximumWidth: 200
  maximumHeight: 100

  ColumnLayout {
    id: mainLayout
    anchors.fill: parent
    anchors.margins: margin

    Button {
      id: button
      text: "Update Mods"
      Layout.fillWidth: true
      Layout.fillHeight: true
      onClicked: updater.updateMods()
    }

    Label {
      text: ""
    }
  }
}