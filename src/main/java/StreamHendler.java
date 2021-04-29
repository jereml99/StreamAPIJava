import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamHendler {
    private Path src;
    private Path dst;

    public StreamHendler(Path src, Path dst) {
        this.src = src;
        this.dst = dst;
    }

    public void imageStreamPiplene(){
        List<Path> files;
        try (Stream<Path> stream = Files.list(src)){
            files = stream.collect(Collectors.toList());
            Stream<Path> pathStream = files.stream();
            Stream<Pair<String, BufferedImage>> pairinputStream = makePairStream(pathStream);
            pairinputStream.forEach(pair -> {
                    BufferedImage newImage = manipulateImage(pair.getRight());
            try {
                ImageIO.write(newImage,"JPG",new File(dst+"\\"+pair.getLeft()));
            }
            catch (IOException ex)
            {
                System.out.println(ex);
            }
            });

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    public void imageStreamPiplene(int parallelism){
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        try {
            pool.submit(() ->
                    {
                            List<Path> files;
                    try (Stream<Path> stream = Files.list(src)) {
                    files = stream.collect(Collectors.toList());
                    Stream<Path> pathStream = files.stream().parallel();
                    Stream<Pair<String, BufferedImage>> pairinputStream = makePairStream(pathStream).collect(Collectors.toList()).parallelStream(); //co tu sie dzieje
                    pairinputStream.forEach(pair -> {
                            BufferedImage newImage = manipulateImage(pair.getRight());
                    try {
                        ImageIO.write(newImage, "JPG", new File(dst + "\\" + pair.getLeft()));
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                        });

                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }).get();
            pool.shutdown();
        }
        catch (InterruptedException | ExecutionException e)
        {
            System.out.println(e);
        }


    }

    private Stream<Pair<String, BufferedImage>> makePairStream(Stream<Path> pathStream){
        return pathStream.map(path -> {
        try{
            BufferedImage image = ImageIO.read(path.toFile());
            String name = path.getFileName().toString();
            Pair<String, BufferedImage> pair = Pair.of(name, image);
            return pair;
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
        return null;
        });

    }
    private BufferedImage manipulateImage(BufferedImage original){
        BufferedImage image = new BufferedImage(original.getWidth(),
                original.getHeight(),
                original.getType());
        
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = original.getRGB(i, j);
                Color color = new Color(rgb);
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                Color outColor = new Color(red, blue, green);
                int outRgb = outColor.getRGB();
                image.setRGB(i, j, outRgb);
            }
        }
        return image;
    }
}

