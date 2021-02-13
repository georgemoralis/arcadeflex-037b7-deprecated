/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class I86H {

    public static final int ES = 0, CS = 1, SS = 2, DS = 3;//typedef enum { ES, CS, SS, DS } SREGS;
    public static final int AX = 0, CX = 1, DX = 2, BX = 3, SP = 4, BP = 5, SI = 6, DI = 7;//typedef enum { AX, CX, DX, BX, SP, BP, SI, DI } WREGS;

    public static final int AL = 0, AH = 1, CL = 2, CH = 3, DL = 4, DH = 5, BL = 6, BH = 7, SPL = 8, SPH = 9, BPL = 10, BPH = 11, SIL = 12, SIH = 13, DIL = 14, DIH = 15;

    /*TODO*///
/*TODO*////* parameter x = result, y = source 1, z = source 2 */
/*TODO*///
/*TODO*///#define SetTF(x)			(I.TF = (x))
    public static void SetIF(int x) {
        I.IF = x;
    }

    public static void SetDF(int x) {
        I.DirVal = (x != 0) ? -1 : 1;
    }

    /*TODO*///
/*TODO*///#define SetOFW_Add(x,y,z)	(I.OverVal = ((x) ^ (y)) & ((x) ^ (z)) & 0x8000)
/*TODO*///#define SetOFB_Add(x,y,z)	(I.OverVal = ((x) ^ (y)) & ((x) ^ (z)) & 0x80)
/*TODO*///#define SetOFW_Sub(x,y,z)	(I.OverVal = ((z) ^ (y)) & ((z) ^ (x)) & 0x8000)
/*TODO*///#define SetOFB_Sub(x,y,z)	(I.OverVal = ((z) ^ (y)) & ((z) ^ (x)) & 0x80)
/*TODO*///
/*TODO*///#define SetCFB(x)			(I.CarryVal = (x) & 0x100)
/*TODO*///#define SetCFW(x)			(I.CarryVal = (x) & 0x10000)
/*TODO*///#define SetAF(x,y,z)		(I.AuxVal = ((x) ^ ((y) ^ (z))) & 0x10)
/*TODO*///#define SetSF(x)			(I.SignVal = (x))
/*TODO*///#define SetZF(x)			(I.ZeroVal = (x))
/*TODO*///#define SetPF(x)			(I.ParityVal = (x))
/*TODO*///
/*TODO*///#define SetSZPF_Byte(x) 	(I.ParityVal = I.SignVal = I.ZeroVal = (INT8)(x))
/*TODO*///#define SetSZPF_Word(x) 	(I.ParityVal = I.SignVal = I.ZeroVal = (INT16)(x))
/*TODO*///
/*TODO*///#define ADDB(dst,src) { unsigned res=dst+src; SetCFB(res); SetOFB_Add(res,src,dst); SetAF(res,src,dst); SetSZPF_Byte(res); dst=(BYTE)res; }
/*TODO*///#define ADDW(dst,src) { unsigned res=dst+src; SetCFW(res); SetOFW_Add(res,src,dst); SetAF(res,src,dst); SetSZPF_Word(res); dst=(WORD)res; }
/*TODO*///
/*TODO*///#define SUBB(dst,src) { unsigned res=dst-src; SetCFB(res); SetOFB_Sub(res,src,dst); SetAF(res,src,dst); SetSZPF_Byte(res); dst=(BYTE)res; }
/*TODO*///#define SUBW(dst,src) { unsigned res=dst-src; SetCFW(res); SetOFW_Sub(res,src,dst); SetAF(res,src,dst); SetSZPF_Word(res); dst=(WORD)res; }
/*TODO*///
/*TODO*///#define ORB(dst,src) 		dst |= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Byte(dst)
/*TODO*///#define ORW(dst,src) 		dst |= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Word(dst)
/*TODO*///
/*TODO*///#define ANDB(dst,src) 		dst &= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Byte(dst)
/*TODO*///#define ANDW(dst,src) 		dst &= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Word(dst)
/*TODO*///
/*TODO*///#define XORB(dst,src) 		dst ^= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Byte(dst)
/*TODO*///#define XORW(dst,src) 		dst ^= src; I.CarryVal = I.OverVal = I.AuxVal = 0; SetSZPF_Word(dst)
/*TODO*///
/*TODO*///#define CF					(I.CarryVal != 0)
/*TODO*///#define SF					(I.SignVal < 0)
/*TODO*///#define ZF					(I.ZeroVal == 0)
/*TODO*///#define PF					parity_table[I.ParityVal]
/*TODO*///#define AF					(I.AuxVal != 0)
/*TODO*///#define OF					(I.OverVal != 0)
/*TODO*///#define DF					(I.DirVal < 0)
/*TODO*///
/*TODO*////************************************************************************/
/*TODO*///
    static final int SegBase(int Seg) {
        return I.sregs[Seg] << 4;
    }

    /*TODO*///#define DefaultBase(Seg) 		((seg_prefix && (Seg == DS || Seg == SS)) ? prefix_base : I.base[Seg])
/*TODO*///
/*TODO*///#define GetMemB(Seg,Off)		(cpu_readmem20((DefaultBase(Seg) + (Off)) & AMASK))
/*TODO*///#define GetMemW(Seg,Off)		((WORD)GetMemB(Seg, Off) + (WORD)(GetMemB(Seg, (Off) + 1) << 8))
/*TODO*///#define PutMemB(Seg,Off,x)		cpu_writemem20((DefaultBase(Seg) + (Off)) & AMASK, (x))
/*TODO*///#define PutMemW(Seg,Off,x)		{ PutMemB(Seg, Off, (x) & 0xff); PutMemB(Seg, (Off) + 1, ((x) >> 8) & 0xff); }
/*TODO*///
/*TODO*///#define PEEKBYTE(ea) 			(cpu_readmem20((ea) & AMASK))
/*TODO*///#define ReadByte(ea) 			(cpu_readmem20((ea) & AMASK))
/*TODO*///#define ReadWord(ea)			(cpu_readmem20((ea) & AMASK) + (cpu_readmem20(((ea) + 1) & AMASK) << 8))
/*TODO*///#define WriteByte(ea,val)		cpu_writemem20((ea) & AMASK, val);
/*TODO*///#define WriteWord(ea,val)		{ cpu_writemem20((ea) & AMASK, (val) & 0xff); cpu_writemem20(((ea) + 1) & AMASK, ((val) >> 8) & 0xff); }
/*TODO*///
/*TODO*///#define read_port(port) 		cpu_readport(port)
/*TODO*///#define write_port(port,val) 	cpu_writeport(port,val)
/*TODO*///
    public static final int FETCH() {
        int i = cpu_readop_arg(I.pc);
        I.pc = (I.pc + 1);
        return i;
    }

    public static final int FETCHOP() {
        int i = cpu_readop(I.pc);
        I.pc = (I.pc + 1);
        return i;
    }
    /*TODO*///#define PEEKOP(addr)			(cpu_readop(addr)) 
/*TODO*///#define FETCHWORD(var) 			{ var = cpu_readop_arg(I.pc); var += (cpu_readop_arg(I.pc + 1) << 8); I.pc += 2; }
/*TODO*///#define CHANGE_PC(addr)			change_pc20(addr)
/*TODO*///#define PUSH(val)				{ I.regs.w[SP] -= 2; WriteWord(((I.base[SS] + I.regs.w[SP]) & AMASK), val); }
/*TODO*///#define POP(var)				{ var = ReadWord(((I.base[SS] + I.regs.w[SP]) & AMASK)); I.regs.w[SP] += 2; }
/*TODO*///
/*TODO*////************************************************************************/
/*TODO*///
/*TODO*///#define CompressFlags() (WORD)(CF | (PF << 2) | (AF << 4) | (ZF << 6) \
/*TODO*///				| (SF << 7) | (I.TF << 8) | (I.IF << 9) \
/*TODO*///				| (DF << 10) | (OF << 11))
/*TODO*///
/*TODO*///#define ExpandFlags(f) \
/*TODO*///{ \
/*TODO*///	  I.CarryVal = (f) & 1; \
/*TODO*///	  I.ParityVal = !((f) & 4); \
/*TODO*///	  I.AuxVal = (f) & 16; \
/*TODO*///	  I.ZeroVal = !((f) & 64); \
/*TODO*///	  I.SignVal = ((f) & 128) ? -1 : 0; \
/*TODO*///	  I.TF = ((f) & 256) >> 8; \
/*TODO*///	  I.IF = ((f) & 512) >> 9; \
/*TODO*///	  I.DirVal = ((f) & 1024) ? -1 : 1; \
/*TODO*///	  I.OverVal = (f) & 2048; \
/*TODO*///}    
}
