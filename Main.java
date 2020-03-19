package encryptdecrypt;

import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void main(String[] args) {
        String algorithm = "shift";
        String mode = "enc";
        String sequence = "";
        String pathIn = "";
        String pathOut = "";
        int key = 0;
        boolean dataUsed = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-alg":
                    algorithm = args[++i];
                    break;
                case "-mode":
                    mode = args[++i];
                    break;
                case "-key":
                    key = Integer.parseInt(args[++i]);
                    break;
                case "-data":
                    sequence = args[++i];
                    dataUsed = true;
                    break;
                case "-in":
                    pathIn = args[++i];
                    break;
                case "-out":
                    pathOut = args[++i];
                    break;
            }
        }

        if (!dataUsed) {
            try {
                sequence = readFileAsString(pathIn);
            } catch (IOException e) {
                System.out.println("Error");
            }
        }

        EncDecChooser chooser = new EncDecChooser();
        EncDecAlgo encdec = chooser.chooseAlgo(algorithm, mode, sequence, key);
        sequence = encdec.performOp();

        if (!(pathOut.equals(""))) {
            try (PrintWriter printWriter = new PrintWriter(pathOut)) {
                printWriter.print(sequence);
            } catch (IOException e) {
                System.out.println("Error");
            }
        } else {
            System.out.println(sequence);
        }
    }
}

interface Performer {
    String performOp();
}

abstract class EncDecAlgo implements Performer {
    public String mode;
    public String sequence;
    public int key;

    public EncDecAlgo(String mode, String sequence, int key) {
        this.mode = mode;
        this.sequence = sequence;
        this.key = key;
    }

    @Override
    public String performOp() {
        return (mode.equals("enc")) ? encrypt(sequence, key) : decrypt(sequence, key);
    }

    abstract String encrypt(String sequence, int key);
    abstract String decrypt(String sequence, int key);
}

class EncDecChooser {
    public EncDecAlgo chooseAlgo(String algorithm, String mode, String sequence, int key) {
        switch (algorithm) {
            case "shift":
                return new Shift(mode, sequence, key);
            case "unicode":
                return new Unicode(mode, sequence, key);
            default:
                return null;
        }
    }
}

class Unicode extends EncDecAlgo {

    public Unicode(String mode, String sequence, int key) {
        super(mode, sequence, key);
    }

    @Override
    public String encrypt(String sequence, int key) {
        int size = '~' - ' ' + 1;
        char[] result = sequence.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] += key;
            if (result[i] > '~') {
                result[i] += size;
            }
        }
        return String.valueOf(result);
    }

    @Override
    public String decrypt(String sequence, int key) {
        int size = '~' - ' ' + 1;
        char[] result = sequence.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] -= key;
            if (result[i] < ' ') {
                result[i] -= size;
            }
        }
        return String.valueOf(result);
    }
}

class Shift extends EncDecAlgo {

    public Shift(String mode, String sequence, int key) {
        super(mode, sequence, key);
    }

    @Override
    public String encrypt(String sequence, int key) {
        int size = 26;
        char[] result = sequence.toCharArray();
        for (int i = 0; i < result.length; i++) {
            if (!(Character.isWhitespace(result[i]))) {
                result[i] += key;
                if (result[i] > 'z') {
                    result[i] -= size;
                }
            }
        }
        return String.valueOf(result);
    }

    @Override
    public String decrypt(String sequence, int key) {
        int size = 26;
        char[] result = sequence.toCharArray();
        for (int i = 0; i < result.length; i++) {
            if (!(Character.isWhitespace(result[i]))) {
                result[i] -= key;
                if (result[i] < 'a') {
                    result[i] += size;
                }
            }
        }
        return String.valueOf(result);
    }
}
