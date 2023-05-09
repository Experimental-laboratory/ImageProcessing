package org.example.cli.blur;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.restrictions.Pattern;
import com.github.rvesse.airline.annotations.restrictions.Required;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

@Command(name = "blur",
        description = "Make blur for image")
public class Blur implements Runnable
{
    @Inject
    private HelpOption<Blur> helpOption;

    @Required
    @Option(type = OptionType.COMMAND,
    name={"-in", "--input_image_path"})
    @Pattern(pattern = "(?i).+\\.jpg")
    protected String inputImagePath;

    @Required
    @Option(type = OptionType.COMMAND,
    name = {"-out", "--out_image_path"})
    @Pattern(pattern = "(?i).+\\.jpg")
    protected String outImagePath;

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"-obw", "--odd_blur_width"})
    protected int oddBlurWidth;

    private void checkInputParams() throws InputParamException
    {
        if(oddBlurWidth % 2 != 1)
            throw new InputParamException("Blur width not odd");

        if(!(new File(inputImagePath).exists()))
            throw new InputParamException("File ".concat(inputImagePath).concat(" not exists"));
    }

    @Override
    public void run()
    {
        try
        {
            checkInputParams();
        } catch (InputParamException e)
        {
            e.printStackTrace();
            return;
        }


        File srcFile = new File(inputImagePath);
        BufferedImage inputImage = null;

        try
        {
            inputImage = ImageIO.read(srcFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        BufferedImage outImage = blur(inputImage);

        File outFile = new File(outImagePath);
        try
        {
            ImageIO.write(outImage, "jpg", outFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private BufferedImage blur(final BufferedImage input)
    {
        final int allWidth = input.getWidth();
        final int allHeight = input.getHeight();

        int[] in = input.getRGB(0, 0, allWidth, allHeight, null, 0, allWidth);
        int[] out = new int[in.length];

        RecursiveAction recursiveBlur = new RecursiveActionBlur(in, 0, in.length, oddBlurWidth, out);
        ForkJoinPool threadsPool = new ForkJoinPool();
        threadsPool.invoke(recursiveBlur);

        BufferedImage output = new BufferedImage(allWidth, allHeight, BufferedImage.TYPE_3BYTE_BGR);
        output.setRGB(0, 0, allWidth, allHeight, out, 0, allWidth);
        return output;
    }
}
