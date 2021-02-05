/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;

public class modrmH {

    public static _Mod_RM Mod_RM = new _Mod_RM();

    static class _Mod_RM {

        public _reg reg = new _reg();
        public _RM RM = new _RM();

        static class _RM {

            public int[] w = new int[256];
            public int[] b = new int[256];
        }

        static class _reg {

            public int[] w = new int[256];
            public int[] b = new int[256];
        }
    }

    public static final int RegWord(int ModRM) {
        return I.regs.w[Mod_RM.reg.w[ModRM]];
    }

    public static final int RegByte(int ModRM) {
        return I.regs.b[Mod_RM.reg.b[ModRM]];
    }

    /*TODO*///#define GetRMWord(ModRM) \
/*TODO*///	((ModRM) >= 0xc0 ? I.regs.w[Mod_RM.RM.w[ModRM]] : ( (*GetEA[ModRM])(), ReadWord( EA ) ))
/*TODO*///
/*TODO*///#define PutbackRMWord(ModRM,val) \
/*TODO*///{ \
/*TODO*///	if (ModRM >= 0xc0) I.regs.w[Mod_RM.RM.w[ModRM]]=val; \
/*TODO*///    else WriteWord(EA,val); \
/*TODO*///}
/*TODO*///
/*TODO*///#define GetnextRMWord ReadWord(EA+2)
/*TODO*///
/*TODO*///#define GetRMWordOffset(offs) \
/*TODO*///		ReadWord(EA-EO+(UINT16)(EO+offs))
/*TODO*///
/*TODO*///#define GetRMByteOffset(offs) \
/*TODO*///		ReadByte(EA-EO+(UINT16)(EO+offs))
/*TODO*///
/*TODO*///#define PutRMWord(ModRM,val)				\
/*TODO*///{											\
/*TODO*///	if (ModRM >= 0xc0)						\
/*TODO*///		I.regs.w[Mod_RM.RM.w[ModRM]]=val;	\
/*TODO*///	else {									\
/*TODO*///		(*GetEA[ModRM])();					\
/*TODO*///		WriteWord( EA ,val);				\
/*TODO*///	}										\
/*TODO*///}
/*TODO*///
/*TODO*///#define PutRMWordOffset(offs, val) \
/*TODO*///		WriteWord( EA-EO+(UINT16)(EO+offs), val)
/*TODO*///
/*TODO*///#define PutRMByteOffset(offs, val) \
/*TODO*///		WriteByte( EA-EO+(UINT16)(EO+offs), val)
/*TODO*///
/*TODO*///#define PutImmRMWord(ModRM) 				\
/*TODO*///{											\
/*TODO*///	WORD val;								\
/*TODO*///	if (ModRM >= 0xc0)						\
/*TODO*///		FETCHWORD(I.regs.w[Mod_RM.RM.w[ModRM]]) \
/*TODO*///	else {									\
/*TODO*///		(*GetEA[ModRM])();					\
/*TODO*///		FETCHWORD(val)						\
/*TODO*///		WriteWord( EA , val);				\
/*TODO*///	}										\
/*TODO*///}
/*TODO*///	
/*TODO*///#define GetRMByte(ModRM) \
/*TODO*///	((ModRM) >= 0xc0 ? I.regs.b[Mod_RM.RM.b[ModRM]] : ReadByte( (*GetEA[ModRM])() ))
/*TODO*///
/*TODO*///#define PutRMByte(ModRM,val)				\
/*TODO*///{											\
/*TODO*///	if (ModRM >= 0xc0)						\
/*TODO*///		I.regs.b[Mod_RM.RM.b[ModRM]]=val;	\
/*TODO*///	else									\
/*TODO*///		WriteByte( (*GetEA[ModRM])() ,val); \
/*TODO*///}
/*TODO*///
/*TODO*///#define PutImmRMByte(ModRM) 				\
/*TODO*///{											\
/*TODO*///	if (ModRM >= 0xc0)						\
/*TODO*///		I.regs.b[Mod_RM.RM.b[ModRM]]=FETCH; \
/*TODO*///	else {									\
/*TODO*///		(*GetEA[ModRM])();					\
/*TODO*///		WriteByte( EA , FETCH );			\
/*TODO*///	}										\
/*TODO*///}
/*TODO*///	
/*TODO*///#define PutbackRMByte(ModRM,val)			\
/*TODO*///{											\
/*TODO*///	if (ModRM >= 0xc0)						\
/*TODO*///		I.regs.b[Mod_RM.RM.b[ModRM]]=val;	\
/*TODO*///	else									\
/*TODO*///		WriteByte(EA,val);					\
/*TODO*///}
/*TODO*///
/*TODO*///#define DEF_br8(dst,src)					\
/*TODO*///	unsigned ModRM = FETCHOP;				\
/*TODO*///	unsigned src = RegByte(ModRM);			\
/*TODO*///    unsigned dst = GetRMByte(ModRM)
/*TODO*///
/*TODO*///#define DEF_wr16(dst,src)					\
/*TODO*///	unsigned ModRM = FETCHOP;				\
/*TODO*///	unsigned src = RegWord(ModRM);			\
/*TODO*///    unsigned dst = GetRMWord(ModRM)
/*TODO*///
/*TODO*///#define DEF_r8b(dst,src)					\
/*TODO*///	unsigned ModRM = FETCHOP;				\
/*TODO*///	unsigned dst = RegByte(ModRM);			\
/*TODO*///    unsigned src = GetRMByte(ModRM)
/*TODO*///
/*TODO*///#define DEF_r16w(dst,src)					\
/*TODO*///	unsigned ModRM = FETCHOP;				\
/*TODO*///	unsigned dst = RegWord(ModRM);			\
/*TODO*///    unsigned src = GetRMWord(ModRM)
/*TODO*///
/*TODO*///#define DEF_ald8(dst,src)					\
/*TODO*///	unsigned src = FETCHOP; 				\
/*TODO*///	unsigned dst = I.regs.b[AL]
/*TODO*///
/*TODO*///#define DEF_axd16(dst,src)					\
/*TODO*///	unsigned src = FETCHOP; 				\
/*TODO*///	unsigned dst = I.regs.w[AX];			\
/*TODO*///    src += (FETCH << 8)
/*TODO*///    
}
