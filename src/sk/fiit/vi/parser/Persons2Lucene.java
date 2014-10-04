package sk.fiit.vi.parser;

import org.apache.log4j.Logger;
import sk.fiit.vi.util.Configuration;
import sk.fiit.vi.util.GZIP;
import sk.fiit.vi.util.Lucene;

import java.util.ArrayList;

/**
 * Created by Seky on 1. 10. 2014.
 */
public class Persons2Lucene {
    private static final Logger LOGGER = Logger.getLogger(Persons2Lucene.class.getName());

    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        LOGGER.info("Deserializing start");
        ArrayList<Person> people;
        people = (ArrayList<Person>) GZIP.deserialize(StructurePeople.FILE_STRUCTURED_PEOPLE);
        LOGGER.info("Deserialized");
        Lucene.getInstance().addPeople(people);
        LOGGER.info("Added people");
        Lucene.getInstance().close();
    }
}
