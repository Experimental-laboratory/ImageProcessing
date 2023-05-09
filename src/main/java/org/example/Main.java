package org.example;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import org.example.cli.blur.Blur;

import java.util.logging.Logger;

@Cli(name = "ImageProcessing",
defaultCommand = Help.class,
commands = {Help.class, Blur.class})
public class Main
{
    static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args)
    {
        com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<>(Main.class);

        Runnable act = null;

        try
        {
            act = cli.parse(args);
        }
        catch(com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException |
              ParseOptionConversionException ex)
        {
            logger.info(ex.getMessage());
        }

        if(act != null)
            act.run();
    }
}