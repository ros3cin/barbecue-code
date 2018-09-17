/***********************************************************************************************************************
 * Copyright (c) 2004, International Barcode Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * Neither the name of the International Barcode Consortium nor the names of any contributors may be used to endorse
 * or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***********************************************************************************************************************/

package net.sourceforge.barbecue.linear.twoOfFive;

import java.awt.Rectangle;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.GraphicsMock;
import net.sourceforge.barbecue.Module;

public class Std2of5BarcodeTest extends TestCase {
	public void testConstructingWithNoDataThrowsException() throws Exception {
		try {
			new Std2of5Barcode(null);
			fail("Missing expected Exception");
		} catch (BarcodeException e) {
			// Expected Exception
		}

		try {
			new Std2of5Barcode("");
			fail("Missing expected Exception");
		} catch (BarcodeException e) {
			// Expected Exception
		}
	}

	public void testInvalidCharacters() throws Exception {
		try {
			new Std2of5Barcode("1234567890A");
			fail("Non numeric data was allowed via constructor");
		} catch (BarcodeException e) {
			// Expected Exception
		}
	}

	public void testPreAmble() throws Exception {
		Std2of5Barcode barcode = new Std2of5Barcode("1234567890");
		assertEquals(new Module(new int[] { 3, 1, 3, 1, 1, 1 }), barcode.getPreAmble());
	}

	public void testPostAmble() throws Exception {
		Std2of5Barcode barcode = new Std2of5Barcode("1234567890");
		assertEquals(new Module(new int[] { 3, 1, 1, 1, 3, 1 }), barcode.getPostAmble());
	}

	public void testChecksumIsNull() throws Exception {
		Std2of5Barcode barcode = new Std2of5Barcode("1234567890");
		assertNull(barcode.calculateChecksum());
	}

	public void testEncodingIsCorrect() throws Exception {
		String data = "12345670";
		Std2of5Barcode barcode = new Std2of5Barcode(data);
		Module[] modules = barcode.encodeData();
		assertEquals(8, modules.length);
		int index = 0;
		for (char c : data.toCharArray()) {
			assertEquals(Std2of5ModuleFactory.getModule(String.valueOf(c)), modules[index]);
			index++;
		}
	}

	public void testDrawPaintsCorrectBars() throws Exception {
		Std2of5Barcode barcode = new Std2of5Barcode("12345670");
		barcode.setBarWidth(1);
		barcode.setDrawingText(false);
		GraphicsMock g = new GraphicsMock();
		barcode.draw(g, 0, 0);
		int[] expected = new int[] {
				3, 1, 3, 1, 1, 1, // start char
				3, 1, 1, 1, 1, 1, 1, 1, 3, 1, // 1
				1, 1, 3, 1, 1, 1, 1, 1, 3, 1, // 2
				3, 1, 3, 1, 1, 1, 1, 1, 1, 1, // 3
				1, 1, 1, 1, 3, 1, 1, 1, 3, 1, // 4
				3, 1, 1, 1, 3, 1, 1, 1, 1, 1, // 5
				1, 1, 3, 1, 3, 1, 1, 1, 1, 1, // 6
				1, 1, 1, 1, 1, 1, 3, 1, 3, 1, // 7
				1, 1, 1, 1, 3, 1, 3, 1, 1, 1, // 0
				3, 1, 1, 1, 3, 1 // stop char
		};
		List<Rectangle> rects = g.getRects();
		assertEquals(92, rects.size());
		for (int i = 0; i < rects.size(); i++) {
			Rectangle rectangle = (Rectangle) rects.get(i);
			assertEquals(expected[i], (int) rectangle.getWidth());
		}
	}
}
