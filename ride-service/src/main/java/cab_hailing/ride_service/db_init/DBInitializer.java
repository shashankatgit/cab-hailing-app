package cab_hailing.ride_service.db_init;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.ride_service.repository.RideRepository;
import cab_hailing.ride_service.repository.CabStatusRepository;

@Component
public class DBInitializer {

	
	//-----------------------------------------------------------
	@Value("${db.init_file_dir}")
	private String csvFilesDirectory;	
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	RideRepository rideRepository;
	
	@Autowired
	CabStatusRepository cabStatusRepository;
	
	
	//-----------------------------------------------------------
	// To be used for first run DB initialization as well as resetting microservice
	@Transactional
	public void initAllTables() {
		System.out.println("LOG : Trying to load all DB tables from files in folder : " + csvFilesDirectory);
		try {
			initRideTable();
			initCabStatusTable();
			System.out.println("LOG : All table initialization complete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// To be used privately for resetting the micro-service
	@Transactional
	private void clearAllTables() {
		// Respect the foreign key constraints and order while truncating
		rideRepository.deleteAll();
		cabStatusRepository.deleteAll();		
	}
		
	// To be invoked by reset end-point to clear and reload the state of tables
	@Transactional
	public void resetAndLoadAllTables(){
		clearAllTables();
		initAllTables();
	}	
	
	@Transactional
	private void initRideTable() throws IOException {
		String record[];

		// Reading customers file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "ride.txt", ',');
		
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
			entityManager.createNativeQuery("INSERT INTO RIDES(ride_id, cust_id, cab_id, ride_state) "
					+ "VALUES (?,?,?,?)")
			  .setParameter(1, Long.parseLong(record[0]))
			  .setParameter(2, Long.parseLong(record[1]))
			  .setParameter(3, Long.parseLong(record[2]))
			  .setParameter(4, record[3])
		      .executeUpdate();
		}
	}
	
	@Transactional
	private void initCabStatusTable() throws IOException {
		String record[];

		// Reading customers file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "cabstatus.txt", ',');
		
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
			entityManager.createNativeQuery("INSERT INTO CAB_STATUS(CAB_ID,MAJOR_STATE ,MINOR_STATE, CURR_POS) VALUES (?,?,?,?)")
			  .setParameter(1, Long.parseLong(record[0]))
			  .setParameter(2, record[1])
			  .setParameter(3, record[2])
		      .setParameter(4, Long.parseLong(record[3]))
		      .executeUpdate();
		}
	}

}
