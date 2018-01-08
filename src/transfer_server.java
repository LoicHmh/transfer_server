/**
 * Created by limit on 2017/12/21.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.sqlite.JDBC;

import java.sql.*;


/**
 * Servlet implementation class UploadServlet
 */
public class transfer_server extends HttpServlet {
    private static int ANDROID=1;
    private static int PC=2;
    private static int platform = ANDROID;
    //private static int platform = PC;
    private static final long serialVersionUID = 1L;

    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";

    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException{

        Statement stmt = null;
        Connection var1 = null;

        try {
            Class.forName("org.sqlite.JDBC");
            var1 = DriverManager.getConnection("jdbc:sqlite:" + request.getServletContext().getRealPath("./") + File.separator + "test.db");
            stmt = var1.createStatement();
            System.out.println("Opened database successfully");
        } catch (Exception var3) {
            System.err.println(var3.getClass().getName() + ": " + var3.getMessage());
            System.exit(0);
        }
        PrintWriter writer = response.getWriter();
        request.setCharacterEncoding("UTF-8");
        if (request.getParameter("type").equals("1")) {

            String usrname = new String(request.getParameter("usrname"));
            String password = new String(request.getParameter("password"));
            response.setCharacterEncoding("UTF-8");

            try {
                String sql = "SELECT * FROM USER WHERE USERNAME = " + "\"" + usrname + "\"" + " AND PASSWORD = " + "\"" + password + "\"" + " ORDER BY ROWID DESC ;";
                System.out.println(sql);
                ResultSet rs = stmt.executeQuery(sql);
                int flag=0;
                JSONArray jsonArray=new JSONArray();

                while (rs.next()) {
                    JSONObject textJson=new JSONObject();
                    flag=1;
                    String fileName = rs.getString("PICTURENAME");
                    String model = rs.getString("STYLE");
                    String path = "/complete/" + usrname + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "_" + model + fileName.substring(fileName.lastIndexOf("."));
                    File file = new File(request.getServletContext().getRealPath("./") + path);
                    if (file.exists()) {
                        if (platform == PC) {
                            writer.print("<html>");
                            writer.println("<img src=\"" + "http://127.0.0.1:8080/" + path + "\"/>");
                            writer.print("</html>");
                        } else if (platform == ANDROID) {
                            //text = text + "http://127.0.0.1:8080/" + path + "\n";
                            textJson.put("photoAddress","http://127.0.0.1:8080/" + path);
                            textJson.put("usrname",usrname);
                            textJson.put("good",rs.getInt("GOOD"));
                            textJson.put("picname",fileName);
                            textJson.put("style",model);
                            jsonArray.add(textJson.toString());
                        }
                    }
                }
                if (platform==ANDROID && flag!=0){
                    JSONObject temp=new JSONObject();
                    temp.put("ok",1);
                    temp.put("data",jsonArray.toString());
                    writer.print(temp.toString());
                }
                if (flag==0){
                    sql = "SELECT * FROM USER WHERE USERNAME = " + "\"" + usrname + "\" LIMIT 1;";
                    System.out.println(sql);
                    rs = stmt.executeQuery(sql);
                    if (rs.next()){
                        JSONObject temp=new JSONObject();
                        temp.put("ok",0);
                        //temp.put("data",jsonArray.toString());
                        writer.print(temp.toString());
                    }
                    else {
                        JSONObject temp = new JSONObject();
                        temp.put("ok", 2);
                        //temp.put("data",jsonArray.toString());
                        writer.print(temp.toString());
                        //writer.println("<h1>" + "Wrong password or user doesn't exists." + "</h1>");
                    }
                }
                stmt.close();
                var1.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        else if (request.getParameter("type").equals("2")) {
            try{
                JSONArray jsonArray=new JSONArray();
                ResultSet rs = stmt.executeQuery("SELECT * FROM USER ORDER BY ROWID DESC LIMIT 20;");
                System.out.println("sql: " + "SELECT * FROM USER ORDER BY ROWID DESC LIMIT 20;");
                int count=0;
                while (rs.next() && count<10) {
                    JSONObject textJson=new JSONObject();
                    String fileName = rs.getString("PICTURENAME");
                    String model = rs.getString("STYLE");
                    String usrname = rs.getString("USERNAME");
                    String path = "/complete/" + usrname + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "_" + model + fileName.substring(fileName.lastIndexOf("."));
                    File file = new File(request.getServletContext().getRealPath("./") + path);
                    if (file.exists()) {
                        count++;
                        if (platform == PC) {
                            System.out.println("PC");
                            writer.print("<html>");
                            writer.println("<img src=\"" + "http://127.0.0.1:8080/" + path + "\"/>");
                            writer.print("</html>");
                        } else if (platform == ANDROID) {
                            textJson.put("photoAddress","http://127.0.0.1:8080/" + path);
                            textJson.put("usrname",usrname);
                            textJson.put("good",rs.getInt("GOOD"));
                            textJson.put("picname",fileName);
                            textJson.put("style",model);
                            jsonArray.add(textJson.toString());
                        }
                    }
                }
                if (platform==ANDROID){
                    writer.print(jsonArray.toString());
                }
                stmt.close();
                var1.close();
            }
            catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        else
        {
            try{
                stmt.close();
                var1.close();
            }
            catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }

    /**
     * 上传数据及保存文件
     */
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        // 检测是否为多媒体上传

        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            PrintWriter writer = response.getWriter();
            writer.println("<h1>" + "Error: Needs contain enctype=multipart/form-data" + "</h1>");
            writer.flush();
            return;
        }

        Statement stmt = null;
        Connection var1 = null;

        try {
            Class.forName("org.sqlite.JDBC");
            var1 = DriverManager.getConnection("jdbc:sqlite:" + request.getServletContext().getRealPath("./") + File.separator + "test.db");
            stmt = var1.createStatement();
            System.out.println("Opened database successfully");
        } catch (Exception var3) {
            System.err.println(var3.getClass().getName() + ": " + var3.getMessage());
            System.exit(0);
        }


        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 中文处理
        upload.setHeaderEncoding("UTF-8");

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;


        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }


        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            String fileName = null;
            String model = "rain_princess";
            String usrname = "";
            String password = "";
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator +
                                fileName.substring(0,fileName.lastIndexOf(".")) + "_" + model + fileName.substring(fileName.lastIndexOf("."));
                        File storeFile = new File(filePath);
                        // 在控制台输出文件的上传路径
                        System.out.println(filePath);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter writer = response.getWriter();

                        //writer.println(" <a href=" + "http://localhost:8080\\upload\\" + fileName + ">" + fileName + "</a>");
                        //writer.println(" <a href=" + "http://10.163.97.10:8080\\upload\\" + fileName + ">" + fileName + "</a>");
                        writer.println(fileName);
                    }
                    else{
                        if (item.getFieldName().equals("model"))
                        {
                            model = new String(item.getString());
                            System.out.println(model);
                        }
                        if (item.getFieldName().equals("usrname"))
                        {
                            usrname = new String(item.getString());
                            System.out.println(usrname);
                        }
                        if (item.getFieldName().equals("password"))
                        {
                            password = new String(item.getString());
                            System.out.println(password);
                        }
                    }
                }
                ResultSet rs = stmt.executeQuery( "SELECT * FROM USER WHERE USERNAME = " + "\"" + usrname + "\"" + " LIMIT 1" + ";");
                if (rs.next()){
                    PrintWriter writer = response.getWriter();
                    if (rs.getString("PASSWORD").equals(password) &&
                            ((!rs.getString("PICTURENAME").equals(fileName)) ||
                                    (!rs.getString("STYLE").equals(model)))){
                        String sql = "INSERT INTO USER VALUES(" + "\"" + usrname + "\",\"" + password + "\",\"" + fileName + "\",\"" +  model + "\"," + "0" + ");";
                        System.out.println("INSERT COMMAND: " + sql);
                        stmt.executeUpdate(sql);
                        File workDir = new File(request.getServletContext().getRealPath("./") + "\\complete\\" + usrname);
                        if (!workDir .exists()) {
                            workDir .mkdir();
                        }
                        MyThread mythread1 = new MyThread(request.getServletContext().getRealPath("./"), fileName, model, usrname);
                        mythread1.start();
                    }
                    else {
                        writer.println("<h1>" + "Error: The password is wrong or the picture exists." + "</h1>");
                    }
                    stmt.close();
                    var1.close();
                }
                else {
                    String sql = "INSERT INTO USER VALUES(" + "\"" + usrname + "\",\"" + password + "\",\"" + fileName + "\",\"" +  model + "\"," + "0" + ");";
                    System.out.println("INSERT COMMAND: " + sql);
                    stmt.executeUpdate(sql);
                    File workDir = new File(request.getServletContext().getRealPath("./") + "\\complete\\" + usrname);
                    if (!workDir .exists()) {
                        workDir .mkdir();
                    }
                    MyThread mythread1 = new MyThread(request.getServletContext().getRealPath("./"), fileName, model, usrname);
                    mythread1.start();
                    stmt.close();
                    var1.close();
                }
            }
        } catch (Exception ex) {
            PrintWriter writer = response.getWriter();
            writer.println("<h1>" + "Error: " + ex.getMessage() + "</h1>");
        }
    }
    public class MyThread extends Thread {
        private String path;
        private String fileName;
        private String model;
        private  String usrname;
        public MyThread(String path, String fileName, String model, String usrname){
            this.path=path;
            this.fileName=fileName;
            this.model=model;
            this.usrname=usrname;
        }
        public void run() {
            Process process = null;
            try {
                Runtime runtime = Runtime.getRuntime();
                String cmd = "python " + path + "\\fast-style-transfer\\evaluate.py" +
                        " --checkpoint " + path +"\\fast-style-transfer\\models\\" + model + ".ckpt" +
                        " --in-path " + path +"\\upload\\" + fileName.substring(0,fileName.lastIndexOf(".")) + "_" + model + fileName.substring(fileName.lastIndexOf(".")) +
                        " --out-path " + path + "\\complete\\" + usrname;
                System.out.println("Python command: " + cmd);
                process = runtime.exec(cmd);
                process.waitFor();
                System.out.println("Finished");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }
    }
}