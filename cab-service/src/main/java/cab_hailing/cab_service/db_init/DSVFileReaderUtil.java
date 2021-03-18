package cab_hailing.cab_service.db_init;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class DSVFileReaderUtil {
	
    CSVReader csvReader;
	
	public DSVFileReaderUtil(String filePath, char delimeter) throws FileNotFoundException {	
		CSVParser parser = new CSVParserBuilder().withSeparator(delimeter).build();
		FileReader fileReader = new FileReader(filePath);
		this.csvReader = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();
	}
	
	public String[] readNextRecord() throws IOException {
		return csvReader.readNext();
	}

}
