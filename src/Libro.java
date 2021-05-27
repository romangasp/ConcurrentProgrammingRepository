import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Libro
{
    Semaphore sPrueba;
    Control control;
    String nombre;
    int id,revisiones,lecturas,revtotales,lecTotales;
    static Object llaveLectura,llaveRevision;
    ReadWriteLock lockCntLect,lockCntRevis;

    public Libro(String nombre,int id,int revtotales,int lecTotales)
    {
        sPrueba         = new Semaphore(0,true);
        this.nombre     = nombre;
        this.id         = id;
        this.revtotales = revtotales;
        this.lecTotales = lecTotales;
        control         = new Control(lecTotales,nombre);
        revisiones      = 0;
        lecturas        = 0;
        llaveLectura    = new Object();
        llaveRevision   = new Object();
        lockCntRevis    = new ReentrantReadWriteLock(); //Lock que permitira leer y escribir correctamente 'escrituras'
        lockCntLect     = new ReentrantReadWriteLock(); //Lock que permitira leer y escribir correctamente 'revisiones'
    }

    /*METODO DE PRUEBA. NO SIRVE PARA EL PROYECTO*/
    public void hacerAlgo(String hilo)
    {
        try
        {
            System.out.println("Esperando: " + hilo + "...");
            sPrueba.acquire();
            //Thread.sleep(1000);
            System.out.println("Haciendo la tarea: " + hilo);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /*METODO DE PRUEBA. NO SIRVE PARA EL PROYECTO*/
    public void liberarSemaforo()
    {
        System.out.println("Intentando liberar semaforo...");
        sPrueba.release(10);
    }

    public void hacerRevision()
    {
        control.permisoHacerRevision();
        System.out.println(Thread.currentThread().getName() + ": EMPEZÓ A REVISAR (" + nombre + ") - TOTAL REVISANDO: " + control.getEscritoresAdentro());
        /*synchronized (llaveLectura)
        {
            System.out.println(Thread.currentThread().getName() + ": SECCCION CRITICA E1 (" + nombre + ")");
            System.out.println(Thread.currentThread().getName() + ": SECCCION CRITICA E2 (" + nombre + ")");
            System.out.println(Thread.currentThread().getName() + ": SECCCION CRITICA E3 (" + nombre + ")");
            //System.out.println(Thread.currentThread().getName() + ": SECCCION CRITICA E4 (" + nombre + ")");
            //System.out.println(Thread.currentThread().getName() + ": SECCCION CRITICA E5 (" + nombre + ")");
        }*/
        try
        {

            //String auxiliar = Thread.currentThread().getName();
            long duracion = (long)(Math.random()*200);

            /*PRUEBAS*/
            //System.out.println(Thread.currentThread().getName() + ": Por hacer revision (" + nombre + ")...");
            TimeUnit.MILLISECONDS.sleep(duracion);
            incrementarRevisiones();


            //System.out.println(Thread.currentThread().getName() + ": Soy una variable local de " + nombre + ": " + auxiliar);
        }
        catch (InterruptedException i)
        {
            i.printStackTrace();
        }

        //System.out.println(Thread.currentThread().getName() + ": POR TERMINAR DE REVISAR (" + nombre + ")");
        control.terminarRevision();//ESCRITOR TERMINA DE REVISAR LIBRO Y AVISA AL CONTROL
        //System.out.println(Thread.currentThread().getName() + ": REVISION TERMINADA (" + nombre + ")- TOTAL REVISANDO: " + control.getEscritoresAdentro());
    }

    public boolean leer()
    {
        //System.out.println(Thread.currentThread().getName() + ": PIDE PERMISO PARA LEER (" + nombre + ")");
        control.permisoHacerLectura();
        System.out.println(Thread.currentThread().getName() + ": EMPEZÓ A LEER (" + nombre + ")");
        int revisionLocal = getRevisiones();

        try
        {
            /*PRUEBAS*/
            //System.out.println(Thread.currentThread().getName() + ": Por hacer lectura (" + nombre + ") Revision nro: " + revisionLocal);
            long duracion = (long)(Math.random()*200);
            TimeUnit.MILLISECONDS.sleep(duracion);
        }
        catch (InterruptedException i)
        {
            i.printStackTrace();
        }

        if(revisionLocal == revtotales)
        {
            incrementarLecturas();
            int f = getLecturas();

            /*ES SOLO PARA PRUEBAS DE PRINT*/
            if(f == 20)
            {
                //System.out.println(Thread.currentThread().getName() + ": FIN lectura " + nombre + " - Revisiones: " + revisionLocal + " - Lecturas: " + f);
            }

            control.terminarLectura();//LECTOR TERMINA DE LEER Y AVISA AL CONTROL
            return true;
        }
        else
        {
            control.terminarLectura();//LECTOR TERMINA DE LEER Y AVISA AL CONTROL
            //System.out.println(Thread.currentThread().getName() + ": Sale de lectura " + nombre + " - Revisiones: " + revisionLocal);
            return false;
        }
    }

    public int getId()
    {
        return id;
    }

    /*DEVUELVE LA CANTIDAD DE ESCRITORES QUE HAN REVISADO EL LIBRO HASTA EL MOMENTO*/
    public int getRevisiones()
    {
        /*CONTROL DE CONCURRENCIA*/
        lockCntRevis.readLock().lock();
            int revisionesAux = revisiones;
            //System.out.println(Thread.currentThread().getName() + ": valor revisiones obtenido - " + nombre);
        lockCntRevis.readLock().unlock();
        /*FIN CONTROL*/

        return revisionesAux;
    }

    private void incrementarRevisiones()
    {

        /*INICIO SECCION CRITICA*/
        lockCntRevis.writeLock().lock();
        //System.out.println(Thread.currentThread().getName() + ": por incrementar revisiones - " + nombre);
        revisiones++;
        //System.out.println(Thread.currentThread().getName() + ": termino incremento revisiones - " + nombre);
        lockCntRevis.writeLock().unlock();
        /*FIN SECCION CRITICA*/
    }

    public int getLecturas()
    {
        lockCntLect.readLock().lock();
            int lecturasAux = lecturas;
        lockCntLect.readLock().unlock();

        return lecturasAux;
    }

    private void incrementarLecturas()
    {
        lockCntLect.writeLock().lock();
            lecturas++;
        lockCntLect.writeLock().unlock();
    }

    public String getNombre()
    {
        return nombre;
    }

    public int getEscritoresAdentro2()
    {
        return control.getEscritoresAdentro();
    }
}
