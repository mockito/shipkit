package org.shipkit.internal.exec;

import org.shipkit.internal.util.ArgumentValidation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * On top of standard output stream, this implementation adds prefixing of the output.
 * It is super useful to make the log output clear.
 */
public class ExternalProcessStream extends OutputStream {

    private final PrintStream output;
    private final String outputPrefix;

    private StringBuilder line = new StringBuilder();
    private boolean writePrefix = true;

    /**
     * @param outputPrefix the prefix to be used
     * @param output
     */
    public ExternalProcessStream(String outputPrefix, PrintStream output) {
        ArgumentValidation.notNull(outputPrefix, "outputPrefix", output, "output");
        this.outputPrefix = outputPrefix;
        this.output = output;
    }

    @Override
    public void write(int b) throws IOException {
        //keep building the line
        line.append((char) b);
        //maybe write to output
        maybeOutput(b);

        if (b == '\n') {
            //clear the line
            line = new StringBuilder();
            //next time, before anything is printed to output, print the prefix
            writePrefix = true;
        }
    }

    private void maybeOutput(int b) {
        //optionally write prefix so that terminal looks clean
        if (writePrefix) {
            output.print(outputPrefix);
            writePrefix = false;
        }
        //write to output
        output.write(b);
    }
}
