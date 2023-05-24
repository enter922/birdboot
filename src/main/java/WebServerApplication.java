import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServerApplication {
    private ServerSocket websocket;
    private ExecutorService threadPool;

    public WebServerApplication() {
        try {
            websocket = new ServerSocket(8066);
            threadPool = Executors.newFixedThreadPool(30);
            System.out.println("启动完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            while (true) {
                Socket accept = websocket.accept();
                System.out.println("一个客户端已连接");
                ClientHandler cl = new ClientHandler(accept);
                threadPool.execute(cl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServerApplication webServer = new WebServerApplication();
        webServer.start();
    }














































}
