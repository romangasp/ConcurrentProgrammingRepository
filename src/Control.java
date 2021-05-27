import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Control
{

    //Lock lockRevision;
    Semaphore sRevison,sLectura;
    int lectAdentro,escritAdentro,lecTotales,lectoresLeyendo;
    String nombreLibro;
    ReadWriteLock lockLectores,lockEscritores,lockLeyendo;
    final Object lockPermEscritura,lockPermLectura,lockDevolverLock;


    public Control(int lecTotales,String nombreLibro)
    {
        sRevison            = new Semaphore(1,true);     //Semaforo que permite la entrada o no de un Escritor
        sLectura            = new Semaphore(lecTotales,true);   //Semaforo que permite la entrada o no de un Lector
        lectAdentro         = 0;    //Indica cuantos lectores tienen intenciÃ³n de leer actualmente el libro
        lectoresLeyendo     = 0;    //Indica cuantos lectores ya estÃ¡n leyendo
        escritAdentro       = 0;    //Indica cuantos escritores escriben y/o esperan (solo uno por vez podrÃ¡ escribir)
        lockEscritores      = new ReentrantReadWriteLock();
        lockLectores        = new ReentrantReadWriteLock();
        lockLeyendo         = new ReentrantReadWriteLock();
        lockPermLectura     = new Object();
        lockPermEscritura   = new Object();
        lockDevolverLock    = new Object();
        this.lecTotales     = lecTotales;
        this.nombreLibro    = nombreLibro;
    }

    /*Metodo usado por cada hilo revisor/escritor para saber si puede avanzar
    * o no en la revision de un libro. Si no puede, este metodo lo duerme*/
    public void permisoHacerRevision()
    {
        incrementarEscritores();
    }

    /*Metodo usado por cada hilo lector para saber si puede avanzar
     * o no en la lectura de un libro. Si no puede, este metodo lo duerme*/
    public void permisoHacerLectura()
    {
        /*SOLO DEJO QUE ENTREN DE A UNO LOS LECTORES A SOLICITAR
        * EL PASE PARA LEER*/
        synchronized (lockPermLectura)
        {
            incrementarLectores();  //INCREMENTO LA CANTIDAD DE LECTORES QUE SOLICITAN ENTRAR O ESTAN LEYENDO

            /*MIENTRAS HAYA ESCRITORES, EL LECTOR QUE PUDO ENTRAR QUEDARÃ� DORMIDO 10ms
            * Y LUEGO VOLVERA A PREGUNTAR SI HAY ESCRITORES. CUANDO NO HAYA MAS,
            * PODRÃ� AVANZAR*/
            while (getEscritoresAdentro() != 0)
            {
                try {
                    System.out.println(Thread.currentThread().getName() + ": ESPERA: Esperando disponibilidad - " + nombreLibro);
                    TimeUnit.MILLISECONDS.sleep(10); //LO DUERMO 10 ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": ESPERA: ya puede leer - " + nombreLibro);
        }

        /*ANTES DE TERMINAR DE CONCEDER EL PERMISO, INCREMENTO LA CANTIDAD DE LECTORES
        * LEYENDO. POR AHORA NO TIENE UTILIDAD, PERO PUEDE SERVIR*/
        incLectoresLeyendo();
    }

    /*El lector termina de hacer su trabajo y libera recurso*/
    public void terminarLectura()
    {
        decrementarLectores();
        decLectoresLeyendo();

        /*SOLO SI NO HAY MAS LECTORES LEYENDO Y EL LOCK NO ESTÃ�
         * LIBERADO, SE LO LIBERA PARA QUE EL ESCRITOR PUEDA SEGUIR*/
    }

    /*El escritor termina de hacer su trabajo y libera recurso*/
    public void terminarRevision()
    {
        decrementarEscritores();
    }

    public int getLectoresAdentro()
    {
        /*CONTROL DE CONCURRENCIA*/
        lockLectores.readLock().lock();
            int n = lectAdentro;
        lockLectores.readLock().unlock();
        /*FIN DE CONTROL*/

        return n;
    }

    public int getEscritoresAdentro()
    {
        /*CONTROL DE CONCURRENCIA*/
        lockEscritores.readLock().lock();
            int n = escritAdentro;
        lockEscritores.readLock().unlock();
        /*FIN DE CONTROL*/

        return n;
    }

    public void incrementarEscritores()
    {
        /*INICIO SECCION CRITICA*/
        lockEscritores.writeLock().lock();       
        escritAdentro++;
        lockEscritores.writeLock().unlock();
        /*FIN SECCCION CRITICA*/
    }


    public void incrementarLectores()
    {
            lectAdentro++;
    }

    public void incLectoresLeyendo()
    {
        /*INICIO SECCION CRITICA*/
        lockLeyendo.writeLock().lock();
        lectoresLeyendo++;
        lockLeyendo.writeLock().unlock();
        /*FIN SECCION CRITICA*/
    }

    public void decLectoresLeyendo()
    {
        /*INICIO SECCION CRITICA*/
        lockLeyendo.writeLock().lock();
        lectoresLeyendo--;
        lockLeyendo.writeLock().unlock();
        /*FIN SECCION CRITICA*/
    }

    public int getLectoresLeyendo()
    {
        /*INICIO SECCION CRITICA*/
        lockLeyendo.readLock().lock();
        int i = lectoresLeyendo;
        lockLeyendo.readLock().unlock();
        /*FIN SECCION CRITICA*/

        return i;
    }

    public void decrementarEscritores()
    {
        /*INICIO SECCION CRITICA*/
        lockEscritores.writeLock().lock();   
        escritAdentro--;
        lockEscritores.writeLock().unlock();
        /*FIN SECCCION CRITICA*/
    }


    public void decrementarLectores()
    {
        /*INICIO SECCION CRITICA*/
        lockLectores.writeLock().lock();
        lectAdentro--;
        lockLectores.writeLock().unlock();
        /*FIN SECCION CRITICA*/
    }

}
