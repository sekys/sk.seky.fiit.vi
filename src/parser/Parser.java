package parser;

import util.GZIP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class Parser {
    private final static Pattern REGEX_DEAD_PEOPLE = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/people\\.deceased_person>.*");
    private final static Pattern REGEX_OBJECTS = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/type\\.object\\.name>.*");
    private final static Pattern REGEX_DATE_OF_BIRTH = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/people\\.person\\.date_of_birth>.*");

    private final static int FLUSH_LIMIT = 10000;
    private final static double TOTAL_LINES = 2746142741L;

    private List<String> m_deads;
    private List<String> m_objects;
    private List<String> m_births;

    public Parser() {
        m_deads = new ArrayList<>(FLUSH_LIMIT);
        m_objects = new ArrayList<>(FLUSH_LIMIT);
        m_births = new ArrayList<>(FLUSH_LIMIT);
    }

    public void parse() throws Exception {
        BufferedWriter writerForDeaths = GZIP.write("deaths.gz");
        BufferedWriter writerForObjects = GZIP.write("objects.gz");
        BufferedWriter writerForBirths = GZIP.write("births.gz");
        BufferedReader in = GZIP.read("D:/dump.gz");

        String line;
        int processed = 0;
        double percent;
        while ((line = in.readLine()) != null) {
            processed++;

            if (REGEX_DEAD_PEOPLE.matcher(line).matches()) {
                if (m_deads.size() == FLUSH_LIMIT - 5) {
                    write(writerForDeaths, m_deads);
                    m_deads = new ArrayList<>(FLUSH_LIMIT);
                }
                m_deads.add(line);
            } else if (REGEX_OBJECTS.matcher(line).matches()) {
                if (m_objects.size() == FLUSH_LIMIT - 5) {
                    write(writerForObjects, m_objects);
                    m_objects = new ArrayList<>(FLUSH_LIMIT);
                }
                m_objects.add(line);
            } else if (REGEX_DATE_OF_BIRTH.matcher(line).matches()) {
                if (m_births.size() == FLUSH_LIMIT - 5) {
                    write(writerForBirths, m_births);
                    m_births = new ArrayList<>(FLUSH_LIMIT);
                }
                m_births.add(line);
            }

            // Show status
            if (processed % 1000000 == 0) {
                percent = (double) 100 * (((double) processed) / TOTAL_LINES);
                System.out.println(Double.toString(percent));
            }
        }

        // Flush rest data
        write(writerForDeaths, m_deads);
        write(writerForObjects, m_objects);
        write(writerForBirths, m_births);

        // Close streams
        writerForDeaths.close();
        writerForObjects.close();
        writerForBirths.close();
        in.close();
    }

    private void write(BufferedWriter writer, List<String> data) throws IOException {
        for (String record : data) {
            writer.write(record);
            writer.newLine();
        }
        System.out.println("flushing");
        writer.flush();
    }

    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.parse();
    }
}
