package gui.ui.tree;

import gui.model.domain.Term;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	public static final String IMAGE_PATH = "/gui/ui/resources/";
	private final ImageIcon IMAGE_MODEL = getImage("model.png");
	private final ImageIcon IMAGE_MUTUALLY_NOT_EXCLUSIVE = getImage("mutually_not_exclusive.png");
	private final ImageIcon IMAGE_MUTUALLY_EXCLUSIVE = getImage("mutually_exclusive.png");
	// private final ImageIcon IMAGE_MANDATORY = getImage("mandatory.png");
	private final ImageIcon IMAGE_OPTIONAL = getImage("optional.png");
	private final ImageIcon IMAGE_BLANK = getImage("blank.png");

	private static final Color COLOR_HIDDEN = Color.LIGHT_GRAY;
	private static final Color COLOR_NORMAL = Color.BLACK;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (value instanceof TreeNode) {
			TreeNode node = (TreeNode) value;
			if (node.isHidden()) {
				setForeground(COLOR_HIDDEN);
			} else {
				setForeground(COLOR_NORMAL);
			}

			Object userObject = node.getUserObject();

			if (userObject instanceof Term) {
				Term f = (Term) userObject;
				Icon icon = IMAGE_OPTIONAL;

				if (node.isLeaf()) {
					// ak chcem vykreslit ikonu, ktora je na danom funkcnom
					// prvku
					// je mozne implementovat vlastny jlabel pre tento komponent
					// - alebo vlastny komponent alias kontajner, ktory sa
					// vykresli
					// icon = f.getIcon();
					// if (icon != null)
					// icon = resizeTo16(icon);
				} else {

					switch (f.getRelation()) {
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

				if (icon == null)
					icon = IMAGE_BLANK;

				if (f.getComponent() instanceof Window) {
					if (f.getIcon() != null)
						icon = f.getIcon();
				}

				setIcon(icon);

				setToolTipText(f.getDescription());
			} else
				setToolTipText(null);
		}
		return this;
	}

	private ImageIcon getImage(String name) {
		ImageIcon icon = new ImageIcon(getClass()
				.getResource(IMAGE_PATH + name));
		return icon;
	}

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

		icon.paintIcon(new JLabel(), g, 0, 0);

		return combined;
	}

	private Image iconToImage(Icon icon) {
		if (icon == null)
			return null;
		if (icon instanceof ImageIcon) {
			return ((ImageIcon) icon).getImage();
		} else {
			return getBufferedImage(icon);
		}
	}

	private BufferedImage getBufferedImage(Icon icon) {
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h);
		Graphics2D g = image.createGraphics();
		icon.paintIcon(new JLabel(), g, 0, 0);
		g.dispose();
		return image;
	}
}
