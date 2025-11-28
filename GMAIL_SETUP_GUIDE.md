# 📧 Guía de Configuración de Gmail SMTP

Esta guía te ayudará a configurar el envío de emails usando Gmail SMTP en el sistema de notificaciones de la clínica veterinaria.

---

## 🔐 Paso 1: Obtener Contraseña de Aplicación de Google

Google no permite usar tu contraseña normal para aplicaciones de terceros por seguridad. Debes generar una **"Contraseña de Aplicación"**.

### Instrucciones:

1. **Ir a tu cuenta de Google**
   - Abre tu navegador y ve a: https://myaccount.google.com/

2. **Ir a la sección de Seguridad**
   - En el menú lateral izquierdo, haz clic en "Seguridad"

3. **Activar Verificación en 2 pasos** (si no está activa)
   - Busca la sección "Cómo inicias sesión en Google"
   - Haz clic en "Verificación en 2 pasos"
   - Sigue las instrucciones para activarla (necesitarás tu teléfono)

4. **Generar Contraseña de Aplicación**
   - Vuelve a la página de Seguridad
   - Busca "Contraseñas de aplicaciones" (aparece después de activar 2FA)
   - Haz clic en "Contraseñas de aplicaciones"
   - En "Seleccionar app", elige "Correo"
   - En "Seleccionar dispositivo", elige "Otro (nombre personalizado)"
   - Escribe: "Veterinaria App"
   - Haz clic en "Generar"

5. **Copiar la contraseña**
   - Google mostrará una contraseña de 16 caracteres (ejemplo: `abcd efgh ijkl mnop`)
   - Cópiala (puedes copiarla con o sin espacios)
   - **⚠️ IMPORTANTE:** Esta contraseña solo se muestra UNA VEZ. Guárdala bien.

---

## ⚙️ Paso 2: Configurar el Proyecto

### 2.1. Editar `application-local.yml`

Abre el archivo `src/main/resources/application-local.yml` y actualiza:

```yaml
spring:
  mail:
    username: tucorreo@gmail.com              # 🔴 Tu email de Gmail
    password: abcd efgh ijkl mnop             # 🔴 Tu contraseña de aplicación
```

**Ejemplo real:**
```yaml
spring:
  mail:
    username: veterinaria.humboldt@gmail.com
    password: xmkp qwer tyui asdf
```

### 2.2. Configurar la Base de Datos

Abre PostgreSQL y ejecuta:

```bash
# En Windows (PowerShell)
psql -U vet_admin -d veterinaria_db -f init-scripts/configurar-canal-email.sql

# O conéctate manualmente
psql -U vet_admin -d veterinaria_db
```

Luego edita y ejecuta las líneas del script, cambiando `tu-email@gmail.com` por tu email real:

```sql
-- ... (copiar el contenido del script SQL)
-- Recuerda cambiar 'tu-email@gmail.com' por tu email real
```

---

## 🚀 Paso 3: Ejecutar el Proyecto

### Opción A: Desde la terminal

```bash
cd "C:\Users\Lab Ingenieria 12\Downloads\proyectoVeterinaria"

# Compilar el proyecto
mvn clean install

# Ejecutar con el perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Opción B: Con variables de entorno

En PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:GMAIL_USERNAME="tucorreo@gmail.com"
$env:GMAIL_APP_PASSWORD="xmkp qwer tyui asdf"

mvn spring-boot:run
```

### Opción C: Desde IntelliJ IDEA

1. Abrir el proyecto en IntelliJ
2. Ir a: Run → Edit Configurations
3. En "Active profiles" escribir: `local`
4. Click OK
5. Run o Debug el proyecto

---

## 🧪 Paso 4: Probar el Envío de Emails

### Opción 1: Desde Postman

**1. Hacer login para obtener el token:**

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "usuario": "secretario1",
  "contrasena": "Password123!"
}
```

Copia el token JWT de la respuesta.

**2. Enviar una notificación de prueba:**

```http
POST http://localhost:8080/api/notificaciones/enviar
Content-Type: application/json
Authorization: Bearer TU_TOKEN_AQUI

{
  "tipo": "prueba_email",
  "mensaje": "Este es un email de prueba desde la clínica veterinaria. Si recibes este mensaje, el sistema de notificaciones está funcionando correctamente.",
  "canalId": 1
}
```

### Opción 2: Desde cURL (PowerShell)

```powershell
# Login
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"usuario": "secretario1", "contrasena": "Password123!"}'

$token = $response.token

# Enviar notificación
Invoke-RestMethod -Uri "http://localhost:8080/api/notificaciones/enviar" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{Authorization = "Bearer $token"} `
    -Body '{
        "tipo": "prueba_email",
        "mensaje": "Email de prueba desde PowerShell",
        "canalId": 1
    }'
```

### Opción 3: Desde el Frontend

1. Iniciar el frontend: `cd frontend; npm run dev`
2. Hacer login en `http://localhost:5173`
3. Ir al módulo de "Notificaciones"
4. Click en "Enviar Notificación"
5. Llenar el formulario y enviar

---

## ✅ Verificación

### Logs Esperados en la Consola del Backend

Si todo funciona correctamente, deberías ver:

```
✅ JavaMailSender configurado en 1 canales de email
...
✅ Email enviado exitosamente a través de smtp.gmail.com
   → Para: tucorreo@gmail.com
   → Asunto: 🐾 Clínica Veterinaria - Prueba Email
```

### Revisar tu Email

1. Abre Gmail
2. Revisa tu bandeja de entrada (o spam)
3. Deberías ver un email con el asunto: **"🐾 Clínica Veterinaria - Prueba Email"**

---

## 🐛 Solución de Problemas

### Error: "Username and Password not accepted"

**Problema:** La contraseña no es correcta o no es una contraseña de aplicación.

**Solución:**
- Asegúrate de usar una **contraseña de aplicación**, NO tu contraseña de Gmail normal
- Verifica que la verificación en 2 pasos esté activada
- Genera una nueva contraseña de aplicación

### Error: "JavaMailSender no está configurado"

**Problema:** Spring no pudo cargar la configuración de mail.

**Solución:**
- Verifica que `application-local.yml` esté en `src/main/resources/`
- Asegúrate de ejecutar con el perfil local: `-Dspring-boot.run.profiles=local`
- Revisa que no haya errores de sintaxis en el archivo YAML (indentación)

### Error: "Could not connect to SMTP host"

**Problema:** No se puede conectar al servidor SMTP de Gmail.

**Solución:**
- Verifica tu conexión a Internet
- Algunos firewalls corporativos bloquean el puerto 587
- Intenta desde otra red (ej: hotspot móvil)

### El email no llega

**Problema:** El envío parece exitoso pero no llega el email.

**Solución:**
- Revisa la carpeta de **Spam** en Gmail
- Verifica que el `from_address` en la BD sea tu email correcto
- Revisa los logs del backend para ver si hubo errores silenciosos

### Error: "Cannot find symbol: method setMailSender"

**Problema:** Error de compilación en el IDE (VS Code, NetBeans).

**Solución:**
- Este es un error del IDE con Lombok, no afecta la compilación con Maven
- Ejecuta: `mvn clean install` desde la terminal
- El proyecto debería compilar sin problemas
- Ignora los errores del IDE o reconstruye el proyecto

---

## 📊 Estructura de Archivos Modificados

```
proyectoVeterinaria/
├── pom.xml                                    ✅ (Ya tenía spring-boot-starter-mail)
├── src/main/
│   ├── java/com/tuorg/veterinaria/
│   │   └── notificaciones/
│   │       ├── config/
│   │       │   └── EmailConfig.java           🆕 NUEVO
│   │       └── model/
│   │           └── CanalEmail.java            ✏️ ACTUALIZADO
│   └── resources/
│       ├── application.yml                    ✏️ ACTUALIZADO
│       └── application-local.yml              🆕 NUEVO (NO SUBIR A GIT)
└── init-scripts/
    └── configurar-canal-email.sql             🆕 NUEVO
```

---

## 🎯 Próximos Pasos

Una vez que el envío de emails funcione, puedes:

1. **Integrar con Citas**: Enviar confirmación automática al crear una cita
2. **Recordatorios**: Programar emails 24h antes de las citas
3. **Plantillas HTML**: Mejorar el diseño de los emails con HTML y Thymeleaf
4. **Email del Cliente**: Obtener el email real del cliente en lugar de enviarte a ti mismo
5. **Producción**: Migrar a SendGrid o AWS SES para producción

---

## 📞 Contacto

Si tienes problemas, verifica:
- ✅ PostgreSQL está corriendo
- ✅ La base de datos `veterinaria_db` existe
- ✅ El canal EMAIL está configurado en la BD
- ✅ `application-local.yml` tiene tus credenciales correctas
- ✅ Estás ejecutando con el perfil `local`

¡Buena suerte! 🐾
