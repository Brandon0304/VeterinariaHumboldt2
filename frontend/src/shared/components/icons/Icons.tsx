/**
 * Biblioteca completa de iconos SVG personalizados
 * Diseñados con CSS siguiendo la paleta de colores de la aplicación
 */

interface IconProps {
  className?: string;
  size?: number;
}

// ==================== ICONOS DE CONFIGURACIÓN ====================

export const ClinicaIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M19 3H5C3.89 3 3 3.89 3 5V19C3 20.1 3.89 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.89 20.1 3 19 3Z" 
          stroke="currentColor" strokeWidth="2" className="text-primary" />
    <path d="M12 8V16M8 12H16" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" className="text-primary-dark" />
  </svg>
);

export const PermisosIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="7" y="2" width="10" height="14" rx="2" stroke="currentColor" strokeWidth="2" className="text-secondary" />
    <circle cx="12" cy="14" r="3" fill="currentColor" className="text-primary" />
    <rect x="11" y="14" width="2" height="6" fill="currentColor" className="text-primary-dark" />
  </svg>
);

export const ServiciosIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M5 8L12 2L19 8V19C19 19.5304 18.7893 20.0391 18.4142 20.4142C18.0391 20.7893 17.5304 21 17 21H7C6.46957 21 5.96086 20.7893 5.58579 20.4142C5.21071 20.0391 5 19.5304 5 19V8Z" 
          stroke="currentColor" strokeWidth="2" className="text-primary" />
    <path d="M9 21V11H15V21" stroke="currentColor" strokeWidth="2" className="text-primary-dark" />
    <circle cx="12" cy="7" r="1.5" fill="currentColor" className="text-secondary" />
  </svg>
);

export const HorariosIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <path d="M12 6v6l4 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-primary-dark" />
    <circle cx="12" cy="12" r="1.5" fill="currentColor" className="text-secondary" />
  </svg>
);

export const AuditoriaIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="4" y="2" width="16" height="20" rx="2" stroke="currentColor" strokeWidth="2" className="text-secondary" />
    <line x1="8" y1="7" x2="16" y2="7" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <line x1="8" y1="11" x2="14" y2="11" stroke="currentColor" strokeWidth="1.5" className="text-primary-light" />
    <line x1="8" y1="15" x2="12" y2="15" stroke="currentColor" strokeWidth="1.5" className="text-primary-light" />
  </svg>
);

export const RespaldosIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" strokeWidth="2" className="text-secondary" />
    <circle cx="7" cy="10" r="1.5" fill="currentColor" className="text-primary" />
    <circle cx="12" cy="10" r="1.5" fill="currentColor" className="text-primary" />
    <circle cx="17" cy="10" r="1.5" fill="currentColor" className="text-primary" />
    <rect x="5" y="14" width="14" height="4" rx="1" fill="currentColor" className="text-primary-light" opacity="0.3" />
  </svg>
);

// ==================== ICONOS DE ANIMALES ====================

export const PerroIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <ellipse cx="12" cy="14" rx="8" ry="7" fill="currentColor" className="text-primary-light" opacity="0.2" />
    <circle cx="12" cy="12" r="7" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <circle cx="9" cy="11" r="1.5" fill="currentColor" className="text-primary-dark" />
    <circle cx="15" cy="11" r="1.5" fill="currentColor" className="text-primary-dark" />
    <path d="M12 13c.5.5 1 1 1.5 1.5M10.5 14.5c.5.5 1 1 1.5 1.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" className="text-primary-dark" />
    <path d="M6 8c-1-2-1.5-4-1-5M18 8c1-2 1.5-4 1-5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-secondary" />
  </svg>
);

export const GatoIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <ellipse cx="12" cy="14" rx="8" ry="7" fill="currentColor" className="text-purple-200" opacity="0.3" />
    <circle cx="12" cy="12" r="7" stroke="currentColor" strokeWidth="2" className="text-purple-500" />
    <circle cx="9" cy="11" r="1.5" fill="currentColor" className="text-purple-700" />
    <circle cx="15" cy="11" r="1.5" fill="currentColor" className="text-purple-700" />
    <path d="M12 13v2m-2 1h4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" className="text-purple-600" />
    <path d="M5 6l2 5M19 6l-2 5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-purple-500" />
  </svg>
);

// ==================== ICONOS DE ACCIONES ====================

export const EditIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" 
          stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-primary" />
    <path d="M18.5 2.5C18.8978 2.10217 19.4374 1.87868 20 1.87868C20.5626 1.87868 21.1022 2.10217 21.5 2.5C21.8978 2.89782 22.1213 3.43739 22.1213 4C22.1213 4.56261 21.8978 5.10217 21.5 5.5L12 15L8 16L9 12L18.5 2.5Z" 
          stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-primary-dark" />
  </svg>
);

export const DeleteIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" 
          stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-danger" />
    <line x1="10" y1="11" x2="10" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-danger" />
    <line x1="14" y1="11" x2="14" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-danger" />
  </svg>
);

export const ViewIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" strokeWidth="2" className="text-info" />
    <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="2" className="text-info" fill="currentColor" fillOpacity="0.2" />
  </svg>
);

export const CheckIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M20 6L9 17L4 12" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className="text-success" />
  </svg>
);

export const CloseIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" className="text-danger" />
  </svg>
);

// ==================== ICONOS DE DOCUMENTOS ====================

export const PDFIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" 
          stroke="currentColor" strokeWidth="2" className="text-danger" fill="currentColor" fillOpacity="0.1" />
    <path d="M14 2v6h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-danger" />
    <text x="12" y="16" fontSize="6" fontWeight="bold" fill="currentColor" className="text-danger" textAnchor="middle">PDF</text>
  </svg>
);

export const ExcelIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" 
          stroke="currentColor" strokeWidth="2" className="text-success" fill="currentColor" fillOpacity="0.1" />
    <path d="M14 2v6h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-success" />
    <path d="M8 12h8M8 16h8" stroke="currentColor" strokeWidth="1.5" className="text-success" />
  </svg>
);

// ==================== ICONOS DE USUARIO ====================

export const UserIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="8" r="4" stroke="currentColor" strokeWidth="2" className="text-secondary" />
    <path d="M5 20c0-4 3-6 7-6s7 2 7 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-primary" />
  </svg>
);

export const PhoneIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72c.127.96.361 1.903.7 2.81a2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0122 16.92z" 
          stroke="currentColor" strokeWidth="2" className="text-primary" />
  </svg>
);

export const EmailIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" strokeWidth="2" className="text-primary"/>
    <path d="M22 6l-10 7L2 6" stroke="currentColor" strokeWidth="2" className="text-primary"/>
  </svg>
);

export const LocationIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z" stroke="currentColor" strokeWidth="2" className="text-primary"/>
    <circle cx="12" cy="10" r="3" stroke="currentColor" strokeWidth="2" className="text-primary"/>
  </svg>
);

// ==================== ICONOS MÉDICOS ====================

export const VacunaIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M9 2v3M15 2v3M9 5h6v14a2 2 0 01-2 2h-2a2 2 0 01-2-2V5z" 
          stroke="currentColor" strokeWidth="2" className="text-primary" fill="currentColor" fillOpacity="0.1" />
    <line x1="9" y1="9" x2="15" y2="9" stroke="currentColor" strokeWidth="2" className="text-primary-dark" />
    <line x1="9" y1="13" x2="15" y2="13" stroke="currentColor" strokeWidth="2" className="text-primary-dark" />
    <circle cx="12" cy="16" r="1" fill="currentColor" className="text-primary-dark" />
  </svg>
);

export const HistoriaIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="4" y="2" width="16" height="20" rx="2" stroke="currentColor" strokeWidth="2" className="text-secondary" />
    <line x1="8" y1="7" x2="16" y2="7" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <line x1="8" y1="11" x2="14" y2="11" stroke="currentColor" strokeWidth="1.5" className="text-primary-light" />
    <line x1="8" y1="15" x2="12" y2="15" stroke="currentColor" strokeWidth="1.5" className="text-primary-light" />
    <path d="M16 13l2 2 3-3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-success" />
  </svg>
);

export const ConsultaIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <path d="M12 8v8M8 12h8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-primary-dark" />
  </svg>
);

export const CalendarioIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" strokeWidth="2" className="text-primary" />
    <line x1="3" y1="10" x2="21" y2="10" stroke="currentColor" strokeWidth="2" className="text-primary-dark" />
    <line x1="8" y1="2" x2="8" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-secondary" />
    <line x1="16" y1="2" x2="16" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" className="text-secondary" />
  </svg>
);

// ==================== ICONOS DE ESTADO ====================

export const SuccessIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="10" fill="currentColor" className="text-success" opacity="0.2" />
    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" className="text-success" />
    <path d="M8 12l3 3 5-6" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const ErrorIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="10" fill="currentColor" className="text-danger" opacity="0.2" />
    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" className="text-danger" />
    <path d="M15 9l-6 6M9 9l6 6" stroke="white" strokeWidth="2.5" strokeLinecap="round" />
  </svg>
);

export const WarningIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" 
          fill="currentColor" className="text-warning" opacity="0.2" />
    <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" 
          stroke="currentColor" strokeWidth="2" className="text-warning" />
    <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" className="text-warning" />
    <circle cx="12" cy="17" r="1" fill="currentColor" className="text-warning" />
  </svg>
);

export const InfoIcon = ({ className = '', size = 24 }: IconProps) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="10" fill="currentColor" className="text-info" opacity="0.2" />
    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" className="text-info" />
    <line x1="12" y1="16" x2="12" y2="12" stroke="white" strokeWidth="2.5" strokeLinecap="round" />
    <circle cx="12" cy="8" r="1" fill="white" />
  </svg>
);
