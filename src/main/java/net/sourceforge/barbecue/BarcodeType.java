package net.sourceforge.barbecue;

public enum BarcodeType {
	Code128,
	Code128A,
	Code128B,
	Code128C,
	EAN13,
	EAN128,
	PDF417;

	public static BarcodeType getFromString(String name) throws Exception {
		for (BarcodeType barcodeType : BarcodeType.values()) {
			if (barcodeType.toString().equalsIgnoreCase(name)) {
				return barcodeType;
			}
		}
		throw new Exception("Invalid name for BarcodeType: " + name);
	}
}
