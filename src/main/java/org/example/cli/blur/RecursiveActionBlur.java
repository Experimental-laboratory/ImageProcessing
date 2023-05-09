package org.example.cli.blur;

import java.util.concurrent.RecursiveAction;

public class RecursiveActionBlur extends RecursiveAction
{
    /**
     *  Размер маски для усрежнения
     */
    private final int blurWidth;

    /**
     * Размер данных для обработки
     */
    private final int length;

    /**
     * Индекс массива, с которого начнется обработка
     */
    private final int start;

    /**
     * Массив для формирования выходного изображения
     */
    private final int[] out;

    /**
     * Массив, сформированный из входного изображения
     */
    private final int[] in;

    /**
     * Порог, по которому принимается решение рекурсивного разбиения задачи на подзадачи
     * меньшего размера
     */
    private static final int treshold = 1000000;

    public RecursiveActionBlur(int[] in, int start, int length, int blurWidth, int[] out)
    {
        this.blurWidth = blurWidth;
        this.length = length;
        this.start = start;
        this.out = out;
        this.in = in;
    }

    private void computeHere()
    {
        int halfPixels = (blurWidth - 1) / 2;

        for(int i = start; i < start + length; ++i)
        {
            float r =0, g = 0, b = 0;

            for(int step = -halfPixels; step <= halfPixels; ++step)
            {
                int index = Math.min(Math.max(i + step, 0), in.length - 1);

                int pixel = in[index];

                r += (float) ((pixel & 0x00ff0000) >> 16) / blurWidth;
                g += (float) ((pixel & 0x0000ff00) >> 8) / blurWidth;
                b += (float) (pixel & 0x000000ff) / blurWidth;
            }

            int resultPixel = (0xff000000) | (((int)r) << 16) | (((int)g) << 8)| (((int)b));
            out[i] = resultPixel;
        }
    }

    @Override
    protected void compute()
    {
        if(length < treshold)
        {
            computeHere();
            return;
        }

        int half = length / 2;

        invokeAll(new RecursiveActionBlur(in, start, half, blurWidth, out),
                new RecursiveActionBlur(in, start + half, length - half, blurWidth, out));
    }
}
