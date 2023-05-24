package Http;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public class DispatcherServlet {
    private static File file;
    public static File staticFile;
    private static DispatcherServlet dispatcherServlet;

    private DispatcherServlet() {
    }

    //单列模式，对外提供一个入口
    public static DispatcherServlet getDispatcherServlet() {
        return dispatcherServlet;
    }

    static {
        dispatcherServlet = new DispatcherServlet();
        try {
            //定位文件目录
            file = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );
            staticFile = new File(file, "static");
            //File indexFile = new File(staticFile,"index.html");//依次定位到可执行html文件位置
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //处理响应
    public void service(HttpServletResponse response, HttpServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String path = request.getRequestURI();
        File file = new File(staticFile, path);
        System.out.println("该页面是否存在:" + (file.exists() ? "存在" : "不存在"));
        Method method = HandlerMapping.getMethod(path);
        if (method!=null){
            method.invoke(method.getDeclaringClass().newInstance(),request,response);
            return;
        }
        if (file.isFile()) {
            //当前分支代表浏览器访问的资源存在
            response.setContentFile(file);
        } else {
            //浏览器访问的资源不存在，需要更改默认的状态嘛以及自定义的404界面
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            response.setContentFile(new File(staticFile, "/root/404.html"));
        }
        //测试添加其它响应头
        response.addHeader("Server", "WebServer");
    }

}





















