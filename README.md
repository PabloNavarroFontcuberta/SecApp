# 🔐 SecApp

Una aplicación Android desarrollada con **Jetpack Compose** que lista todas las apps instaladas 📱 y muestra los **permisos que solicita cada una** 🔍. También compara los permisos actuales con los almacenados anteriormente en un archivo JSON para detectar cambios 🔁.

---

## ✨ Características

- 🔎 **Escanea todas las apps instaladas** en el dispositivo.
- 🛂 **Muestra los permisos** que cada aplicación solicita.
- 📁 **Genera un archivo JSON** con los permisos y lo guarda localmente.
- 🔄 **Compara automáticamente** si los permisos han cambiado desde la última vez.
- 👀 Botón para **abrir el archivo JSON** generado con cualquier visor compatible.

---

## 📸 Interfaz de usuario

- Lista interactiva con tarjetas para cada app 📋.
- Expansión para ver todos los permisos de forma detallada ➕.
- Íconos de aplicaciones mostrados junto al nombre y package 🖼️.

---

## 🛠️ Tecnologías

- 🧩 **Kotlin**
- 🧵 **Jetpack Compose**
- 🎨 Material3
- 📦 `PackageManager`
- 🧾 `kotlinx.serialization` para JSON
- 🗂️ `FileProvider` para compartir el archivo

---

## 🚀 Cómo ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/PabloNavarroFontcuberta/SecApp.git


##Ábrelo en Android Studio 📱.
Compila y ejecuta en un dispositivo físico (preferiblemente con muchas apps instaladas) ⚙️.

##🧪 Uso
Al iniciar, escaneará automáticamente las apps y generará el JSON.

Si los permisos han cambiado desde la última ejecución, verás una notificación 📣.

Pulsa en "Ver JSON generado" para revisar el archivo en tu app favorita 📖.

## 📂 Estructura del archivo JSON
   ```bash
    {
      "nombre": "WhatsApp",
      "paquete": "com.whatsapp",
      "permisos": [
        "android.permission.CAMERA",
        "android.permission.READ_CONTACTS"
      ]
    }

## 📌 Nota de seguridad
Esta app no modifica nada, solo lee y muestra los permisos de apps instaladas, útil para auditorías personales 🕵️‍♂️.

## 🧑‍💻 Autor
## 👨‍💻 Pablo Navarro Fontcuberta
## 📍 GitHub: @PabloNavarroFontcuberta

