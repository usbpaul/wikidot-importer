package nl.w4w.work;

import nl.w4w.work.exception.InvalidArgumentsException;
import nl.w4w.work.service.ImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ImporterApplication implements ApplicationRunner {

	@Autowired
	private ImporterService importerService;

	private static final Logger logger = LoggerFactory.getLogger(ImporterApplication.class);

	public static void main(String... args) {
		SpringApplication app = new SpringApplication(ImporterApplication.class);
//		app.setAddCommandLineProperties(true);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(ImporterApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args.getOptionNames().size() <4) {
			throw new InvalidArgumentsException("Not enough arguments to run this application");
		}
		if (!args.containsOption("wikidotName") || !args.containsOption("apiKey")
			|| !args.containsOption("sourcesDir") ||!args.containsOption("filesDir")) {
			throw new InvalidArgumentsException("Wrong arguments passed to this application");
		}
		if (!new File(args.getOptionValues("sourcesDir").get(0)).isDirectory()) {
			throw new InvalidArgumentsException("Provided sources directory is not a directory");
		}
		if (!new File(args.getOptionValues("filesDir").get(0)).isDirectory()) {
			throw new InvalidArgumentsException("Provided files directory is not a directory");
		}
		importerService.importData();
	}
}
