/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.eaH.*;
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

    public static final void SetRegWord(int ModRM, int val) {
        I.regs.SetW(Mod_RM.reg.w[ModRM], val);
    }

    public static final int RegByte(int ModRM) {
        return I.regs.b[Mod_RM.reg.b[ModRM]];
    }

    public static final void SetRegByte(int ModRM, int val) {
        I.regs.SetB(Mod_RM.reg.b[ModRM], val);
    }

    public static final int GetRMWord(int ModRM) {
        if (ModRM >= 0xc0) {
            return I.regs.w[Mod_RM.RM.w[ModRM]];
        } else {
            return ReadWord(GetEA[ModRM].handler());
        }
    }

    public static final void PutbackRMWord(int ModRM, int val) {
        if (ModRM >= 0xc0) {
            I.regs.SetW(Mod_RM.RM.w[ModRM], val);
        } else {
            WriteWord(EA, val);
        }
    }

     public static final int GetnextRMWord() {
        return ReadWord(EA + 2);
    }

/*TODO*///#define GetRMWordOffset(offs) \
/*TODO*///		ReadWord(EA-EO+(UINT16)(EO+offs))
/*TODO*///
/*TODO*///#define GetRMByteOffset(offs) \
/*TODO*///		ReadByte(EA-EO+(UINT16)(EO+offs))
/*TODO*///
    public static final void PutRMWord(int ModRM, int val) {
        if (ModRM >= 0xc0) {
            I.regs.SetW(Mod_RM.RM.w[ModRM], val);
        } else {
            GetEA[ModRM].handler();
            WriteWord(EA, val);
        }
    }

    /*TODO*///#define PutRMWordOffset(offs, val) \
/*TODO*///		WriteWord( EA-EO+(UINT16)(EO+offs), val)
/*TODO*///
/*TODO*///#define PutRMByteOffset(offs, val) \
/*TODO*///		WriteByte( EA-EO+(UINT16)(EO+offs), val)
/*TODO*///
    public static final void PutImmRMWord(int ModRM) {
        if (ModRM >= 0xc0) {
            I.regs.SetW(Mod_RM.RM.w[ModRM], FETCHWORD());
        } else {
            GetEA[ModRM].handler();
            int i = FETCHWORD();
            WriteWord(EA, i);
        }
    }

    public static final int GetRMByte(int ModRM) {
        if (ModRM >= 0xc0) {
            return I.regs.b[Mod_RM.RM.b[ModRM]];
        } else {
            return ReadByte(GetEA[ModRM].handler());
        }
    }

    public static final void PutRMByte(int ModRM, int val) {
        if (ModRM >= 0xc0) {
            I.regs.SetB(Mod_RM.RM.b[ModRM], val);
        } else {
            WriteByte(GetEA[ModRM].handler(), val);
        }
    }
    
    public static final void PutImmRMByte(int ModRM) {
        if (ModRM >= 0xc0) {
            I.regs.SetB(Mod_RM.RM.b[ModRM], FETCH());
        } else {
            GetEA[ModRM].handler();
            WriteByte(EA, FETCH());
        }
    }
    public static final void PutbackRMByte(int ModRM, int val) {
        if (ModRM >= 0xc0) {
            I.regs.SetB(Mod_RM.RM.b[ModRM], val);
        } else {
            WriteByte(EA, val);
        }
    }
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
