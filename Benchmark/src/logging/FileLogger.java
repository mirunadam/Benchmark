package logging;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger implements ILogger {

    private PrintWriter pw;

    public FileLogger(String fullPath) {

        try {
            BufferedWriter logFile = new BufferedWriter(new FileWriter(
                    fullPath, false));
            pw = new PrintWriter(logFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void write(String string) {
        pw.println(string);
    }

    @Override
    public void write(long value) {
        pw.println(String.valueOf(value));
    }

    @Override
    public void write(Object... values) {
        String s = "";
        for (Object o : values)
            s += o.toString() + " ";
        pw.println(s);
    }


    public void writeTime(long value, TimeUnit unit) {
        double converted = unit.fromNano(value);
        pw.println(unit.format(converted));
    }

    public void writeTime(String string, long value, TimeUnit unit) {
        double converted = unit.fromNano(value);
        pw.println(string + " " + unit.format(converted));;
    }

    public void close() {
        if (pw != null)
            pw.close();
    }
}
