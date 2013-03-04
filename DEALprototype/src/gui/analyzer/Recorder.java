package gui.analyzer;

import gui.analyzer.util.Logger;
import gui.model.application.events.UiEvent;
import gui.model.application.events.UiEventSequence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Trieda Recorder m· za ˙lohu fyzickÈ nahr·vanie prÌkazov do textovÈho s˙boru
 * na lok·lnom disku.
 * 
 * @author Michaela Kreutzova <michaela.kreutzova@gmail.com>
 */
public class Recorder {

	public static final String DEAL_FILE_EXT = ".deal";
	public static final String DEFAULT_RECORD_PATH = "record/";

	/** Inötancia triedy BufferedWriter zodpovedn· za z·pis do s˙boru. */
	private BufferedWriter writer;
	/**
	 * PrÌznak priebehu nahr·vania.
	 * 
	 * @value true ak nahr·vanie prebieha, false inak.
	 */
	private boolean recording = false;
	/** V˝stupn˝ s˙bor. */
	private File file;
	/** N·zov v˝stupnÈho s˙boru. */
	private String fileName;
	/** A UiEvent sequence to be recorded. Later, this sequence is going to be saved to a file. */
	private UiEventSequence uiEventSequence = new UiEventSequence();

	/**
	 * VytvorÌ nov˝ s˙bor, ktor˝ m· automaticky vygenerovan˝ n·zov.
	 * Creates a new file with an automatically generated name. Also creates a new uiEventSequence.
	 * @throws IOException
	 *             v prÌpade chyby pri vytv·ranÌ s˙boru.
	 */
	public void createNewFile() throws IOException {
		createNewUiSequence();
		createNewFile((String) null);
	}

	/**
	 * VytvorÌ nov˝ s˙bor s pomocou pouûÌvateæom zadanÈho n·zvu.
	 * 
	 * @param fileName
	 *            pouûÌvateæom zadan˝ n·zov s˙boru.
	 * @throws IOException
	 *             v prÌpade chyby pri vytv·ranÌ s˙boru.
	 */
	public void createNewFile(String fileName) throws IOException {
		createNewUiSequence();
		if (fileName == null || fileName.equals("")) {
			fileName = createNewRecordFileName();
		}
		this.fileName = fileName;
		file = new File(this.fileName);
		if (!file.exists())
			file.createNewFile();
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//TODO: maybe in the future add a name for the uiSequence - this will be used to save the sequence.
	public void createNewUiSequence() {
		this.uiEventSequence = new UiEventSequence();
	}

	/**
	 * ZatvorÌ inötanciu triedy BufferedWriter.
	 */
	public void closeWriter() {
		try {
			if (writer != null) {
				writer.close();
				this.fileName = null;
				file = null;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		recording = false;
	}

	/**
	 * ZapÌöe jeden prÌkaz do textovÈho s˙boru. Ak rekordÈr nenahr·va (prÌznak
	 * priebehu nahr·vania recording je nastaven˝ na false), ak je zadan˝ prÌkaz
	 * null alebo je to pr·zdny reùazec, nevykon· sa niË.
	 * 
	 * @param command
	 *            prÌkaz, ktor˝ sa m· zapÌsaù.
	 */
	public void record(String command) {
		if (recording) {
			if (command != null && !command.equals("")) {
				if (this.file.canWrite()) {
					try {
						writer.append(command + "\n");
						writer.flush();
						Logger.log("Command recorded: " + command);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					Logger.logError(">>Cannot write into file: "
							+ file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Records a uiEvent into a uiSequence
	 * 
	 * @param uiEvent
	 */
	public void record(UiEvent uiEvent) {
		if (recording) {
			if (uiEvent != null) {
				uiEventSequence.add(uiEvent);
				Logger.log("UiEvent recorded: " + uiEvent);
			}
		}
	}

	/**
	 * @return PrÌznak priebehu nahr·vania. True ak nahr·vanie prebieha, false
	 *         inak.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * @param recording
	 *            prÌznak priebehu nahr·vania.
	 */
	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	/**
	 * @return N·zov s˙boru, do ktorÈho m· rekordÈr zapisovaù.
	 */
	public String getFileName() {
		return fileName;
	}

	private String createNewRecordFileName() {
		return DEFAULT_RECORD_PATH
				+ "record_"
				+ new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss")
						.format(new Date()) + DEAL_FILE_EXT;
	}
	
	public void writeResultToConsole() {
		Logger.logError(uiEventSequence.toString());
	}
}
