import java.nio.file.Path;

public class Lab6 {



    public static void main(String[] args) {
        Path src = Path.of(args[0]);
        Path dst =Path.of(args[1]);
        StreamHendler streamHendler = new StreamHendler(src,dst);

        long time = System.currentTimeMillis();
        streamHendler.imageStreamPiplene();
        System.out.println("Not parallel time: "+(System.currentTimeMillis() - time) +"ms");

        for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
            time = System.currentTimeMillis();
            streamHendler.imageStreamPiplene(i);
            System.out.println("Time for "+i+ "-Threads: "+(System.currentTimeMillis() - time) +"ms");

        }

    }
}
