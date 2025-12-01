
# Guía de Configuración para Resolver el Error de CORS

## 🔍 Problema Identificado

El frontend desplegado en Railway (`https://clinicaveterinariahumboldt.up.railway.app`) está intentando conectarse a `http://localhost:8080/api`, que solo existe en tu máquina local. Esto causa un error de CORS.

## ✅ Solución Implementada

### 1. Frontend - Variables de Entorno

Se han creado dos archivos de configuración:

- **`frontend/.env.development`**: Para desarrollo local
  ```env
  VITE_API_URL=http://localhost:8080/api
  ```

- **`frontend/.env.production`**: Para producción (se sobrescribe con variable de entorno de Railway)
  ```env
  VITE_API_URL=https://your-backend-url.up.railway.app/api
  ```

### 2. Backend - Configuración de CORS

Se actualizó la configuración de CORS en:
- `SecurityConfig.java`: Ahora lee los orígenes permitidos desde `application.yml`
- `application.yml`: Incluye `https://clinicaveterinariahumboldt.up.railway.app`
- `application-prod.yml`: Configuración específica para producción

## 🚀 Pasos para Desplegar en Railway

### A. Configurar el Backend en Railway

1. Ve a tu proyecto de **Backend** en Railway
2. Ve a la pestaña **Variables**
3. Agrega/actualiza estas variables:

```env
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=tu-clave-secreta-super-segura-de-al-menos-256-bits
CORS_ALLOWED_ORIGINS=https://clinicaveterinariahumboldt.up.railway.app
```

4. **Importante**: Copia la URL pública de tu backend (ej: `https://tu-backend.up.railway.app`)

### B. Configurar el Frontend en Railway

1. Ve a tu proyecto de **Frontend** en Railway
2. Ve a la pestaña **Variables**
3. Agrega esta variable (reemplaza con la URL real de tu backend):

```env
VITE_API_URL=https://tu-backend.up.railway.app/api
```

### C. Redesplegar Ambos Servicios

1. **Backend**: Railway lo redesplegará automáticamente al detectar los cambios de variables
2. **Frontend**: También se redesplegará automáticamente

Si no se redesplegan automáticamente:
- Ve a la pestaña **Deployments**
- Haz clic en **Redeploy** en el último deployment

## 🧪 Verificar que Funciona

### En Desarrollo Local:

1. Inicia el backend:
   ```bash
   mvn spring-boot:run
   ```

2. Inicia el frontend:
   ```bash
   cd frontend
   npm run dev
   ```

3. Abre la consola del navegador y verifica los logs:
   ```
   🔧 Configuración API Client:
     - BASE_URL: http://localhost:8080/api
     - VITE_API_URL: http://localhost:8080/api
     - MODE: development
   ```

### En Producción (Railway):

1. Abre tu aplicación: `https://clinicaveterinariahumboldt.up.railway.app`
2. Abre la consola del navegador (F12)
3. Verifica los logs:
   ```
   🔧 Configuración API Client:
     - BASE_URL: https://tu-backend.up.railway.app/api
     - VITE_API_URL: https://tu-backend.up.railway.app/api
     - MODE: production
   ```

4. Intenta hacer login con:
   - Usuario: `admin`
   - Contraseña: `Admin123!`

5. **NO** deberías ver errores de CORS

## 🐛 Solución de Problemas

### Error: "No 'Access-Control-Allow-Origin' header"

**Causa**: El backend no está configurado correctamente para permitir tu frontend.

**Solución**:
1. Verifica que `CORS_ALLOWED_ORIGINS` en Railway backend incluya tu URL de frontend
2. **NO** incluyas una barra diagonal al final: ✅ `https://example.com` ❌ `https://example.com/`

### Error: "Network Error" o "ERR_NETWORK"

**Causa**: La URL del backend está incorrecta o el backend no está accesible.

**Solución**:
1. Verifica que `VITE_API_URL` en Railway frontend sea correcta
2. Incluye `/api` al final: `https://tu-backend.up.railway.app/api`
3. Verifica que el backend esté corriendo en Railway (debe estar en "Active")

### El login funciona en local pero no en Railway

**Causa**: Las variables de entorno no están configuradas correctamente en Railway.

**Solución**:
1. Verifica que configuraste `VITE_API_URL` en el **frontend de Railway**
2. Verifica que configuraste `CORS_ALLOWED_ORIGINS` en el **backend de Railway**
3. Redespliega ambos servicios después de configurar las variables

## 📝 Notas Importantes

- **Las variables de entorno en Railway se configuran por servicio** (una para frontend, otra para backend)
- **Vite solo reconstruye las variables de entorno en build time**, por lo que debes redesplegar el frontend después de cambiar `VITE_API_URL`
- **No subas archivos `.env` a git** (ya están en `.gitignore`)
- Las configuraciones locales (`.env.development`) y de producción (`.env.production`) son diferentes

## ✨ Cambios Realizados en el Código

### Frontend
- ✅ Creado `frontend/.env.development`
- ✅ Creado `frontend/.env.production`
- ✅ Actualizado `ApiClient.ts` con logs de diagnóstico
- ✅ Ya usaba `import.meta.env.VITE_API_URL` (no requiere cambios)

### Backend
- ✅ Actualizado `SecurityConfig.java` para leer CORS desde configuración
- ✅ Actualizado `application.yml` para incluir Railway frontend
- ✅ Actualizado `application-prod.yml` con configuración de producción
- ✅ Actualizado `.env.railway.example` con instrucciones correctas

## 🎯 Próximos Pasos

1. Commit y push de los cambios:
   ```bash
   git add .
   git commit -m "Configurar CORS y variables de entorno para Railway"
   git push
   ```

2. Configurar las variables de entorno en Railway (ver sección 🚀)

3. Verificar que el despliegue funcione correctamente

4. ¡Tu aplicación debería funcionar sin errores de CORS! 🎉

