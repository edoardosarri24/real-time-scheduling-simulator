package helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    /**
     * Recursively searches for a declared field in the class hierarchy.
     * @param clazz     the class to start searching from
     * @param fieldName the name of the field to find
     * @return the {@link Field} object representing the requested field
     * @throws NoSuchFieldException if the field is not found in any superclass
     */
    private static Field getFieldRecursive(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Campo non trovato: " + fieldName);
    }

    /**
     * Recursively searches for a declared method in the class hierarchy.
     * @param clazz          the class to start searching from
     * @param methodName     the name of the method to find
     * @param parameterTypes the types of the parameters
     * @return the {@link Method} object representing the requested method
     * @throws NoSuchMethodException if the method is not found in any superclass
     */
    private static Method getMethodRecursive(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Metodo non trovato: " + methodName);
    }

    /**
     * Sets the value of a specified field on the given target object using reflection.
     * This method also considers fields declared in superclasses.
     * @param target    the object whose field should be modified
     * @param fieldName the name of the field to set
     * @param value     the new value to assign to the field
     * @throws RuntimeException if the field does not exist or cannot be accessed
     */
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = getFieldRecursive(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'accesso al campo " + fieldName, e);
        }
    }

    /**
     * Retrieves the value of a specified field from the given target object using reflection.
     * This method also considers fields declared in superclasses.
     * @param target    the object from which to retrieve the field value
     * @param fieldName the name of the field to retrieve
     * @return the value of the specified field in the target object
     * @throws RuntimeException if the field does not exist or cannot be accessed
     */
    public static Object getField(Object target, String fieldName) {
        try {
            Field field = getFieldRecursive(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel leggere il campo " + fieldName, e);
        }
    }

    /**
     * Invokes a no-argument method with the specified name on the given target object using reflection.
     * This method also considers methods declared in superclasses.
     * @param target     the object on which to invoke the method
     * @param methodName the name of the method to invoke
     * @return the result of the method invocation, or null if the method returns void
     * @throws RuntimeException if the method cannot be found or invoked
     */
    public static Object invokeMethod(Object target, String methodName) {
        try {
            Method method = getMethodRecursive(target.getClass(), methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'invocazione del metodo " + methodName, e);
        }
    }

    /**
     * Invokes a method with parameters on the given target object using reflection.
     * This method also considers methods declared in superclasses.
     * @param target         the object on which to invoke the method
     * @param methodName     the name of the method to invoke
     * @param parameterTypes the types of the parameters
     * @param args           the arguments to pass to the method
     * @return the result of the method invocation, or null if the method returns void
     * @throws RuntimeException if the method cannot be found or invoked
     */
    public static Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) {
        try {
            Method method = getMethodRecursive(target.getClass(), methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'invocazione del metodo " + methodName, e);
        }
    }
}