/*****************************************************************************
 *
 *	 z8000ops.c
 *	 Portable Z8000(2) emulator
 *	 Opcode functions
 *
 *	 Copyright (c) 1998 Juergen Buchmueller, all rights reserved.
 *	 Bug fixes and MSB_FIRST compliance Ernesto Corvi.
 *
 *	 - This source code is released as freeware for non-commercial purposes.
 *	 - You are free to use and redistribute this code in modified or
 *	   unmodified form, provided you list me in the credits.
 *	 - If you modify this source code, you must add a notice to each modified
 *	   source file that it has been changed.  If you're a nice person, you
 *	   will clearly mark each change too.  :)
 *	 - If you wish to use this for commercial purposes, please contact me at
 *	   pullmoll@t-online.de
 *	 - The author of this copywritten work reserves the right to change the
 *     terms of its usage and license at any time, including retroactively
 *   - This entire notice must remain in the source code.
 *
 *****************************************************************************/
package gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000.z8000cpuH.*;
import gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000.z8000cpuH.OpcodePtr;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class z8000ops {
    
    private z8000 _cpu = null;
    
    public z8000ops(z8000 _cpu) {
        this._cpu = _cpu;
    }
    
/*TODO*////******************************************
/*TODO*/// helper functions
/*TODO*/// ******************************************/
/*TODO*///
/*TODO*////******************************************
/*TODO*/// check new fcw for switch to system mode
/*TODO*/// and swap stack pointer if needed
/*TODO*/// ******************************************/
/*TODO*///INLINE void CHANGE_FCW(UINT16 fcw)
/*TODO*///{
/*TODO*///	if ((fcw & F_S_N) != 0) {			/* system mode now? */
/*TODO*///		if (!(FCW & F_S_N)) {	/* and not before? */
/*TODO*///			UINT16 tmp = RW(SP);
/*TODO*///			RW(SP) = NSP;
/*TODO*///			NSP = tmp;
/*TODO*///		}
/*TODO*///	} else {					/* user mode now */
/*TODO*///		if ((FCW & F_S_N) != 0) {		/* and not before? */
/*TODO*///			UINT16 tmp = RW(SP);
/*TODO*///			RW(SP) = NSP;
/*TODO*///			NSP = tmp;
/*TODO*///        }
/*TODO*///    }
/*TODO*///#if NEW_INTERRUPT_SYSTEM
/*TODO*///    if (!(FCW & F_NVIE) && (fcw & F_NVIE) && (Z.irq_state[0] != CLEAR_LINE))
/*TODO*///		IRQ_REQ |= Z8000_NVI;
/*TODO*///	if (!(FCW & F_VIE) && (fcw & F_VIE) && (Z.irq_state[1] != CLEAR_LINE))
/*TODO*///		IRQ_REQ |= Z8000_VI;
/*TODO*///#endif
/*TODO*///    FCW = fcw;  /* set new FCW */
/*TODO*///}
/*TODO*///
/*TODO*///INLINE void PUSHW(UINT8 dst, UINT16 value)
/*TODO*///{
/*TODO*///    RW(dst) -= 2;
/*TODO*///	WRMEM_W( RW(dst), value );
/*TODO*///}

    public int POPW(int src)
    {
        int result = (_cpu.RDMEM_W( _cpu._cpuH.RW(src) )) & 0xffff;
        _cpu._cpuH.RW(src, _cpu._cpuH.RW(src) + 2);
        return result;
    }

/*TODO*///INLINE void PUSHL(UINT8 dst, UINT32 value)
/*TODO*///{
/*TODO*///	RW(dst) -= 4;
/*TODO*///	WRMEM_L( RW(dst), value );
/*TODO*///}

    public int POPL(int src)
    {
        int result = _cpu.RDMEM_L( _cpu._cpuH.RW(src) );
        _cpu._cpuH.RW(src, _cpu._cpuH.RW(src) + 4);
        return result;
    }

    /* check zero and sign flag for byte, word and long results */
    public void CHK_XXXB_ZS(int result){ if (result==0) _cpu._cpuH.SET_Z(); else if (result < 0) _cpu._cpuH.SET_S(); }
    public void CHK_XXXW_ZS(int result){ if (result==0) _cpu._cpuH.SET_Z(); else if (result < 0) _cpu._cpuH.SET_S(); }
    public void CHK_XXXL_ZS(int result){ if (result==0) _cpu._cpuH.SET_Z(); else if (result < 0) _cpu._cpuH.SET_S(); }
/*TODO*///#define CHK_XXXQ_ZS if (!result) SET_Z; else if ((INT64)result < 0) SET_S

    public void CHK_XXXB_ZSP(int result){ _cpu.Z.fcw |= _cpu.z8000_zsp[result]; }

/*TODO*////* check carry for addition and subtraction */
    public void CHK_ADDX_C(int result, int dest){ if (result < dest) _cpu._cpuH.SET_C(); }
/*TODO*///#define CHK_ADCX_C if (result < dest || (result == dest && value)) SET_C

    public void CHK_SUBX_C(int result, int dest){ if (result > dest) _cpu._cpuH.SET_C(); }
/*TODO*///#define CHK_SBCX_C if (result > dest || (result == dest && value)) SET_C

/*TODO*////* check half carry for A addition and S subtraction */
    public void CHK_ADDB_H(int result, int dest){  if ((result & 15) < (dest & 15)) _cpu._cpuH.SET_H(); }
/*TODO*///#define CHK_ADCB_H	if ((result & 15) < (dest & 15) || ((result & 15) == (dest & 15) && (value & 15))) SET_H

    public void CHK_SUBB_H(int result, int dest){  if ((result & 15) > (dest & 15)) _cpu._cpuH.SET_H(); }
/*TODO*///#define CHK_SBCB_H	if ((result & 15) > (dest & 15) || ((result & 15) == (dest & 15) && (value & 15))) SET_H

    /* check overflow for addition for byte, word and long */
    public void CHK_ADDB_V(int value, int dest, int result){ if ((((value & dest & ~result) | (~value & ~dest & result)) & S08) != 0) _cpu._cpuH.SET_V(); }
    public void CHK_ADDW_V(int value, int dest, int result){ if ((((value & dest & ~result) | (~value & ~dest & result)) & S16) != 0) _cpu._cpuH.SET_V(); }
/*TODO*///#define CHK_ADDL_V if (((value & dest & ~result) | (~value & ~dest & result)) & S32) SET_V

    /* check overflow for subtraction for byte, word and long */
    public void CHK_SUBB_V(int value, int dest, int result){ if ((((~value & dest & ~result) | (value & ~dest & result)) & S08) != 0) _cpu._cpuH.SET_V(); }
    public void CHK_SUBW_V(int value, int dest, int result){ if ((((~value & dest & ~result) | (value & ~dest & result)) & S16) != 0) _cpu._cpuH.SET_V(); }
    public void CHK_SUBL_V(int value, int dest, int result){ if ((((~value & dest & ~result) | (value & ~dest & result)) & S32) != 0) _cpu._cpuH.SET_V(); }

    //static int result=0;
    /******************************************
     add byte
     flags:  CZSVDH
     ******************************************/
    public int ADDB(int dest, int value)
    {
        int result = (dest + value) & 0xff;
        _cpu._cpuH.CLR_CZSVH();      /* first clear C, Z, S, P/V and H flags    */
        _cpu._cpuH.CLR_DA();         /* clear DA (decimal adjust) flag for addb */
        CHK_XXXB_ZS(result);    /* set Z and S flags for result byte       */
        CHK_ADDX_C(result, dest); 	/* set C if result overflowed			   */
        CHK_ADDB_V(value, dest, result); 	/* set V if result has incorrect sign	   */
        CHK_ADDB_H(result, dest);     /* set H if lower nibble overflowed        */
        return result;
    }

    /******************************************
     add word
     flags:  CZSV--
     ******************************************/
    public int ADDW(int dest, int value)
    {
        int result = (dest + value) & 0xffff;
        _cpu._cpuH.CLR_CZSV();       /* first clear C, Z, S, P/V flags          */
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        CHK_ADDX_C(result, dest); 	/* set C if result overflowed			   */
        CHK_ADDW_V(value, dest, result); 	/* set V if result has incorrect sign	   */
        return result;
    }

/*TODO*////******************************************
/*TODO*/// add long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT32 ADDL(UINT32 dest, UINT32 value)
/*TODO*///{
/*TODO*///	UINT32 result = dest + value;
/*TODO*///    CLR_CZSV;       /* first clear C, Z, S, P/V flags          */
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	CHK_ADDX_C; 	/* set C if result overflowed			   */
/*TODO*///	CHK_ADDL_V; 	/* set V if result has incorrect sign	   */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// add with carry byte
/*TODO*/// flags:  CZSVDH
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 ADCB(UINT8 dest, UINT8 value)
/*TODO*///{
/*TODO*///	UINT8 result = dest + value + GET_C;
/*TODO*///    CLR_CZSVH;      /* first clear C, Z, S, P/V and H flags    */
/*TODO*///    CLR_DA;         /* clear DA (decimal adjust) flag for adcb */
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	CHK_ADCX_C; 	/* set C if result overflowed			   */
/*TODO*///	CHK_ADDB_V; 	/* set V if result has incorrect sign	   */
/*TODO*///	CHK_ADCB_H; 	/* set H if lower nibble overflowed 	   */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// add with carry word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 ADCW(UINT16 dest, UINT16 value)
/*TODO*///{
/*TODO*///	UINT16 result = dest + value + GET_C;
/*TODO*///    CLR_CZSV;       /* first clear C, Z, S, P/V flags          */
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	CHK_ADCX_C; 	/* set C if result overflowed			   */
/*TODO*///	CHK_ADDW_V; 	/* set V if result has incorrect sign	   */
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     subtract byte
     flags:  CZSVDH
     ******************************************/
    public int SUBB(int dest, int value)
    {
        int result = (dest - value) & 0xff;
        _cpu._cpuH.CLR_CZSVH();      /* first clear C, Z, S, P/V and H flags    */
        _cpu._cpuH.SET_DA();         /* set DA (decimal adjust) flag for subb   */
        CHK_XXXB_ZS(result);    /* set Z and S flags for result byte       */
        CHK_SUBX_C(result, dest); 	/* set C if result underflowed			   */
        CHK_SUBB_V(value, dest, result); 	/* set V if result has incorrect sign	   */
        CHK_SUBB_H(result, dest);     /* set H if lower nibble underflowed       */
        return result;
    }

    /******************************************
     subtract word
     flags:  CZSV--
     ******************************************/
    public int SUBW(int dest, int value)
    {
        int result = dest - value;
        _cpu._cpuH.CLR_CZSV();       /* first clear C, Z, S, P/V flags          */
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        CHK_SUBX_C(result, dest); 	/* set C if result underflowed			   */
        CHK_SUBW_V(value, dest, result); 	/* set V if result has incorrect sign	   */
        return result;
    }

    /******************************************
     subtract long
     flags:  CZSV--
     ******************************************/
    public int SUBL(int dest, int value)
    {
        int result = dest - value;
        _cpu._cpuH.CLR_CZSV();       /* first clear C, Z, S, P/V flags          */
        CHK_XXXL_ZS(result);    /* set Z and S flags for result long       */
        CHK_SUBX_C(result, dest); 	/* set C if result underflowed			   */
        CHK_SUBL_V(value, dest, result); 	/* set V if result has incorrect sign	   */
        return result;
    }

/*TODO*////******************************************
/*TODO*/// subtract with carry byte
/*TODO*/// flags:  CZSVDH
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SBCB(UINT8 dest, UINT8 value)
/*TODO*///{
/*TODO*///	UINT8 result = dest - value - GET_C;
/*TODO*///    CLR_CZSVH;      /* first clear C, Z, S, P/V and H flags    */
/*TODO*///    SET_DA;         /* set DA (decimal adjust) flag for sbcb   */
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	CHK_SBCX_C; 	/* set C if result underflowed			   */
/*TODO*///	CHK_SUBB_V; 	/* set V if result has incorrect sign	   */
/*TODO*///	CHK_SBCB_H; 	/* set H if lower nibble underflowed	   */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// subtract with carry word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 SBCW(UINT16 dest, UINT16 value)
/*TODO*///{
/*TODO*///	UINT16 result = dest - value - GET_C;
/*TODO*///    CLR_CZSV;       /* first clear C, Z, S, P/V flags          */
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	CHK_SBCX_C; 	/* set C if result underflowed			   */
/*TODO*///	CHK_SUBW_V; 	/* set V if result has incorrect sign	   */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// logical or byte
/*TODO*/// flags:  -ZSP--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 ORB(UINT8 dest, UINT8 value)
/*TODO*///{
/*TODO*///	UINT8 result = dest | value;
/*TODO*///	CLR_ZSP;		/* first clear Z, S, P/V flags			   */
/*TODO*///	CHK_XXXB_ZSP;	/* set Z, S and P flags for result byte    */
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     logical or word
     flags:  -ZS---
     ******************************************/
    public int ORW(int dest, int value)
    {
            int result = (dest | value) & 0xffff;
            _cpu._cpuH.CLR_ZS(); 		/* first clear Z, and S flags			   */
            CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
            return result;
    }

/*TODO*////******************************************
/*TODO*/// logical and byte
/*TODO*/// flags:  -ZSP--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 ANDB(UINT8 dest, UINT8 value)
/*TODO*///{
/*TODO*///	UINT8 result = dest & value;
/*TODO*///    CLR_ZSP;        /* first clear Z,S and P/V flags           */
/*TODO*///	CHK_XXXB_ZSP;	/* set Z, S and P flags for result byte    */
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     logical and word
     flags:  -ZS---
     ******************************************/
    public int ANDW(int dest, int value)
    {
        int result = dest & value;
        _cpu._cpuH.CLR_ZS();         /* first clear Z and S flags               */
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        return result;
    }

    /******************************************
     logical exclusive or byte
     flags:  -ZSP--
     ******************************************/
    public int XORB(int dest, int value)
    {
            int result = (dest ^ value) & 0xff;
            _cpu._cpuH.CLR_ZSP();        /* first clear Z, S and P/V flags          */
            CHK_XXXB_ZSP(result);	/* set Z, S and P flags for result byte    */
            return result;
    }

    /******************************************
     logical exclusive or word
     flags:  -ZS---
     ******************************************/
    public int XORW(int dest, int value)
    {
        int result = (dest ^ value) & 0xffff;
        _cpu._cpuH.CLR_ZS();         /* first clear Z and S flags               */
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        return result;
    }


    /******************************************
     compare byte
     flags:  CZSV--
     ******************************************/
    public void CPB(int dest, int value)
    {
        int result = (dest - value) & 0xff;
        _cpu._cpuH.CLR_CZSV();       /* first clear C, Z, S and P/V flags       */
        CHK_XXXB_ZS(result);    /* set Z and S flags for result byte       */
        CHK_SUBX_C(result, dest); 	/* set C if result underflowed			   */
        CHK_SUBB_V(value, dest, result);
    }

    /******************************************
     compare word
     flags:  CZSV--
     ******************************************/
    public void CPW(int dest, int value)
    {
            int result = (dest - value) & 0xffff;
            _cpu._cpuH.CLR_CZSV();
            CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
            CHK_SUBX_C(result, dest); 	/* set C if result underflowed			   */
            CHK_SUBW_V(value, dest, result);
    }

/*TODO*////******************************************
/*TODO*/// compare long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE void CPL(UINT32 dest, UINT32 value)
/*TODO*///{
/*TODO*///	UINT32 result = dest - value;
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	CHK_SUBX_C; 	/* set C if result underflowed			   */
/*TODO*///	CHK_SUBL_V;
/*TODO*///}

    /******************************************
     complement byte
     flags: -ZSP--
     ******************************************/
    public int COMB(int dest)
    {
            int result = (~dest) & 0xff;
            _cpu._cpuH.CLR_ZSP();
            CHK_XXXB_ZSP(result);	/* set Z, S and P flags for result byte    */
            return result;
    }

/*TODO*////******************************************
/*TODO*/// complement word
/*TODO*/// flags: -ZS---
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 COMW(UINT16 dest)
/*TODO*///{
/*TODO*///	UINT16 result = ~dest;
/*TODO*///	CLR_ZS;
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// negate byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 NEGB(UINT8 dest)
/*TODO*///{
/*TODO*///	UINT8 result = (UINT8) -dest;
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (result > 0) SET_C;
/*TODO*///    if (result == S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// negate word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 NEGW(UINT16 dest)
/*TODO*///{
/*TODO*///	UINT16 result = (UINT16) -dest;
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (result > 0) SET_C;
/*TODO*///    if (result == S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// test byte
/*TODO*/// flags:  -ZSP--
/*TODO*/// ******************************************/
/*TODO*///INLINE void TESTB(UINT8 result)
/*TODO*///{
/*TODO*///	CLR_ZSP;
/*TODO*///	CHK_XXXB_ZSP;	/* set Z and S flags for result byte	   */
/*TODO*///}

    /******************************************
     test word
     flags:  -ZS---
     ******************************************/
    public void TESTW(int dest)
    {
        _cpu._cpuH.CLR_ZS();
        if (dest==0) _cpu._cpuH.SET_Z(); else if ((dest & S16) != 0) _cpu._cpuH.SET_S();
    }

/*TODO*////******************************************
/*TODO*/// test long
/*TODO*/// flags:  -ZS---
/*TODO*/// ******************************************/
/*TODO*///INLINE void TESTL(UINT32 dest)
/*TODO*///{
/*TODO*///	CLR_ZS;
/*TODO*///	if (!dest) SET_Z; else if ((dest & S32) != 0) SET_S;
/*TODO*///}

    /******************************************
     increment byte
     flags: -ZSV--
     ******************************************/
    public int INCB(int dest, int value)
    {
        int result = (dest + value) & 0xff;
        _cpu._cpuH.CLR_ZSV();
        CHK_XXXB_ZS(result);    /* set Z and S flags for result byte       */
        CHK_ADDB_V(value, dest, result); 	/* set V if result overflowed			   */
        return result;
    }

    /******************************************
     increment word
     flags: -ZSV--
     ******************************************/
    public int INCW(int dest, int value)
    {
        int result = (dest + value) & 0xffff;
        _cpu._cpuH.CLR_ZSV();
        CHK_XXXW_ZS(result);    /* set Z and S flags for result byte       */
        CHK_ADDW_V(value, dest, result); 	/* set V if result overflowed			   */
        return result;
    }

/*TODO*////******************************************
/*TODO*/// decrement byte
/*TODO*/// flags: -ZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 DECB(UINT8 dest, UINT8 value)
/*TODO*///{
/*TODO*///    UINT8 result = dest - value;
/*TODO*///	CLR_ZSV;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	CHK_SUBB_V; 	/* set V if result overflowed			   */
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     decrement word
     flags: -ZSV--
     ******************************************/
    public int DECW(int dest, int value)
    {
        int result = (dest - value) & 0xffff;
        _cpu._cpuH.CLR_ZSV();
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        CHK_SUBW_V(value, dest, result); 	/* set V if result overflowed			   */
        return result;
    }

    /******************************************
     multiply words
     flags:  CZSV--
     ******************************************/
    public int MULTW(int dest, int value)
    {
            int result = (dest * value) & 0xffff;
            _cpu._cpuH.CLR_CZSV();
            CHK_XXXL_ZS(result);
            if( value==0 )
            {
                    /* multiplication with zero is faster */
                    _cpu.z8000_ICount[0] += (70-18);
            }
            if( result < -0x7fff || result >= 0x7fff ) _cpu._cpuH.SET_C();
            return result;
    }

/*TODO*////******************************************
/*TODO*/// multiply longs
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT64 MULTL(UINT32 dest, UINT32 value)
/*TODO*///{
/*TODO*///	UINT64 result = (INT64)(INT32)dest * (INT32)value;
/*TODO*///    if( !value )
/*TODO*///	{
/*TODO*///		/* multiplication with zero is faster */
/*TODO*///		z8000_ICount += (282 - 30);
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		int n;
/*TODO*///		for( n = 0; n < 32; n++ )
/*TODO*///			if( dest & (1L << n) ) z8000_ICount -= 7;
/*TODO*///    }
/*TODO*///    CLR_CZSV;
/*TODO*///	CHK_XXXQ_ZS;
/*TODO*///	if( (INT64)result < -0x7fffffffL || (INT64)result >= 0x7fffffffL ) SET_C;
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     divide long by word
     flags: CZSV--
     ******************************************/
    public int DIVW(int dest, int value)
    {
            int result = dest;
            int remainder = 0;
            _cpu._cpuH.CLR_CZSV();
            if (value != 0)
            {
                    int qsign = ((dest >> 16) ^ value) & S16;
                    int rsign = (dest >> 16) & S16;
                    if (dest < 0) dest = -dest;
                    if (value < 0) value = -value;
                    result = dest / value;
                    remainder = dest % value;
                    if (qsign != 0) result = -result;
                    if (rsign != 0) remainder = -remainder;
                    if (result < -0x8000 || result > 0x7fff)
                    {
                            int temp = result >> 1;
                            _cpu._cpuH.SET_V();
                            if (temp >= -0x8000 && temp <= 0x7fff)
                            {
                                    result = (temp < 0) ? -1 : 0;
                                    CHK_XXXW_ZS(result);
                                    _cpu._cpuH.SET_C();
                            }
                    }
                    else
                    {
                            CHK_XXXW_ZS(result);
                    }
                    result = (remainder << 16) | (result & 0xffff);
        }
        else
        {
            _cpu._cpuH.SET_Z();
            _cpu._cpuH.SET_V();
        }
            return result;
    }

/*TODO*////******************************************
/*TODO*/// divide quad word by long
/*TODO*/// flags: CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT64 DIVL(UINT64 dest, UINT32 value)
/*TODO*///{
/*TODO*///	UINT64 result = dest;
/*TODO*///	UINT32 remainder = 0;
/*TODO*///	CLR_CZSV;
/*TODO*///	if (value != 0)
/*TODO*///	{
/*TODO*///		UINT32 qsign = ((dest >> 32) ^ value) & S32;
/*TODO*///		UINT32 rsign = (dest >> 32) & S32;
/*TODO*///		if ((INT64)dest < 0) dest = -dest;
/*TODO*///		if ((INT32)value < 0) value = -value;
/*TODO*///		result = dest / value;
/*TODO*///		remainder = dest % value;
/*TODO*///		if (qsign != 0) result = -result;
/*TODO*///		if (rsign != 0) remainder = -remainder;
/*TODO*///		if ((INT64)result < -0x80000000 || (INT64)result > 0x7fffffff)
/*TODO*///		{
/*TODO*///			INT64 temp = (INT64)result >> 1;
/*TODO*///			SET_V;
/*TODO*///			if (temp >= -0x80000000 && temp <= 0x7fffffff)
/*TODO*///			{
/*TODO*///				result = (temp < 0) ? -1 : 0;
/*TODO*///				CHK_XXXL_ZS;
/*TODO*///				SET_C;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			CHK_XXXL_ZS;
/*TODO*///		}
/*TODO*///		result = ((UINT64)remainder << 32) | (result & 0xffffffff);
/*TODO*///    }
/*TODO*///    else
/*TODO*///    {
/*TODO*///		SET_Z;
/*TODO*///        SET_V;
/*TODO*///    }
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate left byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 RLB(UINT8 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT8 result = (dest << 1) | (dest >> 7);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) result = (result << 1) | (result >> 7);
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if ((result & 0x01) != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate left word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 RLW(UINT16 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT16 result = (dest << 1) | (dest >> 15);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) result = (result << 1) | (result >> 15);
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if ((result & 0x0001) != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate left through carry byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 RLCB(UINT8 dest, UINT8 twice)
/*TODO*///{
/*TODO*///    UINT8 c = dest & S08;
/*TODO*///	UINT8 result = (dest << 1) | GET_C;
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) {
/*TODO*///		UINT8 c1 = c >> 7;
/*TODO*///        c = result & S08;
/*TODO*///		result = (result << 1) | c1;
/*TODO*///	}
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate left through carry word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 RLCW(UINT16 dest, UINT8 twice)
/*TODO*///{
/*TODO*///    UINT16 c = dest & S16;
/*TODO*///	UINT16 result = (dest << 1) | GET_C;
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) {
/*TODO*///		UINT16 c1 = c >> 15;
/*TODO*///        c = result & S16;
/*TODO*///		result = (result << 1) | c1;
/*TODO*///    }
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate right byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 RRB(UINT8 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT8 result = (dest >> 1) | (dest << 7);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) result = (result >> 1) | (result << 7);
/*TODO*///    if (!result) SET_Z; else if ((result & S08) != 0) SET_SC;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate right word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 RRW(UINT16 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT16 result = (dest >> 1) | (dest << 15);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) result = (result >> 1) | (result << 15);
/*TODO*///    if (!result) SET_Z; else if ((result & S16) != 0) SET_SC;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate right through carry byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 RRCB(UINT8 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT8 c = dest & 1;
/*TODO*///	UINT8 result = (dest >> 1) | (GET_C << 7);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) {
/*TODO*///		UINT8 c1 = c << 7;
/*TODO*///		c = result & 1;
/*TODO*///		result = (result >> 1) | c1;
/*TODO*///	}
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// rotate right through carry word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 RRCW(UINT16 dest, UINT8 twice)
/*TODO*///{
/*TODO*///	UINT16 c = dest & 1;
/*TODO*///	UINT16 result = (dest >> 1) | (GET_C << 15);
/*TODO*///	CLR_CZSV;
/*TODO*///	if (twice != 0) {
/*TODO*///		UINT16 c1 = c << 15;
/*TODO*///		c = result & 1;
/*TODO*///		result = (result >> 1) | c1;
/*TODO*///    }
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic arithmetic byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SDAB(UINT8 dest, INT8 count)
/*TODO*///{
/*TODO*///	INT8 result = (INT8) dest;
/*TODO*///	UINT8 c = 0;
/*TODO*///	CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S08;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x01;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return (UINT8)result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic arithmetic word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 SDAW(UINT16 dest, INT8 count)
/*TODO*///{
/*TODO*///	INT16 result = (INT16) dest;
/*TODO*///	UINT16 c = 0;
/*TODO*///	CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S16;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x0001;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return (UINT16)result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic arithmetic long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT32 SDAL(UINT32 dest, INT8 count)
/*TODO*///{
/*TODO*///	INT32 result = (INT32) dest;
/*TODO*///	UINT32 c = 0;
/*TODO*///	CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S32;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x00000001;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S32) SET_V;
/*TODO*///	return (UINT32) result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic logic byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SDLB(UINT8 dest, INT8 count)
/*TODO*///{
/*TODO*///	UINT8 result = dest;
/*TODO*///	UINT8 c = 0;
/*TODO*///	CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S08;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x01;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic logic word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 SDLW(UINT16 dest, INT8 count)
/*TODO*///{
/*TODO*///	UINT16 result = dest;
/*TODO*///	UINT16 c = 0;
/*TODO*///    CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S16;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x0001;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift dynamic logic long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT32 SDLL(UINT32 dest, INT8 count)
/*TODO*///{
/*TODO*///	UINT32 result = dest;
/*TODO*///	UINT32 c = 0;
/*TODO*///    CLR_CZSV;
/*TODO*///	while (count > 0) {
/*TODO*///        c = result & S32;
/*TODO*///		result <<= 1;
/*TODO*///		count--;
/*TODO*///	}
/*TODO*///	while (count < 0) {
/*TODO*///		c = result & 0x00000001;
/*TODO*///		result >>= 1;
/*TODO*///		count++;
/*TODO*///	}
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S32) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift left arithmetic byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SLAB(UINT8 dest, UINT8 count)
/*TODO*///{
/*TODO*///    UINT8 c = (count) ? (dest << (count - 1)) & S08 : 0;
/*TODO*///	UINT8 result = (UINT8)((INT8)dest << count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S08) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift left arithmetic word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 SLAW(UINT16 dest, UINT8 count)
/*TODO*///{
/*TODO*///    UINT16 c = (count) ? (dest << (count - 1)) & S16 : 0;
/*TODO*///	UINT16 result = (UINT16)((INT16)dest << count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S16) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift left arithmetic long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT32 SLAL(UINT32 dest, UINT8 count)
/*TODO*///{
/*TODO*///    UINT32 c = (count) ? (dest << (count - 1)) & S32 : 0;
/*TODO*///	UINT32 result = (UINT32)((INT32)dest << count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///    if ((result ^ dest) & S32) SET_V;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift left logic byte
/*TODO*/// flags:  CZS---
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SLLB(UINT8 dest, UINT8 count)
/*TODO*///{
/*TODO*///    UINT8 c = (count) ? (dest << (count - 1)) & S08 : 0;
/*TODO*///	UINT8 result = dest << count;
/*TODO*///	CLR_CZS;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     shift left logic word
     flags:  CZS---
     ******************************************/
    public int SLLW(int dest, int count)
    {
        int c = (count!=0) ? (dest << (count - 1)) & S16 : 0;
        int result = dest << count;
        _cpu._cpuH.CLR_CZS();
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        if (c != 0) _cpu._cpuH.SET_C();
        return result;
    }

    /******************************************
     shift left logic long
     flags:  CZS---
     ******************************************/
    public int SLLL(int dest, int count)
    {
        int c = (count!=0) ? (dest << (count - 1)) & S32 : 0;
        int result = dest << count;
        _cpu._cpuH.CLR_CZS();
        CHK_XXXL_ZS(result);    /* set Z and S flags for result long       */
        if (c != 0) _cpu._cpuH.SET_C();
        return result;
    }

/*TODO*////******************************************
/*TODO*/// shift right arithmetic byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SRAB(UINT8 dest, UINT8 count)
/*TODO*///{
/*TODO*///	UINT8 c = (count) ? ((INT8)dest >> (count - 1)) & 1 : 0;
/*TODO*///	UINT8 result = (UINT8)((INT8)dest >> count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift right arithmetic word
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT16 SRAW(UINT16 dest, UINT8 count)
/*TODO*///{
/*TODO*///	UINT8 c = (count) ? ((INT16)dest >> (count - 1)) & 1 : 0;
/*TODO*///	UINT16 result = (UINT16)((INT16)dest >> count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXW_ZS;    /* set Z and S flags for result word       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift right arithmetic long
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT32 SRAL(UINT32 dest, UINT8 count)
/*TODO*///{
/*TODO*///	UINT8 c = (count) ? ((INT32)dest >> (count - 1)) & 1 : 0;
/*TODO*///	UINT32 result = (UINT32)((INT32)dest >> count);
/*TODO*///	CLR_CZSV;
/*TODO*///    CHK_XXXL_ZS;    /* set Z and S flags for result long       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*////******************************************
/*TODO*/// shift right logic byte
/*TODO*/// flags:  CZSV--
/*TODO*/// ******************************************/
/*TODO*///INLINE UINT8 SRLB(UINT8 dest, UINT8 count)
/*TODO*///{
/*TODO*///	UINT8 c = (count) ? (dest >> (count - 1)) & 1 : 0;
/*TODO*///	UINT8 result = dest >> count;
/*TODO*///	CLR_CZS;
/*TODO*///    CHK_XXXB_ZS;    /* set Z and S flags for result byte       */
/*TODO*///	if (c != 0) SET_C;
/*TODO*///	return result;
/*TODO*///}

    /******************************************
     shift right logic word
     flags:  CZSV--
     ******************************************/
    public int SRLW(int dest, int count)
    {
        int c = (count!=0) ? (dest >> (count - 1)) & 1 : 0;
        int result = (dest >> count) & 0xffff;
        _cpu._cpuH.CLR_CZS();
        CHK_XXXW_ZS(result);    /* set Z and S flags for result word       */
        if (c != 0) _cpu._cpuH.SET_C();
        return result;
    }

    /******************************************
     shift right logic long
     flags:  CZSV--
     ******************************************/
    public int SRLL(int dest, int count)
    {
            int c = (count!=0) ? (dest >> (count - 1)) & 1 : 0;
            int result = dest >> count;
            _cpu._cpuH.CLR_CZS();
            CHK_XXXL_ZS(result);    /* set Z and S flags for result long       */
            if (c != 0) _cpu._cpuH.SET_C();
            return result;
    }

    /******************************************
     invalid
     flags:  ------
     ******************************************/
    public OpcodePtr zinvalid = new OpcodePtr() {
        @Override
        public void handler() {
            logerror("Z8000 invalid opcode %04x: %04x\n", _cpu.Z.pc, _cpu.Z.op[0]);
            gr.codebb.arcadeflex.old.arcadeflex.libc_old.printf("Z8000 invalid opcode %04x: %04x\n", _cpu.Z.pc, _cpu.Z.op[0]);
            //throw new UnsupportedOperationException("unsupported");
        }
    };
    
    /******************************************
     addb	 rbd,imm8
     flags:  CZSVDH
     ******************************************/
    public OpcodePtr  Z00_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM8(OP1);
            _cpu._cpuH.RB(dst, ADDB( _cpu._cpuH.RB(dst), imm8));
        }
    };

    /******************************************
     addb	 rbd,@rs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z00_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RB(dst, ADDB( _cpu._cpuH.RB(dst), _cpu.RDMEM_B(_cpu._cpuH.RW(src)) ));
        }
     };

    /******************************************
     add	 rd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z01_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM16(OP1);
            _cpu._cpuH.RW(dst, ADDW( _cpu._cpuH.RW(dst), imm16 ));
        }
     };
    
    /******************************************
     add	 rd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z01_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, ADDW( _cpu._cpuH.RW(dst), _cpu.RDMEM_W(_cpu._cpuH.RW(src)) ));
        }
     };
    
    /******************************************
     subb	 rbd,imm8
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z02_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	RB(dst) = SUBB( RB(dst), imm8 );
        }
     };
    
    /******************************************
     subb	 rbd,@rs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z02_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RB(dst, SUBB( _cpu._cpuH.RB(dst), _cpu.RDMEM_B(_cpu._cpuH.RW(src)) )); /* EHC */
        }
     };
    
    /******************************************
     sub	 rd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z03_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	RW(dst) = SUBW( RW(dst), imm16 );
        }
     };
    
    /******************************************
     sub	 rd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z03_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, SUBW( _cpu._cpuH.RW(dst), _cpu.RDMEM_W(_cpu._cpuH.RW(src)) ));
        }
     };
    
    /******************************************
     orb	 rbd,imm8
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z04_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	RB(dst) = ORB( RB(dst), imm8 );
        }
     };
    
    /******************************************
     orb	 rbd,@rs
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z04_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ORB( RB(dst), RDMEM_B(RW(src)) );
        }
     };
    
    /******************************************
     or 	 rd,imm16
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z05_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	RW(dst) = ORW( RW(dst), imm16 );
        }
     };
    
    /******************************************
     or 	 rd,@rs
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z05_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = ORW( RW(dst), RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     andb	 rbd,imm8
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z06_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	RB(dst) = ANDB( RB(dst), imm8 );
        }
     };
    
    /******************************************
     andb	 rbd,@rs
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z06_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ANDB( RB(dst), RDMEM_B(RW(src)) );
        }
     };
    
    /******************************************
     and	 rd,imm16
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z07_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM16(OP1);
            _cpu._cpuH.RW(dst, ANDW( _cpu._cpuH.RW(dst), imm16 ));
        }
     };
    
    /******************************************
     and	 rd,@rs
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z07_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = ANDW( RW(dst), RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     xorb	 rbd,imm8
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z08_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	RB(dst) = XORB(RB(dst), imm8);
        }
     };
    
    /******************************************
     xorb	 rbd,@rs
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z08_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = XORB( RB(dst), RDMEM_B(RW(src)) );
        }
     };
    
    /******************************************
     xor	 rd,imm16
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z09_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	RW(dst) = XORW( RW(dst), imm16 );
        }
     };
    
    /******************************************
     xor	 rd,@rs
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z09_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, XORW( _cpu._cpuH.RW(dst), _cpu.RDMEM_W(_cpu._cpuH.RW(src)) ));
        }
     };
    
    /******************************************
     cpb	 rbd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0A_0000_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	CPB(RB(dst), imm8);
        }
     };
    
    /******************************************
     cpb	 rbd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0A_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            CPB( _cpu._cpuH.RB(dst), _cpu.RDMEM_B(_cpu._cpuH.RW(src)) );
        }
     };
    
    /******************************************
     cp 	 rd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0B_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM16(OP1);
            CPW( _cpu._cpuH.RW(dst), imm16 );
        }
     };
    
    /******************************************
     cp 	 rd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0B_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	CPW( RW(dst), RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     comb	 @rd
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z0C_ddN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	WRMEM_B( RW(dst), COMB(RDMEM_B(RW(dst))) );
        }
     };
    
    /******************************************
     cpb	 @rd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0C_ddN0_0001_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	CPB( RB(dst), imm8 );
        }
     };
    
    /******************************************
     negb	 @rd
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0C_ddN0_0010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B( RW(dst), NEGB(RDMEM_B(RW(dst))) );
        }
     };
    
    /******************************************
     testb	 @rd
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z0C_ddN0_0100 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	TESTB(RDMEM_B(RW(dst)));
        }
     };
    
    /******************************************
     ldb	 @rd,imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z0C_ddN0_0101_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM8(OP1);
    /*TODO*///	WRMEM_B( RW(dst), imm8 );
        }
     };
    
    /******************************************
     tsetb	 @rd
     flags:  --S---
     ******************************************/
     public OpcodePtr Z0C_ddN0_0110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///    if (RDMEM_B(RW(dst)) & S08) SET_S; else CLR_S;
    /*TODO*///    WRMEM_B(RW(dst), 0xff);
        }
     };
    
    /******************************************
     clrb	 @rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z0C_ddN0_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B( RW(dst), 0 );
        }
     };
    
    /******************************************
     com	 @rd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z0D_ddN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W( RW(dst), COMW(RDMEM_W(RW(dst))) );
        }
     };
    
    /******************************************
     cp 	 @rd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0D_ddN0_0001_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	CPW( RDMEM_W(RW(dst)), imm16 );
        }
     };
    
    /******************************************
     neg	 @rd
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z0D_ddN0_0010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W( RW(dst), NEGW(RDMEM_W(RW(dst))) );
        }
     };
    
    /******************************************
     test	 @rd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z0D_ddN0_0100 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	TESTW( RDMEM_W(RW(dst)) );
        }
     };
    
    /******************************************
     ld 	 @rd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z0D_ddN0_0101_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.GET_IMM16(OP1);
            _cpu.WRMEM_W( _cpu._cpuH.RW(dst), imm16);
        }
     };
    
    /******************************************
     tset	 @rd
     flags:  --S---
     ******************************************/
     public OpcodePtr Z0D_ddN0_0110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///    if (RDMEM_W(RW(dst)) & S16) SET_S; else CLR_S;
    /*TODO*///    WRMEM_W(RW(dst), 0xffff);
        }
     };
    
    /******************************************
     clr	 @rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z0D_ddN0_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W( RDMEM_W(RW(dst)), 0 );
        }
     };
    
    /******************************************
     push	 @rd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z0D_ddN0_1001_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	PUSHW( dst, imm16 );
        }
     };
    
    /******************************************
     ext0e	 imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z0E_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: ext0e  $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///	}
        }
     };
    
    /******************************************
     ext0f	 imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z0F_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: ext0f  $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     cpl	 rrd,imm32
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z10_0000_dddd_imm32 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM32;
    /*TODO*///	CPL( RL(dst), imm32 );
        }
     };
    
    /******************************************
     cpl	 rrd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z10_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	CPL( RL(dst), RDMEM_L(RW(src)) );
        }
     };
    
    /******************************************
     pushl	 @rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z11_ddN0_ssN0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	PUSHL( dst, RDMEM_L(RW(src)) );
        }
     };
    
    /******************************************
     subl	 rrd,imm32
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z12_0000_dddd_imm32 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM32;
    /*TODO*///	RL(dst) = SUBL( RL(dst), imm32 );
        }
     };
    
    /******************************************
     subl	 rrd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z12_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = SUBL( RL(dst), RDMEM_L(RW(src)) );
        }
     };
    
    /******************************************
     push	 @rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z13_ddN0_ssN0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	PUSHW( dst, RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     ldl	 rrd,imm32
     flags:  ------
     ******************************************/
     public OpcodePtr Z14_0000_dddd_imm32 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM32();
            _cpu._cpuH.RL(dst, imm32);
        }
     };
    
    /******************************************
     ldl	 rrd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z14_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = RDMEM_L( RW(src) );
        }
     };
    
    /******************************************
     popl	 @rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z15_ssN0_ddN0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = POPL( src );
        }
     };
    
    /******************************************
     addl	 rrd,imm32
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z16_0000_dddd_imm32 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM32;
    /*TODO*///	RL(dst) = ADDL( RL(dst), imm32 );
        }
     };
    
    /******************************************
     addl	 rrd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z16_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = ADDL( RL(dst), RDMEM_L(RW(src)) );
        }
     };
    
    /******************************************
     pop	 @rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z17_ssN0_ddN0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = POPW( src );
        }
     };
    
    /******************************************
     multl	 rqd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z18_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RQ(dst) = MULTL( RQ(dst), RL(src) );
        }
     };
    
    /******************************************
     mult	 rrd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z19_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM16(OP1);
            _cpu._cpuH.RL(dst, MULTW( _cpu._cpuH.RL(dst), imm16 ));
        }
     };
    
    /******************************************
     mult	 rrd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z19_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = MULTW( RL(dst), RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     divl	 rqd,imm32
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z1A_0000_dddd_imm32 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_IMM32;
    /*TODO*///	RQ(dst) = DIVL( RQ(dst), imm32 );
        }
     };
    
    /******************************************
     divl	 rqd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z1A_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RQ(dst) = DIVL( RQ(dst), RDMEM_L(RW(src)) );
        }
     };
    
    /******************************************
     div	 rrd,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z1B_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_IMM16(OP1);
            _cpu._cpuH.RL(dst, DIVW( _cpu._cpuH.RL(dst), imm16 ));
        }
     };
    
    /******************************************
     div	 rrd,@rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z1B_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = DIVW( RL(dst), RDMEM_W(RW(src)) );
        }
     };
    
    /******************************************
     testl	 @rd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z1C_ddN0_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	TESTL( RDMEM_L(RW(dst)) );
        }
     };
    
    /******************************************
     ldm     @rd,rs,n
     flags:  ------
     ******************************************/
     public OpcodePtr Z1C_ddN0_1001_0000_ssss_0000_nmin1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB3);
    /*TODO*///    GET_SRC(OP1,NIB1);
    /*TODO*///	UINT16 idx = RW(dst);
    /*TODO*///    while (cnt-- >= 0) {
    /*TODO*///        WRMEM_W( idx, RW(src) );
    /*TODO*///		idx = (idx + 2) & 0xffff;
    /*TODO*///		src = ++src & 15;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldm	 rd,@rs,n
     flags:  ------
     ******************************************/
     public OpcodePtr Z1C_ssN0_0001_0000_dddd_0000_nmin1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	UINT16 idx = RW(src);
    /*TODO*///	while (cnt-- >= 0) {
    /*TODO*///		RW(dst) = RDMEM_W( idx );
    /*TODO*///		idx = (idx + 2) & 0xffff;
    /*TODO*///		dst = ++dst & 15;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldl	 @rd,rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z1D_ddN0_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_L( RW(dst), RL(src) );
        }
     };
    
    /******************************************
     jp      cc,rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z1E_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_CCC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) PC = RW(dst); break;
    /*TODO*///		case  1: if (CC1 != 0) PC = RW(dst); break;
    /*TODO*///		case  2: if (CC2 != 0) PC = RW(dst); break;
    /*TODO*///		case  3: if (CC3 != 0) PC = RW(dst); break;
    /*TODO*///		case  4: if (CC4 != 0) PC = RW(dst); break;
    /*TODO*///		case  5: if (CC5 != 0) PC = RW(dst); break;
    /*TODO*///		case  6: if (CC6 != 0) PC = RW(dst); break;
    /*TODO*///		case  7: if (CC7 != 0) PC = RW(dst); break;
    /*TODO*///		case  8: if (CC8 != 0) PC = RW(dst); break;
    /*TODO*///		case  9: if (CC9 != 0) PC = RW(dst); break;
    /*TODO*///		case 10: if (CCA != 0) PC = RW(dst); break;
    /*TODO*///		case 11: if (CCB != 0) PC = RW(dst); break;
    /*TODO*///		case 12: if (CCC != 0) PC = RW(dst); break;
    /*TODO*///		case 13: if (CCD != 0) PC = RW(dst); break;
    /*TODO*///		case 14: if (CCE != 0) PC = RW(dst); break;
    /*TODO*///		case 15: if (CCF != 0) PC = RW(dst); break;
    /*TODO*///	}
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     call	 @rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z1F_ddN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	PUSHW( SP, PC );
    /*TODO*///    PC = RW(dst);
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     ldb	 rbd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z20_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RB(dst, _cpu.RDMEM_B( _cpu._cpuH.RW(src) ));
        }
     };
    
    /******************************************
     ld 	 rd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z21_0000_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
	        _cpu._cpuH.GET_DST(OP0,NIB3);
	    	_cpu._cpuH.GET_IMM16(OP1);
                _cpu._cpuH.RW(dst, imm16);
        }
     };
    
    /******************************************
     ld 	 rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z21_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = RDMEM_W( RW(src) );
        }
     };
    
    /******************************************
     resb	 rbd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z22_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	RB(dst) = RB(dst) & ~(1 << (RW(src) & 7));
        }
     };
    
    /******************************************
     resb	 @rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z22_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B(RW(dst), RDMEM_B(RW(dst)) & ~bit);
        }
     };
    
    /******************************************
     result 	rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z23_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_SRC(OP0,NIB3);
            _cpu._cpuH.GET_DST(OP1,NIB1);
            _cpu._cpuH.RW(dst, _cpu._cpuH.RW(dst) & ~(1 << (_cpu._cpuH.RW(src) & 15)));
        }
     };
    
    /******************************************
     res	 @rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z23_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W(RW(dst), RDMEM_W(RW(dst)) & ~bit);
        }
     };
    
    /******************************************
     setb	 rbd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z24_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	RB(dst) = RB(dst) | (1 << (RW(src) & 7));
        }
     };
    
    /******************************************
     setb	 @rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z24_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B(RW(dst), RDMEM_B(RW(dst)) | bit);
        }
     };
    
    /******************************************
     set	 rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z25_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	RW(dst) = RW(dst) | (1 << (RW(src) & 15));
        }
     };
    
    /******************************************
     set	 @rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z25_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W(RW(dst), RDMEM_W(RW(dst)) | bit);
        }
     };
    
    /******************************************
     bitb	 rbd,rs
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z26_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	if (RB(dst) & (1 << (RW(src) & 7))) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bitb	 @rd,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z26_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	if (RDMEM_B(RW(dst)) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bit	 rd,rs
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z27_0000_ssss_0000_dddd_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	if (RW(dst) & (1 << (RW(src) & 15))) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bit	 @rd,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z27_ddN0_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_BIT(OP0);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            if ((_cpu.RDMEM_W(_cpu._cpuH.RW(dst)) & bit)!=0) _cpu._cpuH.CLR_Z(); else _cpu._cpuH.SET_Z();
        }
     };
    
    /******************************************
     incb	 @rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z28_ddN0_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B( RW(dst), INCB( RDMEM_B(RW(dst)), i4p1) );
        }
     };
    
    /******************************************
     inc     @rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z29_ddN0_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W( RW(dst), INCW( RDMEM_W(RW(dst)), i4p1 ) );
        }
     };
    
    /******************************************
     decb	 @rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z2A_ddN0_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B( RW(dst), DECB( RDMEM_B(RW(dst)), i4p1 ) );
        }
     };
    
    /******************************************
     dec     @rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z2B_ddN0_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_W( RW(dst), DECW( RDMEM_W(RW(dst)), i4p1 ) );
        }
     };
    
    /******************************************
     exb	 rbd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z2C_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	UINT8 tmp = RDMEM_B( RW(src) );
    /*TODO*///	WRMEM_B( RW(src), RB(dst) );
    /*TODO*///	RB(dst) = tmp;
        }
     };
    
    /******************************************
     ex 	 rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z2D_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	UINT16 tmp = RDMEM_W( RW(src) );
    /*TODO*///	WRMEM_W( RW(src), RW(dst) );
    /*TODO*///	RW(dst) = tmp;
        }
     };
    
    /******************************************
     ldb	 @rd,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z2E_ddN0_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	WRMEM_B( RW(dst), RB(src) );
        }
     };
    
    /******************************************
     ld 	 @rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z2F_ddN0_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_SRC(OP0,NIB3);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu.WRMEM_W( _cpu._cpuH.RW(dst), _cpu._cpuH.RW(src) );
        }
     };
    
    /******************************************
     ldrb	 rbd,dsp16
     flags:  ------
     ******************************************/
     public OpcodePtr Z30_0000_dddd_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	RB(dst) = RDMEM_B(dsp16);
        }
     };
    
    /******************************************
     ldb	 rbd,rs(imm16)
     flags:  ------
     ******************************************/
     public OpcodePtr Z30_ssN0_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(src);
    /*TODO*///	RB(dst) = RDMEM_B( imm16 );
        }
     };
    
    /******************************************
     ldr	 rd,dsp16
     flags:  ------
     ******************************************/
     public OpcodePtr Z31_0000_dddd_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	RW(dst) = RDMEM_W(dsp16);
        }
     };
    
    /******************************************
     ld 	 rd,rs(imm16)
     flags:  ------
     ******************************************/
     public OpcodePtr Z31_ssN0_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(src);
    /*TODO*///	RW(dst) = RDMEM_W( imm16 );
        }
     };
    
    /******************************************
     ldrb	 dsp16,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z32_0000_ssss_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	WRMEM_B( dsp16, RB(src) );
        }
     };
    
    /******************************************
     ldb	 rd(imm16),rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z32_ddN0_ssss_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(dst);
    /*TODO*///	WRMEM_B( imm16, RB(src) );
        }
     };
    
    /******************************************
     ldr	 dsp16,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z33_0000_ssss_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	WRMEM_W( dsp16, RW(src) );
        }
     };
    
    /******************************************
     ld 	 rd(imm16),rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z33_ddN0_ssss_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(dst);
    /*TODO*///	WRMEM_W( imm16, RW(src) );
        }
     };
    
    /******************************************
     ldar	 prd,dsp16
     flags:  ------
     ******************************************/
     public OpcodePtr Z34_0000_dddd_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	RW(dst) = dsp16;
        }
     };
    
    /******************************************
     lda	 prd,rs(imm16)
     flags:  ------
     ******************************************/
     public OpcodePtr Z34_ssN0_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(src);
    /*TODO*///	RW(dst) = imm16;
        }
     };
    
    /******************************************
     ldrl	 rrd,dsp16
     flags:  ------
     ******************************************/
     public OpcodePtr Z35_0000_dddd_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	RL(dst) = RDMEM_L( dsp16 );
        }
     };
    
    /******************************************
     ldl	 rrd,rs(imm16)
     flags:  ------
     ******************************************/
     public OpcodePtr Z35_ssN0_dddd_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(src);
    /*TODO*///	RL(dst) = RDMEM_L( imm16 );
        }
     };
    
    /******************************************
     bpt
     flags:  ------
     ******************************************/
     public OpcodePtr Z36_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* execute break point trap IRQ_REQ */
    /*TODO*///	IRQ_REQ = Z8000_TRAP;
        }
     };
    
    /******************************************
     rsvd36
     flags:  ------
     ******************************************/
     public OpcodePtr Z36_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd36 $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldrl	 dsp16,rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z37_0000_ssss_dsp16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DSP16;
    /*TODO*///	WRMEM_L( dsp16, RL(src) );
        }
     };
    
    /******************************************
     ldl	 rd(imm16),rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z37_ddN0_ssss_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	imm16 += RW(dst);
    /*TODO*///	WRMEM_L( imm16, RL(src) );
        }
     };
    
    /******************************************
     rsvd38
     flags:  ------
     ******************************************/
     public OpcodePtr Z38_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd38 $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldps	 @rs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z39_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	UINT16 fcw;
    /*TODO*///	fcw = RDMEM_W( RW(src) );
    /*TODO*///	PC	= RDMEM_W( (UINT16)(RW(src) + 2) );
    /*TODO*///	CHANGE_FCW(fcw); /* check for user/system mode change */
    /*TODO*///    change_pc16bew(PC);
        }
     };
    
    /******************************************
     inib(r) @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3A_ssss_0000_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///    GET_DST(OP1,NIB2);
    /*TODO*///    GET_CCC(OP1,NIB3);
    /*TODO*///    WRMEM_B( RW(dst), RDPORT_B( 0, RW(src) ) );
    /*TODO*///    RW(dst)++;
    /*TODO*///    RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     sinib	 @rd,@rs,ra
     sinibr  @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_ssss_0001_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///    GET_DST(OP1,NIB2);
    /*TODO*///    GET_CCC(OP1,NIB3);
    /*TODO*///    WRMEM_B( RW(dst), RDPORT_B( 1, RW(src) ) );
    /*TODO*///    RW(dst)++;
    /*TODO*///    RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     outib	 @rd,@rs,ra
     outibr  @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3A_ssss_0010_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///    GET_DST(OP1,NIB2);
    /*TODO*///    GET_CCC(OP1,NIB3);
    /*TODO*///    WRPORT_B( 0, RW(dst), RDMEM_B( RW(src) ) );
    /*TODO*///    RW(dst)++;
    /*TODO*///    RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     soutib  @rd,@rs,ra
     soutibr @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_ssss_0011_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///    GET_DST(OP1,NIB2);
    /*TODO*///    GET_CCC(OP1,NIB3);
    /*TODO*///    WRPORT_B( 1, RW(dst), RDMEM_B( RW(src) ) );
    /*TODO*///    RW(dst)++;
    /*TODO*///    RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     inb     rbd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_dddd_0100_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///    RB(dst) = RDPORT_B( 0, imm16 );
        }
     };
    
    /******************************************
     sinb    rbd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_dddd_0101_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///    RB(dst) = RDPORT_B( 1, imm16 );
        }
     };
    
    /******************************************
     outb    imm16,rbs
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3A_ssss_0110_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///    WRPORT_B( 0, imm16, RB(src) );
        }
     };
    
    /******************************************
     soutb   imm16,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_ssss_0111_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///    WRPORT_B( 1, imm16, RB(src) );
        }
     };
    
    /******************************************
     indb	 @rd,@rs,rba
     indbr	 @rd,@rs,rba
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3A_ssss_1000_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_B( RW(dst), RDPORT_B( 0, RW(src) ) );
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     sindb	 @rd,@rs,rba
     sindbr  @rd,@rs,rba
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_ssss_1001_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_B( RW(dst), RDPORT_B( 1, RW(src) ) );
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     outdb	 @rd,@rs,rba
     outdbr  @rd,@rs,rba
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3A_ssss_1010_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_B( 0, RW(dst), RDMEM_B( RW(src) ) );
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     soutdb  @rd,@rs,rba
     soutdbr @rd,@rs,rba
     flags:  ------
     ******************************************/
     public OpcodePtr Z3A_ssss_1011_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_B( 1, RW(dst), RDMEM_B( RW(src) ) );
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     ini	 @rd,@rs,ra
     inir	 @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3B_ssss_0000_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_W( RW(dst), RDPORT_W( 0, RW(src) ) );
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     sini	 @rd,@rs,ra
     sinir	 @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_ssss_0001_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_W( RW(dst), RDPORT_W( 1, RW(src) ) );
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     outi	 @rd,@rs,ra
     outir	 @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3B_ssss_0010_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_W( 0, RW(dst), RDMEM_W( RW(src) ) );
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     souti	 @rd,@rs,ra
     soutir  @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_ssss_0011_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_W( 1, RW(dst), RDMEM_W( RW(src) ) );
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     in 	 rd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_dddd_0100_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///	RW(dst) = RDPORT_W( 0, imm16 );
        }
     };
    
    /******************************************
     sin	 rd,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_dddd_0101_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///	RW(dst) = RDPORT_W( 1, imm16 );
        }
     };
    
    /******************************************
     out	 imm16,rs
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3B_ssss_0110_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///	WRPORT_W( 0, imm16, RW(src) );
        }
     };
    
    /******************************************
     sout	 imm16,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_ssss_0111_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_IMM16(OP1);
    /*TODO*///	WRPORT_W( 1, imm16, RW(src) );
        }
     };
    
    /******************************************
     ind	 @rd,@rs,ra
     indr	 @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3B_ssss_1000_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_W( RW(dst), RDPORT_W( 0, RW(src) ) );
    /*TODO*///	RW(dst) -= 2;
    /*TODO*///	RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     sind	 @rd,@rs,ra
     sindr	 @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_ssss_1001_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_W( RW(dst), RDPORT_W( 1, RW(src) ) );
    /*TODO*///	RW(dst) -= 2;
    /*TODO*///	RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     outd	 @rd,@rs,ra
     outdr	 @rd,@rs,ra
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3B_ssss_1010_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_W( 0, RW(dst), RDMEM_W( RW(src) ) );
    /*TODO*///	RW(dst) -= 2;
    /*TODO*///	RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     soutd	 @rd,@rs,ra
     soutdr  @rd,@rs,ra
     flags:  ------
     ******************************************/
     public OpcodePtr Z3B_ssss_1011_0000_aaaa_dddd_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRPORT_W( 1, RW(dst), RDMEM_W( RW(src) ) );
    /*TODO*///	RW(dst) -= 2;
    /*TODO*///	RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     inb	 rbd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z3C_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	RB(dst) = RDPORT_B( 0, RDMEM_W( RW(src) ) );
        }
     };
    
    /******************************************
     in 	 rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z3D_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	RW(dst) = RDPORT_W( 0, RDMEM_W( RW(src) ) );
        }
     };
    
    /******************************************
     outb	 @rd,rbs
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3E_dddd_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	WRPORT_B( 0, RDMEM_W( RW(dst) ), RB(src) );
        }
     };
    
    /******************************************
     out	 @rd,rs
     flags:  ---V--
     ******************************************/
     public OpcodePtr Z3F_dddd_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	WRPORT_W( 0, RDMEM_W( RW(dst) ), RW(src) );
        }
     };
    
    /******************************************
     addb	 rbd,addr
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z40_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.RB(dst, ADDB( _cpu._cpuH.RB(dst), _cpu.RDMEM_B(addr) ));
        }
     };
    
    /******************************************
     addb	 rbd,addr(rs)
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z40_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = ADDB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     add	 rd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z41_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = ADDW( RW(dst), RDMEM_W(addr)); /* EHC */
        }
     };
    
    /******************************************
     add	 rd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z41_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RW(dst) = ADDW( RW(dst), RDMEM_W(addr) );	/* ASG */
        }
     };
    
    /******************************************
     subb	 rbd,addr
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z42_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RB(dst) = SUBB( RB(dst), RDMEM_B(addr)); /* EHC */
        }
     };
    
    /******************************************
     subb	 rbd,addr(rs)
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z42_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = SUBB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     sub	 rd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z43_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = SUBW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     sub	 rd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z43_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RW(dst) = SUBW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     orb	 rbd,addr
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z44_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RB(dst) = ORB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     orb	 rbd,addr(rs)
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z44_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = ORB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     or 	 rd,addr
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z45_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = ORW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     or 	 rd,addr(rs)
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z45_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RW(dst) = ORW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     andb	 rbd,addr
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z46_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RB(dst) = ANDB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     andb	 rbd,addr(rs)
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z46_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = ANDB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     and	 rd,addr
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z47_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = ANDW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     and	 rd,addr(rs)
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z47_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RW(dst) = ANDW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     xorb	 rbd,addr
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z48_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.RB(dst, XORB( _cpu._cpuH.RB(dst), _cpu.RDMEM_B(addr) ));
        }
     };
    
    /******************************************
     xorb	 rbd,addr(rs)
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z48_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = XORB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     xor	 rd,addr
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z49_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = XORW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     xor	 rd,addr(rs)
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z49_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RW(dst) = XORW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     cpb	 rbd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4A_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	CPB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     cpb	 rbd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4A_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	CPB( RB(dst), RDMEM_B(addr) );
        }
     };
    
    /******************************************
     cp 	 rd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4B_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	CPW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     cp 	 rd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4B_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	CPW( RW(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     comb	 addr
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z4C_0000_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, COMB(RDMEM_W(addr)) );
        }
     };
    
    /******************************************
     cpb	 addr,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4C_0000_0001_addr_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM8(OP2);
    /*TODO*///	CPB( RDMEM_B(addr), imm8 );
        }
     };
    
    /******************************************
     negb	 addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4C_0000_0010_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, NEGB(RDMEM_B(addr)) );
        }
     };
    
    /******************************************
     testb	 addr
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z4C_0000_0100_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	TESTB(RDMEM_B(addr));
        }
     };
    
    /******************************************
     ldb	 addr,imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z4C_0000_0101_addr_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM8(OP2);
    /*TODO*///	WRMEM_B( addr, imm8 );
        }
     };
    
    /******************************************
     tsetb	 addr
     flags:  --S---
     ******************************************/
     public OpcodePtr Z4C_0000_0110_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///    if (RDMEM_B(addr) & S08) SET_S; else CLR_S;
    /*TODO*///    WRMEM_B(addr, 0xff);
        }
     };
    
    /******************************************
     clrb	 addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z4C_0000_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, 0 );
        }
     };
    
    /******************************************
     comb	 addr(rd)
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z4C_ddN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, COMB(RDMEM_B(addr)) );
        }
     };
    
    /******************************************
     cpb	 addr(rd),imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4C_ddN0_0001_addr_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM8(OP2);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	CPB( RDMEM_B(addr), imm8 );
        }
     };
    
    /******************************************
     negb	 addr(rd)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4C_ddN0_0010_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, NEGB(RDMEM_B(addr)) );
        }
     };
    
    /******************************************
     testb	 addr(rd)
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z4C_ddN0_0100_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	TESTB( RDMEM_B(addr) );
        }
     };
    
    /******************************************
     ldb	 addr(rd),imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z4C_ddN0_0101_addr_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM8(OP2);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, imm8 );
        }
     };
    
    /******************************************
     tsetb	 addr(rd)
     flags:  --S---
     ******************************************/
     public OpcodePtr Z4C_ddN0_0110_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///    if (RDMEM_B(addr) & S08) SET_S; else CLR_S;
    /*TODO*///    WRMEM_B(addr, 0xff);
        }
     };
    
    /******************************************
     clrb	 addr(rd)
     flags:  ------
     ******************************************/
     public OpcodePtr Z4C_ddN0_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, 0 );
        }
     };
    
    /******************************************
     com	 addr
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z4D_0000_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, COMW(RDMEM_W(addr)) );
        }
     };
    
    /******************************************
     cp 	 addr,imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4D_0000_0001_addr_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM16(OP2);
    /*TODO*///	CPW( RDMEM_W(addr), imm16 );
        }
     };
    
    /******************************************
     neg	 addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4D_0000_0010_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, NEGW(RDMEM_W(addr)) );
        }
     };
    
    /******************************************
     test	 addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_0000_0100_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_ADDR(OP1);
            TESTW( _cpu.RDMEM_W(addr) );
        }
     };
    
    /******************************************
     ld 	 addr,imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_0000_0101_addr_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.GET_IMM16(OP2);
            _cpu.WRMEM_W( addr, imm16 );
        }
     };
    
    /******************************************
     tset	 addr
     flags:  --S---
     ******************************************/
     public OpcodePtr Z4D_0000_0110_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///    if (RDMEM_W(addr) & S16) SET_S; else CLR_S;
    /*TODO*///    WRMEM_W(addr, 0xffff);
        }
     };
    
    /******************************************
     clr	 addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_0000_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu.WRMEM_W( addr, 0 );
        }
     };
    
    /******************************************
     com	 addr(rd)
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z4D_ddN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, COMW(RDMEM_W(addr)) );
        }
     };
    
    /******************************************
     cp 	 addr(rd),imm16
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4D_ddN0_0001_addr_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM16(OP2);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	CPW( RDMEM_W(addr), imm16 );
        }
     };
    
    /******************************************
     neg	 addr(rd)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z4D_ddN0_0010_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, NEGW(RDMEM_W(addr)) );
        }
     };
    
    /******************************************
     test	 addr(rd)
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_ddN0_0100_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	TESTW( RDMEM_W(addr) );
        }
     };
    
    /******************************************
     ld 	 addr(rd),imm16
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_ddN0_0101_addr_imm16 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	GET_IMM16(OP2);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, imm16 );
        }
     };
    
    /******************************************
     tset	 addr(rd)
     flags:  --S---
     ******************************************/
     public OpcodePtr Z4D_ddN0_0110_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///    if (RDMEM_W(addr) & S16) SET_S; else CLR_S;
    /*TODO*///    WRMEM_W(addr, 0xffff);
        }
     };
    
    /******************************************
     clr	 addr(rd)
     flags:  ------
     ******************************************/
     public OpcodePtr Z4D_ddN0_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, 0 );
        }
     };
    
    /******************************************
     ldb	 addr(rd),rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z4E_ddN0_ssN0_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, RB(src) );
        }
     };
    
    /******************************************
     cpl	 rrd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z50_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	CPL( RL(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     cpl	 rrd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z50_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	CPL( RL(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     pushl	 @rd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z51_ddN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	PUSHL( dst, RDMEM_L(addr) );
        }
     };
    
    /******************************************
     pushl	 @rd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z51_ddN0_ssN0_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	PUSHL( dst, RDMEM_L(addr) );
        }
     };
    
    /******************************************
     subl	 rrd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z52_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.RL(dst, SUBL( _cpu._cpuH.RL(dst), _cpu.RDMEM_L(addr) ));
        }
     };
    
    /******************************************
     subl	 rrd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z52_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RL(dst) = SUBL( RL(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     push	 @rd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z53_ddN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	PUSHW( dst, RDMEM_W(addr) );
        }
     };
    
    /******************************************
     push	 @rd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z53_ddN0_ssN0_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	PUSHW( dst, RDMEM_W(addr) );
        }
     };
    
    /******************************************
     ldl	 rrd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z54_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RL(dst) = RDMEM_L( addr );
        }
     };
    
    /******************************************
     ldl	 rrd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z54_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RL(dst) = RDMEM_L( addr );
        }
     };
    
    /******************************************
     popl	 addr,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z55_ssN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_L( addr, POPL(src) );
        }
     };
    
    /******************************************
     popl	 addr(rd),@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z55_ssN0_ddN0_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_L( addr, POPL(src) );
        }
     };
    
    /******************************************
     addl	 rrd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z56_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RL(dst) = ADDL( RL(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     addl	 rrd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z56_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RL(dst) = ADDL( RL(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     pop	 addr,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z57_ssN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, POPW(src) );
        }
     };
    
    /******************************************
     pop	 addr(rd),@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z57_ssN0_ddN0_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, POPW(src) );
        }
     };
    
    /******************************************
     multl	 rqd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z58_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RQ(dst) = MULTL( RQ(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     multl	 rqd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z58_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RQ(dst) = MULTL( RQ(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     mult	 rrd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z59_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.RL(dst, MULTW( _cpu._cpuH.RL(dst), _cpu.RDMEM_W(addr) ));
        }
     };
    
    /******************************************
     mult	 rrd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z59_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RL(dst) = MULTW( RL(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     divl	 rqd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z5A_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RQ(dst) = DIVL( RQ(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     divl	 rqd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z5A_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RQ(dst) = DIVL( RQ(dst), RDMEM_L(addr) );
        }
     };
    
    /******************************************
     div	 rrd,addr
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z5B_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RL(dst) = DIVW( RL(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     div	 rrd,addr(rs)
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z5B_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RL(dst) = DIVW( RL(dst), RDMEM_W(addr) );
        }
     };
    
    /******************************************
     ldm	 rd,addr,n
     flags:  ------
     ******************************************/
     public OpcodePtr Z5C_0000_0001_0000_dddd_0000_nmin1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	GET_CNT(OP1,NIB3);
    /*TODO*///	GET_ADDR(OP2);
    /*TODO*///	while (cnt-- >= 0) {
    /*TODO*///		RW(dst) = RDMEM_W(addr);
    /*TODO*///		dst = ++dst & 15;
    /*TODO*///		addr = (addr + 2) & 0xffff;
    /*TODO*///	}
        }
     };
    
    /******************************************
     testl	 addr
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z5C_0000_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	TESTL( RDMEM_L(addr) );
        }
     };
    
    /******************************************
     ldm	 addr,rs,n
     flags:  ------
     ******************************************/
     public OpcodePtr Z5C_0000_1001_0000_ssss_0000_nmin1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	GET_CNT(OP1,NIB3);
    /*TODO*///	GET_ADDR(OP2);
    /*TODO*///	while (cnt-- >= 0) {
    /*TODO*///		WRMEM_W( addr, RW(src) );
    /*TODO*///		src = ++src & 15;
    /*TODO*///		addr = (addr + 2) & 0xffff;
    /*TODO*///	}
        }
     };
    
    /******************************************
     testl	 addr(rd)
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z5C_ddN0_1000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	TESTL( RDMEM_L(addr) );
        }
     };
    
    /******************************************
     ldm	 addr(rd),rs,n
     flags:  ------
     ******************************************/
     public OpcodePtr Z5C_ddN0_1001_0000_ssN0_0000_nmin1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	GET_CNT(OP1,NIB3);
    /*TODO*///	GET_ADDR(OP2);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	while (cnt-- >= 0) {
    /*TODO*///		WRMEM_W( addr, RW(src) );
    /*TODO*///		src = ++src & 15;
    /*TODO*///		addr = (addr + 2) & 0xffff;
    /*TODO*///	}
        }
     };
    
    /******************************************
     ldm	 rd,addr(rs),n
     flags:  ------
     ******************************************/
     public OpcodePtr Z5C_ssN0_0001_0000_dddd_0000_nmin1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_DST(OP1,NIB1);
    /*TODO*///	GET_CNT(OP1,NIB3);
    /*TODO*///	GET_ADDR(OP2);
    /*TODO*///	addr += RW(src);
    /*TODO*///	while (cnt-- >= 0) {
    /*TODO*///		RW(dst) = RDMEM_W(addr);
    /*TODO*///		dst = ++dst & 15;
    /*TODO*///		addr = (addr + 2) & 0xffff;
    /*TODO*///	}
        }
     };
    
    /******************************************
     ldl	 addr,rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z5D_0000_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_L( addr, RL(src) );
        }
     };
    
    /******************************************
     ldl	 addr(rd),rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z5D_ddN0_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_L( addr, RL(src) );
        }
     };
    
    /******************************************
     jp 	 cc,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z5E_0000_cccc_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_CCC(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            switch (cc) {
                    case  0: if (_cpu._cpuH.CC0() != 0) _cpu.Z.pc = addr; break;
                    case  1: if (_cpu._cpuH.CC1() != 0) _cpu.Z.pc = addr; break;
                    case  2: if (_cpu._cpuH.CC2() != 0) _cpu.Z.pc = addr; break;
                    case  3: if (_cpu._cpuH.CC3() != 0) _cpu.Z.pc = addr; break;
                    case  4: if (_cpu._cpuH.CC4() != 0) _cpu.Z.pc = addr; break;
                    case  5: if (_cpu._cpuH.CC5() != 0) _cpu.Z.pc = addr; break;
                    case  6: if (_cpu._cpuH.CC6() != 0) _cpu.Z.pc = addr; break;
                    case  7: if (_cpu._cpuH.CC7() != 0) _cpu.Z.pc = addr; break;
                    case  8: if (_cpu._cpuH.CC8() != 0) _cpu.Z.pc = addr; break;
                    case  9: if (_cpu._cpuH.CC9() != 0) _cpu.Z.pc = addr; break;
                    case 10: if (_cpu._cpuH.CCA() != 0) _cpu.Z.pc = addr; break;
                    case 11: if (_cpu._cpuH.CCB() != 0) _cpu.Z.pc = addr; break;
                    case 12: if (_cpu._cpuH.CCC() != 0) _cpu.Z.pc = addr; break;
                    case 13: if (_cpu._cpuH.CCD() != 0) _cpu.Z.pc = addr; break;
                    case 14: if (_cpu._cpuH.CCE() != 0) _cpu.Z.pc = addr; break;
                    case 15: if (_cpu._cpuH.CCF() != 0) _cpu.Z.pc = addr; break;
            }
            change_pc16bew(_cpu.Z.pc);
        }
     };
    
    /******************************************
     jp 	 cc,addr(rd)
     flags:  ------
     ******************************************/
     public OpcodePtr Z5E_ddN0_cccc_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_CCC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) PC = addr; break;
    /*TODO*///		case  1: if (CC1 != 0) PC = addr; break;
    /*TODO*///		case  2: if (CC2 != 0) PC = addr; break;
    /*TODO*///		case  3: if (CC3 != 0) PC = addr; break;
    /*TODO*///		case  4: if (CC4 != 0) PC = addr; break;
    /*TODO*///		case  5: if (CC5 != 0) PC = addr; break;
    /*TODO*///		case  6: if (CC6 != 0) PC = addr; break;
    /*TODO*///		case  7: if (CC7 != 0) PC = addr; break;
    /*TODO*///		case  8: if (CC8 != 0) PC = addr; break;
    /*TODO*///		case  9: if (CC9 != 0) PC = addr; break;
    /*TODO*///		case 10: if (CCA != 0) PC = addr; break;
    /*TODO*///		case 11: if (CCB != 0) PC = addr; break;
    /*TODO*///		case 12: if (CCC != 0) PC = addr; break;
    /*TODO*///		case 13: if (CCD != 0) PC = addr; break;
    /*TODO*///		case 14: if (CCE != 0) PC = addr; break;
    /*TODO*///		case 15: if (CCF != 0) PC = addr; break;
    /*TODO*///	}
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     call	 addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z5F_0000_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	PUSHW( SP, PC );
    /*TODO*///	PC = addr;
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     call	 addr(rd)
     flags:  ------
     ******************************************/
     public OpcodePtr Z5F_ddN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	PUSHW( SP, PC );
    /*TODO*///	addr += RW(dst);
    /*TODO*///	PC = addr;
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     ldb	 rbd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z60_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RB(dst) = RDMEM_B(addr);
        }
     };
    
    /******************************************
     ldb	 rbd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z60_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///	RB(dst) = RDMEM_B(addr);
        }
     };
    
    /******************************************
     ld 	 rd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z61_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu._cpuH.RW(dst, _cpu.RDMEM_W(addr));
        }
     };
    
    /******************************************
     ld 	 rd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z61_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.GET_ADDR(OP1);
            addr += _cpu._cpuH.RW(src);
            _cpu._cpuH.RW(dst, _cpu.RDMEM_W(addr));
        }
     };
    
    /******************************************
     resb	 addr,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z62_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, RDMEM_B(addr) & ~bit );
        }
     };
    
    /******************************************
     resb	 addr(rd),imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z62_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, RDMEM_B(addr) & ~bit );
        }
     };
    
    /******************************************
     res	 addr,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z63_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_BIT(OP0);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu.WRMEM_W( addr, _cpu.RDMEM_W(addr) & ~bit );
        }
     };
    
    /******************************************
     res	 addr(rd),imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z63_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, RDMEM_W(addr) & ~bit );
        }
     };
    
    /******************************************
     setb	 addr,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z64_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, RDMEM_B(addr) | bit );
        }
     };
    
    /******************************************
     setb	 addr(rd),imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z64_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, RDMEM_B(addr) | bit );
        }
     };
    
    /******************************************
     set	 addr,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z65_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, RDMEM_W(addr) | bit );
        }
     };
    
    /******************************************
     set	 addr(rd),imm4
     flags:  ------
     ******************************************/
     public OpcodePtr Z65_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, RDMEM_W(addr) | bit );
        }
     };
    
    /******************************************
     bitb	 addr,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z66_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	if ( RDMEM_B(addr) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bitb	 addr(rd),imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z66_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///    addr += RW(dst);
    /*TODO*///    if ( RDMEM_B(addr) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bit	 addr,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z67_0000_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	if ( RDMEM_W(addr) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bit	 addr(rd),imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr Z67_ddN0_imm4_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///    addr += RW(dst);
    /*TODO*///	if ( RDMEM_W(addr) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     incb	 addr,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z68_0000_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, INCB(RDMEM_B(addr), i4p1) );
        }
     };
    
    /******************************************
     incb	 addr(rd),imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z68_ddN0_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, INCB(RDMEM_B(addr), i4p1) );
        }
     };
    
    /******************************************
     inc	 addr,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z69_0000_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, INCW(RDMEM_W(addr), i4p1) );
        }
     };
    
    /******************************************
     inc	 addr(rd),imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z69_ddN0_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, INCW(RDMEM_W(addr), i4p1) );
        }
     };
    
    /******************************************
     decb	 addr,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z6A_0000_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, DECB(RDMEM_B(addr), i4p1) );
        }
     };
    
    /******************************************
     decb	 addr(rd),imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z6A_ddN0_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, DECB(RDMEM_B(addr), i4p1) );
        }
     };
    
    /******************************************
     dec	 addr,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z6B_0000_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_W( addr, DECW(RDMEM_W(addr), i4p1) );
        }
     };
    
    /******************************************
     dec	 addr(rd),imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr Z6B_ddN0_imm4m1_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, DECW(RDMEM_W(addr), i4p1) );
        }
     };
    
    /******************************************
     exb	 rbd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z6C_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT8 tmp = RDMEM_B(addr);
    /*TODO*///	WRMEM_B(addr, RB(dst));
    /*TODO*///	RB(dst) = tmp;
        }
     };
    
    /******************************************
     exb	 rbd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z6C_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT8 tmp;
    /*TODO*///	addr += RW(src);
    /*TODO*///	tmp = RDMEM_B(addr);
    /*TODO*///	WRMEM_B(addr, RB(dst));
    /*TODO*///    RB(dst) = tmp;
        }
     };
    
    /******************************************
     ex 	 rd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z6D_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT16 tmp = RDMEM_W(addr);
    /*TODO*///	WRMEM_W( addr, RW(dst) );
    /*TODO*///	RW(dst) = tmp;
        }
     };
    
    /******************************************
     ex 	 rd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z6D_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT16 tmp;
    /*TODO*///	addr += RW(src);
    /*TODO*///	tmp = RDMEM_W(addr);
    /*TODO*///	WRMEM_W( addr, RW(dst) );
    /*TODO*///    RW(dst) = tmp;
        }
    };
    
    /******************************************
     ldb	 addr,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z6E_0000_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	WRMEM_B( addr, RB(src) );
        }
    };
    
    /******************************************
     ldb	 addr(rd),rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z6E_ddN0_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_B( addr, RB(src) );
        }
     };
    
    /******************************************
     ld 	 addr,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z6F_0000_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_SRC(OP0,NIB3);
            _cpu._cpuH.GET_ADDR(OP1);
            _cpu.WRMEM_W( addr, _cpu._cpuH.RW(src) );
        }
     };
    
    /******************************************
     ld 	 addr(rd),rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z6F_ddN0_ssss_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(dst);
    /*TODO*///	WRMEM_W( addr, RW(src) );
        }
     };
    
    /******************************************
     ldb	 rbd,rs(rx)
     flags:  ------
     ******************************************/
     public OpcodePtr Z70_ssN0_dddd_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	RB(dst) = RDMEM_B( (UINT16)(RW(src) + RW(idx)) );
        }
     };
    
    /******************************************
     ld 	 rd,rs(rx)
     flags:  ------
     ******************************************/
     public OpcodePtr Z71_ssN0_dddd_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	RW(dst) = RDMEM_W( (UINT16)(RW(src) + RW(idx)) );
        }
     };
    
    /******************************************
     ldb	 rd(rx),rbs
     flags:  ------
     ******************************************/
     public OpcodePtr Z72_ddN0_ssss_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	WRMEM_B( (UINT16)(RW(dst) + RW(idx)), RB(src) );
        }
     };
    
    /******************************************
     ld 	 rd(rx),rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z73_ddN0_ssss_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	WRMEM_W( (UINT16)(RW(dst) + RW(idx)), RW(src) );
        }
     };
    
    /******************************************
     lda	 prd,rs(rx)
     flags:  ------
     ******************************************/
     public OpcodePtr Z74_ssN0_dddd_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	RW(dst) = (UINT16)(RW(src) + RW(idx));
        }
     };
    
    /******************************************
     ldl	 rrd,rs(rx)
     flags:  ------
     ******************************************/
     public OpcodePtr Z75_ssN0_dddd_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	RL(dst) = RDMEM_L( (UINT16)(RW(src) + RW(idx)) );
        }
     };
    
    /******************************************
     lda	 prd,addr
     flags:  ------
     ******************************************/
     public OpcodePtr Z76_0000_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	RW(dst) = addr;
        }
     };
    
    /******************************************
     lda	 prd,addr(rs)
     flags:  ------
     ******************************************/
     public OpcodePtr Z76_ssN0_dddd_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	addr += RW(src);
    /*TODO*///    RW(dst) = addr;
        }
     };
    
    /******************************************
     ldl	 rd(rx),rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z77_ddN0_ssss_0000_xxxx_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IDX(OP1,NIB1);
    /*TODO*///	WRMEM_L( (UINT16)(RW(dst) + RW(idx)), RL(src) );
        }
     };
    
    /******************************************
     rsvd78
     flags:  ------
     ******************************************/
     public OpcodePtr Z78_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd78 $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldps	 addr
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z79_0000_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT16 fcw;
    /*TODO*///	fcw = RDMEM_W(addr);
    /*TODO*///	PC	= RDMEM_W((UINT16)(addr + 2));
    /*TODO*///	CHANGE_FCW(fcw); /* check for user/system mode change */
    /*TODO*///    change_pc16bew(PC);
        }
     };
    
    /******************************************
     ldps	 addr(rs)
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z79_ssN0_0000_addr = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_ADDR(OP1);
    /*TODO*///	UINT16 fcw;
    /*TODO*///	addr += RW(src);
    /*TODO*///	fcw = RDMEM_W(addr);
    /*TODO*///	PC	= RDMEM_W((UINT16)(addr + 2));
    /*TODO*///	CHANGE_FCW(fcw); /* check for user/system mode change */
    /*TODO*///    change_pc16bew(PC);
        }
     };
    
    /******************************************
     halt
     flags:  ------
     ******************************************/
     public OpcodePtr Z7A_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	IRQ_REQ |= Z8000_HALT;
    /*TODO*///	if (z8000_ICount > 0) z8000_ICount = 0;
        }
     };
    
    /******************************************
     iret
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z7B_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	UINT16 tag, fcw;
    /*TODO*///	tag = POPW( SP );	/* get type tag */
    /*TODO*///	fcw = POPW( SP );	/* get FCW	*/
    /*TODO*///	PC	= POPW( SP );	/* get PC	*/
    /*TODO*///    IRQ_SRV &= ~tag;    /* remove IRQ serviced flag */
    /*TODO*///	CHANGE_FCW(fcw);		 /* check for user/system mode change */
    /*TODO*///    change_pc16bew(PC);
    /*TODO*///	LOG(("Z8K#%d IRET tag $%04x, fcw $%04x, pc $%04x\n", cpu_getactivecpu(), tag, fcw, PC));
        }
     };
    
    /******************************************
     mset
     flags:  ------
     ******************************************/
     public OpcodePtr Z7B_0000_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* set mu-0 line */
        }
     };
    
    /******************************************
     mres
     flags:  ------
     ******************************************/
     public OpcodePtr Z7B_0000_1001 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* reset mu-0 line */
        }
     };
    
    /******************************************
     mbit
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z7B_0000_1010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* test mu-I line */
        }
     };
    
    /******************************************
     mreq	 rd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z7B_dddd_1101 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* test mu-I line, invert cascade to mu-0  */
        }
     };
    
    /******************************************
     di 	 i2
     flags:  ------
     ******************************************/
     public OpcodePtr Z7C_0000_00ii = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM2(OP0,NIB3);
    /*TODO*///	UINT16 fcw = FCW;
    /*TODO*///	fcw &= ~(imm2 << 11);
    /*TODO*///	CHANGE_FCW(fcw);
        }
     };
    
    /******************************************
     ei 	 i2
     flags:  ------
     ******************************************/
     public OpcodePtr Z7C_0000_01ii = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM2(OP0,NIB3);
    /*TODO*///	UINT16 fcw = FCW;
    /*TODO*///	fcw |= imm2 << 11;
    /*TODO*///	CHANGE_FCW(fcw);
        }
     };
    
    /******************************************
     ldctl	 rd,ctrl
     flags:  ------
     ******************************************/
     public OpcodePtr Z7D_dddd_0ccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM3(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	switch (imm3) {
    /*TODO*///		case 0:
    /*TODO*///			RW(dst) = FCW;
    /*TODO*///			break;
    /*TODO*///		case 3:
    /*TODO*///			RW(dst) = REFRESH;
    /*TODO*///			break;
    /*TODO*///		case 5:
    /*TODO*///			RW(dst) = PSAP;
    /*TODO*///			break;
    /*TODO*///		case 7:
    /*TODO*///			RW(dst) = NSP;
    /*TODO*///			break;
    /*TODO*///		default:
    /*TODO*///			LOG(("Z8K#%d LDCTL R%d,%d\n", cpu_getactivecpu(), dst, imm3));
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldctl	 ctrl,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z7D_ssss_1ccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM3(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	switch (imm3) {
    /*TODO*///		case 0:
    /*TODO*///			{
    /*TODO*///				UINT16 fcw;
    /*TODO*///				fcw = RW(src);
    /*TODO*///				CHANGE_FCW(fcw); /* check for user/system mode change */
    /*TODO*///			}
    /*TODO*///            break;
    /*TODO*///		case 3:
    /*TODO*///			REFRESH = RW(src);
    /*TODO*///			break;
    /*TODO*///		case 5:
    /*TODO*///			PSAP = RW(src);
    /*TODO*///			break;
    /*TODO*///		case 7:
    /*TODO*///			NSP = RW(src);
    /*TODO*///			break;
    /*TODO*///		default:
    /*TODO*///			LOG(("Z8K#%d LDCTL %d,R%d\n", cpu_getactivecpu(), imm3, src));
    /*TODO*///    }
        }
     };
    
    /******************************************
     rsvd7e
     flags:  ------
     ******************************************/
     public OpcodePtr Z7E_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd7e $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     sc 	 imm8
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z7F_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_IMM8(0);
    /*TODO*///	/* execute system call via IRQ */
    /*TODO*///	IRQ_REQ = Z8000_SYSCALL | imm8;
    /*TODO*///
        }
     };
    
    /******************************************
     addb	 rbd,rbs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z80_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ADDB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     add	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z81_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, ADDW( _cpu._cpuH.RW(dst), _cpu._cpuH.RW(src) ));
        }
     };
    
    /******************************************
     subb	 rbd,rbs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr Z82_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = SUBB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     sub	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z83_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = SUBW( RW(dst), RW(src) );
        }
     };
    
    /******************************************
     orb	 rbd,rbs
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z84_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ORB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     or 	 rd,rs
     flags:  CZS---
     ******************************************/
     public OpcodePtr Z85_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, ORW( _cpu._cpuH.RW(dst), _cpu._cpuH.RW(src) ));
        }
     };
    
    /******************************************
     andb	 rbd,rbs
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z86_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ANDB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     and	 rd,rs
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z87_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = ANDW( RW(dst), RW(src) );
        }
     };
    
    /******************************************
     xorb	 rbd,rbs
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z88_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = XORB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     xor	 rd,rs
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z89_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = XORW( RW(dst), RW(src) );
        }
     };
    
    /******************************************
     cpb	 rbd,rbs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8A_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	CPB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     cp 	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8B_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            CPW( _cpu._cpuH.RW(dst), _cpu._cpuH.RW(src) );
        }
     };
    
    /******************************************
     comb	 rbd
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z8C_dddd_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RB(dst, COMB( _cpu._cpuH.RB(dst) ));
        }
     };
    
    /******************************************
     negb	 rbd
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8C_dddd_0010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RB(dst) = NEGB( RB(dst) );
        }
     };
    
    /******************************************
     testb	 rbd
     flags:  -ZSP--
     ******************************************/
     public OpcodePtr Z8C_dddd_0100 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	TESTB( RB(dst) );
        }
     };
    
    /******************************************
     tsetb	 rbd
     flags:  --S---
     ******************************************/
     public OpcodePtr Z8C_dddd_0110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///    if (RB(dst) & S08) SET_S; else CLR_S;
    /*TODO*///    RB(dst) = 0xff;
        }
     };
    
    /******************************************
     clrb	 rbd
     flags:  ------
     ******************************************/
     public OpcodePtr Z8C_dddd_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RB(dst, 0);
        }
     };
    
    /******************************************
     nop
     flags:  ------
     ******************************************/
     public OpcodePtr Z8D_0000_0111 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	/* nothing */
        }
     };
    
    /******************************************
     com	 rd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z8D_dddd_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RW(dst) = COMW( RW(dst) );
        }
     };
    
    /******************************************
     neg	 rd
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8D_dddd_0010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RW(dst) = NEGW( RW(dst) );
        }
     };
    
    /******************************************
     test	 rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z8D_dddd_0100 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            TESTW( _cpu._cpuH.RW(dst) );
        }
     };
    
    /******************************************
     tset	 rd
     flags:  --S---
     ******************************************/
     public OpcodePtr Z8D_dddd_0110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///    if (RW(dst) & S16) SET_S; else CLR_S;
    /*TODO*///    RW(dst) = 0xffff;
        }
     };
    
    /******************************************
     clr	 rd
     flags:  ------
     ******************************************/
     public OpcodePtr Z8D_dddd_1000 = new OpcodePtr() {
        @Override
        public void handler() {            
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RW(dst, 0);
        }
     };
    
    /******************************************
     setflg  imm4
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8D_imm4_0001 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	FCW |= Z.op[0] & 0x00f0;
        }
     };
    
    /******************************************
     resflg  imm4
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z8D_imm4_0011 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	FCW &= ~(Z.op[0] & 0x00f0);
        }
     };
    
    /******************************************
     comflg  flags
     flags:  CZSP--
     ******************************************/
     public OpcodePtr Z8D_imm4_0101 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	FCW ^= (Z.op[0] & 0x00f0);
        }
     };
    
    /******************************************
     ext8e	 imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z8E_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: ext8e  $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ext8f	 imm8
     flags:  ------
     ******************************************/
     public OpcodePtr Z8F_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: ext8f  $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     cpl	 rrd,rrs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z90_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    CPL( RL(dst), RL(src) );
        }
     };
    
    /******************************************
     pushl	 @rd,rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z91_ddN0_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB3);
    /*TODO*///    GET_DST(OP0,NIB2);
    /*TODO*///    PUSHL( dst, RL(src) );
        }
     };
    
    /******************************************
     subl	 rrd,rrs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z92_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    RL(dst) = SUBL( RL(dst), RL(src) );
        }
     };
    
    /******************************************
     push	 @rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z93_ddN0_ssss = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	PUSHW(dst, RW(src));
        }
     };
    
    /******************************************
     ldl	 rrd,rrs
     flags:  ------
     ******************************************/
     public OpcodePtr Z94_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RL(dst, _cpu._cpuH.RL(src));
        }
     };
    
    /******************************************
     popl	 rrd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z95_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RL(dst, POPL( src ));
        }
     };
    
    /******************************************
     addl	 rrd,rrs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z96_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RL(dst) = ADDL( RL(dst), RL(src) );
        }
     };
    
    /******************************************
     pop	 rd,@rs
     flags:  ------
     ******************************************/
     public OpcodePtr Z97_ssN0_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    RW(dst) = POPW( src );
        }
     };
    
    /******************************************
     multl	 rqd,rrs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z98_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///	RQ(dst) = MULTL( RQ(dst), RL(src) );
        }
     };
    
    /******************************************
     mult	 rrd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z99_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RL(dst, MULTW( _cpu._cpuH.RL(dst), _cpu._cpuH.RW(src) ));
        }
     };
    
    /******************************************
     divl	 rqd,rrs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z9A_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    RQ(dst) = DIVL( RQ(dst), RL(src) );
        }
     };
    
    /******************************************
     div	 rrd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr Z9B_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_DST(OP0,NIB3);
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    RL(dst) = DIVW( RL(dst), RW(src) );
        }
     };
    
    /******************************************
     testl	 rrd
     flags:  -ZS---
     ******************************************/
     public OpcodePtr Z9C_dddd_1000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	CLR_ZS;
    /*TODO*///	if (!RL(dst)) SET_Z;
    /*TODO*///    else if (RL(dst) & S32) SET_S;
        }
     };
    
    /******************************************
     rsvd9d
     flags:  ------
     ******************************************/
     public OpcodePtr Z9D_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd9d $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ret	 cc
     flags:  ------
     ******************************************/
     public OpcodePtr Z9E_0000_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
    	_cpu._cpuH.GET_CCC(OP0,NIB3);
    	switch (cc) {
    		case  0: if (_cpu._cpuH.CC0() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  1: if (_cpu._cpuH.CC1() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  2: if (_cpu._cpuH.CC2() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  3: if (_cpu._cpuH.CC3() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  4: if (_cpu._cpuH.CC4() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  5: if (_cpu._cpuH.CC5() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  6: if (_cpu._cpuH.CC6() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  7: if (_cpu._cpuH.CC7() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  8: if (_cpu._cpuH.CC8() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case  9: if (_cpu._cpuH.CC9() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 10: if (_cpu._cpuH.CCA() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 11: if (_cpu._cpuH.CCB() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 12: if (_cpu._cpuH.CCC() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 13: if (_cpu._cpuH.CCD() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 14: if (_cpu._cpuH.CCE() != 0) _cpu.Z.pc = POPW( SP ); break;
    		case 15: if (_cpu._cpuH.CCF() != 0) _cpu.Z.pc = POPW( SP ); break;
    	}
    	change_pc16bew(_cpu.Z.pc);
        }
     };
    
    /******************************************
     rsvd9f
     flags:  ------
     ******************************************/
     public OpcodePtr Z9F_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvd9f $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
        }
     };
    
    /******************************************
     ldb	 rbd,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr ZA0_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RB(dst, _cpu._cpuH.RB(src));
        }
     };
    
    /******************************************
     ld 	 rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr ZA1_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB3);
            _cpu._cpuH.GET_SRC(OP0,NIB2);
            _cpu._cpuH.RW(dst, _cpu._cpuH.RW(src));
        }
     };
    
    /******************************************
     resb	 rbd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr ZA2_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RB(dst) &= ~bit;
        }
     };
    
    /******************************************
     res	 rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr ZA3_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RW(dst) &= ~bit;
        }
     };
    
    /******************************************
     setb	 rbd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr ZA4_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RB(dst) |= bit;
        }
     };
    
    /******************************************
     set	 rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr ZA5_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RW(dst) |= bit;
        }
     };
    
    /******************************************
     bitb	 rbd,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZA6_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_BIT(OP0);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	if (RB(dst) & bit) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     bit	 rd,imm4
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZA7_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_BIT(OP0);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            if ((_cpu._cpuH.RW(dst) & bit) != 0) _cpu._cpuH.CLR_Z(); else _cpu._cpuH.SET_Z();
        }
     };
    
    /******************************************
     incb	 rbd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZA8_dddd_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_I4M1(OP0,NIB3);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RB(dst, INCB( _cpu._cpuH.RB(dst), i4p1));
        }
     };
    
    /******************************************
     inc	 rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZA9_dddd_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_I4M1(OP0,NIB3);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RW(dst, INCW( _cpu._cpuH.RW(dst), i4p1 ));
        }
     };
    
    /******************************************
     decb	 rbd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZAA_dddd_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_I4M1(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RB(dst) = DECB( RB(dst), i4p1 );
        }
     };
    
    /******************************************
     dec	 rd,imm4m1
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZAB_dddd_imm4m1 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_I4M1(OP0,NIB3);
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RW(dst, DECW( _cpu._cpuH.RW(dst), i4p1 ));
        }
     };
    
    /******************************************
     exb	 rbd,rbs
     flags:  ------
     ******************************************/
     public OpcodePtr ZAC_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	UINT8 tmp = RB(src);
    /*TODO*///	RB(src) = RB(dst);
    /*TODO*///	RB(dst) = tmp;
        }
     };
    
    /******************************************
     ex 	 rd,rs
     flags:  ------
     ******************************************/
     public OpcodePtr ZAD_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	UINT16 tmp = RW(src);
    /*TODO*///	RW(src) = RW(dst);
    /*TODO*///	RW(dst) = tmp;
        }
     };
    
    /******************************************
     tccb	 cc,rbd
     flags:  ------
     ******************************************/
     public OpcodePtr ZAE_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_CCC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	UINT8 tmp = RB(dst) & ~1;
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) tmp |= 1; break;
    /*TODO*///		case  1: if (CC1 != 0) tmp |= 1; break;
    /*TODO*///		case  2: if (CC2 != 0) tmp |= 1; break;
    /*TODO*///		case  3: if (CC3 != 0) tmp |= 1; break;
    /*TODO*///		case  4: if (CC4 != 0) tmp |= 1; break;
    /*TODO*///		case  5: if (CC5 != 0) tmp |= 1; break;
    /*TODO*///		case  6: if (CC6 != 0) tmp |= 1; break;
    /*TODO*///		case  7: if (CC7 != 0) tmp |= 1; break;
    /*TODO*///		case  8: if (CC8 != 0) tmp |= 1; break;
    /*TODO*///		case  9: if (CC9 != 0) tmp |= 1; break;
    /*TODO*///		case 10: if (CCA != 0) tmp |= 1; break;
    /*TODO*///		case 11: if (CCB != 0) tmp |= 1; break;
    /*TODO*///		case 12: if (CCC != 0) tmp |= 1; break;
    /*TODO*///		case 13: if (CCD != 0) tmp |= 1; break;
    /*TODO*///		case 14: if (CCE != 0) tmp |= 1; break;
    /*TODO*///		case 15: if (CCF != 0) tmp |= 1; break;
    /*TODO*///    }
    /*TODO*///	RB(dst) = tmp;
        }
     };
    
    /******************************************
     tcc	 cc,rd
     flags:  ------
     ******************************************/
     public OpcodePtr ZAF_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_CCC(OP0,NIB3);
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	UINT16 tmp = RW(dst) & ~1;
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) tmp |= 1; break;
    /*TODO*///		case  1: if (CC1 != 0) tmp |= 1; break;
    /*TODO*///		case  2: if (CC2 != 0) tmp |= 1; break;
    /*TODO*///		case  3: if (CC3 != 0) tmp |= 1; break;
    /*TODO*///		case  4: if (CC4 != 0) tmp |= 1; break;
    /*TODO*///		case  5: if (CC5 != 0) tmp |= 1; break;
    /*TODO*///		case  6: if (CC6 != 0) tmp |= 1; break;
    /*TODO*///		case  7: if (CC7 != 0) tmp |= 1; break;
    /*TODO*///		case  8: if (CC8 != 0) tmp |= 1; break;
    /*TODO*///		case  9: if (CC9 != 0) tmp |= 1; break;
    /*TODO*///		case 10: if (CCA != 0) tmp |= 1; break;
    /*TODO*///		case 11: if (CCB != 0) tmp |= 1; break;
    /*TODO*///		case 12: if (CCC != 0) tmp |= 1; break;
    /*TODO*///		case 13: if (CCD != 0) tmp |= 1; break;
    /*TODO*///		case 14: if (CCE != 0) tmp |= 1; break;
    /*TODO*///		case 15: if (CCF != 0) tmp |= 1; break;
    /*TODO*///    }
    /*TODO*///	RW(dst) = tmp;
        }
     };
    
    /******************************************
     dab	 rbd
     flags:  CZS---
     ******************************************/
     public OpcodePtr ZB0_dddd_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	UINT8 result;
    /*TODO*///	UINT16 idx = RB(dst);
    /*TODO*///	if ((FCW & F_C) != 0)	idx |= 0x100;
    /*TODO*///	if ((FCW & F_H) != 0)	idx |= 0x200;
    /*TODO*///	if ((FCW & F_DA) != 0) idx |= 0x400;
    /*TODO*///	result = Z8000_dab[idx];
    /*TODO*///	CLR_CZS;
    /*TODO*///	CHK_XXXB_ZS;
    /*TODO*///	if (Z8000_dab[idx] & 0x100) SET_C;
    /*TODO*///	RB(dst) = result;
        }
     };
    
    /******************************************
     extsb	 rd
     flags:  ------
     ******************************************/
     public OpcodePtr ZB1_dddd_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.RW(dst, (_cpu._cpuH.RW(dst) & 0xff) | ((_cpu._cpuH.RW(dst) & S08)!=0 ? 0xff00 : 0x0000));
        }
     };
    
    /******************************************
     extsl	 rqd
     flags:  ------
     ******************************************/
     public OpcodePtr ZB1_dddd_0111 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	RQ(dst) = COMBINE_U64_U32_U32( (RQ(dst) & S32) ?
    /*TODO*///		0xfffffffful : 0, LO32_U32_U64(RQ(dst)));
        }
     };
    
    /******************************************
     exts	 rrd
     flags:  ------
     ******************************************/
     public OpcodePtr ZB1_dddd_1010 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///    RL(dst) = (RL(dst) & 0xffff) | ((RL(dst) & S16) ?
    /*TODO*///		0xffff0000ul : 0x00000000ul);
        }
     };
    
    /******************************************
     sllb	 rbd,imm8
     flags:  CZS---
     srlb	 rbd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB2_dddd_0001_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	if ((imm16 & S16) != 0)
    /*TODO*///		RB(dst) = SRLB( RB(dst), -(INT16)imm16 );
    /*TODO*///	else
    /*TODO*///		RB(dst) = SLLB( RB(dst), imm16 );
        }
     };
    
    /******************************************
     sdlb	 rbd,rs
     flags:  CZS---
     ******************************************/
     public OpcodePtr ZB2_dddd_0011_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RB(dst) = SRLB( RB(dst), (INT8)RW(src) );
        }
     };
    
    /******************************************
     rlb	 rbd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB2_dddd_00I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RB(dst) = RLB( RB(dst), imm1 );
        }
     };
    
    /******************************************
     rrb	 rbd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB2_dddd_01I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RB(dst) = RRB( RB(dst), imm1 );
        }
     };
    
    /******************************************
     slab	 rbd,imm8
     flags:  CZSV--
     srab	 rbd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB2_dddd_1001_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	if ((imm16 & S16) != 0)
    /*TODO*///		RB(dst) = SRAB( RB(dst), -(INT16)imm16 );
    /*TODO*///	else
    /*TODO*///		RB(dst) = SLAB( RB(dst), imm16 );
        }
     };
    
    /******************************************
     sdab	 rbd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB2_dddd_1011_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RB(dst) = SDAB( RB(dst), (INT8) RW(src) );
        }
     };
    
    /******************************************
     rlcb	 rbd,imm1or2
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZB2_dddd_10I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RB(dst) = RLCB( RB(dst), imm1 );
        }
     };
    
    /******************************************
     rrcb	 rbd,imm1or2
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZB2_dddd_11I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RB(dst) = RRCB( RB(dst), imm1 );
        }
     };
    
    /******************************************
     sll	 rd,imm8
     flags:  CZS---
     srl	 rd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_0001_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.GET_IMM16(OP1);
            if ((imm16 & S16) != 0)
                    _cpu._cpuH.RW(dst, SRLW( _cpu._cpuH.RW(dst), -imm16 ));
            else
                _cpu._cpuH.RW(dst, SLLW( _cpu._cpuH.RW(dst), imm16 ));
        }
     };
    
    /******************************************
     sdl	 rd,rs
     flags:  CZS---
     ******************************************/
     public OpcodePtr ZB3_dddd_0011_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RW(dst) = SDLW( RW(dst), (INT8)RW(src) );
        }
     };
    
    /******************************************
     rl 	 rd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_00I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RW(dst) = RLW( RW(dst), imm1 );
        }
     };
    
    /******************************************
     slll	 rrd,imm8
     flags:  CZS---
     srll	 rrd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_0101_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB2);
            _cpu._cpuH.GET_IMM16(OP1);
            if ((imm16 & S16) != 0)
                _cpu._cpuH.RL(dst, SRLL( _cpu._cpuH.RL(dst), -imm16 ));
            else
                _cpu._cpuH.RL(dst, SLLL( _cpu._cpuH.RL(dst), imm16 ));
        }
     };
    
    /******************************************
     sdll	 rrd,rs
     flags:  CZS---
     ******************************************/
     public OpcodePtr ZB3_dddd_0111_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RL(dst) = SDLL( RL(dst), RW(src) & 0xff );
        }
     };
    
    /******************************************
     rr 	 rd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_01I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RW(dst) = RRW( RW(dst), imm1 );
        }
     };
    
    /******************************************
     sla	 rd,imm8
     flags:  CZSV--
     sra	 rd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_1001_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	if ((imm16 & S16) != 0)
    /*TODO*///		RW(dst) = SRAW( RW(dst), -(INT16)imm16 );
    /*TODO*///	else
    /*TODO*///        RW(dst) = SLAW( RW(dst), imm16 );
        }
     };
    
    /******************************************
     sda	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_1011_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RW(dst) = SDAW( RW(dst), (INT8)RW(src) );
        }
     };
    
    /******************************************
     rlc	 rd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_10I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RW(dst) = RLCW( RW(dst), imm1 );
        }
     };
    
    /******************************************
     slal	 rrd,imm8
     flags:  CZSV--
     sral	 rrd,imm8
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_1101_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM16(OP1);
    /*TODO*///	if ((imm16 & S16) != 0)
    /*TODO*///		RL(dst) = SRAL( RL(dst), -(INT16)imm16 );
    /*TODO*///	else
    /*TODO*///		RL(dst) = SLAL( RL(dst), imm16 );
        }
     };
    
    /******************************************
     sdal	 rrd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_1111_0000_ssss_0000_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB1);
    /*TODO*///	RL(dst) = SDAL( RL(dst), RW(src) & 0xff );
        }
     };
    
    /******************************************
     rrc	 rd,imm1or2
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB3_dddd_11I0 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM1(OP0,NIB3);
    /*TODO*///	RW(dst) = RRCW( RW(dst), imm1 );
        }
     };
    
    /******************************************
     adcb	 rbd,rbs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr ZB4_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = ADCB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     adc	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB5_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = ADCW( RW(dst), RW(src) );
        }
     };
    
    /******************************************
     sbcb	 rbd,rbs
     flags:  CZSVDH
     ******************************************/
     public OpcodePtr ZB6_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RB(dst) = SBCB( RB(dst), RB(src) );
        }
     };
    
    /******************************************
     sbc	 rd,rs
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZB7_ssss_dddd = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB3);
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	RW(dst) = SBCW( RW(dst), RW(src) );
        }
     };
    
    /******************************************
     trtib	 @rd,@rs,rr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_0010_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	RB(2) = xlt;
    /*TODO*///	if (xlt != 0) CLR_Z; else SET_Z;
    /*TODO*///	RW(dst)++;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     trtirb  @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_0110_0000_rrrr_ssN0_1110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	RB(2) = xlt;
    /*TODO*///	if (xlt != 0) CLR_Z; else SET_Z;
    /*TODO*///	RW(dst)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     trtdb	 @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_1010_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	RB(2) = xlt;
    /*TODO*///	if (xlt != 0) CLR_Z; else SET_Z;
    /*TODO*///    RW(dst)--;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     trtdrb  @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_1110_0000_rrrr_ssN0_1110 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	RB(2) = xlt;
    /*TODO*///	if (xlt != 0) CLR_Z; else SET_Z;
    /*TODO*///    RW(dst)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     trib	 @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_0000_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	WRMEM_B( RW(dst), xlt );
    /*TODO*///	RW(dst)++;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     trirb	 @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_0100_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	WRMEM_B( RW(dst), xlt );
    /*TODO*///	RW(dst)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     trdb	 @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_1000_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	WRMEM_B( RW(dst), xlt );
    /*TODO*///    RW(dst)--;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     trdrb	 @rd,@rs,rbr
     flags:  -ZSV--
     ******************************************/
     public OpcodePtr ZB8_ddN0_1100_0000_rrrr_ssN0_0000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_SRC(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	UINT8 xlt = RDMEM_B( (UINT16)(RW(src) + RDMEM_B(RW(dst))) );
    /*TODO*///	WRMEM_B( RW(dst), xlt );
    /*TODO*///    RW(dst)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     rsvdb9
     flags:  ------
     ******************************************/
     public OpcodePtr ZB9_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvdb9 $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
    /*TODO*///	(void)imm8;
        }
     };
    
    /******************************************
     cpib	 rbd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_0000_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPB( RB(dst), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(src)++;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     ldib	 @rd,@rs,rr
     ldibr	 @rd,@rs,rr
     flags:  ---V--
     ******************************************/
     public OpcodePtr ZBA_ssN0_0001_0000_rrrr_ddN0_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);	/* repeat? */
    /*TODO*///    WRMEM_B( RW(dst), RDMEM_B(RW(src)) );
    /*TODO*///	RW(dst)++;
    /*TODO*///	RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsib	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_0010_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPB( RDMEM_B(RW(dst)), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(dst)++;
    /*TODO*///	RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpirb	 rbd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_0100_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPB( RB(dst), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsirb  @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_0110_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPB( RDMEM_B(RW(dst)), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(dst)++;
    /*TODO*///	RW(src)++;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpdb	 rbd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_1000_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///    CPB( RB(dst), RDMEM_B(RW(src)) );
    /*TODO*///    switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(src)--;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     lddb	 @rs,@rd,rr
     lddbr	 @rs,@rd,rr
     flags:  ---V--
     ******************************************/
     public OpcodePtr ZBA_ssN0_1001_0000_rrrr_ddN0_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_B( RW(dst), RDMEM_B(RW(src)) );
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsdb	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_1010_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///    CPB( RDMEM_B(RW(dst)), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     cpdrb	 rbd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_1100_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPB( RB(dst), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsdrb  @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBA_ssN0_1110_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///    CPB( RDMEM_B(RW(dst)), RDMEM_B(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///	}
    /*TODO*///	RW(dst)--;
    /*TODO*///	RW(src)--;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpi	 rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_0000_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///    switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     ldi	 @rd,@rs,rr
     ldir	 @rd,@rs,rr
     flags:  ---V--
     ******************************************/
     public OpcodePtr ZBB_ssN0_0001_0000_rrrr_ddN0_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	WRMEM_W( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsi	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_0010_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RDMEM_W(RW(dst)), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(dst) += 2;
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     cpir	 rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_0100_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsir	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_0110_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RDMEM_W(RW(dst)), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///	RW(dst) += 2;
    /*TODO*///    RW(src) += 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpd	 rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_1000_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///    switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     ldd     @rs,@rd,rr
     lddr	 @rs,@rd,rr
     flags:  ---V--
     ******************************************/
     public OpcodePtr ZBB_ssN0_1001_0000_rrrr_ddN0_x000 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///    GET_SRC(OP0,NIB2);
    /*TODO*///    GET_CNT(OP1,NIB1);
    /*TODO*///    GET_DST(OP1,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///    WRMEM_W( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///    RW(dst) -= 2;
    /*TODO*///    RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (cc == 0) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsd	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_1010_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RDMEM_W(RW(dst)), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(dst) -= 2;
    /*TODO*///    RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) CLR_V; else SET_V;
        }
     };
    
    /******************************************
     cpdr	 rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_1100_0000_rrrr_dddd_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RW(dst), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     cpsdr	 @rd,@rs,rr,cc
     flags:  CZSV--
     ******************************************/
     public OpcodePtr ZBB_ssN0_1110_0000_rrrr_ddN0_cccc = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_SRC(OP0,NIB2);
    /*TODO*///	GET_CCC(OP1,NIB3);
    /*TODO*///	GET_DST(OP1,NIB2);
    /*TODO*///	GET_CNT(OP1,NIB1);
    /*TODO*///	CPW( RDMEM_W(RW(dst)), RDMEM_W(RW(src)) );
    /*TODO*///	switch (cc) {
    /*TODO*///		case  0: if (CC0 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  1: if (CC1 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  2: if (CC2 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  3: if (CC3 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  4: if (CC4 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  5: if (CC5 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  6: if (CC6 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  7: if (CC7 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  8: if (CC8 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case  9: if (CC9 != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 10: if (CCA != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 11: if (CCB != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 12: if (CCC != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 13: if (CCD != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 14: if (CCE != 0) SET_Z; else CLR_Z; break;
    /*TODO*///		case 15: if (CCF != 0) SET_Z; else CLR_Z; break;
    /*TODO*///    }
    /*TODO*///    RW(dst) -= 2;
    /*TODO*///    RW(src) -= 2;
    /*TODO*///	if (--RW(cnt)) { CLR_V; if (!(FCW & F_Z)) PC -= 4; } else SET_V;
        }
     };
    
    /******************************************
     rrdb	 rbb,rba
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZBC_aaaa_bbbb = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	UINT8 b = Z.op[0] & 15;
    /*TODO*///	UINT8 a = (Z.op[0] >> 4) & 15;
    /*TODO*///	UINT8 tmp = RB(b);
    /*TODO*///	RB(a) = (RB(a) >> 4) | (RB(b) << 4);
    /*TODO*///	RB(b) = (RB(b) & 0xf0) | (tmp & 0x0f);
    /*TODO*///    if (RB(b)) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     ldk	 rd,imm4
     flags:  ------
     ******************************************/
     public OpcodePtr ZBD_dddd_imm4 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB2);
    /*TODO*///	GET_IMM4(OP0,NIB3);
    /*TODO*///	RW(dst) = imm4;
        }
     };
    
    /******************************************
     rldb	 rbb,rba
     flags:  -Z----
     ******************************************/
     public OpcodePtr ZBE_aaaa_bbbb = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	UINT8 b = Z.op[0] & 15;
    /*TODO*///	UINT8 a = (Z.op[0] >> 4) & 15;
    /*TODO*///	UINT8 tmp = RB(a);
    /*TODO*///	RB(a) = (RB(a) << 4) | (RB(b) & 0x0f);
    /*TODO*///	RB(b) = (RB(b) & 0xf0) | (tmp >> 4);
    /*TODO*///	if (RB(b)) CLR_Z; else SET_Z;
        }
     };
    
    /******************************************
     rsvdbf
     flags:  ------
     ******************************************/
     public OpcodePtr ZBF_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_IMM8(0);
    /*TODO*///	LOG(("Z8K#%d %04x: rsvdbf $%02x\n", cpu_getactivecpu(), PC, imm8));
    /*TODO*///    if ((FCW & F_EPU) != 0) {
    /*TODO*///		/* Z8001 EPU code goes here */
    /*TODO*///		(void)imm8;
    /*TODO*///    }
    /*TODO*///	(void)imm8;
        }
     };
    
    /******************************************
     ldb	 rbd,imm8
     flags:  ------
     ******************************************/
     public OpcodePtr ZC_dddd_imm8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB1);
            _cpu._cpuH.GET_IMM8(0);
            _cpu._cpuH.RB(dst, imm8);
        }
     };
    
    /******************************************
     calr	 dsp12
     flags:  ------
     ******************************************/
     public OpcodePtr ZD_dsp12 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	INT16 dsp12 = Z.op[0] & 0xfff;
    /*TODO*///	PUSHW( SP, PC );
    /*TODO*///	dsp12 = (dsp12 & 2048) ? 4096 -2 * (dsp12 & 2047) : -2 * (dsp12 & 2047);
    /*TODO*///	PC += dsp12;
    /*TODO*///	change_pc16bew(PC);
        }
     };
    
    /******************************************
     jr 	 cc,dsp8
     flags:  ------
     ******************************************/
     public OpcodePtr ZE_cccc_dsp8 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DSP8();
            _cpu._cpuH.GET_CCC(OP0,NIB1);
            switch (cc) {
                    case  0: if (_cpu._cpuH.CC0() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  1: if (_cpu._cpuH.CC1() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  2: if (_cpu._cpuH.CC2() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  3: if (_cpu._cpuH.CC3() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  4: if (_cpu._cpuH.CC4() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  5: if (_cpu._cpuH.CC5() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  6: if (_cpu._cpuH.CC6() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  7: if (_cpu._cpuH.CC7() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  8: if (_cpu._cpuH.CC8() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case  9: if (_cpu._cpuH.CC9() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 10: if (_cpu._cpuH.CCA() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 11: if (_cpu._cpuH.CCB() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 12: if (_cpu._cpuH.CCC() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 13: if (_cpu._cpuH.CCD() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 14: if (_cpu._cpuH.CCE() != 0) _cpu.Z.pc += dsp8 * 2; break;
                    case 15: if (_cpu._cpuH.CCF() != 0) _cpu.Z.pc += dsp8 * 2; break;
            }
            change_pc16bew(_cpu.Z.pc);
        }
     };
    
    /******************************************
     dbjnz   rbd,dsp7
     flags:  ------
     ******************************************/
     public OpcodePtr ZF_dddd_0dsp7 = new OpcodePtr() {
        @Override
        public void handler() {
            throw new UnsupportedOperationException("unsupported");
    /*TODO*///	GET_DST(OP0,NIB1);
    /*TODO*///    GET_DSP7;
    /*TODO*///    RB(dst) -= 1;
    /*TODO*///    if (RB(dst)) {
    /*TODO*///        PC = PC - 2 * dsp7;
    /*TODO*///        change_pc16bew(PC);
    /*TODO*///    }
        }
     };
    
    /******************************************
     djnz	 rd,dsp7
     flags:  ------
     ******************************************/
     public OpcodePtr ZF_dddd_1dsp7 = new OpcodePtr() {
        @Override
        public void handler() {
            //throw new UnsupportedOperationException("unsupported");
            _cpu._cpuH.GET_DST(OP0,NIB1);
            _cpu._cpuH.GET_DSP7();
            _cpu._cpuH.RW(dst,  _cpu._cpuH.RW(dst)-1);
            if (_cpu._cpuH.RW(dst) != 0) {
                    _cpu.Z.pc = _cpu.Z.pc - 2 * dsp7;
                    change_pc16bew(_cpu.Z.pc);
            }
        }
     };
    
    
}
