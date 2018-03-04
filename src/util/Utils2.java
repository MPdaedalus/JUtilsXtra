package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



import gnu.trove3.list.array.TByteArrayList;
import gnu.trove3.list.array.TIntArrayList;
import gnu.trove3.map.custom_hash.TObjectByteCustomHashMap;
import gnu.trove3.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove3.map.custom_hash.TObjectLongCustomHashMap;
import gnu.trove3.map.hash.TCustomHashMap;
import gnu.trove3.set.hash.TByteHashSet;
import gnu.trove3.set.hash.TCustomHashSet;
import gnu.trove3.set.hash.TIntHashSet;
import gnu.trove3.set.hash.TLongHashSet;
import gnu.trove3.set.hash.TShortHashSet;
import gnu.trove3.strategy.HashingStrategy;
import sun.misc.Unsafe;
public final class Utils2 {

	public static final Unsafe unsafe =getUnsafe();
	private static final int byteAryOffset = unsafe.arrayBaseOffset(byte[].class);
	//hash stuff
	private static final long PRIME64_1 = -7046029288634856825L; //11400714785074694791
	private static final long PRIME64_2 = -4417276706812531889L; //14029467366897019727
	private static final long PRIME64_3 = 1609587929392839161L;
	private static final long PRIME64_4 = -8796714831421723037L; //9650029242287828579
	private static final long PRIME64_5 = 2870177450012600261L;

	private static final long seed=-1640531527053942041L;
	private static final Random rand = new Random();


	public static Unsafe getUnsafe() {
		try { 
			final Field fld = Unsafe.class.getDeclaredField("theUnsafe");
			fld.setAccessible(true);
			return (Unsafe) fld.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//	public static int binarySearch(long[] a, int fromIndex, int toIndex,long key) {
	//		int low = fromIndex;
	//		int high = toIndex - 1;
	//
	//		while (low <= high) {
	//			int mid = (low + high) >>> 1;
	//			long midVal = a[mid];
	//
	//			if (midVal < key)
	//				low = mid + 1;
	//			else if (midVal > key)
	//				high = mid - 1;
	//			else
	//				return mid; // key found
	//		}
	//		return -(low);  // key not found.
	//	}

	public static String toStringRaw(String[] words) {
		StringBuilder output=new StringBuilder(words.length*6);
		for(int i=0; i<words.length;i++) {
			output.append(words[i]);
			if(i+1 != words.length) output.append(' ');
		}
		return output.toString();
	}

	public static int containsIndexOf(String[] words, String word) {
		for(int i=0; i<words.length;i++) if(words[i].contains(word)) return i;
		return 0;
	}
	public static int binarySearch(long[] a, int fromIndex, int toIndex,long key) {
		toIndex--;
		int mid;
		long midVal;
		while (fromIndex <= toIndex) {
			mid = (fromIndex + toIndex) >>> 1;
		midVal = a[mid];
		if (midVal < key) {
			fromIndex = mid + 1;
		} else if (midVal > key) {
			toIndex = mid - 1;
		} else return mid; 
		}
		return -(fromIndex);  
	}

	public static int indexOfIgnoreCase(final String haystack, final String needle) {
		if (needle.isEmpty() || haystack.isEmpty()) {
			// Fallback to legacy behavior.
			return haystack.indexOf(needle);
		}
		int j,ii;
		final int hayLen=haystack.length(),nedLen=needle.length();
		for (int i = 0; i < hayLen; ++i) {
			// Early out, if possible.
			if (i + nedLen > hayLen) {
				return -1;
			}

			// Attempt to match substring starting at position i of haystack.
			j = 0;
			ii = i;
			while (ii < hayLen && j < nedLen) {
				if (Character.toLowerCase(haystack.charAt(ii)) != Character.toLowerCase(needle.charAt(j))) {
					break;
				}
				j++;
				ii++;
			}
			// Walked all the way to the end of the needle, return the start
			// position that this was found.
			if (j == nedLen) {
				return i;
			}
		}

		return -1;
	}

	public static List<TIntArrayList> permute(int[] nums) {
		List<TIntArrayList> results = new ArrayList<TIntArrayList>();
		if (nums == null || nums.length == 0) {
			return results;
		}
		TIntArrayList result = new TIntArrayList();
		dfs(nums, results, result);
		return results;
	}

	private static void dfs(int[] nums, List<TIntArrayList> results, TIntArrayList result) {
		if (nums.length == result.size()) {
			TIntArrayList temp = new TIntArrayList(result);
			results.add(temp);
		}        
		for (int i=0; i<nums.length; i++) {
			if (!result.contains(nums[i])) {
				result.add(nums[i]);
				dfs(nums, results, result);
				result.remove(result.size() - 1);
			}
		}
	}

	public static int indexOf(long[] array, long valueToFind, int startIndex) {
		if (array == null) {
			return -1;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		for (int i = startIndex; i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return -1;
	}


	public static  byte[] toByteArrayIgnoreLastZeros(long[] obj) {//ignore any zeros and anything after it in last 10% of array
		if(obj == null) return null;
		int valueIndex;
		if((valueIndex=indexOf(obj, 0, obj.length-(int) (obj.length+Math.ceil(((double)obj.length)/10)))) == -1) valueIndex = obj.length-1;
		byte[] ary = new byte[(valueIndex+1)*8];
		int curIdx=0;
		long value;
		for(int i=0,len=valueIndex+1;i<len;i++) {
			value = obj[i];
			ary[curIdx] =  (byte)(value >>> 56);
			ary[curIdx+1] =  (byte)(value >>> 48);
			ary[curIdx+2] =  (byte)(value >>> 40);
			ary[curIdx+3] =  (byte)(value >>> 32);
			ary[curIdx+4] =  (byte)(value >>> 24);
			ary[curIdx+5] =  (byte)(value >>> 16);
			ary[curIdx+6] =  (byte)(value >>> 8);
			ary[curIdx+7] =  (byte)value;
			curIdx +=8;
		}
		return ary;
	}

	public static byte[] toByteArray(long[] obj) {
		if(obj == null) return null;
		byte[] ary = new byte[obj.length*8];
		int curIdx=0;
		for(long value : obj) {
			ary[curIdx] =  (byte)(value >>> 56);
			ary[curIdx+1] =  (byte)(value >>> 48);
			ary[curIdx+2] =  (byte)(value >>> 40);
			ary[curIdx+3] =  (byte)(value >>> 32);
			ary[curIdx+4] =  (byte)(value >>> 24);
			ary[curIdx+5] =  (byte)(value >>> 16);
			ary[curIdx+6] =  (byte)(value >>> 8);
			ary[curIdx+7] =  (byte)value;
			curIdx +=8;
		}
		return ary;
	}


	public static long[] toLongArray(byte[] ary) {
		if(ary == null) return null;
		long[] out = new long[ary.length/8];
		int curIdx=0;
		for(int i=0; i<out.length; i++) {
			out[i] = ((ary[curIdx+7] & 0xFFL) << 0) +
					((ary[curIdx+6] & 0xFFL) << 8) +
					((ary[curIdx+5] & 0xFFL) << 16) +
					((ary[curIdx+4] & 0xFFL) << 24) +
					((ary[curIdx+3] & 0xFFL) << 32) +
					((ary[curIdx+2] & 0xFFL) << 40) +
					((ary[curIdx+1] & 0xFFL) << 48) +
					(((long) ary[curIdx]) << 56);
			curIdx +=8;
		}
		return out;
	}

	public static byte[] toByteArray(int[] value) {
		if(value == null) return null;
		byte[] b = new byte[value.length*4];
		int counter = 0;
		for(int v : value){
			b[counter] = (byte)(v >>> 24);
			b[counter+1] = (byte)(v >>> 16);
			b[counter+2] = (byte)(v >>> 8);
			b[counter+3] = (byte)v;
			counter+=4;
		}
		return b;
	}

	public static int[] toIntArray(byte[] ba) {
		if(ba == null) return null;
		int[] out = new int[ba.length/4];
		int curIdx=0;
		for(int i=0; i<out.length; i++) {
			out[i] =  (ba[curIdx] << 24)  + ((ba[curIdx+1] & 0xFF) << 16) + ((ba[curIdx+2] & 0xFF) << 8) + (ba[curIdx+3] & 0xFF);
			curIdx +=4;
		}
		return out;
	}




	public static boolean isPrime ( int num )
	{
		boolean prime = true;
		int limit = (int) Math.sqrt ( num );  

		for ( int i = 2; i <= limit; i++ )
		{
			if ( num % i == 0 )
			{
				prime = false;
				break;
			}
		}

		return prime;
	}


	public static double geoDistance(double lat1, double lat2, double lon1,double lon2) {
		final int R = 6371; // Radius of the earth
		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; 
		distance = Math.pow(distance, 2);
		return Math.sqrt(distance);//distance in km
	}

	public static String deleteAll(String source, String startTag, String endTag, int end) {
		int tagStartIdx,tagEndIdx;
		while((tagStartIdx= source.indexOf(startTag)) != -1){
			tagEndIdx = source.indexOf(endTag,tagStartIdx);
			if(tagEndIdx == -1) break;
			source = source.substring(0, tagStartIdx).concat(source.substring(tagEndIdx+endTag.length(), source.length()));
		}
		return source;
	}

	public static String deleteAll(String source, char startTag, char endTag, int end) {
		int tagStartIdx,tagEndIdx;
		while((tagStartIdx= source.indexOf(startTag)) != -1){
			tagEndIdx = source.indexOf(endTag,tagStartIdx);
			if(tagEndIdx == -1) break;
			source = source.substring(0, tagStartIdx).concat(source.substring(tagEndIdx+1, source.length()));
		}
		return source;
	}

	public static int lowestPosValue(int v1, int v2) {
		if(v1 > -1 && v2 > -1) return Math.min(v1, v2);
		if(v1 == -1) return v2;
		return v1;
	}

	public static String deleteAll(String source, String startTag, String minorEndTag1, String majorEndTag2) {
		int tagStartIdx,tagEndIdx, tagEndIdx1,tagEndIdx2,count=0;
		String endTag = null, newSource;
		while((tagStartIdx= indexOfIgnoreCase(source,startTag)) != -1){
			if(count++ == 1000) return source;
			tagEndIdx2 = source.indexOf(majorEndTag2,tagStartIdx+1);
			if(tagEndIdx2 != -1) {
				tagEndIdx1 = source.lastIndexOf(minorEndTag1,tagEndIdx2);
			} else {
				tagEndIdx1 = source.indexOf(minorEndTag1,tagStartIdx+3);
			}
			tagEndIdx=-1;
			if(tagEndIdx1 == -1 && tagEndIdx2 != -1) {
				tagEndIdx = tagEndIdx2;
				endTag = majorEndTag2;
			}
			if(tagEndIdx1 != -1) {
				tagEndIdx = tagEndIdx1;
				endTag = minorEndTag1;
			}
			if(tagEndIdx != -1) {
				newSource = source.substring(0, tagStartIdx).concat(source.substring(tagEndIdx+endTag.length(), source.length()));
				if(newSource.length() > source.length()) return source;
				source = newSource;
			} else {
				return source;
			}
		}
		return source;
	}

	public static int lastIndexOf(int[] array,int value,int startIdx,int endIdx, boolean left2Right) {
		int index=-1;
		if(left2Right) {
			for(int i=startIdx; i<endIdx; i++) if(array[i] == value) index=i;
			return index;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(array[i] == value) index=i;
			return index;
		}
	}

	public static int lastIndexOf(long[] array,int value,int endIdx) {
		for(int i=array.length; i>endIdx; i--) if(array[i] == value) return i;
		return -1;
	}

	public static int lastIndexOf(String text, char ch, int startIdx, int endIdx) {
		for(int i=endIdx-1; i>startIdx; i--) if(text.charAt(i) == ch) return i;
		return -1;
	}

	public static String flattenToAscii(String string) {
		char[] out = new char[string.length()];
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		int j = 0;
		for (int i = 0, n = string.length(); i < n; ++i) {
			char c = string.charAt(i);
			if (c <= '\u007F') out[j++] = c;
		}
		return new String(out);
	}

	public static int fstIdxLesThn(byte value, byte[] ary) {
		for(int i=0; i<ary.length; i++) if(ary[i] < value) return i;
		return -1;
	}

	public static int fstIdxLesThn(byte value, byte[] ary, int startIdx) {
		for(int i=startIdx; i<ary.length; i++) if(ary[i] < value) return i;
		return -1;
	}

	public static int lowestBelowIdx(byte value, byte[] ary, int startIdx) {
		return lowestBelowIdx(value,ary,startIdx,ary.length,true);
	}

	public static int lowestBelowIdx(byte value, byte[] ary, int startIdx, int endIdx, boolean left2Right) {
		int lowestIdx = -1;
		if(left2Right) {
			for(int i=startIdx; i<endIdx; i++) if(ary[i] < value) {
				value = ary[i];
				lowestIdx = i;
			}
		} else {
			for(int i=startIdx; i>endIdx; i--)  if(ary[i] < value) {
				value = ary[i];
				lowestIdx = i;
			}
		}
		return lowestIdx;
	}

	public static int fstIdxLesThn(byte value, byte[] ary, int startIdx, int endIdx, boolean left2Right) {
		if(left2Right) {
			for(int i=startIdx; i<endIdx; i++) if(ary[i] < value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(ary[i] < value) return i;
		}
		return -1;
	}

	public static boolean hshSetAryAnyOvrLap(HashSet<String> hm, String[] ary) {
		for(String s : ary) if(hm.contains(s)) return true;
		return false;
	}

	public static int aryColIdx2D(int[][] ary2D,int val2Find) {
		for(int i=0; i<ary2D.length;i++) for(int x : ary2D[i]) if(x==val2Find) return i;
		return -1;
	}

	public static int[][] addAryTo2DAry(int[][] twoDAry, int[] aryToAdd,int addIdx){
		int[][] newAry = new int[twoDAry.length+1][];
		for(int i=0; i<addIdx;i++) newAry[i] = twoDAry[i];
		newAry[addIdx] = aryToAdd;
		for(int i=addIdx+1; i<newAry.length;i++) newAry[i] = twoDAry[i-1];
		return newAry;
	}

	public static int[][] removeAryFrom2DAry(int[][] twoDAry, int removeIdx) {
		int[][] newAry = new int[twoDAry.length-1][];
		int index=0;
		for(int i=0; i<twoDAry.length; i++) {
			if(index == removeIdx) index++;
			newAry[i] = twoDAry[index++];
		}
		return newAry;
	}

	public static int[][] deepCopyIntMatrix(int[][] input) {
		if (input == null)  return null;
		int[][] result = new int[input.length][];
		for (int r = 0; r < input.length; r++)  result[r] = input[r].clone();
		return result;
	}

	public static int firstIndexOf(int[] ary2Search, int[] values) {
		int valueLen = values.length;
		for(int i=0; i<ary2Search.length; i++) for(int j=0; j<valueLen; j++) if(ary2Search[i] == values[j]) return i;
		return -1;
	}

	public static long intsToLong(int x, int y) {
		return (((long)x) << 32) | (y & 0xffffffffL);
	}

	public static int firstIntInLong(long l) {
		return (int)(l >> 32);
	}

	public static int secondIntInLong(long l) {
		return (int)l;
	}

	public static TObjectIntCustomHashMap<String> newStringIntHashMap(){
		return newStringIntHashMap(50,(float)0.7);
	}

	public static TObjectIntCustomHashMap<String> newStringIntHashMap(int initialSize, float loadFact) {
		return new TObjectIntCustomHashMap<String>(new HashingStrategy<String>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(String object) {
				return Utils2.hash(object);
			}
			@Override
			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		},initialSize,loadFact);
	}

	public static TObjectByteCustomHashMap<String> newStringByteHashMap(byte defValue){
		return newStringByteHashMap(50,(float)0.7,defValue);
	}

	public static TObjectByteCustomHashMap<String> newStringByteHashMap(){
		return newStringByteHashMap(50,(float)0.7,(byte)0);
	}

	public static TObjectByteCustomHashMap<String> newStringByteHashMap(int initialSize, float loadFact){
		return newStringByteHashMap(initialSize,loadFact,(byte)0);
	}

	public static TObjectByteCustomHashMap<String> newStringByteHashMap(int initialSize, float loadFact,byte defValue) {
		return new TObjectByteCustomHashMap<String>(new HashingStrategy<String>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(String object) {
				return Utils2.hash(object);
			}
			@Override
			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		},initialSize,loadFact,defValue);
	}

	public static TObjectLongCustomHashMap<String> newStringLongHashMap(){
		return newStringLongHashMap(50,(float)0.7);
	}

	public static TObjectLongCustomHashMap<String> newStringLongHashMap(int initialSize, float loadFact) {
		return new TObjectLongCustomHashMap<String>(new HashingStrategy<String>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(String object) {
				return Utils2.hash(object);
			}
			@Override
			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		},initialSize,loadFact);
	}

	public static TCustomHashMap<byte[], byte[]> newByteAryByteAryHashMap(){
		return newByteAryByteAryHashMap(50,(float)0.7);
	}

	public static TCustomHashMap<byte[], byte[]> newByteAryByteAryHashMap(int initialSize, float loadFact){
		return new TCustomHashMap<byte[], byte[]> (new HashingStrategy<byte[]>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(byte[] object) {
				return hash(object);
			}
			@Override
			public boolean equals(byte[] o1, byte[] o2) {
				return Arrays.equals(o1, o2);
			}
		},initialSize,loadFact);
	}



	public static TCustomHashMap<int[], int[]> newIntAryIntAryHashMap(){
		return newIntAryIntAryHashMap(50,0.7f);
	}

	public static TCustomHashSet<String> newStringHashSet(){
		return newStringHashSet(50,0.7f);
	}

	public static TCustomHashSet<String> newStringHashSet(int initialSize, float loadFact){
		return new TCustomHashSet<String>(new HashingStrategy<String>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(String object) {
				return Utils2.hash(object);
			}
			@Override
			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		},initialSize,loadFact);
	}

	public static TCustomHashMap<int[], int[]> newIntAryIntAryHashMap(int initialSize, float loadFact){
		return new TCustomHashMap<int[], int[]> (new HashingStrategy<int[]>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(int[] object) {
				return hash(object);
			}
			@Override
			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		},initialSize,loadFact);
	}

	public static TCustomHashMap<byte[], int[]> newByteAryIntAryHashMap(){
		return newByteAryIntAryHashMap(50,(float)0.7);
	}

	public static TCustomHashMap<byte[], int[]> newByteAryIntAryHashMap(int initialSize, float loadFact){
		return new TCustomHashMap<byte[], int[]> (new HashingStrategy<byte[]>() {
			private static final long serialVersionUID = 1412010267385998998L;
			@Override
			public int computeHashCode(byte[] object) {
				return hash(object);
			}
			@Override
			public boolean equals(byte[] o1, byte[] o2) {
				return Arrays.equals(o1, o2);
			}
		},initialSize,loadFact);
	}


	public static boolean SetNAryAnyOverlap (Set<String> st, String[] ary) {
		for(String s : ary) if(st.contains(s)) return true;
		return false;
	}

	/**
	 * 
	 * @param source
	 * @param startIdx search from idx inclusive
	 * @param endIdx search to idx exclusive
	 * @param data
	 * @param returnIdxFrom0, return offset from 0 or from source.length
	 * @return index where data starts from 0 or source.length (depending on returnIdxFrom0) or -1 if not present
	 */
	public static int AryContainsIdx(int[] source, int startIdx, int endIdx, int[] data, boolean returnIdxFrom0) {
		main:for(int i=startIdx, len = (endIdx-data.length)+1; i<len; i++) {
			for(int j=0; j<data.length; j++) {
				if(source[i+j] != data[j]) continue main;
			}
			if(returnIdxFrom0) return i; else return endIdx - i;
		}
	return -1;
	}


	/**
	 * 
	 * @param source
	 * @param startIdx search from idx inclusive
	 * @param endIdx search to idx exclusive
	 * @param data
	 * @param returnIdxFrom0, return offset from 0 or from source.length
	 * @return true if data ary is present in source
	 */
	public static boolean AryContains(int[] source, int startIdx, int endIdx, int[] data) {
		main:for(int i=startIdx, len = (endIdx-data.length)+1; i<len; i++) {
			for(int j=0; j<data.length; j++) {
				if(source[i+j] != data[j]) continue main;
			}
			return true;
		}
	return false;
	}

	public static boolean AryContains(int[] source, int[] data) {
		return AryContains(source, 0, source.length, data);
	}

	public static double round (double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}


	public static void printByteAryBits(byte[] ba, String msg) {
		System.out.println(msg);
		for(byte b : ba) System.out.print("value=" + b + "  " + String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0') + "  ");
		System.out.println();
	}


	public static int indexOf(int endIdx, String[] words, String word) {
		for(int i=0; i<endIdx; i++) if(words[i].equals(word)) return i;
		return -1;
	}


	public static int indexOf(byte[] ary,byte value, int startIdx, int endIdx, boolean left2Right) {
		if(ary == null) return -1;
		if(left2Right) {
			if(endIdx > ary.length) endIdx=ary.length;
			for(int i=startIdx; i<endIdx; i++) if(ary[i] == value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(ary[i] == value) return i;
		}
		return -1;
	}

	public static int indexOf(String txt,char value, int startIdx, int endIdx, boolean left2Right) {
		if(left2Right) {
			if(endIdx > txt.length()) endIdx=txt.length();
			for(int i=startIdx; i<endIdx; i++) if(txt.charAt(i) ==value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(txt.charAt(i) == value) return i;
		}
		return -1;
	}


	public static int indexOf(short[] ary, short value, int startIdx, int endIdx, boolean left2Right) {
		if(ary == null) return -1;
		if(left2Right) {
			if(endIdx > ary.length) endIdx=ary.length;
			for(int i=startIdx; i<endIdx; i++) if(ary[i] == value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(ary[i] == value) return i;
		}
		return -1;
	}

	public static int indexOf(int[] ary, int value, int startIdx, int endIdx, boolean left2Right) {
		if(ary == null) return -1;
		if(left2Right) {
			if(endIdx > ary.length) endIdx=ary.length;
			for(int i=startIdx; i<endIdx; i++) if(ary[i] == value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(ary[i] == value) return i;
		}
		return -1;
	}

	public static long indexOf(long[] ary, long value, int startIdx, int endIdx, boolean left2Right) {
		if(ary == null) return -1;
		if(left2Right) {
			if(endIdx > ary.length) endIdx=ary.length;
			for(int i=startIdx; i<endIdx; i++) if(ary[i] == value) return i;
		} else {
			for(int i=startIdx; i>endIdx; i--) if(ary[i] == value) return i;
		}
		return -1;
	}

	public static int  mean(int[] data)
	{
		int sum = 0;
		for(int a : data)  sum += a;
		return sum/data.length;
	}

	public static int  mean(Integer[] data)
	{
		int sum = 0;
		for(int a : data)  sum += a;
		return sum/data.length;
	}

	public static int variance(int[] data)
	{
		int mean = mean(data);
		int temp = 0;
		for(int a :data)  temp += (mean-a)*(mean-a);
		return temp/data.length;
	}

	public static int stdDev(int[] data)
	{
		return (int) Math.round(Math.sqrt(variance(data)));
	}

	public static int median(int[] data) 
	{
		int[] b = new int[data.length];
		System.arraycopy(data, 0, b, 0, b.length);
		Arrays.sort(b);

		if (data.length % 2 == 0) 
		{
			return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2;
		}
		return b[b.length / 2];
	}

	//use on small arrays only, < 50
	public static void aryTinySort(int[] a) {
		final int last = a.length-1;
		int ai;
		for (int i = 0, j = i; i < last; j = ++i) {
			ai = a[i + 1];
			while (ai < a[j]) {
				a[j + 1] = a[j];
				if (j-- == 0) {
					break;
				}
			}
			a[j + 1] = ai;
		}
	}

	//use on small arrays only, < 50
	public static long[] aryTinySort(long[] a) {
		final int last = a.length-1;
		long ai;
		for (int i = 0, j = i; i < last; j = ++i) {
			ai = a[i + 1];
			while (ai < a[j]) {
				a[j + 1] = a[j];
				if (j-- == 0) {
					break;
				}
			}
			a[j + 1] = ai;
		}
		return a;
	}

	//use on small arrays only, < 50
	public static void aryTinySort(short[] a) {
		final int last = a.length-1;
		short ai;
		for (int i = 0, j = i; i < last; j = ++i) {
			ai = a[i + 1];
			while (ai < a[j]) {
				a[j + 1] = a[j];
				if (j-- == 0) {
					break;
				}
			}
			a[j + 1] = ai;
		}
	}

	//use on small arrays only, < 50
	public static void aryTinySort(byte[] a) {
		final int last = a.length-1;
		byte ai;
		for (int i = 0, j = i; i < last; j = ++i) {
			ai = a[i + 1];
			while (ai < a[j]) {
				a[j + 1] = a[j];
				if (j-- == 0) {
					break;
				}
			}
			a[j + 1] = ai;
		}
	}


	public static byte[] aryRemoveElements(byte[] ary, int fromIndex, int numRemovals){
		byte[] newAry = new byte[ary.length-numRemovals];
		for(int i=0; i<ary.length; i++){
			if(i==fromIndex)  break;
			newAry[i] = ary[i];
		}
		for(int i=fromIndex+numRemovals; i<ary.length; i++){
			newAry[i-numRemovals] = ary[i];
		}
		return newAry;
	}

	public static int[] aryRemoveElements(int[] ary, int fromIndex, int numRemovals){
		int[] newAry = new int[ary.length-numRemovals];
		for(int i=0; i<ary.length; i++){
			if(i==fromIndex)  break;
			newAry[i] = ary[i];
		}
		for(int i=fromIndex+numRemovals; i<ary.length; i++){
			newAry[i-numRemovals] = ary[i];
		}
		return newAry;
	}

	public static String[] aryRemoveElements(String[] ary, int fromIndex, int numRemovals){
		String[] newAry = new String[ary.length-numRemovals];
		for(int i=0; i<ary.length; i++){
			if(i==fromIndex)  break;
			newAry[i] = ary[i];
		}
		for(int i=fromIndex+numRemovals; i<ary.length; i++){
			newAry[i-numRemovals] = ary[i];
		}
		return newAry;
	}


	public static void AllRowCombinations(int[][] grid, int column, TIntArrayList aRow, ArrayList<int[]> finalRows) {
		if (column > grid.length - 1) {
			finalRows.add(aRow.toArray());
			return;
		}
		for (int k : grid[column]) {
			TIntArrayList row = new TIntArrayList(aRow.toArray());
			row.add(k);
			AllRowCombinations(grid, column + 1, row,finalRows);
		}
	}



	public static <K extends Comparable<K>, V> Map<K, V> sortByKeys(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			@Override
			public int compare(K k1, K k2) {
				int compare = k1.compareTo(k2);
				if (compare == 0) return 1;
				return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return new LinkedHashMap<K,V>(sortedByValues);
	}

	public static <K extends Comparable<K>, V extends Comparable<? super V>> Map<K, V>  sortByValues( Map<K, V> map ) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				int compare = (o1.getValue()).compareTo( o2.getValue() );
				if (compare == 0) {
					compare =  o1.getKey().compareTo(o2.getKey());
					if(compare == 0) return 1;
					return compare;
				}
				return compare;
			}
		} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	public static int AryOverlapAmount(Object[] ary1, Object[] ary2){
		int overlap = 0;
		for(Object o1 : ary1){
			for(Object o2 : ary2){
				if(o1.equals(o2)){
					overlap ++;
					break;
				}
			}
		}
		return overlap;
	}

	public static int AryOverlapAmount(int[] ary1, int[] ary2){
		int overlap = 0;
		for(int o1 : ary1){
			for(int o2 : ary2){
				if(o1==o2){
					overlap ++;
					break;
				}
			}
		}
		return overlap;
	}

	public static int AryFirstOverlapValue(int[] ary1, int[] ary2){
		for(int o1 : ary1){
			for(int o2 : ary2){
				if(o1==o2){
					return o1;
				}
			}
		}
		return -1;
	}
	/** idx returned is match from first array **/
	public static int AryFirstOverlapValueIdx(int[] ary1, int[] ary2){
		for(int i=0; i<ary1.length;i++) for(int j=0;j<ary2.length;j++) if(ary1[i] == ary2[j]) return i;
		return -1;
	}

	public static int[] inAllArrays(int[]... arys) {
		int smallestArySize = Integer.MAX_VALUE;
		int smallestAryIdx=0;
		boolean inAll;
		for(int i=0; i<arys.length;i++) if(arys[i].length<=smallestArySize) {
			smallestArySize =arys[i].length;
			smallestAryIdx=i;
		}
		int[] smallestAry = arys[smallestAryIdx];
		int[] curAry;
		TIntArrayList lst = new TIntArrayList(smallestArySize);
		for(int i=0;i<smallestAry.length;i++) {
			inAll=true;
			allArys:for(int j=0; j<arys.length;j++) {
				if(j==smallestAryIdx) continue;
				curAry = arys[j];
				for(int k=0;k<curAry.length;k++) if(smallestAry[i] == curAry[k]) continue allArys;
				inAll=false;
				break;
			}
			if(inAll) lst.add(smallestAry[i]);
		}
		return lst.toArray();
	}

	public static List<int[]> AllRowCombinations(int[][] g) {
		int []i = new int[g.length];
		List<int[]> list = new ArrayList<>();
		while (i[0] < g.length) {
			int[] v = new int[g.length];
			for (int rr = 0; rr < g.length; rr++) {
				v[rr] = g[rr][i[rr]];
			}
			list.add(v);
			int j = g.length - 1;
			i[j]++;
			while (j > 0 && i[j] >= g[j].length) {
				i[j] = 0;
				i[--j]++;
			}
		}
		return list;
	}

	public static boolean AllRowCombinations2(int[][] matrix, ArrayList<int[]> results,int maxResults) {
		int[] index = new int[matrix.length];
		do{
			results.add(nextRow(index,matrix));
			if(results.size() > maxResults)  return false;	
		} while(nextRowIndexes(index,matrix));
		return true;
	}


	public static  int[] nextRow(int[] index, int[][] matrix) {
		int[] c= new int[index.length];
		for (int i= 0; i < index.length; i++)
			c[i]= matrix[i][index[i]];
		return c;
	}

	public  static  boolean nextRowIndexes(int[] index, int[][] matrix) {
		for (int i= index.length ; i-- > 0;) {
			if (++index[i] >= matrix[i].length){
				index[i]= 0;
			}else{
				return true;
			}
		}
		return false;
	}

	public static int averageArray(int[] numbers){
		return sumArray(numbers) / numbers.length;
	}

	public static int sumArray(int[] numbers){
		int total = 0;
		for(int i : numbers){
			total += i;
		}
		return total;
	}

	public static long sumArray(long[] numbers){
		long total = 0;
		for(long i : numbers){
			total += i;
		}
		return total;
	}

	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}



	//check if arrays contains the same elements (regardless of order of elements)
	public static  boolean isArraysEqual(Object[] a1, Object[] a2){
		if(a1.length != a2.length) return false;
		HashSet<Object> al = new HashSet<Object>(a1.length);
		for(Object o : a1) al.add(o);
		for(Object o2 : a2){
			if(!al.contains(o2)) return false;
		}
		return true;
	}



	public static  byte ByteRandom(byte min, byte max){
		return new Integer(rand.nextInt(max - min + 1) + min).byteValue();
	}

	public static  short ShortRandom(short min, short max){
		return new Integer(rand.nextInt(max - min + 1) + min).shortValue();
	}

	public static int IntRandom(int min, int max){
		return rand.nextInt(max - min + 1) + min;
	}

	public static byte[] randomBytes(byte[] b){
		rand.nextBytes(b);
		return b;
	}



	public static int[] randomInts(int[] ary){
		for(int i=0; i<ary.length; i++) ary[i] = rand.nextInt();
		return ary;
	}

	public static long[] randomLongs(long[] ary){
		for(int i=0; i<ary.length; i++) ary[i] = rand.nextLong();
		return ary;
	}



	public static String compress(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		try{
			// System.out.println("String length : " + str.length());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			String outStr = out.toString("ISO-8859-1");
			// System.out.println("Output String lenght : " + outStr.length());
			return outStr;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static String decompress(String str)  {
		if (str == null || str.length() == 0) {
			return str;
		}
		try{
			//System.out.println("Input String length : " + str.length());
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
			String line;
			StringBuilder sb = new StringBuilder(10000);
			while ((line=bf.readLine())!=null) {
				sb.append(line);
			}
			//System.out.println("Output String lenght : " + outStr.length());
			return sb.toString();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static  long LongRandom(long min, long max){
		long bits, val;
		long n = max - min + 1;
		if(n==0) n=1;
		do {
			bits = (rand.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits-val+(n-1) < 0L);
		return val + min;
	}

	public static  long LongRandom() {
		return rand.nextLong() ^ rand.nextLong();
	}

	public static boolean arysSegmentMatch(byte[] ary1, byte[] ary2, int length) {
		return arysSegmentMatch(ary1,ary2,0,length,0);
	}

	public static boolean arysSegmentMatch(byte[] ary1, byte[] ary2, int ary1StartIdx, int ary1EndIdx, int ary2StartIdx) {
		int segLength = ary1EndIdx-ary1StartIdx;
		if(ary2StartIdx+segLength > ary2.length) return false;
		for(int i=0; i<segLength; i++) if(ary1[ary1StartIdx+i] != ary2[ary2StartIdx+i]) return false;
		return true;
	}


	public static int[] ArrayListArysMerge(ArrayList<int[]> aryLst) {
		int length = 0, idx=0;
		for(int[] ary : aryLst) length += ary.length;
		int[] out = new int[length];
		for(int[] ary : aryLst) for(int v : ary)  out[idx++] = v;
		return out;
	}

	public static  byte[] aryMerge(byte[]... arys) {
		int length = 0;
		int count =0;
		for(byte[] ary : arys) length+=ary.length;
		byte[] out = new byte[length];
		for(byte[] ary : arys) for(int i=0; i<ary.length; i++) out[count++] = ary[i];
		return out;
	}

	public static  String[] aryMerge(String[]... arys) {
		int length = 0;
		int count =0;
		for(String[] ary : arys) length+=ary.length;
		String[] out = new String[length];
		for(String[] ary : arys) for(int i=0; i<ary.length; i++) out[count++] = ary[i];
		return out;
	}

	public static  int[] aryMerge(int[]... arys) {
		int length = 0;
		int count =0;
		for(int[] ary : arys) length+=ary.length;
		int[] out = new int[length];
		for(int[] ary : arys) for(int i=0; i<ary.length; i++) out[count++] = ary[i];
		return out;
	}

	public static  byte[] aryMergeNoDup(byte[]... arys){
		TByteHashSet hs = new TByteHashSet(181);
		for(byte[] ary : arys){
			if(ary == null) continue;
			for(byte item : ary){
				hs.add(item);
			}
		}
		return hs.toArray();
	}

	public static short[] aryMergeNoDup(short[]... arys){
		TShortHashSet hs = new TShortHashSet(181);
		for(short[] ary : arys){
			if(ary == null) continue;
			for(short item : ary){
				hs.add(item);
			}
		}
		return hs.toArray();
	}

	public static  int[] aryMergeNoDup(int[]... arys){
		TIntHashSet hs = new TIntHashSet(181);
		for(int[] ary : arys){
			if(ary == null) continue;
			for(int item : ary){
				hs.add(item);
			}
		}
		return hs.toArray();
	}

	public static  long[] aryMergeNoDup(long[]... arys){
		TLongHashSet hs = new TLongHashSet(181);
		for(long[] ary : arys){
			if(ary == null) continue;
			for(long item : ary){
				hs.add(item);
			}
		}
		return hs.toArray();
	}

	public static  <E> int numOccurances(E[] ary, E value){
		int num = 0;
		for(E v : ary){
			if(v.equals(value)) num++;
		}
		return num;
	}

	public static   int numOccurances(byte[] ary, byte value){
		int num = 0;
		for(byte v : ary){
			if(v == value) num++;
		}
		return num;
	}

	public static  int numOccurances(short[] ary, short value){
		int num = 0;
		for(short v : ary){
			if(v == value) num++;
		}
		return num;
	}

	public static  int numOccurances(int[] ary, int value){
		int num = 0;
		for(int v : ary){
			if(v == value) num++;
		}
		return num;
	}

	public static  int numOccurances(long[] ary, long value){
		int num = 0;
		for(long v : ary){
			if(v == value) num++;
		}
		return num;
	}

	public static int ArraysOverlapAmount(String[] ary1, String[] ary2) {
		int overlap = 0;
		for(String s1 : ary1) {
			for(String s2 : ary2) {
				if(s1.equals(s2)) {
					overlap++;
					break;
				}
			}
		}
		return overlap;
	}

	public static int ArraysOverlapAmount(long[] ary1, long[] ary2) {
		int overlap = 0;
		for(long s1 : ary1) {
			for(long s2 : ary2) {
				if(s1==s2) {
					overlap++;
					break;
				}
			}
		}
		return overlap;
	}

	/** return match index from second arrau **/
	public static int ArraysAnyOverlapIdx(Object[] a1, int a1endIdx, Object[] a2){
		if(a1endIdx < a2.length) {
			for(int i=0; i<a1endIdx; i++) for(int j=0; j<a2.length; j++) if(a1[i].equals(a2[j])) return j;
		} else {
			for(int j=0; j<a2.length; j++) for(int i=0; i<a1endIdx; i++) if(a2[j].equals(a1[i])) return j;
		}
		return -1;
	}

	public static boolean ArraysAnyOverlap(Object[] a1, int a1endIdx, Object[] a2){
		if(a1endIdx < a2.length) {
			for(int i=0; i<a1endIdx; i++) for(Object b : a2) if(a1[i].equals(b)) return true;
		} else {
			for(Object a : a2) for(int i=0; i<a1endIdx; i++) if(a.equals(a1[i])) return true;
		}
		return false;
	}

	public static boolean ArraysAnyOverlap(ArrayList<String> a1, String[] a2){
		if(a1.size()< a2.length) {
			for(String a : a1) for(String b : a2) if(a.equals(b)) return true;
		} else {
			for(String  a : a2) for(String  b : a1) if(a.equals(b)) return true;
		}
		return false;
	}


	public static boolean ArraysAnyOverlap(Object[] a1, Object[] a2){
		if(a1.length < a2.length) {
			for(Object a : a1) for(Object b : a2) if(a.equals(b)) return true;
		} else {
			for(Object a : a2) for(Object b : a1) if(a.equals(b)) return true;
		}
		return false;
	}

	public static boolean ArraysAnyOverlap(int[] a1, int[] a2){
		if(a1.length < a2.length) {
			for(int a : a1) for(int b : a2) if(a==b) return true;
		} else {
			for(int a : a2) for(int b : a1) if(a==b) return true;
		}
		return false;
	}

	public static boolean ArraysAnyOverlap(byte[] a1, byte[] a2){
		if(a1.length < a2.length) {
			for(byte a : a1) for(byte b : a2) if(a==b) return true;
		} else {
			for(byte a : a2) for(byte b : a1) if(a==b) return true;
		}
		return false;
	}

	public static boolean ArraysAnyOverlap(long[] a1, long[] a2){
		if(a1.length < a2.length) {
			for(long a : a1) for(long b : a2) if(a==b) return true;
		} else{
			for(long a : a2) for(long b : a1) if(a==b) return true;
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public static  <E> E[]  aryMergeNoDup(E[]... arys){
		HashSet<E> hs = new HashSet<E>();
		for(E[] ary : arys){
			if(ary == null) continue;
			for(E item : ary){
				if(item != null) hs.add(item);
			}
		}
		hs.remove(null);
		return hs.toArray((E[]) Array.newInstance(arys.getClass().getComponentType().getComponentType(),hs.size()));
	}

	@SuppressWarnings("unchecked")
	public static  <E> E[]  aryMerge(E[]... arys){
		int finalSize = 0, count=0;
		for(E[] ary : arys) {
			if(ary == null) continue;
			finalSize += ary.length;
		}
		E[] out =  (E[]) Array.newInstance(arys.getClass().getComponentType().getComponentType(),finalSize);
		for(E[] ary : arys){
			if(ary == null) continue;
			for(int i=0; i<ary.length; i++) out[count++] = ary[i];
		}
		return out;
	}

	public static  long[] toLongArray(List<Long> longList) {
		long[] longArray = new long[longList.size()];
		int i=0;
		for (Long num : longList) {
			longArray[i++] = num;
		}
		return longArray;
	}

	public static  int[] toIntArray(List<Integer> integerList) {
		int[] intArray = new int[integerList.size()];
		int i=0;
		for (Integer num : integerList) {
			intArray[i++] = num;
		}
		return intArray;
	}

	public static short[] toShortArray(List<Short> shortList) {
		short[] shortArray = new short[shortList.size()];
		int i=0;
		for (Short num : shortList) {
			shortArray[i++] = num;
		}
		return shortArray;
	}

	public static  byte[] toByteArray(List<Byte> byteList) {
		byte[] intArray = new byte[byteList.size()];
		int i=0;
		for (Byte num : byteList) {
			intArray[i++] = num;
		}
		return intArray;
	}

	public static  ArrayList<Long> toLongList(long[] values){
		ArrayList<Long> ary = new ArrayList<>(values.length);
		for(long v : values) ary.add(v);
		return ary;
	}

	public static  ArrayList<Integer> toIntegerList(int[] values){
		ArrayList<Integer> ary = new ArrayList<>(values.length);
		for(int v : values) ary.add(v);
		return ary;
	}

	public static ArrayList<Short> toShortList(short[] values){
		ArrayList<Short> ary = new ArrayList<>(values.length);
		for(Short v : values) ary.add(v);
		return ary;
	}

	public static  ArrayList<Byte> toLongList(byte[] values){
		ArrayList<Byte> ary = new ArrayList<>(values.length);
		for(byte v : values) ary.add(v);
		return ary;
	}

	public static short highestShortValue(short[] values){
		short currentHighest = Short.MIN_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
			}
		}
		return currentHighest;
	}

	public static short lowestShortValue(short[] values){
		short currentLowest = Short.MAX_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
			}
		}
		return currentLowest;
	}

	public static  int highestShortValueIndex(short[] values){
		short currentHighest = Short.MIN_VALUE;
		int highestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
				highestIndex = i;
			}
		}
		return highestIndex;
	}

	public static  int lowestShortValueIndex(short[] values){
		short currentLowest =  Short.MAX_VALUE;
		int lowestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
				lowestIndex = i;
			}
		}
		return lowestIndex;
	}

	public static  int highestIntValue(int[] values){
		int currentHighest = Integer.MIN_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
			}
		}
		return currentHighest;
	}

	public static  int highestIntValue(TIntArrayList values){
		int currentHighest = Integer.MIN_VALUE;
		for(int i=0; i<values.size(); i++){
			if(values.getQuick(i) > currentHighest){
				currentHighest = values.getQuick(i);
			}
		}
		return currentHighest;
	}

	public static  int lowestIntValue(int[] values){
		int currentLowest = Integer.MAX_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
			}
		}
		return currentLowest;
	}

	public static  int highestIntValueIndex(int[] values){
		int currentHighest = Integer.MIN_VALUE;
		int highestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
				highestIndex = i;
			}
		}
		return highestIndex;
	}

	public static  int highestIntValueIndex(TIntArrayList values){
		int currentHighest = Integer.MIN_VALUE;
		int highestIndex = 0;
		for(int i=0; i<values.size(); i++){
			if(values.getQuick(i) > currentHighest){
				currentHighest = values.getQuick(i);
				highestIndex = i;
			}
		}
		return highestIndex;
	}

	public static  int lowestIntValueIndex(int[] values){
		int currentLowest = Integer.MAX_VALUE;
		int lowestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
				lowestIndex = i;
			}
		}
		return lowestIndex;
	}

	public static  byte highestByteValue(byte[] values){
		byte currentHighest = Byte.MIN_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
			}
		}
		return currentHighest;
	}

	public static  byte lowestByteValue(byte[] values){
		byte currentLowest = Byte.MAX_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
			}
		}
		return currentLowest;
	}

	public static  int highestByteValueIndex(byte[] values){
		byte currentHighest = Byte.MIN_VALUE;
		int highestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] > currentHighest){
				currentHighest = values[i];
				highestIndex = i;
			}
		}
		return highestIndex;
	}

	public static  int lowestByteValueIndex(byte[] values){
		byte currentLowest = Byte.MAX_VALUE;
		int lowestIndex = 0;
		for(int i=0; i<values.length; i++){
			if(values[i] < currentLowest){
				currentLowest = values[i];
				lowestIndex = i;
			}
		}
		return lowestIndex;
	}


	//	//Robert Jenkins' 32 bit integer hash function   
	//	public static  int hash( int key)
	//	{
	//		key = (key+0x7ed55d16) + (key<<12);
	//		key = (key^0xc761c23c) ^ (key>>19);
	//		key = (key+0x165667b1) + (key<<5);
	//		key = (key+0xd3a2646c) ^ (key<<9);
	//		key = (key+0xfd7046c5) + (key<<3);
	//		key = (key^0xb55a4f09) ^ (key>>16);
	//		return key;
	//	}
	//
	//	//Robert Jenkins' 32 bit integer hash function output constrained to maxvalue
	//	public static  int hashConstrained( int key, int maxvalue)
	//	{
	//		key = (key+0x7ed55d16) + (key<<12);
	//		key = (key^0xc761c23c) ^ (key>>19);
	//		key = (key+0x165667b1) + (key<<5);
	//		key = (key+0xd3a2646c) ^ (key<<9);
	//		key = (key+0xfd7046c5) + (key<<3);
	//		key = (key^0xb55a4f09) ^ (key>>16);
	//		return key & maxvalue;
	//	}

	// rounding m up to next highest multiple of n
	public static  int RndMultipleOfCeil(int value, int multiple){
		return  ( value + multiple - 1 ) / multiple * multiple;
	}
	// rounding m down to multiple of n
	public static int RndMultipleOfFloor(int value, int multiple){
		return value /multiple * multiple;
	}
	// rounding m to nearest multiple of n
	public static  int RndMultipleOfNear(int value, int multiple){
		return( value + multiple/2 ) / multiple * multiple;
	}

	public static Class<? extends Number> numtoClassType(byte num){
		if(num==1) return Byte.TYPE; else if(num==2) return Short.TYPE; else if(num==3) return Integer.TYPE; else if(num==4) return Long.TYPE; else return null;
	}

	public static byte classtoNumType(Class<? extends Number> type){
		if(type==Byte.TYPE) return 1; else if(type==Short.TYPE) return 2; else if(type==Integer.TYPE) return 4; else if(type==Long.TYPE) return 8; else return -1;
	}

	public static byte[] objtoByteAry(Object obj) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(obj);
		oos.flush(); 
		oos.close(); 
		bos.close();
		return bos.toByteArray();
	}

	public static byte[] toByteArray(short value) {
		return new byte[] {
				(byte)(value >>> 8),
				(byte)value};
	}

	public static short byteArrayToShort(byte [] b) {
		return (short) ((short) ((b[0] & 0xFF) << 0) + ((b[1] & 0xFF) << 8));
	}

	public static void toByteArrayList(TByteArrayList bal, int value) {
		bal.add((byte)(value >>> 24));
		bal.add((byte)(value >>> 16));
		bal.add((byte)(value >>> 8));
		bal.add((byte)value);
	}


	public static ArrayList<Byte> toByteList(byte[] ba) {
		ArrayList<Byte> l = new ArrayList<>();
		for(byte b : ba) l.add(b);
		return l;
	}
	public static byte[] toByteArray(int value, byte[] array) {
		array[0] = (byte)(value >>> 24);
		array[1] = (byte)(value >>> 16);
		array[2] = (byte)(value >>> 8);
		array[3] = (byte)value;
		return array;
	}

	public static byte[] longToByteArray(long value, byte[] array, int startIdx) {
		array[startIdx] = (byte)(value >>> 56);
		array[startIdx+1] = (byte)(value >>> 48);
		array[startIdx+2] = (byte)(value >>> 40);
		array[startIdx+3] = (byte)(value >>> 32);
		array[startIdx+4] = (byte)(value >>> 24);
		array[startIdx+5] = (byte)(value >>> 16);
		array[startIdx+6] = (byte)(value >>> 8);
		array[startIdx+7] = (byte)value;
		return array;
	}

	public static byte[] intToByteArray(int value, byte[] array, int startIdx) {
		array[startIdx] = (byte)(value >>> 24);
		array[startIdx+1] = (byte)(value >>> 16);
		array[startIdx+2] = (byte)(value >>> 8);
		array[startIdx+3] = (byte)value;
		return array;
	}

	public static byte[] shortToByteArray(short value, byte[] array, int startIdx) {
		array[startIdx] = (byte)(value >>> 8);
		array[startIdx+1] = (byte)value;
		return array;
	}

	public static byte[] byteAryIntoByteAry(byte[] values, byte[] array,int startIdx) {
		for(int i=0, len=values.length; i<len; i++) array[startIdx+i] = values[i];
		return array;
	}

	public static byte[] toByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}


	public static byte[] toByteArray(TIntArrayList ial) {
		byte[] b = new byte[ial.size()*4];
		int counter = 0;
		int[] data = ial._data;
		int aNum;
		for(int i=0; i<ial.size(); i++) {
			aNum = data[i];
			b[counter] = (byte)(aNum >>> 24);
			b[counter+1] = (byte)(aNum >>> 16);
			b[counter+2] = (byte)(aNum >>> 8);
			b[counter+3] = (byte)aNum;
			counter+=4;
		}
		return b;
	}

	public static int toInt(byte[] ba) {
		return toInt(ba,0);
	}

	public static int toInt(byte[] ba, int startIdx) {
		return  (ba[startIdx] << 24)  + ((ba[startIdx+1] & 0xFF) << 16) + ((ba[startIdx+2] & 0xFF) << 8) + (ba[startIdx+3] & 0xFF);
	}

	//	public static int[] toIntArray(byte[] ba){
	//		if(ba == null) return null;
	//		int[] ia = new int[ba.length/4];
	//		int counter = 0;
	//		int intCounter = 0;
	//		while(counter < ba.length){
	//			ia[intCounter] = (ba[counter] << 24)  + ((ba[counter+1] & 0xFF) << 16) + ((ba[counter+2] & 0xFF) << 8) + (ba[counter+3] & 0xFF);
	//			counter+=4;
	//			intCounter+=1;
	//		}
	//		return ia;
	//	}

	//	public static int[] toIntArray(byte buf[]) {
	//		   int intArr[] = new int[buf.length / 4];
	//		   int offset = 0;
	//		   for(int i = 0; i < intArr.length; i++) {
	//		      intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
	//		                  ((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);  
	//		   offset += 4;
	//		   }
	//		   return intArr;
	//		}

	//	public static byte[] toByteArray(long[] value){
	//		byte[] b = new byte[value.length*8];
	//		int counter = 0;
	//		for(long v : value){
	//			b[counter] = (byte)(v >>> 56);
	//			b[counter+1] = (byte)(v >>> 48);
	//			b[counter+2] = (byte)(v >>> 40);
	//			b[counter+3] = (byte)(v >>> 32);
	//			b[counter+4] = (byte)(v >>> 24);
	//			b[counter+5] = (byte)(v >>> 16);
	//			b[counter+6] = (byte)(v >>> 8);
	//			b[counter+7] = (byte)v;
	//			counter+=8;
	//		}
	//		return b;
	//	}

	//	public static long[] toLongArray(byte[] ba){
	//		if(ba == null) return new long[0];
	//		long[] la = new long[ba.length/8];
	//		int counter = 0;
	//		int longCounter = 0;
	//		while(counter < ba.length){
	//			la[longCounter] = readLong(counter,ba);
	//			counter+=8;
	//			longCounter+=1;
	//		}
	//		return la;
	//	}

	public static int byteArrayToInt(byte [] b, int startIdx) {
		return (b[startIdx] << 24) + ((b[startIdx+1] & 0xFF) << 16) + ((b[startIdx+2] & 0xFF) << 8) + (b[startIdx+3] & 0xFF);
	}

	public static byte[] toByteArray(long value) {
		return new byte[] {
				(byte)(value >>> 56),
				(byte)(value >>> 48),
				(byte)(value >>> 40),
				(byte)(value >>> 32),
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	public static long byteArrayToLong(byte[] b) {
		return byteArrayToLong(b,0);
	}

	public static long byteArrayToLong(byte[] b, int off){
		return ((b[off + 7] & 0xFFL) << 0) +
				((b[off + 6] & 0xFFL) << 8) +
				((b[off + 5] & 0xFFL) << 16) +
				((b[off + 4] & 0xFFL) << 24) +
				((b[off + 3] & 0xFFL) << 32) +
				((b[off + 2] & 0xFFL) << 40) +
				((b[off + 1] & 0xFFL) << 48) +
				(((long) b[off + 0]) << 56);
	}



	public static long readLong(int idx, byte[] ary) {	//TODO check long/int/byte ary conversion works properly
		return ((ary[idx+7] & 0xFFL) << 0) +
				((ary[idx+6] & 0xFFL) << 8) +
				((ary[idx+5] & 0xFFL) << 16) +
				((ary[idx+4] & 0xFFL) << 24) +
				((ary[idx+3] & 0xFFL) << 32) +
				((ary[idx+2] & 0xFFL) << 40) +
				((ary[idx+1] & 0xFFL) << 48) +
				(((long) ary[idx]) << 56);
	}

	public static byte[] addInt(byte[] ary, int value) {
		byte[] newArray = Arrays.copyOf(ary, ary.length+4);
		newArray[newArray.length-4] = (byte)(value >>> 24);
		newArray[newArray.length-3] = (byte)(value >>> 16);
		newArray[newArray.length-2] = (byte)(value >>> 8);
		newArray[newArray.length-1] = (byte)value;
		return newArray;
	}

	public static byte[] addLong(byte[] ary, long value) {
		byte[] newArray = Arrays.copyOf(ary, ary.length+8);
		newArray[newArray.length-8] = (byte)(value >>> 56);
		newArray[newArray.length-7] = (byte)(value >>> 48);
		newArray[newArray.length-6] = (byte)(value >>> 40);
		newArray[newArray.length-5] =  (byte)(value >>> 32);
		newArray[newArray.length-4] = (byte)(value >>> 24);
		newArray[newArray.length-3] = (byte)(value >>> 16);
		newArray[newArray.length-2] = (byte)(value >>> 8);
		newArray[newArray.length-1] = (byte)value;
		return newArray;
	}

	public static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}

	public static boolean isAphaNumeric(char c){
		return isLetter(c) ||  isNumber(c);
	}


	public static boolean isNumber(String str) {
		char c;
		for(int i=0; i<str.length(); i++) {
			if(isNumber(c=str.charAt(i)) || c=='.') continue;
			return false;
		}
		return true;
	}


	public static String dump(Object o) {
		StringBuffer buffer = new StringBuffer();
		Class oClass = o.getClass();
		if (oClass.isArray()) {
			buffer.append("[");
			for (int i = 0; i < Array.getLength(o); i++) {
				Object value = Array.get(o, i);
				if (value.getClass().isPrimitive() ||
						value.getClass() == java.lang.Long.class ||
						value.getClass() == java.lang.Integer.class ||
						value.getClass() == java.lang.Boolean.class ||
						value.getClass() == java.lang.String.class ||
						value.getClass() == java.lang.Short.class ||
						value.getClass() == java.lang.Byte.class
						) {
					buffer.append(value);
					if(i != (Array.getLength(o)-1)) buffer.append(",");
				} else {
					buffer.append(dump(value));
				}
			}
			buffer.append("]\n");
		}  if (oClass.isPrimitive() ||
				oClass == java.lang.Long.class ||
				oClass== java.lang.Integer.class ||
				oClass == java.lang.Boolean.class ||
				oClass == java.lang.String.class ||
				oClass == java.lang.Short.class ||
				oClass == java.lang.Byte.class
				) {
			buffer.append(o.toString());
		}else {
			buffer.append("Class: " + oClass.getName());
			buffer.append("{\n");
			while (oClass != null) {
				Field[] fields = oClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					buffer.append(fields[i].getName());
					buffer.append("=");
					try {
						Object value = fields[i].get(o);
						if (value != null) {
							if (value.getClass().isPrimitive() ||
									value.getClass() == java.lang.Long.class ||
									value.getClass() == java.lang.String.class ||
									value.getClass() == java.lang.Integer.class ||
									value.getClass() == java.lang.Boolean.class ||
									value.getClass() == java.lang.Short.class ||
									value.getClass() == java.lang.Byte.class
									) {
								buffer.append(value);
							} else {
								buffer.append(dump(value));
							}
						}
					} catch (IllegalAccessException e) {
						buffer.append(e.getMessage());
					}
					buffer.append("\n");
				}
				oClass = oClass.getSuperclass();
			}
			buffer.append("}\n");
		}
		return buffer.toString();
	}

	public static String stripPunctuation(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == 32 || (s.charAt(i) >= 65 && s.charAt(i) <= 90) || (s.charAt(i) >= 97 && s.charAt(i) <= 122) || (s.charAt(i) >= 48 && s.charAt(i) <=57))  sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public static <T> List<List<T>> partition(List<T> list, int size) {  

		if (list == null)  
			throw new NullPointerException(  
					"'list' must not be null");  
		if (!(size > 0))  
			throw new IllegalArgumentException(  
					"'size' must be greater than 0");  

		return new Partition<T>(list, size);  
	}  

	private static class Partition<T> extends AbstractList<List<T>> {  

		final List<T> list;  
		final int size;  

		Partition(List<T> list, int size) {  
			this.list = list;  
			this.size = size;  
		}  

		@Override  
		public List<T> get(int index) {  
			int listSize = size();  
			if (index < 0)  
				throw new IndexOutOfBoundsException(  
						"index " + index + " must not be negative");  
			if (index >= listSize)  
				throw new IndexOutOfBoundsException(  
						"index " + index + " must be less than size " + listSize);  
			int start = index * size;  
			int end = Math.min(start + size, list.size());  
			return list.subList(start, end);  
		}  

		@Override  
		public int size() {  
			return (list.size() + size - 1) / size;  
		}  

		@Override  
		public boolean isEmpty() {  
			return list.isEmpty();  
		}  
	}

	/** 
	 * Generates 32 bit hash from byte array of the given length and
	 * seed.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @param seed initial seed value
	 * @return 32 bit hash of the given array
	 */
	public static int hash32(final byte[] data, int length, int seed) {
		// 'm' and 'r' are mixing constants generated offline.
		// They're not really 'magic', they just happen to work well.
		final int m = 0x5bd1e995;
		final int r = 24;

		// Initialize the hash to a random value
		int h = seed^length;
		int length4 = length/4;

		for (int i=0; i<length4; i++) {
			final int i4 = i*4;
			int k = (data[i4+0]&0xff) +((data[i4+1]&0xff)<<8)
					+((data[i4+2]&0xff)<<16) +((data[i4+3]&0xff)<<24);
			k *= m;
			k ^= k >>> r;
					k *= m;
					h *= m;
					h ^= k;
		}

		// Handle the last few bytes of the input array
		switch (length%4) {
		case 3: h ^= (data[(length&~3) +2]&0xff) << 16;
		case 2: h ^= (data[(length&~3) +1]&0xff) << 8;
		case 1: h ^= (data[length&~3]&0xff);
		h *= m;
		}

		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;

					return h;
	}

	public static int hash(byte[] ary) {
		if(ary.length == 0 ) return 1;
		return longHash(hash(ary, 0, ary.length, -1640531527));
	}

	public static long longHash(byte[] ary) {
		if(ary.length == 0 ) return 1;
		return hash(ary, 0, ary.length, -1640531527);
	}

	public static int hash(char[] ary) {
		if(ary.length == 0 ) return 1;
		return longHash(hash(ary, 0, ary.length, -1640531527));
	}

	public static int hash(String ary) {
		if(ary.length() == 0 ) return 1;
		return longHash(hash(ary, 0, ary.length(), -1640531527));
	}

	private static int longHash(long h) {
		//$DELAY$
		h = h * -7046029254386353131L;
		h ^= h >> 32;
			return (int)(h ^ h >> 16);
	}

	public static long hash(long input) {
		input = Long.reverseBytes(input);
		long hash = seed + PRIME64_5 + 8;
		input *= PRIME64_2;
		input = Long.rotateLeft(input, 31);
		input *= PRIME64_1;
		hash ^= input;
		hash = Long.rotateLeft(hash, 27) * PRIME64_1 + PRIME64_4;
		hash ^= hash >>> 33;
		hash *= PRIME64_2;
		hash ^= hash >>> 29;
		hash *= PRIME64_3;
		hash ^= hash >>> 32;
		return hash;
	}

	public static long hash(String buf, int off, int len, long seed) {
		if (len < 0) {
			throw new IllegalArgumentException("lengths must be >= 0");
		}
		if(off<0 || off>buf.length() || off+len<0 || off+len>buf.length()){
			throw new IndexOutOfBoundsException();
		}

		final int end = off + len;
		long h64;

		if (len >= 16) {
			final int limit = end - 16;
			long v1 = seed + PRIME64_1 + PRIME64_2;
			long v2 = seed + PRIME64_2;
			long v3 = seed + 0;
			long v4 = seed - PRIME64_1;
			do {
				v1 += readLongLE(buf, off) * PRIME64_2;
				v1 = Long.rotateLeft(v1, 31);
				v1 *= PRIME64_1;
				off += 4;

				v2 += readLongLE(buf, off) * PRIME64_2;
				v2 = Long.rotateLeft(v2, 31);
				v2 *= PRIME64_1;
				off += 4;

				v3 += readLongLE(buf, off) * PRIME64_2;
				v3 = Long.rotateLeft(v3, 31);
				v3 *= PRIME64_1;
				off += 4;

				v4 += readLongLE(buf, off) * PRIME64_2;
				v4 = Long.rotateLeft(v4, 31);
				v4 *= PRIME64_1;
				off += 4;
			} while (off <= limit);

			h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);

			v1 *= PRIME64_2; v1 = Long.rotateLeft(v1, 31); v1 *= PRIME64_1; h64 ^= v1;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v2 *= PRIME64_2; v2 = Long.rotateLeft(v2, 31); v2 *= PRIME64_1; h64 ^= v2;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v3 *= PRIME64_2; v3 = Long.rotateLeft(v3, 31); v3 *= PRIME64_1; h64 ^= v3;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v4 *= PRIME64_2; v4 = Long.rotateLeft(v4, 31); v4 *= PRIME64_1; h64 ^= v4;
			h64 = h64 * PRIME64_1 + PRIME64_4;
		} else {
			h64 = seed + PRIME64_5;
		}

		h64 += len;

		while (off <= end - 4) {
			long k1 = readLongLE(buf, off);
			k1 *= PRIME64_2; k1 = Long.rotateLeft(k1, 31); k1 *= PRIME64_1; h64 ^= k1;
			h64 = Long.rotateLeft(h64, 27) * PRIME64_1 + PRIME64_4;
			off += 4;
		}

		if (off <= end - 2) {
			h64 ^= (readIntLE(buf, off) & 0xFFFFFFFFL) * PRIME64_1;
			h64 = Long.rotateLeft(h64, 23) * PRIME64_2 + PRIME64_3;
			off += 2;
		}

		while (off < end) {
			h64 ^= (readCharLE(buf,off) & 0xFFFF) * PRIME64_5;
			h64 = Long.rotateLeft(h64, 11) * PRIME64_1;
			++off;
		}

		h64 ^= h64 >>> 33;
		h64 *= PRIME64_2;
		h64 ^= h64 >>> 29;
		h64 *= PRIME64_3;
		h64 ^= h64 >>> 32;

		return h64;
	}



	public static long xxhash(int[] buf) {//TODO recheck
		//	        if (len < 0) {
		//	            throw new IllegalArgumentException("lengths must be >= 0");
		//	        }
		//	        if(off<0 || off>=buf.length || off+len<0 || off+len>buf.length){
		//	            throw new IndexOutOfBoundsException();
		//	        }
		final int len = buf.length;
		if(len == 0) return 0; 
		long h64;
		int off = 0;
		if (len >= 8) {
			final int limit = len - 8;
			long v1 = seed + PRIME64_1 + PRIME64_2;
			long v2 = seed + PRIME64_2;
			long v3 = seed + 0;
			long v4 = seed - PRIME64_1;
			do {
				v1 += readLongLE(buf, off) * PRIME64_2;
				v1 = Long.rotateLeft(v1, 31);
				v1 *= PRIME64_1;
				off += 2;

				v2 += readLongLE(buf, off) * PRIME64_2;
				v2 = Long.rotateLeft(v2, 31);
				v2 *= PRIME64_1;
				off += 2;

				v3 += readLongLE(buf, off) * PRIME64_2;
				v3 = Long.rotateLeft(v3, 31);
				v3 *= PRIME64_1;
				off += 2;

				v4 += readLongLE(buf, off) * PRIME64_2;
				v4 = Long.rotateLeft(v4, 31);
				v4 *= PRIME64_1;
				off += 2;
			} while (off <= limit);

			h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);

			v1 *= PRIME64_2; v1 = Long.rotateLeft(v1, 31); v1 *= PRIME64_1; h64 ^= v1;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v2 *= PRIME64_2; v2 = Long.rotateLeft(v2, 31); v2 *= PRIME64_1; h64 ^= v2;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v3 *= PRIME64_2; v3 = Long.rotateLeft(v3, 31); v3 *= PRIME64_1; h64 ^= v3;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v4 *= PRIME64_2; v4 = Long.rotateLeft(v4, 31); v4 *= PRIME64_1; h64 ^= v4;
			h64 = h64 * PRIME64_1 + PRIME64_4;
		} else {
			h64 = seed + PRIME64_5;
		}

		h64 += len;

		while (off <= len - 4) {
			long k1 = readLongLE(buf, off);
			k1 *= PRIME64_2; k1 = Long.rotateLeft(k1, 31); k1 *= PRIME64_1; h64 ^= k1;
			h64 = Long.rotateLeft(h64, 27) * PRIME64_1 + PRIME64_4;
			off += 2;
		}

		if (off <= len - 2) {
			h64 ^= readLongLE(buf, off) * PRIME64_1;
			h64 = Long.rotateLeft(h64, 23) * PRIME64_2 + PRIME64_3;
			off += 2;
		}

		while (off < len) {
			h64 ^= buf[off] * PRIME64_5;
			h64 = Long.rotateLeft(h64, 11) * PRIME64_1;
			++off;
		}

		h64 ^= h64 >>> 33;
		h64 *= PRIME64_2;
		h64 ^= h64 >>> 29;
		h64 *= PRIME64_3;
		h64 ^= h64 >>> 32;

		return h64;
	}

	/**
	 * <p>
	 * Calculates XXHash64 from given {@code byte[]} buffer.
	 * </p><p>
	 * This code comes from <a href="https://github.com/jpountz/lz4-java">LZ4-Java</a> created
	 * by Adrien Grand.
	 * </p>
	 *
	 * @param buf to calculate hash from
	 * @param off offset to start calculation from
	 * @param len length of data to calculate hash
	 * @param seed  hash seed
	 * @return XXHash.
	 */
	public static long hash(byte[] buf, int off, int len, long seed) {
		if (len < 0) {
			throw new IllegalArgumentException("lengths must be >= 0");
		}
		if(off<0 || off>=buf.length || off+len<0 || off+len>buf.length){
			throw new IndexOutOfBoundsException();
		}

		final int end = off + len;
		long h64;

		if (len >= 32) {
			final int limit = end - 32;
			long v1 = seed + PRIME64_1 + PRIME64_2;
			long v2 = seed + PRIME64_2;
			long v3 = seed + 0;
			long v4 = seed - PRIME64_1;
			do {
				v1 += readLongLE(buf, off) * PRIME64_2;
				v1 = Long.rotateLeft(v1, 31);
				v1 *= PRIME64_1;
				off += 8;

				v2 += readLongLE(buf, off) * PRIME64_2;
				v2 = Long.rotateLeft(v2, 31);
				v2 *= PRIME64_1;
				off += 8;

				v3 += readLongLE(buf, off) * PRIME64_2;
				v3 = Long.rotateLeft(v3, 31);
				v3 *= PRIME64_1;
				off += 8;

				v4 += readLongLE(buf, off) * PRIME64_2;
				v4 = Long.rotateLeft(v4, 31);
				v4 *= PRIME64_1;
				off += 8;
			} while (off <= limit);

			h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);

			v1 *= PRIME64_2; v1 = Long.rotateLeft(v1, 31); v1 *= PRIME64_1; h64 ^= v1;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v2 *= PRIME64_2; v2 = Long.rotateLeft(v2, 31); v2 *= PRIME64_1; h64 ^= v2;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v3 *= PRIME64_2; v3 = Long.rotateLeft(v3, 31); v3 *= PRIME64_1; h64 ^= v3;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v4 *= PRIME64_2; v4 = Long.rotateLeft(v4, 31); v4 *= PRIME64_1; h64 ^= v4;
			h64 = h64 * PRIME64_1 + PRIME64_4;
		} else {
			h64 = seed + PRIME64_5;
		}

		h64 += len;

		while (off <= end - 8) {
			long k1 = readLongLE(buf, off);
			k1 *= PRIME64_2; k1 = Long.rotateLeft(k1, 31); k1 *= PRIME64_1; h64 ^= k1;
			h64 = Long.rotateLeft(h64, 27) * PRIME64_1 + PRIME64_4;
			off += 8;
		}

		if (off <= end - 4) {
			h64 ^= (readIntLE(buf, off) & 0xFFFFFFFFL) * PRIME64_1;
			h64 = Long.rotateLeft(h64, 23) * PRIME64_2 + PRIME64_3;
			off += 4;
		}

		while (off < end) {
			h64 ^= (buf[off] & 0xFF) * PRIME64_5;
			h64 = Long.rotateLeft(h64, 11) * PRIME64_1;
			++off;
		}

		h64 ^= h64 >>> 33;
		h64 *= PRIME64_2;
		h64 ^= h64 >>> 29;
		h64 *= PRIME64_3;
		h64 ^= h64 >>> 32;

		return h64;
	}

	/**
	 * <p>
	 * Calculates XXHash64 from given {@code char[]} buffer.
	 * </p><p>
	 * This code comes from <a href="https://github.com/jpountz/lz4-java">LZ4-Java</a> created
	 * by Adrien Grand.
	 * </p>
	 *
	 * @param buf to calculate hash from
	 * @param off offset to start calculation from
	 * @param len length of data to calculate hash
	 * @param seed  hash seed
	 * @return XXHash.
	 */
	public static long hash(char[] buf, int off, int len, long seed) {
		if (len < 0) {
			throw new IllegalArgumentException("lengths must be >= 0");
		}
		if(off<0 || off>buf.length || off+len<0 || off+len>buf.length){
			throw new IndexOutOfBoundsException();
		}

		final int end = off + len;
		long h64;

		if (len >= 16) {
			final int limit = end - 16;
			long v1 = seed + PRIME64_1 + PRIME64_2;
			long v2 = seed + PRIME64_2;
			long v3 = seed + 0;
			long v4 = seed - PRIME64_1;
			do {
				v1 += readLongLE(buf, off) * PRIME64_2;
				v1 = Long.rotateLeft(v1, 31);
				v1 *= PRIME64_1;
				off += 4;

				v2 += readLongLE(buf, off) * PRIME64_2;
				v2 = Long.rotateLeft(v2, 31);
				v2 *= PRIME64_1;
				off += 4;

				v3 += readLongLE(buf, off) * PRIME64_2;
				v3 = Long.rotateLeft(v3, 31);
				v3 *= PRIME64_1;
				off += 4;

				v4 += readLongLE(buf, off) * PRIME64_2;
				v4 = Long.rotateLeft(v4, 31);
				v4 *= PRIME64_1;
				off += 4;
			} while (off <= limit);

			h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);

			v1 *= PRIME64_2; v1 = Long.rotateLeft(v1, 31); v1 *= PRIME64_1; h64 ^= v1;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v2 *= PRIME64_2; v2 = Long.rotateLeft(v2, 31); v2 *= PRIME64_1; h64 ^= v2;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v3 *= PRIME64_2; v3 = Long.rotateLeft(v3, 31); v3 *= PRIME64_1; h64 ^= v3;
			h64 = h64 * PRIME64_1 + PRIME64_4;

			v4 *= PRIME64_2; v4 = Long.rotateLeft(v4, 31); v4 *= PRIME64_1; h64 ^= v4;
			h64 = h64 * PRIME64_1 + PRIME64_4;
		} else {
			h64 = seed + PRIME64_5;
		}

		h64 += len;

		while (off <= end - 4) {
			long k1 = readLongLE(buf, off);
			k1 *= PRIME64_2; k1 = Long.rotateLeft(k1, 31); k1 *= PRIME64_1; h64 ^= k1;
			h64 = Long.rotateLeft(h64, 27) * PRIME64_1 + PRIME64_4;
			off += 4;
		}

		if (off <= end - 2) {
			h64 ^= (readIntLE(buf, off) & 0xFFFFFFFFL) * PRIME64_1;
			h64 = Long.rotateLeft(h64, 23) * PRIME64_2 + PRIME64_3;
			off += 2;
		}

		while (off < end) {
			h64 ^= (readCharLE(buf,off) & 0xFFFF) * PRIME64_5;
			h64 = Long.rotateLeft(h64, 11) * PRIME64_1;
			++off;
		}

		h64 ^= h64 >>> 33;
		h64 *= PRIME64_2;
		h64 ^= h64 >>> 29;
		h64 *= PRIME64_3;
		h64 ^= h64 >>> 32;

		return h64;
	}

	static long readLongLE(int[] buf, int i) {
		return (((long)buf[i]) << 32) | (buf[i+1] & 0xffffffffL);
	}

	static long readLongLE(byte[] buf, int i) {
		return (buf[i] & 0xFFL) | ((buf[i+1] & 0xFFL) << 8) | ((buf[i+2] & 0xFFL) << 16) | ((buf[i+3] & 0xFFL) << 24)
				| ((buf[i+4] & 0xFFL) << 32) | ((buf[i+5] & 0xFFL) << 40) | ((buf[i+6] & 0xFFL) << 48) | ((buf[i+7] & 0xFFL) << 56);
	}

	static int readIntLE(byte[] buf, int i) {
		return (buf[i] & 0xFF) | ((buf[i+1] & 0xFF) << 8) | ((buf[i+2] & 0xFF) << 16) | ((buf[i+3] & 0xFF) << 24);
	}

	private static int readCharLE(char[] buf, int i) {
		return buf[i];
	}

	private static int readCharLE(String buf, int i) {
		return buf.charAt(i);
	}

	static long readLongLE(char[] buf, int i) {
		return (buf[i] & 0xFFFFL) |
				((buf[i+1] & 0xFFFFL) << 16) |
				((buf[i+2] & 0xFFFFL) << 32) |
				((buf[i+3] & 0xFFFFL) << 48);

	}

	static long readLongLE(String buf, int i) {
		return (buf.charAt(i) & 0xFFFFL) |
				((buf.charAt(i+1) & 0xFFFFL) << 16) |
				((buf.charAt(i+2) & 0xFFFFL) << 32) |
				((buf.charAt(i+3) & 0xFFFFL) << 48);
	}


	static int readIntLE(char[] buf, int i) {
		return (buf[i] & 0xFFFF) |
				((buf[i+1] & 0xFFFF) << 16);
	}

	static int readIntLE(String buf, int i) {
		return (buf.charAt(i) & 0xFFFF) |
				((buf.charAt(i+1) & 0xFFFF) << 16);
	}

	public static int hash(long[] data) {
		int seed = -1640531527;
		int elementHash;
		for (int i=0; i<data.length;i++) {
			elementHash = (int)(data[i] ^ (data[i] >>> 32));
			seed = (-1640531527) * seed + elementHash;
		}
		return seed;
	}

	public static int hash(int h) {
		h += (h <<  15) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h <<   3);
		h ^= (h >>>  6);
		h += (h <<   2) + (h << 14);
		return h ^ (h >>> 16);
	}

	public static int hash(int[] data) {
		int seed  =-1640531527;
		for (int i=0; i<data.length; i++) seed = (-1640531527) * seed + data[i];
		return seed;
	}

	public static int hash(short[] data) {
		int seed  =-1640531527;
		for (int i=0; i<data.length; i++) seed = (-1640531527) * seed + data[i];
		return seed;
	}

	/** 
	 * Generates 32 bit hash from byte array with default seed value.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @return 32 bit hash of the given array
	 */
	public static int hash32(final byte[] data) {
		return hash32( data, data.length, 0x9747b28c); 
	}


	/** 
	 * Generates 32 bit hash from a string.
	 * 
	 * @param text string to hash
	 * @return 32 bit hash of the given string
	 */
	public static int hash32(final String text) {
		final byte[] bytes = text.getBytes(); 
		return hash32( bytes);
	}


	/** 
	 * Generates 32 bit hash from a substring.
	 * 
	 * @param text string to hash
	 * @param from starting index
	 * @param length length of the substring to hash
	 * @return 32 bit hash of the given string
	 */
	public static int hash32(final String text, int from, int length) {
		return hash32( text.substring( from, from+length));
	}


	/** 
	 * Generates 64 bit hash from byte array of the given length and seed.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @param seed initial seed value
	 * @return 64 bit hash of the given array
	 */
	public static long hash64(final byte[] data, int length, int seed) {
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;
		long h = (seed&0xffffffffl)^(length*m);
		int length8 = length/8;
		for (int i=0; i<length8; i++) {
			final int i8 = i*8;
			long k =  ((long)data[i8+0]&0xff)      +(((long)data[i8+1]&0xff)<<8)
					+(((long)data[i8+2]&0xff)<<16) +(((long)data[i8+3]&0xff)<<24)
					+(((long)data[i8+4]&0xff)<<32) +(((long)data[i8+5]&0xff)<<40)
					+(((long)data[i8+6]&0xff)<<48) +(((long)data[i8+7]&0xff)<<56);

			k *= m;
			k ^= k >>> r;
					k *= m;

					h ^= k;
					h *= m; 
		}

		switch (length%8) {
		case 7: h ^= (long)(data[(length&~7)+6]&0xff) << 48;
		case 6: h ^= (long)(data[(length&~7)+5]&0xff) << 40;
		case 5: h ^= (long)(data[(length&~7)+4]&0xff) << 32;
		case 4: h ^= (long)(data[(length&~7)+3]&0xff) << 24;
		case 3: h ^= (long)(data[(length&~7)+2]&0xff) << 16;
		case 2: h ^= (long)(data[(length&~7)+1]&0xff) << 8;
		case 1: h ^= (data[length&~7]&0xff);
		h *= m;
		};
		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;
		return h;
	}


	/** 
	 * Generates 64 bit hash from byte array with default seed value.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @return 64 bit hash of the given string
	 */
	public static long hash64(final byte[] data) {
		return hash64( data, data.length, 0xe17a1465);
	}


	/** 
	 * Generates 64 bit hash from a string.
	 * 
	 * @param text string to hash
	 * @return 64 bit hash of the given string
	 */
	public static long hash64(final String text) {
		final byte[] bytes = text.getBytes(); 
		return hash64( bytes);
	}


	/** 
	 * Generates 64 bit hash from a substring.
	 * 
	 * @param text string to hash
	 * @param from starting index
	 * @param length length of the substring to hash
	 * @return 64 bit hash of the given array
	 */
	public static long hash64(final String text, int from, int length) {
		return hash64( text.substring( from, from+length));
	}


	public static int murmurhash3_x86_32(byte[] data) {
		final int seed = 0xe17a1465;
		final int len = data.length;
		final int offset = 0;
		final int c1 = 0xcc9e2d51;
		final int c2 = 0x1b873593;

		int h1 = seed;
		int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

		for (int i=offset; i<roundedEnd; i+=4) {
			// little endian load order
			int k1 = (data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | (data[i+3] << 24);
			k1 *= c1;
			k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
			k1 *= c2;

			h1 ^= k1;
			h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
			h1 = h1*5+0xe6546b64;
		}

		// tail
		int k1 = 0;

		switch(len & 0x03) {
		case 3:
			k1 = (data[roundedEnd + 2] & 0xff) << 16;
			// fallthrough
		case 2:
			k1 |= (data[roundedEnd + 1] & 0xff) << 8;
			// fallthrough
		case 1:
			k1 |= (data[roundedEnd] & 0xff);
			k1 *= c1;
			k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
			k1 *= c2;
			h1 ^= k1;
		}

		// finalization
		h1 ^= len;

		// fmix(h1);
		h1 ^= h1 >>> 16;
			h1 *= 0x85ebca6b;
			h1 ^= h1 >>> 13;
			h1 *= 0xc2b2ae35;
			h1 ^= h1 >>> 16;

		return h1;
	}

	public static boolean byteAryEquals(byte[] b1,byte[] b2)
	{   
		if(b1 == b2)
		{
			return true;
		}
		if(b1.length != b2.length)
		{
			return false;
		}
		final int numLongs = (int)Math.ceil(b1.length / 8.0);
		long currentOffset;
		long l1;
		long l2;
		for(int i = 0;i < numLongs; ++i)
		{
			currentOffset = byteAryOffset + (i * 8);
			l1 = unsafe.getLong(b1, currentOffset);
			l2 = unsafe.getLong(b2, currentOffset);
			if(0L != (l1 ^ l2))
			{
				return false;
			}
		}
		return true;    
	}

	public static int difference(int x1, int x2) {
		if(x1 > x2) return x1-x2; else return x2-x1;
	}


}
