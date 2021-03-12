/*** m6809: Portable 6809 emulator ******************************************

	Copyright (C) John Butler 1997

	References:

		6809 Simulator V09, By L.C. Benschop, Eidnhoven The Netherlands.

		m6809: Portable 6809 emulator, DS (6809 code in MAME, derived from
			the 6809 Simulator V09)

		6809 Microcomputer Programming & Interfacing with Experiments"
			by Andrew C. Staugaard, Jr.; Howard W. Sams & Co., Inc.

	System dependencies:	UINT16 must be 16 bit unsigned int
							UINT8 must be 8 bit unsigned int
							UINT32 must be more than 16 bits
							arrays up to 65536 bytes must be supported
							machine must be twos complement

	History:
991026 HJB:
	Fixed missing calls to cpu_changepc() for the TFR and EXG ocpodes.
	Replaced m6809_slapstic checks by a macro (CHANGE_PC). ESB still
	needs the tweaks.

991024 HJB:
	Tried to improve speed: Using bit7 of cycles1/2 as flag for multi
	byte opcodes is gone, those opcodes now call fetch_effective_address().
	Got rid of the slow/fast flags for stack (S and U) memory accesses.
	Minor changes to use 32 bit values as arguments to memory functions
    and added defines for that purpose (e.g. X = 16bit XD = 32bit).

990312 HJB:
	Added bugfixes according to Aaron's findings.
	Reset only sets CC_II and CC_IF, DP to zero and PC from reset vector.
990311 HJB:
	Added _info functions. Now uses static m6808_Regs struct instead
	of single statics. Changed the 16 bit registers to use the generic
	PAIR union. Registers defined using macros. Split the core into
	four execution loops for M6802, M6803, M6808 and HD63701.
    TST, TSTA and TSTB opcodes reset carry flag.
	Modified the read/write stack handlers to push LSB first then MSB
	and pull MSB first then LSB.

990228 HJB:
	Changed the interrupt handling again. Now interrupts are taken
	either right at the moment the lines are asserted or whenever
	an interrupt is enabled and the corresponding line is still
	asserted. That way the pending_interrupts checks are not
	needed anymore. However, the CWAI and SYNC flags still need
	some flags, so I changed the name to 'int_state'.
	This core also has the code for the old interrupt system removed.

990225 HJB:
	Cleaned up the code here and there, added some comments.
	Slightly changed the SAR opcodes (similiar to other CPU cores).
	Added symbolic names for the flag bits.
	Changed the way CWAI/Interrupt() handle CPU state saving.
	A new flag M6809_STATE in pending_interrupts is used to determine
	if a state save is needed on interrupt entry or already done by CWAI.
	Added M6809_IRQ_LINE and M6809_FIRQ_LINE defines to m6809.h
	Moved the internal interrupt_pending flags from m6809.h to m6809.c
	Changed CWAI cycles2[0x3c] to be 2 (plus all or at least 19 if
	CWAI actually pushes the entire state).
	Implemented undocumented TFR/EXG for undefined source and mixed 8/16
	bit transfers (they should transfer/exchange the constant $ff).
	Removed unused jmp/jsr _slap functions from 6809ops.c,
	m6809_slapstick check moved into the opcode functions.

*****************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809ops.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809tbl.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.cpu_interface;
import gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.irqcallbacksPtr;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import gr.codebb.arcadeflex.old.arcadeflex.libc_old.FILE;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;

public class m6809 extends cpu_interface {
    
    /* public globals */
    public static int[] m6809_ICount = new int[1]; //50000;
    public static FILE m6809log=null;//fopen("m6809.log", "wa");  //for debug purposes
    
    private m6809ops _opcodes = null;
    
    public m6809() {
        cpu_num = CPU_M6809;
        num_irqs = 2;
        default_vector = 0;
        overclock = 1.0;
        no_int = M6809_INT_NONE;
        irq_int = M6809_INT_IRQ;
        nmi_int = M6809_INT_NMI;
        address_shift = 0;
        address_bits = 16;
        endianess = CPU_IS_BE;
        align_unit = 1;
        max_inst_len = 4;
        abits1 = ABITS1_16;
        abits2 = ABITS2_16;
        abitsmin = ABITS_MIN_16;
        icount = m6809_ICount;
        m6809_ICount[0] = 50000;
        
        _opcodes = new m6809ops(this);
    }

    @Override
    public void reset(Object param) {
        m6809_reset(param);
    }

    @Override
    public void exit() {
        m6809_exit();
    }

    @Override
    public int execute(int cycles) {
        return m6809_execute(cycles);
    }

    @Override
    public Object init_context() {
        Object reg = new m6809_Regs();
        return reg;
    }

    @Override
    public Object get_context() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_context(Object reg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] get_cycle_table(int which) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_cycle_table(int which, int[] new_table) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_pc() {
        return m6809_get_pc();
    }

    @Override
    public void set_pc(int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_sp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_sp(int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_reg(int regnum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_reg(int regnum, int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_nmi_line(int linestate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_line(int irqline, int linestate) {
        m6809_set_irq_line(irqline, linestate);
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        m6809_set_irq_callback(callback);
    }

    @Override
    public void internal_interrupt(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cpu_state_save(Object file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cpu_state_load(Object file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        return m6809_info(context, regnum);
    }

    @Override
    public int memory_read(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void memory_write(int offset, int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int internal_read(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void internal_write(int offset, int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_op_base(int pc) {
        cpu_setOPbase16.handler(pc & 0xFFFF);
    }

/*TODO*///	
/*TODO*///	/* Enable big switch statement for the main opcodes */
/*TODO*///	#ifndef BIG_SWITCH
/*TODO*///	#define BIG_SWITCH  1
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#define VERBOSE 0
/*TODO*///	
/*TODO*///	#if VERBOSE
/*TODO*///	#define LOG(x)	logerror x
/*TODO*///	#else
/*TODO*///	#define LOG(x)
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	static UINT8 m6809_reg_layout[] = {
/*TODO*///		M6809_PC, M6809_S, M6809_CC, M6809_A, M6809_B, M6809_X, -1,
/*TODO*///		M6809_Y, M6809_U, M6809_DP, M6809_NMI_STATE, M6809_IRQ_STATE, M6809_FIRQ_STATE, 0
/*TODO*///	};
/*TODO*///	
/*TODO*///	/* Layout of the debugger windows x,y,w,h */
/*TODO*///	static UINT8 m6809_win_layout[] = {
/*TODO*///		27, 0,53, 4,	/* register window (top, right rows) */
/*TODO*///		 0, 0,26,22,	/* disassembler window (left colums) */
/*TODO*///		27, 5,53, 8,	/* memory #1 window (right, upper middle) */
/*TODO*///		27,14,53, 8,	/* memory #2 window (right, lower middle) */
/*TODO*///		 0,23,80, 1,	/* command line window (bottom rows) */
/*TODO*///	};


	/* 6809 Registers */
	public static class m6809_Regs
        {
            public /*PAIR*/ char pc; 		/* Program counter */
            public /*PAIR*/ char ppc;		/* Previous program counter */
            public char a;
            public char b;   //PAIR	d;		/* Accumulator a and b */
            public /*PAIR*/ char dp; 		/* Direct Page register (page in MSB) */
            public char u;
            public char s;//PAIR	u, s;		/* Stack pointers */
            public char x;
            public char y;//PAIR	x, y;		/* Index registers */
            public char /*UINT8*/   cc;
            public int /*UINT8*/   ireg;		/* First opcode */
            public int[] /*UINT8*/   irq_state=new int[2];
            public    int     extra_cycles; /* cycles used up by interrupts */
            public irqcallbacksPtr irq_callback;
            public int /*UINT8*/   int_state;  /* SYNC and CWAI flags */
            public int /*UINT8*/   nmi_state;
	};
	
	/* flag bits in the cc register */
	public static final int CC_C    = 0x01;        /* Carry */
	public static final int CC_V    = 0x02;        /* Overflow */
	public static final int CC_Z    = 0x04;        /* Zero */
	public static final int CC_N    = 0x08;        /* Negative */
	public static final int CC_II   = 0x10;        /* Inhibit IRQ */
	public static final int CC_H    = 0x20;        /* Half (auxiliary) carry */
        public static final int CC_IF   = 0x40;        /* Inhibit FIRQ */
	public static final int CC_E    = 0x80;        /* entire state pushed */

	/* 6809 registers */
	public static m6809_Regs _m6809=new m6809_Regs();/* 6809 registers */
    
/*TODO*///	int m6809_slapstic = 0;
/*TODO*///	
/*TODO*///	#define pPPC    m6809.ppc
/*TODO*///	#define pPC 	m6809.pc
/*TODO*///	#define pU		m6809.u
/*TODO*///	#define pS		m6809.s
/*TODO*///	#define pX		m6809.x
/*TODO*///	#define pY		m6809.y
/*TODO*///	#define pD		m6809.d
/*TODO*///	
/*TODO*///	#define	PPC		m6809.ppc.w.l
/*TODO*///	#define PC  	m6809.pc.w.l
/*TODO*///	#define PCD 	m6809.pc.d
/*TODO*///	#define U		m6809.u.w.l
/*TODO*///	#define UD		m6809.u.d
/*TODO*///	#define S		m6809.s.w.l
/*TODO*///	#define SD		m6809.s.d
/*TODO*///	#define X		m6809.x.w.l
/*TODO*///	#define XD		m6809.x.d
/*TODO*///	#define Y		m6809.y.w.l
/*TODO*///	#define YD		m6809.y.d
/*TODO*///	#define D   	m6809.d.w.l
/*TODO*///	#define A   	m6809.d.b.h
/*TODO*///	#define B		m6809.d.b.l
/*TODO*///	#define DP		m6809.dp.b.h
/*TODO*///	#define DPD 	m6809.dp.d
/*TODO*///	#define CC  	m6809.cc
/*TODO*///	
/*TODO*///	static PAIR ea;         /* effective address */
/*TODO*///	#define EA	ea.w.l
/*TODO*///	#define EAD ea.d
        public int ea;

    //	#define CHANGE_PC change_pc16(PCD)
    public void CHANGE_PC()
    {
        change_pc16(_m6809.pc & 0xFFFF);//ensure it's 16bit just in case
    }
/*TODO*///	#if 0
/*TODO*///	#define CHANGE_PC	{			\
/*TODO*///		if (m6809_slapstic != 0)		\
/*TODO*///			cpu_setOPbase16(PCD);	\
/*TODO*///		else						\
/*TODO*///			change_pc16(PCD);		\
/*TODO*///		}
/*TODO*///	#endif

	public static final int M6809_CWAI		= 8;	/* set when CWAI is waiting for an interrupt */
	public static final int M6809_SYNC		= 16;	/* set when SYNC is waiting for an interrupt */
        public static final int M6809_LDS		= 32;	/* set when LDS occured at least once */

	public void CHECK_IRQ_LINES()
        {
		if( _m6809.irq_state[M6809_IRQ_LINE] != CLEAR_LINE ||
			_m6809.irq_state[M6809_FIRQ_LINE] != CLEAR_LINE )
			_m6809.int_state &= ~M6809_SYNC; /* clear SYNC flag */
		if( _m6809.irq_state[M6809_FIRQ_LINE]!=CLEAR_LINE && (_m6809.cc & CC_IF)==0 )
		{
			/* fast IRQ */
			/* HJB 990225: state already saved by CWAI? */
			if(( _m6809.int_state & M6809_CWAI ) != 0)
			{
				_m6809.int_state &= ~M6809_CWAI;  /* clear CWAI */
				_m6809.extra_cycles += 7;		 /* subtract +7 cycles */
	        }
			else
			{
				_m6809.cc &= ~CC_E;				/* save 'short' state */
				PUSHWORD(_m6809.pc);
				PUSHBYTE(_m6809.cc);
				_m6809.extra_cycles += 10;	/* subtract +10 cycles */
			}
			_m6809.cc |= CC_IF | CC_II;			/* inhibit FIRQ and IRQ */
			_m6809.pc=RM16(0xfff6);
			CHANGE_PC();
			(_m6809.irq_callback).handler(M6809_FIRQ_LINE);
		}
		else
		if( _m6809.irq_state[M6809_IRQ_LINE]!=CLEAR_LINE && (_m6809.cc & CC_II)==0 )
		{
			/* standard IRQ */
			/* HJB 990225: state already saved by CWAI? */
			if(( _m6809.int_state & M6809_CWAI ) != 0)
			{
				_m6809.int_state &= ~M6809_CWAI;  /* clear CWAI flag */
				_m6809.extra_cycles += 7;		 /* subtract +7 cycles */
			}
			else
			{
				_m6809.cc |= CC_E; 				/* save entire state */
				PUSHWORD(_m6809.pc);
				PUSHWORD(_m6809.u);
				PUSHWORD(_m6809.y);
				PUSHWORD(_m6809.x);
				PUSHBYTE(_m6809.dp);
				PUSHBYTE(_m6809.b);
				PUSHBYTE(_m6809.a);
				PUSHBYTE(_m6809.cc);
				_m6809.extra_cycles += 19;	 /* subtract +19 cycles */
			}
			_m6809.cc |= CC_II;					/* inhibit IRQ */
			_m6809.pc=RM16(0xfff8);
			CHANGE_PC();
			(_m6809.irq_callback).handler(M6809_IRQ_LINE);
		}
        }


    /* these are re-defined in m6809.h TO RAM, ROM or functions in cpuintrf.c */
    public char RM(int addr)
    {
        return (char)M6809_RDMEM(addr);
    }

    public void WM(int Addr, int Value)
    { 
        M6809_WRMEM(Addr,Value); 
    }
    
    public char ROP(int Addr)
    {
        return M6809_RDOP(Addr);
    }

    public char ROP_ARG(int Addr){ return (char) M6809_RDOP_ARG(Addr); }

    /* macros to access memory */

    public char IMMBYTE(){	int b = ROP_ARG(_m6809.pc); _m6809.pc++; return (char) (b & 0xFF); }
    public char IMMWORD(){	int _s= (ROP_ARG(_m6809.pc)<<8) | ROP_ARG((_m6809.pc+1)&0xffff); _m6809.pc+=2; return (char) _s; }

    public void PUSHBYTE(int w)
    {
        _m6809.s = (char)(_m6809.s -1);
        WM(_m6809.s,w);
    }

    public void PUSHWORD(int w)
    {
        _m6809.s = (char)(_m6809.s -1);
        WM(_m6809.s,w & 0xFF); 
        _m6809.s = (char)(_m6809.s -1);
        WM(_m6809.s,w >>>8);
    }
/*TODO*///	#define PULLBYTE(b) b = RM(SD); S++
/*TODO*///	#define PULLWORD(w) w = RM(SD)<<8; S++; w |= RM(SD); S++
/*TODO*///	
/*TODO*///	#define PSHUBYTE(b) --U; WM(UD,b);
/*TODO*///	#define PSHUWORD(w) --U; WM(UD,w.b.l); --U; WM(UD,w.b.h)
/*TODO*///	#define PULUBYTE(b) b = RM(UD); U++
/*TODO*///	#define PULUWORD(w) w = RM(UD)<<8; U++; w |= RM(UD); U++

        public void CLR_HNZVC(){ _m6809.cc&=~(CC_H|CC_N|CC_Z|CC_V|CC_C); }
        public void CLR_NZV(){ 	_m6809.cc&=~(CC_N|CC_Z|CC_V); }
/*TODO*///	#define CLR_HNZC	CC&=~(CC_H|CC_N|CC_Z|CC_C)
	public void CLR_NZVC(){	_m6809.cc&=~(CC_N|CC_Z|CC_V|CC_C); }
        public void CLR_Z(){ _m6809.cc&=~(CC_Z); }
/*TODO*///	#define CLR_NZC 	CC&=~(CC_N|CC_Z|CC_C)
	public void CLR_ZC(){ _m6809.cc&=~(CC_Z|CC_C); }
	
	/* macros for CC -- CC bits affected should be reset before calling */
	public void SET_Z(int a){ if(a==0)SEZ(); }
        public void SET_Z8(int a){ SET_Z(a); }
	public void SET_Z16(int a){ SET_Z(a&0xFFFF); }
	public void SET_N8(int a){ _m6809.cc|=((a&0x80)>>4); }
	public void SET_N16(int a){ _m6809.cc|=((a&0x8000)>>12); }
        public void SET_H(int a, int b, int r){ _m6809.cc|=(((a^b^r)&0x10)<<1); }
        public void SET_C8(int a){ _m6809.cc|=((a&0x100)>>8); }
        public void SET_C16(int a){ _m6809.cc|=((a&0x10000)>>16); }
	public void SET_V8(int a, int b, int r){ _m6809.cc|=(((a^b^r^(r>>1))&0x80)>>6); }
        public void SET_V16(int a, int b, int r){ _m6809.cc|=(((a^b^r^(r>>1))&0x8000)>>14); }
/*TODO*///	
/*TODO*///	static UINT8 flags8i[256]=	 /* increment */
/*TODO*///	{
/*TODO*///	CC_Z,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	CC_N|CC_V,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N
/*TODO*///	};
/*TODO*///	static UINT8 flags8d[256]= /* decrement */
/*TODO*///	{
/*TODO*///	CC_Z,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CC_V,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
/*TODO*///	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N
/*TODO*///	};
/*TODO*///	#define SET_FLAGS8I(a)		{CC|=flags8i[(a)&0xff];}
/*TODO*///	#define SET_FLAGS8D(a)		{CC|=flags8d[(a)&0xff];}
	
	/* combos */
	public void SET_NZ8(int a){SET_N8(a);SET_Z(a);}
        public void SET_NZ16(int a){ SET_N16(a);SET_Z(a); }
	public void SET_FLAGS8(int a, int b, int r){SET_N8(r);SET_Z8(r);SET_V8(a,b,r);SET_C8(r);}
        public void SET_FLAGS16(int a, int b, int r){SET_N16(r);SET_Z16(r);SET_V16(a,b,r);SET_C16(r);}

/*TODO*///	/* for treating an unsigned byte as a signed word */
	public int SIGNED(int b){ return (((b&0x80)!=0?b|0xff00:b)&0xffff); }

/*TODO*///	/* macros for addressing modes (postbytes have their own code) */
/*TODO*///	#define DIRECT	EAD = DPD; IMMBYTE(ea.b.l)
/*TODO*///	#define IMM8	EAD = PCD; PC++
/*TODO*///	#define IMM16	EAD = PCD; PC+=2
	public void EXTENDED(){ ea = IMMWORD(); }

/*TODO*///	/* macros to set status flags */
	public static void SEC(){ _m6809.cc|=CC_C; }
/*TODO*///	#define CLC CC&=~CC_C
	public static void SEZ(){ _m6809.cc|=CC_Z; }
/*TODO*///	#define CLZ CC&=~CC_Z
/*TODO*///	#define SEN CC|=CC_N
/*TODO*///	#define CLN CC&=~CC_N
/*TODO*///	#define SEV CC|=CC_V
/*TODO*///	#define CLV CC&=~CC_V
/*TODO*///	#define SEH CC|=CC_H
/*TODO*///	#define CLH CC&=~CC_H

	/* macros for convenience */
/*TODO*///	#define DIRBYTE(b) {DIRECT;b=RM(EAD);}
/*TODO*///	#define DIRWORD(w) {DIRECT;w.d=RM16(EAD);}
	public char EXTBYTE() {
            EXTENDED();
            return RM(ea);
        }
/*TODO*///	#define EXTWORD(w) {EXTENDED;w.d=RM16(EAD);}

	/* macros for branch instructions */
	public void BRANCH(boolean f) {
		int t;
		t=IMMBYTE();
		if (f)
		{
			_m6809.pc += SIGNED(t);
			CHANGE_PC();
		}
	}
	
	public void LBRANCH(boolean f) {
		int t;
		t = IMMWORD();
		if (f)
		{
			m6809_ICount[0] -= 1;
			_m6809.pc = (char)(_m6809.pc + t);
			CHANGE_PC();
		}
	}
	
/*TODO*///	#define NXORV  ((CC&CC_N)^((CC&CC_V)<<2))
/*TODO*///	
/*TODO*///	/* macros for setting/getting registers in TFR/EXG instructions */
/*TODO*///	
/*TODO*///	#if (!BIG_SWITCH)
/*TODO*///	/* timings for 1-byte opcodes */
/*TODO*///	static UINT8 cycles1[] =
/*TODO*///	{
/*TODO*///		/*	 0	1  2  3  4	5  6  7  8	9  A  B  C	D  E  F */
/*TODO*///	  /*0*/  6, 0, 0, 6, 6, 0, 6, 6, 6, 6, 6, 0, 6, 6, 3, 6,
/*TODO*///	  /*1*/  0, 0, 2, 4, 0, 0, 5, 9, 0, 2, 3, 0, 3, 2, 8, 6,
/*TODO*///	  /*2*/  3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
/*TODO*///	  /*3*/  4, 4, 4, 4, 5, 5, 5, 5, 0, 5, 3, 6,20,11, 0,19,
/*TODO*///	  /*4*/  2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2,
/*TODO*///	  /*5*/  2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2,
/*TODO*///	  /*6*/  6, 0, 0, 6, 6, 0, 6, 6, 6, 6, 6, 0, 6, 6, 3, 6,
/*TODO*///	  /*7*/  7, 0, 0, 7, 7, 0, 7, 7, 7, 7, 7, 0, 7, 7, 4, 7,
/*TODO*///	  /*8*/  2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 2, 4, 7, 3, 0,
/*TODO*///	  /*9*/  4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 7, 5, 5,
/*TODO*///	  /*A*/  4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 7, 5, 5,
/*TODO*///	  /*B*/  5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 8, 6, 6,
/*TODO*///	  /*C*/  2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 2, 3, 0, 3, 3,
/*TODO*///	  /*D*/  4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5,
/*TODO*///	  /*E*/  4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5,
/*TODO*///	  /*F*/  5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6
/*TODO*///	};
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	INLINE UINT32 RM16( UINT32 Addr )
/*TODO*///	{
/*TODO*///		UINT32 result = RM(Addr) << 8;
/*TODO*///		return result | RM((Addr+1)&0xffff);
/*TODO*///	}
    public char RM16(int addr)
    {
        /*int i = RM(addr + 1 & 0xFFFF);
        i |= RM(addr) << 8;
        return i;*/
        int temp = RM(addr & 0xffff) << 8;
        temp = temp | RM((addr+1)&0xffff);
        return (char)temp;
    }
	
    public void WM16( int addr, int reg )
    {
            /*WM(addr + 1 & 0xFFFF, reg & 0xFF);
            WM(addr, reg >> 8);*/
            WM((addr + 1)&0xffff , reg & 0xff);
            WM(addr & 0xffff, (reg >>> 8) & 0xff);
    }
	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Get all registers in given buffer
/*TODO*///	 ****************************************************************************/
/*TODO*///	unsigned m6809_get_context(void *dst)
/*TODO*///	{
/*TODO*///		if (dst != 0)
/*TODO*///			*(m6809_Regs*)dst = m6809;
/*TODO*///		return sizeof(m6809_Regs);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Set all registers to given values
/*TODO*///	 ****************************************************************************/
/*TODO*///	void m6809_set_context(void *src)
/*TODO*///	{
/*TODO*///		if (src != 0)
/*TODO*///			m6809 = *(m6809_Regs*)src;
/*TODO*///		CHANGE_PC;
/*TODO*///	
/*TODO*///	    CHECK_IRQ_LINES;
/*TODO*///	}
	
	/****************************************************************************
	 * Return program counter
	 ****************************************************************************/
	public int m6809_get_pc()
	{
		return _m6809.pc & 0xFFFF;
	}
	
	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Set program counter
/*TODO*///	 ****************************************************************************/
/*TODO*///	void m6809_set_pc(unsigned val)
/*TODO*///	{
/*TODO*///		PC = val;
/*TODO*///		CHANGE_PC;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Return stack pointer
/*TODO*///	 ****************************************************************************/
/*TODO*///	unsigned m6809_get_sp(void)
/*TODO*///	{
/*TODO*///		return S;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Set stack pointer
/*TODO*///	 ****************************************************************************/
/*TODO*///	void m6809_set_sp(unsigned val)
/*TODO*///	{
/*TODO*///		S = val;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Return a specific register                                               */
/*TODO*///	/****************************************************************************/
/*TODO*///	unsigned m6809_get_reg(int regnum)
/*TODO*///	{
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case M6809_PC: return PC;
/*TODO*///			case M6809_S: return S;
/*TODO*///			case M6809_CC: return CC;
/*TODO*///			case M6809_U: return U;
/*TODO*///			case M6809_A: return A;
/*TODO*///			case M6809_B: return B;
/*TODO*///			case M6809_X: return X;
/*TODO*///			case M6809_Y: return Y;
/*TODO*///			case M6809_DP: return DP;
/*TODO*///			case M6809_NMI_STATE: return m6809.nmi_state;
/*TODO*///			case M6809_IRQ_STATE: return m6809.irq_state[M6809_IRQ_LINE];
/*TODO*///			case M6809_FIRQ_STATE: return m6809.irq_state[M6809_FIRQ_LINE];
/*TODO*///			case REG_PREVIOUSPC: return PPC;
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = S + 2 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xffff )
/*TODO*///						return ( RM( offset ) << 8 ) | RM( offset + 1 );
/*TODO*///				}
/*TODO*///		}
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Set a specific register                                                  */
/*TODO*///	/****************************************************************************/
/*TODO*///	void m6809_set_reg(int regnum, unsigned val)
/*TODO*///	{
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case M6809_PC: PC = val; CHANGE_PC; break;
/*TODO*///			case M6809_S: S = val; break;
/*TODO*///			case M6809_CC: CC = val; CHECK_IRQ_LINES; break;
/*TODO*///			case M6809_U: U = val; break;
/*TODO*///			case M6809_A: A = val; break;
/*TODO*///			case M6809_B: B = val; break;
/*TODO*///			case M6809_X: X = val; break;
/*TODO*///			case M6809_Y: Y = val; break;
/*TODO*///			case M6809_DP: DP = val; break;
/*TODO*///			case M6809_NMI_STATE: m6809.nmi_state = val; break;
/*TODO*///			case M6809_IRQ_STATE: m6809.irq_state[M6809_IRQ_LINE] = val; break;
/*TODO*///			case M6809_FIRQ_STATE: m6809.irq_state[M6809_FIRQ_LINE] = val; break;
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = S + 2 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xffff )
/*TODO*///					{
/*TODO*///						WM( offset, (val >> 8) & 0xff );
/*TODO*///						WM( offset+1, val & 0xff );
/*TODO*///					}
/*TODO*///				}
/*TODO*///	    }
/*TODO*///	}
	
	
	/****************************************************************************/
	/* Reset registers to their initial values									*/
	/****************************************************************************/
	public void m6809_reset(Object param)
	{
		_m6809.int_state = 0;
		_m6809.nmi_state = CLEAR_LINE;
		_m6809.irq_state[0] = CLEAR_LINE;
		_m6809.irq_state[0] = CLEAR_LINE;
	
		_m6809.dp = 0;			/* Reset direct page register */
	
                _m6809.cc |= CC_II;        /* IRQ disabled */
                _m6809.cc |= CC_IF;        /* FIRQ disabled */
	
		_m6809.pc = RM16(0xfffe);
		CHANGE_PC();
	}
	
	public void m6809_exit()
	{
		/* nothing to do ? */
	}
	
/*TODO*///	/* Generate interrupts */
/*TODO*///	/****************************************************************************
/*TODO*///	 * Set NMI line state
/*TODO*///	 ****************************************************************************/
/*TODO*///	void m6809_set_nmi_line(int state)
/*TODO*///	{
/*TODO*///		if (m6809.nmi_state == state) return;
/*TODO*///		m6809.nmi_state = state;
/*TODO*///		LOG(("M6809#%d set_nmi_line %d\n", cpu_getactivecpu(), state));
/*TODO*///		if( state == CLEAR_LINE ) return;
/*TODO*///	
/*TODO*///		/* if the stack was not yet initialized */
/*TODO*///	    if( !(m6809.int_state & M6809_LDS) ) return;
/*TODO*///	
/*TODO*///	    m6809.int_state &= ~M6809_SYNC;
/*TODO*///		/* HJB 990225: state already saved by CWAI? */
/*TODO*///		if( m6809.int_state & M6809_CWAI )
/*TODO*///		{
/*TODO*///			m6809.int_state &= ~M6809_CWAI;
/*TODO*///			m6809.extra_cycles += 7;	/* subtract +7 cycles next time */
/*TODO*///	    }
/*TODO*///		else
/*TODO*///		{
/*TODO*///			CC |= CC_E; 				/* save entire state */
/*TODO*///			PUSHWORD(pPC);
/*TODO*///			PUSHWORD(pU);
/*TODO*///			PUSHWORD(pY);
/*TODO*///			PUSHWORD(pX);
/*TODO*///			PUSHBYTE(DP);
/*TODO*///			PUSHBYTE(B);
/*TODO*///			PUSHBYTE(A);
/*TODO*///			PUSHBYTE(CC);
/*TODO*///			m6809.extra_cycles += 19;	/* subtract +19 cycles next time */
/*TODO*///		}
/*TODO*///		CC |= CC_IF | CC_II;			/* inhibit FIRQ and IRQ */
/*TODO*///		PCD = RM16(0xfffc);
/*TODO*///		CHANGE_PC;
/*TODO*///	}
	
	/****************************************************************************
	 * Set IRQ line state
	 ****************************************************************************/
	public void m6809_set_irq_line(int irqline, int state)
	{
/*TODO*///	    LOG(("M6809#%d set_irq_line %d, %d\n", cpu_getactivecpu(), irqline, state));
		_m6809.irq_state[irqline] = state;
		if (state == CLEAR_LINE) return;
		CHECK_IRQ_LINES();
	}

	/****************************************************************************
	 * Set IRQ vector callback
	 ****************************************************************************/
	public void m6809_set_irq_callback(irqcallbacksPtr callback)
	{
		_m6809.irq_callback = callback;
	}
	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Save CPU state
/*TODO*///	 ****************************************************************************/
/*TODO*///	static void state_save(void *file, const char *module)
/*TODO*///	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///		state_save_UINT16(file, module, cpu, "PC", &PC, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "U", &U, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "S", &S, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "X", &X, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "Y", &Y, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "DP", &DP, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "CC", &CC, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "INT", &m6809.int_state, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "NMI", &m6809.nmi_state, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "IRQ", &m6809.irq_state[0], 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "FIRQ", &m6809.irq_state[1], 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Load CPU state
/*TODO*///	 ****************************************************************************/
/*TODO*///	static void state_load(void *file, const char *module)
/*TODO*///	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///		state_load_UINT16(file, module, cpu, "PC", &PC, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "U", &U, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "S", &S, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "X", &X, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "Y", &Y, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "DP", &DP, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "CC", &CC, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "INT", &m6809.int_state, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "NMI", &m6809.nmi_state, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "IRQ", &m6809.irq_state[0], 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "FIRQ", &m6809.irq_state[1], 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m6809_state_save(void *file) { state_save(file, "m6809"); }
/*TODO*///	void m6809_state_load(void *file) { state_load(file, "m6809"); }
	
	/****************************************************************************
	 * Return a formatted string for a register
	 ****************************************************************************/
	public String m6809_info(Object context, int regnum)
	{
/*TODO*///		static char buffer[16][47+1];
/*TODO*///		static int which = 0;
/*TODO*///		m6809_Regs *r = context;
/*TODO*///	
/*TODO*///		which = ++which % 16;
/*TODO*///	    buffer[which][0] = '\0';
/*TODO*///		if( !context )
/*TODO*///			r = &m6809;
	
		switch( regnum )
		{
			case CPU_INFO_NAME: return "M6809";
			case CPU_INFO_FAMILY: return "Motorola 6809";
			case CPU_INFO_VERSION: return "1.1";
			case CPU_INFO_FILE: return "m6809.java";
			case CPU_INFO_CREDITS: return "Copyright (C) John Butler 1997";
/*TODO*///			case CPU_INFO_REG_LAYOUT: return (const char*)m6809_reg_layout;
/*TODO*///			case CPU_INFO_WIN_LAYOUT: return (const char*)m6809_win_layout;
/*TODO*///	
/*TODO*///			case CPU_INFO_FLAGS:
/*TODO*///				sprintf(buffer[which], "%c%c%c%c%c%c%c%c",
/*TODO*///					r.cc & 0x80 ? 'E':'.',
/*TODO*///					r.cc & 0x40 ? 'F':'.',
/*TODO*///	                r.cc & 0x20 ? 'H':'.',
/*TODO*///	                r.cc & 0x10 ? 'I':'.',
/*TODO*///	                r.cc & 0x08 ? 'N':'.',
/*TODO*///	                r.cc & 0x04 ? 'Z':'.',
/*TODO*///	                r.cc & 0x02 ? 'V':'.',
/*TODO*///	                r.cc & 0x01 ? 'C':'.');
/*TODO*///	            break;
/*TODO*///			case CPU_INFO_REG+M6809_PC: sprintf(buffer[which], "PC:%04X", r.pc.w.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_S: sprintf(buffer[which], "S:%04X", r.s.w.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_CC: sprintf(buffer[which], "CC:%02X", r.cc); break;
/*TODO*///			case CPU_INFO_REG+M6809_U: sprintf(buffer[which], "U:%04X", r.u.w.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_A: sprintf(buffer[which], "A:%02X", r.d.b.h); break;
/*TODO*///			case CPU_INFO_REG+M6809_B: sprintf(buffer[which], "B:%02X", r.d.b.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_X: sprintf(buffer[which], "X:%04X", r.x.w.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_Y: sprintf(buffer[which], "Y:%04X", r.y.w.l); break;
/*TODO*///			case CPU_INFO_REG+M6809_DP: sprintf(buffer[which], "DP:%02X", r.dp.b.h); break;
/*TODO*///			case CPU_INFO_REG+M6809_NMI_STATE: sprintf(buffer[which], "NMI:%X", r.nmi_state); break;
/*TODO*///			case CPU_INFO_REG+M6809_IRQ_STATE: sprintf(buffer[which], "IRQ:%X", r.irq_state[M6809_IRQ_LINE]); break;
/*TODO*///			case CPU_INFO_REG+M6809_FIRQ_STATE: sprintf(buffer[which], "FIRQ:%X", r.irq_state[M6809_FIRQ_LINE]); break;
		}
/*TODO*///		return buffer[which];
            throw new UnsupportedOperationException("unsupported m6809 cpu_info");
	}
	
/*TODO*///	unsigned m6809_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	    return Dasm6809(buffer,pc);
/*TODO*///	#else
/*TODO*///		sprintf( buffer, "$%02X", cpu_readop(pc) );
/*TODO*///		return 1;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* includes the static function prototypes and the master opcode table */
/*TODO*///	
/*TODO*///	/* includes the actual opcode implementations */
	
	/* execute instructions on this CPU until icount expires */
	public int m6809_execute(int cycles)	/* NS 970908 */
	{
                m6809_ICount[0] = cycles - _m6809.extra_cycles;
		_m6809.extra_cycles = 0;
	
		if ((_m6809.int_state & (M6809_CWAI | M6809_SYNC)) != 0)
		{
			m6809_ICount[0] = 0;
		}
		else
		{
			do
			{
				_m6809.ppc = _m6809.pc;
	
/*TODO*///				CALL_MAME_DEBUG;
	
				_m6809.ireg = ROP(_m6809.pc);
				_m6809.pc++;
/*TODO*///	#if BIG_SWITCH
                                //System.out.println(_m6809.ireg);
                                switch( _m6809.ireg )
				{
/*TODO*///				case 0x00: neg_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x01: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x02: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x03: com_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x04: lsr_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x05: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x06: ror_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x07: asr_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x08: asl_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x09: rol_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x0a: dec_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x0b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x0c: inc_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x0d: tst_di();   m6809_ICount-= 6; break;
/*TODO*///				case 0x0e: jmp_di();   m6809_ICount-= 3; break;
/*TODO*///				case 0x0f: clr_di();   m6809_ICount-= 6; break;
				case 0x10: _opcodes.pref10();					 break;
/*TODO*///				case 0x11: pref11();					 break;
/*TODO*///				case 0x12: nop();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x13: sync();	   m6809_ICount-= 4; break;
/*TODO*///				case 0x14: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x15: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x16: lbra();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x17: lbsr();	   m6809_ICount-= 9; break;
/*TODO*///				case 0x18: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x19: daa();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x1a: orcc();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x1b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x1c: andcc();    m6809_ICount-= 3; break;
/*TODO*///				case 0x1d: sex();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x1e: exg();	   m6809_ICount-= 8; break;
				case 0x1f: _opcodes.tfr();	   m6809_ICount[0]-= 6; break;
				case 0x20: _opcodes.bra();	   m6809_ICount[0]-= 3; break;
/*TODO*///				case 0x21: brn();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x22: bhi();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x23: bls();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x24: bcc();	   m6809_ICount-= 3; break;
				case 0x25: _opcodes.bcs();	   m6809_ICount[0]-= 3; break;
				case 0x26: _opcodes.bne();	   m6809_ICount[0]-= 3; break;
				case 0x27: _opcodes.beq();	   m6809_ICount[0]-= 3; break;
/*TODO*///				case 0x28: bvc();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x29: bvs();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2a: bpl();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2b: bmi();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2c: bge();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2d: blt();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2e: bgt();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x2f: ble();	   m6809_ICount-= 3; break;
				case 0x30: _opcodes.leax();	   m6809_ICount[0]-= 4; break;
/*TODO*///				case 0x31: leay();	   m6809_ICount-= 4; break;
/*TODO*///				case 0x32: leas();	   m6809_ICount-= 4; break;
/*TODO*///				case 0x33: leau();	   m6809_ICount-= 4; break;
/*TODO*///				case 0x34: pshs();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x35: puls();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x36: pshu();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x37: pulu();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x38: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x39: rts();	   m6809_ICount-= 5; break;
/*TODO*///				case 0x3a: abx();	   m6809_ICount-= 3; break;
/*TODO*///				case 0x3b: rti();	   m6809_ICount-= 6; break;
/*TODO*///				case 0x3c: cwai();	   m6809_ICount-=20; break;
				case 0x3d: _opcodes.mul();	   m6809_ICount[0]-=11; break;
/*TODO*///				case 0x3e: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x3f: swi();	   m6809_ICount-=19; break;
/*TODO*///				case 0x40: nega();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x41: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x42: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x43: coma();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x44: lsra();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x45: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x46: rora();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x47: asra();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x48: asla();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x49: rola();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x4a: deca();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x4b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x4c: inca();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x4d: tsta();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x4e: illegal();  m6809_ICount-= 2; break;
				case 0x4f: _opcodes.clra();	   m6809_ICount[0]-= 2; break;
/*TODO*///				case 0x50: negb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x51: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x52: illegal();  m6809_ICount-= 2; break;
				case 0x53: _opcodes.comb();	   m6809_ICount[0]-= 2; break;
/*TODO*///				case 0x54: lsrb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x55: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x56: rorb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x57: asrb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x58: aslb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x59: rolb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x5a: decb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x5b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x5c: incb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x5d: tstb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x5e: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x5f: clrb();	   m6809_ICount-= 2; break;
/*TODO*///				case 0x60: neg_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x61: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x62: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x63: com_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x64: lsr_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x65: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x66: ror_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x67: asr_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x68: asl_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x69: rol_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x6a: dec_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x6b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x6c: inc_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x6d: tst_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x6e: jmp_ix();   m6809_ICount-= 3; break;
/*TODO*///				case 0x6f: clr_ix();   m6809_ICount-= 6; break;
/*TODO*///				case 0x70: neg_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x71: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x72: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x73: com_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x74: lsr_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x75: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x76: ror_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x77: asr_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x78: asl_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x79: rol_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x7a: dec_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x7b: illegal();  m6809_ICount-= 2; break;
/*TODO*///				case 0x7c: inc_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x7d: tst_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x7e: jmp_ex();   m6809_ICount-= 4; break;
/*TODO*///				case 0x7f: clr_ex();   m6809_ICount-= 7; break;
/*TODO*///				case 0x80: suba_im();  m6809_ICount-= 2; break;
				case 0x81: _opcodes.cmpa_im();  m6809_ICount[0]-= 2; break;
/*TODO*///				case 0x82: sbca_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0x83: subd_im();  m6809_ICount-= 4; break;
/*TODO*///				case 0x84: anda_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0x85: bita_im();  m6809_ICount-= 2; break;
				case 0x86: _opcodes.lda_im();   m6809_ICount[0]-= 2; break;
/*TODO*///				case 0x87: sta_im();   m6809_ICount-= 2; break;
/*TODO*///				case 0x88: eora_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0x89: adca_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0x8a: ora_im();   m6809_ICount-= 2; break;
				case 0x8b: _opcodes.adda_im();  m6809_ICount[0]-= 2; break;
				case 0x8c: _opcodes.cmpx_im();  m6809_ICount[0]-= 4; break;
/*TODO*///				case 0x8d: bsr();	   m6809_ICount-= 7; break;
				case 0x8e: _opcodes.ldx_im();   m6809_ICount[0]-= 3; break;
/*TODO*///				case 0x8f: stx_im();   m6809_ICount-= 2; break;
/*TODO*///				case 0x90: suba_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x91: cmpa_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x92: sbca_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x93: subd_di();  m6809_ICount-= 6; break;
/*TODO*///				case 0x94: anda_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x95: bita_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x96: lda_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0x97: sta_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0x98: eora_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x99: adca_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x9a: ora_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0x9b: adda_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0x9c: cmpx_di();  m6809_ICount-= 6; break;
/*TODO*///				case 0x9d: jsr_di();   m6809_ICount-= 7; break;
/*TODO*///				case 0x9e: ldx_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0x9f: stx_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0xa0: suba_ix();  m6809_ICount-= 4; break;
				case 0xa1: _opcodes.cmpa_ix();  m6809_ICount[0]-= 4; break;
/*TODO*///				case 0xa2: sbca_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xa3: subd_ix();  m6809_ICount-= 6; break;
/*TODO*///				case 0xa4: anda_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xa5: bita_ix();  m6809_ICount-= 4; break;
				case 0xa6: _opcodes.lda_ix();   m6809_ICount[0]-= 4; break;
				case 0xa7: _opcodes.sta_ix();   m6809_ICount[0]-= 4; break;
/*TODO*///				case 0xa8: eora_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xa9: adca_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xaa: ora_ix();   m6809_ICount-= 4; break;
/*TODO*///				case 0xab: adda_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xac: cmpx_ix();  m6809_ICount-= 6; break;
/*TODO*///				case 0xad: jsr_ix();   m6809_ICount-= 7; break;
/*TODO*///				case 0xae: ldx_ix();   m6809_ICount-= 5; break;
/*TODO*///				case 0xaf: stx_ix();   m6809_ICount-= 5; break;
/*TODO*///				case 0xb0: suba_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb1: cmpa_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb2: sbca_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb3: subd_ex();  m6809_ICount-= 7; break;
/*TODO*///				case 0xb4: anda_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb5: bita_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb6: lda_ex();   m6809_ICount-= 5; break;
				case 0xb7: _opcodes.sta_ex();   m6809_ICount[0]-= 5; break;
/*TODO*///				case 0xb8: eora_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xb9: adca_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xba: ora_ex();   m6809_ICount-= 5; break;
/*TODO*///				case 0xbb: adda_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xbc: cmpx_ex();  m6809_ICount-= 7; break;
/*TODO*///				case 0xbd: jsr_ex();   m6809_ICount-= 8; break;
/*TODO*///				case 0xbe: ldx_ex();   m6809_ICount-= 6; break;
/*TODO*///				case 0xbf: stx_ex();   m6809_ICount-= 6; break;
/*TODO*///				case 0xc0: subb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xc1: cmpb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xc2: sbcb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xc3: addd_im();  m6809_ICount-= 4; break;
/*TODO*///				case 0xc4: andb_im();  m6809_ICount-= 2; break;
				case 0xc5: _opcodes.bitb_im();  m6809_ICount[0]-= 2; break;
				case 0xc6: _opcodes.ldb_im();   m6809_ICount[0]-= 2; break;
/*TODO*///				case 0xc7: stb_im();   m6809_ICount-= 2; break;
/*TODO*///				case 0xc8: eorb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xc9: adcb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xca: orb_im();   m6809_ICount-= 2; break;
/*TODO*///				case 0xcb: addb_im();  m6809_ICount-= 2; break;
/*TODO*///				case 0xcc: ldd_im();   m6809_ICount-= 3; break;
/*TODO*///				case 0xcd: std_im();   m6809_ICount-= 2; break;
				case 0xce: _opcodes.ldu_im();   m6809_ICount[0]-= 3; break;
/*TODO*///				case 0xcf: stu_im();   m6809_ICount-= 3; break;
/*TODO*///				case 0xd0: subb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd1: cmpb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd2: sbcb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd3: addd_di();  m6809_ICount-= 6; break;
/*TODO*///				case 0xd4: andb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd5: bitb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd6: ldb_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0xd7: stb_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0xd8: eorb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xd9: adcb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xda: orb_di();   m6809_ICount-= 4; break;
/*TODO*///				case 0xdb: addb_di();  m6809_ICount-= 4; break;
/*TODO*///				case 0xdc: ldd_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0xdd: std_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0xde: ldu_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0xdf: stu_di();   m6809_ICount-= 5; break;
/*TODO*///				case 0xe0: subb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe1: cmpb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe2: sbcb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe3: addd_ix();  m6809_ICount-= 6; break;
/*TODO*///				case 0xe4: andb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe5: bitb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe6: ldb_ix();   m6809_ICount-= 4; break;
				case 0xe7: _opcodes.stb_ix();   m6809_ICount[0]-= 4; break;
/*TODO*///				case 0xe8: eorb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xe9: adcb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xea: orb_ix();   m6809_ICount-= 4; break;
/*TODO*///				case 0xeb: addb_ix();  m6809_ICount-= 4; break;
/*TODO*///				case 0xec: ldd_ix();   m6809_ICount-= 5; break;
/*TODO*///				case 0xed: std_ix();   m6809_ICount-= 5; break;
/*TODO*///				case 0xee: ldu_ix();   m6809_ICount-= 5; break;
				case 0xef: _opcodes.stu_ix();   m6809_ICount[0]-= 5; break;
/*TODO*///				case 0xf0: subb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xf1: cmpb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xf2: sbcb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xf3: addd_ex();  m6809_ICount-= 7; break;
/*TODO*///				case 0xf4: andb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xf5: bitb_ex();  m6809_ICount-= 5; break;
				case 0xf6: _opcodes.ldb_ex();   m6809_ICount[0]-= 5; break;
				case 0xf7: _opcodes.stb_ex();   m6809_ICount[0]-= 5; break;
/*TODO*///				case 0xf8: eorb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xf9: adcb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xfa: orb_ex();   m6809_ICount-= 5; break;
/*TODO*///				case 0xfb: addb_ex();  m6809_ICount-= 5; break;
/*TODO*///				case 0xfc: ldd_ex();   m6809_ICount-= 6; break;
/*TODO*///				case 0xfd: std_ex();   m6809_ICount-= 6; break;
/*TODO*///				case 0xfe: ldu_ex();   m6809_ICount-= 6; break;
/*TODO*///				case 0xff: stu_ex();   m6809_ICount-= 6; break;
                                    default:
                                        throw new UnsupportedOperationException("Opcode "+_m6809.ireg+" not implemented!");
				}
/*TODO*///	#else
/*TODO*///	            (*m6809_main[m6809.ireg])();
/*TODO*///	            m6809_ICount -= cycles1[m6809.ireg];
/*TODO*///	#endif
	
			} while( m6809_ICount[0] > 0 );
	
	        m6809_ICount[0] -= _m6809.extra_cycles;
		_m6809.extra_cycles = 0;
	    }
	
	    return cycles - m6809_ICount[0];   /* NS 970908 */
	}
	
	public void fetch_effective_address()
	{
            int postbyte = ROP_ARG(_m6809.pc) & 0xFF;
            _m6809.pc++;
    
    	
	
            switch(postbyte)
            {
		case 0x00: ea=_m6809.x; m6809_ICount[0]-=1;   break;
		case 0x01: ea=_m6809.x+1; m6809_ICount[0]-=1;   break;
		case 0x02: ea=_m6809.x+2; m6809_ICount[0]-=1;   break;
		case 0x03: ea=_m6809.x+3; m6809_ICount[0]-=1;   break;
		case 0x04: ea=_m6809.x+4; m6809_ICount[0]-=1;   break;
		case 0x05: ea=_m6809.x+5; m6809_ICount[0]-=1;   break;
		case 0x06: ea=_m6809.x+6; m6809_ICount[0]-=1;   break;
		case 0x07: ea=_m6809.x+7; m6809_ICount[0]-=1;   break;
		case 0x08: ea=_m6809.x+8; m6809_ICount[0]-=1;   break;
		case 0x09: ea=_m6809.x+9; m6809_ICount[0]-=1;   break;
		case 0x0a: ea=_m6809.x+10; m6809_ICount[0]-=1;   break;
		case 0x0b: ea=_m6809.x+11; m6809_ICount[0]-=1;   break;
		case 0x0c: ea=_m6809.x+12; m6809_ICount[0]-=1;   break;
		case 0x0d: ea=_m6809.x+13; m6809_ICount[0]-=1;   break;
		case 0x0e: ea=_m6809.x+14; m6809_ICount[0]-=1;   break;
		case 0x0f: ea=_m6809.x+15; m6809_ICount[0]-=1;   break;
	
		case 0x10: ea=_m6809.x-16; 											m6809_ICount[0]-=1;   break;
		case 0x11: ea=_m6809.x-15; 											m6809_ICount[0]-=1;   break;
		case 0x12: ea=_m6809.x-14; 											m6809_ICount[0]-=1;   break;
		case 0x13: ea=_m6809.x-13; 											m6809_ICount[0]-=1;   break;
		case 0x14: ea=_m6809.x-12; 											m6809_ICount[0]-=1;   break;
		case 0x15: ea=_m6809.x-11; 											m6809_ICount[0]-=1;   break;
		case 0x16: ea=_m6809.x-10; 											m6809_ICount[0]-=1;   break;
		case 0x17: ea=_m6809.x-9;												m6809_ICount[0]-=1;   break;
		case 0x18: ea=_m6809.x-8;												m6809_ICount[0]-=1;   break;
		case 0x19: ea=_m6809.x-7;												m6809_ICount[0]-=1;   break;
		case 0x1a: ea=_m6809.x-6;												m6809_ICount[0]-=1;   break;
		case 0x1b: ea=_m6809.x-5;												m6809_ICount[0]-=1;   break;
		case 0x1c: ea=_m6809.x-4;												m6809_ICount[0]-=1;   break;
		case 0x1d: ea=_m6809.x-3;												m6809_ICount[0]-=1;   break;
		case 0x1e: ea=_m6809.x-2;												m6809_ICount[0]-=1;   break;
		case 0x1f: ea=_m6809.x-1;												m6809_ICount[0]-=1;   break;
	
		case 0x20: ea=_m6809.y;												m6809_ICount[0]-=1;   break;
		case 0x21: ea=_m6809.y+1;												m6809_ICount[0]-=1;   break;
		case 0x22: ea=_m6809.y+2;												m6809_ICount[0]-=1;   break;
		case 0x23: ea=_m6809.y+3;												m6809_ICount[0]-=1;   break;
		case 0x24: ea=_m6809.y+4;												m6809_ICount[0]-=1;   break;
		case 0x25: ea=_m6809.y+5;												m6809_ICount[0]-=1;   break;
		case 0x26: ea=_m6809.y+6;												m6809_ICount[0]-=1;   break;
		case 0x27: ea=_m6809.y+7;												m6809_ICount[0]-=1;   break;
		case 0x28: ea=_m6809.y+8;												m6809_ICount[0]-=1;   break;
		case 0x29: ea=_m6809.y+9;												m6809_ICount[0]-=1;   break;
		case 0x2a: ea=_m6809.y+10; 											m6809_ICount[0]-=1;   break;
		case 0x2b: ea=_m6809.y+11; 											m6809_ICount[0]-=1;   break;
		case 0x2c: ea=_m6809.y+12; 											m6809_ICount[0]-=1;   break;
		case 0x2d: ea=_m6809.y+13; 											m6809_ICount[0]-=1;   break;
		case 0x2e: ea=_m6809.y+14; 											m6809_ICount[0]-=1;   break;
		case 0x2f: ea=_m6809.y+15; 											m6809_ICount[0]-=1;   break;
	
		case 0x30: ea=_m6809.y-16; 											m6809_ICount[0]-=1;   break;
		case 0x31: ea=_m6809.y-15; 											m6809_ICount[0]-=1;   break;
		case 0x32: ea=_m6809.y-14; 											m6809_ICount[0]-=1;   break;
		case 0x33: ea=_m6809.y-13; 											m6809_ICount[0]-=1;   break;
		case 0x34: ea=_m6809.y-12; 											m6809_ICount[0]-=1;   break;
		case 0x35: ea=_m6809.y-11; 											m6809_ICount[0]-=1;   break;
		case 0x36: ea=_m6809.y-10; 											m6809_ICount[0]-=1;   break;
		case 0x37: ea=_m6809.y-9;												m6809_ICount[0]-=1;   break;
		case 0x38: ea=_m6809.y-8;												m6809_ICount[0]-=1;   break;
		case 0x39: ea=_m6809.y-7;												m6809_ICount[0]-=1;   break;
		case 0x3a: ea=_m6809.y-6;												m6809_ICount[0]-=1;   break;
		case 0x3b: ea=_m6809.y-5;												m6809_ICount[0]-=1;   break;
		case 0x3c: ea=_m6809.y-4;												m6809_ICount[0]-=1;   break;
		case 0x3d: ea=_m6809.y-3;												m6809_ICount[0]-=1;   break;
		case 0x3e: ea=_m6809.y-2;												m6809_ICount[0]-=1;   break;
		case 0x3f: ea=_m6809.y-1;												m6809_ICount[0]-=1;   break;
	
		case 0x40: ea=_m6809.u;												m6809_ICount[0]-=1;   break;
		case 0x41: ea=_m6809.u+1;												m6809_ICount[0]-=1;   break;
		case 0x42: ea=_m6809.u+2;												m6809_ICount[0]-=1;   break;
		case 0x43: ea=_m6809.u+3;												m6809_ICount[0]-=1;   break;
		case 0x44: ea=_m6809.u+4;												m6809_ICount[0]-=1;   break;
		case 0x45: ea=_m6809.u+5;												m6809_ICount[0]-=1;   break;
		case 0x46: ea=_m6809.u+6;												m6809_ICount[0]-=1;   break;
		case 0x47: ea=_m6809.u+7;												m6809_ICount[0]-=1;   break;
		case 0x48: ea=_m6809.u+8;												m6809_ICount[0]-=1;   break;
		case 0x49: ea=_m6809.u+9;												m6809_ICount[0]-=1;   break;
		case 0x4a: ea=_m6809.u+10; 											m6809_ICount[0]-=1;   break;
		case 0x4b: ea=_m6809.u+11; 											m6809_ICount[0]-=1;   break;
		case 0x4c: ea=_m6809.u+12; 											m6809_ICount[0]-=1;   break;
		case 0x4d: ea=_m6809.u+13; 											m6809_ICount[0]-=1;   break;
		case 0x4e: ea=_m6809.u+14; 											m6809_ICount[0]-=1;   break;
		case 0x4f: ea=_m6809.u+15; 											m6809_ICount[0]-=1;   break;
	
		case 0x50: ea=_m6809.u-16; 											m6809_ICount[0]-=1;   break;
		case 0x51: ea=_m6809.u-15; 											m6809_ICount[0]-=1;   break;
		case 0x52: ea=_m6809.u-14; 											m6809_ICount[0]-=1;   break;
		case 0x53: ea=_m6809.u-13; 											m6809_ICount[0]-=1;   break;
		case 0x54: ea=_m6809.u-12; 											m6809_ICount[0]-=1;   break;
		case 0x55: ea=_m6809.u-11; 											m6809_ICount[0]-=1;   break;
		case 0x56: ea=_m6809.u-10; 											m6809_ICount[0]-=1;   break;
		case 0x57: ea=_m6809.u-9;												m6809_ICount[0]-=1;   break;
		case 0x58: ea=_m6809.u-8;												m6809_ICount[0]-=1;   break;
		case 0x59: ea=_m6809.u-7;												m6809_ICount[0]-=1;   break;
		case 0x5a: ea=_m6809.u-6;												m6809_ICount[0]-=1;   break;
		case 0x5b: ea=_m6809.u-5;												m6809_ICount[0]-=1;   break;
		case 0x5c: ea=_m6809.u-4;												m6809_ICount[0]-=1;   break;
		case 0x5d: ea=_m6809.u-3;												m6809_ICount[0]-=1;   break;
		case 0x5e: ea=_m6809.u-2;												m6809_ICount[0]-=1;   break;
		case 0x5f: ea=_m6809.u-1;												m6809_ICount[0]-=1;   break;
	
		case 0x60: ea=_m6809.s;												m6809_ICount[0]-=1;   break;
		case 0x61: ea=_m6809.s+1;												m6809_ICount[0]-=1;   break;
		case 0x62: ea=_m6809.s+2;												m6809_ICount[0]-=1;   break;
		case 0x63: ea=_m6809.s+3;												m6809_ICount[0]-=1;   break;
		case 0x64: ea=_m6809.s+4;												m6809_ICount[0]-=1;   break;
		case 0x65: ea=_m6809.s+5;												m6809_ICount[0]-=1;   break;
		case 0x66: ea=_m6809.s+6;												m6809_ICount[0]-=1;   break;
		case 0x67: ea=_m6809.s+7;												m6809_ICount[0]-=1;   break;
		case 0x68: ea=_m6809.s+8;												m6809_ICount[0]-=1;   break;
		case 0x69: ea=_m6809.s+9;												m6809_ICount[0]-=1;   break;
		case 0x6a: ea=_m6809.s+10; 											m6809_ICount[0]-=1;   break;
		case 0x6b: ea=_m6809.s+11; 											m6809_ICount[0]-=1;   break;
		case 0x6c: ea=_m6809.s+12; 											m6809_ICount[0]-=1;   break;
		case 0x6d: ea=_m6809.s+13; 											m6809_ICount[0]-=1;   break;
		case 0x6e: ea=_m6809.s+14; 											m6809_ICount[0]-=1;   break;
		case 0x6f: ea=_m6809.s+15; 											m6809_ICount[0]-=1;   break;
	
		case 0x70: ea=_m6809.s-16; 											m6809_ICount[0]-=1;   break;
		case 0x71: ea=_m6809.s-15; 											m6809_ICount[0]-=1;   break;
		case 0x72: ea=_m6809.s-14; 											m6809_ICount[0]-=1;   break;
		case 0x73: ea=_m6809.s-13; 											m6809_ICount[0]-=1;   break;
		case 0x74: ea=_m6809.s-12; 											m6809_ICount[0]-=1;   break;
		case 0x75: ea=_m6809.s-11; 											m6809_ICount[0]-=1;   break;
		case 0x76: ea=_m6809.s-10; 											m6809_ICount[0]-=1;   break;
		case 0x77: ea=_m6809.s-9;												m6809_ICount[0]-=1;   break;
		case 0x78: ea=_m6809.s-8;												m6809_ICount[0]-=1;   break;
		case 0x79: ea=_m6809.s-7;												m6809_ICount[0]-=1;   break;
		case 0x7a: ea=_m6809.s-6;												m6809_ICount[0]-=1;   break;
		case 0x7b: ea=_m6809.s-5;												m6809_ICount[0]-=1;   break;
		case 0x7c: ea=_m6809.s-4;												m6809_ICount[0]-=1;   break;
		case 0x7d: ea=_m6809.s-3;												m6809_ICount[0]-=1;   break;
		case 0x7e: ea=_m6809.s-2;												m6809_ICount[0]-=1;   break;
		case 0x7f: ea=_m6809.s-1;												m6809_ICount[0]-=1;   break;
	
		case 0x80: ea=_m6809.x;	_m6809.x++;										m6809_ICount[0]-=2;   break;
		case 0x81: ea=_m6809.x;	_m6809.x+=2;										m6809_ICount[0]-=3;   break;
		case 0x82: _m6809.x--; 	ea=_m6809.x;										m6809_ICount[0]-=2;   break;
		case 0x83: _m6809.x-=2;	ea=_m6809.x;										m6809_ICount[0]-=3;   break;
		case 0x84: ea=_m6809.x;																   break;
		case 0x85: ea=_m6809.x+SIGNED(_m6809.b);										m6809_ICount[0]-=1;   break;
		case 0x86: ea=_m6809.x+SIGNED(_m6809.a);										m6809_ICount[0]-=1;   break;
		case 0x87: ea=0;																   break; /*   ILLEGAL*/
		case 0x88: ea=IMMBYTE(); 	ea=_m6809.x+SIGNED(ea);					m6809_ICount[0]-=1;   break; /* this is a hack to make Vectrex work. It should be m6809_ICount[0]-=1. Dunno where the cycle was lost :( */
		case 0x89: ea=IMMWORD(); 	ea+=_m6809.x;								m6809_ICount[0]-=4;   break;
		case 0x8a: ea=0;																   break; /*   ILLEGAL*/
		case 0x8b: ea=_m6809.x+_opcodes.getDreg() &0xFFFF;												m6809_ICount[0]-=4;   break;
		case 0x8c: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0x8d: ea=IMMWORD(); 	ea+=_m6809.pc; 							m6809_ICount[0]-=5;   break;
		case 0x8e: ea=0;																   break; /*   ILLEGAL*/
		case 0x8f: ea=IMMWORD(); 										m6809_ICount[0]-=5;   break;
	
		case 0x90: ea=_m6809.x;	_m6809.x++;						ea=RM16(ea);	m6809_ICount[0]-=5;   break; /* Indirect ,R+ not in my specs */
		case 0x91: ea=_m6809.x;	_m6809.x+=2;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0x92: _m6809.x--; 	ea=_m6809.x;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0x93: _m6809.x-=2;	ea=_m6809.x;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0x94: ea=_m6809.x;								ea=RM16(ea);	m6809_ICount[0]-=3;   break;
		case 0x95: ea=_m6809.x+SIGNED(_m6809.b);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0x96: ea=_m6809.x+SIGNED(_m6809.a);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0x97: ea=0;																   break; /*   ILLEGAL*/
		case 0x98: ea=IMMBYTE(); 	ea=_m6809.x+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0x99: ea=IMMWORD(); 	ea+=_m6809.x;				ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0x9a: ea=0;																   break; /*   ILLEGAL*/
		case 0x9b: ea=_m6809.x+_opcodes.getDreg();								ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0x9c: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0x9d: ea=IMMWORD(); 	ea+=_m6809.pc; 			ea=RM16(ea);	m6809_ICount[0]-=8;   break;
		case 0x9e: ea=0;																   break; /*   ILLEGAL*/
		case 0x9f: ea=IMMWORD(); 						ea=RM16(ea);	m6809_ICount[0]-=8;   break;
	
		case 0xa0: ea=_m6809.y;	_m6809.y++;										m6809_ICount[0]-=2;   break;
		case 0xa1: ea=_m6809.y;	_m6809.y+=2;										m6809_ICount[0]-=3;   break;
		case 0xa2: _m6809.y--; 	ea=_m6809.y;										m6809_ICount[0]-=2;   break;
		case 0xa3: _m6809.y-=2;	ea=_m6809.y;										m6809_ICount[0]-=3;   break;
		case 0xa4: ea=_m6809.y;																   break;
		case 0xa5: ea=_m6809.y+SIGNED(_m6809.b);										m6809_ICount[0]-=1;   break;
		case 0xa6: ea=_m6809.y+SIGNED(_m6809.a);										m6809_ICount[0]-=1;   break;
		case 0xa7: ea=0;																   break; /*   ILLEGAL*/
		case 0xa8: ea=IMMBYTE(); 	ea=_m6809.y+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xa9: ea=IMMWORD(); 	ea+=_m6809.y;								m6809_ICount[0]-=4;   break;
		case 0xaa: ea=0;																   break; /*   ILLEGAL*/
		case 0xab: ea=_m6809.y+_opcodes.getDreg();												m6809_ICount[0]-=4;   break;
		case 0xac: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xad: ea=IMMWORD(); 	ea+=_m6809.pc; 							m6809_ICount[0]-=5;   break;
		case 0xae: ea=0;																   break; /*   ILLEGAL*/
		case 0xaf: ea=IMMWORD(); 										m6809_ICount[0]-=5;   break;
	
		case 0xb0: ea=_m6809.y;	_m6809.y++;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xb1: ea=_m6809.y;	_m6809.y+=2;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xb2: _m6809.y--; 	ea=_m6809.y;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xb3: _m6809.y-=2;	ea=_m6809.y;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xb4: ea=_m6809.y;								ea=RM16(ea);	m6809_ICount[0]-=3;   break;
		case 0xb5: ea=_m6809.y+SIGNED(_m6809.b);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xb6: ea=_m6809.y+SIGNED(_m6809.a);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xb7: ea=0;																   break; /*   ILLEGAL*/
		case 0xb8: ea=IMMBYTE(); 	ea=_m6809.y+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xb9: ea=IMMWORD(); 	ea+=_m6809.y;				ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xba: ea=0;																   break; /*   ILLEGAL*/
		case 0xbb: ea=_m6809.y+_opcodes.getDreg();								ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xbc: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xbd: ea=IMMWORD(); 	ea+=_m6809.pc; 			ea=RM16(ea);	m6809_ICount[0]-=8;   break;
		case 0xbe: ea=0;																   break; /*   ILLEGAL*/
		case 0xbf: ea=IMMWORD(); 						ea=RM16(ea);	m6809_ICount[0]-=8;   break;
	
		case 0xc0: ea=_m6809.u&0xFFFF;			_m6809.u++;								m6809_ICount[0]-=2;   break;
		case 0xc1: ea=_m6809.u;			_m6809.u+=2;								m6809_ICount[0]-=3;   break;
		case 0xc2: _m6809.u--; 			ea=_m6809.u;								m6809_ICount[0]-=2;   break;
		case 0xc3: _m6809.u-=2;			ea=_m6809.u;								m6809_ICount[0]-=3;   break;
		case 0xc4: ea=_m6809.u;																   break;
		case 0xc5: ea=_m6809.u+SIGNED(_m6809.b);										m6809_ICount[0]-=1;   break;
		case 0xc6: ea=_m6809.u+SIGNED(_m6809.a);										m6809_ICount[0]-=1;   break;
		case 0xc7: ea=0;																   break; /*ILLEGAL*/
		case 0xc8: ea=IMMBYTE(); 	ea=_m6809.u+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xc9: ea=IMMWORD(); 	ea+=_m6809.u;								m6809_ICount[0]-=4;   break;
		case 0xca: ea=0;																   break; /*ILLEGAL*/
		case 0xcb: ea=_m6809.u+_opcodes.getDreg();												m6809_ICount[0]-=4;   break;
		case 0xcc: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xcd: ea=IMMWORD(); 	ea+=_m6809.pc; 							m6809_ICount[0]-=5;   break;
		case 0xce: ea=0;																   break; /*ILLEGAL*/
		case 0xcf: ea=IMMWORD(); 										m6809_ICount[0]-=5;   break;
	
		case 0xd0: ea=_m6809.u;	_m6809.u++;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xd1: ea=_m6809.u;	_m6809.u+=2;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xd2: _m6809.u--; 	ea=_m6809.u;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xd3: _m6809.u-=2;	ea=_m6809.u;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xd4: ea=_m6809.u;								ea=RM16(ea);	m6809_ICount[0]-=3;   break;
		case 0xd5: ea=_m6809.u+SIGNED(_m6809.b);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xd6: ea=_m6809.u+SIGNED(_m6809.a);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xd7: ea=0;																   break; /*ILLEGAL*/
		case 0xd8: ea=IMMBYTE(); 	ea=_m6809.u+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xd9: ea=IMMWORD(); 	ea+=_m6809.u;				ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xda: ea=0;																   break; /*ILLEGAL*/
		case 0xdb: ea=_m6809.u+_opcodes.getDreg();								ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xdc: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xdd: ea=IMMWORD(); 	ea+=_m6809.pc; 			ea=RM16(ea);	m6809_ICount[0]-=8;   break;
		case 0xde: ea=0;																   break; /*ILLEGAL*/
		case 0xdf: ea=IMMWORD(); 						ea=RM16(ea);	m6809_ICount[0]-=8;   break;
	
		case 0xe0: ea=_m6809.s;	_m6809.s++;										m6809_ICount[0]-=2;   break;
		case 0xe1: ea=_m6809.s;	_m6809.s+=2;										m6809_ICount[0]-=3;   break;
		case 0xe2: _m6809.s--; 	ea=_m6809.s;										m6809_ICount[0]-=2;   break;
		case 0xe3: _m6809.s-=2;	ea=_m6809.s;										m6809_ICount[0]-=3;   break;
		case 0xe4: ea=_m6809.s;																   break;
		case 0xe5: ea=_m6809.s+SIGNED(_m6809.b);										m6809_ICount[0]-=1;   break;
		case 0xe6: ea=_m6809.s+SIGNED(_m6809.a);										m6809_ICount[0]-=1;   break;
		case 0xe7: ea=0;																   break; /*ILLEGAL*/
		case 0xe8: ea=IMMBYTE(); 	ea=_m6809.s+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xe9: ea=IMMWORD(); 	ea+=_m6809.s;								m6809_ICount[0]-=4;   break;
		case 0xea: ea=0;																   break; /*ILLEGAL*/
		case 0xeb: ea=_m6809.s+_opcodes.getDreg();												m6809_ICount[0]-=4;   break;
		case 0xec: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);					m6809_ICount[0]-=1;   break;
		case 0xed: ea=IMMWORD(); 	ea+=_m6809.pc; 							m6809_ICount[0]-=5;   break;
		case 0xee: ea=0;																   break;  /*ILLEGAL*/
		case 0xef: ea=IMMWORD(); 										m6809_ICount[0]-=5;   break;
	
		case 0xf0: ea=_m6809.s;	_m6809.s++;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xf1: ea=_m6809.s;	_m6809.s+=2;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xf2: _m6809.s--; 	ea=_m6809.s;						ea=RM16(ea);	m6809_ICount[0]-=5;   break;
		case 0xf3: _m6809.s-=2;	ea=_m6809.s;						ea=RM16(ea);	m6809_ICount[0]-=6;   break;
		case 0xf4: ea=_m6809.s;								ea=RM16(ea);	m6809_ICount[0]-=3;   break;
		case 0xf5: ea=_m6809.s+SIGNED(_m6809.b);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xf6: ea=_m6809.s+SIGNED(_m6809.a);						ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xf7: ea=0;																   break; /*ILLEGAL*/
		case 0xf8: ea=IMMBYTE(); 	ea=_m6809.s+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xf9: ea=IMMWORD(); 	ea+=_m6809.s;				ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xfa: ea=0;																   break; /*ILLEGAL*/
		case 0xfb: ea=_m6809.s+_opcodes.getDreg();								ea=RM16(ea);	m6809_ICount[0]-=7;   break;
		case 0xfc: ea=IMMBYTE(); 	ea=_m6809.pc+SIGNED(ea);	ea=RM16(ea);	m6809_ICount[0]-=4;   break;
		case 0xfd: ea=IMMWORD(); 	ea+=_m6809.pc; 			ea=RM16(ea);	m6809_ICount[0]-=8;   break;
		case 0xfe: ea=0;																   break; /*ILLEGAL*/
		case 0xff: ea=IMMWORD(); 						ea=RM16(ea);	m6809_ICount[0]-=8;   break;
		}
	}
    
}
