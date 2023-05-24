package Controller;

import Annotations.Controller;
import Annotations.RequestMapping;
import Http.HttpServletRequest;
import Http.HttpServletResponse;
import entity.User;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 处理用户请求
 */
@Controller
public class UserController {
    private static File userDir;//该目录用于保存所有注册用户文件(一堆的.obj文件)
    private static final Logger logger = Logger.getLogger(UserController.class);

            static{
                userDir = new File("./users");
                if(!userDir.exists()){//如果该目录不存在
                    userDir.mkdirs();
                }
            }

    @RequestMapping("userList")
    public void userList(HttpServletRequest request,HttpServletResponse response){
        try {
            logger.error("获取所有用户列表");
            File file = new File("./users");
            File[] files = file.listFiles();
            Map<String,User> map = new HashMap<>();

            for (int i = 0; i < files.length; i++) {
                File filer = new File(file,files[i].getName());
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filer)));
                map.put(files[i].getName(),(User) is.readObject());
                is.close();
            }
            System.out.println(map);
            response.setContentType("text/html;charset=utf-8");

            PrintWriter pw = response.getWriter();
                pw.println("<!DOCTYPE html>");
                pw.println("<html lang=\"en\">");
                pw.println("<head>");
                pw.println("<meta charset=\"UTF-8\">");
                pw.println("<title>用户列表</title>");
                pw.println("</head>");
                pw.println("<body>");
                pw.println("<center>");
                pw.println("<h1>用户列表</h1>");
                pw.println("<table border=\"1\">");
                pw.println("<tr>");
                pw.println("<td>用户名</td>");
                pw.println("<td>密码</td>");
                pw.println("<td>昵称</td>");
                pw.println("<td>年龄</td>");
                pw.println("<td>操作</td>");
                pw.println("</tr>");


                Set<Map.Entry<String,User>> set = map.entrySet();

                for (Map.Entry<String, User> us : set) {
                    User user = us.getValue();
                    pw.println("<tr>");
                    pw.println("<td>"+user.getUsername()+"</td>");
                    pw.println("<td>"+user.getPassword()+"</td>");
                    pw.println("<td>"+user.getNickname()+"</td>");
                    pw.println("<td>"+user.getAge()+"</td>");
                    pw.println("<td><a href='/deleteUser?username="+user.getUsername()+"'>删除</a></td>");
                    pw.println("</tr>");
                }

                pw.println("</table>");
                pw.println("</center>");
                pw.println("</body>");
                pw.println("</html>");


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @RequestMapping("regUser")
    public void register(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理用户注册!!!!!!!!!!!!!!!!!!!");
        //对应reg.html页面表单中<input name="username" type="text">
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String age = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+age);

        //对数据进行必要的验证工作
        if(username.isEmpty()||password.isEmpty()||nickname.isEmpty()||age.isEmpty()||
                !age.matches("[0-9]+")){
            //如果如何上述情况，则直接响应给用户一个注册失败提示页面，告知信息输入有误。

            response.sendRedirect("/reg_info_error.html");
            return;
        }
        //处理注册
        //将年龄转换为int值
        int age_ = 0;
        age_ = Integer.parseInt(age);
        User user = new User(username,password,nickname,age_);

        //参数1:当前File表示的文件所在的目录  参数2:当前文件的名字
        File userFile = new File(userDir,username+".obj");
        if(userFile.exists()){//文件已经存在说明该用户已经存在了!
            response.sendRedirect("/have_user.html");
            return;
        }

        try (
                FileOutputStream fos = new FileOutputStream(userFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(user);

            //响应注册成功页面给浏览器
            response.sendRedirect("/reg_success.html");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("logUser")
    public void loginUser(HttpServletRequest request,HttpServletResponse response) {
        try {
            String name = request.getParameter("username");
            String password = request.getParameter("password");

            File file = new File(userDir,name+".obj");
            System.out.println(name + "     " + password + "         " + file);
            if (!file.isFile())response.sendRedirect("/loginerror.html");
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            Object o = input.readObject();
            if (!(o instanceof User))response.sendRedirect("/loginerror.html");
            User user = (User)o;
            if (password.equals(user.getPassword())){
                System.out.println("密码正确");
                response.sendRedirect("/reg_success.html");
            }
            input.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("deleteUser")
    public void deleteUser(HttpServletRequest request,HttpServletResponse response){
        String username = request.getParameter("username");
        File file = new File("./users");
        File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".obj"));
        for (File f : files) {
            if (f.getName().equals(username+".obj")){
                File ff = new File(file,f.getName());
                ff.delete();
            }
        }
        response.sendRedirect("/userList");

    }





































}

