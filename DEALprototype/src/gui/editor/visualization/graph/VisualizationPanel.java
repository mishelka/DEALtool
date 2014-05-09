package gui.editor.visualization.graph;

import gui.editor.visualization.graph.gui.GuiControlPanel;
import gui.editor.visualization.graph.gui.GuiProgress;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;

/**
 * Vstupn‡ trieda vizualiza‹nŽho modulu. Predstavuje panel vizualiz‡cie
 * @author D‡vid
 *
 */
public class VisualizationPanel extends JPanel {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * graf komponent
	 */
	private mxGraphComponent graphComponent;
	/**
	 * vizualiz‡cia
	 */
	private GraphVisualization vizGraph;
	
	private Thread initViewThread;
	
	/**
	 * ‹as ätartu na‹’tania a konca na‹’tania modulu
	 */
	private long sTime, eTime;

	/**
	 * inicializovanù
	 */
	private boolean isInited = false;

	/**
	 * konätruktor
	 */
	public VisualizationPanel() {
		super();
		isInited = false;
	}

	/**
	 * inicializ‡cia komponentov
	 * @param isShowInfoTypeCheckboxSelected je zakliknut‡ moìnosé zobrazenia typov komponentov
	 */
	public void initializeView(final boolean isShowInfoTypeCheckboxSelected) {
		removeAll();
		sTime = System.currentTimeMillis();
		final GuiProgress guiProgress = new GuiProgress(this);
		guiProgress.show();

		initViewThread = new Thread(new Runnable() {

			@Override
			public void run() {
				setLayout(new BoxLayout(VisualizationPanel.this, BoxLayout.Y_AXIS));
				setAlignmentX(Component.LEFT_ALIGNMENT);
				setAlignmentY(Component.TOP_ALIGNMENT);
				vizGraph = new GraphVisualization();

				if (isShowInfoTypeCheckboxSelected)
					actionPerformedInfoTypeCheckBox(isShowInfoTypeCheckboxSelected);

				graphComponent = new mxGraphComponent(vizGraph.getGraph());
				graphComponent.setPreferredSize(new Dimension(1920, 1200));
				graphComponent.setDragEnabled(false);
				graphComponent.setGridVisible(false);
				graphComponent.setToolTips(true);
				GuiControlPanel controlPanel = new GuiControlPanel(VisualizationPanel.this, graphComponent);
				GraphMouseListener gml = new GraphMouseListener(graphComponent, VisualizationPanel.this, new Color(0x00D89840));
				graphComponent.getGraphControl().addMouseListener(gml);
				graphComponent.getGraphControl().addMouseMotionListener(gml);

				add(graphComponent);
				add(Box.createRigidArea(new Dimension(0, 5)));
				controlPanel.show();
				isInited = true;
				guiProgress.destroy();
				invalidate();
				updateUI();
				eTime = System.currentTimeMillis();
				System.out.println("TIME TO LOAD: " + (eTime-sTime));
			}
		});
		initViewThread.start();
	}
	
	public void update(boolean isShowInfoTypeCheckboxSelected) {
		if(initViewThread == null || !initViewThread.isAlive()) {
			initializeView(isShowInfoTypeCheckboxSelected);
		}
	}

	/**
	 * 
	 * @return objekt vizualiz‡cie
	 */
	public GraphVisualization getGraphVisualization() {
		return vizGraph;
	}

	/**
	 * nastav’ zobrazenie typu komponentov
	 * @param isChecked true ak ‡no, inak false
	 */
	public void actionPerformedInfoTypeCheckBox(boolean isChecked) {
		vizGraph.updateGraphVertexColor(isChecked);
	}

	/**
	 * 
	 * @return je inicializovanù
	 */
	public boolean isInited() {
		return isInited;
	}

	/**
	 * vytvor’ obr‡zok z vizualiza‹nŽho komponentu
	 * @param file cesta k sœboru
	 */
	public void makeGraphImage(File file) {
		Dimension size = graphComponent.getGraphControl().getPreferredSize();
		BufferedImage image = mxUtils.createBufferedImage(size.width, size.height, Color.WHITE);
		Graphics2D g2 = image.createGraphics();
		graphComponent.getGraphControl().paint(g2);
		try {
			ImageIO.write(image, "png", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
