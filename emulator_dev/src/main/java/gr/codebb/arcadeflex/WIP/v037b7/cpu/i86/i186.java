/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.FETCHOP;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186intfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86time.i186_cycles;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.instr86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class i186 extends i86 {

    public i186() {
        cpu_num = CPU_I186;
        num_irqs = 1;
        default_vector = 0;
        overclock = 1.0;
        no_int = I186_INT_NONE;
        irq_int = -1000;
        nmi_int = I186_NMI_INT;
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

    @Override
    public String cpu_info(Object context, int regnum) {
        switch (regnum) {
            case CPU_INFO_NAME:
                return "I186";
        }
        return super.cpu_info(context, regnum);
    }

    @Override
    public void reset(Object param) {
        super.reset(param);
        cycles = i186_cycles;
    }

    /*TODO*///#include "i186intf.h"
/*TODO*///
/*TODO*///#undef PREFIX
/*TODO*///#define PREFIX(name) i186##name
/*TODO*///#define PREFIX186(name) i186##name
/*TODO*///
/*TODO*///#define I186
/*TODO*///#include "instr186.h"
/*TODO*///#include "table186.h"
/*TODO*///
/*TODO*///#include "instr86.c"
/*TODO*///#include "instr186.c"
/*TODO*///#undef I186
    @Override
    public int execute(int num_cycles) {
        /* copy over the cycle counts if they're not correct */
        if (cycles.id != 80186) {
            cycles = i186_cycles;
        }

        /* adjust for any interrupts that came in */
        i86_ICount[0] = num_cycles;
        i86_ICount[0] -= I.extra_cycles;
        I.extra_cycles = 0;

        /* run until we're out */
        while (i86_ICount[0] > 0) {
            //printf("[%04x:%04x]=%02x\tAX=%04x\tBX=%04x\tCX=%04x\tDX=%04x\n", I.sregs[CS], I.pc, ReadByte(I.pc), I.regs.w[AX],I.regs.w[BX], I.regs.w[CX], I.regs.w[DX]);
            seg_prefix = 0;
            I.prevpc = I.pc;
            fetchInstruction();
        }

        /* adjust for any interrupts that came in */
        i86_ICount[0] -= I.extra_cycles;
        I.extra_cycles = 0;

        return num_cycles - i86_ICount[0];
    }

    public static void fetchInstruction() {
        int fetchop = FETCHOP();
        switch (fetchop) {
            /*TODO*///	case 0x00:    PREFIX86(_add_br8)(); break;
/*TODO*///	case 0x01:    PREFIX86(_add_wr16)(); break;
/*TODO*///	case 0x02:    PREFIX86(_add_r8b)(); break;
/*TODO*///	case 0x03:    PREFIX86(_add_r16w)(); break;
/*TODO*///	case 0x04:    PREFIX86(_add_ald8)(); break;
/*TODO*///	case 0x05:    PREFIX86(_add_axd16)(); break;
/*TODO*///	case 0x06:    PREFIX86(_push_es)(); break;
/*TODO*///	case 0x07:    PREFIX86(_pop_es)(); break;
/*TODO*///	case 0x08:    PREFIX86(_or_br8)(); break;
/*TODO*///	case 0x09:    PREFIX86(_or_wr16)(); break;
/*TODO*///	case 0x0a:    PREFIX86(_or_r8b)(); break;
/*TODO*///	case 0x0b:    PREFIX86(_or_r16w)(); break;
/*TODO*///	case 0x0c:    PREFIX86(_or_ald8)(); break;
/*TODO*///	case 0x0d:    PREFIX86(_or_axd16)(); break;
/*TODO*///	case 0x0e:    PREFIX86(_push_cs)(); break;
/*TODO*///	case 0x0f:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0x10:    PREFIX86(_adc_br8)(); break;
/*TODO*///	case 0x11:    PREFIX86(_adc_wr16)(); break;
/*TODO*///	case 0x12:    PREFIX86(_adc_r8b)(); break;
/*TODO*///	case 0x13:    PREFIX86(_adc_r16w)(); break;
/*TODO*///	case 0x14:    PREFIX86(_adc_ald8)(); break;
/*TODO*///	case 0x15:    PREFIX86(_adc_axd16)(); break;
/*TODO*///	case 0x16:    PREFIX86(_push_ss)(); break;
/*TODO*///	case 0x17:    PREFIX86(_pop_ss)(); break;
/*TODO*///	case 0x18:    PREFIX86(_sbb_br8)(); break;
/*TODO*///	case 0x19:    PREFIX86(_sbb_wr16)(); break;
/*TODO*///	case 0x1a:    PREFIX86(_sbb_r8b)(); break;
/*TODO*///	case 0x1b:    PREFIX86(_sbb_r16w)(); break;
/*TODO*///	case 0x1c:    PREFIX86(_sbb_ald8)(); break;
/*TODO*///	case 0x1d:    PREFIX86(_sbb_axd16)(); break;
/*TODO*///	case 0x1e:    PREFIX86(_push_ds)(); break;
/*TODO*///	case 0x1f:    PREFIX86(_pop_ds)(); break;
/*TODO*///	case 0x20:    PREFIX86(_and_br8)(); break;
/*TODO*///	case 0x21:    PREFIX86(_and_wr16)(); break;
/*TODO*///	case 0x22:    PREFIX86(_and_r8b)(); break;
/*TODO*///	case 0x23:    PREFIX86(_and_r16w)(); break;
/*TODO*///	case 0x24:    PREFIX86(_and_ald8)(); break;
/*TODO*///	case 0x25:    PREFIX86(_and_axd16)(); break;
/*TODO*///	case 0x26:    PREFIX86(_es)(); break;
/*TODO*///	case 0x27:    PREFIX86(_daa)(); break;
/*TODO*///	case 0x28:    PREFIX86(_sub_br8)(); break;
/*TODO*///	case 0x29:    PREFIX86(_sub_wr16)(); break;
/*TODO*///	case 0x2a:    PREFIX86(_sub_r8b)(); break;
/*TODO*///	case 0x2b:    PREFIX86(_sub_r16w)(); break;
/*TODO*///	case 0x2c:    PREFIX86(_sub_ald8)(); break;
/*TODO*///	case 0x2d:    PREFIX86(_sub_axd16)(); break;
            case 0x2e:
                i86_cs.handler();
                break;
            /*TODO*///	case 0x2f:    PREFIX86(_das)(); break;
/*TODO*///	case 0x30:    PREFIX86(_xor_br8)(); break;
/*TODO*///	case 0x31:    PREFIX86(_xor_wr16)(); break;
/*TODO*///	case 0x32:    PREFIX86(_xor_r8b)(); break;
            case 0x33:
                i86_xor_r16w.handler();
                break;
            /*TODO*///	case 0x34:    PREFIX86(_xor_ald8)(); break;
/*TODO*///	case 0x35:    PREFIX86(_xor_axd16)(); break;
/*TODO*///	case 0x36:    PREFIX86(_ss)(); break;
/*TODO*///	case 0x37:    PREFIX86(_aaa)(); break;
/*TODO*///	case 0x38:    PREFIX86(_cmp_br8)(); break;
/*TODO*///	case 0x39:    PREFIX86(_cmp_wr16)(); break;
/*TODO*///	case 0x3a:    PREFIX86(_cmp_r8b)(); break;
/*TODO*///	case 0x3b:    PREFIX86(_cmp_r16w)(); break;
/*TODO*///	case 0x3c:    PREFIX86(_cmp_ald8)(); break;
/*TODO*///	case 0x3d:    PREFIX86(_cmp_axd16)(); break;
/*TODO*///	case 0x3e:    PREFIX86(_ds)(); break;
/*TODO*///	case 0x3f:    PREFIX86(_aas)(); break;
/*TODO*///	case 0x40:    PREFIX86(_inc_ax)(); break;
/*TODO*///	case 0x41:    PREFIX86(_inc_cx)(); break;
/*TODO*///	case 0x42:    PREFIX86(_inc_dx)(); break;
/*TODO*///	case 0x43:    PREFIX86(_inc_bx)(); break;
/*TODO*///	case 0x44:    PREFIX86(_inc_sp)(); break;
/*TODO*///	case 0x45:    PREFIX86(_inc_bp)(); break;
/*TODO*///	case 0x46:    PREFIX86(_inc_si)(); break;
/*TODO*///	case 0x47:    PREFIX86(_inc_di)(); break;
/*TODO*///	case 0x48:    PREFIX86(_dec_ax)(); break;
/*TODO*///	case 0x49:    PREFIX86(_dec_cx)(); break;
/*TODO*///	case 0x4a:    PREFIX86(_dec_dx)(); break;
/*TODO*///	case 0x4b:    PREFIX86(_dec_bx)(); break;
/*TODO*///	case 0x4c:    PREFIX86(_dec_sp)(); break;
/*TODO*///	case 0x4d:    PREFIX86(_dec_bp)(); break;
/*TODO*///	case 0x4e:    PREFIX86(_dec_si)(); break;
/*TODO*///	case 0x4f:    PREFIX86(_dec_di)(); break;
/*TODO*///	case 0x50:    PREFIX86(_push_ax)(); break;
/*TODO*///	case 0x51:    PREFIX86(_push_cx)(); break;
/*TODO*///	case 0x52:    PREFIX86(_push_dx)(); break;
/*TODO*///	case 0x53:    PREFIX86(_push_bx)(); break;
/*TODO*///	case 0x54:    PREFIX86(_push_sp)(); break;
/*TODO*///	case 0x55:    PREFIX86(_push_bp)(); break;
/*TODO*///	case 0x56:    PREFIX86(_push_si)(); break;
/*TODO*///	case 0x57:    PREFIX86(_push_di)(); break;
/*TODO*///	case 0x58:    PREFIX86(_pop_ax)(); break;
/*TODO*///	case 0x59:    PREFIX86(_pop_cx)(); break;
/*TODO*///	case 0x5a:    PREFIX86(_pop_dx)(); break;
/*TODO*///	case 0x5b:    PREFIX86(_pop_bx)(); break;
/*TODO*///	case 0x5c:    PREFIX86(_pop_sp)(); break;
/*TODO*///	case 0x5d:    PREFIX86(_pop_bp)(); break;
/*TODO*///	case 0x5e:    PREFIX86(_pop_si)(); break;
/*TODO*///	case 0x5f:    PREFIX86(_pop_di)(); break;
/*TODO*///		  case 0x60:    PREFIX186(_pusha)(); break;
/*TODO*///		  case 0x61:    PREFIX186(_popa)(); break;
/*TODO*///		  case 0x62:    PREFIX186(_bound)(); break;
/*TODO*///	case 0x63:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0x64:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0x65:	  PREFIX86(_invalid)(); break;
/*TODO*///	case 0x66:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0x67:    PREFIX86(_invalid)(); break;
/*TODO*///		  case 0x68:    PREFIX186(_push_d16)(); break;
/*TODO*///		  case 0x69:    PREFIX186(_imul_d16)(); break;
/*TODO*///		  case 0x6a:    PREFIX186(_push_d8)(); break;
/*TODO*///		  case 0x6b:    PREFIX186(_imul_d8)(); break;
/*TODO*///		  case 0x6c:    PREFIX186(_insb)(); break;
/*TODO*///		  case 0x6d:    PREFIX186(_insw)(); break;
/*TODO*///		  case 0x6e:    PREFIX186(_outsb)(); break;
/*TODO*///		  case 0x6f:    PREFIX186(_outsw)(); break;
/*TODO*///	case 0x70:    PREFIX86(_jo)(); break;
/*TODO*///	case 0x71:    PREFIX86(_jno)(); break;
/*TODO*///	case 0x72:    PREFIX86(_jb)(); break;
/*TODO*///	case 0x73:    PREFIX86(_jnb)(); break;
            case 0x74:
                i86_jz.handler();
                break;
            case 0x75:
                i86_jnz.handler();
                break;
            /*TODO*///	case 0x76:    PREFIX86(_jbe)(); break;
/*TODO*///	case 0x77:    PREFIX86(_jnbe)(); break;
/*TODO*///	case 0x78:    PREFIX86(_js)(); break;
/*TODO*///	case 0x79:    PREFIX86(_jns)(); break;
/*TODO*///	case 0x7a:    PREFIX86(_jp)(); break;
/*TODO*///	case 0x7b:    PREFIX86(_jnp)(); break;
/*TODO*///	case 0x7c:    PREFIX86(_jl)(); break;
/*TODO*///	case 0x7d:    PREFIX86(_jnl)(); break;
/*TODO*///	case 0x7e:    PREFIX86(_jle)(); break;
/*TODO*///	case 0x7f:    PREFIX86(_jnle)(); break;
/*TODO*///	case 0x80:    PREFIX86(_80pre)(); break;
            case 0x81:
                i86_81pre.handler();
                break;
            /*TODO*///	case 0x82:	  PREFIX86(_82pre)(); break;
            case 0x83:
                i86_83pre.handler();
                break;
            /*TODO*///	case 0x84:    PREFIX86(_test_br8)(); break;
/*TODO*///	case 0x85:    PREFIX86(_test_wr16)(); break;
/*TODO*///	case 0x86:    PREFIX86(_xchg_br8)(); break;
/*TODO*///	case 0x87:    PREFIX86(_xchg_wr16)(); break;
/*TODO*///	case 0x88:    PREFIX86(_mov_br8)(); break;
            case 0x89:
                i86_mov_wr16.handler();
                break;
            /*TODO*///	case 0x8a:    PREFIX86(_mov_r8b)(); break;
            case 0x8b:
                i86_mov_r16w.handler();
                break;
            /*TODO*///	case 0x8c:    PREFIX86(_mov_wsreg)(); break;
/*TODO*///	case 0x8d:    PREFIX86(_lea)(); break;
            case 0x8e:
                i86_mov_sregw.handler();
                break;
            /*TODO*///	case 0x8f:    PREFIX86(_popw)(); break;
            case 0x90:
                i86_nop.handler();
                break;
            /*TODO*///	case 0x91:    PREFIX86(_xchg_axcx)(); break;
/*TODO*///	case 0x92:    PREFIX86(_xchg_axdx)(); break;
/*TODO*///	case 0x93:    PREFIX86(_xchg_axbx)(); break;
/*TODO*///	case 0x94:    PREFIX86(_xchg_axsp)(); break;
/*TODO*///	case 0x95:    PREFIX86(_xchg_axbp)(); break;
/*TODO*///	case 0x96:    PREFIX86(_xchg_axsi)(); break;
/*TODO*///	case 0x97:    PREFIX86(_xchg_axdi)(); break;
/*TODO*///	case 0x98:    PREFIX86(_cbw)(); break;
/*TODO*///	case 0x99:    PREFIX86(_cwd)(); break;
/*TODO*///	case 0x9a:    PREFIX86(_call_far)(); break;
/*TODO*///	case 0x9b:    PREFIX86(_wait)(); break;
/*TODO*///	case 0x9c:    PREFIX86(_pushf)(); break;
/*TODO*///	case 0x9d:    PREFIX86(_popf)(); break;
/*TODO*///	case 0x9e:    PREFIX86(_sahf)(); break;
/*TODO*///	case 0x9f:    PREFIX86(_lahf)(); break;
/*TODO*///	case 0xa0:    PREFIX86(_mov_aldisp)(); break;
/*TODO*///	case 0xa1:    PREFIX86(_mov_axdisp)(); break;
/*TODO*///	case 0xa2:    PREFIX86(_mov_dispal)(); break;
/*TODO*///	case 0xa3:    PREFIX86(_mov_dispax)(); break;
/*TODO*///	case 0xa4:    PREFIX86(_movsb)(); break;
/*TODO*///	case 0xa5:    PREFIX86(_movsw)(); break;
/*TODO*///	case 0xa6:    PREFIX86(_cmpsb)(); break;
/*TODO*///	case 0xa7:    PREFIX86(_cmpsw)(); break;
/*TODO*///	case 0xa8:    PREFIX86(_test_ald8)(); break;
/*TODO*///	case 0xa9:    PREFIX86(_test_axd16)(); break;
/*TODO*///	case 0xaa:    PREFIX86(_stosb)(); break;
/*TODO*///	case 0xab:    PREFIX86(_stosw)(); break;
/*TODO*///	case 0xac:    PREFIX86(_lodsb)(); break;
/*TODO*///	case 0xad:    PREFIX86(_lodsw)(); break;
/*TODO*///	case 0xae:    PREFIX86(_scasb)(); break;
/*TODO*///	case 0xaf:    PREFIX86(_scasw)(); break;
/*TODO*///	case 0xb0:    PREFIX86(_mov_ald8)(); break;
/*TODO*///	case 0xb1:    PREFIX86(_mov_cld8)(); break;
/*TODO*///	case 0xb2:    PREFIX86(_mov_dld8)(); break;
/*TODO*///	case 0xb3:    PREFIX86(_mov_bld8)(); break;
/*TODO*///	case 0xb4:    PREFIX86(_mov_ahd8)(); break;
/*TODO*///	case 0xb5:    PREFIX86(_mov_chd8)(); break;
/*TODO*///	case 0xb6:    PREFIX86(_mov_dhd8)(); break;
/*TODO*///	case 0xb7:    PREFIX86(_mov_bhd8)(); break;
            case 0xb8:
                i86_mov_axd16.handler();
                break;
            case 0xb9:
                i86_mov_cxd16.handler();
                break;
            /*TODO*///	case 0xba:    PREFIX86(_mov_dxd16)(); break;
/*TODO*///	case 0xbb:    PREFIX86(_mov_bxd16)(); break;
/*TODO*///	case 0xbc:    PREFIX86(_mov_spd16)(); break;
/*TODO*///	case 0xbd:    PREFIX86(_mov_bpd16)(); break;
            case 0xbe:
                i86_mov_sid16.handler();
                break;
            case 0xbf:
                i86_mov_did16.handler();
                break;
            /*TODO*///		  case 0xc0:    PREFIX186(_rotshft_bd8)(); break;
/*TODO*///		  case 0xc1:    PREFIX186(_rotshft_wd8)(); break;
/*TODO*///	case 0xc2:    PREFIX86(_ret_d16)(); break;
/*TODO*///	case 0xc3:    PREFIX86(_ret)(); break;
/*TODO*///	case 0xc4:    PREFIX86(_les_dw)(); break;
/*TODO*///	case 0xc5:    PREFIX86(_lds_dw)(); break;
/*TODO*///	case 0xc6:    PREFIX86(_mov_bd8)(); break;
/*TODO*///	case 0xc7:    PREFIX86(_mov_wd16)(); break;
/*TODO*///		  case 0xc8:    PREFIX186(_enter)(); break;
/*TODO*///		  case 0xc9:    PREFIX186(_leave)(); break;
/*TODO*///	case 0xca:    PREFIX86(_retf_d16)(); break;
/*TODO*///	case 0xcb:    PREFIX86(_retf)(); break;
/*TODO*///	case 0xcc:    PREFIX86(_int3)(); break;
/*TODO*///	case 0xcd:    PREFIX86(_int)(); break;
/*TODO*///	case 0xce:    PREFIX86(_into)(); break;
/*TODO*///	case 0xcf:    PREFIX86(_iret)(); break;
/*TODO*///		  case 0xd0:    PREFIX86(_rotshft_b)(); break;
/*TODO*///		  case 0xd1:    PREFIX86(_rotshft_w)(); break;
/*TODO*///		  case 0xd2:    PREFIX86(_rotshft_bcl)(); break;
/*TODO*///		  case 0xd3:    PREFIX86(_rotshft_wcl)(); break;
/*TODO*///	case 0xd4:    PREFIX86(_aam)(); break;
/*TODO*///	case 0xd5:    PREFIX86(_aad)(); break;
/*TODO*///	case 0xd6:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0xd7:    PREFIX86(_xlat)(); break;
/*TODO*///	case 0xd8:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xd9:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xda:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xdb:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xdc:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xdd:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xde:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xdf:    PREFIX86(_escape)(); break;
/*TODO*///	case 0xe0:    PREFIX86(_loopne)(); break;
/*TODO*///	case 0xe1:    PREFIX86(_loope)(); break;
            case 0xe2:
                i86_loop.handler();
                break;
            /*TODO*///	case 0xe3:    PREFIX86(_jcxz)(); break;
/*TODO*///	case 0xe4:    PREFIX86(_inal)(); break;
/*TODO*///	case 0xe5:    PREFIX86(_inax)(); break;
/*TODO*///	case 0xe6:    PREFIX86(_outal)(); break;
/*TODO*///	case 0xe7:    PREFIX86(_outax)(); break;
/*TODO*///	case 0xe8:    PREFIX86(_call_d16)(); break;
/*TODO*///	case 0xe9:    PREFIX86(_jmp_d16)(); break;
            case 0xea:
                i86_jmp_far.handler();
                break;
            case 0xeb:
                i86_jmp_d8.handler();
                break;
            /*TODO*///	case 0xec:    PREFIX86(_inaldx)(); break;
/*TODO*///	case 0xed:    PREFIX86(_inaxdx)(); break;
/*TODO*///	case 0xee:    PREFIX86(_outdxal)(); break;
            case 0xef:
                i86_outdxax.handler();
                break;
            /*TODO*///	case 0xf0:    PREFIX86(_lock)(); break;
/*TODO*///	case 0xf1:    PREFIX86(_invalid)(); break;
/*TODO*///	case 0xf2:    PREFIX186(_repne)(); break;
/*TODO*///	case 0xf3:    PREFIX186(_repe)(); break;
/*TODO*///	case 0xf4:    PREFIX86(_hlt)(); break;
/*TODO*///	case 0xf5:    PREFIX86(_cmc)(); break;
            case 0xf6:
                i86_f6pre.handler();
                break;
            /*TODO*///	case 0xf7:    PREFIX86(_f7pre)(); break;
/*TODO*///	case 0xf8:    PREFIX86(_clc)(); break;
/*TODO*///	case 0xf9:    PREFIX86(_stc)(); break;
            case 0xfa:
                i86_cli.handler();
                break;
            /*TODO*///	case 0xfb:    PREFIX86(_sti)(); break;
            case 0xfc:
                i86_cld.handler();
                break;
            case 0xfd:
                i86_std.handler();
                break;
            /*TODO*///	case 0xfe:    PREFIX86(_fepre)(); break;
/*TODO*///	case 0xff:    PREFIX86(_ffpre)(); break;
            default:
                System.out.println("Unsupported 186 instruction 0x " + Integer.toHexString(fetchop));
                throw new UnsupportedOperationException("Unsupported");
        }
    }
}
