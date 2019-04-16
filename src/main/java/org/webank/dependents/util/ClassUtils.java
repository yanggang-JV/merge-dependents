package org.webank.dependents.util;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

    private static String classPath;

    private static List<Class<?>> classList;

    /**
     * 检查某个类是否包含指定接口.
     */
    private static boolean isMatch(Class<?> cls, Class<?> interfaceClass) {
        if (Modifier.isAbstract(cls.getModifiers())) {
            return false;
        }
        Class<?>[] interfaces = cls.getInterfaces();
        for (Class<?> class1 : interfaces) {
            if (class1 == interfaceClass) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取类名
     * @param classFilePath
     * @return
     */
    private static String getClassName(String classFilePath) {
        String classFile = classFilePath;
        classFile = classFile.substring(classPath.length() + 1);
        classFile = classFile.substring(0, classFile.length() - 6);
        classFile = classFile.replace("\\", ".");
        return classFile;
    }

    /**
     * 找到工程下面的所有类.
     */
    private static void findFile(File file) throws ClassNotFoundException {
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                findFile(file2);
            } else {
                if (file2.getName().endsWith(".class")) {
                    try {
                        classList.add(Class.forName(getClassName(file2.getAbsolutePath())));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * 找到某接口下面的所有子类.
     * @throws ClassNotFoundException 
     */
    public static List<Class<?>> findAllChildClassFromInterface(Class<?> interfaceCls)
        throws ClassNotFoundException {
        if (classList == null) {
            loadAllClass();
        }
        List<Class<?>> childClass = new ArrayList<Class<?>>();
        for (Class<?> cls : classList) {
            if (isMatch(cls, interfaceCls)) {
                childClass.add(cls);
            }
        }
        return childClass;
    }

    /**
     * 加载所有类.
     */
    private static synchronized void loadAllClass() throws ClassNotFoundException {
        classList = new ArrayList<Class<?>>();
        String classPathBase = 
            Thread.currentThread().getContextClassLoader().getResource("./").getPath();
        File file = new File(classPathBase);
        classPath = file.getAbsolutePath();
        findFile(file);
    }

}
