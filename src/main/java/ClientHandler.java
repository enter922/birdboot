import Http.DispatcherServlet;
import Http.HttpServletRequest;
import Http.HttpServletResponse;
import MyException.EmptyRequestException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class    ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            //解析请求，将相关信息保存到静态变量对外提供方法调用
            HttpServletRequest request = new HttpServletRequest(socket);
            //创建实列将变量传过去
            HttpServletResponse response = new HttpServletResponse(socket);
            //单列模式创建将响应和请求放进去处理。
            DispatcherServlet.getDispatcherServlet().service(response,request);

            //返回响应
            response.response();
        } catch (IOException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e) {
            //空请求
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }









































}
