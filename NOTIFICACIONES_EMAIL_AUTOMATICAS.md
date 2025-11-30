# 📧 Sistema de Notificaciones Automáticas por Email

## ✅ Funcionalidad Implementada

El sistema ahora **envía automáticamente emails** al cliente cuando:

### 1. **Se programa una nueva cita** 
- ✉️ El cliente recibe un email de confirmación con:
  - Fecha y hora de la cita
  - Nombre del paciente (mascota)
  - Veterinario asignado
  - Tipo de servicio
  - Motivo de la consulta

### 2. **Se reprograma una cita existente**
- ✉️ El cliente recibe un email notificando:
  - Fecha anterior (cancelada)
  - Nueva fecha y hora
  - Detalles de la cita

---

## 🔧 Requisitos para que Funcione

### 1. **Configurar Gmail SMTP**

Edita el archivo: `src/main/resources/application-local.yml`

```yaml
spring:
  mail:
    username: tu-correo@gmail.com         # Tu Gmail
    password: xxxx xxxx xxxx xxxx         # Contraseña de aplicación
```

**Cómo obtener la contraseña:**
1. Ve a: https://myaccount.google.com/security
2. Activa "Verificación en 2 pasos"
3. Genera una "Contraseña de aplicación" para "Correo"
4. Copia la contraseña de 16 caracteres

### 2. **Configurar el Canal de Email en la BD**

Ejecuta este script SQL:

```powershell
$env:PGPASSWORD='1234'
psql -U postgres -d veterinaria_db -f init-scripts/configurar-canal-email.sql
```

O manualmente:

```sql
-- Verificar que existe el canal EMAIL
SELECT * FROM canales_envio WHERE tipo = 'EMAIL';

-- Si no existe, crearlo:
INSERT INTO canales_envio (tipo, activo, configuracion)
VALUES ('EMAIL', true, '{"host":"smtp.gmail.com","port":587,"from_address":"tu-correo@gmail.com"}');
```

⚠️ **IMPORTANTE**: Cambia `tu-correo@gmail.com` por tu email real.

### 3. **Ejecutar el Backend con el perfil local**

En IntelliJ:
- Run → Edit Configurations → Active profiles: `local`

O desde terminal:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 📝 Ejemplo de Email que Recibe el Cliente

```
🐾 Confirmación de Cita - Clínica Veterinaria Humboldt

Estimado/a María González,

Su cita ha sido programada exitosamente:

📅 Fecha y Hora: 27/11/2025 10:30
🐕 Paciente: Max (Perro)
👨‍⚕️ Veterinario: Dr. Juan Pérez
🏥 Tipo de Servicio: Consulta General
Motivo: Control de vacunación

Por favor, llegue 10 minutos antes de su cita.

Si necesita cancelar o reprogramar, contáctenos con al menos 24 horas de anticipación.

Saludos cordiales,
Clínica Veterinaria Humboldt
```

---

## 🧪 Cómo Probar

### Opción 1: Desde el Frontend (Secretario/Admin)

1. Inicia sesión como **secretario** o **admin**
2. Ve a **"Citas"** → **"Nueva Cita"**
3. Selecciona:
   - Un paciente cuyo cliente tenga **email real**
   - Un veterinario
   - Fecha y hora
4. Click en **"Crear Cita"**
5. **Revisa el email** del cliente (inbox o spam)

### Opción 2: Desde Postman/API

```http
POST http://localhost:8080/api/citas
Authorization: Bearer <tu_token_jwt>
Content-Type: application/json

{
  "pacienteId": 1,
  "veterinarioId": 2,
  "fechaHora": "2025-11-27T10:30:00",
  "tipoServicio": "Consulta General",
  "motivo": "Control de vacunación"
}
```

---

## 🐛 Solución de Problemas

### El email no llega

**Verificar:**
1. ✅ El cliente tiene email registrado en la BD:
   ```sql
   SELECT c.*, p.correo 
   FROM clientes c 
   JOIN personas p ON c.id_usuario = p.id_persona;
   ```

2. ✅ El canal EMAIL está activo:
   ```sql
   SELECT * FROM canales_envio WHERE tipo = 'EMAIL' AND activo = true;
   ```

3. ✅ Configuración de Gmail correcta en `application-local.yml`

4. ✅ Revisa la carpeta de **Spam** del cliente

5. ✅ Verifica los logs del backend:
   ```
   ✅ Email enviado exitosamente a través de smtp.gmail.com
      → Para: maria.gonzalez@email.com
   ```

### Error: "JavaMailSender no configurado"

- Asegúrate de ejecutar con el perfil `local`: `-Dspring-boot.run.profiles=local`

### Error: "Username and Password not accepted"

- Verifica que uses una **contraseña de aplicación**, NO tu contraseña normal de Gmail
- La verificación en 2 pasos debe estar activa

---

## 📊 Datos que Recibe el Cliente

El email se envía **automáticamente** cuando:
- Secretario crea una cita
- Admin crea una cita  
- Secretario/Admin reprograma una cita

**NO se envía si:**
- El cliente no tiene email registrado
- El canal EMAIL no está configurado
- Hay error en la configuración de Gmail

---

## 🎯 Próximas Mejoras

Puedes agregar:
- ✉️ Email de recordatorio 24h antes de la cita
- ✉️ Email de confirmación al cancelar una cita
- ✉️ Email con resultados de consultas
- 📱 Notificaciones por SMS (Twilio)
- 📱 Notificaciones push (Firebase)

---

## 📞 Resumen

| Evento | Email enviado a | Contenido |
|--------|----------------|-----------|
| Crear Cita | Cliente (propietario del paciente) | Confirmación con fecha, veterinario, detalles |
| Reprogramar Cita | Cliente | Fecha anterior y nueva fecha |
| Cancelar Cita | ❌ NO implementado aún | - |

**¡Listo!** El sistema ya envía emails reales a los clientes. 🎉
