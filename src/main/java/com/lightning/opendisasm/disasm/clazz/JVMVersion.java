package com.lightning.opendisasm.disasm.clazz;

public interface JVMVersion {
	;
	
	public static boolean supportsVersion(int code) {
		return 45<=code&&code<=56;
	}
	public static String getVersionFromCode(int code) {
		switch(code) {
		case 45:
			return "Java 1.1";
		case 46:
			return "Java 1.2";
		case 47:
			return "Java 1.3";
		case 48:
			return "Java 1.4";
		case 49:
			return "Java 1.5";
		case 50:
			return "Java 1.6";
		case 51:
			return "Java 1.7";
		case 52:
			return "Java 1.8";
		case 53:
			return "Java 9";
		case 54:
			return "Java 10";
		case 55:
			return "Java 11";
		case 56:
			return "Java 12";
		default:
			return "Unknown Version Code: "+code;
		}
	}
	
	public static boolean isSuperAllowedToBeClear(int code) {
		return code==45;
	}
	
	public static boolean isJSRAndRetLegal(int code) {
		return code<51;
	}
	
	public static boolean isCONSTClassLoadable(int code) {
		return code>48;
	}
	
	public static boolean areSignaturePolymorphicMethodsSupported(int code) {
		return code>50;
	}
}
