package Http;

import MyException.EmptyRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpServletRequest {
    private Socket socket;
    private String uri;//请求资源路径
    private String method; //请求方式
    private String protocol;//请求协议版本
    private final Map<String, String> headers = new HashMap<>();//请求的数据
    private final Map<String,String> parameters = new HashMap<>();//请求参数
    private String requestURI;//请求路径
    private String queryString;//路径参数

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //解析请求行
        parseRequestLine();
        //读取所有请求数据
        parseHeaders();
        //解析正文
        parseContent();

    }

    //解析请求行以及请求餐宿
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if (line.isEmpty()){//判断请求行是否为空请求
            throw new EmptyRequestException();

        }
//         System.out.println("请求行:"+line);

        String[] st = line.split("\\s");
        method = st[0];
        uri = st[1];
        protocol = st[2];
        //解析请求参数
        parseURI();
    }

    //解析消息头
    private void parseHeaders() throws IOException {
        while (true) {
            String line = readLine();
            if (line.isEmpty()) { //如果readLine返回空字符串，说明单独读取到了回车+换行
                break;
            }
//            System.out.println("消息头:" + line);
            String[] split = line.split(":\\s", 0);
            headers.put(split[0].toLowerCase(Locale.ROOT), split[1]);//转为小写
        }
    }

    //解析消息正文 post
    private void parseContent() throws IOException {
//        System.err.println("正文");
        if ("POST".equalsIgnoreCase(method)){
            String header = getHeader("Content-Length"); //根据key获取value
            String decode = URLDecoder.decode(header, "UTF-8");
            if (decode!=null){//判断不为空的前提是有contentlenght 这个key
                int i = Integer.parseInt(decode);
                System.err.println("正文长度"+i);
                byte[] bytes = new byte[i];
                InputStream is = socket.getInputStream();
                is.read(bytes);

                String contentType = getHeader("Content-Type");
                if ("application/x-www-form-urlencoded".equals(contentType)){//是否为form表单不带附件的数据
                    String line = new String(bytes, StandardCharsets.ISO_8859_1);
                    System.err.println(line);
                    parseParameters(line);
                }
            }
//            else if (){}
        }
    }


    //读取请求
    private String readLine() throws IOException {//重用代码不铺货异常
        InputStream in = socket.getInputStream();
        int d;
        char cur = 'a', pre = 'a';
        StringBuilder builder = new StringBuilder();
        while ((d = in.read()) != -1) {
            cur = (char) d;
            if (pre == 13 && cur == 10) {
                break;
            }
            builder.append(cur);
            pre = cur;
        }
        String line = builder.toString().trim();//转化为字符并且去除空格

        return line;
    }


    //解析uri
    private void parseURI() {
        String[] data = uri.split("\\?");//先按问号拆分，
        requestURI = data[0];//
        if (data.length > 1) {//如果拆分长度大于1证明有请求参数
            queryString = data[1];//将请求参数添加到变量
            parseParameters(queryString);
        }
    }

    //解析参数,复用get和post
    private void parseParameters(String line){
        if (queryString !=null){
            String[] data = queryString.split("&");//对所有请求参数再次分割
            for (String datum : data) {
                String[] paras = datum.split("=");//对请求餐宿分割为键值对
                parameters.put(paras[0], paras.length > 1 ? paras[1] : "");//key有值添加value，没有value为空
            }
        }

    }













    public String getParameter(String st) {
        return parameters.get(st);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getUri() {
        return uri;
    }
    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String st) {
        return headers.get(st.toLowerCase(Locale.ROOT));
    }


}
