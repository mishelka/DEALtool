package gui.editor.tree;

import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	public static final String IMAGE_PATH = "/gui/editor/resources/tree/";
	private final ImageIcon IMAGE_MODEL = getImage("model.png");
	private final ImageIcon IMAGE_MUTUALLY_NOT_EXCLUSIVE = getImage("mutually_not_exclusive.png");
	private final ImageIcon IMAGE_MUTUALLY_EXCLUSIVE = getImage("mutually_exclusive.png");
	// private final ImageIcon IMAGE_MANDATORY = getImage("mandatory.png");
	private final ImageIcon IMAGE_OPTIONAL = getImage("optional.png");
	private final ImageIcon IMAGE_BLANK = getImage("blank.png");

	private static final Color FG_COLOR_HIDDEN = Color.LIGHT_GRAY;
	private static final Color FG_COLOR_NORMAL = Color.BLACK;

	private static final Color BG_COLOR_INFORMATIVE_AND_TEXTUAL = Color.BLUE;
	private static final Color BG_COLOR_FUNCTIONAL = Color.RED;
	private static final Color BG_COLOR_GRAPHICALLY_GROUPING = Color.GREEN;
	private static final Color BG_COLOR_LOGICALLY_GROUPING = Color.CYAN;
	private static final Color BG_COLOR_CUSTOM = Color.YELLOW;
	private static final Color BG_COLOR_NORMAL = Color.WHITE;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (value instanceof TreeNode) {
			TreeNode node = (TreeNode) value;
			if (node.isHidden()) {
				setForeground(FG_COLOR_HIDDEN);
			} else {
				setForeground(FG_COLOR_NORMAL);
			}

			Object userObject = node.getUserObject();

			if (userObject instanceof Term) {
				Term term = (Term) userObject;
				Icon icon = IMAGE_OPTIONAL;

				if (node.isLeaf()) {
					icon = term.getIcon();
					if (icon != null)
						icon = resizeTo16(icon);
				} else {

					switch (term.getRelation()) {
					case MODEL:
						icon = IMAGE_MODEL;
						break;
					case MUTUALLY_NOT_EXCLUSIVE:
						icon = IMAGE_MUTUALLY_NOT_EXCLUSIVE;
						break;
					case AND:
						icon = IMAGE_OPTIONAL;
						break;
					case MUTUALLY_EXCLUSIVE:
						icon = IMAGE_MUTUALLY_EXCLUSIVE;
						break;
					}
				}

				// set the node icon
				if (icon == null)
					icon = IMAGE_BLANK;

				if (term.getComponent() instanceof Window) {
					if (term.getIcon() != null) {
						icon = resizeTo16(term.getIcon());
					}
				}

				setIcon(icon);
				setToolTipText(term.getDescription());

				// set node background
				DomainModel model = term.getDomainModel();
				Color bgColor = BG_COLOR_NORMAL;

				if (model != null && model.isShowComponentInfoTypes()) {
					ComponentInfoType infoType = term.getComponentInfoType();
					if (infoType != null) {
						switch (infoType) {
						case DESCRIPTIVE:
						case TEXTUAL:
							bgColor = BG_COLOR_INFORMATIVE_AND_TEXTUAL;
							break;
						case FUNCTIONAL:
							bgColor = BG_COLOR_FUNCTIONAL;
							break;
						case CONTAINERS:
							bgColor = BG_COLOR_GRAPHICALLY_GROUPING;
							break;
						case LOGICALLY_GROUPING:
							bgColor = BG_COLOR_LOGICALLY_GROUPING;
							break;
						case CUSTOM:
							bgColor = BG_COLOR_CUSTOM;
							break;
						case UNKNOWN:
							bgColor = BG_COLOR_NORMAL;
							break;
						default:
							bgColor = BG_COLOR_NORMAL;
							break;
						}
					}
				}

				setBackgroundNonSelectionColor(bgColor);

			} else if (node.isRoot()) {
				setIcon(IMAGE_MODEL);
			} else
				setToolTipText(null);
		}
		return this;
	}

	/**
	 * Loads an icon from the physical memory according to the icon name.
	 * 
	 * @param name
	 *            the name of the icon.
	 * @return the icon.
	 */
	private ImageIcon getImage(String name) {
		ImageIcon icon = new ImageIcon(getClass()
				.getResource(IMAGE_PATH + name));
		return icon;
	}

	/**
	 * Resizes icon to a size of 16x16px.
	 * 
	 * @param icon
	 *            the icon to resize.
	 * @return the resized icon.
	 */
	private Icon resizeTo16(Icon icon) {
		Image img = iconToImage(icon);
		int width = 16;
		if (icon.getIconWidth() > 16) {
			int height = (width * icon.getIconHeight()) / icon.getIconWidth();

			Image newImage = img.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);

			return new ImageIcon(newImage);
		} else {
			Image newImage = mergeImages(icon);
			return new ImageIcon(newImage);
		}
	}

	/**
	 * If the image icon is transparent, it is a problem, therefore we have to
	 * merge it with a blank white image.
	 * 
	 * @param icon
	 *            the icon to merge
	 * @return the merged icon
	 */
	private Image mergeImages(Icon icon) {
		BufferedImage image = getBufferedImage(IMAGE_BLANK);
		BufferedImage overlay = getBufferedImage(icon);

		// create the new image, canvas size is the max. of both image sizes
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage combined = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		try {
			icon.paintIcon(new JLabel(), g, 0, 0);
		} catch (Exception e) {
			//can not be done
		}

		return combined;
	}

	/**
	 * In the tree we need Image class, therefore this method encapsulates Icon
	 * object into an ImageIcon object.
	 * 
	 * @param icon
	 *            the Icon to encapsuled into Image.
	 * @return the Image created from the Icon object.
	 */
	private Image iconToImage(Icon icon) {
		if (icon == null)
			return null;
		if (icon instanceof ImageIcon) {
			return ((ImageIcon) icon).getImage();
		} else {
			return getBufferedImage(icon);
		}
	}

	/**
	 * If the icon is not an ImageIcon, it is possible to create a BufferedImage
	 * from the icon.
	 * 
	 * @param icon
	 *            The icon to transform into a BufferedImage.
	 * @return the BufferedImage created from the Icon object.
	 */
	private BufferedImage getBufferedImage(Icon icon) {
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h);
		Graphics2D g = image.createGraphics();
		try {
		icon.paintIcon(new JLabel(), g, 0, 0);
		} catch(Exception ex) {
			//do nothing, can not be done
		}
		g.dispose();
		return image;
	}
}
