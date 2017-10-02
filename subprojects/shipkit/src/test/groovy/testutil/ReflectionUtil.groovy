package testutil

import java.lang.reflect.Method

class ReflectionUtil {

    public static final int SET_LENGTH = 3
    public static final String GET = "get"
    public static final String IS = "is"
    public static final String SET = "set"

    static List<ObjectWithProperties> findGettersAndSetters(obj) {
        def gettersAndSettersList = []
        def parentList = []
        parentList << obj.class
        doReflectionRec(obj, gettersAndSettersList, parentList)
        return gettersAndSettersList;
    }

    static def doReflectionRec(obj, myList, parents) {
        def methods = obj.class.declaredMethods

        for (method in methods) {
            if (isGetter(method)) {
                def setter = findSetterForGetter(obj, method)
                if (setter != null) {
                    myList << new ObjectWithProperties(method, setter, obj)
                } else {
                    def getterResult = method.invoke(obj)
                    //Protection against cyclic dependencies
                    if (getterResult != null && !parents.contains(getterResult.class)) {
                        parents << getterResult.class
                        doReflectionRec(getterResult, myList, parents)
                    }
                }
            }
        }
    }

    static Method findSetterForGetter(Object object, Method getter) {
        def methods = object.class.declaredMethods
        for (it in methods) {
            def fieldName = it.name.substring(SET_LENGTH)
            if (isSetter(it) && (getter.name.equals(GET + fieldName) || getter.name.equals(IS + fieldName))) {
                return it;
            }
        }
        return null;
    }

    static boolean isSetter(Method method) {
        if (method.name.startsWith(SET) && method.parameterCount == 1) {
            return true;
        }
        return false;
    }

    static boolean isGetter(Method method) {
        if (!(method.name.startsWith(IS) || method.name.startsWith(GET))) {
            return false;
        }
        if (method.parameterCount != 0) {
            return false;
        }
        return true;
    }

    static class ObjectWithProperties {
        Method getter;
        Method setter;
        Object object;

        ObjectWithProperties(Method getter, Method setter, Object object) {
            this.getter = getter
            this.setter = setter
            this.object = object
        }

        Object getObject() {
            return object
        }

        Method getGetter() {
            return getter
        }

        Method getSetter() {
            return setter
        }

        int hashCode() {
            int result
            result = (getter != null ? getter.hashCode() : 0)
            result = 31 * result + (setter != null ? setter.hashCode() : 0)
            result = 31 * result + (object != null ? object.hashCode() : 0)
            return result
        }

        boolean equals(o) {
            if (this.is(o)) {
                return true
            }
            if (getClass() != o.class) {
                return false
            }

            ObjectWithProperties value = (ObjectWithProperties) o

            if (getter != value.getter) {
                return false
            }
            if (object != value.object) {
                return false
            }
            if (setter != value.setter) {
                return false
            }

            return true
        }
    }
}
