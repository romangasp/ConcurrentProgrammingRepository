public class Lector implements Runnable
{
    Libro[] libros;
    int cantLibrosDisp;
    boolean[] lecturaTerminada;
    boolean trabajoTerminado;
    public Lector(Libro[] libros)
    {
        this.libros         = libros;
        cantLibrosDisp      = libros.length;
        lecturaTerminada    = new boolean[libros.length];
        trabajoTerminado    = false;
        resetLecturaTerminada();
    }

    public void run()
    {
        /*PRUEBAS*/
        while (!trabajoTerminado)
        {
            trabajoTerminado = true;
            for(int i = 0; i < libros.length; i++)
            {
                if(!lecturaTerminada[i])
                {
                    lecturaTerminada[i] = libros[i].leer();
                    trabajoTerminado    = false;
                }
            }
        }
        System.out.printf("Termino hilo %s%n", Thread.currentThread());

    }

    private void resetLecturaTerminada()
    {
        for(int i = 0; i < libros.length; i++)
        {
            lecturaTerminada[i] = false;
        }
    }

}
