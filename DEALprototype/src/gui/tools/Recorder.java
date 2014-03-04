package gui.tools;

import gui.analyzer.util.Logger;
import gui.editor.DealFileChooser;
import gui.model.application.events.UiEvent;
import gui.model.application.events.UiEventSequence;
import gui.tools.listener.UiEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Performs physical recording of commands performed on the UI into a text file
 * located on the local disk, or into a list of UiEventSequence objects.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Recorder {
	public static final String DEFAULT_RECORD_PATH = "record/";

	/** A BufferedReader instance - responsible for writing into the file. */
	private BufferedWriter writer;
	/**
	 * Flag representing the state of the recording process.
	 * @value true if the recording is running, false otherwise.
	 */
	private boolean recording = false;
	/** The output file. */
	private File file;
	/** The name of the output file. */
	private String fileName;
	/** A UiEvent sequence to be recorded. 
	 * Later, this sequence is going to be saved into a file. */
	private UiEventSequence uiEventSequence = new UiEventSequence();
	/** A list of UIEvent listeners listening to new event recording. */
	private List<UiEventListener> listeners = new ArrayList<UiEventListener>();

	/**
	 * Creates a new file with an automatically generated name. 
	 * Also creates a new uiEventSequence.
	 * @throws IOException in case of an error during the file creation
	 */
	public void createNewFile() throws IOException {
		createNewUiSequence();
		createNewFile((String) null);
	}

	/**
	 * Creates a new file with the user given name
	 * @param fileName the user given file name
	 * @throws IOException in case of an error during the file creation
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
	/**
	 * Creates a new UiEventSequence instance.
	 */
	public void createNewUiSequence() {
		this.uiEventSequence = new UiEventSequence();
	}

	/**
	 * Closes the BufferedWriter instance.
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
			notifyListeners(uiEvent);
			if (uiEvent != null) {
				uiEventSequence.add(uiEvent);
				record(uiEvent.toString());
			}
		}
	}

	/**
	 * Returns the value of the recording flag representing the state of the recording process.
	 * @return true if the recording is running, false otherwise.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * Sets the value of the recording flag representing the state of the recording process.
	 * @param recording the value of the recording flag
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
						.format(new Date()) + "." + DealFileChooser.DEAL_FILE_EXT;
	}
	
	/** Writes the recorded UI event sequence into the console. */
	public void writeResultToConsole() {
		Logger.logError(uiEventSequence.toString());
	}
	
	public void removeListener(UiEventListener listener) {
		listeners.remove(listener);
	}
	
	public void addListener(UiEventListener listener) {
		listeners.add(listener);
	}
	
	private void notifyListeners(UiEvent event) {
		for ( int i = 0; i < listeners.size(); i++ ){
			listeners.get(i).uiEventRecorded(event);
		}
	}
}
