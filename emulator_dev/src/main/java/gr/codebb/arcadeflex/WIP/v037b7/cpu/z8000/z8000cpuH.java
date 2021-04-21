package gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000;

import gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000.z8000;
import gr.codebb.arcadeflex.WIP.v037b7.cpu.z8000.z8000.z8000_Regs;

public class z8000cpuH {
/*****************************************************************************
 *
 *   z8000cpu.h
 *	 Portable Z8000(2) emulator
 *	 Macros and types used in z8000.c / z8000ops.c / z8000tbl.c
 *
 *	 Copyright (c) 1998,1999 Juergen Buchmueller, all rights reserved.
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

/*TODO*////* pointers to the registers inside the Z8000_Regs struct Z */
    public int RB(int n){   return _cpu.pRB(n); }
    public void RB(int n, int m) { _cpu.pRB(n, m); }
    public int RW(int n) { return _cpu.pRW(n); }
    public void RW(int n, int m) { _cpu.pRW(n, m); }
    public int RL(int n){ return _cpu.pRL(n); }
    public void RL(int n, int m) { _cpu.pRL(n, m); }
    public int RQ(int n){   return _cpu.pRQ(n); }
    public void RQ(int n, int m){   _cpu.pRQ(n, m); }

    /* the register used as stack pointer */
    public int SP      = 15;

/*TODO*////* programm status */
/*TODO*///#define PPC     Z.ppc
/*TODO*///#define PC      Z.pc
    public int PSAP() { return _cpu.Z.psap; }
    public void PSAP(int _v) { _cpu.Z.psap=_v; }
/*TODO*///#define FCW     Z.fcw
    public int REFRESH() { return _cpu.Z.refresh; }
    public void REFRESH(int _v) { _cpu.Z.refresh=_v; }
    public int NSP() { return	_cpu.Z.nsp; }
    public void NSP(int _v) { _cpu.Z.nsp=_v; }
    public int IRQ_REQ() { return _cpu.Z.irq_req; }
    public void IRQ_REQ(int _irq) { _cpu.Z.irq_req=_irq; /*System.out.println("SET "+_irq);*/ }
    public int IRQ_SRV(){ return _cpu.Z.irq_srv; }
    public void IRQ_SRV(int _v){ _cpu.Z.irq_srv=_v; }
    public int IRQ_VEC(){ return _cpu.Z.irq_vec; }
    public void IRQ_VEC(int _v){ _cpu.Z.irq_vec=_v; }
/*TODO*///
/*TODO*////* these vectors are based on PSAP */
/*TODO*///#define RST 	(Z.psap + 0x0000)	/* start up FCW and PC */
/*TODO*///#define EPU 	(Z.psap + 0x0004)	/* extension processor unit? trap */
    public int TRAP(){ return	(_cpu.Z.psap + 0x0008); }	/* privilege violation trap */
    public int SYSCALL(){ return	(_cpu.Z.psap + 0x000c); }	/* system call SC */
    public int SEGTRAP(){ return	(_cpu.Z.psap + 0x0010); }	/* segment trap */
    public int NMI(){ return	(_cpu.Z.psap + 0x0014); }	/* non maskable interrupt */
    public int NVI(){ return	(_cpu.Z.psap + 0x0018); }	/* non vectored interrupt */
/*TODO*///#define VI		(Z.psap + 0x001c)	/* vectored interrupt */
    public int VEC00(){ return	(_cpu.Z.psap + 0x001e); }	/* vector n PC value */
/*TODO*///
/*TODO*////* bits of the FCW */
/*TODO*///#define F_SEG	0x8000				/* segmented mode (Z8001 only) */
    public static final int F_S_N	= 0x4000;				/* system / normal mode */
    public static final int F_EPU	= 0x2000;				/* extension processor unit? */
    public static final int F_NVIE	= 0x1000;				/* non vectored interrupt enable */
    public static final int F_VIE	= 0x0800;				/* vectored interrupt enable */
/*TODO*///#define F_10	0x0400				/* unused */
/*TODO*///#define F_9 	0x0200				/* unused */
/*TODO*///#define F_8 	0x0100				/* unused */
    public static final int F_C 	= 0x0080;	/* carry flag */
    public static final int F_Z 	= 0x0040;	/* zero flag */
    public static final int F_S 	= 0x0020;	/* sign flag */
    public static final int F_PV	= 0x0010;	/* parity/overflow flag */
    public static final int F_DA	= 0x0008;				/* decimal adjust flag (0 add/adc, 1 sub/sbc) */
    public static final int F_H 	= 0x0004;	/* half carry flag (byte arithmetic only) */
/*TODO*///#define F_1 	0x0002				/* unused */
/*TODO*///#define F_0 	0x0001				/* unused */

    /* opcode word numbers in Z.op[] array */
    public static final int OP0     = 0;
    public static final int OP1     = 1;
    public static final int OP2     = 2;

    /* nibble shift factors for an opcode word */
    /* left to right: 0x1340 . NIB0=1, NIB1=3, NIB2=4, NIB3=0 */
    public static final int NIB0    = 12;
    public static final int NIB1    = 8;
    public static final int NIB2    = 4;
    public static final int NIB3    = 0;

    /* sign bit masks for byte, word and long */
    public static final int S08 = 0x80;
    public static final int S16 = 0x8000;
    public static final int S32 = 0x80000000;

    /* get a single flag bit 0/1 */
    public int GET_C(){ return((_cpu.Z.fcw >>> 7) & 1); }
    public int GET_Z(){ return((_cpu.Z.fcw >>> 6) & 1); }
    public int GET_S(){ return((_cpu.Z.fcw >>> 5) & 1); }
    public int GET_PV(){ return((_cpu.Z.fcw >>> 4) & 1); }
    public int GET_DA(){ return((_cpu.Z.fcw >>> 3) & 1); }
    public int GET_H(){ return((_cpu.Z.fcw >>> 2) & 1); }

/*TODO*////* clear a single flag bit */
/*TODO*///#define CLR_C       FCW &= ~F_C
    public void CLR_Z(){     _cpu.Z.fcw &= ~F_Z; }
/*TODO*///#define CLR_S		FCW &= ~F_S
/*TODO*///#define CLR_P		FCW &= ~F_PV
    public void CLR_V(){    _cpu.Z.fcw &= ~F_PV; }
    public void CLR_DA(){    _cpu.Z.fcw &= ~F_DA; }
/*TODO*///#define CLR_H		FCW &= ~F_H

    /* clear a flag bit combination */
    public void CLR_CZS(){       _cpu.Z.fcw &= ~(F_C|F_Z|F_S); }
    public void CLR_CZSP(){      _cpu.Z.fcw &= ~(F_C|F_Z|F_S|F_PV); }
    public void CLR_CZSV(){	_cpu.Z.fcw &= ~(F_C|F_Z|F_S|F_PV); }
    public void CLR_CZSVH(){	_cpu.Z.fcw &= ~(F_C|F_Z|F_S|F_PV|F_H); }
    public void CLR_ZS(){        _cpu.Z.fcw &= ~(F_Z|F_S); }
    public void CLR_ZSV(){ 	_cpu.Z.fcw &= ~(F_Z|F_S|F_PV); }
    public void CLR_ZSP(){ 	_cpu.Z.fcw &= ~(F_Z|F_S|F_PV); }

    /* set a single flag bit */
    public void SET_C(){ _cpu.Z.fcw |= F_C; }
    public void SET_Z(){ _cpu.Z.fcw |= F_Z; }
    public void SET_S(){ _cpu.Z.fcw |= F_S; }
    public void SET_P(){ _cpu.Z.fcw |= F_PV; }
    public void SET_V(){ _cpu.Z.fcw |= F_PV; }
    public void SET_DA(){_cpu.Z.fcw |= F_DA; }
    public void SET_H(){ _cpu.Z.fcw |= F_H; }

/*TODO*////* set a flag bit combination */
/*TODO*///#define SET_SC      FCW |= F_C | F_S

    /* check condition codes */
    public int CC0(){ return (0); } 						/* always false */
    public int CC1(){ return GET_PV()^GET_S(); }				/* less than */
    public int CC2(){ return (GET_Z()|(GET_PV()^GET_S())); }		/* less than or equal */
    public int CC3(){ return (GET_Z()|GET_C()); }				/* unsigned less than or equal */
    public int CC4(){ return GET_PV(); }						/* parity even / overflow */
    public int CC5(){ return GET_S(); }						/* minus (signed) */
    public int CC6(){ return GET_Z(); }						/* zero / equal */
    public int CC7(){ return GET_C(); }						/* carry / unsigned less than */

    public int CC8(){ return (1); } 						/* always true */
    public int CC9(){ return (GET_PV()^GET_S())!=0?0:1; } 			/* greater than or equal */
    public int CCA(){ return (GET_Z()|(GET_PV()^GET_S()))!=0?0:1; } 	/* greater than */
    public int CCB(){ return (GET_Z()|GET_C())!=0?0:1; }				/* unsigned greater than */
    public int CCC(){ return GET_PV()!=0?0:1; } 					/* parity odd / no overflow */
    public int CCD(){ return GET_S()!=0?0:1; }						/* plus (not signed) */
    public int CCE(){ return GET_Z()!=0?0:1; }						/* not zero / not equal */
    public int CCF(){ return GET_C()!=0?0:1; }						/* not carry / unsigned greater than */

    /* get data from the opcode words */
    /* o is the opcode word offset	  */
    /* s is a nibble shift factor	  */
    public int cc;
    public int bit;
    
    public void GET_BIT(int o){      /*UINT16*/ bit = (1 << (_cpu.Z.op[o] & 15)) & 0xffff; }
    public void GET_CCC(int o, int s){	/*UINT8*/ cc = ((_cpu.Z.op[o] >>> (s)) & 15)&0xff; }
    
    public int dst;
    public int src;
    public int i4p1;
    public int imm8;
    public int cnt;
    public int imm4;
    public int imm3;
    public int imm2;
    public int idx;
    
    public void GET_DST(int o, int s){	/*UINT8 dst =*/ dst=((_cpu.Z.op[o] >>> (s)) & 15) & 0xff; }
    public void GET_SRC(int o, int s){	/*UINT8*/ src = ((_cpu.Z.op[o] >>> (s)) & 15) & 0xff; }
    public void GET_IDX(int o, int s){	/*UINT8*/ idx = ((_cpu.Z.op[o] >>> (s)) & 15) & 0xff; }
    public void GET_CNT(int o, int s){	/*UINT8*/ cnt = ((_cpu.Z.op[o] >>> (s)) & 15) & 0xff; }
    public void GET_IMM4(int o, int s){ /*UINT8*/ imm4 = ((_cpu.Z.op[o] >>> (s)) & 15) & 0xff; }

    public void GET_I4M1(int o, int s){	/*UINT8*/ i4p1 = (((_cpu.Z.op[o] >>> (s)) & 15) + 1) & 0xff; }
/*TODO*///#define GET_IMM1(o,s)	UINT8 imm1 = (Z.op[o] >> (s)) & 2
    public void GET_IMM2(int o, int s){	/*UINT8*/ imm2 = ((_cpu.Z.op[o] >>> (s)) & 3) & 0xff; }
    public void GET_IMM3(int o, int s){	/*UINT8*/ imm3 = ((_cpu.Z.op[o] >>> (s)) & 7) & 0xff; }

    public void GET_IMM8(int o){ 	/*UINT8*/ imm8 = _cpu.Z.op[o] & 0xff; }

    public int imm16;
    public int dsp7;
    public byte dsp8;
    public int addr;
    public int imm32;
    public int dsp16;
    
    public void GET_IMM16(int o){	/*UINT16 imm16 =*/ imm16 = (_cpu.Z.op[o])&0xffff; }
    public void GET_IMM32(){		/*UINT32*/ imm32 = _cpu.Z.op[2] + (_cpu.Z.op[1] << 16); }
    public void GET_DSP7(){		/*UINT8*/ dsp7 = (_cpu.Z.op[0] & 127)&0xff; }
    public void GET_DSP8(){		/*INT8*/ dsp8 = (byte)(_cpu.Z.op[0]); }
    public void GET_DSP16(){		/*UINT16*/ dsp16 = ((_cpu.Z.pc + (short)_cpu.Z.op[1]) & 0xffff); }
    public void GET_ADDR(int o){ 	/*UINT16*/ addr = _cpu.Z.op[o] & 0xffff; }

    private z8000 _cpu;
    
    public z8000cpuH(z8000 aThis) {
        _cpu=aThis;
    }

    /* structure for the opcode definition table */
    public static class Z8000_init {
            int 	beg, end, step;
            int 	size, cycles;
            OpcodePtr	opcode;
            String	dasm;
            
            public Z8000_init(int beg, int end, int step, int size, int cycles, OpcodePtr opcode, String dasm) {
                this.beg=beg;
                this.end=end;
                this.step=step;
                this.size=size;
                this.cycles=cycles;
                this.opcode=opcode;
                this.dasm=dasm;
            }
    };
    
    public static abstract interface OpcodePtr {
        public abstract void handler();
    }
    
    /* structure for the opcode execution table / disassembler */
    public static class Z8000_exec {
        OpcodePtr opcode;
        int     cycles;
        int 	size;
        String    dasm;
        
        public Z8000_exec(){
            super();
            opcode=null;
            cycles=0;
            size=0;
            dasm=null;
        }
    };
    
/*TODO*////* opcode execution table */
/*TODO*///extern Z8000_exec *z8000_exec;
/*TODO*///
/*TODO*///extern extern 
    
}
