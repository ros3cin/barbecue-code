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
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test helper class to track the drawing actions that lead to a graphic representation of a barcode
 *
 */
public class GraphicsMock extends Graphics2D {
	private List<Color> colors;
	private List<Rectangle> rects;
	private int maxX;
	private int maxY;
	private int minY;
	private int minX;
	private boolean maxSet;
	private boolean minSet;
	private Rectangle textBounds;
	private Color currentColor;
	private List<String> strings;
	private boolean textDrawn;
	private Font font;

	public GraphicsMock() {
		init();
	}

	public void reset() {
		init();
	}

	private void init() {
		colors = new ArrayList<Color>();
		rects = new ArrayList<Rectangle>();
		strings = new ArrayList<String>();
		currentColor = Color.black;
		minSet = false;
		maxSet = false;
		textDrawn = false;
		maxX = -1;
		maxY = -1;
		minY = -1;
		minX = -1;
		textBounds = null;
		font = null;

		// Font.getFont("Arial") doesn't find the Arial font. So look for it in another way.
		for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			if ("Arial".equalsIgnoreCase(font.getFontName())) {
				setFont(font);
				break;
			}
		}
	}

	public Rectangle getTextBounds() {
		return textBounds;
	}

	public Rectangle getModifiedBounds() {
		return new Rectangle(minX, minY, maxX, maxY);
	}

	public List<Color> getColors() {
		return colors;
	}

	public List<Rectangle> getRects() {
		return rects;
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return false;
	}

	public void draw(Shape s) {
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		updateMin(x, y);
		updateMax(x, y);
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
	}

	public void drawString(String str, int x, int y) {
		updateMin(x, y);
		updateMax(x, y);
		textBounds = new Rectangle(x, y, x, y);
		strings.add(str);
		textDrawn = true;
	}

	public void drawString(String s, float x, float y) {
		updateMin(x, y);
		updateMax((int) x, (int) y);
		textBounds = new Rectangle((int) x, (int) y, (int) x, (int) y);
		strings.add(s);
		textDrawn = true;
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		updateMin(x, y);
		updateMax(x, y);
		textBounds = new Rectangle(x, y, x, y);
		textDrawn = true;
	}

	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		updateMin(x, y);
		updateMax((int) x, (int) y);
		textBounds = new Rectangle((int) x, (int) y, (int) x, (int) y);
		textDrawn = true;
	}

	public void drawGlyphVector(GlyphVector g, float x, float y) {
		updateMin(x, y);
		updateMax(x, y);
		textBounds = new Rectangle((int) x, (int) y, (int) x, (int) y);
		textDrawn = true;
	}

	public void fill(Shape s) {
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return false;
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}

	public void setComposite(Composite comp) {
	}

	public void setPaint(Paint paint) {
	}

	public void setStroke(Stroke s) {
	}

	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
	}

	public Object getRenderingHint(RenderingHints.Key hintKey) {
		return null;
	}

	public void setRenderingHints(Map<?, ?> hints) {
	}

	public void addRenderingHints(Map<?, ?> hints) {
	}

	public RenderingHints getRenderingHints() {
		return null;
	}

	public void translate(int x, int y) {
	}

	public void translate(double tx, double ty) {
	}

	public void rotate(double theta) {
	}

	public void rotate(double theta, double x, double y) {
	}

	public void scale(double sx, double sy) {
	}

	public void shear(double shx, double shy) {
	}

	public void transform(AffineTransform Tx) {
	}

	public void setTransform(AffineTransform Tx) {
	}

	public AffineTransform getTransform() {
		return null;
	}

	public Paint getPaint() {
		return null;
	}

	public Composite getComposite() {
		return null;
	}

	public void setBackground(Color color) {
	}

	public Color getBackground() {
		return null;
	}

	public Stroke getStroke() {
		return null;
	}

	public void clip(Shape s) {
	}

	public FontRenderContext getFontRenderContext() {
		return new FontRenderContext(new AffineTransform(0, 0, 0, 0, 0, 0), false, false);
	}

	public Graphics create() {
		return this;
	}

	public Color getColor() {
		return currentColor;
	}

	public void setColor(Color c) {
		currentColor = c;
		colors.add(c);
	}

	public void setPaintMode() {
	}

	public void setXORMode(Color c1) {
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public FontMetrics getFontMetrics(Font f) {
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		return graphics2D.getFontMetrics(f);
	}

	public Rectangle getClipBounds() {
		return null;
	}

	public void clipRect(int x, int y, int width, int height) {
	}

	public void setClip(int x, int y, int width, int height) {
	}

	public Shape getClip() {
		return null;
	}

	public void setClip(Shape clip) {
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		updateMin(x1, y1);
		updateMax(x2, y2);
	}

	public void fillRect(int x, int y, int width, int height) {
		rects.add(new Rectangle(x, y, width, height));
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void clearRect(int x, int y, int width, int height) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void drawOval(int x, int y, int width, int height) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void fillOval(int x, int y, int width, int height) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		updateMin(x, y);
		updateMax(x + width, y + height);
	}

	public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {
		for (int i = 0; i < nPoints; i++) {
			updateMin(xPoints[i], yPoints[i]);
			updateMax(xPoints[i], yPoints[i]);
		}
	}

	public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
		for (int i = 0; i < nPoints; i++) {
			updateMin(xPoints[i], yPoints[i]);
			updateMax(xPoints[i], yPoints[i]);
		}
	}

	public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
		for (int i = 0; i < nPoints; i++) {
			updateMin(xPoints[i], yPoints[i]);
			updateMax(xPoints[i], yPoints[i]);
		}
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		updateMin(x, y);
		return false;
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		updateMin(x, y);
		updateMax(x + width, y + height);
		return false;
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		updateMin(x, y);
		return false;
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		updateMin(x, y);
		updateMax(x + width, y + height);
		return false;
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return false;
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		return false;
	}

	public void dispose() {
	}

	private void updateMin(float x, float y) {
		updateMin((int) x, (int) y);
	}

	private void updateMin(int x, int y) {
		if (!minSet) {
			minX = x;
			minY = y;
			minSet = true;
		} else {
			if (x < minX) {
				minX = x;
			}
			if (y < minY) {
				minY = y;
			}
		}
	}

	private void updateMax(float x, float y) {
		updateMax((int) x, (int) y);
	}

	private void updateMax(int x, int y) {
		if (!maxSet) {
			maxX = x;
			maxY = y;
			maxSet = true;
		} else {
			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
		}
	}

	public List<String> getStrings() {
		return strings;
	}

	public boolean wasTextDrawn() {
		return textDrawn;
	}
}
