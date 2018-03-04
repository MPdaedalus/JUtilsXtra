package util;

import java.util.Arrays;

public class S {
	
	public static void exit() {
		System.exit(0);
	}
	
	public static void p(String text) {
		System.out.print(text);
	}
	
	public static void ep(String text) {
		System.err.print(text);
	}
	
	public static void p(int number) {
		System.out.print(number);
	}
	
	public static void p(long number) {
		System.out.print(number);
	}
	
	public static void p(boolean isTrue) {
		if(isTrue) {
			System.out.print("true");
		} else {
			System.out.print("false");
		}
	}
	
	public static void p(long[] numbers) {
		System.out.print(Arrays.toString(numbers));
	}
	public static void p(int[] numbers) {
		System.out.print(Arrays.toString(numbers));
	}
	public static void p(char[] numbers) {
		System.out.print(Arrays.toString(numbers));
	}
	public static void p(byte[] numbers) {
		System.out.print(Arrays.toString(numbers));
	}
	
	public static void pl(String[] text) {
		System.out.println(Arrays.toString(text));
	}
	
	public static void pl(String text) {
		System.out.println(text);
	}
	
	public static void epl(String text) {
		System.err.println(text);
	}
	
	public static void pl(int number) {
		System.out.println(number);
	}
	
	public static void pl(long number) {
		System.out.println(number);
	}
	
	public static void pl() {
		System.out.println();
	}
	
	public static void pl(boolean isTrue) {
		if(isTrue) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}
	}
	
	public static void pl(long[] numbers) {
		System.out.println(Arrays.toString(numbers));
	}
	public static void pl(int[] numbers) {
		System.out.println(Arrays.toString(numbers));
	}
	public static void pl(char[] numbers) {
		System.out.println(Arrays.toString(numbers));
	}
	public static void pl(byte[] numbers) {
		System.out.println(Arrays.toString(numbers));
	}
	
	public static void nt() {
		return;
	}

}
