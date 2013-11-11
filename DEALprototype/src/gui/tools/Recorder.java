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
 * Trieda Recorder má za úlohu fyzické nahrávanie príkazov do textového súboru
 * na lokálnom disku.
 * 
 * @author Michaela Kreutzova <michaela.kreutzova@gmail.com>
 */
public class Recorder {

	public static final String DEAL_FILE_EXT = ".deal";
	public static final String DEFAULT_RECORD_PATH = "record/";

	/** Inštancia triedy BufferedWriter zodpovedná za zápis do súboru. */
	private BufferedWriter writer;
	/**
	 * Príznak priebehu nahrávania.
	 * 
	 * @value true ak nahrávanie prebieha, false inak.
	 */
	private boolean recording = false;
	/** Výstupný súbor. */
	private File file;
	/** Názov výstupného súboru. */
	private String fileName;
	/** A UiEvent sequence to be recorded. Later, this sequence is going to be saved to a file. */
	private UiEventSequence uiEventSequence = new UiEventSequence();

	/**
	 * Vytvorí nový súbor, ktorý má automaticky vygenerovaný názov.
	 * Creates a new file with an automatically generated name. Also creates a new uiEventSequence.
	 * @throws IOException
	 *             v prípade chyby pri vytváraní súboru.
	 */
	public void createNewFile() throws IOException {
		createNewUiSequence();
		createNewFile((String) null);
	}

	/**
	 * Vytvorí nový súbor s pomocou používate¾om zadaného názvu.
	 * 
	 * @param fileName
	 *            používate¾om zadaný názov súboru.
	 * @throws IOException
	 *             v prípade chyby pri vytváraní súboru.
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
	 * Zatvorí inštanciu triedy BufferedWriter.
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
	 * @return Príznak priebehu nahrávania. True ak nahrávanie prebieha, false
	 *         inak.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * @param recording
	 *            príznak priebehu nahrávania.
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
