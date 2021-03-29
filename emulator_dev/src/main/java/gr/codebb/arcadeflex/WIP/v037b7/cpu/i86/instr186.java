/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.instr86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.table186H.i186_instruction;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.modrmH.*;

public class instr186 {

    /*TODO*////****************************************************************************
/*TODO*///*			  real mode i286 emulator v1.4 by Fabrice Frances				*
/*TODO*///*				(initial work based on David Hedley's pcemu)                *
/*TODO*///****************************************************************************/
/*TODO*///
/*TODO*///// file will be included in all cpu variants
/*TODO*///// function renaming will be added when neccessary
/*TODO*///// timing value should move to separate array
/*TODO*///
/*TODO*///#undef ICOUNT
/*TODO*///
/*TODO*///#ifdef V20
/*TODO*///#define ICOUNT nec_ICount
/*TODO*///#else
/*TODO*///#define ICOUNT i86_ICount
/*TODO*///#endif
/*TODO*///
/*TODO*///
    static InstructionPtr i186_pusha = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_pusha)(void)    /* Opcode 0x60 */
/*TODO*///{
/*TODO*///	unsigned tmp=I.regs.w[SP];
/*TODO*///	
/*TODO*///	ICOUNT -= cycles.pusha;
/*TODO*///	PUSH(I.regs.w[AX]);
/*TODO*///	PUSH(I.regs.w[CX]);
/*TODO*///	PUSH(I.regs.w[DX]);
/*TODO*///	PUSH(I.regs.w[BX]);
/*TODO*///    PUSH(tmp);
/*TODO*///	PUSH(I.regs.w[BP]);
/*TODO*///	PUSH(I.regs.w[SI]);
/*TODO*///	PUSH(I.regs.w[DI]);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_popa = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_popa)(void)    /* Opcode 0x61 */
/*TODO*///{
/*TODO*///	 unsigned tmp;
/*TODO*///
/*TODO*///	ICOUNT -= cycles.popa;
/*TODO*///	POP(I.regs.w[DI]);
/*TODO*///	POP(I.regs.w[SI]);
/*TODO*///	POP(I.regs.w[BP]);
/*TODO*///	POP(tmp);
/*TODO*///	POP(I.regs.w[BX]);
/*TODO*///	POP(I.regs.w[DX]);
/*TODO*///	POP(I.regs.w[CX]);
/*TODO*///	POP(I.regs.w[AX]);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_bound = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_bound)(void)    /* Opcode 0x62 */
/*TODO*///{
/*TODO*///	unsigned ModRM = FETCHOP;
/*TODO*///	int low = (INT16)GetRMWord(ModRM);
/*TODO*///    int high= (INT16)GetnextRMWord;
/*TODO*///	int tmp= (INT16)RegWord(ModRM);
/*TODO*///	if (tmp<low || tmp>high) {
/*TODO*///		/* OB: on NECs CS:IP points to instruction
/*TODO*///		   FOLLOWING the BOUND instruction ! */
/*TODO*///#if !defined(V20)
/*TODO*///		I.pc-=2;
/*TODO*///		PREFIX86(_interrupt)(5);
/*TODO*///#else
/*TODO*///		PREFIX(_interrupt)(5,0);
/*TODO*///#endif
/*TODO*///	}
/*TODO*///	ICOUNT -= cycles.bound;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_push_d16 = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_push_d16)(void)    /* Opcode 0x68 */
/*TODO*///{
/*TODO*///	unsigned tmp = FETCH;
/*TODO*///
/*TODO*///	ICOUNT -= cycles.push_imm;
/*TODO*///	tmp += FETCH << 8;
/*TODO*///	PUSH(tmp);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_imul_d16 = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_imul_d16)(void)    /* Opcode 0x69 */
/*TODO*///{
/*TODO*///	DEF_r16w(dst,src);
/*TODO*///	unsigned src2=FETCH;
/*TODO*///	src+=(FETCH<<8);
/*TODO*///
/*TODO*///	ICOUNT -= (ModRM >= 0xc0) ? cycles.imul_rri16 : cycles.imul_rmi16;
/*TODO*///
/*TODO*///	dst = (INT32)((INT16)src)*(INT32)((INT16)src2);
/*TODO*///	I.CarryVal = I.OverVal = (((INT32)dst) >> 15 != 0) && (((INT32)dst) >> 15 != -1);
/*TODO*///	RegWord(ModRM)=(WORD)dst;
/*TODO*///}
/*TODO*///
/*TODO*///
    static InstructionPtr i186_push_d8 = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_push_d8)(void)    /* Opcode 0x6a */
/*TODO*///{
/*TODO*///	unsigned tmp = (WORD)((INT16)((INT8)FETCH));
/*TODO*///
/*TODO*///	ICOUNT -= cycles.push_imm;
/*TODO*///	PUSH(tmp);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_imul_d8 = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_imul_d8)(void)    /* Opcode 0x6b */
/*TODO*///{
/*TODO*///	DEF_r16w(dst,src);
/*TODO*///	unsigned src2= (WORD)((INT16)((INT8)FETCH));
/*TODO*///
/*TODO*///	ICOUNT -= (ModRM >= 0xc0) ? cycles.imul_rri8 : cycles.imul_rmi8;
/*TODO*///
/*TODO*///	dst = (INT32)((INT16)src)*(INT32)((INT16)src2);
/*TODO*///	I.CarryVal = I.OverVal = (((INT32)dst) >> 15 != 0) && (((INT32)dst) >> 15 != -1);
/*TODO*///	RegWord(ModRM)=(WORD)dst;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_insb = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_insb)(void)    /* Opcode 0x6c */
/*TODO*///{
/*TODO*///	ICOUNT -= cycles.ins8;
/*TODO*///	PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_insw = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_insw)(void)    /* Opcode 0x6d */
/*TODO*///{
/*TODO*///	ICOUNT -= cycles.ins16;
/*TODO*///	PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///	PutMemB(ES,I.regs.w[DI]+1,read_port(I.regs.w[DX]+1));
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_outsb = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_outsb)(void)    /* Opcode 0x6e */
/*TODO*///{
/*TODO*///	ICOUNT -= cycles.outs8;
/*TODO*///	write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///	I.regs.w[DI] += I.DirVal;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_outsw = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_outsw)(void)    /* Opcode 0x6f */
/*TODO*///{
/*TODO*///	ICOUNT -= cycles.outs16;
/*TODO*///	write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///	write_port(I.regs.w[DX]+1,GetMemB(DS,I.regs.w[SI]+1));
/*TODO*///	I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_rotshft_bd8 = new InstructionPtr() {
        public void handler() {
            int/*unsigned*/ ModRM = FETCH();
            int/*unsigned*/ count = FETCH();
            i86_rotate_shift_Byte(ModRM, count);
        }
    };
    static InstructionPtr i186_rotshft_wd8 = new InstructionPtr() {
        public void handler() {
            int/*unsigned*/ ModRM = FETCH();
            int/*unsigned*/ count = FETCH();

            i86_rotate_shift_Word(ModRM, count);
        }
    };

    static InstructionPtr i186_enter = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_enter)(void)    /* Opcode 0xc8 */
/*TODO*///{
/*TODO*///	unsigned nb = FETCH;	 unsigned i,level;
/*TODO*///
/*TODO*///	nb += FETCH << 8;
/*TODO*///	level = FETCH;
/*TODO*///	ICOUNT -= (level == 0) ? cycles.enter0 : (level == 1) ? cycles.enter1 : cycles.enter_base + level * cycles.enter_count;
/*TODO*///	PUSH(I.regs.w[BP]);
/*TODO*///	I.regs.w[BP]=I.regs.w[SP];
/*TODO*///	I.regs.w[SP] -= nb;
/*TODO*///	for (i=1;i<level;i++)
/*TODO*///		PUSH(GetMemW(SS,I.regs.w[BP]-i*2));
/*TODO*///	if (level) PUSH(I.regs.w[BP]);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_leave = new InstructionPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
    /*TODO*///static void PREFIX186(_leave)(void)    /* Opcode 0xc9 */
/*TODO*///{
/*TODO*///	ICOUNT -= cycles.leave;
/*TODO*///	I.regs.w[SP]=I.regs.w[BP];
/*TODO*///	POP(I.regs.w[BP]);
/*TODO*///}
/*TODO*///
    static InstructionPtr i186_repne = new InstructionPtr() {
        public void handler() {
            rep(0);
        }
    };

    static InstructionPtr i186_repe = new InstructionPtr() /* Opcode 0xf3 */ {
        public void handler() {
            rep(1);
        }
    };

    static void rep(int flagval) {
        /* Handles rep- and repnz- prefixes. flagval is the value of ZF for the 
		 loop  to continue for CMPS and SCAS instructions. */

        int/*unsigned*/ next = FETCHOP();
        int/*unsigned*/ count = I.regs.w[CX];

        switch (next) {
            /*TODO*///	case 0x26:  /* ES: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[ES]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
            case 0x2e:
                /* CS: */
                seg_prefix = 1;
                prefix_base = I.base[CS];
                i86_ICount[0] -= cycles.override;
                rep(flagval);
                break;
            /*TODO*///	case 0x36:  /* SS: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[SS]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///	case 0x3e:  /* DS: */ 
/*TODO*///		seg_prefix=TRUE; 
/*TODO*///		prefix_base=I.base[DS]; 
/*TODO*///		i86_ICount[0] -= cycles.override;
/*TODO*///		PREFIX(rep)(flagval); 
/*TODO*///		break; 
/*TODO*///#ifndef I86 
/*TODO*///	case 0x6c:	/* REP INSB */
/*TODO*///		i86_ICount[0] -= cycles.rep_ins8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_ins8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6d:  /* REP INSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_ins16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			PutMemB(ES,I.regs.w[DI],read_port(I.regs.w[DX]));
/*TODO*///			PutMemB(ES,I.regs.w[DI]+1,read_port(I.regs.w[DX]+1));
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_ins16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6e:  /* REP OUTSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_outs8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_outs8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0x6f:  /* REP OUTSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_outs16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			write_port(I.regs.w[DX],GetMemB(DS,I.regs.w[SI]));
/*TODO*///			write_port(I.regs.w[DX]+1,GetMemB(DS,I.regs.w[SI]+1));
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_outs16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///#endif 
            case 0xa4:
                /* REP MOVSB */
                i86_ICount[0] -= cycles.rep_movs8_base;
                for (; count > 0; count--) {
                    int/*BYTE*/ tmp;

                    if (i86_ICount[0] <= 0) {
                        I.pc = I.prevpc;
                        break;
                    }
                    tmp = GetMemB(DS, I.regs.w[SI]) & 0xFF;
                    PutMemB(ES, I.regs.w[DI], tmp);
                    I.regs.SetW(DI, I.regs.w[DI] + I.DirVal);
                    I.regs.SetW(SI, I.regs.w[SI] + I.DirVal);
                    i86_ICount[0] -= cycles.rep_movs8_count;
                }
                I.regs.SetW(CX, count);
                break;
            case 0xa5:
                /* REP MOVSW */
                i86_ICount[0] -= cycles.rep_movs16_base;
                for (; count > 0; count--) {
                    int /*WORD*/ tmp;
                    int di = I.regs.w[DI];
                    int si = I.regs.w[SI];
                    if (i86_ICount[0] <= 0) {
                        I.pc = I.prevpc;
                        break;
                    }
                    tmp = GetMemW(DS, si);
                    PutMemW(ES, I.regs.w[DI], tmp);
                    di += 2 * I.DirVal;
                    si += 2 * I.DirVal;
                    I.regs.SetW(DI, di & 0xFFFF);
                    I.regs.SetW(SI, si & 0xFFFF);
                    i86_ICount[0] -= cycles.rep_movs16_count;
                }
                I.regs.SetW(CX, count);
                break;
            /*TODO*///	case 0xa6:  /* REP(N)E CMPSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_cmps8_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned dst, src;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			dst = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			src = GetMemB(DS, I.regs.w[SI]);
/*TODO*///		    SUBB(src,dst); /* opposite of the usual convention */
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			I.regs.w[SI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_cmps8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xa7:  /* REP(N)E CMPSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_cmps16_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned dst, src;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			dst = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			src = GetMemB(DS, I.regs.w[SI]);
/*TODO*///		    SUBB(src,dst); /* opposite of the usual convention */
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_cmps16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
            case 0xaa:
                /* REP STOSB */
                i86_ICount[0] -= cycles.rep_stos8_base;
                for (; count > 0; count--) {
                    if (i86_ICount[0] <= 0) {
                        I.pc = I.prevpc;
                        break;
                    }
                    PutMemB(ES, I.regs.w[DI], I.regs.b[AL]);
                    I.regs.SetW(DI, I.regs.w[DI] + I.DirVal);
                    i86_ICount[0] -= cycles.rep_stos8_count;
                }
                I.regs.SetW(CX, count);
                break;
            case 0xab:
                /* REP STOSW */
                i86_ICount[0] -= cycles.rep_stos16_base;
                for (; count > 0; count--) {
                    if (i86_ICount[0] <= 0) {
                        I.pc = I.prevpc;
                        break;
                    }
                    int tmp = I.regs.w[DI];
                    //PutMemB(ES,I.regs.w[DI],I.regs.b[AL]);
                    //PutMemB(ES,I.regs.w[DI]+1,I.regs.b[AH]);
                    PutMemW(ES, tmp, I.regs.w[AX]);
                    tmp += 2 * I.DirVal;
                    I.regs.SetW(DI, tmp & 0xFFFF);
                    i86_ICount[0] -= cycles.rep_stos16_count;
                }
                I.regs.SetW(CX, count);
                break;
            /*TODO*///	case 0xac:  /* REP LODSB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_lods8_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			I.regs.b[AL] = GetMemB(DS,I.regs.w[SI]);
/*TODO*///			I.regs.w[SI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_lods8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xad:  /* REP LODSW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_lods16_base;
/*TODO*///		for (; count > 0; count--) 
/*TODO*///		{
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			I.regs.w[AX] = GetMemW(DS,I.regs.w[SI]);
/*TODO*///			I.regs.w[SI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_lods16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xae:  /* REP(N)E SCASB */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_scas8_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned src, dst;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			src = GetMemB(ES, I.regs.w[DI]);
/*TODO*///			dst = I.regs.b[AL];
/*TODO*///		    SUBB(dst,src);
/*TODO*///			I.regs.w[DI] += I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_scas8_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
/*TODO*///	case 0xaf:  /* REP(N)E SCASW */ 
/*TODO*///		i86_ICount[0] -= cycles.rep_scas16_base;
/*TODO*///		for (I.ZeroVal = !flagval; (ZF == flagval) && (count > 0); count--) 
/*TODO*///		{
/*TODO*///			unsigned src, dst;
/*TODO*///			
/*TODO*///			if (i86_ICount[0] <= 0) { I.pc = I.prevpc; break; }
/*TODO*///			src = GetMemW(ES, I.regs.w[DI]);
/*TODO*///			dst = I.regs.w[AX];
/*TODO*///		    SUBW(dst,src);
/*TODO*///			I.regs.w[DI] += 2 * I.DirVal;
/*TODO*///			i86_ICount[0] -= cycles.rep_scas16_count;
/*TODO*///		}
/*TODO*///		I.regs.w[CX]=count; 
/*TODO*///		break; 
            default:
                System.out.println("rep 0x" + Integer.toHexString(next));
                throw new UnsupportedOperationException("Unsupported");
            /*TODO*///		PREFIX(_instruction)[next](); 
        }
    }
    /**
     * need to fetch instruction from i86 table
     */
    static InstructionPtr i186_cs = new InstructionPtr() /* Opcode 0x2e */ {
        public void handler() {
            seg_prefix = 1;
            prefix_base = I.base[CS];
            i86_ICount[0] -= cycles.override;
            i186_instruction[FETCHOP()].handler();
        }
    };
    static InstructionPtr i186_mov_sregw = new InstructionPtr() /* Opcode 0x8e */ {
        public void handler() {
            int /*unsigned*/ ModRM = FETCH();
            int /*WORD*/ src = GetRMWord(ModRM) & 0xFFFF;

            i86_ICount[0] -= (ModRM >= 0xc0) ? cycles.mov_sr : cycles.mov_sm;
            /*TODO*///#ifdef I286
/*TODO*///    switch (ModRM & 0x38)
/*TODO*///    {
/*TODO*///    case 0x00:  /* mov es,ew */
/*TODO*///		i286_data_descriptor(ES,src);
/*TODO*///		break;
/*TODO*///    case 0x18:  /* mov ds,ew */
/*TODO*///		i286_data_descriptor(DS,src);
/*TODO*///		break;
/*TODO*///    case 0x10:  /* mov ss,ew */
/*TODO*///		i286_data_descriptor(SS,src);
/*TODO*///		PREFIX(_instruction)[FETCHOP]();
/*TODO*///		break;
/*TODO*///    case 0x08:  /* mov cs,ew */
/*TODO*///		break;  /* doesn't do a jump far */
/*TODO*///    }
/*TODO*///#else
            switch (ModRM & 0x38) {
                case 0x00:
                    /* mov es,ew */
                    I.sregs[ES] = src;
                    I.base[ES] = SegBase(ES);
                    break;
                case 0x18:
                    /* mov ds,ew */
                    I.sregs[DS] = src;
                    I.base[DS] = SegBase(DS);
                    break;
                case 0x10:
                    /* mov ss,ew */
                    I.sregs[SS] = src;
                    I.base[SS] = SegBase(SS);/* no interrupt allowed before next instr */
                    i186_instruction[FETCHOP()].handler();
                    break;
                case 0x08:
                    /* mov cs,ew */
                    break;
                /* doesn't do a jump far */
            }
            /*TODO*///#endif
        }
    };
    static InstructionPtr i186_sti = new InstructionPtr() /* Opcode 0xfb */ {
        public void handler() {
            i86_ICount[0] -= cycles.flag_ops;
            SetIF(1);
            i186_instruction[FETCHOP()].handler();/* no interrupt before next instruction */

 /* if an interrupt is pending, signal an interrupt */
            if (I.irq_state != 0) {
                i86_interrupt(-1);
            }
        }
    };
    static InstructionPtr i186_es = new InstructionPtr() {
        public void handler() {
            seg_prefix = 1;
            prefix_base = I.base[ES];
            i86_ICount[0] -= cycles.override;
            i186_instruction[FETCHOP()].handler();
        }
    };
    static InstructionPtr i186_lock = new InstructionPtr() {
        public void handler() {
            i86_ICount[0] -= cycles.nop;
            i186_instruction[FETCHOP()].handler();/* un-interruptible */
        }
    };
}
