# Solución para el error "Error al iniciar sesión con Google"

Para solucionar el problema del inicio de sesión con Google en tu aplicación CUCEI Verde, necesitas seguir estos pasos:

## 1. Obtener la firma SHA-1 de tu aplicación

1. Abre una terminal en Android Studio (View > Tool Windows > Terminal)
2. Ejecuta uno de los siguientes comandos dependiendo de si quieres el certificado de depuración o de lanzamiento:

   **Para certificado de depuración (desarrollo):**
   ```
   ./gradlew signingReport
   ```
   o en Windows:
   ```
   gradlew signingReport
   ```

   **Alternativamente**, puedes usar el siguiente comando para el certificado de depuración:
   ```
   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```

3. Busca la línea que dice `SHA1:` y copia el valor (ejemplo: `SHA1: 43:BB:D5:FF:E1:5B:F8:86:BE:31:D2:AF:ED:14:C2:E8:D3:AE:D5:AB`)

## 2. Agregar la firma SHA-1 a Firebase

1. Ve a la [consola de Firebase](https://console.firebase.google.com/)
2. Selecciona tu proyecto "cucei-verde"
3. Ve a Project settings (⚙️) > Your apps > Android apps 
4. Selecciona la aplicación con el paquete "cuceiverdecom.example"
5. Haz clic en "Add fingerprint" o "Agregar huella digital"
6. Pega la firma SHA-1 que copiaste (sin los dos puntos)
7. Guarda los cambios

## 3. Descargar el nuevo archivo google-services.json

1. En la misma página, haz clic en "Download google-services.json"
2. Reemplaza el archivo existente en tu proyecto (app/google-services.json)

## 4. Reconstruir la aplicación

1. En Android Studio, selecciona Build > Clean Project
2. Luego selecciona Build > Rebuild Project
3. Ejecuta tu aplicación nuevamente

Con estos pasos, el inicio de sesión con Google debería funcionar correctamente.

## Información de Depuración

Hemos agregado código de diagnóstico que te ayudará a identificar problemas específicos. Cuando intentes iniciar sesión con Google, verás mensajes más detallados en el LogCat de Android Studio que te indicarán exactamente qué está fallando.

Busca los mensajes con la etiqueta "GoogleSignIn" para obtener información sobre el proceso de inicio de sesión.
