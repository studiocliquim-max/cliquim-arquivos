package com.studiocliquim.arquivos;

import java.io.IOException;

/** Power-of-two decoding bound: at most 2048 px on either axis, independent of input size. */
public final class ImageSizing {
    private ImageSizing() {}
    public static int sample(int width,int height) throws IOException {
        if(width<=0||height<=0)throw new IOException("Dimensões indisponíveis.");
        int sample=1;
        while(((long)width+sample-1)/sample>2048||((long)height+sample-1)/sample>2048)
            sample*=2;
        return sample;
    }
}
