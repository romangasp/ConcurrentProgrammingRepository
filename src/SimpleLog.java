/**
 * Utilities log
 */
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SimpleLog implements Runnable{

    private final static DateFormat df = new SimpleDateFormat ("yyyy.MM.dd  hh:mm:ss ");
    public boolean finishMain;
    private static String fileName;
    Libro[]  libros;

    public SimpleLog(Libro[] libros) {
        setFinishMain(false);
        this.fileName = "./log.txt";
        this.libros = libros;
    }

    public void setFinishMain(boolean finishMain){
        this.finishMain = finishMain;
    }

    public boolean getFinishMain(){
        return this.finishMain;
    }



    public void write() {
        try {
            File file = new File(this.fileName);
            Date now = new Date();
            String currentTime = SimpleLog.df.format(now);
            FileWriter aWriter = new FileWriter(this.fileName, true);
            BufferedWriter bw = new BufferedWriter(aWriter);
            bw.newLine();
            bw.write(this.df.format(now) + "    - Revisados por todos: " + this.checkVersionesFinales());
            bw.newLine();
            bw.write(this.df.format(now) + "    -  Leidos por todos: " + this.cheakLeidosPorTodos());
            bw.newLine();
            bw.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }

    public void write(String msg) {
        try {
            File file = new File(this.fileName);
            Date now = new Date();
            String currentTime = SimpleLog.df.format(now);
            FileWriter aWriter = new FileWriter(this.fileName, true);
            BufferedWriter bw = new BufferedWriter(aWriter);
            bw.newLine();
            bw.write(msg);
            bw.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }
    private static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string";
        }
    }

    private int checkVersionesFinales() {
        int revisiones = 0;
        for (int i=0; i<libros.length; i++) {
            if (libros[i].getRevisiones() == 10) {
                revisiones += 1;
            }
        }
        return revisiones;
    }

    private int cheakLeidosPorTodos() {
        int lecturas = 0;
        for (int i=0; i<libros.length; i++) {
            if (libros[i].getLecturas() == 20) {
                lecturas += 1;
            }
        }
        return lecturas;
    }

    public void run() {
        this.write("----------INICIO---------");
        while (!getFinishMain()) {
            this.write();
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                this.setFinishMain(true);
                this.write();
            }
        }
        this.write("---------FIN---------");
    }
}