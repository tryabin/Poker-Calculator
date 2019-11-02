import java.io.*;
import java.util.Map;

import static java.util.Map.entry;

public class ConvertTableData {

    enum DataType {
        CHAR,
        SHORT,
        INT,
        UNSIGNED_INT
    }

    public static void main(String[] args) throws IOException {

        File folder = new File("src/resources");

        Map<String, DataType> dataFiles = Map.ofEntries(
                entry("binaries_by_id_data.txt", DataType.SHORT),
                entry("dp_data.txt", DataType.INT),
                entry("flush_data.txt", DataType.SHORT),
                entry("noflush7_data.txt", DataType.SHORT),
                entry("suitbit_by_id_data.txt", DataType.SHORT),
                entry("suits_data.txt", DataType.CHAR)
        );

        for (Map.Entry<String, DataType> entry : dataFiles.entrySet()) {

            System.out.println("Processing " + entry.getKey());

            File dataFile = new File(folder.getAbsolutePath() + "/" + entry.getKey());

            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            StringBuilder builder = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                builder.append(currentLine);
                currentLine = reader.readLine();
            }
            reader.close();

            String stringData = builder.toString().trim();
            String[] dataParts = stringData.split("\\s+");

            System.out.println("dataParts.length = " + dataParts.length);

            String newDataFileName = dataFile.getName().split("\\.")[0] + ".dat";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/resources/" + newDataFileName));

            if (entry.getValue() == DataType.CHAR) {
                byte[] data = new byte[dataParts.length]; // chars are 2 bytes in Java, but 1 byte in CUDA, so we load the data as bytes.
                for (int i = 0; i < dataParts.length; i++) {
                    data[i] = (byte) Integer.parseInt(dataParts[i].trim());
                }
                out.writeObject(data);
            }
            if (entry.getValue() == DataType.SHORT) {
                short[] data = new short[dataParts.length];
                for (int i = 0; i < dataParts.length; i++) {
                    if (dataParts[i].contains("x")) {
                        data[i] = Short.parseShort(dataParts[i].split("x")[1].trim(), 16);
                    }
                    else {
                        data[i] = Short.parseShort(dataParts[i].trim());
                    }
                }
                out.writeObject(data);
            }
            if (entry.getValue() == DataType.INT) {
                int[] data = new int[dataParts.length];
                for (int i = 0; i < dataParts.length; i++) {
                    data[i] = Integer.parseInt(dataParts[i].trim());
                }
                out.writeObject(data);
            }
            if (entry.getValue() == DataType.UNSIGNED_INT) {
                byte[] data = new byte[dataParts.length*Integer.BYTES];
                for (int i = 0; i < dataParts.length; i++) {
                    long curLong = Long.parseLong(dataParts[i].trim());
                    byte[] curBytes = longToBytes(curLong);
                    for (int j = Long.BYTES-1; j >= 4; j--) {
                        data[i*Integer.BYTES + Long.BYTES-1-j] = curBytes[j];
                    }
                }
                out.writeObject(data);
            }

            out.flush();
            out.close();
        }
    }


    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }
}
