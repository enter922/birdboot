package Controller;

import Annotations.Controller;
import Annotations.RequestMapping;
import Http.HttpServletRequest;
import Http.HttpServletResponse;
import qrcode.QRCodeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
public class ToolsController {

    public static void main(String[] args) throws Exception {
//        String line = "花落花开";
//        QRCodeUtil.encode(line,"./qr.jpg");

        QRCodeUtil.encode("花落话","./qr.jpg","./rr.jpg",true);
        File file = new File("./src/main/resources/static/logo.png");
        FileInputStream f = new FileInputStream(file);

    }

    @RequestMapping("img")
    public void img(HttpServletRequest request, HttpServletResponse response){
        System.err.println("img");
//        String img = request.getParameter("img");
        OutputStream os  = response.getOutputStream();
        try {
            QRCodeUtil.encode("一弦一柱思华年","./qr.jpg",os,true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





































}
