package org.anoopam.ext.smart.exception;

@SuppressWarnings("serial")
public class NullDataException extends Exception{

	@Override
	public String toString() {
		return "com.smart.Exception:NullDataException :No meaning of passing null data to intent";
	}
}
