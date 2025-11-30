# Guía de Iconos CSS - Veterinaria Humboldt

## ✅ Módulos Actualizados

### 1. **Módulo de Configuración** ✅
- **Página**: `ConfiguracionPageNew.tsx`
- **Iconos implementados**:
  - ClinicaIcon (Información clínica)
  - PermisosIcon (Permisos y roles)
  - ServiciosIcon (Servicios)
  - HorariosIcon (Horarios)
  - AuditoriaIcon (Auditoría)
  - RespaldosIcon (Respaldos)

### 2. **Módulo de Pacientes** ✅ (Parcial)
- **Página**: `VeterinarianPatientsPage.tsx`
- **Iconos implementados**:
  - PerroIcon y GatoIcon (especies)
  - ViewIcon (ver perfil)
  - HistoriaIcon (historia clínica)
  - ExcelIcon y PDFIcon (exportación)

- **Componente**: `PacienteDetailModal.tsx` ⚠️ (Parcialmente actualizado)
- **Iconos implementados**:
  - PerroIcon/GatoIcon (avatar)
  - InfoIcon, ConsultaIcon, VacunaIcon (tabs) - Pendiente de verificar
  - UserIcon, EditIcon, VacunaIcon, PDFIcon, HistoriaIcon (acciones) - Pendiente de verificar

## 📋 Emojis Pendientes de Reemplazar

### Módulo de Historias
- `VeterinarianHistoriesPage.tsx`:
  - Line 266: `✏️ Editar`

### Módulo de Reportes  
- `ReportesPage.tsx`:
  - Line 146: `📄 PDF`
  - Line 152: `📊 Excel`

### Módulo de Proveedores
- `ProveedoresPage.tsx`:
  - Line 226: `📞` (teléfono)
  - Line 258: `🗑️` (eliminar)

### Módulo de Seguimientos
- `VeterinarianFollowUpsPage.tsx`:
  - Line 237: `📝` (en toast)

## 🎨 Biblioteca de Iconos Disponibles

Ubicación: `frontend/src/shared/components/icons/Icons.tsx`

### Iconos de Configuración
- `ClinicaIcon`
- `PermisosIcon`
- `ServiciosIcon`
- `HorariosIcon`
- `AuditoriaIcon`
- `RespaldosIcon`

### Iconos de Animales
- `PerroIcon`
- `GatoIcon`

### Iconos de Acciones
- `EditIcon`
- `DeleteIcon`
- `ViewIcon`
- `CheckIcon`
- `CloseIcon`

### Iconos de Documentos
- `PDFIcon`
- `ExcelIcon`

### Iconos de Usuario
- `UserIcon`
- `PhoneIcon`

### Iconos Médicos
- `VacunaIcon`
- `HistoriaIcon`
- `ConsultaIcon`
- `CalendarioIcon`

### Iconos de Estado
- `SuccessIcon`
- `ErrorIcon`
- `WarningIcon`
- `InfoIcon`

## 🔧 Uso de los Iconos

```tsx
// Importar
import { PerroIcon, GatoIcon } from '../../../shared/components/icons/Icons';

// Usar en componente
<PerroIcon size={24} className="text-primary" />

// Con props personalizadas
<GatoIcon 
  size={32} 
  className="text-purple-500 hover:text-purple-700" 
/>
```

## 📝 Props Disponibles

```typescript
interface IconProps {
  className?: string;  // Clases Tailwind
  size?: number;       // Tamaño en píxeles (default: 24)
}
```

## 🎨 Paleta de Colores

Los iconos están diseñados para usar con estas clases de Tailwind:

- `text-primary` - Turquesa principal (#1ABCBC)
- `text-primary-dark` - Turquesa oscuro (#0F6A7B)
- `text-primary-light` - Turquesa claro (#55E0D5)
- `text-secondary` - Azul oscuro (#114264)
- `text-success` - Verde (#4ADE80)
- `text-danger` - Rojo (#F87171)
- `text-warning` - Amarillo (#FACC15)
- `text-info` - Azul (#60A5FA)

## ⏭️ Próximos Pasos

1. Completar actualización de `PacienteDetailModal.tsx`
2. Actualizar módulos pendientes (Historias, Reportes, Proveedores, Seguimientos)
3. Verificar todos los componentes en el navegador
4. Ajustar tamaños y colores según feedback visual
