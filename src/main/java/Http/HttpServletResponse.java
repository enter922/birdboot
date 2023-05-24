package Http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletResponse {
    private Socket socket;
    //定义全局变量存放一些响应信息
    private int statusCode  = 200;
    private String statusReason = "OK";
    private File contentFile;
    private ByteArrayOutputStream baos;
    private Map<String,String> map = new HashMap<>();


    public HttpServletResponse(Socket socket){
        this.socket = socket;
    }
    //依次响应结果
    public void response() throws IOException {
        //发送前准备工作
        sendBefore();

        sendStatusLine();
        sendHeaders();
        sendContent();
    }

    //发送状态行
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1" + " " + statusCode + statusReason);
    }

    //发送响应头
    private void sendHeaders() throws IOException {

        Set<Map.Entry<String,String>> entrySet = map.entrySet();
        for(Map.Entry<String,String> e : entrySet){
            String name = e.getKey();
            String value = e.getValue();
            //调用方法依次将map集合存放的响应头信息写出
            println(name + ": " + value);
        }
        println("");
    }

    //发送正文
    private void sendContent() throws IOException {
        if (baos!=null){
            //有动态数据
            byte[] bytes = baos.toByteArray();
            System.out.println(bytes.length);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        }else{
            if(contentFile==null)return;
            try(
                    OutputStream outputStream =  socket.getOutputStream();
            ) {
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(contentFile));
                int len;
                byte[] bytes = new byte[1024];
                while((len = input.read(bytes)) != -1){
                    outputStream.write(bytes,0,len);
                }
            }
        }

    }

    //写出响应头
    private void println(String line) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(13);//写出回车换行符号
        outputStream.write(10);
    }

    //发送状态码
    public void sendRedirect(String path){
        statusCode = 302;
        statusReason = "Moved Temporarily";
        addHeader("Location",path);
    }

    //发送前准备工作
    private void sendBefore(){
        if (baos != null) {
            addHeader("Content-Length",baos.size() + "");//发送长度
        }
    }

    //添加像一头content-type
    public void setContentType(String mime){
        addHeader("Content-Type",mime);
    }












    public int getStatusCode() {
        return statusCode;
    }

    //设置状态码
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {

        return contentFile;
    }

    public void setContentFile(File contentFile) throws IOException {
        //将传送过来的变量赋值到自己的全局变量
        this.contentFile = contentFile;
        //获取文件类型
        String contentType = Files.probeContentType(contentFile.toPath());
        //如果文件类型未解析成功则不设置类型，将由浏览器自动解析
        if (contentType!=null){
            addHeader("Content-Type",contentType);
        }
        addHeader("Content-Length", String.valueOf(contentFile.length()));
    }

    //将传送过来响应头依次添加到map集合,再在便利集合写出
    public void addHeader(String name,String value){
        map.put(name,value);
    }

    public OutputStream getOutputStream(){
        if (baos == null) {
            baos =  new ByteArrayOutputStream();
        }
        return baos;
    }

    public PrintWriter getWriter(){
        OutputStream os = getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os,StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw,true);
        return pw;
    }




















}
