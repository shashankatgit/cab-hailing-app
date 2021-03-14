package cab_hailing.wallet_service.db_init;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.wallet_service.model.Customer;
import cab_hailing.wallet_service.model.Wallet;
import cab_hailing.wallet_service.repository.CustomerRepository;
import cab_hailing.wallet_service.repository.WalletRepository;

@Component
public class DBInitializer {

	@Value("${db.init_file_dir}")
	private String csvFilesDirectory;

	@Autowired
	CustomerRepository custRepo;

	@Autowired
	WalletRepository walletRepo;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	// To be used for first run DB initialization as well as resetting microservice
	@Transactional
	public void initAllTables() {
		System.out.println("LOG : Trying to load all DB tables from files in folder : " + csvFilesDirectory);
		try {
			initCustomersTable();
			initWalletsTable();
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
		walletRepo.deleteAll();
		custRepo.deleteAll();
	}
	
	@Transactional
	private void initCustomersTable() throws IOException {
		String record[];

		// Reading customers file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "customers.txt", ',');
		
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
//			Customer customer = new Customer(Long.parseLong(record[0]), record[1]);
//			custRepo.save(customer);
			entityManager.createNativeQuery("INSERT INTO CUSTOMERS VALUES (?,?)")
		      .setParameter(1, Long.parseLong(record[0]))
		      .setParameter(2, record[1])
		      .executeUpdate();
		}
	}
	
	@Transactional
	private void initWalletsTable() throws IOException {
		String record[];

		// Reading wallets file and loading the table
		DSVFileReaderUtil dsvFileReaderUtil = new DSVFileReaderUtil(csvFilesDirectory + "wallets.txt", ',');
		while( (record = dsvFileReaderUtil.readNextRecord()) != null) {
//			Customer cust = custRepo.findById(Long.parseLong(record[2])).orElse(new Customer(Long.parseLong(record[2])));
//			Wallet wallet = new Wallet( Long.parseLong(record[0]), Long.parseLong(record[0]), cust);
//			
//			walletRepo.save(wallet);
			
			entityManager.createNativeQuery("INSERT INTO WALLETS VALUES (?,?,?)")
		      .setParameter(1, Long.parseLong(record[0]))
		      .setParameter(2, Long.parseLong(record[1]))
		      .setParameter(3, Long.parseLong(record[2]))
		      .executeUpdate();
		}
	}


}
