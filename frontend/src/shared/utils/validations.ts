/**
 * Utilidades de validación para formularios.
 * 
 * Proporciona funciones reutilizables para validar datos de entrada
 * con mensajes de error descriptivos.
 */

export interface ValidationResult {
  isValid: boolean;
  error?: string;
}

/**
 * Valida formato de email.
 */
export const validateEmail = (email: string): ValidationResult => {
  if (!email || email.trim() === '') {
    return { isValid: false, error: 'El email es requerido' };
  }

  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  
  if (!emailRegex.test(email)) {
    return { isValid: false, error: 'El formato del email no es válido' };
  }

  return { isValid: true };
};

/**
 * Valida formato de teléfono (10-15 dígitos, puede incluir + al inicio).
 */
export const validatePhone = (phone: string): ValidationResult => {
  if (!phone || phone.trim() === '') {
    return { isValid: false, error: 'El teléfono es requerido' };
  }

  const phoneRegex = /^\+?[0-9]{10,15}$/;
  const cleanPhone = phone.replace(/[\s\-()]/g, '');
  
  if (!phoneRegex.test(cleanPhone)) {
    return { isValid: false, error: 'El teléfono debe contener entre 10 y 15 dígitos' };
  }

  return { isValid: true };
};

/**
 * Valida que una fecha no sea en el pasado.
 */
export const validateFutureDate = (date: string | Date): ValidationResult => {
  if (!date) {
    return { isValid: false, error: 'La fecha es requerida' };
  }

  const selectedDate = typeof date === 'string' ? new Date(date) : date;
  const now = new Date();
  now.setHours(0, 0, 0, 0); // Reset a medianoche para comparar solo fechas

  if (selectedDate < now) {
    return { isValid: false, error: 'La fecha no puede ser en el pasado' };
  }

  return { isValid: true };
};

/**
 * Valida que una fecha/hora no sea en el pasado (incluye hora).
 */
export const validateFutureDateTime = (dateTime: string | Date): ValidationResult => {
  if (!dateTime) {
    return { isValid: false, error: 'La fecha y hora son requeridas' };
  }

  const selectedDateTime = typeof dateTime === 'string' ? new Date(dateTime) : dateTime;
  const now = new Date();

  if (selectedDateTime < now) {
    return { isValid: false, error: 'La fecha y hora no pueden ser en el pasado' };
  }

  return { isValid: true };
};

/**
 * Valida longitud mínima de texto.
 */
export const validateMinLength = (text: string, minLength: number, fieldName: string): ValidationResult => {
  if (!text || text.trim() === '') {
    return { isValid: false, error: `${fieldName} es requerido` };
  }

  if (text.trim().length < minLength) {
    return { isValid: false, error: `${fieldName} debe tener al menos ${minLength} caracteres` };
  }

  return { isValid: true };
};

/**
 * Valida que un número sea positivo.
 */
export const validatePositiveNumber = (value: number | string, fieldName: string): ValidationResult => {
  const num = typeof value === 'string' ? parseFloat(value) : value;

  if (isNaN(num)) {
    return { isValid: false, error: `${fieldName} debe ser un número válido` };
  }

  if (num <= 0) {
    return { isValid: false, error: `${fieldName} debe ser mayor a 0` };
  }

  return { isValid: true };
};

/**
 * Valida que un número sea no negativo (puede ser 0).
 */
export const validateNonNegativeNumber = (value: number | string, fieldName: string): ValidationResult => {
  const num = typeof value === 'string' ? parseFloat(value) : value;

  if (isNaN(num)) {
    return { isValid: false, error: `${fieldName} debe ser un número válido` };
  }

  if (num < 0) {
    return { isValid: false, error: `${fieldName} no puede ser negativo` };
  }

  return { isValid: true };
};

/**
 * Valida formato de RUT/DNI chileno (opcional, ajustar según país).
 */
export const validateRut = (rut: string): ValidationResult => {
  if (!rut || rut.trim() === '') {
    return { isValid: false, error: 'El RUT es requerido' };
  }

  // Formato esperado: 12.345.678-9 o 12345678-9
  const rutRegex = /^[0-9]{1,2}\.?[0-9]{3}\.?[0-9]{3}-?[0-9kK]$/;
  
  if (!rutRegex.test(rut)) {
    return { isValid: false, error: 'El formato del RUT no es válido (ej: 12.345.678-9)' };
  }

  return { isValid: true };
};

/**
 * Valida similitud entre dos strings (para detectar duplicados).
 * Retorna un score de similitud entre 0 y 1.
 */
export const calculateSimilarity = (str1: string, str2: string): number => {
  const s1 = str1.toLowerCase().trim();
  const s2 = str2.toLowerCase().trim();

  if (s1 === s2) return 1;
  if (s1.length === 0 || s2.length === 0) return 0;

  const longer = s1.length > s2.length ? s1 : s2;
  const shorter = s1.length > s2.length ? s2 : s1;

  if (longer.includes(shorter)) return 0.8;

  // Algoritmo de distancia de Levenshtein simplificado
  const editDistance = levenshteinDistance(s1, s2);
  return 1 - editDistance / Math.max(s1.length, s2.length);
};

/**
 * Calcula la distancia de Levenshtein entre dos strings.
 */
const levenshteinDistance = (str1: string, str2: string): number => {
  const matrix: number[][] = [];

  for (let i = 0; i <= str2.length; i++) {
    matrix[i] = [i];
  }

  for (let j = 0; j <= str1.length; j++) {
    matrix[0][j] = j;
  }

  for (let i = 1; i <= str2.length; i++) {
    for (let j = 1; j <= str1.length; j++) {
      if (str2.charAt(i - 1) === str1.charAt(j - 1)) {
        matrix[i][j] = matrix[i - 1][j - 1];
      } else {
        matrix[i][j] = Math.min(
          matrix[i - 1][j - 1] + 1,
          matrix[i][j - 1] + 1,
          matrix[i - 1][j] + 1
        );
      }
    }
  }

  return matrix[str2.length][str1.length];
};

/**
 * Valida múltiples campos y retorna el primer error encontrado.
 */
export const validateFields = (validations: ValidationResult[]): ValidationResult => {
  const firstError = validations.find(v => !v.isValid);
  return firstError || { isValid: true };
};
