package Http;

import Annotations.Controller;
import Annotations.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {
    private static Map<String,Method> mapping = new HashMap<>();
    static {
        init();
    }

    private static void init() {
        try {
            File files = new File(HandlerMapping.class.getClassLoader().getResource("./Controller").toURI());
            File[] f = files.listFiles(pathname -> pathname.getName().endsWith(".class"));
            for (File fe : f) {
                String name = fe.getName();
                String substring = name.substring(0, name.indexOf("."));
                String paths = "Controller." + substring;

                Class c = Class.forName(paths);
                if (c.isAnnotationPresent(Controller.class)) {
                    Object o = c.newInstance();
                    Method[] m = c.getDeclaredMethods();
                    for (Method method : m) {
                        if (method.isAnnotationPresent(RequestMapping.class)
                                && method.getModifiers() == Modifier.PUBLIC) {
                            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                            String value = annotation.value();
                            if (!value.contains("/")) {
                                value = "/" + value;
                            }
                            mapping.put(value,method);

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Method getMethod(String path){
        if (!path.contains("/")){
            path = "/"+path;
        }
        return mapping.get(path);
    }

//    public static void main(String[] args) {
//        System.err.println(mapping);
//        Method userList = getMethod("userList");
//        System.out.println(userList.getDeclaringClass().getName());
//        System.out.println(userList.getName());
//
//    }
//
//
//

















}
