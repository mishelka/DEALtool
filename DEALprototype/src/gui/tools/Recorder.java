package gui.tools;

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
 * Trieda Recorder m� za �lohu fyzick� nahr�vanie pr�kazov do textov�ho s�boru
 * na lok�lnom disku.
 * 
 * @author Michaela Kreutzova <michaela.kreutzova@gmail.com>
 */
public class Recorder {

	public static final String DEAL_FILE_EXT = ".deal";
	public static final String DEFAULT_RECORD_PATH = "record/";

	/** In�tancia triedy BufferedWriter zodpovedn� za z�pis do s�boru. */
	private BufferedWriter writer;
	/**
	 * Pr�znak priebehu nahr�vania.
	 * 
	 * @value true ak nahr�vanie prebieha, false inak.
	 */
	private boolean recording = false;
	/** V�stupn� s�bor. */
	private File file;
	/** N�zov v�stupn�ho s�boru. */
	private String fileName;
	/** A UiEvent sequence to be recorded. Later, this sequence is going to be saved to a file. */
	private UiEventSequence uiEventSequence = new UiEventSequence();

	/**
	 * Vytvor� nov� s�bor, ktor� m� automaticky vygenerovan� n�zov.
	 * Creates a new file with an automatically generated name. Also creates a new uiEventSequence.
	 * @throws IOException
	 *             v pr�pade chyby pri vytv�ran� s�boru.
	 */
	public void createNewFile() throws IOException {
		createNewUiSequence();
		createNewFile((String) null);
	}

	/**
	 * Vytvor� nov� s�bor s pomocou pou��vate�om zadan�ho n�zvu.
	 * 
	 * @param fileName
	 *            pou��vate�om zadan� n�zov s�boru.
	 * @throws IOException
	 *             v pr�pade chyby pri vytv�ran� s�boru.
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
	 * Zatvor� in�tanciu triedy BufferedWriter.
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
	 * Writes one command into a text file. If the recorder is not recording
	 * (the recording flag is set to false) or if the command is null or empty string,
	 * nothing will happen.
	 * @param command
	 *            command, which should be recorded.
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
				record(uiEvent.toString());
			}
		}
	}

	/**
	 * @return Pr�znak priebehu nahr�vania. True ak nahr�vanie prebieha, false
	 *         inak.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * @param recording
	 *            pr�znak priebehu nahr�vania.
	 */
	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	/**
	 * @return The name of the file to record to.
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
