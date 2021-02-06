/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86intfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.modrmH.Mod_RM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_setOPbase20;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;

public class i86 extends cpu_interface {

    public static int[] i86_ICount = new int[1];

    public i86() {
        cpu_num = CPU_I86;
        num_irqs = 1;
        default_vector = 0;
        overclock = 1.0;
        no_int = I86_INT_NONE;
        irq_int = -1000;
        nmi_int = I86_NMI_INT;
        address_bits = 20;
        address_shift = 0;
        endianess = CPU_IS_LE;
        align_unit = 1;
        max_inst_len = 5;
        abits1 = ABITS1_20;
        abits2 = ABITS2_20;
        abitsmin = ABITS_MIN_20;
        icount = i86_ICount;
    }

    /*TODO*///#include "host.h"
/*TODO*///#include "cpuintrf.h"
/*TODO*///#include "memory.h"
/*TODO*///#include "mamedbg.h"
/*TODO*///#include "mame.h"
/*TODO*///
/*TODO*///#include "i86.h"
/*TODO*///#include "i86intf.h"
/*TODO*///
/*TODO*///
    /* All pre-i286 CPUs have a 1MB address space */
    public static final int AMASK = 0xfffff;

    /*TODO*///
/*TODO*///
/*TODO*///static UINT8 i86_reg_layout[] =
/*TODO*///{
/*TODO*///	I86_AX, I86_BX, I86_DS, I86_ES, I86_SS, I86_FLAGS, I86_CS, I86_VECTOR, -1,
/*TODO*///	I86_CX, I86_DX, I86_SI, I86_DI, I86_SP, I86_BP, I86_IP,
/*TODO*///	I86_IRQ_STATE, I86_NMI_STATE, 0
/*TODO*///};
/*TODO*///
/*TODO*////* Layout of the debugger windows x,y,w,h */
/*TODO*///static UINT8 i86_win_layout[] =
/*TODO*///{
/*TODO*///	0, 0, 80, 2,					   /* register window (top rows) */
/*TODO*///	0, 3, 34, 19,					   /* disassembler window (left colums) */
/*TODO*///	35, 3, 45, 9,					   /* memory #1 window (right, upper middle) */
/*TODO*///	35, 13, 45, 9,					   /* memory #2 window (right, lower middle) */
/*TODO*///	0, 23, 80, 1,					   /* command line window (bottom rows) */
/*TODO*///};
/*TODO*///
/*TODO*////* I86 registers */
/*TODO*///typedef union
/*TODO*///{									   /* eight general registers */
/*TODO*///	UINT16 w[8];					   /* viewed as 16 bits registers */
/*TODO*///	UINT8 b[16];					   /* or as 8 bit registers */
/*TODO*///}
/*TODO*///i86basicregs;
/*TODO*///
    public static class i86_Regs {

        _regs regs = new _regs();//int[]   mainregs = new int[8];
        int/*UINT32*/ pc;
        int/*UINT32*/ prevpc;
        int[] /*UINT32*/ base = new int[4];
        int[] /*UINT16*/ sregs = new int[4];
        int/*UINT16*/ flags;
        public irqcallbacksPtr irq_callback;
        int AuxVal, OverVal, SignVal, ZeroVal, CarryVal, DirVal;
        /* 0 or non-0 valued flags */
        int /*UINT8*/ ParityVal;
        int /*UINT8*/ TF, IF;
        /* 0 or 1 valued flags */
        int /*UINT8*/ MF;
        /* V30 mode flag */
        int /*UINT8*/ int_vector;
        int /*INT8*/ nmi_state;
        int /*INT8*/ irq_state;
        int extra_cycles;

        /* extra cycles for interrupts */
        class _regs {

            public int[] w = new int[8];
            public int[] b = new int[16];

            public void SetB(int index, int val) {
                b[index] = val;
                w[(index >> 1)] = (b[((index & 0xFFFFFFFE) + 1)] << 8 | b[(index & 0xFFFFFFFE)]);
            }

            public void AddB(int index, int val) {
                b[index] = (b[index] + val & 0xFF);
                w[(index >> 1)] = (b[((index & 0xFFFFFFFE) + 1)] << 8 | b[(index & 0xFFFFFFFE)]);
            }

            public void SetW(int index, int val) {
                w[index] = val;
                index <<= 1;
                b[index] = (val & 0xFF);
                b[(index + 1)] = (val >> 8);
            }
        }
    }

    /**
     * ************************************************************************
     */
    /* cpu state                                                               */
    /**
     * ************************************************************************
     */
    static i86_Regs I = new i86_Regs();
    static int/*unsigned*/ prefix_base;/* base address of the latest prefix segment */
    static int /*char*/ seg_prefix;/* prefix segment indicator */

    static /*UINT8*/ int[] parity_table = new int[256];

    /*TODO*///
/*TODO*///static struct i86_timing cycles;
/*TODO*///
/*TODO*////* The interrupt number of a pending external interrupt pending NMI is 2.	*/
/*TODO*////* For INTR interrupts, the level is caught on the bus during an INTA cycle */
/*TODO*///
/*TODO*///#define PREFIX(name) i86##name
/*TODO*///#define PREFIX86(name) i86##name
/*TODO*///
/*TODO*///#define I86
/*TODO*///#include "instr86.h"
/*TODO*///#include "ea.h"
/*TODO*///#include "modrm.h"
/*TODO*///#include "table86.h"
/*TODO*///
/*TODO*///#include "instr86.c"
/*TODO*///#undef I86
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*///
/*TODO*////* ASG 971222 -- added these interface functions */
/*TODO*///
/*TODO*///unsigned i86_get_context(void *dst)
/*TODO*///{
/*TODO*///	if (dst)
/*TODO*///		*(i86_Regs *) dst = I;
/*TODO*///	return sizeof (i86_Regs);
/*TODO*///}
/*TODO*///
/*TODO*///void i86_set_context(void *src)
/*TODO*///{
/*TODO*///	if (src)
/*TODO*///	{
/*TODO*///		I = *(i86_Regs *)src;
/*TODO*///		I.base[CS] = SegBase(CS);
/*TODO*///		I.base[DS] = SegBase(DS);
/*TODO*///		I.base[ES] = SegBase(ES);
/*TODO*///		I.base[SS] = SegBase(SS);
/*TODO*///		change_pc20(I.pc);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void i86_set_pc(unsigned val)
/*TODO*///{
/*TODO*///	if (val - I.base[CS] >= 0x10000)
/*TODO*///	{
/*TODO*///		I.base[CS] = val & 0xffff0;
/*TODO*///		I.sregs[CS] = I.base[CS] >> 4;
/*TODO*///	}
/*TODO*///	I.pc = val;
/*TODO*///}
/*TODO*///
/*TODO*///unsigned i86_get_sp(void)
/*TODO*///{
/*TODO*///	return I.base[SS] + I.regs.w[SP];
/*TODO*///}
/*TODO*///
/*TODO*///void i86_set_sp(unsigned val)
/*TODO*///{
/*TODO*///	if (val - I.base[SS] < 0x10000)
/*TODO*///	{
/*TODO*///		I.regs.w[SP] = val - I.base[SS];
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		I.base[SS] = val & 0xffff0;
/*TODO*///		I.sregs[SS] = I.base[SS] >> 4;
/*TODO*///		I.regs.w[SP] = val & 0x0000f;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///unsigned i86_get_reg(int regnum)
/*TODO*///{
/*TODO*///	switch (regnum)
/*TODO*///	{
/*TODO*///	case I86_IP:		return I.pc - I.base[CS];
/*TODO*///	case I86_SP:		return I.regs.w[SP];
/*TODO*///	case I86_FLAGS: 	CompressFlags(); return I.flags;
/*TODO*///	case I86_AX:		return I.regs.w[AX];
/*TODO*///	case I86_CX:		return I.regs.w[CX];
/*TODO*///	case I86_DX:		return I.regs.w[DX];
/*TODO*///	case I86_BX:		return I.regs.w[BX];
/*TODO*///	case I86_BP:		return I.regs.w[BP];
/*TODO*///	case I86_SI:		return I.regs.w[SI];
/*TODO*///	case I86_DI:		return I.regs.w[DI];
/*TODO*///	case I86_ES:		return I.sregs[ES];
/*TODO*///	case I86_CS:		return I.sregs[CS];
/*TODO*///	case I86_SS:		return I.sregs[SS];
/*TODO*///	case I86_DS:		return I.sregs[DS];
/*TODO*///	case I86_VECTOR:	return I.int_vector;
/*TODO*///	case I86_PENDING:	return I.irq_state;
/*TODO*///	case I86_NMI_STATE: return I.nmi_state;
/*TODO*///	case I86_IRQ_STATE: return I.irq_state;
/*TODO*///	case REG_PREVIOUSPC:return I.prevpc;
/*TODO*///	default:
/*TODO*///		if (regnum <= REG_SP_CONTENTS)
/*TODO*///		{
/*TODO*///			unsigned offset = ((I.base[SS] + I.regs.w[SP]) & AMASK) + 2 * (REG_SP_CONTENTS - regnum);
/*TODO*///
/*TODO*///			if (offset < AMASK)
/*TODO*///				return cpu_readmem20(offset) | (cpu_readmem20(offset + 1) << 8);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///void i86_set_reg(int regnum, unsigned val)
/*TODO*///{
/*TODO*///	switch (regnum)
/*TODO*///	{
/*TODO*///	case I86_IP:		I.pc = I.base[CS] + val;	break;
/*TODO*///	case I86_SP:		I.regs.w[SP] = val; 		break;
/*TODO*///	case I86_FLAGS: 	I.flags = val;	ExpandFlags(val); break;
/*TODO*///	case I86_AX:		I.regs.w[AX] = val; 		break;
/*TODO*///	case I86_CX:		I.regs.w[CX] = val; 		break;
/*TODO*///	case I86_DX:		I.regs.w[DX] = val; 		break;
/*TODO*///	case I86_BX:		I.regs.w[BX] = val; 		break;
/*TODO*///	case I86_BP:		I.regs.w[BP] = val; 		break;
/*TODO*///	case I86_SI:		I.regs.w[SI] = val; 		break;
/*TODO*///	case I86_DI:		I.regs.w[DI] = val; 		break;
/*TODO*///	case I86_ES:		I.sregs[ES] = val;	I.base[ES] = SegBase(ES);	break;
/*TODO*///	case I86_CS:		I.sregs[CS] = val;	I.base[CS] = SegBase(CS);	break;
/*TODO*///	case I86_SS:		I.sregs[SS] = val;	I.base[SS] = SegBase(SS);	break;
/*TODO*///	case I86_DS:		I.sregs[DS] = val;	I.base[DS] = SegBase(DS);	break;
/*TODO*///	case I86_VECTOR:	I.int_vector = val; 		break;
/*TODO*///	case I86_PENDING:								break;
/*TODO*///	case I86_NMI_STATE: i86_set_nmi_line(val);		break;
/*TODO*///	case I86_IRQ_STATE: i86_set_irq_line(0, val);	break;
/*TODO*///	default:
/*TODO*///		if (regnum <= REG_SP_CONTENTS)
/*TODO*///		{
/*TODO*///			unsigned offset = ((I.base[SS] + I.regs.w[SP]) & AMASK) + 2 * (REG_SP_CONTENTS - regnum);
/*TODO*///
/*TODO*///			if (offset < AMASK - 1)
/*TODO*///			{
/*TODO*///				cpu_writemem20(offset, val & 0xff);
/*TODO*///				cpu_writemem20(offset + 1, (val >> 8) & 0xff);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void i86_set_nmi_line(int state)
/*TODO*///{
/*TODO*///	if (I.nmi_state == state)
/*TODO*///		return;
/*TODO*///	I.nmi_state = state;
/*TODO*///
/*TODO*///	/* on a rising edge, signal the NMI */
/*TODO*///	if (state != CLEAR_LINE)
/*TODO*///		PREFIX(_interrupt)(I86_NMI_INT);
/*TODO*///}
/*TODO*///
/*TODO*///void i86_set_irq_line(int irqline, int state)
/*TODO*///{
/*TODO*///	I.irq_state = state;
/*TODO*///
/*TODO*///	/* if the IF is set, signal an interrupt */
/*TODO*///	if (state != CLEAR_LINE && I.IF)
/*TODO*///		PREFIX(_interrupt)(-1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int i86_execute(int num_cycles)
/*TODO*///{
/*TODO*///	/* copy over the cycle counts if they're not correct */
/*TODO*///	if (cycles.id != 8086)
/*TODO*///		cycles = i86_cycles;
/*TODO*///
/*TODO*///	/* adjust for any interrupts that came in */
/*TODO*///	i86_ICount = num_cycles;
/*TODO*///	i86_ICount -= I.extra_cycles;
/*TODO*///	I.extra_cycles = 0;
/*TODO*///
/*TODO*///	/* run until we're out */
/*TODO*///	while (i86_ICount > 0)
/*TODO*///	{
/*TODO*/////#define VERBOSE_DEBUG
/*TODO*///#ifdef VERBOSE_DEBUG
/*TODO*///		logerror("[%04x:%04x]=%02x\tF:%04x\tAX=%04x\tBX=%04x\tCX=%04x\tDX=%04x %d%d%d%d%d%d%d%d%d\n",
/*TODO*///				I.sregs[CS], I.pc - I.base[CS], ReadByte(I.pc), I.flags, I.regs.w[AX], I.regs.w[BX], I.regs.w[CX], I.regs.w[DX], I.AuxVal ? 1 : 0, I.OverVal ? 1 : 0,
/*TODO*///				I.SignVal ? 1 : 0, I.ZeroVal ? 1 : 0, I.CarryVal ? 1 : 0, I.ParityVal ? 1 : 0, I.TF, I.IF, I.DirVal < 0 ? 1 : 0);
/*TODO*///#endif
/*TODO*///		CALL_MAME_DEBUG;
/*TODO*///
/*TODO*///		seg_prefix = FALSE;
/*TODO*///		I.prevpc = I.pc;
/*TODO*///		TABLE86;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* adjust for any interrupts that came in */
/*TODO*///	i86_ICount -= I.extra_cycles;
/*TODO*///	I.extra_cycles = 0;
/*TODO*///
/*TODO*///	return num_cycles - i86_ICount;
/*TODO*///}
/*TODO*///
    @Override
    public void reset(Object param) {
        /*unsigned*/ int i, j, c;

        int[] reg_name = {AL, CL, DL, BL, AH, CH, DH, BH};

        I.sregs[CS] = 0xf000;
        I.base[CS] = SegBase(CS);
        I.pc = 0xffff0 & AMASK;

        change_pc20(I.pc);
        for (i = 0; i < 256; i++) {
            for (j = i, c = 0; j > 0; j >>= 1) {
                if ((j & 1) != 0) {
                    c++;
                }
            }

            parity_table[i] = NOT(c & 1);
        }

        I.ZeroVal = I.ParityVal = 1;

        for (i = 0; i < 256; i++) {
            Mod_RM.reg.b[i] = reg_name[(i & 0x38) >> 3];
            Mod_RM.reg.w[i] = ((i & 0x38) >> 3);
        }

        for (i = 0xc0; i < 0x100; i++) {
            Mod_RM.RM.w[i] = (i & 7);
            Mod_RM.RM.b[i] = reg_name[i & 7];
        }
    }

    @Override
    public void exit() {
        /* nothing to do ? */
    }

    @Override
    public int execute(int cycles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return I.pc;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        I.irq_callback = callback;
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
        /*TODO*///	static char buffer[32][63 + 1];
/*TODO*///	static int which = 0;
/*TODO*///	i86_Regs *r = context;
/*TODO*///
/*TODO*///	which = ++which % 32;
/*TODO*///	buffer[which][0] = '\0';
/*TODO*///	if (!context)
/*TODO*///		r = &I;

        switch (regnum) {
            /*TODO*///	case CPU_INFO_REG + I86_IP: 		sprintf(buffer[which], "IP: %04X", r->pc - r->base[CS]); break;
/*TODO*///	case CPU_INFO_REG + I86_SP: 		sprintf(buffer[which], "SP: %04X", r->regs.w[SP]);  break;
/*TODO*///	case CPU_INFO_REG + I86_FLAGS:		sprintf(buffer[which], "F:%04X", r->flags);         break;
/*TODO*///	case CPU_INFO_REG + I86_AX: 		sprintf(buffer[which], "AX:%04X", r->regs.w[AX]);   break;
/*TODO*///	case CPU_INFO_REG + I86_CX: 		sprintf(buffer[which], "CX:%04X", r->regs.w[CX]);   break;
/*TODO*///	case CPU_INFO_REG + I86_DX: 		sprintf(buffer[which], "DX:%04X", r->regs.w[DX]);   break;
/*TODO*///	case CPU_INFO_REG + I86_BX: 		sprintf(buffer[which], "BX:%04X", r->regs.w[BX]);   break;
/*TODO*///	case CPU_INFO_REG + I86_BP: 		sprintf(buffer[which], "BP:%04X", r->regs.w[BP]);   break;
/*TODO*///	case CPU_INFO_REG + I86_SI: 		sprintf(buffer[which], "SI: %04X", r->regs.w[SI]);  break;
/*TODO*///	case CPU_INFO_REG + I86_DI: 		sprintf(buffer[which], "DI: %04X", r->regs.w[DI]);  break;
/*TODO*///	case CPU_INFO_REG + I86_ES: 		sprintf(buffer[which], "ES:%04X", r->sregs[ES]);    break;
/*TODO*///	case CPU_INFO_REG + I86_CS: 		sprintf(buffer[which], "CS:%04X", r->sregs[CS]);    break;
/*TODO*///	case CPU_INFO_REG + I86_SS: 		sprintf(buffer[which], "SS:%04X", r->sregs[SS]);    break;
/*TODO*///	case CPU_INFO_REG + I86_DS: 		sprintf(buffer[which], "DS:%04X", r->sregs[DS]);    break;
/*TODO*///	case CPU_INFO_REG + I86_VECTOR: 	sprintf(buffer[which], "V:%02X", r->int_vector);    break;
/*TODO*///	case CPU_INFO_REG + I86_PENDING:	sprintf(buffer[which], "P:%X", r->irq_state);       break;
/*TODO*///	case CPU_INFO_REG + I86_NMI_STATE:	sprintf(buffer[which], "NMI:%X", r->nmi_state);     break;
/*TODO*///	case CPU_INFO_REG + I86_IRQ_STATE:	sprintf(buffer[which], "IRQ:%X", r->irq_state);     break;
/*TODO*///	case CPU_INFO_FLAGS:
/*TODO*///		r->flags = CompressFlags();
/*TODO*///		sprintf(buffer[which], "%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c",
/*TODO*///				r->flags & 0x8000 ? '?' : '.',
/*TODO*///				r->flags & 0x4000 ? '?' : '.',
/*TODO*///				r->flags & 0x2000 ? '?' : '.',
/*TODO*///				r->flags & 0x1000 ? '?' : '.',
/*TODO*///				r->flags & 0x0800 ? 'O' : '.',
/*TODO*///				r->flags & 0x0400 ? 'D' : '.',
/*TODO*///				r->flags & 0x0200 ? 'I' : '.',
/*TODO*///				r->flags & 0x0100 ? 'T' : '.',
/*TODO*///				r->flags & 0x0080 ? 'S' : '.',
/*TODO*///				r->flags & 0x0040 ? 'Z' : '.',
/*TODO*///				r->flags & 0x0020 ? '?' : '.',
/*TODO*///				r->flags & 0x0010 ? 'A' : '.',
/*TODO*///				r->flags & 0x0008 ? '?' : '.',
/*TODO*///				r->flags & 0x0004 ? 'P' : '.',
/*TODO*///				r->flags & 0x0002 ? 'N' : '.',
/*TODO*///				r->flags & 0x0001 ? 'C' : '.');
/*TODO*///		break;
            case CPU_INFO_NAME:
                return "I86";
            case CPU_INFO_FAMILY:
                return "Intel 80x86";
            case CPU_INFO_VERSION:
                return "1.4";
            case CPU_INFO_FILE:
                return "i86.java";
            case CPU_INFO_CREDITS:
                return "Real mode i286 emulator v1.4 by Fabrice Frances\n(initial work I.based on David Hedley's pcemu)";
            /*TODO*///	case CPU_INFO_REG_LAYOUT:	return (const char *) i86_reg_layout;
/*TODO*///	case CPU_INFO_WIN_LAYOUT:	return (const char *) i86_win_layout;
        }
        throw new UnsupportedOperationException("Not supported yet.");
        /*TODO*///	return buffer[which];
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

    /**
     * arcadeflex functions
     */
    @Override
    public Object init_context() {
        Object reg = new i86_Regs();
        return reg;
    }

    @Override
    public void set_op_base(int pc) {
        cpu_setOPbase20.handler(pc);
    }
}
