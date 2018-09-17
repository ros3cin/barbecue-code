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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import net.sourceforge.barbecue.example.BarcodeServlet;
import net.sourceforge.barbecue.linear.code128.Code128Barcode;
import net.sourceforge.barbecue.linear.ean.UCCEAN128Barcode;

public class BarcodeServletTest extends BarcodeTestCase {

	private BarcodeServletMock servlet;
	private HttpServletResponseMock res;
	private HttpServletRequestMock req;
	private Map<String, String[]> params;

	protected void setUp() throws Exception {
		super.setUp();
		servlet = new BarcodeServletMock();
		req = new HttpServletRequestMock();
		params = new HashMap<String, String[]>();
		params.put("data", new String[] { "1234567890" });
		params.put("type", new String[] { "SSCC18" });
		params.put("resolution", new String[] { "100" });
		req.setParameters(params);
		res = new HttpServletResponseMock();
	}

	public void testValueIsRequired() throws Exception {
		params.remove("data");
		try {
			servlet.doGet(req, res);
			fail();
		} catch (ServletException e) {
			// Good
		}
	}

	public void testGetReturnsBarcodeImage() throws Exception {
		servlet.doGet(req, res);
		assertEquals("image/png", res.getContentType());
		assertTrue(res.hasOutput());
	}

	public void testPostReturnsBarcodeImage() throws Exception {
		servlet.doPost(req, res);
		assertEquals("image/png", res.getContentType());
		assertTrue(res.hasOutput());
	}

	public void testServletNameReturnsBarbecue() throws Exception {
		assertEquals("barbecue", servlet.getServletName());
	}

	public void testSettingResolutionChangesDefaultResolution() throws Exception {
		params.put("height", new String[] { "200" });
		params.put("width", new String[] { "3" });
		params.put("resolution", new String[] { "72" });
		req.setParameters(params);
		servlet.doGet(req, res);
		Barcode barcode = servlet.getBarcode();
		assertEquals(72, barcode.getResolution());
	}

	public void testGettingBarcodeWithNoTypeCreatesCode128Optimal() throws Exception {
		req.setParameters(params);
		servlet.doGet(req, res);
		assertTrue(servlet.getBarcode() instanceof Code128Barcode);
	}

	public void testSettingTypeToUCC128WithNoAppIdThrowsException() throws Exception {
		params.put("type", new String[] { "UCC128" });
		req.setParameters(params);
		try {
			servlet.doGet(req, res);
			fail();
		} catch (ServletException e) {
			// OK
		}
	}

	/**
	 * Verifies fix for bug: 732629
	 */
	public void testSettingWidthChangesDefault() throws Exception {
		params.put("data", new String[] { "12345" });
		params.put("width", new String[] { "3" });
		req.setParameters(params);
		servlet.doGet(req, res);
		assertEquals("image/png", res.getContentType());
		assertTrue(res.hasOutput());
		Barcode barcode = servlet.getBarcode();
		assertEquals(330, barcode.getWidth());
	}

	/**
	 * Verifies fix for bug: 736794
	 */
	public void testGettingUCC128BarcodeReturnsCorrectType() throws Exception {
		params.put("type", new String[] { "UCC128" });
		params.put("appid", new String[] { UCCEAN128Barcode.GTIN_AI });
		req.setParameters(params);
		servlet.doGet(req, res);
		assertEquals("image/png", res.getContentType());
		assertTrue(res.hasOutput());
	}

	/**
	 * Verifies fix for bug: 737122
	 */
	public void testCanGetAllBarcodeTypes() throws Exception {
		assertCanGenerateBarcodeInServlet("EAN128");
		assertCanGenerateBarcodeInServlet("ean128");
		assertCanGenerateBarcodeInServlet("Code128");
		assertCanGenerateBarcodeInServlet("Code128A");
		assertCanGenerateBarcodeInServlet("Code128B");
		assertCanGenerateBarcodeInServlet("Code128C");
		assertCanGenerateBarcodeInServlet("code128c");
		assertCanGenerateBarcodeInServlet("USPS");
		assertCanGenerateBarcodeInServlet("usps");
		assertCanGenerateBarcodeInServlet("ShipmentIdentificationNumber");
		assertCanGenerateBarcodeInServlet("shipmentIdentificationnumber");
		assertCanGenerateBarcodeInServlet("SCC14ShippingCode");
		assertCanGenerateBarcodeInServlet("SCC14SHIPPINGcode");
		assertCanGenerateBarcodeInServlet("GlobalTradeItemNumber");
		assertCanGenerateBarcodeInServlet("PDF417");
		assertCanGenerateBarcodeInServlet("Code39");
		assertCanGenerateBarcodeInServlet("Std2of5");
		assertCanGenerateBarcodeInServlet("Int2of5");
		assertCanGenerateBarcodeInServlet("3of9");
		assertCanGenerateBarcodeInServlet("USD3");
		assertCanGenerateBarcodeInServlet("USD4");
		assertCanGenerateBarcodeInServlet("NW7");
		assertCanGenerateBarcodeInServlet("Monarch");
		assertCanGenerateBarcodeInServlet("2of7");
		assertCanGenerateBarcodeInServlet("Codabar");
	}

	public void testCheckSumParameterIsUsedForCode39Barcodes() throws Exception {
		params.put("type", new String[] { "Code39" });
		params.put("checksum", new String[] { "true" });
		params.put("data", new String[] { "1234567890" });
		req.setParameters(params);
		servlet.doGet(req, res);
		Barcode barcode = servlet.getBarcode();
		assertEquals(336, barcode.getWidth());
	}

	public void testServletForcesHeadlessModeByDefault() throws Exception {
		params.put("type", new String[] { "Code128" });
		req.setParameters(params);
		servlet.doGet(req, res);
		assertTrue(EnvironmentFactory.getEnvironment() instanceof HeadlessEnvironment);
	}

	public void testDrawTextParameterIsIgnoredIfHeadless() throws Exception {
		params.put("type", new String[] { "Code128" });
		params.put("drawText", new String[] { "true" });
		req.setParameters(params);
		servlet.doGet(req, res);
		Barcode barcode = servlet.getBarcode();
		assertFalse(barcode.isDrawingText());
	}

	public void testDrawTextParameterIsNotIgnoredIfNotHeadless() throws Exception {
		params.put("type", new String[] { "Code128" });
		params.put("headless", new String[] { "false" });
		params.put("drawText", new String[] { "true" });
		req.setParameters(params);
		servlet.doGet(req, res);
		Barcode barcode = servlet.getBarcode();
		assertTrue(barcode.isDrawingText());
	}

	private void assertCanGenerateBarcodeInServlet(String type) throws ServletException {
		params.put("type", new String[] { type });
		req.setParameters(params);
		servlet.doGet(req, res);
		assertEquals("image/png", res.getContentType());
		assertTrue(res.hasOutput());
	}

	class BarcodeServletMock extends BarcodeServlet {
		private static final long serialVersionUID = -8849329406386229255L;

		Barcode barcode;

		protected Barcode getBarcode(String type, String data, String appId, boolean checkSum) throws ServletException {
			barcode = super.getBarcode(type, data, appId, checkSum);
			return barcode;
		}

		public Barcode getBarcode() {
			return barcode;
		}
	}

	class HttpServletResponseMock implements HttpServletResponse {
		private String contentType;
		private ServletOutputStreamMock outputStream;

		public HttpServletResponseMock() {
			outputStream = new ServletOutputStreamMock();
		}

		public void addCookie(Cookie cookie) {
		}

		public void addDateHeader(String s, long l) {
		}

		public void addHeader(String s, String s1) {
		}

		public void addIntHeader(String s, int i) {
		}

		public boolean containsHeader(String s) {
			return false;
		}

		public String encodeRedirectURL(String s) {
			return null;
		}

		public String encodeRedirectUrl(String s) {
			return null;
		}

		public String encodeURL(String s) {
			return null;
		}

		public String encodeUrl(String s) {
			return null;
		}

		public void sendError(int i) throws IOException {
		}

		public void sendError(int i, String s) throws IOException {
		}

		public void sendRedirect(String s) throws IOException {
		}

		public void setDateHeader(String s, long l) {
		}

		public void setHeader(String s, String s1) {
		}

		public void setIntHeader(String s, int i) {
		}

		public void setStatus(int i) {
		}

		public void setStatus(int i, String s) {
		}

		public void flushBuffer() throws IOException {
		}

		public int getBufferSize() {
			return 0;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public Locale getLocale() {
			return null;
		}

		public ServletOutputStream getOutputStream() throws IOException {
			return outputStream;
		}

		public PrintWriter getWriter() throws IOException {
			return null;
		}

		public boolean isCommitted() {
			return false;
		}

		public void reset() {
		}

		public void resetBuffer() {
		}

		public void setBufferSize(int i) {
		}

		public void setContentLength(int i) {
		}

		public void setContentType(String s) {
			contentType = s;
		}

		public void setLocale(Locale locale) {
		}

		public boolean hasOutput() {
			return outputStream.hasOutput;
		}

		public String getContentType() {
			return contentType;
		}

		@Override
		public void setCharacterEncoding(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getHeader(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getStatus() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	class ServletOutputStreamMock extends ServletOutputStream {
		boolean hasOutput = false;

		public void write(int b) throws IOException {
			hasOutput = true;
		}
	}

	class HttpServletRequestMock implements HttpServletRequest {
		private Map<String, String[]> params;

		public String getAuthType() {
			return null;
		}

		public String getContextPath() {
			return null;
		}

		public Cookie[] getCookies() {
			return new Cookie[0];
		}

		public long getDateHeader(String s) {
			return 0;
		}

		public String getHeader(String s) {
			return null;
		}

		public Enumeration<String> getHeaderNames() {
			return null;
		}

		public Enumeration<String> getHeaders(String s) {
			return null;
		}

		public int getIntHeader(String s) {
			return 0;
		}

		public String getMethod() {
			return null;
		}

		public String getPathInfo() {
			return null;
		}

		public String getPathTranslated() {
			return null;
		}

		public String getQueryString() {
			return null;
		}

		public String getRemoteUser() {
			return null;
		}

		public String getRequestURI() {
			return null;
		}

		public StringBuffer getRequestURL() {
			return null;
		}

		public String getRequestedSessionId() {
			return null;
		}

		public String getServletPath() {
			return null;
		}

		public HttpSession getSession() {
			return null;
		}

		public HttpSession getSession(boolean b) {
			return null;
		}

		public Principal getUserPrincipal() {
			return null;
		}

		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			return false;
		}

		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}

		public boolean isRequestedSessionIdValid() {
			return false;
		}

		public boolean isUserInRole(String s) {
			return false;
		}

		public Object getAttribute(String s) {
			return null;
		}

		public Enumeration<String> getAttributeNames() {
			return null;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public int getContentLength() {
			return 0;
		}

		public String getContentType() {
			return null;
		}

		public ServletInputStream getInputStream() throws IOException {
			return null;
		}

		public Locale getLocale() {
			return null;
		}

		public Enumeration<Locale> getLocales() {
			return null;
		}

		public String getParameter(String s) {
			if (params.get(s) == null) {
				return null;
			} else {
				return params.get(s)[0];
			}
		}

		public Enumeration<String> getParameterNames() {
			return null;
		}

		public String[] getParameterValues(String s) {
			return new String[0];
		}

		public String getProtocol() {
			return null;
		}

		public BufferedReader getReader() throws IOException {
			return null;
		}

		public String getRealPath(String s) {
			return null;
		}

		public String getRemoteAddr() {
			return null;
		}

		public String getRemoteHost() {
			return null;
		}

		public RequestDispatcher getRequestDispatcher(String s) {
			return null;
		}

		public String getScheme() {
			return null;
		}

		public String getServerName() {
			return null;
		}

		public int getServerPort() {
			return 0;
		}

		public boolean isSecure() {
			return false;
		}

		public void removeAttribute(String s) {
		}

		public void setAttribute(String s, Object o) {
		}

		public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
		}

		public void setParameters(Map<String, String[]> params) {
			this.params = params;
		}

		@Override
		public AsyncContext getAsyncContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getLocalPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isAsyncStarted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Part getPart(String arg0) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void login(String arg0, String arg1) throws ServletException {
			// TODO Auto-generated method stub

		}

		@Override
		public void logout() throws ServletException {
			// TODO Auto-generated method stub

		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return params;
		}
	}
}
