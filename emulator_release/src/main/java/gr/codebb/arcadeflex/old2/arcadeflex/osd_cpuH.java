/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.codebb.arcadeflex.old2.arcadeflex;

/** @author shadow-laptop */
public class osd_cpuH {

  /*TODO*/
  /// *******************************************************************************
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *	Define size independent data types and operations.						   *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *   The following types must be supported by all platforms:					   *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *	UINT8  - Unsigned 8-bit Integer		INT8  - Signed 8-bit integer           *
  /*TODO*/
  // *	UINT16 - Unsigned 16-bit Integer	INT16 - Signed 16-bit integer          *
  /*TODO*/
  // *	UINT32 - Unsigned 32-bit Integer	INT32 - Signed 32-bit integer          *
  /*TODO*/
  // *	UINT64 - Unsigned 64-bit Integer	INT64 - Signed 64-bit integer          *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *   The macro names for the artithmatic operations are composed as follows:    *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *   XXX_R_A_B, where XXX - 3 letter operation code (ADD, SUB, etc.)			   *
  /*TODO*/
  // *					 R   - The type	of the result							   *
  /*TODO*/
  // *					 A   - The type of operand 1							   *
  /*TODO*/
  // *			         B   - The type of operand 2 (if binary operation)		   *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *				     Each type is one of: U8,8,U16,16,U32,32,U64,64			   *
  /*TODO*/
  // *																			   *
  /*TODO*/
  // *******************************************************************************/
  /*TODO*/
  //
  /*TODO*/
  //
  /*TODO*/
  // #ifndef OSD_CPU_H
  /*TODO*/
  // #define OSD_CPU_H
  /*TODO*/
  //
  /*TODO*/
  //
  /*TODO*/
  // typedef unsigned char						UINT8;
  /*TODO*/
  // typedef unsigned short						UINT16;
  /*TODO*/
  // typedef unsigned int						UINT32;
  /*TODO*/
  // __extension__ typedef unsigned long long	UINT64;
  /*TODO*/
  // typedef signed char 						INT8;
  /*TODO*/
  // typedef signed short						INT16;
  /*TODO*/
  // typedef signed int							INT32;
  /*TODO*/
  // __extension__ typedef signed long long		INT64;
  /*TODO*/
  //
  /*TODO*/
  /// * Combine two 32-bit integers into a 64-bit integer */
  /*TODO*/
  // #define COMBINE_64_32_32(A,B)     ((((UINT64)(A))<<32) | (UINT32)(B))
  /*TODO*/
  // #define COMBINE_U64_U32_U32(A,B)  COMBINE_64_32_32(A,B)
  /*TODO*/
  //
  /*TODO*/
  /// * Return upper 32 bits of a 64-bit integer */
  /*TODO*/
  // #define HI32_32_64(A)		  (((UINT64)(A)) >> 32)
  /*TODO*/
  // #define HI32_U32_U64(A)		  HI32_32_64(A)
  /*TODO*/
  //
  /*TODO*/
  /// * Return lower 32 bits of a 64-bit integer */
  /*TODO*/
  // #define LO32_32_64(A)		  ((A) & 0xffffffff)
  /*TODO*/
  // #define LO32_U32_U64(A)		  LO32_32_64(A)
  /*TODO*/
  //
  /*TODO*/
  // #define DIV_64_64_32(A,B)	  ((A)/(B))
  /*TODO*/
  // #define DIV_U64_U64_U32(A,B)  ((A)/(UINT32)(B))
  /*TODO*/
  //
  /*TODO*/
  // #define MOD_32_64_32(A,B)	  ((A)%(B))
  /*TODO*/
  // #define MOD_U32_U64_U32(A,B)  ((A)%(UINT32)(B))
  public static int MOD_U32_U64_U32(int A, int B) {
    return (int) ((A) % ((long) B) & 0xFFFFFFFFL);
  }

  /*TODO*/
  //
  /*TODO*/
  // #define MUL_64_32_32(A,B)	  ((A)*(INT64)(B))
  /*TODO*/
  // #define MUL_U64_U32_U32(A,B)  ((A)*(UINT64)(UINT32)(B))
  public static int MUL_U64_U32_U32(int A, int B) {
    return (int) ((A) * ((long) B) & 0xFFFFFFFFL);
  }
  /*TODO*/
  //
  /*TODO*/
  //
  /*TODO*/
  /// ******************************************************************************
  /*TODO*/
  // * Union of UINT8, UINT16 and UINT32 in native endianess of the target
  /*TODO*/
  // * This is used to access bytes and words in a machine independent manner.
  /*TODO*/
  // * The upper bytes h2 and h3 normally contain zero (16 bit CPU cores)
  /*TODO*/
  // * thus PAIR.d can be used to pass arguments to the memory system
  /*TODO*/
  // * which expects 'int' really.
  /*TODO*/
  // ******************************************************************************/
  /*TODO*/
  // typedef union {
  /*TODO*/
  // #ifdef LSB_FIRST
  /*TODO*/
  //	struct { UINT8 l,h,h2,h3; } b;
  /*TODO*/
  //	struct { UINT16 l,h; } w;
  /*TODO*/
  // #else
  /*TODO*/
  //	struct { UINT8 h3,h2,h,l; } b;
  /*TODO*/
  //	struct { UINT16 h,l; } w;
  /*TODO*/
  // #endif
  /*TODO*/
  //	UINT32 d;
  /*TODO*/
  // }	PAIR;
  /*TODO*/
  //
  /*TODO*/
  // #endif	/* defined OSD_CPU_H */
  /*TODO*/
  //
}
