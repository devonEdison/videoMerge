package com.dragonplayer.merge.utils;

import java.io.*;

public class FileMover {

    String destination;
    InputStream inputStream;

    public FileMover(InputStream inputstream, String dstPath) {
        inputStream = inputstream;
        destination = dstPath;
    }

    public void moveIt() throws IOException {
    	
        BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(new File(destination)));
        byte temp[] = new byte[1024];
        
        do
        {
            int len = inputStream.read(temp);
            
            if(len < 0) {
                bufferedoutputstream.flush();
                bufferedoutputstream.close();
                return;
            }
            
            bufferedoutputstream.write(temp, 0, len);
        } while(true);
    }
}
