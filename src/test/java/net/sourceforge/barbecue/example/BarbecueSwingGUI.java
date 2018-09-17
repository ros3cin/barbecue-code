package net.sourceforge.barbecue.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.BarcodeType;

/**
 * The GUI for Barbecue.
 */
public class BarbecueSwingGUI extends JFrame {
	private static final long serialVersionUID = -827068178819379368L;

	/** The barcodeType combo. */
	private JComboBox<String> typeCombo;

	/** The content field. */
	private JTextField contentField;
	
	/** The height field. */
	private JTextField heightField;
	
	private JCheckBox drawTextBox;
	
	private JButton startButton;
	
	private JLabel imageLabel;

	/**
	 * The main method.
	 *
	 * @param arguments the arguments
	 */
	public static void main(String[] arguments) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						new BarbecueSwingGUI().setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Instantiates a new BarbecueGUI.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public BarbecueSwingGUI() {
		super();

		setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		// Parameter Panel
		JPanel parameterPanel = new JPanel();
		parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.LINE_AXIS));

		// Mandatory parameter Panel
		JPanel mandatoryParameterPanel = new JPanel();
		mandatoryParameterPanel.setLayout(new BoxLayout(mandatoryParameterPanel, BoxLayout.PAGE_AXIS));

		JPanel typePanel = new JPanel();
		typePanel.setLayout(new FlowLayout());
		JLabel typeLabel = new JLabel("Barcode type");
		typePanel.add(typeLabel);
		typeCombo = new JComboBox<String>();
		for (BarcodeType barcodeType : BarcodeType.values()) {
			typeCombo.addItem(barcodeType.toString());
		}
		typePanel.add(typeCombo, BorderLayout.EAST);
		mandatoryParameterPanel.add(typePanel);
		
		JPanel heightPanel = new JPanel();
		heightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel heightLabel = new JLabel("Barcode height");
		heightPanel.add(heightLabel);
		heightField = new JTextField();
		heightField.setPreferredSize(new Dimension(200, heightField.getPreferredSize().height));
		heightField.setText("30");
		heightPanel.add(heightField);
		mandatoryParameterPanel.add(heightPanel);
		
		drawTextBox = new JCheckBox("Draw text");
		drawTextBox.setSelected(true);
		mandatoryParameterPanel.add(drawTextBox);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel contentLabel = new JLabel("Barcode content");
		contentPanel.add(contentLabel);
		contentField = new JTextField();
		contentField.setText("1234567890");
		contentField.setPreferredSize(new Dimension(200, contentField.getPreferredSize().height));
		contentField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				checkButtonStatus();
			}

			public void removeUpdate(DocumentEvent e) {
				checkButtonStatus();
			}

			public void insertUpdate(DocumentEvent e) {
				checkButtonStatus();
			}
		});
		contentPanel.add(contentField);
		mandatoryParameterPanel.add(contentPanel);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

		// Start Button
		startButton = new JButton("Create image");
		startButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					createImage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		buttonPanel.add(startButton);

		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// Close Button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		buttonPanel.add(closeButton);

		parameterPanel.add(mandatoryParameterPanel);
		add(parameterPanel);
		add(buttonPanel);

		add(Box.createRigidArea(new Dimension(0, 5)));

		pack();

		setLocationRelativeTo(null);
		//setResizable(false);
		
		checkButtonStatus();
	}

	protected void checkButtonStatus() {
		startButton.setEnabled(contentField.getText().length() > 0);
	}

	private void createImage() {
		if (imageLabel != null) {
			remove(imageLabel);
		}
		
		try {
			Barcode barcode;
			BarcodeType type = BarcodeType.getFromString((String) typeCombo.getSelectedItem());
			switch (type) {
				case Code128:
					barcode = BarcodeFactory.createCode128(contentField.getText());
					break;
				case Code128A:
					barcode = BarcodeFactory.createCode128A(contentField.getText());
					break;
				case Code128B:
					barcode = BarcodeFactory.createCode128B(contentField.getText());
					break;
				case Code128C:
					barcode = BarcodeFactory.createCode128C(contentField.getText());
					break;
				case EAN128:
					barcode = BarcodeFactory.createEAN128(contentField.getText());
					break;
				case EAN13:
					barcode = BarcodeFactory.createEAN13(contentField.getText());
					break;
				case PDF417:
					barcode = BarcodeFactory.createPDF417(contentField.getText());
					break;
				default:
					throw new Exception("Invalid barcode type");
			}
			
			// Text is drawn by default
			barcode.setDrawingText(drawTextBox.isSelected());
			
			// Default barHeight is 30
			barcode.setPreferredBarHeight(Integer.parseInt(heightField.getText()));
			
			BufferedImage image = BarcodeImageHandler.getImage(barcode);
			if (image != null) {
				imageLabel = new JLabel(new ImageIcon(image));
				add(imageLabel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pack();
		setLocationRelativeTo(null);
	}
}
