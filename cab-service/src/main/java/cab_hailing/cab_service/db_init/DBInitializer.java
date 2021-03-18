package cab_hailing.cab_service.db_init;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.cab_service.repository.CabRepository;
import cab_hailing.cab_service.repository.CabStatusRepository;

@Component
public class DBInitializer {

	@Value("${db.init_file_dir}")
	private String csvFilesDirectory;

	@Autowired
	CabRepository cabRepository;
	
	@Autowired
	CabStatusRepository cabStatusRepository;
	
	
	@PersistenceContext
    private EntityManager entityManager;
	
	// To be used for first run DB initialization as well as resetting microservice
	@Transactional
	public void initAllTables() {
		System.out.println("LOG : Trying to load all DB tables from files in folder : " + csvFilesDirectory);
		try {
			initCabTable();
			initCabStatusTable();
			System.out.println("LOG : All table initialization complete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// To be invoked by reset end-point to clear and reload the state of tables
	@Transactional
	public void resetAndLoadAllTables() {
		clearAllTables();
		initAllTables();
	}
	
	// To be used privately for resetting the micro-service
	private void clearAllTables() {
		// Respect the foreign key constraints and order while truncating
		cabStatusRepository.deleteAll();
		cabRepository.deleteAll();
	}
	
	@Transactional
	private void initCabTable() throws IOException {
		String record[];

		// Reading customers file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "cab.txt", ',');
		
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
			entityManager.createNativeQuery("INSERT INTO CABS(cab_id, password) VALUES (?,?)")
			  .setParameter(1, Long.parseLong(record[0]))
			  .setParameter(2, record[1])
		      .executeUpdate();
		}
	}
	
	@Transactional
	private void initCabStatusTable() throws IOException {
		String record[];

		// Reading customers file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "cabstatus.txt", ',');
		
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
			entityManager.createNativeQuery("INSERT INTO CAB_STATUS(CAB_ID,MAJOR_STATE ,MINOR_STATE,"
					+ "CURR_RIDE_ID,N_REQUESTS_RECVD,N_RIDES_GIVEN) "
					+ "VALUES (?,?,?,?,?,?)")
			  .setParameter(1, Long.parseLong(record[0]))
			  .setParameter(2, record[1])
			  .setParameter(3, record[2])
		      .setParameter(4, Long.parseLong(record[3]))
		      .setParameter(5, Long.valueOf(0))
		      .setParameter(6, Long.valueOf(0))
		      .executeUpdate();
		}
	}

}
