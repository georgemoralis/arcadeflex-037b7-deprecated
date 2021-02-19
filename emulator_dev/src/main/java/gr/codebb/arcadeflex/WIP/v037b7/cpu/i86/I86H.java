/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_readmem20;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_readport;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_writemem20;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_writeport;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.common.libc.expressions.BOOL;

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

    public static void SetOFW_Add(int x, int y, int z) {
        I.OverVal = ((x) ^ (y)) & ((x) ^ (z)) & 0x8000;
    }

    public static void SetOFB_Add(int x, int y, int z) {
        I.OverVal = ((x) ^ (y)) & ((x) ^ (z)) & 0x80;

    }

    public static void SetOFW_Sub(int x, int y, int z) {
        I.OverVal = ((z) ^ (y)) & ((z) ^ (x)) & 0x8000;

    }

    public static void SetOFB_Sub(int x, int y, int z) {
        I.OverVal = ((z) ^ (y)) & ((z) ^ (x)) & 0x80;
    }

    public static void SetCFB(int x) {
        I.CarryVal = (x) & 0x100;
    }

    public static void SetCFW(int x) {
        I.CarryVal = (x) & 0x10000;
    }

    public static void SetAF(int x, int y, int z) {
        I.AuxVal = ((x) ^ ((y) ^ (z))) & 0x10;
    }

    /*TODO*///#define SetSF(x)			(I.SignVal = (x))
/*TODO*///#define SetZF(x)			(I.ZeroVal = (x))
/*TODO*///#define SetPF(x)			(I.ParityVal = (x))
/*TODO*///
    public static void SetSZPF_Byte(int x) {
        I.SignVal = (byte) (x);
        I.ZeroVal = (byte) (x);
        I.ParityVal = (byte) (x);

    }

    public static void SetSZPF_Word(int x) {
        I.SignVal = (short) (x);
        I.ZeroVal = (short) (x);
        I.ParityVal = (short) (x);
    }

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
    static final int CF() {
        return BOOL(I.CarryVal != 0);
    }

    static final int SF() {
        return BOOL(I.SignVal < 0);
    }

    static final int ZF() {
        return BOOL(I.ZeroVal == 0);
    }

    static final int PF() {
        return BOOL(parity_table[(I.ParityVal & 0xFF)]);
    }

    static final int AF() {
        return BOOL(I.AuxVal != 0);
    }

    static final int OF() {
        return BOOL(I.OverVal != 0);
    }

    static final int DF() {
        return BOOL(I.DirVal < 0);
    }

    /**
     * *********************************************************************
     */
    static final int SegBase(int Seg) {
        return I.sregs[Seg] << 4;
    }

    static final int DefaultBase(int Seg) {
        return ((seg_prefix != 0 && (Seg == DS || Seg == SS)) ? prefix_base : I.base[Seg]);
    }

    public static int GetMemB(int Seg, int Off) {
        return (cpu_readmem20((DefaultBase(Seg) + (Off)) & AMASK));
    }

    public static int GetMemW(int Seg, int Off) {
        return (GetMemB(Seg, Off) & 0xFFFF) + ((GetMemB(Seg, (Off) + 1) << 8) & 0xFFFF);
    }

    public static void PutMemB(int Seg, int Off, int x) {
        cpu_writemem20((DefaultBase(Seg) + (Off)) & AMASK, (x));
    }

    public static void PutMemW(int Seg, int Off, int x) {
        PutMemB(Seg, Off, (x) & 0xFF);
        PutMemB(Seg, (Off) + 1, ((x) >> 8) & 0xFF);
    }

    /*TODO*///#define PEEKBYTE(ea) 			(cpu_readmem20((ea) & AMASK))
    public static int ReadByte(int ea) {
        return cpu_readmem20((ea) & AMASK);
    }

    public static int ReadWord(int ea) {
        return (cpu_readmem20((ea) & AMASK) + (cpu_readmem20(((ea) + 1) & AMASK) << 8));
    }

    public static void WriteByte(int ea, int val) {
        cpu_writemem20((ea) & AMASK, val);
    }

    public static void WriteWord(int ea, int val) {
        cpu_writemem20((ea) & AMASK, (val) & 0xff);
        cpu_writemem20(((ea) + 1) & AMASK, ((val) >> 8) & 0xff);
    }

    public static int read_port(int port) {
        return cpu_readport(port);
    }

    public static void write_port(int port, int val) {
        cpu_writeport(port, val);
    }

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
    public static final int FETCHWORD() {
        int var = cpu_readop_arg(I.pc);
        var += (cpu_readop_arg(I.pc + 1) << 8);
        I.pc += 2;
        return var;
    }

    public static final void PUSH(int val) {
        I.regs.SetW(SP, (I.regs.w[SP] - 2) & 0xFFFF);
        WriteWord(((I.base[SS] + I.regs.w[SP]) & AMASK), val);
    }

    public static final int POP() {
        int tmp = ReadWord(((I.base[SS] + I.regs.w[SP]) & AMASK));
        I.regs.SetW(SP, (I.regs.w[SP] + 2) & 0xFFFF);
        return tmp;
    }
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
