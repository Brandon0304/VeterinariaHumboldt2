package com.tuorg.veterinaria.common.util;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;

import java.util.regex.Pattern;

/**
 * Clase de utilidades para validaciones comunes.
 * 
 * Esta clase proporciona métodos estáticos para validar diferentes
 * tipos de datos según las reglas de negocio de la aplicación.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public final class ValidationUtil {

    /**
     * Patrón para validar formato de correo electrónico.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Patrón para validar formato de teléfono (solo números, guiones y espacios).
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9\\s\\-+()]+$");

    /**
     * Constructor privado para prevenir instanciación.
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no debe instanciarse");
    }

    /**
     * Valida el formato de un correo electrónico.
     * 
     * @param email Correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida el formato de un correo electrónico y lanza excepción si es inválido.
     * 
     * @param email Correo electrónico a validar
     * @throws BusinessException Si el formato del correo es inválido
     */
    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new BusinessException("El formato del correo electrónico no es válido");
        }
    }

    /**
     * Valida el formato de un teléfono.
     * 
     * @param phone Teléfono a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Valida el formato de un teléfono y lanza excepción si es inválido.
     * 
     * @param phone Teléfono a validar
     * @throws BusinessException Si el formato del teléfono es inválido
     */
    public static void validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new BusinessException("El formato del teléfono no es válido");
        }
    }

    /**
     * Valida la longitud de un nombre de usuario.
     * 
     * @param username Nombre de usuario a validar
     * @throws BusinessException Si el nombre de usuario no cumple con los requisitos
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("El nombre de usuario no puede estar vacío");
        }
        if (username.length() < AppConstants.MIN_USERNAME_LENGTH) {
            throw new BusinessException(
                    String.format("El nombre de usuario debe tener al menos %d caracteres",
                            AppConstants.MIN_USERNAME_LENGTH));
        }
        if (username.length() > AppConstants.MAX_USERNAME_LENGTH) {
            throw new BusinessException(
                    String.format("El nombre de usuario no puede tener más de %d caracteres",
                            AppConstants.MAX_USERNAME_LENGTH));
        }
    }

    /**
     * Valida la fortaleza de una contraseña.
     * 
     * @param password Contraseña a validar
     * @throws BusinessException Si la contraseña no cumple con los requisitos
     */
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("La contraseña no puede estar vacía");
        }
        if (password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            throw new BusinessException(
                    String.format("La contraseña debe tener al menos %d caracteres",
                            AppConstants.MIN_PASSWORD_LENGTH));
        }
    }

    /**
     * Valida que un valor numérico sea positivo.
     * 
     * @param value Valor a validar
     * @param fieldName Nombre del campo (para el mensaje de error)
     * @throws BusinessException Si el valor no es positivo
     */
    public static void validatePositiveNumber(double value, String fieldName) {
        if (value <= 0) {
            throw new BusinessException(
                    String.format("El campo %s debe ser mayor que cero", fieldName));
        }
    }

    /**
     * Valida que un valor numérico sea no negativo (>= 0).
     * 
     * @param value Valor a validar
     * @param fieldName Nombre del campo (para el mensaje de error)
     * @throws BusinessException Si el valor es negativo
     */
    public static void validateNonNegativeNumber(double value, String fieldName) {
        if (value < 0) {
            throw new BusinessException(
                    String.format("El campo %s no puede ser negativo", fieldName));
        }
    }
}

