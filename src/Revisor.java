public class Revisor implements Runnable
{
    Libro[] libros;

    boolean[] librosRevisados;


    public Revisor(Libro[] libros)
    {
        this.libros             = libros;
        librosRevisados         = new boolean[libros.length];
        resetLecturaTerminada();
    }


    public void run()
    {

        while (getCantLibrosSinRevisar() > 0)
        {
            Libro l = getLibroRandom();
            l.hacerRevision();
            librosRevisados[l.getId()] = true;
        }
        System.out.printf("Termino hilo %s%n", Thread.currentThread());
    }


    private Libro getLibroRandom()
    {
        Libro[] librosSinREvisar = getLibrosSinRevisar();
        int indiceRandom = (int) (Math.random()*(getCantLibrosSinRevisar()-1));
        return librosSinREvisar[indiceRandom];

    }

    private Libro[] getLibrosSinRevisar()
    {
        Libro[] librosSinREvisar = new Libro[libros.length];

        int posicion = 0;
        for(int i = 0; i < libros.length; i++)
        {
            if(!librosRevisados[i])
            {
                librosSinREvisar[posicion] = libros[i];
                posicion++;
            }
        }

        return librosSinREvisar;
    }

    /*Devuelve la cantidad de libros que aun no revisó/firmó el escritor*/
    private int getCantLibrosSinRevisar()
    {
        int n = 0;
        for(int i = 0; i < libros.length; i++)
        {
            if(!librosRevisados[i])
            {
                n++;
            }
        }
        return n;
    }


    private void resetLecturaTerminada()
    {
        for(int i = 0; i < libros.length; i++)
        {
            librosRevisados[i] = false;
        }
    }


}
