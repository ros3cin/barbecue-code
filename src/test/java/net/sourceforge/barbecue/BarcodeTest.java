/***********************************************************************************************************************
Copyright (c) 2003, International Barcode Consortium
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of
      conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.
    * Neither the name of the International Barcode Consortium nor the names of any contributors may be used to endorse
      or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
***********************************************************************************************************************/

package net.sourceforge.barbecue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import junit.framework.TestCase;
import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import net.sourceforge.barbecue.output.Output;

public class BarcodeTest extends TestCase {

	public void testConstructingWithNoDataThrowsException() throws Exception {
		try {
			new BarcodeMock(null);
			fail();
		} catch (BarcodeException e) {
			// Good
		}

		try {
			new BarcodeMock("");
			fail();
		} catch (BarcodeException e) {
			// Good
		}
	}

	public void testBoundsAreNotZero() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		Rectangle bounds = barcode.getBounds();
		assertTrue(bounds.getWidth() > 0);
		assertTrue(bounds.getHeight() > 0);
	}

	public void testBarcodeWidthCannotBeSetBelowOne() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		assertEquals(2, (int) barcode.getBarWidth());
		barcode.setBarWidth(0);
		assertEquals(1, (int) barcode.getBarWidth());
	}

	public void testWidthAndHeightAreNotZero() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		assertTrue(barcode.getWidth() > 0);
		assertTrue(barcode.getHeight() > 0);
	}

	public void testPaintingDoesNotAffectBounds() throws Exception {
		Barcode barcode = new BarcodeMock("12345", false);
		Rectangle bounds1 = barcode.getBounds();
		barcode.paintComponent(new BufferedImage(500, 500, BufferedImage.TYPE_BYTE_GRAY).getGraphics());
		Rectangle bounds2 = barcode.getBounds();
		assertEquals(bounds1, bounds2);
	}

    public void testDrawingDoesNotAffectBounds() throws Exception {
		Barcode barcode = new BarcodeMock("12345", false);
		Rectangle bounds1 = barcode.getBounds();
		barcode.draw((Graphics2D) new BufferedImage(500, 500, BufferedImage.TYPE_BYTE_GRAY).getGraphics(), 0, 0);
		Rectangle bounds2 = barcode.getBounds();
		assertEquals(bounds1, bounds2);
	}

    // Test is not working because BarcodeMock doesn't draw anything on the graphics
//	public void testBarcodeIsDrawnAtOriginForZeroZeroDraw() throws Exception {
//		BarcodeMock barcode = new BarcodeMock("12345");
//		barcode.setDrawingText(false);
//		GraphicsMock g = new GraphicsMock();
//		barcode.paintComponent(g);
//		assertEquals(0, (int) g.getModifiedBounds().getX());
//		assertEquals(0, (int) g.getModifiedBounds().getY());
//	}

	public void testSettingColorsChangesPaintedColors() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		barcode.setForeground(Color.blue);
		barcode.setBackground(Color.cyan);
		barcode.setDrawingText(false);
		GraphicsMock g = new GraphicsMock();
		barcode.paintComponent(g);
		for (Color color : g.getColors()) {
			assertTrue(isSameColor(color, Color.blue) || isSameColor(color, Color.cyan) || isSameColor(color, g.getColor()));
		}
	}

	public void testDrawingLeavesColorUnchanged() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		barcode.setForeground(Color.red);
		barcode.setBackground(Color.blue);
		barcode.setDrawingText(false);
		GraphicsMock g = new GraphicsMock();
		g.setColor(Color.cyan);
		barcode.draw(g, 0, 0);
		assertTrue(isSameColor(Color.cyan, g.getColor()));
	}

	public void testPaintingLeavesColorUnchanged() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		barcode.setForeground(Color.red);
		barcode.setBackground(Color.blue);
		barcode.setDrawingText(false);
		GraphicsMock g = new GraphicsMock();
		g.setColor(Color.cyan);
		barcode.paintComponent(g);
		assertTrue(isSameColor(Color.cyan, g.getColor()));
	}

	public void testResolutionCannotBeSetNegativeOrZero() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		int expected = barcode.getResolution();
		barcode.setResolution(-1);
		assertEquals(expected, barcode.getResolution());
		barcode.setResolution(0);
		assertEquals(expected, barcode.getResolution());
	}

	public void testResolutionIsDefaultInHeadlessMode() throws Exception {
		EnvironmentFactory.setHeadlessMode();
		Barcode barcode = new BarcodeMock("1234");
		assertEquals(HeadlessEnvironment.DEFAULT_RESOLUTION, barcode.getResolution());
	}

	public void testSettingResolutionOverridesDefaultResolution() throws Exception {
		int resolution = 42;
		BarcodeMock barcode = new BarcodeMock("12345");
		barcode.setResolution(resolution);
		assertEquals(resolution, barcode.getResolution());
	}

	public void testSettingFontChangesDrawnFont() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		
		// Font.getFont("Arial") dowsn't find the Arial font. So look for it in another way.
		Font arialFont = null;
		for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			if ("Arial".equalsIgnoreCase(font.getFontName())) {
				arialFont = font;
				break;
			}
		}
		
		barcode.setFont(arialFont);
		assertEquals(arialFont, barcode.getFont());
	}

	public void testGetDataReturnsData() throws Exception {
		String data = "12345";
		BarcodeMock barcode = new BarcodeMock(data);
		assertEquals(data, barcode.getData());
	}

	public void testSettingHeightThatIsToSmallLeavesHeightUnchanged() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		int height = barcode.getHeight();
		barcode.setPreferredBarHeight(0);
		assertEquals(height, barcode.getHeight());
	}

	public void testAllSizesAreActualSize() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		assertEquals(barcode.getSize(), barcode.getPreferredSize());
		assertEquals(barcode.getSize(), barcode.getMinimumSize());
		assertEquals(barcode.getSize(), barcode.getMaximumSize());
	}

	public void testDrawingNullModuleReturnsSizeOfZero() throws Exception {
		BarcodeMock barcode = new BarcodeMock("12345");
		assertEquals(0, (int) barcode.drawModule(null, null, 0, 0, 0, 0));
	}

	public void testLabelIsData() throws Exception {
		String data = "12345";
		BarcodeMock barcode = new BarcodeMock(data);
		assertEquals(data, barcode.getLabel());
	}

	private boolean isSameColor(Color color, Color expected) {
		return color.getRGB() == expected.getRGB();
	}

	public class BarcodeMock extends Barcode {
		private static final long serialVersionUID = -2174347100120269219L;

		public BarcodeMock(String data) throws BarcodeException {
			this(data, true);
		}

		public BarcodeMock(String data, boolean drawText) throws BarcodeException {
			super(data);
			setDrawingText(drawText);
		}

		protected double getBarcodeWidth(int resolution) {
			return 0;
		}

		protected int calculateMinimumBarHeight(int resolution) {
			return 50;
		}

		protected Module[] encodeData() {
			return new Module[] {new Module( new int[] {2, 1, 1, 2, 4} )};
		}

		protected Module calculateChecksum() {
			return new BlankModule(0);
		}

		protected Module getPreAmble() {
			return new BlankModule(0);
		}

		protected Module getPostAmble() {
			return new BlankModule(0);
		}

		protected Dimension draw(Output output, int x, int y, int barWidth, int barHeight) {
			return new Dimension(50, 50);
		}
	}
}
