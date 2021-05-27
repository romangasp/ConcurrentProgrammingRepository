import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class Main
{
    public static void main(String[] args) throws InterruptedException {
        Timestamp timestampInicio = new Timestamp(System.currentTimeMillis());
        int cantLibros      = 24;
        int cantRevisores   = 10;//10;
        int cantLectores    = 20;//20;
        Thread[] lectores   = new Thread[cantLectores];
        Thread[] revisores  = new Thread[cantRevisores];
        Libro[]  libros     = new Libro[cantLibros];
        //Control control     = new Control();

        //Libro libro = new Libro();
        //Thread escritor = new Thread(new Revisor(new Control(),libro));

        /*Instancio libros*/
        SimpleLog log = new SimpleLog(libros);
        Thread logger = new Thread(log);
        logger.start();

        for(int i = 0; i < cantLibros; i++)
        {
            libros[i] = new Libro("Libro " + i,i,cantRevisores,cantLectores);
        }

        /*Instancio hilos de lectores*/
        for(int i = 0; i < cantLectores; i++)
        {
            lectores[i] = new Thread(new Lector(libros),"Lector " + i);
        }


        /*Instancio hilos de escritores*/
        for(int i = 0; i < cantRevisores; i++)
        {
            revisores[i] = new Thread(new Revisor(libros),"Escritor " + i);
        }

        /*Arranco hilos de escritores*/
        for(int i = 0; i < cantRevisores; i++)
        {
            revisores[i].start();
        }

        /*Arranco hilos de lectores*/
        for(int i = 0; i < cantLectores; i++)
        {
            lectores[i].start();
        }

        try
        {
            SECONDS.sleep(3);
            System.out.println("<-----EMPIEZA TIEMPO LIMITE MINIMO---->");
            SECONDS.sleep(7);
            System.out.println("<-----LIMITE MÁXIMO DE TIEMPO---->");
            imprimirEstadosLibros(libros);
        }
        catch (InterruptedException i)
        {
            i.printStackTrace();
        }

        for(Thread thread : lectores)
        {
            thread.join();
        }
        for(Thread thread : revisores)
        {
            thread.join();
        }

        Timestamp timestampFinal = new Timestamp(System.currentTimeMillis());
        long miliseconds = timestampFinal.getTime() - timestampInicio.getTime();
        System.out.printf("Timestamp inicio: %s%n", timestampInicio);
        log.write("Timestamp inicio: " + timestampInicio.toString());
        System.out.printf("Timestamp final: ", timestampFinal);
        log.write("Timestamp final: " +timestampFinal.toString());
        System.out.printf("Duracion en milisegundos: %d%n", miliseconds);
        log.write("Duracion en milisegundos: " + Long.toString(miliseconds));
        log.setFinishMain(true);
    }

    public static void imprimirEstadosLibros(Libro[] libros)
    {
        for(int i = 0; i < libros.length; i++)
        {
            System.out.println("Estado " + libros[i].getNombre() + ": " + libros[i].getLecturas() + " Lecturas - Escritores adentro: " + libros[i].getEscritoresAdentro2());
        }
    }

}
