package gr.codebb.arcadeflex.common.libc;

import gr.codebb.arcadeflex.common.PtrLib.*;

/** @author shadow */
public class cstring {
    
    /**
    *
    * const char * strstr ( const char * str1, const char * str2 ); char * strstr
    * (char * str1, const char * str2 );
    *
    * Locate substring Returns a pointer to the first occurrence of str2 in str1,
    * or a null pointer if str2 is not part of str1.
    *
    * The matching process does not include the terminating null-characters, but it
    * stops there.
    */
    public static String strstr(String str1, String str2) {
        int found = str1.indexOf(str2);
        if (found == -1)//not found
        {
            return null;
        } else {
            return str1.substring(found, str1.length());
        }
    }

  /**
   * Get string length
   *
   * @param str
   * @return
   */
  public static int strlen(String str) {
    return str.length();
  }

  /**
   * memset
   *
   * @param dst
   * @param value
   * @param size
   */
  public static void memset(char[] dst, int value, int size) {
    for (int mem = 0; mem < size; mem++) {
      dst[mem] = (char) value;
    }
  }

  public static void memset(short[] dst, int value, int size) {
    for (int mem = 0; mem < size; mem++) {
      dst[mem] = (short) value;
    }
  }

  public static void memset(int[] dst, int value, int size) {
    for (int mem = 0; mem < size; mem++) {
      dst[mem] = value;
    }
  }

  public static void memset(UBytePtr ptr, int value, int length) {
    for (int i = 0; i < length; i++) {
      ptr.write(i, value);
    }
  }

  public static void memset(UBytePtr ptr, int offset, int value, int length) {
    for (int i = 0; i < length; i++) {
      ptr.write(i + offset, value);
    }
  }

  public static void memset(ShortPtr buf, int value, int size) {
    for (int i = 0; i < size; i++) {
      buf.write(i, (short) value);
    }
  }

  /** memcpy */
  public static void memcpy(UBytePtr dst, int dstoffs, UBytePtr src, int srcoffs, int size) {
    for (int i = 0; i < Math.min(size, src.memory.length); i++) {
      dst.write(i + dstoffs, src.read(i + srcoffs));
    }
  }

  public static void memcpy(UBytePtr dst, int dstoffs, int[] src, int size) {
    for (int i = 0; i < Math.min(size, src.length); i++) {
      dst.write(i + dstoffs, src[i]);
    }
  }

  public static void memcpy(UBytePtr dst, int dstoffs, char[] src, int size) {
    for (int i = 0; i < Math.min(size, src.length); i++) {
      dst.write(i + dstoffs, src[i]);
    }
  }

  public static void memcpy(UBytePtr dst, char[] src, int size) {
    for (int i = 0; i < Math.min(size, src.length); i++) {
      dst.write(i, src[i]);
    }
  }

  public static void memcpy(UBytePtr dst, int dstoffs, UBytePtr src, int size) {
    for (int i = 0; i < Math.min(size, src.memory.length); i++) {
      dst.write(i + dstoffs, src.read(i));
    }
  }

  public static void memcpy(UBytePtr dst, UBytePtr src, int srcoffs, int size) {
    for (int i = 0; i < Math.min(size, src.memory.length); i++) {
      dst.write(i, src.read(i + srcoffs));
    }
  }

  public static void memcpy(UBytePtr dst, UBytePtr src, int size) {
    if (size == 0) return;
    for (int i = 0; i < Math.min(size, src.memory.length); i++) {
      dst.write(i, src.read(i));
    }
  }

  public static void memcpy(char[] dst, char[] src, int size) {
    for (int i = 0; i < Math.min(size, src.length); i++) {
      dst[i] = src[i];
    }
  }

  /** memcmp */
  public static int memcmp(char[] dst, char[] src, int size) {
    for (int i = 0; i < size; i++) {
      if (dst[i] != src[i]) {
        return -1;
      }
    }
    return 0;
  }

  /** STRCMP function */
  public static int strcmp(String str1, String str2) {
    return str1.compareTo(str2);
  }
  
  public static void memmove(char[] dst,UBytePtr src,int src_offset,int size)
  {
      System.arraycopy(src.memory, src.offset+src_offset, dst, 0, size);
  }
  public static void memmove(UBytePtr dst,int dst_offset,UBytePtr src,int src_offset,int size)
  {
      System.arraycopy(src.memory, src.offset+src_offset, dst.memory, dst.offset+dst_offset, size);
  }
  public static void memmove(UBytePtr dst,int dst_offset,char[] src,int size)
  {
      System.arraycopy(src, 0, dst.memory, dst.offset+dst_offset, size);
  }
}
